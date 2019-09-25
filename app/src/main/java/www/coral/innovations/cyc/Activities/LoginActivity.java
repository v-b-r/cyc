package www.coral.innovations.cyc.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import www.coral.innovations.cyc.Fragment.UserReadingDetails;
import www.coral.innovations.cyc.R;
import www.coral.innovations.cyc.Storage.Constants;
import www.coral.innovations.cyc.Storage.Storage;
import www.coral.innovations.cyc.Storage.Utility;
import www.coral.innovations.cyc.Utils.GPSTracker;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    TextInputLayout input_layout_username, input_layout_password;
    EditText input_username, input_password;
    TextView new_regst;
    Button btn_submit, btn_reset, btn_guest;
    Storage storage ;
    TextView toolbar_title ;
    ProgressBar progressSpinner ;

    double latitude = 0.0, longitude = 0.0;
    GPSTracker gps;
    LocationRequest mLocationRequest;
    GoogleApiClient location_mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        storage = new Storage(LoginActivity.this);

        if (storage.getValue(Constants.USC_NO).length()==0) {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        111);
            }
        }

        gps = new GPSTracker(LoginActivity.this);

        progressSpinner = (ProgressBar) findViewById(R.id.progressSpinner);

        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.cps);

        input_layout_username = (TextInputLayout) findViewById(R.id.input_layout_username);
        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);

        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);
        new_regst = (TextView) findViewById(R.id.new_regst);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_guest = (Button) findViewById(R.id.btn_guest);

        input_username.addTextChangedListener(new MyTextWatcher(input_username));
        input_password.addTextChangedListener(new MyTextWatcher(input_password));

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_username.setText("");
                input_password.setText("");

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitform();
            }
        });

        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showToast(getApplicationContext(), getString(R.string.under_development));

            }
        });
        new_regst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Registration.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (storage.getValue(Constants.USC_NO).length()==0) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    Log.i("LatLong", "==>" + latitude + "\nLong : " + longitude);
                } else {
                    // Utility.showToast(getApplicationContext(), getString(R.string.GPS_TOAST));
                }
            else {
                gpsCheck();
            }
        }
    }

    private void gpsCheck() {
        location_mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(LoginActivity.this).addOnConnectionFailedListener(LoginActivity.this).build();
        if (location_mGoogleApiClient.isConnected()) {
            location_mGoogleApiClient.stopAutoManage(LoginActivity.this);
            location_mGoogleApiClient.disconnect();
        }
        location_mGoogleApiClient.connect();
    }

    private void submitform() {
        if (!validateUsername()) {
            return;
        } else if (!validatePassword()) {
            return;
        } else {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                CheckUserLogin(input_username.getText().toString().trim(), input_password.getText().toString().trim());
            }else {
                Utility.showToast(getApplicationContext(),getString(R.string.Internet_Error));
            }
        }
    }

    private void CheckUserLogin(final String username, String password) {
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait");
        dialog.show();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (user != null) {
                    Log.i("LatLong","==>"+latitude+"\nLong : "+user.getObjectId());
                    storage.saveSecure(Constants.LATITUDE,String.valueOf(latitude));
                    storage.saveSecure(Constants.LONGITUDE,String.valueOf(longitude));
                    storage.saveSecure(Constants.IS_LOGIN, "LoggedInn");
                    storage.saveSecure(Constants.USER_OBJECT_ID, user.getObjectId());
                    storage.saveSecure(Constants.USER_MOBILE_NO, user.getString("MobileNo"));
                    storage.saveSecure(Constants.USER_FIRSTNAME, user.getString("FirstName"));
                    storage.saveSecure(Constants.USER_LASTNAME, user.getString("LastName"));
                    storage.saveSecure(Constants.USER_EMAIL, user.getString("email"));
                    storage.saveSecure(Constants.USER_PASSWORD, user.getString("UserPassword"));
                    storage.saveSecure(Constants.USER_IMAGE, user.getString("Image"));
                    Utility.showToast(LoginActivity.this, "Login Success");
                    startActivity(new Intent(LoginActivity.this, HomeScreen.class));
                } else {
                    Log.i("ErrorMessage","==>"+e.getMessage());
                    Utility.showToast(LoginActivity.this, e.getMessage());
                }
            }
        });

    }

    private boolean validateUsername() {
        if (input_username.getText().toString().trim().isEmpty()) {
            input_layout_username.setError(getString(R.string.user_id_error));
            return false;
        } else {
            input_layout_username.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (input_password.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getString(R.string.password_error));
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(location_mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                    } else {
                        //GPS Disabled
                    }
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    //GPS Disabled
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_username:
                    validateUsername();
                    break;

                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (progressSpinner.getVisibility()==View.VISIBLE) {
            progressSpinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

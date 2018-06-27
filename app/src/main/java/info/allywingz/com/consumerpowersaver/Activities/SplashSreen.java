package info.allywingz.com.consumerpowersaver.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

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

import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;
import info.allywingz.com.consumerpowersaver.Utils.GPSTracker;

public class SplashSreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Storage storage ;
    GPSTracker gps ;
    String mAddress ;
    double latitude=0.0, longitude =0.0;
    LocationRequest mLocationRequest;
    GoogleApiClient location_mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sreen);
        storage = new Storage(SplashSreen.this);
        loadLogoAnimation();
        isValidating();
    }

    private void gpsCheck() {
        location_mGoogleApiClient = new GoogleApiClient.Builder(SplashSreen.this).
                addApi(LocationServices.API).
                addConnectionCallbacks(SplashSreen.this).
                addOnConnectionFailedListener(SplashSreen.this).build();
        if (location_mGoogleApiClient.isConnected()) {
            location_mGoogleApiClient.stopAutoManage(SplashSreen.this);
            location_mGoogleApiClient.disconnect();
        }
        location_mGoogleApiClient.connect();
    }

    private void loadLogoAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_animation_in);
        ((LinearLayout) findViewById(R.id.splash)).startAnimation(a);
    }

    void isValidating() {

        if (storage.getValue(Constants.IS_LOGIN).equals("LoggedInn")) {
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    startActivity(new Intent(SplashSreen.this, HomeScreen.class));
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    startActivity(new Intent(SplashSreen.this, LoginActivity.class));
                }
            }, 2000);

        }
    }


    @Override
    public void onConnected(Bundle bundle) {
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
                            status.startResolutionForResult(SplashSreen.this, REQUEST_LOCATION);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        mAddress = Utility.getLocationAddress(getApplicationContext(), latitude, longitude) ;
                        storage.saveSecure(Constants.ADDRESS, mAddress);
                        loadLogoAnimation();
                        isValidating();

                    } else {
                        Utility.showToast(SplashSreen.this, "Check GPS Connection");
                    }
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Utility.showToast(SplashSreen.this, "Check GPS Connection");
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

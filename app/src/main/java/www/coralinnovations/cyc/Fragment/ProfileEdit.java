package www.coralinnovations.cyc.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import www.coralinnovations.cyc.Activities.HomeScreen;
import www.coralinnovations.cyc.Activities.LoginActivity;
import www.coralinnovations.cyc.Activities.Registration;
import www.coralinnovations.cyc.Adapter.NewAppliancesAdapter;
import www.coralinnovations.cyc.Model.DataModel;
import www.coralinnovations.cyc.R;
import www.coralinnovations.cyc.Storage.Constants;
import www.coralinnovations.cyc.Storage.Storage;
import www.coralinnovations.cyc.Storage.Utility;

public class ProfileEdit extends Fragment {
    private EditText inputName, input_last_name ,inputmobile, inputEmail, inputPassword, inputconfimed_password;
    private TextInputLayout inputLayoutName, inputLayoutLastName, inputLayoutmobile, inputLayoutEmail,
            inputLayoutPassword, inputLayoutconfim_password;
    private Button btnRest, btnSubmit;
    Storage storage;

    TextView toolbar_title;
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    String userChoosenTask, imageString = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile_edit, container, false);

        storage = new Storage(getActivity());

        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.cps);

        profile_image = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.profile_image);
        inputLayoutName = (TextInputLayout) v.findViewById(R.id.input_layout_name);
        inputLayoutLastName = (TextInputLayout) v.findViewById(R.id.input_layout_last_name);
        inputLayoutmobile = (TextInputLayout) v.findViewById(R.id.input_layout_mobile);
        inputLayoutEmail = (TextInputLayout) v.findViewById(R.id.input_layout_mail);
        inputLayoutPassword = (TextInputLayout) v.findViewById(R.id.input_layout_password);
        inputLayoutconfim_password = (TextInputLayout) v.findViewById(R.id.input_layout_conpassword);

        inputName = (EditText) v.findViewById(R.id.input_name);
        input_last_name = (EditText) v.findViewById(R.id.input_last_name);
        inputmobile = (EditText) v.findViewById(R.id.input_mobile);
        inputEmail = (EditText) v.findViewById(R.id.input_mail);
        inputPassword = (EditText) v.findViewById(R.id.input_password);
        inputconfimed_password = (EditText) v.findViewById(R.id.input_conpassword);

        btnRest = (Button) v.findViewById(R.id.btn_reset);
        btnSubmit = (Button) v.findViewById(R.id.btn_submit);


        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputmobile.addTextChangedListener(new MyTextWatcher(inputmobile));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputconfimed_password.addTextChangedListener(new MyTextWatcher(inputconfimed_password));

        inputName.setText(storage.getValue(Constants.USER_FIRSTNAME));
        input_last_name.setText(storage.getValue(Constants.USER_LASTNAME));
        inputmobile.setText(storage.getValue(Constants.USER_MOBILE_NO));
        inputEmail.setText(storage.getValue(Constants.USER_EMAIL));
        inputPassword.setText(storage.getValue(Constants.USER_PASSWORD));
        inputconfimed_password.setText(storage.getValue(Constants.USER_PASSWORD));

        Log.i("USER_IMAGE","-->"+storage.getValue(Constants.USER_IMAGE));
        if (storage.getValue(Constants.USER_IMAGE).trim().length() > 4){
            byte[] decodedString = Base64.decode(storage.getValue(Constants.USER_IMAGE), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profile_image.setImageBitmap(decodedByte);
        }else{
            profile_image.setImageDrawable(getResources().getDrawable(R.drawable.logo_orange));
        }


        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Class fragmentClass = null;
                FragmentManager fragmentManager ;
                fragmentClass = CurrentReadingNew.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getActivity(), Manifest.permission.CAMERA)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("CAMERA is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) getActivity(),
                                        new String[]{Manifest.permission.CAMERA}, 999);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.CAMERA}, 999);
                    }
                } else {
                    selectImage();
                }
            }
        });
        return v;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "View Image",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";

                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";

                    galleryIntent();
                } else if (items[item].equals("View Image")) {
                    userChoosenTask = "View Image";
                    if (storage.getValue(Constants.USER_IMAGE).length()>0) {
                        Utility.ZoomImage(getActivity(), profile_image);
                    }else{
                        Utility.showToast(getActivity(), getString(R.string.NO_IMAGE));
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 111);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 222);
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        } else if (!validateMobile()) {
            return;
        } else if (!validateEmail()) {
            return;
        } else if (!validatePassword()) {
            return;
        } else if (!validateConfimed_password()) {
            return;
        } else if (!inputPassword.getText().toString().trim().equals(inputconfimed_password.getText().toString().trim())) {
            inputLayoutPassword.setError(getString(R.string.password_match_erro));
            requestFocus(inputPassword);
        } else {
            if (Utility.isNetworkAvailable(getActivity())){
                UpdateUserData();
            }else{
                Utility.showToast(getActivity(),getString(R.string.Internet_Error));
            }
        }
    }

    private void UpdateUserData() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Updating Profile, Please wait...!");
        dialog.show();

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    parseUser.put("FirstName", inputName.getText().toString().trim());
                    parseUser.put("LastName", input_last_name.getText().toString().trim());
                    parseUser.put("password", inputPassword.getText().toString().trim());
                    parseUser.put("UserPassword", inputPassword.getText().toString().trim());
                    parseUser.put("email", inputmobile.getText().toString().trim());
                    parseUser.put("Image", storage.getValue(Constants.USER_IMAGE));
                   /* parseUser.put("MobileNo", inputmobile.getText().toString().trim());
                    parseUser.put("email", inputmobile.getText().toString().trim());
                    parseUser.put("Image", storage.getValue(Constants.USER_IMAGE));
                    */parseUser.saveInBackground();

                    Utility.showToast(getActivity(), getString(R.string.update_success));
                    storage.saveSecure(Constants.USER_FIRSTNAME, inputName.getText().toString().trim());
                    storage.saveSecure(Constants.USER_LASTNAME, input_last_name.getText().toString().trim());
                    storage.saveSecure(Constants.USER_EMAIL, inputEmail.getText().toString().trim());
                    storage.saveSecure(Constants.USER_PASSWORD, inputPassword.getText().toString().trim());
                    storage.saveSecure(Constants.USER_IMAGE, storage.getValue(Constants.USER_IMAGE) );

                    if (storage.getValue(Constants.IS_LOGIN).equals("LoggedInn")){
                        Fragment fragment = null;
                        Class fragmentClass = null;
                        FragmentManager fragmentManager ;
                        fragmentClass = CurrentReadingNew.class;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception ew) {
                            ew.printStackTrace();
                        }
                        // Insert the fragment by replacing any existing fragment
                        fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }else {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                }else{
                    Log.i("mException","==>"+e);
                    Utility.showToast(getActivity(), "Updation Failed...!");
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222)
                onSelectFromGalleryResult(data);
            else if (requestCode == 111)
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    profile_image.setImageBitmap(photo);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    storage.saveSecure(Constants.USER_IMAGE,imageString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                storage.saveSecure(Constants.USER_IMAGE,imageString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profile_image.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        profile_image.setImageBitmap(thumbnail);
    }


    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobile() {
        if (inputmobile.getText().toString().trim().isEmpty()) {
            inputLayoutmobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(inputmobile);
            return false;
        } else {
            inputLayoutmobile.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {

            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfimed_password() {
        if (inputconfimed_password.getText().toString().trim().isEmpty()) {
            inputLayoutconfim_password.setError(getString(R.string.err_msg_con_password));
            requestFocus(inputconfimed_password);
            return false;
        } else {
            inputLayoutconfim_password.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_mobile:
                    validateMobile();
                    break;
                case R.id.input_mail:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_conpassword:
                    validatePassword();
                    break;

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }
        ((HomeScreen) getActivity()).setActionBarTitle(getString(R.string.profile_details));

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    FragmentManager fragmentManager ;
                    fragmentClass = CurrentReadingNew.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Insert the fragment by replacing any existing fragment
                    fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    return true;
                }
                return false;
            }
        });
    }
}

package info.allywingz.com.consumerpowersaver.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class Registration extends AppCompatActivity {
    private EditText inputName,inputlastName,inputmobile, inputEmail, inputPassword ,inputconfimed_password;
    private TextInputLayout inputLayoutName,inputLayoutmobile , inputLayoutEmail,
            inputLayoutPassword , inputLayoutconfim_password;
    private Button btnRest , btnSubmit;
    Storage storage ;

    TextView toolbar_title ;
    String userChoosenTask, imageString=null ;
    de.hdodenhof.circleimageview.CircleImageView profile_image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        storage = new Storage(Registration.this);
        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.cps);

        profile_image = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutmobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_mail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutconfim_password = (TextInputLayout) findViewById(R.id.input_layout_conpassword);

        inputName = (EditText) findViewById(R.id.input_name);
        inputlastName = (EditText) findViewById(R.id.input_last_name);
        inputmobile = (EditText) findViewById(R.id.input_mobile);
        inputEmail = (EditText) findViewById(R.id.input_mail);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputconfimed_password = (EditText) findViewById(R.id.input_conpassword);

        btnRest = (Button) findViewById(R.id.btn_reset);
        btnSubmit = (Button) findViewById(R.id.btn_submit);


        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputmobile.addTextChangedListener(new MyTextWatcher(inputmobile));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputconfimed_password.addTextChangedListener(new MyTextWatcher(inputconfimed_password));


        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,LoginActivity.class));
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              submitForm();

                ParseObject gameScore = new ParseObject("Registration");
                gameScore.put("FirstName", "Kiran");
                gameScore.put("LastName", "Kumar");
                gameScore.put("EmailID", "kirankumar946@gmail.com");
                gameScore.put("Password", "123456");
                gameScore.put("ImageData", "NA");

                gameScore.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i("ParseException","==>"+e.toString());
                    }
                });
                Log.i("FirstName","==>"+gameScore.getString("FirstName"));
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Registration.this, Manifest.permission.CAMERA)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Registration.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("CAMERA is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Registration.this,
                                        new String[]{Manifest.permission.CAMERA}, 999);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(Registration.this, new String[]{Manifest.permission.CAMERA}, 999);
                    }
                } else {
                    selectImage();
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "View Image",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
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
                        Utility.ZoomImage(Registration.this, profile_image);
                    }else{
                        Utility.showToast(Registration.this, getString(R.string.NO_IMAGE));
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
        }else if(!validateMobile()){
            return;
        }else if (!validateEmail()) {
            return;
        }else if (!validatePassword()) {
            return;
        }else if(!validateConfimed_password()) {
            return;
        }else if(!inputPassword.getText().toString().trim().equals(inputconfimed_password.getText().toString().trim())) {
            inputLayoutPassword.setError(getString(R.string.password_match_erro));
            requestFocus(inputPassword);
        }else{

            Utility.showToast(Registration.this, getString(R.string.success_register));
            storage.saveSecure(Constants.USER_ID, inputName.getText().toString().trim());
            storage.saveSecure(Constants.USER_EMAIL, inputEmail.getText().toString().trim());
            storage.saveSecure(Constants.USER_PASSWORD, inputPassword.getText().toString().trim());
            storage.saveSecure(Constants.USER_MOBILE_NO, inputmobile.getText().toString().trim());
            storage.saveSecure(Constants.IS_LOGIN, "Y");
            storage.saveSecure(Constants.USER_IMAGE, imageString );
            startActivity(new Intent(Registration.this, LoginActivity.class));
        }
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
    private boolean validateMobile(){
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

    private boolean validateConfimed_password(){
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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(Registration.this.getContentResolver(), data.getData());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profile_image.setImageBitmap(bm);
    }
}

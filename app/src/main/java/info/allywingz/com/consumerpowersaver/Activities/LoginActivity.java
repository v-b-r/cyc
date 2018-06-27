package info.allywingz.com.consumerpowersaver.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import info.allywingz.com.consumerpowersaver.Fragment.UserReadingDetails;
import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout input_layout_username, input_layout_password;
    EditText input_username, input_password;
    TextView new_regst;
    Button btn_submit, btn_reset, btn_guest;
    Storage storage ;
    TextView toolbar_title ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        storage = new Storage(LoginActivity.this);

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

    private void submitform() {
        if (!validateUsername()) {
            return;
        } else if (!validatePassword()) {
            return;
        } else {
            String id = input_username.getText().toString().toLowerCase().trim() ;
            Log.i("USER_MOBILE_NO","--->"+id+"--"+storage.getValue(Constants.USER_MOBILE_NO)+"--"+storage.getValue(Constants.USER_EMAIL));
            if ((id.equals(storage.getValue(Constants.USER_MOBILE_NO))) && input_password.getText().toString().trim().equals(storage.getValue(Constants.USER_PASSWORD))){
                storage.saveSecure(Constants.IS_LOGIN, "LoggedInn");
                startActivity(new Intent(LoginActivity.this, HomeScreen.class));
            }else if((id.equals(storage.getValue(Constants.USER_EMAIL))) && input_password.getText().toString().trim().equals(storage.getValue(Constants.USER_PASSWORD))){
                storage.saveSecure(Constants.IS_LOGIN, "LoggedInn");
                startActivity(new Intent(LoginActivity.this, HomeScreen.class));
            }else {
                if (!id.equals(storage.getValue(Constants.USER_MOBILE_NO))){
                    input_layout_username.setError("Invalid User ID");
                    input_username.requestFocus();
                }else if(!id.equals(storage.getValue(Constants.USER_PASSWORD))){
                    input_layout_password.setError("Invalid Password");
                    input_password.requestFocus();
                }
            }
        }
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

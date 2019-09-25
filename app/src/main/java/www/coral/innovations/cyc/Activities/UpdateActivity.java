package www.coral.innovations.cyc.Activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import www.coral.innovations.cyc.Database.DBHelper;
import www.coral.innovations.cyc.Fragment.Appliances;
import www.coral.innovations.cyc.R;
import www.coral.innovations.cyc.Storage.Storage;

public class UpdateActivity extends AppCompatActivity {
    Storage storage ;
    int from_hour = 0;
    int from_min = 0;

    int to_hour = 0;
    int to_min = 0;
    DBHelper db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.appliances_items);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button pick_item = (Button)findViewById(R.id.pick_item);
        pick_item.setVisibility(View.GONE);

        final TextInputLayout input_layout_item_name = (TextInputLayout)findViewById(R.id.input_layout_item_name);
        final TextInputLayout input_layout_item_watts = (TextInputLayout)findViewById(R.id.input_layout_item_watts);
        final TextInputLayout input_layout_item_quantity = (TextInputLayout)findViewById(R.id.input_layout_item_quantity);
        final TextInputLayout input_layout_item_usage = (TextInputLayout)findViewById(R.id.input_layout_item_usage);
        final TextInputLayout input_layout_item_usage_days = (TextInputLayout)findViewById(R.id.input_layout_item_usage_days);
        final TextInputLayout input_layout_item_from_time = (TextInputLayout)findViewById(R.id.input_layout_item_from_time);
        final TextInputLayout input_layout_item_to_time = (TextInputLayout)findViewById(R.id.input_layout_item_to_time);

        final EditText input_item_name = (EditText)findViewById(R.id.input_item_name);
        final EditText input_item_watts = (EditText)findViewById(R.id.input_item_watts);
        final EditText input_item_quantity = (EditText)findViewById(R.id.input_item_quantity);
        final EditText input_item_usage = (EditText)findViewById(R.id.input_item_usage);
        final EditText input_item_usage_days = (EditText)findViewById(R.id.input_item_usage_days);
        final EditText input_item_from_time = (EditText)findViewById(R.id.input_item_from_time);
        final EditText input_item_to_time = (EditText)findViewById(R.id.input_item_to_time);

        Intent i = getIntent();
        input_item_name.setText(i.getStringExtra("ItemName"));
        input_item_watts.setText(i.getStringExtra("ItemPower"));
        input_item_quantity.setText(i.getStringExtra("ItemQty"));
        input_item_usage.setText(i.getStringExtra("ItemHrs"));

        Button ok = (Button)findViewById(R.id.ok);
        ok.setText("Update");

        input_item_from_time.setFocusable(false);
        input_item_to_time.setFocusable(false);

        input_item_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(UpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        input_item_from_time.setText( String.format("%02d:%02d",selectedHour , selectedMinute));
                        from_hour = selectedHour ;
                        from_min = selectedMinute ;
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        input_item_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(UpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        input_item_to_time.setText(String.format("%02d:%02d",selectedHour , selectedMinute));
                        to_hour = selectedHour ;
                        to_min = selectedMinute ;

                        if (from_hour>to_hour){
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateActivity.this);
                            alertDialogBuilder.setTitle("Alert");

                            alertDialogBuilder
                                    .setMessage("Please Set Timings Correctly i.e., Usage from date should be less then Usage Till date...!")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            input_item_to_time.getText().clear();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        input_item_to_time.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable paramAnonymousEditable) {}

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
            {
                if ((input_item_from_time.getText().toString().trim().length() != 0) && (input_item_to_time.getText().toString().trim().length() != 0))
                {
                    to_hour = Integer.parseInt(input_item_to_time.getText().toString().split(":")[0]);
                    to_min =  Integer.parseInt(input_item_to_time.getText().toString().split(":")[1]);
                    int hour = 0;
                    int minute = 0;
                    if (to_hour>from_hour){

                        hour = to_hour-from_hour ;
                        Log.i("hour1","-->"+hour+"-from-"+from_hour+"-to-"+to_hour);
                    }else if (to_hour<from_hour){
                        hour = from_hour-to_hour ;
                        Log.i("hour2","-->"+hour+"-from-"+from_hour+"-to-"+to_hour);
                    }
                    if (to_min>from_min){
                        minute = to_min- from_min;
                        Log.i("minute1","-->"+minute+"-from-"+from_min+"-to-"+to_min);
                    }else if (to_min<from_min){
                        minute = from_min- to_min;
                        Log.i("minute2","-->"+minute+"-from-"+from_min+"-to-"+to_min);
                    }
                    input_item_usage.setText(String.format("%02d:%02d",hour , minute));

                }else {
                    input_item_usage.getText().clear();
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_item_name.getText().toString().length()==0){
                    input_layout_item_name.setError("Please Enter Item Name");
                    input_item_name.requestFocus();
                }else if (input_item_watts.getText().toString().length()==0){
                    input_layout_item_watts.setError("Please Enter Item Watts");
                    input_item_watts.requestFocus();
                }else if (input_item_quantity.getText().toString().length()==0){
                    input_layout_item_quantity.setError("Please Enter Item Quantity");
                    input_item_quantity.requestFocus();
                }else if (input_item_from_time.getText().toString().length()==0){
                    input_layout_item_from_time.setError("Please Enter From Time");
                    input_item_from_time.requestFocus();
                }else if (input_item_to_time.getText().toString().length()==0){
                    input_layout_item_to_time.setError("Please Enter Item To Time");
                    input_item_to_time.requestFocus();
                }else if (input_item_usage.getText().toString().length()==0){
                    input_layout_item_usage.setError("Please Enter Item Usage Time");
                    input_item_usage.requestFocus();
                }else if (input_item_usage_days.getText().toString().length()==0){
                    input_layout_item_usage_days.setError("Please Enter Item Usage Days");
                    input_item_usage_days.requestFocus();
                }else{
                    showToastMsg("Details Submitted");
                    String watts = input_item_watts.getText().toString().trim();
                    String hours = input_item_usage.getText().toString().trim();
                    String days = input_item_usage_days.getText().toString().trim();
                    String quantity = input_item_quantity.getText().toString().trim();

                    double temp_units  = Double.parseDouble(watts.split(" ")[0])*Double.parseDouble(hours.split(":")[0])/1000;
                    //Units = Power in Watts * time in hours / 1000 * days
                    Log.i("temp_units 1","-->"+temp_units);
                    Log.i("days 1","-->"+days);
                    temp_units  = temp_units*Double.parseDouble(days);
                    Log.i("temp_units 2","-->"+temp_units);
                    if (Integer.parseInt(quantity)>1){
                        temp_units = temp_units * Integer.parseInt(quantity) ;
                    }
                    db = new DBHelper(UpdateActivity.this);
                    db.addItems(input_item_name.getText().toString().trim(), watts, quantity, hours, days, String.valueOf(temp_units));
                    showToastMsg("Details Updated..!");

                    startActivity(new Intent(UpdateActivity.this, HomeScreen.class));
                }
            }
        });

    }
    public void showToastMsg(String Msg) {
        Toast.makeText(UpdateActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }

}

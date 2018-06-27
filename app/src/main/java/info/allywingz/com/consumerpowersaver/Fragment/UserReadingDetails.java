package info.allywingz.com.consumerpowersaver.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import info.allywingz.com.consumerpowersaver.Activities.HomeScreen;
import info.allywingz.com.consumerpowersaver.Activities.LoginActivity;
import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class UserReadingDetails extends Fragment implements View.OnClickListener {
    EditText present_date, present_reading, previous_date, previous_reading, total_units, total_days, total_amount;
    Button reset, submit;

    public static boolean previousFLG = false, presentFLG = false;
    Calendar mcurrentDate;
    DatePickerDialog dpDialog;
    int year;
    int month;
    int day;
    SimpleDateFormat sdf ;
    Calendar calendar;
    Storage storage ;

    TextView toolbar_title ;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_user_reading_details, container, false);
        storage = new Storage(getActivity());
        toolbar_title = (TextView)v.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.cps);

        present_date = (EditText) v.findViewById(R.id.present_date);
        present_reading = (EditText)v. findViewById(R.id.present_reading);
        previous_date = (EditText) v.findViewById(R.id.previous_date);
        previous_reading = (EditText)v. findViewById(R.id.previous_reding);
        total_units = (EditText) v.findViewById(R.id.total_units);
        total_days = (EditText) v.findViewById(R.id.total_days);
        total_amount = (EditText) v.findViewById(R.id.total_amount);

        previous_reading.setText(storage.getValue(Constants.PREVIOUS_READING));
        present_reading.setText(storage.getValue(Constants.PRESENT_READING));
        present_date.setText(storage.getValue(Constants.PRESENT_DATE));
        previous_date.setText(storage.getValue(Constants.PREVIOUS_DATE));
        total_units.setText(storage.getValue(Constants.TOTAL_UNITS));
        total_amount.setText(storage.getValue(Constants.TOTAL_AMOUNT));

        reset = (Button) v.findViewById(R.id.btn_reset);
        submit = (Button) v.findViewById(R.id.btn_submit);

        present_date.setOnClickListener(this);
        previous_date.setOnClickListener(this);

        previous_date.setFocusable(false);
        previous_date.setFocusable(false);

        total_units.setFocusable(false);
        total_days.setFocusable(false);
        total_amount.setFocusable(false);

        sdf = new SimpleDateFormat("dd-MMM-yyyy");
        sdf.format(new Date());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

        String old_date = format.format(date);

        if (storage.getValue(Constants.PRESENT_DATE).length()==0) {
            present_date.setText(String.format("%s", sdf.format(new Date())));
            previous_date.setText(old_date);
        }
        Log.i("Dates","--->"+sdf.format(new Date()));

        present_reading.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (present_reading.getRight() - present_reading.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Utility.showToast(getActivity(), "Work in Progress");
                        return true;
                    }
                }
                return false;
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present_date.setText("");
                present_reading.setText("");
                previous_date.setText("");
                previous_reading.setText("");
                total_units.setText("");
                total_days.setText("");
                total_amount.setText("");
                //startActivity(new Intent(getActivity(),LoginActivity.class));

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (present_reading.getText().toString().trim().length()==0){
                    Utility.showToast(getActivity(),getString(R.string.present_reading_error));
                }else if (previous_reading.getText().toString().trim().length()==0){
                    Utility.showToast(getActivity(),getString(R.string.previous_reading_error));
                }else {
                    storage.saveSecure(Constants.PRESENT_DATE, present_date.getText().toString().trim());
                    storage.saveSecure(Constants.PREVIOUS_DATE, previous_date.getText().toString().trim());
                    storage.saveSecure(Constants.PRESENT_READING, present_reading.getText().toString().trim());
                    storage.saveSecure(Constants.PREVIOUS_READING, previous_reading.getText().toString().trim());
                    storage.saveSecure(Constants.TOTAL_UNITS, total_units.getText().toString().trim());
                    storage.saveSecure(Constants.TOTAL_DAYS, total_days.getText().toString().trim());
                    storage.saveSecure(Constants.TOTAL_AMOUNT, total_amount.getText().toString().trim());

                    storage.saveSecure(Constants.SAVE_NEW_DATA, "");

                    if(Integer.parseInt(total_units.getText().toString().trim())>90 && Integer.parseInt(total_units.getText().toString().trim())<110) {
                        storage.saveSecure(Constants.CATEGORY, "A");
                    }else if(Integer.parseInt(total_units.getText().toString().trim())>180 && Integer.parseInt(total_units.getText().toString().trim())<220) {
                        storage.saveSecure(Constants.CATEGORY, "B");
                    }else{
                        storage.saveSecure(Constants.CATEGORY, "C");
                    }
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

            }
        });

        setDays();

        present_reading.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (present_reading.getText().toString().trim().length()!=0 && previous_reading.getText().toString().trim().length()!=0){
                    if(Integer.parseInt(present_reading.getText().toString().trim())>Integer.parseInt(previous_reading.getText().toString().trim())) {
                        total_units.setText(calculateReading(present_reading.getText().toString().trim(), previous_reading.getText().toString().trim()));
                    }
                    setDays();
                }else {
                    total_units.getText().clear();
                }
            }
        });

        previous_reading.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (present_reading.getText().toString().trim().length()!=0 && previous_reading.getText().toString().trim().length()!=0){
                    total_units.setText(calculateReading(present_reading.getText().toString().trim(), previous_reading.getText().toString().trim()));
                }else{
                    total_units.getText().clear();
                }
            }
        });

        total_units.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (total_units.getText().toString().trim().length()!=0){
                    int value = Integer.parseInt(total_units.getText().toString().trim()) ;
                    if (value<=50) {
                        double rate = 1.45 ;
                        double sum = value * rate ;
                        double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                        total_amount.setText(String.valueOf(roundedNumber));
                    }else if (value<=100) {
                        double rate = 1.45 ;
                        double rate2 = 2.60 ;

                        double sum = (double) 50 * rate;
                        value = value-50 ;

                        double sum2 = (double) value * rate2;
                        double total = sum+ sum2 ;
                        total_amount.setText(String.valueOf(total));
                    }else if (value<=200) {
                        double rate = 3.30 ;
                        double rate2 = 4.30 ;

                        double sum = (double) 100 * rate;
                        value = value-100 ;

                        double sum2 = (double) value * rate2;
                        double total = sum+ sum2 ;
                        total_amount.setText(String.valueOf(total));

                    }else if (value<=300) {
                        double rate = 5.0 ;
                        double rate2 = 7.20 ;

                        double sum = (double) 200 * rate;
                        value = value-200 ;

                        double sum2 = (double) value * rate2;
                        double total = sum+ sum2 ;
                        total_amount.setText(String.valueOf(total));

                    }else if (value<=800) {
                        double rate = 5.0 ;
                        double rate2 = 7.20 ;
                        double rate3 = 8.50 ;
                        double rate4 = 9.0 ;

                        double sum = (double) 200 * rate;
                        value = value-200 ;

                        double sum2 = (double) 300 * rate2;
                        value = value-300 ;

                        double sum3 = (double) 400 * rate3;
                        value = value-400 ;

                        double sum4 = (double) value * rate4;

                        double total = sum+ sum2+ sum3+ sum4 ;
                        total_amount.setText(String.valueOf(total));

                    }else{
                        double rate = 9.50 ;
                        double sum = (double) value *  rate+60;
                        double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                        total_amount.setText(String.valueOf(roundedNumber));
                    }

                    /*if (value<=50) {
                        double rate = 1.45 ;
                        double sum = (double) value * rate;
                        current_bill_amount.setText(String.valueOf(sum));

                    }else if (value<=100) {
                        double rate = 1.45 ;
                        double rate2 = 2.60 ;

                        double sum = (double) 50 * rate;
                        value = value-50 ;

                        double sum2 = (double) value * rate2;
                        double total = sum+ sum2 ;
                        current_bill_amount.setText(String.valueOf(total));
                    }else if (value<=200) {
                        double rate = 3.30 ;
                        double rate2 = 4.30 ;

                        double sum = (double) 100 * rate;
                        value = value-100 ;

                        double sum2 = (double) value * rate2;
                        double total = sum+ sum2 ;
                        current_bill_amount.setText(String.valueOf(total));

                    }else if (value<=300) {
                        double rate = 5.0 ;
                        double rate2 = 7.20 ;

                        double sum = (double) 200 * rate;
                        value = value-200 ;

                        double sum2 = (double) value * rate2;
                        double total = sum+ sum2 ;
                        current_bill_amount.setText(String.valueOf(total));

                    }else if (value<=800) {
                        double rate = 5.0 ;
                        double rate2 = 7.20 ;
                        double rate3 = 8.50 ;
                        double rate4 = 9.0 ;

                        double sum = (double) 200 * rate;
                        value = value-200 ;

                        double sum2 = (double) 300 * rate2;
                        value = value-300 ;

                        double sum3 = (double) 400 * rate3;
                        value = value-400 ;

                        double sum4 = (double) value * rate4;

                        double total = sum+ sum2+ sum3+ sum4 ;
                        current_bill_amount.setText(String.valueOf(total));

                    }else if (value>800) {
                        double rate = 9.50 ;
                        double sum = (double) value * rate;
                        current_bill_amount.setText(String.valueOf(sum));
                    }*/

                }
            }
        });
        return v;
    }

    private static String calculateReading(String presentReading, String previousReading) {
        int total = Integer.parseInt(presentReading)-Integer.parseInt(previousReading);
        return String.valueOf(total);
    }

    @Override
    public void onClick(View view) {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        switch (view.getId()){
            case R.id.present_date :
                presentFLG=true;
                presentFLG = false;
                getDatefromCalender();
                break;

            case R.id.previous_date :
                presentFLG=false;
                presentFLG = true;
                getDatefromCalender();
                break;
        }
    }

    private void getDatefromCalender() {
        mcurrentDate = Calendar.getInstance();
        year= mcurrentDate.get(Calendar.YEAR);
        month= mcurrentDate.get(Calendar.MONTH);
        day= mcurrentDate.get(Calendar.DAY_OF_MONTH);

        final SimpleDateFormat mdformatpresent = new SimpleDateFormat("dd-MMM-yyyy");
        mcurrentDate = Calendar.getInstance();

        dpDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mcurrentDate = Calendar.getInstance();
                mcurrentDate.set(year, monthOfYear, dayOfMonth);
                if (presentFLG) {
                    present_date.setText(mdformatpresent.format(mcurrentDate.getTime()));
                    setDays();
                }else if(previousFLG){
                    previous_date.setText(mdformatpresent.format(mcurrentDate.getTime()));
                    setDays();
                }
            }
        }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
        //dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        present_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFLG=true;
                previousFLG = false;
                dpDialog.show();
            }
        });


        previous_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFLG=false;
                previousFLG = true;
                dpDialog.show();
            }
        });
    }

    public void setDays(){
        try {
            Date dateBefore = sdf.parse(previous_date.getText().toString());
            Date dateAfter = sdf.parse(present_date.getText().toString());
            long difference = dateAfter.getTime() - dateBefore.getTime();
            float daysBetween = (difference / (1000*60*60*24));
            /* You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
            System.out.println(daysBetween);
            int integer=(int)daysBetween;
            total_days.setText(String.valueOf(integer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

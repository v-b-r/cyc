package www.coral.innovations.cyc.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import www.coral.innovations.cyc.Activities.HomeScreen;
import www.coral.innovations.cyc.Activities.LoginActivity;
import www.coral.innovations.cyc.Activities.Registration;
import www.coral.innovations.cyc.R;
import www.coral.innovations.cyc.Storage.Constants;
import www.coral.innovations.cyc.Storage.Storage;
import www.coral.innovations.cyc.Storage.Utility;

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
    ProgressBar progressSpinner ;

    TextView toolbar_title ;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_user_reading_details, container, false);
        storage = new Storage(getActivity());
        toolbar_title = (TextView)v.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.user_reading_details);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressSpinner = (ProgressBar)v.findViewById(R.id.progressSpinner);

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
                    if (Utility.isNetworkAvailable(getActivity())){
                        SaveMeterDetails();
                    }else{
                        Utility.showToast(getActivity(),getString(R.string.Internet_Error));
                    }
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
                    if (storage.getValue(Constants.BOARD).equals("MPPKVVJ")||
                            storage.getValue(Constants.BOARD).equals("MPPKVVJ")||
                            storage.getValue(Constants.BOARD).equals("MPPKVVJ")) {
                        if (value<=30) {
                            double rate = 3.10 ;
                            double sum = value * rate + 40;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        }else if (value<=50) {
                            double rate = 3.85 ;
                            double sum = (double) value * rate +60;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        }else if (value<=100) {
                            double rate = 4.70 ;
                            double sum = (double) value * rate +60;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        }else if (value<=300) {
                            double rate = 6.00 ;
                            double sum = (double) value * rate+60;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        }else{
                            double rate = 6.30 ;
                            double sum = (double) value *  rate+60;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        }
                    }else {
                        if (value <= 50) {
                            double rate = 1.45;
                            double sum = value * rate;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        } else if (value <= 100) {
                            double rate = 1.45;
                            double rate2 = 2.60;

                            double sum = (double) 50 * rate;
                            value = value - 50;

                            double sum2 = (double) value * rate2;
                            double total = sum + sum2;
                            total_amount.setText(String.valueOf(total));
                        } else if (value <= 200) {
                            double rate = 3.30;
                            double rate2 = 4.30;

                            double sum = (double) 100 * rate;
                            value = value - 100;

                            double sum2 = (double) value * rate2;
                            double total = sum + sum2;
                            total_amount.setText(String.valueOf(total));

                        } else if (value <= 300) {
                            double rate = 5.0;
                            double rate2 = 7.20;

                            double sum = (double) 200 * rate;
                            value = value - 200;

                            double sum2 = (double) value * rate2;
                            double total = sum + sum2;
                            total_amount.setText(String.valueOf(total));

                        } else if (value <= 800) {
                            double rate = 5.0;
                            double rate2 = 7.20;
                            double rate3 = 8.50;
                            double rate4 = 9.0;

                            double sum = (double) 200 * rate;
                            value = value - 200;

                            double sum2 = (double) 300 * rate2;
                            value = value - 300;

                            double sum3 = (double) 400 * rate3;
                            value = value - 400;

                            double sum4 = (double) value * rate4;

                            double total = sum + sum2 + sum3 + sum4;
                            total_amount.setText(String.valueOf(total));

                        } else {
                            double rate = 9.50;
                            double sum = (double) value * rate + 60;
                            double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                            total_amount.setText(String.valueOf(roundedNumber));
                        }

                    }

                }
            }
        });
        return v;
    }

    public void onResume(){
        super.onResume();

        ((HomeScreen) getActivity()).setActionBarTitle(getString(R.string.user_reading_details));

    }

    private void SaveMeterDetails() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait");
        dialog.show();

        if(Integer.parseInt(total_units.getText().toString().trim())>90 && Integer.parseInt(total_units.getText().toString().trim())<110) {
            storage.saveSecure(Constants.CATEGORY, "A");
        }else if(Integer.parseInt(total_units.getText().toString().trim())>180 && Integer.parseInt(total_units.getText().toString().trim())<220) {
            storage.saveSecure(Constants.CATEGORY, "B");
        }else{
            storage.saveSecure(Constants.CATEGORY, "C");
        }

        ParseObject board_details = new ParseObject("MeterReadingDetails");
        board_details.put("PreviousReading", previous_reading.getText().toString().trim());
        board_details.put("PresentReading", previous_reading.getText().toString().trim());
        board_details.put("FromDate", present_date.getText().toString().trim());
        board_details.put("ToDate", previous_date.getText().toString().trim());
        board_details.put("Units", total_units.getText().toString().trim());
        board_details.put("TotalDays", total_days.getText().toString().trim());
        board_details.put("Amount", total_amount.getText().toString().trim());
        board_details.put("UserCategory",storage.getValue(Constants.CATEGORY) );
        board_details.put("USC_No", storage.getValue(Constants.USC_NO));

        board_details.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i("ParseException","==>"+e);
                dialog.dismiss();
                if (e==null) {
                    storage.saveSecure(Constants.PRESENT_DATE, present_date.getText().toString().trim());
                    storage.saveSecure(Constants.PREVIOUS_DATE, previous_date.getText().toString().trim());
                    storage.saveSecure(Constants.PRESENT_READING, present_reading.getText().toString().trim());
                    storage.saveSecure(Constants.PREVIOUS_READING, previous_reading.getText().toString().trim());
                    storage.saveSecure(Constants.TOTAL_UNITS, total_units.getText().toString().trim());
                    storage.saveSecure(Constants.TOTAL_DAYS, total_days.getText().toString().trim());
                    storage.saveSecure(Constants.TOTAL_AMOUNT, total_amount.getText().toString().trim());

                    storage.saveSecure(Constants.SAVE_NEW_DATA, "");

                    Fragment fragment = null;
                    Class fragmentClass = null;
                    FragmentManager fragmentManager ;
                    fragmentClass = CurrentReadingNew.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    Utility.showToast(getActivity(), "Data Saved Successfully...!");

                    fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }else{
                    Utility.showToast(getActivity(), "Please try Again...!");
                }
            }
        });
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

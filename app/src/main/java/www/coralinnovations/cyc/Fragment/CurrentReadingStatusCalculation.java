package www.coralinnovations.cyc.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import www.coralinnovations.cyc.Activities.GenerateBill;
import www.coralinnovations.cyc.Activities.HomeScreen;
import www.coralinnovations.cyc.Activities.ShortBillGeneration;
import www.coralinnovations.cyc.R;
import www.coralinnovations.cyc.Storage.Constants;
import www.coralinnovations.cyc.Storage.Storage;
import www.coralinnovations.cyc.Storage.Utility;

public class CurrentReadingStatusCalculation extends Fragment {
    EditText from_date_et, to_date_et, present_submitted_treading, previous_submitted_treading,
            present_submitted_units, present_average_unit_per_day, average_unit_per_day, current_date_et, calculated_units,
            current_unit_count, current_bill_amount, days_left;

    Button back, btnfinal_submit;
    double roundedNumber ;

    int temp_count = 0;

    SimpleDateFormat sdf;
    Storage storage;
    CountDownTimer mTimer;
    TextView toolbar_title, scan;

    TextInputLayout input_layout_current_reading ;
    Calendar calendar;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_current_reading_status_calculation, container, false);

        storage = new Storage(getActivity());

        sdf = new SimpleDateFormat("dd-MMM-yyyy");
        sdf.format(new Date());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        final SimpleDateFormat sformat = new SimpleDateFormat("dd-MMM-yyyy");

        String old_date = sformat.format(date);

        Date d=new Date();
        final SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a");
        final String currentDateTimeString = sdf.format(d);

        toolbar_title = (TextView) v.findViewById(R.id.toolbar_title);
        scan = (TextView) v.findViewById(R.id.scan);
        toolbar_title.setText(R.string.cps);

        from_date_et = (EditText) v.findViewById(R.id.from_date_et);
        to_date_et = (EditText) v.findViewById(R.id.to_date_et);
        present_submitted_treading = (EditText) v.findViewById(R.id.present_submitted_treading);
        previous_submitted_treading = (EditText) v.findViewById(R.id.previous_submitted_treading);
        present_submitted_units = (EditText) v.findViewById(R.id.present_submitted_units);
        present_average_unit_per_day = (EditText) v.findViewById(R.id.present_average_unit_per_day);
        average_unit_per_day = (EditText) v.findViewById(R.id.average_unit_per_day);
        current_date_et = (EditText) v.findViewById(R.id.current_date_et);
        calculated_units = (EditText) v.findViewById(R.id.calculated_units);
        current_unit_count = (EditText) v.findViewById(R.id.current_unit_count);
        current_bill_amount = (EditText) v.findViewById(R.id.current_bill_amount);
        days_left = (EditText) v.findViewById(R.id.days_left);

        input_layout_current_reading = (TextInputLayout)v.findViewById(R.id.input_layout_current_reading);

        back = (Button) v.findViewById(R.id.btn_back);
        btnfinal_submit = (Button) v.findViewById(R.id.btnfinal_submit);

        resetFeilds();
        setDays();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HomeScreen.class));
            }
        });
        btnfinal_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calculated_units.getText().toString().trim().length()==0){
                    input_layout_current_reading.setError("Please Enter Present Reading");
                    calculated_units.requestFocus();
                }else{
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 7);
                    Date date = calendar.getTime();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    sdf.format(new Date());

                    storage.saveSecure(Constants.PREVIOUS_DATE, to_date_et.getText().toString().trim());
                    storage.saveSecure(Constants.PRESENT_DATE, current_date_et.getText().toString().trim());

                    storage.saveSecure(Constants.PREVIOUS_READING, previous_submitted_treading.getText().toString().trim());
                   // storage.saveSecure(Constants.PRESENT_READING, calculated_units.getText().toString().trim());

                    storage.saveSecure(Constants.DATE, "DT : "+String.format("%s", sdf.format(new Date()))) ;
                    storage.saveSecure(Constants.TIME, "TI : " +currentDateTimeString) ;
                    storage.saveSecure(Constants.BILL, "BILL NO : 0123" ) ;
                    storage.saveSecure(Constants.ERO_NO, "ERONO : 014" ) ;
                    storage.saveSecure(Constants.ERO, "ER : MP" ) ;
                    storage.saveSecure(Constants.SEC, "SEC : MP" ) ;
                    storage.saveSecure(Constants.AREA_CODE, "AREA CODE : 2132" ) ;
                    storage.saveSecure(Constants.GRP, "GRP : M" ) ;
                    storage.saveSecure(Constants.SC_NO, "SC.NO : 2132 07716" ) ;
                    storage.saveSecure(Constants.NAME, "NAME : "+storage.getValue(Constants.USER_ID) ) ;
                    storage.saveSecure(Constants.CAT, "CAT : 1B(ii) DOMESTIC" ) ;
                    storage.saveSecure(Constants.CONTRACTED_LOAD, "CONTRACTED LOAD : 1.00KW" ) ;
                    storage.saveSecure(Constants.METER_NO, "METER NO : 28025037" ) ;
                    storage.saveSecure(Constants.MF, "MF : 1.00\tPH : 1" ) ;
                    storage.saveSecure(Constants.PREVIOUS, storage.getValue(Constants.PREVIOUS_DATE) ) ;
                    storage.saveSecure(Constants.PRESENT, storage.getValue(Constants.PRESENT_DATE) ) ;
                    storage.saveSecure(Constants.PREVIOUS_UNITS, storage.getValue(Constants.PREVIOUS_READING) ) ;
                    storage.saveSecure(Constants.PRESENT_UNITS, storage.getValue(Constants.PRESENT_READING) ) ;
                    storage.saveSecure(Constants.RMD, "1.48" ) ;
                    storage.saveSecure(Constants.ENERGY_CHARGES, storage.getValue(Constants.TOTAL_AMOUNT)) ;
                    storage.saveSecure(Constants.CUST_CHARGES, "60" ) ;
                    storage.saveSecure(Constants.ELECTRICITY_DUTY, "15.06" ) ;
                    storage.saveSecure(Constants.EDINT, "0.00" ) ;
                    storage.saveSecure(Constants.ADDITIONAL_CHARGES, "0.00" ) ;
                    storage.saveSecure(Constants.INT_ON_SD, "-12.68" ) ;
                    storage.saveSecure(Constants.BILL_AMOUNT, ""+current_bill_amount.getText().toString().trim() ) ;
                    storage.saveSecure(Constants.NET_AMOUNT, ""+current_bill_amount.getText().toString().trim()  ) ;
                    storage.saveSecure(Constants.AS_ON_DATE,storage.getValue(Constants.PREVIOUS_DATE) ) ;
                    storage.saveSecure(Constants.AFTER, storage.getValue(Constants.PRESENT_DATE)) ;
                    storage.saveSecure(Constants.TOTAL_AMOUNT, ""+current_bill_amount.getText().toString().trim() ) ;
                    storage.saveSecure(Constants.TOTAL_DUE, ""+current_bill_amount.getText().toString().trim() ) ;
                    storage.saveSecure(Constants.DUE_DATE, ""+format.format(date) ) ;
                    storage.saveSecure(Constants.LAST_PAID_DATE, ""+format.format(date) ) ;
                    Log.i("current_unit_count","==>"+current_unit_count.getText().toString().trim());
                    storage.saveSecure(Constants.FINAL_TOTAL_UNITS, current_unit_count.getText().toString().trim()) ;
                    storage.saveSecure(Constants.FINAL_READINGS, calculated_units.getText().toString().trim()) ;
                    storage.saveSecure(Constants.SUBSIDY , "1.60");

                    startActivity(new Intent(getActivity(), ShortBillGeneration.class));
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showToast(getActivity(), "Work In Progress");
            }
        });
        calculated_units.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!calculated_units.getText().toString().trim().contains("-")) {
                    if (calculated_units.getText().toString().trim().length() != 0) {
                        int no_of_units = Integer.parseInt(calculated_units.getText().toString().trim()) - Integer.parseInt(storage.getValue(Constants.PRESENT_READING));
                        roundedNumber = Utility.DecimalUtils.round(no_of_units, 2);
                        current_unit_count.setText(String.valueOf(roundedNumber));

                        if (no_of_units < 190) {
                            current_unit_count.setTextColor(getResources().getColor(R.color.green));
                        } else if (no_of_units > 190 && no_of_units < 200) {
                            current_unit_count.setTextColor(getResources().getColor(R.color.orange));
                        } else if (no_of_units > 200) {
                            current_unit_count.setTextColor(getResources().getColor(R.color.red));
                        } else {

                        }

                        double unit_per_day = Double.parseDouble(current_unit_count.getText().toString().trim());
                        int days_count = Integer.parseInt(days_left.getText().toString().trim());
                        double avg_unit = unit_per_day / days_count;

                        double roundedNumber2 = Utility.DecimalUtils.round(avg_unit, 2);
                        present_average_unit_per_day.setText(String.valueOf(roundedNumber2));

                        if (no_of_units > 50) {

                            mTimer = new CountDownTimer(10000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    mTimer.cancel();
                                    //setAlertDialog(getString(R.string.oops_your_bill_going_to_be_a_burden_on_you));
                                }
                            }.start();

                        }

                    }
                }
            }
        });

        current_unit_count.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!current_unit_count.getText().toString().trim().contains("-")) {
                    if (current_unit_count.getText().toString().trim().length() != 0) {
                        if (!current_unit_count.getText().toString().trim().contains("-")) {
                            double value = Double.parseDouble(current_unit_count.getText().toString().trim());
                            if (value<=30) {
                                double rate = 3.10 ;
                                double sum = value * rate + 40;
                                double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                                current_bill_amount.setText(String.valueOf(roundedNumber));
                            }else if (value<=50) {
                                double rate = 3.85 ;
                                double sum = (double) value * rate +60;
                                double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                                current_bill_amount.setText(String.valueOf(roundedNumber));
                            }else if (value<=100) {
                                double rate = 4.70 ;
                                double sum = (double) value * rate +60;
                                double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                                current_bill_amount.setText(String.valueOf(roundedNumber));
                            }else if (value<=300) {
                                double rate = 6.00 ;
                                double sum = (double) value * rate+60;
                                double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                                current_bill_amount.setText(String.valueOf(roundedNumber));
                            }else{
                                double rate = 6.30 ;
                                double sum = (double) value *  rate+60;
                                double roundedNumber = Utility.DecimalUtils.round(sum, 2);
                                current_bill_amount.setText(String.valueOf(roundedNumber));
                            }
                        }
                    }
                }
            }
        });
        return v;
    }

    private void setAlertDialog(String msge) {
        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.custom_dialog);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView message = (TextView) d.findViewById(R.id.message);
        message.setText(msge);

        Button control = (Button) d.findViewById(R.id.control);
        Button ok = (Button) d.findViewById(R.id.ok);

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                Fragment fragment = null;
                Class fragmentClass = null;
                FragmentManager fragmentManager;
                fragmentClass = Appliances.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }

    private void resetFeilds() {
        from_date_et.setFocusable(false);
        to_date_et.setFocusable(false);
        present_submitted_treading.setFocusable(false);
        previous_submitted_treading.setFocusable(false);
        present_submitted_units.setFocusable(false);
        present_average_unit_per_day.setFocusable(false);
        average_unit_per_day.setFocusable(false);
        current_date_et.setFocusable(false);

        current_unit_count.setFocusable(false);
        current_bill_amount.setFocusable(false);
        days_left.setFocusable(false);

        from_date_et.setText(storage.getValue(Constants.PREVIOUS_DATE));
        to_date_et.setText(storage.getValue(Constants.PRESENT_DATE));

        present_submitted_treading.setText(storage.getValue(Constants.PREVIOUS_READING));
        previous_submitted_treading.setText(storage.getValue(Constants.PRESENT_READING));

        present_submitted_units.setText(storage.getValue(Constants.TOTAL_UNITS));

        if (present_submitted_units.getText().toString().trim().length() != 0) {
            double per_day = Double.parseDouble(present_submitted_units.getText().toString().trim()) /
                    Integer.parseInt(storage.getValue(Constants.TOTAL_DAYS));
            double roundedNumber = Utility.DecimalUtils.round(per_day, 2);
            average_unit_per_day.setText("" + roundedNumber);
        }

        sdf = new SimpleDateFormat("dd-MMM-yyyy");
        sdf.format(new Date());

        current_date_et.setText(String.format("%s", sdf.format(new Date())));

    }

    private static String calculateReading(String presentReading, String previousReading) {
        int total = Integer.parseInt(presentReading) - Integer.parseInt(previousReading);
        return String.valueOf(total);
    }

    public void setDays() {
        try {
            Date dateBefore = sdf.parse(storage.getValue(Constants.PRESENT_DATE));
            Date dateAfter = sdf.parse(current_date_et.getText().toString());
            long difference = dateAfter.getTime() - dateBefore.getTime();
            float daysBetween = (difference / (1000 * 60 * 60 * 24));
            /*You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
            System.out.println(daysBetween);
            int integer = (int) daysBetween;
            integer = Integer.parseInt(storage.getValue(Constants.TOTAL_DAYS)) - integer;
            days_left.setText(String.valueOf(integer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDaysDifference(String from, String to) {
        try {
            Date dateBefore = sdf.parse(from);
            Date dateAfter = sdf.parse(to);
            long difference = dateBefore.getTime() - dateAfter.getTime();
            float daysBetween = (difference / (1000 * 60 * 60 * 24));
            /*You can also convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
            System.out.println(daysBetween);
            int integer = (int) daysBetween;
            days_left.setText(String.valueOf(integer));
            temp_count = integer;
            Log.i("temp_count", "--->" + temp_count);

            if (temp_count != 0) {
                int unit_count = Integer.parseInt(present_submitted_units.getText().toString().trim());

                double avg_unit = unit_count / temp_count;
                average_unit_per_day.setText("" + avg_unit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

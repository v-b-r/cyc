package info.allywingz.com.consumerpowersaver.Fragment;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class CurrentReadingNew extends Fragment {

    Storage storage;
    CountDownTimer mTimer;
    EditText present_reaing;
    TextView history, previous_reading, date_time,required_units, check, calculated_units, units_generated, gen_unit_charge, avg_unit, avg_amnt, days_left, expected_units, expected_bill, click_here, help;
    LinearLayout display_layout, blink_offer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_current_reading_new, container, false);
        storage = new Storage(getActivity());
        present_reaing = (EditText) v.findViewById(R.id.present_reaing);

        previous_reading = (TextView) v.findViewById(R.id.previous_reading);

        Log.i("PRESENT_READING", "--->" + storage.getValue(Constants.PRESENT_READING));
        previous_reading.setText(storage.getValue(Constants.PRESENT_READING));

        help = (TextView) v.findViewById(R.id.help);
        required_units = (TextView) v.findViewById(R.id.required_units);
        history = (TextView) v.findViewById(R.id.history);
        date_time = (TextView) v.findViewById(R.id.date_time);
        check = (TextView) v.findViewById(R.id.check);
        calculated_units = (TextView) v.findViewById(R.id.calculated_units);
        units_generated = (TextView) v.findViewById(R.id.units_generated);
        gen_unit_charge = (TextView) v.findViewById(R.id.gen_unit_charge);
        avg_unit = (TextView) v.findViewById(R.id.avg_unit);
        //avg_amnt = (TextView) v.findViewById(R.id.avg_amnt);
        days_left = (TextView) v.findViewById(R.id.days_left);
        expected_bill = (TextView) v.findViewById(R.id.expected_bill);
        expected_units = (TextView) v.findViewById(R.id.expected_units);
        click_here = (TextView) v.findViewById(R.id.click_here);

        display_layout = (LinearLayout) v.findViewById(R.id.display_layout);
        blink_offer = (LinearLayout) v.findViewById(R.id.blink_offer);
        display_layout.setVisibility(View.GONE);

        setTimeStamp(date_time);

        if (storage.getValue(Constants.SAVE_NEW_DATA).trim().length() != 0) {
            present_reaing.setText(storage.getValue(Constants.SAVE_NEW_DATA));
            setData();
        }

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (present_reaing.getText().toString().trim().length() == 0) {
                    present_reaing.setError("Enter Reading");
                    present_reaing.requestFocus();
                } else {
                    setDays();
                    setData();
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showToast(getActivity(), "Under development..!");
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHistoryPopup();
            }
        });

        click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage.saveSecure(Constants.SAVE_NEW_DATA, present_reaing.getText().toString().trim());
                Fragment fragment = null;
                Class fragmentClass = null;
                FragmentManager fragmentManager;
                fragmentClass = Appliances.class;
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

        return v;
    }

    private void setHistoryPopup() {
        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.history_popup);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText from_date_et = (EditText) d.findViewById(R.id.from_date_et);
        final EditText to_date_et = (EditText) d.findViewById(R.id.to_date_et);
        final EditText present_submitted_treading = (EditText) d.findViewById(R.id.present_submitted_treading);
        final EditText previous_submitted_treading = (EditText) d.findViewById(R.id.previous_submitted_treading);
        final EditText present_submitted_units = (EditText) d.findViewById(R.id.present_submitted_units);
        final EditText average_unit_per_day = (EditText) d.findViewById(R.id.average_unit_per_day);

        from_date_et.setFocusable(false);
        to_date_et.setFocusable(false);
        present_submitted_treading.setFocusable(false);
        previous_submitted_treading.setFocusable(false);
        present_submitted_units.setFocusable(false);
        average_unit_per_day.setFocusable(false);

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

        final Button ok_btn = (Button) d.findViewById(R.id.ok_btn);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    private void loadAnimationOne(LinearLayout blink_offer) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        previous_reading.startAnimation(animation);

    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 800;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(blink_offer.getVisibility() == View.VISIBLE){
                            blink_offer.setVisibility(View.INVISIBLE);
                        }else{
                            blink_offer.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }


    private void setData() {
        blink();
        double required_units_value = 0.0 ;

        int no_of_units = 0;
        if (calculated_units.getText().toString().trim().length() != 0) {
            if (Integer.parseInt(previous_reading.getText().toString().trim()) < Integer.parseInt(present_reaing.getText().toString().trim())) {
                display_layout.setVisibility(View.VISIBLE);
                no_of_units = Integer.parseInt(present_reaing.getText().toString().trim()) - Integer.parseInt(storage.getValue(Constants.PRESENT_READING));
                double roundedNumber = Utility.DecimalUtils.round(no_of_units, 2);
                calculated_units.setText(String.valueOf(roundedNumber));
                units_generated.setText(String.valueOf(roundedNumber));

                if (no_of_units < 190) {
                    calculated_units.setTextColor(getResources().getColor(R.color.green));
                } else if (no_of_units > 190 && no_of_units < 200) {
                    calculated_units.setTextColor(getResources().getColor(R.color.orange));
                } else if (no_of_units > 200) {
                    calculated_units.setTextColor(getResources().getColor(R.color.red));
                }

                double unit_per_day = Double.parseDouble(calculated_units.getText().toString().trim());
                double days_count = Double.parseDouble(storage.getValue(Constants.TOTAL_DAYS));
                double final_count = unit_per_day / days_count;

                final_count = Utility.DecimalUtils.round(final_count, 2);
                avg_unit.setText(String.valueOf(final_count));

                Log.i("units_generated","==>"+units_generated.getText().toString().trim());
/*
                if (storage.getValue(Constants.CATEGORY).equals("A")){
                    required_units_value = 100- Double.parseDouble(units_generated.getText().toString().trim());
                }else if (storage.getValue(Constants.CATEGORY).equals("B")){
                    required_units_value = 200- Double.parseDouble(units_generated.getText().toString().trim());
                }
*/

                if(Double.parseDouble(units_generated.getText().toString().trim())>90 && Double.parseDouble(units_generated.getText().toString().trim())<110) {
                    required_units_value = 100- Double.parseDouble(units_generated.getText().toString().trim());
                    Log.i("required_units_value A","==>"+required_units_value);
                }else if(Double.parseDouble(units_generated.getText().toString().trim())>180 && Double.parseDouble(units_generated.getText().toString().trim())<220) {
                    required_units_value = 200- Double.parseDouble(units_generated.getText().toString().trim());
                    Log.i("required_units_value B","==>"+required_units_value);
                }else{
                    required_units_value = 300- Double.parseDouble(units_generated.getText().toString().trim());
                    Log.i("required_units_value C","==>"+required_units_value);
                }

                required_units_value = required_units_value/Double.parseDouble(days_left.getText().toString().trim());
                required_units_value = Utility.DecimalUtils.round(required_units_value, 2);
                required_units.setText(String.valueOf(required_units_value).replace("-",""));

//            gen_unit_charge.setText(String.valueOf(Integer.parseInt(storage.getValue(Constants.TOTAL_UNITS))/Integer.parseInt(storage.getValue(Constants.TOTAL_DAYS))));
                if (storage.getValue(Constants.TOTAL_UNITS).length() != 0) {
                    double value = Double.parseDouble(units_generated.getText().toString().trim());
                    if (value <= 50) {
                        double rate = 1.45;
                        double sum = value * rate;
                        gen_unit_charge.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                    } else if (value <= 100) {
                        double rate = 1.45;
                        double rate2 = 2.60;

                        double sum = (double) 50 * rate;
                        value = value - 50;

                        double sum2 = (double) value * rate2;
                        double total = sum + sum2;
                        gen_unit_charge.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                    } else if (value <= 200) {
                        double rate = 3.30;
                        double rate2 = 4.30;

                        double sum = (double) 100 * rate;
                        value = value - 100;

                        double sum2 = (double) value * rate2;
                        double total = sum + sum2;
                        gen_unit_charge.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                    } else if (value <= 300) {
                        double rate = 5.0;
                        double rate2 = 7.20;

                        double sum = (double) 200 * rate;
                        value = value - 200;

                        double sum2 = (double) value * rate2;
                        double total = sum + sum2;
                        gen_unit_charge.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
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
                        gen_unit_charge.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                    } else {
                        double rate = 9.50;
                        double sum = (double) value * rate + 60;
                        double round_Number = Utility.DecimalUtils.round(sum, 2);
                        gen_unit_charge.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                    }

                    double temp_data = Double.parseDouble(gen_unit_charge.getText().toString().trim()) / days_count;

                    double expected_units_value = Double.parseDouble(days_left.getText().toString().trim()) * Double.parseDouble(avg_unit.getText().toString().trim());
                    expected_units_value = expected_units_value + Double.parseDouble(units_generated.getText().toString().trim());
                    expected_units.setText(String.valueOf(Utility.DecimalUtils.round(expected_units_value, 2)));

                    if (expected_units.getText().toString().trim().length() != 0) {
                        double value_expected = Double.parseDouble(expected_units.getText().toString().trim());
                        if (value_expected <= 50) {
                            double rate = 1.45;
                            double sum = value_expected * rate;
                            expected_bill.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                        } else if (value_expected <= 100) {
                            double rate = 1.45;
                            double rate2 = 2.60;

                            double sum = (double) 50 * rate;
                            value_expected = value_expected - 50;

                            double sum2 = (double) value_expected * rate2;
                            double total = sum + sum2;
                            expected_bill.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
                        } else if (value_expected <= 200) {
                            double rate = 3.30;
                            double rate2 = 4.30;

                            double sum = (double) 100 * rate;
                            value_expected = value_expected - 100;

                            double sum2 = (double) value_expected * rate2;
                            double total = sum + sum2;
                            expected_bill.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));

                        } else if (value_expected <= 300) {
                            double rate = 5.0;
                            double rate2 = 7.20;

                            double sum = (double) 200 * rate;
                            value_expected = value_expected - 200;

                            double sum2 = (double) value_expected * rate2;
                            double total = sum + sum2;
                            expected_bill.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));

                        } else if (value_expected <= 800) {
                            double rate = 5.0;
                            double rate2 = 7.20;
                            double rate3 = 8.50;
                            double rate4 = 9.0;

                            double sum = (double) 200 * rate;
                            value_expected = value_expected - 200;

                            double sum2 = (double) 300 * rate2;
                            value_expected = value_expected - 300;

                            double sum3 = (double) 400 * rate3;
                            value_expected = value_expected - 400;

                            double sum4 = (double) value_expected * rate4;

                            double total = sum + sum2 + sum3 + sum4;
                            expected_bill.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));

                        } else {
                            double rate = 9.50;
                            double sum = (double) value_expected * rate + 60;
                            expected_bill.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                        }
                    }
                }
            } else {
                Utility.showToast(getActivity(), "Present Reading Cannot be Less than Previous Reading");
            }
        }
    }

    public void setDays() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            sdf.format(new Date());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            Date date = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

            String temmp = String.format("%s", sdf.format(new Date()));
            Log.i("AfterDate", "==>" + temmp);
            Log.i("BeforeDate", "==>" + storage.getValue(Constants.PRESENT_DATE));

            Date dateBefore = sdf.parse(storage.getValue(Constants.PRESENT_DATE));
            Date dateAfter = sdf.parse(temmp);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            float daysBetween = (difference / (1000 * 60 * 60 * 24));
            /*To convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
            System.out.println("==>" + daysBetween);
            int integer = (int) daysBetween;
            integer = Integer.parseInt(storage.getValue(Constants.TOTAL_DAYS)) - integer;

            String current_date = temmp.split("-")[0];
            String previous_date = storage.getValue(Constants.TOTAL_DAYS).split("-")[0];
            int final_days = 0;

            if (Integer.parseInt(current_date) > Integer.parseInt(previous_date)) {
                final_days = Integer.parseInt(current_date) - Integer.parseInt(previous_date);
            } else {
                final_days = Integer.parseInt(previous_date) - Integer.parseInt(current_date);
            }
            Log.i("final_days", "==>" + final_days);
            days_left.setText(String.valueOf(final_days));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimeStamp(TextView date_time) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa dd-MMM-yyyy");
        String datetime = dateformat.format(c.getTime());
        System.out.println(datetime);
        this.date_time.setText(datetime);

        setDays();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    FragmentManager fragmentManager;
                    fragmentClass = UserReadingDetails.class;
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

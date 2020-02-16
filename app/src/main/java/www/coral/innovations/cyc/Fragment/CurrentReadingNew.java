package www.coral.innovations.cyc.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import www.coral.innovations.cyc.Activities.HomeScreen;
import www.coral.innovations.cyc.Model.DetailedChargesModel;
import www.coral.innovations.cyc.Model.GetChargesModel;
import www.coral.innovations.cyc.R;
import www.coral.innovations.cyc.Server.ServerCalls;
import www.coral.innovations.cyc.Storage.Constants;
import www.coral.innovations.cyc.Storage.Storage;
import www.coral.innovations.cyc.Storage.Utility;

public class CurrentReadingNew extends Fragment {
    ImageView meter_image ;
    float daysBetween ;
    double calculatedUnits = 0.0 , req_expected_Units = 0.0 , total_next_days = 0.0;
    String current_date ;
    Storage storage;
    CountDownTimer mTimer;
    EditText present_reading;
    TableRow tariff_one, required_text, tariff_two, tariff_three, tariff_four, tariff_five, tariff_six, tariff_seven ;
    TextView previous_bill_date, offer_amount, history, previous_reading, date_time, required_units, check, calculated_units, units_generated, gen_unit_charge, avg_unit, avg_amnt, days_left, expected_units, expected_bill, click_here, help;
    LinearLayout display_layout, blink_offer, layout_one, layout_two, layout_three;
    android.support.v7.widget.CardView offer_layout, other_layout;
    CountDownTimer newtimer ;

    /*public static void setKeyboardFocus(final EditText primaryTextField) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 100);
    }
*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_current_reading_new, container, false);
        storage = new Storage(getActivity());
        present_reading = (EditText) v.findViewById(R.id.present_reaing);

        previous_bill_date = (TextView) v.findViewById(R.id.previous_bill_date);
        previous_reading = (TextView) v.findViewById(R.id.previous_reading);
        meter_image = (ImageView)v.findViewById(R.id.meter_image);

        present_reading.requestFocus();
        //setKeyboardFocus(present_reading);

        Log.i("PRESENT_READING", "--->" + storage.getValue(Constants.PRESENT_READING));
        previous_reading.setText(storage.getValue(Constants.PRESENT_READING));
        previous_bill_date.setText(storage.getValue(Constants.PRESENT_DATE));

        help = (TextView) v.findViewById(R.id.help);
        offer_amount = (TextView) v.findViewById(R.id.offer_amount);
        //offer_amount.setShadowLayer(5, 0, 0, Color.BLACK);

        required_text = (TableRow) v.findViewById(R.id.required_text);
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

        offer_layout = (CardView) v.findViewById(R.id.offer_layout);
        other_layout = (CardView) v.findViewById(R.id.other_layout);

        layout_one = (LinearLayout) v.findViewById(R.id.layout_one);
        layout_two = (LinearLayout) v.findViewById(R.id.layout_two);
        layout_three = (LinearLayout) v.findViewById(R.id.layout_three);

        display_layout = (LinearLayout) v.findViewById(R.id.display_layout);
        blink_offer = (LinearLayout) v.findViewById(R.id.blink_offer);

        tariff_one = (TableRow)v.findViewById(R.id.tariff_one);
        tariff_two = (TableRow)v.findViewById(R.id.tariff_two);
        tariff_three = (TableRow)v.findViewById(R.id.tariff_three);
        tariff_four = (TableRow)v.findViewById(R.id.tariff_four);
        tariff_five = (TableRow)v.findViewById(R.id.tariff_five);
        tariff_six = (TableRow)v.findViewById(R.id.tariff_six);
        tariff_seven = (TableRow)v.findViewById(R.id.tariff_seven);

       /* layout_one.setVisibility(View.GONE);
        layout_two.setVisibility(View.GONE);
        layout_three.setVisibility(View.GONE);
*/
        display_layout.setVisibility(View.GONE);
        offer_layout.setVisibility(View.GONE);
        other_layout.setVisibility(View.GONE);

        newtimer = new CountDownTimer(1000000000, 1000) {

            public void onTick(long millisUntilFinished) {
//                Calendar c = Calendar.getInstance();
//                textView.setText(c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND));
                setTimeStamp(date_time);
            }
            public void onFinish() {

            }
        };
        newtimer.start();

        if (storage.getValue(Constants.SAVE_NEW_DATA).trim().length() != 0) {
            present_reading.setText(storage.getValue(Constants.SAVE_NEW_DATA));
            setStaticData();
        }

        if (storage.getValue(Constants.CATEGORY).equalsIgnoreCase("A") || storage.getValue(Constants.CATEGORY).equalsIgnoreCase("B")) {
            required_text.setVisibility(View.VISIBLE);
            required_units.setVisibility(View.VISIBLE);
        } else {
            required_text.setVisibility(View.GONE);
            required_units.setVisibility(View.GONE);
        }

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (present_reading.getText().toString().trim().length() == 0) {
                    present_reading.setError("Enter Reading");
                    present_reading.requestFocus();
                } else {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    if (Utility.isNetworkAvailable(getActivity())) {
                         if (Double.parseDouble(present_reading.getText().toString().trim())>Double.parseDouble(previous_reading.getText().toString().trim())){
                             setData();
                         }else{
                             Utility.showToast(getActivity(), "Current Reading Cannot be less than Previous Reading..!");
                         }
                    } else {
                        Utility.showToast(getActivity(), getString(R.string.Internet_Error));
                    }
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHistoryPopup();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.ZoomImage(getActivity(), meter_image);
            }
        });

        click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newtimer.cancel();
                storage.saveSecure(Constants.SAVE_NEW_DATA, present_reading.getText().toString().trim());
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

        setDays();

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

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 800;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (offer_amount.getVisibility() == View.VISIBLE) {
                            offer_amount.setVisibility(View.INVISIBLE);
                        } else {
                            offer_amount.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    private void setStaticData() {
        //blink();
        setDays();
        calculatedUnits = 0.0 ;
        req_expected_Units = 0.0 ;

        String days_for_Avg = String.valueOf(daysBetween).replace(".0","");
        double AvgUnits = Double.parseDouble(present_reading.getText().toString().trim()) - Double.parseDouble(storage.getValue(Constants.PRESENT_READING));
        AvgUnits =  AvgUnits / Double.parseDouble(days_for_Avg) ;

        avg_unit.setText(String.valueOf(Utility.DecimalUtils.round(AvgUnits, 2)));
        Log.i("AvgUnits","==>"+AvgUnits);

        // calculatedUnits = Double.parseDouble(present_reading.getText().toString().trim()) - Double.parseDouble(previous_reading.getText().toString().trim());
        calculatedUnits = ((Double.parseDouble(present_reading.getText().toString().trim()) - Double.parseDouble(storage.getValue(Constants.PRESENT_READING)))) ;
        Log.i("calculatedUnits 0","==>"+calculatedUnits);
        req_expected_Units = calculatedUnits ;
        calculatedUnits = calculatedUnits + (AvgUnits * Double.parseDouble(days_left.getText().toString().trim()));

        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(calculatedUnits, 2)));
        Log.i("calculatedUnits 1","==>"+calculatedUnits);
        Log.i("calculatedUnits 2","==>"+calculated_units.getText().toString().trim());

        display_layout.setVisibility(View.VISIBLE);
        offer_layout.setVisibility(View.GONE);
        other_layout.setVisibility(View.VISIBLE);

        setCalculatedUnitsToAmount(calculated_units);

        double required_units_value = 0.0 ;
        if (calculatedUnits> 90 && calculatedUnits< 110) {
            required_units_value = 100 - calculatedUnits;
            Log.i("required_units_value A", "==>" + required_units_value);
            required_text.setVisibility(View.VISIBLE);
            required_units.setVisibility(View.VISIBLE);
            offer_layout.setVisibility(View.VISIBLE);
            other_layout.setVisibility(View.GONE);
            offer_amount.setText("127.5");
        } else if (calculatedUnits > 180 && calculatedUnits < 220) {
            required_units_value = 200 - calculatedUnits;
            Log.i("required_units_value B", "==>" + required_units_value);
            required_text.setVisibility(View.VISIBLE);
            required_units.setVisibility(View.VISIBLE);
            offer_layout.setVisibility(View.VISIBLE);
            other_layout.setVisibility(View.GONE);
            offer_amount.setText("240");
        } else {
            required_text.setVisibility(View.GONE);
        }/*else {
            required_units_value = 300 - req_expected_Units;
            required_text.setVisibility(View.GONE);
            required_units.setVisibility(View.GONE);
            offer_layout.setVisibility(View.GONE);
            layout_one.setVisibility(View.VISIBLE);
            other_layout.setVisibility(View.VISIBLE);
            Log.i("required_units_value C", "==>" + required_units_value);
        }*/


        required_units_value = required_units_value / Double.parseDouble(days_left.getText().toString().trim());
        required_units_value = Utility.DecimalUtils.round(required_units_value, 2);
        required_units.setText(String.valueOf(required_units_value).replace("-", ""));

        //SaveCheckedDetails() ;
    }
    private void setData() {
        //blink();
        setDays();
        calculatedUnits = 0.0 ;
        req_expected_Units = 0.0 ;

        String days_for_Avg;
        if (daysBetween >= 1){
            days_for_Avg = String.valueOf(daysBetween).replace(".0","");
        } else {
            days_for_Avg = "1";
        }
        double curUnits = Double.parseDouble(present_reading.getText().toString().trim()) - Double.parseDouble(storage.getValue(Constants.PRESENT_READING));
        double AvgUnits =  curUnits / Double.parseDouble(days_for_Avg) ;

        if (Double.parseDouble(days_for_Avg) !=0) {
            Log.i("AvgUnits", "==>" + AvgUnits);
            avg_unit.setText(String.valueOf(Utility.DecimalUtils.round(AvgUnits, 2)));
        }else{
            Log.i("AvgUnits", "==>" + AvgUnits);
            avg_unit.setText("0.0");
        }

       // calculatedUnits = Double.parseDouble(present_reading.getText().toString().trim()) - Double.parseDouble(previous_reading.getText().toString().trim());
//        calculatedUnits = ((Double.parseDouble(present_reading.getText().toString().trim()) - Double.parseDouble(storage.getValue(Constants.PRESENT_READING)))) ;
        Log.i("calculatedUnits 0","==>"+curUnits);
        req_expected_Units = curUnits ;
        if (Double.parseDouble(days_left.getText().toString().trim()) > 0) {
            calculatedUnits = curUnits + (AvgUnits * Math.abs(Double.parseDouble(days_left.getText().toString().trim())));
        } else {
            calculatedUnits = curUnits;
        }


        Date prevDate = new Date();
        try {
            prevDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US).parse(storage.getValue(Constants.PRESENT_DATE));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

//        calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(calculatedUnits, 2)));
        calculated_units.setText("...");
        Log.i("calculatedUnits 1","==>"+calculatedUnits);
        Log.i("calculatedUnits 2","==>"+calculated_units.getText().toString().trim());

        display_layout.setVisibility(View.VISIBLE);
        offer_layout.setVisibility(View.GONE);
        other_layout.setVisibility(View.VISIBLE);

//        setCalculatedUnitsToAmount(calculated_units);

        double required_units_value = 0.0 ;
        if (Double.parseDouble(days_left.getText().toString().trim()) > 0) {
            if (calculatedUnits> 90 && calculatedUnits< 110) {
                required_units_value = 100 - calculatedUnits;
                Log.i("required_units_value A", "==>" + required_units_value);
                required_text.setVisibility(View.VISIBLE);
                required_units.setVisibility(View.VISIBLE);
                offer_layout.setVisibility(View.VISIBLE);
                other_layout.setVisibility(View.GONE);
                offer_amount.setText("127.5");
            } else if (calculatedUnits > 180 && calculatedUnits < 220) {
                required_units_value = 200 - calculatedUnits;
                Log.i("required_units_value B", "==>" + required_units_value);
                required_text.setVisibility(View.VISIBLE);
                required_units.setVisibility(View.VISIBLE);
                offer_layout.setVisibility(View.VISIBLE);
                other_layout.setVisibility(View.GONE);
                offer_amount.setText("240");
            } else {
                required_text.setVisibility(View.GONE);
            }
        } else {
            required_text.setVisibility(View.GONE);
        }
         /*else {
            required_units_value = 300 - req_expected_Units;
            required_text.setVisibility(View.GONE);
            required_units.setVisibility(View.GONE);
            offer_layout.setVisibility(View.GONE);
            layout_one.setVisibility(View.VISIBLE);
            other_layout.setVisibility(View.VISIBLE);
            Log.i("required_units_value C", "==>" + required_units_value);
        }*/

        if (Double.parseDouble(days_left.getText().toString().trim()) >= 1)
            required_units_value = required_units_value / Double.parseDouble(days_left.getText().toString().trim());
        required_units_value = Utility.DecimalUtils.round(required_units_value, 2);
        required_units.setText(String.valueOf(required_units_value).replace("-", ""));

        ParseObject readingParseObj = getReadingDetailsParseObject();
        GetChargesModel getCharges = new GetChargesModel(String.valueOf(calculatedUnits),
                storage.getValue(Constants.METER_CATEGORY),
                storage.getValue(Constants.METER_SUBCATEGORY),
                storage.getValue(Constants.METER_PHASE),
                storage.getValue(Constants.METER_LOAD),
                storage.getValue(Constants.METER_LOAD),
                new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(prevDate)//"2020-01-18"
        );
        ServerCalls.getDetailedCharges(this.getActivity(), getCharges, calculated_units, readingParseObj, storage);
    }

    private void setCalculatedUnitsToAmount(TextView calculated_units) {
        if (storage.getValue(Constants.TOTAL_UNITS).length() != 0) {
            double value = Double.parseDouble(calculated_units.getText().toString().trim());
            Log.i("BOARD NAMee", "==>" + storage.getValue(Constants.BOARD) );
            if (storage.getValue(Constants.BOARD).equals("MPPKVVJ") ||
                    storage.getValue(Constants.BOARD).equals("MPPKVVJ") ||
                    storage.getValue(Constants.BOARD).equals("MPPKVVJ")) {
                if (value <= 30) {
                    double rate = 3.10;
                    double sum = value * rate + 40;
                    calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                } else if (value <= 50) {
                    double rate = 3.85;
                    double sum = value * rate + 60;
                    calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                } else if (value <= 100) {
                    double rate = 3.85;
                    double sum = value * rate + 60;
                    calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                } else if (value <= 300) {
                    double rate = 6.0;
                    double sum = value * rate + 60;
                    calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                } else  {
                    double rate = 6.30;
                    double sum = value * rate + 60;
                    calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(sum, 2)));
                }
            }else {
                double rate1 = 1.45;
                double rate2 = 2.60;
                double rate3 = 3.30;
                double rate4 = 4.30;
                double rate5 = 5.00;
                double rate6 = 7.20;
                double rate7 = 8.50;
                double rate8 = 9.0;
                double rate9 = 9.50;

                double total = 0.0 ;
                if (value<=50){
                    total = value*rate1 ;
                    Log.i("1","==>"+total);
                }else if (value<=100){
                    value = value - 50 ;
                    total = 72.5 + (value*rate2) ;
                    Log.i("2","==>"+total);
                }else if (value<=200){
                    value = value-100 ;
                    total = 330 + (value*rate4) ;
                    Log.i("3","==>"+total);
                }else if (value<=300){
                    value = value-200 ;
                    total = 1000 + (value*rate6) ;
                    Log.i("4","==>"+total);
                }else if (value<=400){
                    Log.i("5","==>"+value*rate7);
                    value = value - 300 ;
                    total = value*rate7 ;
                    total = total + 1000 + 720  ;
                    Log.i("5","==>"+total);
                }else if (value<=800){
                    value = value-400 ;
                    total = 1000 + 720 + 850+ (value*rate8) ;
                    Log.i("6","==>"+total);
                }else if(value>800){
                    total = 1000 + 720+ 850 + (value*rate9) ;
                }
                Log.i("TTTTT","==>"+total);
                calculated_units.setText(String.valueOf(Utility.DecimalUtils.round(total, 2)));
            }
        }
    }


    private ParseObject getReadingDetailsParseObject() {
//        final ProgressDialog dialog = new ProgressDialog(getActivity());
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setMessage("Please wait");
//        dialog.show();

        if (Double.parseDouble(units_generated.getText().toString().trim()) > 90 && Double.parseDouble(units_generated.getText().toString().trim()) < 110) {
            storage.saveSecure(Constants.CATEGORY, "A");
        } else if (Double.parseDouble(units_generated.getText().toString().trim()) > 180 && Double.parseDouble(units_generated.getText().toString().trim()) < 220) {
            storage.saveSecure(Constants.CATEGORY, "B");
        } else {
            storage.saveSecure(Constants.CATEGORY, "C");
        }

        ParseObject object = new ParseObject("ReadingCheckedDetails");
        object.put("PreviousReading", previous_reading.getText().toString().trim());
        object.put("DateTime", Utility.setTimeStamp());
        object.put("CurrentReading", present_reading.getText().toString().trim());
        object.put("AverageUnit", avg_unit.getText().toString().trim());
        object.put("ExpectedBill", calculated_units.getText().toString().trim());
        object.put("USC_No", storage.getValue(Constants.USC_NO));

        if (required_units.getVisibility() == View.VISIBLE) {
            object.put("RequiredUnits", required_units.getText().toString().trim());
        } else {
            object.put("RequiredUnits", "-");
        }
        object.put("DaysLeft", days_left.getText().toString().trim());
        object.put("TotalCurrentUnits", String.valueOf(calculatedUnits));
        object.put("UserCategory", storage.getValue(Constants.CATEGORY));

        return object;
//        object.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                Log.i("ParseException", "==>" + e);
//                dialog.dismiss();
//                if (e == null) {
//                    storage.saveSecure(Constants.AVG_UNIT_PER_DAY, avg_unit.getText().toString().trim());
//                } else {
//                    Utility.showToast(getActivity(), "Please try Again...!");
//                }
//            }
//        });
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
            daysBetween = (difference / (1000 * 60 * 60 * 24));
            /*To convert the milliseconds to days using this method
             * float daysBetween =
             *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
             */
            System.out.println("==>" + daysBetween);
//            if (daysBetween>31){
//                setDaysAlert();
//            }

            int integer = (int) daysBetween;
            integer = Integer.parseInt(storage.getValue(Constants.TOTAL_DAYS)) - integer;

            current_date = temmp.split("-")[0];
            String previous_date = storage.getValue(Constants.TOTAL_DAYS).split("-")[0];
            double final_days = 0;
            System.out.println("current_date==>" + current_date);
            System.out.println("previous_date==>" + previous_date);

            final_days = Double.parseDouble(previous_date) - Double.parseDouble(String.valueOf(daysBetween));
            System.out.println("final_days==>" + final_days);

            total_next_days = Double.parseDouble(current_date )+ final_days ;
            days_left.setText(String.valueOf(final_days).replace(".0",""));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDaysAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle("Alert")
                .setMessage("Dear User, You have set your billing date for more than one month, Please Reset it to One Month.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newtimer.cancel();
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

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setDaysAlert();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setTimeStamp(TextView date_time) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa dd-MMM-yyyy");
        String datetime = dateformat.format(c.getTime());
       // System.out.println(datetime);
        this.date_time.setText(datetime);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        ((HomeScreen) getActivity()).setActionBarTitle(getString(R.string.present_bill_status));

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    newtimer.cancel();
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

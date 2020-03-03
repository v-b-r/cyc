package www.coralinnovations.cyc.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import www.coralinnovations.cyc.Activities.HomeScreen;
import www.coralinnovations.cyc.R;
import www.coralinnovations.cyc.Storage.Constants;
import www.coralinnovations.cyc.Storage.Storage;
import www.coralinnovations.cyc.Storage.Utility;

public class UserReadingDetails extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText present_date, present_reading, previous_date, previous_reading, total_units, total_days, total_amount;
    EditText meter_load, multi_factor;
    Spinner category_spinner, phase_spinner;
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
    String meterCat, meterSubCat, meterPhase;

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

        category_spinner = v.findViewById(R.id.meter_category);
        meter_load = v.findViewById(R.id.meter_load);
        phase_spinner = v.findViewById(R.id.meter_phase);
        multi_factor = v.findViewById(R.id.multi_factor);

        previous_reading.setText(storage.getValue(Constants.PREVIOUS_READING));
        present_reading.setText(storage.getValue(Constants.PRESENT_READING));
        present_date.setText(storage.getValue(Constants.PRESENT_DATE));
        previous_date.setText(storage.getValue(Constants.PREVIOUS_DATE));
        total_units.setText(storage.getValue(Constants.TOTAL_UNITS));
        total_amount.setText(storage.getValue(Constants.TOTAL_AMOUNT));
        meter_load.setText(storage.getValue(Constants.METER_LOAD));
        multi_factor.setText(storage.getValue(Constants.MULTI_FACTOR));

        reset = (Button) v.findViewById(R.id.btn_reset);
        submit = (Button) v.findViewById(R.id.btn_submit);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.meter_category_array, R.layout.spinner_item_text);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(arrayAdapter);
        category_spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.meter_phase_array, R.layout.spinner_item_text);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phase_spinner.setAdapter(arrayAdapter1);
        phase_spinner.setOnItemSelectedListener(this);

        if (storage.getValue(Constants.METER_CATEGORY).equals("2") && storage.getValue(Constants.METER_SUBCATEGORY).equals("0"))
            category_spinner.setSelection(1);

        if (storage.getValue(Constants.METER_PHASE).equals("3"))
            category_spinner.setSelection(1);

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
                }else if (multi_factor.getText().toString().trim().length()==0){
                    Utility.showToast(getActivity(),getString(R.string.previous_reading_error));
                }else if (meter_load.getText().toString().trim().length()==0){
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
                    double amount = Utility.getChargesAmount(value, storage.getValue(Constants.BOARD));
                    total_amount.setText(String.valueOf(amount));
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

//        Log.d("meterDetailsCat", meterCat==null?"-":meterCat);
//        Log.d("meterDetailsSubCat", meterSubCat==null?"-":meterSubCat);
//        Log.d("meterDetailsPhase", meterPhase==null?"-":meterPhase);

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
        board_details.put(Constants.METER_CATEGORY, meterCat);
        board_details.put(Constants.METER_SUBCATEGORY, meterSubCat);
        board_details.put(Constants.METER_PHASE, meterPhase);
        board_details.put(Constants.MULTI_FACTOR, multi_factor.getText().toString().trim());
        board_details.put(Constants.METER_LOAD, meter_load.getText().toString().trim());

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
                    storage.saveSecure(Constants.METER_CATEGORY, meterCat);
                    storage.saveSecure(Constants.METER_SUBCATEGORY, meterSubCat);
                    storage.saveSecure(Constants.METER_PHASE, meterPhase);
                    storage.saveSecure(Constants.METER_LOAD, meter_load.getText().toString().trim());
                    storage.saveSecure(Constants.MULTI_FACTOR, multi_factor.getText().toString().trim());

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.meter_category) {
            String selectedItem = (String) parent.getItemAtPosition(position);
            switch (position){
                case 0:
                    meterCat = "1";
                    meterSubCat = "0";
                    break;
                case 1:
                    meterCat = "2";
                    meterSubCat = "0";
                    break;
                default:
                    meterCat = "0";
                    meterSubCat = "0";
            }
        } else if (parent.getId() == R.id.meter_phase) {
            switch (position){
//                case 0:
//                    meterPhase = 1;
//                    break;
                case 1:
                    meterPhase = "3";
                    break;
                default:
                    meterPhase = "1";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

package www.coral.innovations.cyc.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import www.coral.innovations.cyc.Activities.HomeScreen;
import www.coral.innovations.cyc.Activities.SuggestionActivity;
import www.coral.innovations.cyc.Adapter.AppliancesAdapter;
import www.coral.innovations.cyc.Adapter.DBAdapter;
import www.coral.innovations.cyc.Adapter.DBTotalAdapter;
import www.coral.innovations.cyc.Adapter.HistoryAdapter;
import www.coral.innovations.cyc.Adapter.NewAppliancesAdapter;
import www.coral.innovations.cyc.Database.DBHelper;
import www.coral.innovations.cyc.Model.Child;
import www.coral.innovations.cyc.Model.DataModel;
import www.coral.innovations.cyc.Model.Group;
import www.coral.innovations.cyc.Model.HistoryModel;
import www.coral.innovations.cyc.R;
import www.coral.innovations.cyc.Storage.Constants;
import www.coral.innovations.cyc.Storage.Storage;
import www.coral.innovations.cyc.Storage.Utility;

import com.github.mikephil.charting.data.BarEntry;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

//Units = Power in Watts * time in hours / 1000
public class Appliances extends Fragment implements View.OnClickListener{
    private ArrayList<Group> mListItems;
    public static View.OnClickListener myOnClickListener;
    AppliancesAdapter mAdapter ;
    Calendar mcurrentDate;
    DatePickerDialog dpDialog;
    int year;
    int month;
    int day;
    SimpleDateFormat sdf ;
    Calendar calendar;
    Storage storage ;
    int from_hour = 0;
    int from_min = 0;

    int to_hour = 0;
    int to_min = 0;

    public static RecyclerView recyclerView, total_recyclerview;
    TextView no_appliance ;
    public static  RecyclerView.LayoutManager layoutManager;
    DBAdapter mDBAdapter ;
    public static NewAppliancesAdapter mNewDBAdapter ;
    DBTotalAdapter mTotalDBAdapter ;
    public static List<DataModel> dbList ;
    public static DBHelper db ;
    public static double temp_total = 0.0 ;

    PieChart pieChart ;
    ArrayList<Entry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;
    android.support.v7.widget.CardView card_view ;

    public static TextView grand_total_txt, avge_total , current_avg_units ;
    TextView fab ;
    LinearLayout dynamic_data ;

    TextInputLayout input_layout_item_name, input_layout_item_maker, input_layout_item_wattage, input_layout_item_usage;
    EditText  input_item_maker,input_item_wattage, input_item_usage ;
    AutoCompleteTextView input_item_name ;
    public static TextView item_name, maker, power, time, total_units, display_total ;
    public static LinearLayout complete_layout, suggestions_layout ;

    public static Button suggestions ;

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_appliances, container, false);
        storage = new Storage(getActivity());

        fab = (TextView)v.findViewById(R.id.fab);
        total_recyclerview = (RecyclerView)v.findViewById(R.id.recyclerview);
        current_avg_units = (TextView)v.findViewById(R.id.current_avg_units);
        no_appliance = (TextView)v.findViewById(R.id.no_appliance);
        no_appliance.setVisibility(View.GONE);

        current_avg_units.setText(storage.getValue(Constants.AVG_UNIT_PER_DAY));

        input_layout_item_name = (TextInputLayout)v.findViewById(R.id.input_layout_item_name);
        input_layout_item_maker = (TextInputLayout)v.findViewById(R.id.input_layout_item_maker);
        input_layout_item_wattage = (TextInputLayout)v.findViewById(R.id.input_layout_item_wattage);
        input_layout_item_usage = (TextInputLayout)v.findViewById(R.id.input_layout_item_usage);

        input_item_name = (AutoCompleteTextView)v.findViewById(R.id.input_item_name);
        input_item_maker = (EditText)v.findViewById(R.id.input_item_maker);
        input_item_wattage = (EditText)v.findViewById(R.id.input_item_wattage);
        input_item_usage = (EditText)v.findViewById(R.id.input_item_usage);

        suggestions = (Button)v.findViewById(R.id.suggestions);
        suggestions.setVisibility(View.INVISIBLE);
        suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SuggestionActivity.class));
            }
        });

        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(2000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        suggestions.startAnimation(animation);

        complete_layout = (LinearLayout)v.findViewById(R.id.complete_layout);
        suggestions_layout = (LinearLayout)v.findViewById(R.id.suggestions_layout);
        complete_layout.setVisibility(View.GONE);
        suggestions_layout.setVisibility(View.GONE);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, Constants.mItemsArray);
        input_item_name.setThreshold(1);
        input_item_name.setAdapter(mAdapter);

        item_name = (TextView)v.findViewById(R.id.item_name);
        maker  = (TextView)v.findViewById(R.id.maker);
        power = (TextView)v.findViewById(R.id.power);
        time = (TextView)v.findViewById(R.id.time);
        total_units = (TextView)v.findViewById(R.id.total_units);
        display_total = (TextView)v.findViewById(R.id.display_total);
        pieChart = (PieChart) v.findViewById(R.id.chart1);
        pieChart.setVisibility(View.GONE);
        entries = new ArrayList<>();

        PieEntryLabels = new ArrayList<String>();
        AddValuesToPIEENTRY();
        AddValuesToPieEntryLabels();
        pieDataSet = new PieDataSet(entries, "");
        pieData = new PieData(PieEntryLabels, pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(pieData);
        pieChart.animateY(3000);
        fab.setOnClickListener(this);

        if (Utility.isNetworkAvailable(getActivity())){
            getAppliancesData();
        }else {
            Utility.showToast(getActivity(),getString(R.string.Internet_Error));
        }

        return v;
    }

    public void AddValuesToPIEENTRY(){

        entries.add(new BarEntry(2f, 0));
        entries.add(new BarEntry(4f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(8f, 3));
        entries.add(new BarEntry(7f, 4));
        entries.add(new BarEntry(3f, 5));

    }

    public void AddValuesToPieEntryLabels(){
        PieEntryLabels.add("Light");
        PieEntryLabels.add("Fan");
        PieEntryLabels.add("Fridge");
        PieEntryLabels.add("AC");
        PieEntryLabels.add("Pump Motor");
        PieEntryLabels.add("Washing Machine");

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab :
                if (Utility.isNetworkAvailable(getActivity())) {
                    if (storage.getValue(Constants.AVG_UNIT_PER_DAY).length()!=0) {
                        if (Double.parseDouble(total_units.getText().toString().trim()) <= Double.parseDouble(storage.getValue(Constants.AVG_UNIT_PER_DAY))) {
                            if (input_item_name.getText().toString().trim().length() == 0) {
                                input_layout_item_name.setError("Please Enter Item Name");
                                input_item_name.requestFocus();
                            } else if (input_item_wattage.getText().toString().trim().length() == 0) {
                                input_layout_item_wattage.setError("Please Enter Item Wattage");
                                input_item_wattage.requestFocus();
                            } else if (input_item_usage.getText().toString().trim().length() == 0) {
                                input_layout_item_usage.setError("Please Enter Item Wattage");
                                input_item_usage.requestFocus();
                            } else {
                                if (Utility.isNetworkAvailable(getActivity())) {
                                    SaveAppliancesDetails();
                                } else {
                                    Utility.showToast(getActivity(), getString(R.string.Internet_Error));
                                }
                            }
                        }else{
                            Utility.showToast(getActivity(), "Total Units should not be greater than Average Units.");
                        }

                    }
                }else{
                    Utility.showToast(getActivity(),getString(R.string.Internet_Error));
                }
                break;
        }
    }

    private void SaveAppliancesDetails() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait");
        dialog.show();

        double temp_units  = Double.parseDouble(input_item_wattage.getText().toString().trim())*Double.parseDouble(input_item_usage.getText().toString().trim())/1000;
        Log.i("temp_units Parse","-->"+temp_units);

        ParseObject object = new ParseObject("ApplianceDetails");
        object.put("ItemName", input_item_name.getText().toString().trim());
        object.put("ItemMake", input_item_maker.getText().toString().trim());
        object.put("ItemWatts", input_item_wattage.getText().toString().trim());
        object.put("ItemUsageTime", input_item_usage.getText().toString().trim());
        object.put("UnitsPerDay", String.valueOf(temp_units));
        object.put("USC_No", storage.getValue(Constants.USC_NO));

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i("ParseException","==>"+e);
                dialog.dismiss();
                if (e==null) {
                    AddItemsLayout(input_item_name.getText().toString().trim(),
                            input_item_maker.getText().toString().trim(),
                            input_item_wattage.getText().toString().trim(),
                            input_item_usage.getText().toString().trim(),
                            total_units);

                    Utility.showToast(getActivity(), "Appliance Added Successfully!");
                }else{
                    Utility.showToast(getActivity(), "Please try Again...!");
                }
            }
        });
    }

    private void AddItemsLayout(String name, String maker_str, String wattage, String usage, TextView total_units) {

        double temp_units  = Double.parseDouble(wattage)*Double.parseDouble(usage)/1000;
        //Units = Power in Watts * time in hours / 1000 * days
        Log.i("temp_units 1","-->"+temp_units);
        Log.i("temp_units_2","-->"+Double.parseDouble(usage.split(":")[0]));

        db = new DBHelper(Appliances.this.getActivity());
        db.insertAppliances(name, maker_str, wattage, usage, String.valueOf(temp_units));

        input_item_name.requestFocus();
        input_item_name.setText("");
        input_item_maker.setText("");
        input_item_wattage.setText("");
        input_item_usage.setText("");

        dbList = new ArrayList<>();
        dbList.clear();
       // dbList = db.getAllAppliances();
        fetchDatabasetoList();
    }

    private void fetchDatabasetoList() {
        Log.i("fetchDatabasetoList","----Entered--->");
        db = new DBHelper(getActivity());
        dbList = new ArrayList<>();
        dbList.clear();

        String query = "SELECT  * FROM " + db.APPLIANCES_TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        DataModel model = null;
        temp_total = 0.0 ;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                model = new DataModel();
                model.setItem_name(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_NAME)));
                model.setItem_maker(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_MAKER)));
                model.setWatts(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_WATTS)));
                model.setUsage_hrs(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_USAGE_HRS)));
                model.setTotal_units(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_TOTAL_UNITS)));

                temp_total = temp_total + Double.parseDouble(model.getTotal_units());
                dbList.add(model);

                cursor.moveToNext();
            }
        }
        if (dbList.size()>0){
            complete_layout.setVisibility(View.VISIBLE);
            suggestions.setVisibility(View.VISIBLE);
        }else{
            complete_layout.setVisibility(View.GONE);
        }

        Log.i("dbList", "--->" + dbList.toString() + dbList.size());
        mNewDBAdapter = new NewAppliancesAdapter(getActivity() , dbList);
        total_recyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        total_recyclerview.setLayoutManager(layoutManager);
        total_recyclerview.setItemAnimator(new DefaultItemAnimator());
        total_recyclerview.setAdapter(mNewDBAdapter);

        temp_total = Utility.DecimalUtils.round(temp_total, 2);
        if(temp_total>15) {
            suggestions_layout.setVisibility(View.GONE);
        }else{
            suggestions_layout.setVisibility(View.GONE);
        }
        total_units.setText(String.valueOf(temp_total));
        display_total.setText(String.valueOf(temp_total));

    }


    private void setItemSelectionDialog() {
        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.appliances_items);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final Button pick_item = (Button)d.findViewById(R.id.pick_item);

        final TextInputLayout input_layout_item_name = (TextInputLayout)d.findViewById(R.id.input_layout_item_name);
        final TextInputLayout input_layout_item_watts = (TextInputLayout)d.findViewById(R.id.input_layout_item_watts);
        final TextInputLayout input_layout_item_quantity = (TextInputLayout)d.findViewById(R.id.input_layout_item_quantity);
        final TextInputLayout input_layout_item_usage = (TextInputLayout)d.findViewById(R.id.input_layout_item_usage);
        final TextInputLayout input_layout_item_usage_days = (TextInputLayout)d.findViewById(R.id.input_layout_item_usage_days);
        final TextInputLayout input_layout_item_from_time = (TextInputLayout)d.findViewById(R.id.input_layout_item_from_time);
        final TextInputLayout input_layout_item_to_time = (TextInputLayout)d.findViewById(R.id.input_layout_item_to_time);

        final EditText input_item_name = (EditText)d.findViewById(R.id.input_item_name);
        final EditText input_item_watts = (EditText)d.findViewById(R.id.input_item_watts);
        final EditText input_item_quantity = (EditText)d.findViewById(R.id.input_item_quantity);
        final EditText input_item_usage = (EditText)d.findViewById(R.id.input_item_usage);
        final EditText input_item_usage_days = (EditText)d.findViewById(R.id.input_item_usage_days);
        final EditText input_item_from_time = (EditText)d.findViewById(R.id.input_item_from_time);
        final EditText input_item_to_time = (EditText)d.findViewById(R.id.input_item_to_time);

        final Button ok = (Button) d.findViewById(R.id.ok);

        input_item_from_time.setFocusable(false);
        input_item_to_time.setFocusable(false);

        pick_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialogList(input_item_name, input_item_watts);
            }
        });

        input_item_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        input_item_to_time.setText(String.format("%02d:%02d",selectedHour , selectedMinute));
                        to_hour = selectedHour ;
                        to_min = selectedMinute ;

                        if (from_hour>to_hour){
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                    d.dismiss();
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
                    Appliances.this.db = new DBHelper(Appliances.this.getActivity());
                    Appliances.this.db.addItems(input_item_name.getText().toString().trim(), watts, quantity, hours, days, String.valueOf(temp_units));
                    Appliances.this.showToastMsg("Details Submitted");

                    Fragment fragment = null;
                    Class<Appliances> fragmentClass = null;
                    FragmentManager fragmentManager ;
                    fragmentClass = Appliances.class;
                    try {
                        fragment = fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
        });

        d.show();
    }

    private void getAppliancesData() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait");
        dialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ApplianceDetails");
        query.whereEqualTo("USC_No",storage.getValue(Constants.USC_NO));
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> personList, ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    Log.i("personList","==>"+personList.size());
                    dbList = new ArrayList<>();
                    dbList.clear();

                    if(personList.size()>0 ){
                        for (ParseObject person : personList) {
                            DataModel model = new DataModel();
                            model.setItem_name(person.getString("ItemName"));
                            model.setItem_maker(person.getString("ItemMake"));
                            model.setWatts(person.getString("ItemWatts"));
                            model.setUsage_hrs(person.getString("ItemUsageTime"));
                            model.setTotal_units(person.getString("UnitsPerDay"));

                            temp_total = temp_total + Double.parseDouble(person.getString("UnitsPerDay"));
                            dbList.add(model);

                        }
                        if (dbList.size()>0){
                            complete_layout.setVisibility(View.VISIBLE);
                            suggestions.setVisibility(View.VISIBLE);
                        }else{
                            complete_layout.setVisibility(View.GONE);
                        }

                        Log.i("dbList", "--->" + dbList.toString() + dbList.size());
                        mNewDBAdapter = new NewAppliancesAdapter(getActivity() , dbList);
                        total_recyclerview.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getActivity());
                        total_recyclerview.setLayoutManager(layoutManager);
                        total_recyclerview.setItemAnimator(new DefaultItemAnimator());
                        total_recyclerview.setAdapter(mNewDBAdapter);

                        temp_total = Utility.DecimalUtils.round(temp_total, 2);
                        if(temp_total>15) {
                            suggestions_layout.setVisibility(View.GONE);
                        }else{
                            suggestions_layout.setVisibility(View.GONE);
                        }
                        total_units.setText(String.valueOf(temp_total));
                        display_total.setText(String.valueOf(temp_total));

                    }else{
                        Utility.showToast(getActivity(),"No Appliance Added, Add now...!");
                    }
                } else {
                    Log.i("personList","==>"+e);
                    //Handle the exception
                }
            }
        });
    }

    private void setDialogList(final EditText input_item_name, final EditText input_item_watts) {
        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.items_popup);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ExpandableListView expandable_listview = (ExpandableListView)d.findViewById(R.id.expandable_listview);

        mListItems = setDatatToList();
        mAdapter = new AppliancesAdapter(getActivity(), mListItems);
        expandable_listview.setAdapter(mAdapter);

        expandable_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String group_name = mListItems.get(groupPosition).getName();

                ArrayList<Child> ch_list = mListItems.get(groupPosition).getItems();
                String child_name = ch_list.get(childPosition).getName();
                String child_watts = ch_list.get(childPosition).getWatts();


                showToastMsg(child_name);
                input_item_name.setText(child_name);
                input_item_watts.setText(child_watts);

                d.dismiss();
                return false;
            }
        });

        expandable_listview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                String group_name = mListItems.get(groupPosition).getName();
                showToastMsg(group_name + "n Expanded");

            }
        });

        d.show();
    }

    private ArrayList<Group> setDatatToList() {
        ArrayList localArrayList = new ArrayList();
        Object localObject1 = new ArrayList();
        Group localGroup = new Group();
        localGroup.setName("Light");
        Object localObject2 = new Child();
        ((Child)localObject2).setName("Tube Light");
        ((Child)localObject2).setWatts("40 Watt");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("LightBulb");
        ((Child)localObject2).setWatts("40 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("Light Bulb");
        ((Child)localObject2).setWatts("60 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("Light Bulb");
        ((Child)localObject2).setWatts("100 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("Light Bulb");
        ((Child)localObject2).setWatts("500 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("LED Tube");
        ((Child)localObject2).setWatts("18 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("LED Tube");
        ((Child)localObject2).setWatts("20 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("LED Tube");
        ((Child)localObject2).setWatts("40 W");
        ((ArrayList)localObject1).add(localObject2);
        localObject2 = new Child();
        ((Child)localObject2).setName("LED Tube");
        ((Child)localObject2).setWatts("80 W");
        ((ArrayList)localObject1).add(localObject2);
        localGroup.setItems((ArrayList<Child>)localObject1);
        localObject2 = new ArrayList();
        localObject1 = new Group();
        ((Group)localObject1).setName("Fan");
        Object localObject3 = new Child();
        ((Child)localObject3).setName("Ceiling Fan");
        ((Child)localObject3).setWatts("60 W");
        ((ArrayList)localObject2).add(localObject3);
        localObject3 = new Child();
        ((Child)localObject3).setName("Table Fan");
        ((Child)localObject3).setWatts("50 W");
        ((ArrayList)localObject2).add(localObject3);
        localObject3 = new Child();
        ((Child)localObject3).setName("Exhaust Fan");
        ((Child)localObject3).setWatts("40 W");
        ((ArrayList)localObject2).add(localObject3);
        localObject3 = new Child();
        ((Child)localObject3).setName("Cooler");
        ((Child)localObject3).setWatts("200 W");
        ((ArrayList)localObject2).add(localObject3);
        ((Group)localObject1).setItems((ArrayList<Child>)localObject2);
        localObject3 = new ArrayList();
        localObject2 = new Group();
        ((Group)localObject2).setName("TV/ Monitor");
        Object localObject4 = new Child();
        ((Child)localObject4).setName("Plasma TV");
        ((Child)localObject4).setWatts("136 W");
        ((ArrayList)localObject3).add(localObject4);
        localObject4 = new Child();
        ((Child)localObject4).setName("LCD TV");
        ((Child)localObject4).setWatts("70 W");
        ((ArrayList)localObject3).add(localObject4);
        localObject4 = new Child();
        ((Child)localObject4).setName("LED TV");
        ((Child)localObject4).setWatts("40 W");
        ((ArrayList)localObject3).add(localObject4);
        Object localObject5 = new Child();
        ((Child)localObject5).setName("CRT TV");
        ((Child)localObject5).setWatts("80 W");
        ((ArrayList)localObject3).add(localObject4);
        ((Group)localObject2).setItems((ArrayList<Child>)localObject3);
        localObject4 = new ArrayList();
        localObject3 = new Group();
        ((Group)localObject3).setName("Fridge");
        localObject5 = new Child();
        ((Child)localObject5).setName("Refrigerator(165 Ltr)");
        ((Child)localObject5).setWatts("180 W");
        ((ArrayList)localObject4).add(localObject5);
        localObject5 = new Child();
        ((Child)localObject5).setName("Refrigerator(210 Ltr)");
        ((Child)localObject5).setWatts("250 W");
        ((ArrayList)localObject4).add(localObject5);
        localObject5 = new Child();
        ((Child)localObject5).setName("Refrigerator(300 Ltr)");
        ((Child)localObject5).setWatts("300 W");
        ((ArrayList)localObject4).add(localObject5);
        ((Group)localObject3).setItems((ArrayList<Child>)localObject4);
        localObject5 = new ArrayList();
        localObject4 = new Group();
        ((Group)localObject4).setName("Computers");
        Child localChild1 = new Child();
        localChild1.setName("Computer general");
        localChild1.setWatts("200 W");
        ((ArrayList)localObject5).add(localChild1);
        localChild1 = new Child();
        localChild1.setName("Computer Gaming");
        localChild1.setWatts("400 W");
        ((ArrayList)localObject5).add(localChild1);
        localChild1 = new Child();
        localChild1.setName("Laptop");
        localChild1.setWatts("40 W");
        ((ArrayList)localObject5).add(localChild1);
        localChild1 = new Child();
        localChild1.setName("Wifi Router");
        localChild1.setWatts("5 W");
        ((ArrayList)localObject5).add(localChild1);
        ((Group)localObject4).setItems((ArrayList<Child>)localObject5);
        Object localObject6 = new ArrayList();
        localObject5 = new Group();
        ((Group)localObject5).setName("AC");
        localChild1 = new Child();
        localChild1.setName("Air Conditioner(1 Ton)");
        localChild1.setWatts("1500 W");
        ((ArrayList)localObject6).add(localChild1);
        localChild1 = new Child();
        localChild1.setName("Air Conditioner(1.5 Ton)");
        localChild1.setWatts("2250 W");
        ((ArrayList)localObject6).add(localChild1);
        localChild1 = new Child();
        localChild1.setName("Air Conditioner(2 Ton)");
        localChild1.setWatts("3000 W");
        ((ArrayList)localObject6).add(localChild1);
        Object localObject7 = new Child();
        ((Child)localObject7).setName("Split AC");
        ((Child)localObject7).setWatts("2250 W");
        ((ArrayList)localObject6).add(localObject7);
        ((Group)localObject5).setItems((ArrayList<Child>)localObject6);
        localObject7 = new ArrayList();
        localObject6 = new Group();
        ((Group)localObject6).setName("Kitchen Appliances");
        Object localObject8 = new Child();
        ((Child)localObject8).setName("Microwave Oven");
        ((Child)localObject8).setWatts("1000 W");
        ((ArrayList)localObject7).add(localObject8);
        localObject8 = new Child();
        ((Child)localObject8).setName("Toaster");
        ((Child)localObject8).setWatts("750 W");
        ((ArrayList)localObject7).add(localObject8);
        localObject8 = new Child();
        ((Child)localObject8).setName("Mixer Grinder");
        ((Child)localObject8).setWatts("500 W");
        ((ArrayList)localObject7).add(localChild1);
        localObject8 = new Child();
        ((Child)localObject8).setName("Grinder(1.5 Ltr)");
        ((Child)localObject8).setWatts("230 W");
        ((ArrayList)localObject7).add(localObject8);
        Child localChild2 = new Child();
        localChild2.setName("Grinder(2 Ltr)");
        localChild2.setWatts("240 W");
        ((ArrayList)localObject7).add(localObject8);
        ((Group)localObject6).setItems((ArrayList<Child>)localObject7);
        localObject7 = new ArrayList();
        localObject8 = new Group();
        ((Group)localObject8).setName("Home Accessories");
        localChild2 = new Child();
        localChild2.setName("Washing Machine");
        localChild2.setWatts("500 W");
        ((ArrayList)localObject7).add(localChild2);
        localChild2 = new Child();
        localChild2.setName("Vaccum Cleaner");
        localChild2.setWatts("200 W");
        ((ArrayList)localObject7).add(localChild2);
        localChild2 = new Child();
        localChild2.setName("Pump Motor");
        localChild2.setWatts("800 W");
        ((ArrayList)localObject7).add(localChild1);
        localChild1 = new Child();
        localChild1.setName("Inverter");
        localChild1.setWatts("600 W");
        ((ArrayList)localObject7).add(localChild1);
        ((Group)localObject8).setItems((ArrayList<Child>)localObject7);
        localArrayList.add(localGroup);
        localArrayList.add(localObject1);
        localArrayList.add(localObject2);
        localArrayList.add(localObject3);
        localArrayList.add(localObject4);
        localArrayList.add(localObject5);
        localArrayList.add(localObject6);
        localArrayList.add(localObject8);

        return localArrayList ;
    }

    public void showToastMsg(String Msg) {
        Toast.makeText(getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchDatabasetoList();

        if(getView() == null){
            return;
        }
        ((HomeScreen) getActivity()).setActionBarTitle(getString(R.string.appliances));

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

    public static void setAlertDialog(final Context context, final DataModel model) {
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        alertbox.setMessage("Would you like to delete Item ?");
        alertbox.setTitle("Delete Alert");

        alertbox.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alertbox.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        final ProgressDialog dialog = new ProgressDialog(context);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setMessage("Please wait");
                        dialog.show();

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ApplianceDetails");
                        query.whereEqualTo("ItemName", model.getItem_name());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                dialog.dismiss();
                                for (ParseObject object : objects) {
                                    try {
                                        object.delete();
                                        object.saveInBackground();
                                        DBHelper hp = new DBHelper(context);
                                        SQLiteDatabase db = hp.getWritableDatabase();

                                        db.delete(DBHelper.APPLIANCES_TABLE_NAME, //table name
                                                hp.ITEM_NAME+" = ?",  // selections
                                                new String[] { String.valueOf(model.getItem_name()) }); //selections args
                                        db.close();

                                        Utility.showToast(context, "Record Deleted...!");

                                        //Refresh DB
                                        dbList = new ArrayList<>();
                                        dbList.clear();

                                        String query = "SELECT  * FROM " + hp.APPLIANCES_TABLE_NAME;
                                        SQLiteDatabase sqLiteDatabase = hp.getWritableDatabase();
                                        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                                        DataModel model = null;
                                        temp_total = 0.0 ;

                                        if (cursor.moveToFirst()) {
                                            while (!cursor.isAfterLast()) {
                                                model = new DataModel();
                                                model.setItem_name(cursor.getString(cursor.getColumnIndexOrThrow(hp.ITEM_NAME)));
                                                model.setItem_maker(cursor.getString(cursor.getColumnIndexOrThrow(hp.ITEM_MAKER)));
                                                model.setWatts(cursor.getString(cursor.getColumnIndexOrThrow(hp.ITEM_WATTS)));
                                                model.setUsage_hrs(cursor.getString(cursor.getColumnIndexOrThrow(hp.ITEM_USAGE_HRS)));
                                                model.setTotal_units(cursor.getString(cursor.getColumnIndexOrThrow(hp.ITEM_TOTAL_UNITS)));

                                                temp_total = temp_total + Double.parseDouble(model.getTotal_units());
                                                dbList.add(model);

                                                cursor.moveToNext();
                                            }
                                        }
                                        if (dbList.size()>0){
                                            complete_layout.setVisibility(View.VISIBLE);
                                            suggestions.setVisibility(View.VISIBLE);
                                        }else{
                                            complete_layout.setVisibility(View.GONE);
                                        }

                                        Log.i("dbList", "--->" + dbList.toString() + dbList.size());
                                        mNewDBAdapter = new NewAppliancesAdapter(context, dbList);
                                        total_recyclerview.setAdapter(mNewDBAdapter);

                                        temp_total = Utility.DecimalUtils.round(temp_total, 2);
                                        if(temp_total>15) {
                                            suggestions_layout.setVisibility(View.GONE);
                                        }else{
                                            suggestions_layout.setVisibility(View.GONE);
                                        }
                                        total_units.setText(String.valueOf(temp_total));
                                        display_total.setText(String.valueOf(temp_total));

                                    } catch (ParseException exe) {
                                        exe.printStackTrace();
                                    }

                                }
                            }
                        });

                    }
                });
        alertbox.show();
    }
}

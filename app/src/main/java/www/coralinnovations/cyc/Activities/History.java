package www.coralinnovations.cyc.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import www.coralinnovations.cyc.Adapter.AppliancesAdapter;
import www.coralinnovations.cyc.Adapter.HistoryAdapter;
import www.coralinnovations.cyc.Fragment.CurrentReadingNew;
import www.coralinnovations.cyc.Model.HistoryModel;
import www.coralinnovations.cyc.R;
import www.coralinnovations.cyc.Storage.Constants;
import www.coralinnovations.cyc.Storage.Storage;
import www.coralinnovations.cyc.Storage.Utility;

import static www.coralinnovations.cyc.Storage.Utility.setTimeStamp;

public class History extends android.support.v4.app.Fragment {
    Storage storage;
    TextView no_data ;
    TableRow heading_row, feedback;
    public static RecyclerView recyclerView ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_history, container, false);
        storage = new Storage(getActivity());
        no_data = (TextView)v.findViewById(R.id.no_data);
        heading_row = (TableRow)v.findViewById(R.id.heading_row);
        feedback = (TableRow)v.findViewById(R.id.feedback);
        feedback.setVisibility(View.GONE);

        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        no_data.setVisibility(View.GONE);
        heading_row.setVisibility(View.GONE);

        if (Utility.isNetworkAvailable(getActivity())) {
            getHistoryData();
        }else {
            Utility.showToast(getActivity(),getString(R.string.Internet_Error));
        }

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFeedbackDialog();
            }
        });
        return v;
    }

    private void setFeedbackDialog() {
        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.feedback_layout);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText feedback = (EditText)d.findViewById(R.id.feedback);
        final Button submit = (Button)d.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.getText().toString().trim().length()==0){
                    feedback.requestFocus();
                    feedback.setError("Please Enter your FeedBack");
                }else {
                    if (Utility.isNetworkAvailable(getActivity())) {
                        submitFeedBack(d, feedback.getText().toString().trim());
                    }else {
                        Utility.showToast(getActivity(),getString(R.string.Internet_Error));
                    }
                }
            }
        });

        d.show();


    }

    private void submitFeedBack(final Dialog d, String feedback_msge) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait");
        dialog.show();

        ParseObject object = new ParseObject("UserFeedBack");
        object.put("DateTime", setTimeStamp());
        object.put("feedback_msge", feedback_msge);
        object.put("USC_No", storage.getValue(Constants.USC_NO));

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i("ParseException", "==>" + e);
                dialog.dismiss();
                if (e == null) {
                    d.dismiss();
                    Utility.showToast(getActivity(), "Thanks for your FeedBack...!");
                } else {
                    Utility.showToast(getActivity(), "Please try Again...!");
                }
            }
        });
    }

    private void getHistoryData() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait");
        dialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ReadingCheckedDetails");
        query.whereEqualTo("USC_No",storage.getValue(Constants.USC_NO));
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> personList, ParseException e) {
                dialog.dismiss();
                if (e == null) {
                    Log.i("personList","==>"+personList.size());
                    ArrayList<HistoryModel> histryList = new ArrayList<>();
                    histryList.clear();
                    if(personList.size()>0 ){
                        heading_row.setVisibility(View.VISIBLE);
                        String date = "" ;
                        String time = "" ;
                        for (ParseObject person : personList) {
                           /* HistoryModel model = new HistoryModel();
                            model.setDate(person.getString("DateTime"));
                            model.setAvg_unit_per_day(person.getString("AverageUnit"));
                            model.setExpected_amount(person.getString("ExpectedBill"));
                            Log.i("PreviousReading","==>"+person);*/

                            if (person.getString("DateTime").contains("am")){
                                String[] temp =  person.getString("DateTime").split("am") ;
                                date = temp[1];
                                time = temp[0];
                            }else{
                                String[] temp =  person.getString("DateTime").split("pm") ;
                                date = temp[1];
                                time = temp[0];
                            }
                            histryList.add(new HistoryModel(date+" "+time,person.getString("AverageUnit") +" (" +person.getString("CurrentReading") + ")",person.getString("ExpectedBill")));
                        }
                        Collections.reverse(histryList);
                        HistoryAdapter mAdapter = new HistoryAdapter(histryList, getActivity());
                        recyclerView.setAdapter(mAdapter);
                    }else{
                        no_data.setVisibility(View.VISIBLE);
                        heading_row.setVisibility(View.GONE);
                       Utility.showToast(getActivity(),"No Records Found...!");
                    }
                } else {
                    Log.i("personList","==>"+e);
                    //Handle the exception
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getView() == null){
            return;
        }
        ((HomeScreen) getActivity()).setActionBarTitle(getString(R.string.history));

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
}

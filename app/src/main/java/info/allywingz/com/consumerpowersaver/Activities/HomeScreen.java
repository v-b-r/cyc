package info.allywingz.com.consumerpowersaver.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.loopj.android.http.HttpGet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import info.allywingz.com.consumerpowersaver.Adapter.AppliancesAdapter;
import info.allywingz.com.consumerpowersaver.Adapter.BoardAdapter;
import info.allywingz.com.consumerpowersaver.Adapter.DBAdapter;
import info.allywingz.com.consumerpowersaver.Database.DBHelper;
import info.allywingz.com.consumerpowersaver.Fragment.Appliances;
import info.allywingz.com.consumerpowersaver.Fragment.CurrentReadingNew;
import info.allywingz.com.consumerpowersaver.Fragment.CurrentReadingStatusCalculation;
import info.allywingz.com.consumerpowersaver.Fragment.ProfileEdit;
import info.allywingz.com.consumerpowersaver.Fragment.UserReadingDetails;
import info.allywingz.com.consumerpowersaver.Model.BoardModel;
import info.allywingz.com.consumerpowersaver.Model.Child;
import info.allywingz.com.consumerpowersaver.Model.Group;
import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;
import info.allywingz.com.consumerpowersaver.Utils.GPSTracker;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    public static View.OnClickListener myOnClickListener;
    DrawerLayout drawer ;
    TextView user_name ;
    de.hdodenhof.circleimageview.CircleImageView user_image ;

    Storage storage ;
    DBHelper db ;

    String selected_item_name, selected_item_watts ;
    int from_hour = 0 , from_min = 0;
    int to_hour = 0 , to_min = 0;
    private ArrayList<Group> mListItems;
    AppliancesAdapter mAdapter ;

    EditText input_address ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = new Storage(HomeScreen.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("ADDRESS","===>"+storage.getValue(Constants.ADDRESS));
        if (storage.getValue(Constants.USC_NO).length()==0) {
            setDialog();
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        user_name = (TextView)headerView.findViewById(R.id.user_name);
        user_name.setText("Hello "+storage.getValue(Constants.USER_ID));

        user_image = (de.hdodenhof.circleimageview.CircleImageView)headerView.findViewById(R.id.profile_image);

        Log.i("USER_IMAGE","-->"+storage.getValue(Constants.USER_IMAGE));
        if (storage.getValue(Constants.USER_IMAGE).trim().length() > 4){
            byte[] decodedString = Base64.decode(storage.getValue(Constants.USER_IMAGE), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            user_image.setImageBitmap(decodedByte);
        }else{
            user_image.setImageDrawable(getResources().getDrawable(R.drawable.logo_orange));
        }

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                Class fragmentClass = null;
                FragmentManager fragmentManager ;
                fragmentClass = ProfileEdit.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                drawer.closeDrawers();
            }
        });

        Fragment fragment = null;
        Class fragmentClass = null;
        FragmentManager fragmentManager ;
        fragmentClass = UserReadingDetails.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


    }


    @SuppressLint("ClickableViewAccessibility")
    private void setDialog() {
        final Dialog d = new Dialog(HomeScreen.this);
        d.setContentView(R.layout.location_popup);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        d.setCanceledOnTouchOutside(false);

        final TextInputLayout input_layout_usc_no = (TextInputLayout)d.findViewById(R.id.input_layout_usc_no);
        final TextInputLayout input_layout_address = (TextInputLayout)d.findViewById(R.id.input_layout_address);

        final EditText input_usc_no = (EditText)d.findViewById(R.id.input_usc_no);
        input_address = (EditText)d.findViewById(R.id.input_address);

        final Spinner state_spinner = (Spinner)d.findViewById(R.id.state_spinner);
        final Spinner board_spinner = (Spinner)d.findViewById(R.id.board_spinner);

        final Button next = (Button)d.findViewById(R.id.next);

        ArrayList<String> mList =  new ArrayList<>();
        mList.add("Andhra Pradesh");
        mList.add("Telangana");
        mList.add("Madhya Pradesh");

        ArrayAdapter<String> madapter = new ArrayAdapter<String>(HomeScreen.this, android.R.layout.simple_list_item_1, mList);
        state_spinner.setAdapter(madapter);

        input_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (input_address.getRight() - input_address.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                        return true;
                    }
                }
                return false;
            }
        });

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(state_spinner.getSelectedItem().equals("Andhra Pradesh")){
                    board_spinner.setAdapter(new BoardAdapter(HomeScreen.this, R.layout.location_popup, getAndhraList()));
                }else if(state_spinner.getSelectedItem().equals("Telangana")){
                    board_spinner.setAdapter(new BoardAdapter(HomeScreen.this, R.layout.location_popup, getTelanganaList()));
                }else if(state_spinner.getSelectedItem().equals("Madhya Pradesh")){
                    board_spinner.setAdapter(new BoardAdapter(HomeScreen.this, R.layout.location_popup, getMadhyaPradeshList()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_usc_no.getText().toString().trim().length()==0){
                    input_layout_usc_no.setError("Please Enter USC No");
                    input_usc_no.requestFocus();
                }else if (input_address.getText().toString().trim().length()==0){
                    input_layout_address.setError("Please Enter Address");
                    input_address.requestFocus();
                }else{
                    if(board_spinner.getSelectedItem().equals("Northern")){
                        storage.saveSecure(Constants.BOARD, "TSNPDCL");
                    }else if(board_spinner.getSelectedItem().equals("Telangana")){
                        storage.saveSecure(Constants.BOARD, "TSSPDCL");
                    }else if(board_spinner.getSelectedItem().equals("Eastern")){
                        storage.saveSecure(Constants.BOARD, "APEPDCL");
                    }else if(board_spinner.getSelectedItem().equals("Southern")){
                        storage.saveSecure(Constants.BOARD, "APSPDCL");
                    }else if(board_spinner.getSelectedItem().equals("MP Poorv")){
                        storage.saveSecure(Constants.BOARD, "MPPKVVJ");
                    }else if(board_spinner.getSelectedItem().equals("MP Madhya")){
                        storage.saveSecure(Constants.BOARD, "MPMKVVB");
                    }else if(board_spinner.getSelectedItem().equals("Madhya")){
                        storage.saveSecure(Constants.BOARD, "MPPKVVCL");
                    }
                    storage.saveSecure(Constants.STATE, String.valueOf(state_spinner.getSelectedItem()));
                    storage.saveSecure(Constants.USC_NO, input_usc_no.getText().toString().trim());
                    storage.saveSecure(Constants.ADDRESS, input_address.getText().toString().trim());
                    Utility.showToast(getApplicationContext(), "Thank You!");
                    d.dismiss();
                }
            }
        });

        d.show();
    }

    public ArrayList<BoardModel> getTelanganaList() {
        ArrayList<BoardModel> allList = new ArrayList<BoardModel>();
        BoardModel item = new BoardModel();
        item.setData("Northern Power Distribution Company Of Telangana Ltd.(TSNPDCL)", R.drawable.tsspdcl_logo);
        allList.add(item);
        item = new BoardModel();
        item.setData("Telangana State Southern Power Distribution Company Ltd.(TSSPDCL)", R.drawable.npdcl);
        allList.add(item);
        return allList;
    }

    public ArrayList<BoardModel> getAndhraList() {
        ArrayList<BoardModel> allList = new ArrayList<BoardModel>();
        BoardModel item = new BoardModel();
        item.setData("Eastern Power Distribution Company of Andhra Pradesh Ltd. (APEPDCL)", R.drawable.eastern);
        allList.add(item);
        item = new BoardModel();
        item.setData("Southern Power Distribution Company of Andhra Pradesh Ltd. (APSPDCL)", R.drawable.southern);
        allList.add(item);
        return allList;
    }

    public ArrayList<BoardModel> getMadhyaPradeshList() {
        ArrayList<BoardModel> allList = new ArrayList<BoardModel>();
        BoardModel item = new BoardModel();
        item.setData("MP Poorv Kshetra Vidyut Vitaran Jabalpur", R.drawable.mpdcl);
        allList.add(item);

        item = new BoardModel();
        item.setData("MP Madhya Kshetra Vidyut Vitran Bhopal", R.drawable.mp_madhya_kshetra);
        allList.add(item);

        item = new BoardModel();
        item.setData("Madhya Pradesh Paschim Kshetra Vidyut Vitaran Company Ltd.(MPPKVVCL)", R.drawable.paschim_kshetra);
        allList.add(item);

        return allList;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void showToastMsg(String Msg) {
        Toast.makeText(HomeScreen.this, Msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        selectDrawerItem(item);

        return true;
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        FragmentManager fragmentManager ;

        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = UserReadingDetails.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                break;
            case R.id.nav_power:
                storage.saveSecure(Constants.SAVE_NEW_DATA, "");
                fragmentClass = CurrentReadingNew.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                break;
            case R.id.nav_profile:
                fragmentClass = ProfileEdit.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                break;

            case R.id.nav_appliances:
                if (storage.getValue(Constants.PRESENT_READING).length()!=0) {
                    fragmentClass = Appliances.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    menuItem.setChecked(true);
                    setTitle(menuItem.getTitle());
                }else{
                    Utility.showToast(getApplicationContext(), "Please fill the details in Previous Bill first");
                }
                break;

            case R.id.nav_logout:
                //fragmentClass = ThirdFragment.class;
                setLogout();
                break;
            default:
                storage.saveSecure(Constants.SAVE_NEW_DATA, "");
                fragmentClass = CurrentReadingNew.class;
        }


        // Close the navigation drawer
        drawer.closeDrawers();
    }

    private void setLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to logout from the application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storage.saveSecure(Constants.IS_LOGIN, "LoggedOut");
                        startActivity(new Intent(HomeScreen.this, LoginActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Alert");
        alert.show();
    }
}

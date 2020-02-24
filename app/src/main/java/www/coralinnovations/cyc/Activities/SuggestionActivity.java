package www.coralinnovations.cyc.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import www.coralinnovations.cyc.Adapter.SuggestionsAdapter;
import www.coralinnovations.cyc.Database.DBHelper;
import www.coralinnovations.cyc.Model.DataModel;
import www.coralinnovations.cyc.R;

public class SuggestionActivity extends AppCompatActivity {
    RecyclerView recyclerview;
    SuggestionsAdapter mNewDBAdapter ;
    List<DataModel> dbList ;
    DBHelper db ;
    ImageView backbutton_arrow_img;
    public static TextView saving_perday_units,toolbar_title, saving_units, current_units;
    TableRow blink_layout;

    LinearLayout one, two, three ;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        backbutton_arrow_img = (ImageView)findViewById(R.id.backbutton_arrow_img);
        saving_units = (TextView)findViewById(R.id.saving_units);
        current_units = (TextView)findViewById(R.id.current_units);
        saving_perday_units = (TextView)findViewById(R.id.saving_perday_units);
        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.suggestions_text);

        blink_layout = (TableRow)findViewById(R.id.blink_layout);
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);

        one = (LinearLayout) findViewById(R.id.one);
        two = (LinearLayout) findViewById(R.id.two);
        three = (LinearLayout) findViewById(R.id.three);

        backbutton_arrow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadAnimationOne();

    }

    private void loadAnimationOne() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_animation_in);
        one.startAnimation(a);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                loadAnimationtwo();
            }
        }, 1000);
    }

    private void loadAnimationtwo() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_animation_in);
        two.startAnimation(a);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                loadAnimationthree();
            }
        }, 1000);
    }

    private void loadAnimationthree() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.logo_animation_in);
        three.startAnimation(a);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDataformDB();
    }

    private void fetchDataformDB() {
        db = new DBHelper(SuggestionActivity.this);
        dbList = new ArrayList<>();
        dbList.clear();

        String query = "SELECT  * FROM " + db.APPLIANCES_TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        DataModel model = null;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                model = new DataModel();
                model.setItem_name(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_NAME)));
                model.setItem_maker(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_MAKER)));
                model.setWatts(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_WATTS)));
                model.setUsage_hrs(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_USAGE_HRS)));
                model.setTotal_units(cursor.getString(cursor.getColumnIndexOrThrow(db.ITEM_TOTAL_UNITS)));

                dbList.add(model);

                cursor.moveToNext();
            }
        }
        mNewDBAdapter = new SuggestionsAdapter(SuggestionActivity.this , dbList);
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SuggestionActivity.this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(mNewDBAdapter);
    }


}

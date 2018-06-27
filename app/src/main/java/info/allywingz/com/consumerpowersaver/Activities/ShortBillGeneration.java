package info.allywingz.com.consumerpowersaver.Activities;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;

public class ShortBillGeneration extends AppCompatActivity {

    ImageView backbutton_arrow_img, check_tick ;
    TextView toolbar_title ;
    TextView bill_amount, total_units, total_amount, discount, click_here, date_time ;
    Storage storage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_bill_generation);
        storage = new Storage(ShortBillGeneration.this);

        backbutton_arrow_img = (ImageView)findViewById(R.id.backbutton_arrow_img);
        check_tick = (ImageView)findViewById(R.id.check_tick);

        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText("CYC Payments");

        date_time = (TextView)findViewById(R.id.date_time);
        bill_amount = (TextView)findViewById(R.id.bill_amount);
        total_units = (TextView)findViewById(R.id.total_units);
        total_amount = (TextView)findViewById(R.id.total_amount);
        discount = (TextView)findViewById(R.id.discount);
        click_here = (TextView)findViewById(R.id.click_here);

        total_units.setText(storage.getValue(Constants.FINAL_TOTAL_UNITS));
        total_amount.setText(storage.getValue(Constants.TOTAL_AMOUNT));
        discount.setText("5.0");

        double temp = Double.parseDouble(storage.getValue(Constants.TOTAL_AMOUNT));
        temp = temp-5.0 ;

        bill_amount.setText(String.valueOf(temp));

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa dd-MMM-yyyy");
        String datetime = dateformat.format(c.getTime());
        System.out.println(datetime);
        date_time.setText(datetime);

        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(1000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.RESTART); // Reverse animation at the end so the button will fade back in
        check_tick.startAnimation(animation);

        /*check_tick.setInAnimation(context, R.anim.grow_from_middle);
        check_tick.setOutAnimation(context, R.anim.shrink_to_middle);*/
        click_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShortBillGeneration.this, GenerateBill.class));
            }
        });

        backbutton_arrow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

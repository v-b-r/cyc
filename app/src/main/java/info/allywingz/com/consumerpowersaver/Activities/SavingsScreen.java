package info.allywingz.com.consumerpowersaver.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class SavingsScreen extends AppCompatActivity {

    ImageView backbutton_arrow_img;
    TextView toolbar_title ;
    TextView previous_reading, date_time, help,  check, gen_unit_charge ;
    EditText present_reading ;
    Storage storage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_screen);
        storage = new Storage(SavingsScreen.this);

        present_reading = (EditText)findViewById(R.id.present_reading);

        toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        date_time = (TextView)findViewById(R.id.date_time);
        previous_reading = (TextView)findViewById(R.id.previous_reading);
        check = (TextView)findViewById(R.id.check);
        gen_unit_charge = (TextView)findViewById(R.id.gen_unit_charge);
        help = (TextView)findViewById(R.id.help);

        backbutton_arrow_img = (ImageView)findViewById(R.id.backbutton_arrow_img);

        toolbar_title.setText("Save Your Consumption");
        date_time.setText(Utility.setTimeStamp());
        previous_reading.setText(storage.getValue(Constants.ENTERED_READING));

        backbutton_arrow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

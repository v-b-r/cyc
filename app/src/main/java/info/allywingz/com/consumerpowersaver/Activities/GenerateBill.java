package info.allywingz.com.consumerpowersaver.Activities;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Constants;
import info.allywingz.com.consumerpowersaver.Storage.Storage;

public class GenerateBill extends AppCompatActivity {
    TextView date, time, bill,ero, ero_no, sec, area_code, grp, sc_no, usc, name, address, CAT, contracted_load, meter_no, mf,
            previous, present, previous_date, present_date, previous_units, present_units, rmd, energy_charges, cust_charges,
            electricity_duty, edint, additional_charges, int_on_sd, bill_amount, net_amount,
            as_on_date, after, total_amount, total_due,
            due_date, last_paid_date, subsidy, toolbar_title, board_main_name ;;

    Storage storage ;
    ImageView backbutton_arrow_img, board_image ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);
        storage = new Storage(GenerateBill.this);

        date = (TextView)findViewById(R.id.date);
        toolbar_title = (TextView)findViewById(R.id.toolbar_title);

        board_image = (ImageView)findViewById(R.id.board_image);
        backbutton_arrow_img = (ImageView)findViewById(R.id.backbutton_arrow_img);

        toolbar_title.setText("ELECTRICTY BILL");
        time = (TextView)findViewById(R.id.time);
        bill = (TextView)findViewById(R.id.bill);
        ero_no = (TextView)findViewById(R.id.ero_no);
        ero = (TextView)findViewById(R.id.ero);
        sec = (TextView)findViewById(R.id.sec);
        area_code = (TextView)findViewById(R.id.area_code);
        grp = (TextView)findViewById(R.id.grp);
        sc_no = (TextView)findViewById(R.id.sc_no);
        usc = (TextView)findViewById(R.id.usc);
        name = (TextView)findViewById(R.id.name);
        address = (TextView)findViewById(R.id.address);
        CAT = (TextView)findViewById(R.id.CAT);
        contracted_load = (TextView)findViewById(R.id.contracted_load);
        meter_no = (TextView)findViewById(R.id.meter_no);
        mf = (TextView)findViewById(R.id.mf);
        board_main_name = (TextView)findViewById(R.id.board_main_name);

        previous = (TextView)findViewById(R.id.previous);
        present = (TextView)findViewById(R.id.present);
        previous_date = (TextView)findViewById(R.id.previous_date);
        present_date = (TextView)findViewById(R.id.present_date);
        previous_units = (TextView)findViewById(R.id.previous_units);
        present_units = (TextView)findViewById(R.id.present_units);
        rmd = (TextView)findViewById(R.id.rmd);
        energy_charges = (TextView)findViewById(R.id.energy_charges);
        cust_charges = (TextView)findViewById(R.id.cust_charges);

        electricity_duty = (TextView)findViewById(R.id.electricity_duty);
        edint = (TextView)findViewById(R.id.edint);
        additional_charges = (TextView)findViewById(R.id.additional_charges);
        int_on_sd = (TextView)findViewById(R.id.int_on_sd);
        bill_amount = (TextView)findViewById(R.id.bill_amount);
        net_amount = (TextView)findViewById(R.id.net_amount);
        as_on_date = (TextView)findViewById(R.id.as_on_date);
        after = (TextView)findViewById(R.id.after);
        total_amount = (TextView)findViewById(R.id.total_amount);
        total_due = (TextView)findViewById(R.id.total_due);

        due_date = (TextView)findViewById(R.id.due_date);
        last_paid_date = (TextView)findViewById(R.id.last_paid_date);
        subsidy = (TextView)findViewById(R.id.subsidy);

        setValues();

        backbutton_arrow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setValues() {
        board_main_name.setText(storage.getValue(Constants.BOARD).toUpperCase());
        if(storage.getValue(Constants.BOARD).equals("TSNPDCL")){
            board_image.setImageDrawable(getResources().getDrawable(R.drawable.npdcl));
        }else if(storage.getValue(Constants.BOARD).equals("TSSPDCL")){
            board_image.setImageDrawable(getResources().getDrawable(R.drawable.tsspdcl_logo));
        }else if(storage.getValue(Constants.BOARD).equals("MPPKVVJ")){
            board_image.setImageDrawable(getResources().getDrawable(R.drawable.mpdcl));
        }

        date.setText(storage.getValue(Constants.DATE));
        time.setText(storage.getValue(Constants.TIME));
        bill.setText(storage.getValue(Constants.BILL));
        ero_no.setText(storage.getValue(Constants.ERO_NO));
        ero.setText(storage.getValue(Constants.ERO_NO));
        sec.setText(storage.getValue(Constants.SEC));
        area_code.setText(storage.getValue(Constants.AREA_CODE));
        grp.setText(storage.getValue(Constants.GRP));

        sc_no.setText(storage.getValue(Constants.SC_NO));
        usc.setText(storage.getValue(Constants.USC_NO));
        name.setText(storage.getValue(Constants.NAME));
        address.setText("Address :"+storage.getValue(Constants.ADDRESS));
        CAT.setText(storage.getValue(Constants.CAT));
        contracted_load.setText(storage.getValue(Constants.CONTRACTED_LOAD));
        meter_no.setText(storage.getValue(Constants.METER_NO));
        mf.setText(storage.getValue(Constants.MF));

        previous.setText(storage.getValue(Constants.PREVIOUS_READING));
        present.setText(storage.getValue(Constants.FINAL_READINGS));
        previous_date.setText(storage.getValue(Constants.PREVIOUS_DATE));
        present_date.setText(storage.getValue(Constants.PRESENT_DATE));
        previous_units.setText(storage.getValue(Constants.PREVIOUS_UNITS));
        present_units.setText(storage.getValue(Constants.PRESENT_UNITS));
        rmd.setText(storage.getValue(Constants.RMD));
        energy_charges.setText(storage.getValue(Constants.ENERGY_CHARGES));
        cust_charges.setText(storage.getValue(Constants.CUST_CHARGES));

        electricity_duty.setText(storage.getValue(Constants.ELECTRICITY_DUTY));
        edint.setText(storage.getValue(Constants.EDINT));
        additional_charges.setText(storage.getValue(Constants.ADDITIONAL_CHARGES));
        int_on_sd.setText(storage.getValue(Constants.INT_ON_SD));
        bill_amount.setText(storage.getValue(Constants.BILL_AMOUNT));
        net_amount.setText(storage.getValue(Constants.NET_AMOUNT));
        as_on_date.setText(storage.getValue(Constants.AS_ON_DATE));
        after.setText(storage.getValue(Constants.AFTER));
        total_amount.setText(storage.getValue(Constants.TOTAL_AMOUNT));
        total_due.setText(storage.getValue(Constants.TOTAL_DUE));

        due_date.setText(storage.getValue(Constants.DUE_DATE));
        last_paid_date.setText(storage.getValue(Constants.LAST_PAID_DATE));
        subsidy.setText(storage.getValue(Constants.SUBSIDY));
    }
}

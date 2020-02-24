package www.coralinnovations.cyc.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import www.coralinnovations.cyc.Activities.UpdateActivity;
import www.coralinnovations.cyc.Database.DBHelper;
import www.coralinnovations.cyc.Fragment.Appliances;
import www.coralinnovations.cyc.Model.DataModel;
import www.coralinnovations.cyc.R;

/**
 * Created by srinu on 5/13/2018.
 */

public class DBAdapter extends RecyclerView.Adapter<DBAdapter.MyViewHolder> {
    private List<DataModel> dataSet;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        TextView item_qty;
        TextView item_power;
        TextView item_days;
        TextView item_time;
        ImageView item_remove;
        TextView total_units;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.item_name = (TextView) itemView.findViewById(R.id.item_name);
            this.item_qty = (TextView) itemView.findViewById(R.id.item_qty);
            this.item_power = (TextView) itemView.findViewById(R.id.power);
            this.item_days = (TextView) itemView.findViewById(R.id.days);
            this.item_time = (TextView) itemView.findViewById(R.id.time);
            this.item_remove = (ImageView) itemView.findViewById(R.id.item_remove);
            this.total_units = (TextView) itemView.findViewById(R.id.total_units);
        }
    }

    public DBAdapter(FragmentActivity appliances, List<DataModel> data) {
        this.dataSet = data;
        this.context = appliances;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView item_name = holder.item_name;
        TextView item_qty = holder.item_qty;
        TextView item_power = holder.item_power;
        TextView item_days = holder.item_days;
        TextView item_time = holder.item_time;
        ImageView item_remove = holder.item_remove;
        TextView total_units = holder.total_units;

        item_name.setText(dataSet.get(listPosition).getItem_name());
        item_power.setText(dataSet.get(listPosition).getItem_power());
        item_qty.setText(dataSet.get(listPosition).getItem_qty());
        item_days.setText(dataSet.get(listPosition).getUsage_days());
        item_time.setText(dataSet.get(listPosition).getUsage_hrs());

        total_units.setText(String.format("%s Units", dataSet.get(listPosition).getTotal_units()));

        item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataModel model = new DataModel();
                model.setItem_name(dataSet.get(listPosition).getItem_name());
                model.setItem_power(dataSet.get(listPosition).getItem_power());
                model.setItem_qty(dataSet.get(listPosition).getItem_qty());
                model.setUsage_days(dataSet.get(listPosition).getUsage_days());
                model.setUsage_hrs(dataSet.get(listPosition).getUsage_hrs());
                Appliances.setAlertDialog(context, model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

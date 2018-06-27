package info.allywingz.com.consumerpowersaver.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.allywingz.com.consumerpowersaver.Database.DBHelper;
import info.allywingz.com.consumerpowersaver.Fragment.Appliances;
import info.allywingz.com.consumerpowersaver.Interfaces.OnRecyclerViewItemClickListener;
import info.allywingz.com.consumerpowersaver.Model.DataModel;
import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class NewAppliancesAdapter extends RecyclerView.Adapter<NewAppliancesAdapter.MyViewHolder> {
    private List<DataModel> dataSet;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        TextView maker;
        TextView power;
        TextView time;
        TextView total_units;
        ImageView delete ;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.item_name = (TextView) itemView.findViewById(R.id.item_name);
            this.maker = (TextView) itemView.findViewById(R.id.maker);
            this.power = (TextView) itemView.findViewById(R.id.power);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.total_units = (TextView) itemView.findViewById(R.id.total_units);
            this.delete = (ImageView) itemView.findViewById(R.id.delete);
        }
    }

    public NewAppliancesAdapter(FragmentActivity appliances, List<DataModel> data) {
        this.dataSet = data;
        this.context = appliances;
    }

    @Override
    public NewAppliancesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_applicances_adding, parent, false);

        NewAppliancesAdapter.MyViewHolder myViewHolder = new NewAppliancesAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final NewAppliancesAdapter.MyViewHolder holder, final int listPosition) {
        final TextView item_name = holder.item_name;
        TextView maker = holder.maker;
        final TextView power = holder.power;
        TextView time = holder.time;
        TextView total_units = holder.total_units;
        ImageView delete = holder.delete;

        Log.i("dataSet","--->"+dataSet.get(listPosition).getTotal_units());

        item_name.setText(dataSet.get(listPosition).getItem_name());
        maker.setText(dataSet.get(listPosition).getItem_maker());
        power.setText(dataSet.get(listPosition).getWatts());
        time.setText(dataSet.get(listPosition).getUsage_hrs());
        double roundedNumber =
                Utility.DecimalUtils.round(Double.parseDouble(dataSet.get(listPosition).getTotal_units()), 2);
        total_units.setText(String.valueOf(roundedNumber));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appliances.setAlertDialog(context, dataSet.get(listPosition).getItem_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

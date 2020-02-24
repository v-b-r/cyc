package www.coralinnovations.cyc.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import www.coralinnovations.cyc.Fragment.Appliances;
import www.coralinnovations.cyc.Model.DataModel;
import www.coralinnovations.cyc.R;

public class DBTotalAdapter extends RecyclerView.Adapter<DBTotalAdapter.MyViewHolder> {

    public static double grand_total = 0.0 ;
    public static double average_per_day = 0.0 ;
    private List<DataModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_name;
        TextView total_units;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.item_name = (TextView) itemView.findViewById(R.id.item_name);
            this.total_units = (TextView) itemView.findViewById(R.id.units);
        }
    }

    public DBTotalAdapter(List<DataModel> data) {
        this.dataSet = data;
    }

    @Override
    public DBTotalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.total_recycler_item, parent, false);

        DBTotalAdapter.MyViewHolder myViewHolder = new DBTotalAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final DBTotalAdapter.MyViewHolder holder, final int listPosition) {

        TextView item_name = holder.item_name;
        TextView total_units = holder.total_units;

        item_name.setText(dataSet.get(listPosition).getItem_name()+"("+dataSet.get(listPosition).getItem_qty()+")");
        total_units.setText(dataSet.get(listPosition).getTotal_units()+ " Units");

        grand_total = grand_total + Double.parseDouble(dataSet.get(listPosition).getTotal_units());

        average_per_day = grand_total/Double.parseDouble(dataSet.get(listPosition).getUsage_days());

        Appliances.grand_total_txt.setText(String.valueOf(grand_total)+ " Units");

        Appliances.avge_total.setText(String.valueOf(average_per_day)+ " Units");

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

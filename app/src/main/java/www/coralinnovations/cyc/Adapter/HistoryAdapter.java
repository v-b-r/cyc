package www.coralinnovations.cyc.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import www.coralinnovations.cyc.Model.HistoryModel;
import www.coralinnovations.cyc.R;
import www.coralinnovations.cyc.Storage.Storage;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>
{
    Storage storage ;
    private List<HistoryModel> mList=new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;


    public HistoryAdapter(List<HistoryModel> mList, Context context) {
        this.mList = mList;
        this.context = context;

        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        HistoryAdapter.MyViewHolder recyclerViewHolder = new HistoryAdapter.MyViewHolder(view);
        storage = new Storage(context);
        return recyclerViewHolder;

    }

    @Override
    public void onBindViewHolder(HistoryAdapter.MyViewHolder holder, int position)
    {
        final HistoryModel mValues= mList.get(position);
        holder.date.setText(mValues.getDate());
        holder.avg_unit.setText(mValues.getAvg_unit_per_day());
        holder.expected_amnt.setText(mValues.getExpected_amount());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView date, avg_unit, expected_amnt;

        public MyViewHolder(View view) {
            super(view);
            date = (TextView)view.findViewById(R.id.date);
            avg_unit = (TextView)view.findViewById(R.id.avg_unit);
            expected_amnt =( TextView)view.findViewById(R.id.expected_amnt);
        }
    }
}



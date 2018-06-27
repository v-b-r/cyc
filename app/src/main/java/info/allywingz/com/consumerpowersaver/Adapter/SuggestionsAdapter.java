package info.allywingz.com.consumerpowersaver.Adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.allywingz.com.consumerpowersaver.Activities.SuggestionActivity;
import info.allywingz.com.consumerpowersaver.Model.DataModel;
import info.allywingz.com.consumerpowersaver.R;
import info.allywingz.com.consumerpowersaver.Storage.Utility;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.MyViewHolder> {
    private List<DataModel> dataSet;
    Context context;
    double total_savings = 0.0 ;
    double total_consumption = 0.0 ;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_name2;
        TextView maker, maker2;
        TextView power, power2;
        TextView time, time2;
        TextView total_units, total_units2;
        ImageView delete ;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.item_name = (TextView) itemView.findViewById(R.id.item_name);
            this.maker = (TextView) itemView.findViewById(R.id.maker);
            this.power = (TextView) itemView.findViewById(R.id.power);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.total_units = (TextView) itemView.findViewById(R.id.total_units);
            this.item_name2 = (TextView) itemView.findViewById(R.id.item_name2);
            this.maker2 = (TextView) itemView.findViewById(R.id.maker2);
            this.power2 = (TextView) itemView.findViewById(R.id.power2);
            this.time2 = (TextView) itemView.findViewById(R.id.time2);
            this.total_units2 = (TextView) itemView.findViewById(R.id.total_units2);
            this.delete = (ImageView) itemView.findViewById(R.id.delete);
        }
    }

    public SuggestionsAdapter(FragmentActivity appliances, List<DataModel> data) {
        this.dataSet = data;
        this.context = appliances;
    }

    @Override
    public SuggestionsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestions_item_layout, parent, false);

        SuggestionsAdapter.MyViewHolder myViewHolder = new SuggestionsAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(final SuggestionsAdapter.MyViewHolder holder, final int listPosition) {
        final TextView item_name = holder.item_name;
        TextView maker = holder.maker;
        final TextView power = holder.power;
        TextView time = holder.time;
        TextView total_units = holder.total_units;
        final TextView item_name2 = holder.item_name2;
        TextView maker2 = holder.maker2;
        final TextView power2 = holder.power2;
        TextView time2 = holder.time2;
        TextView total_units2 = holder.total_units2;
        ImageView delete = holder.delete;

        Log.i("dataSet","--->"+dataSet.get(listPosition).getTotal_units());

        item_name.setText(dataSet.get(listPosition).getItem_name());
        maker.setText(dataSet.get(listPosition).getItem_maker());
        power.setText(dataSet.get(listPosition).getWatts());
        time.setText(dataSet.get(listPosition).getUsage_hrs());
        double roundedNumber = Utility.DecimalUtils.round(Double.parseDouble(dataSet.get(listPosition).getTotal_units()), 2);
        total_units.setText(String.valueOf(roundedNumber));

        //Suggestions
        item_name2.setText(dataSet.get(listPosition).getItem_name());
        maker2.setText("LG");
        double temp_watt = Double.parseDouble(dataSet.get(listPosition).getWatts())-50 ;
        power2.setText(String.valueOf(temp_watt));
        time2.setText(dataSet.get(listPosition).getUsage_hrs());

        double current_units  = Double.parseDouble(power.getText().toString().trim())*Double.parseDouble(time.getText().toString().trim())/1000; //Currentc units
        double current_roundedNumber = Utility.DecimalUtils.round(current_units, 2);


        Log.i("temp_watt","--->"+temp_watt);
        double temp_units  = temp_watt*Double.parseDouble(time2.getText().toString().trim())/1000;
        double roundedNumber2 = Utility.DecimalUtils.round(temp_units, 2);
        total_units2.setText(String.valueOf(roundedNumber2));

        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(1000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        total_units2.startAnimation(animation);

        total_savings = total_savings +  roundedNumber2 ;
        total_consumption = total_consumption +  current_roundedNumber ;

        total_consumption = Utility.DecimalUtils.round(total_consumption, 2);
        total_savings = Utility.DecimalUtils.round(total_savings, 2);
        SuggestionActivity.saving_units.setText(String .valueOf(total_savings));
        SuggestionActivity.current_units.setText(String .valueOf(total_consumption));

        double final_savings = total_consumption-total_savings ;
        final_savings = Utility.DecimalUtils.round(final_savings, 2);
        SuggestionActivity.saving_perday_units.setText(String .valueOf(final_savings));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}


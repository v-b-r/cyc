package www.coral.innovations.cyc.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import www.coral.innovations.cyc.Model.BoardModel;
import www.coral.innovations.cyc.R;

public class BoardAdapter extends ArrayAdapter<BoardModel> {
    LayoutInflater inflater;
    ArrayList<BoardModel> objects;
    ViewHolder holder = null;

    public BoardAdapter(Context context, int textViewResourceId, ArrayList<BoardModel> objects) {
        super(context, textViewResourceId, objects);
        inflater = ((Activity) context).getLayoutInflater();
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        BoardModel model = objects.get(position);
        View row = convertView;
        if (null == row) {
            holder = new ViewHolder();
            row = inflater.inflate(R.layout.spinner_item_image_text, parent, false);
            holder.name = (TextView) row.findViewById(R.id.spinner_name);
            holder.img = (ImageView) row.findViewById(R.id.spinner_img);
            holder.img.setBackgroundResource(objects.get(position).getLogo());
            holder.name.setText(model.getName());
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        return row;
    }

    static class ViewHolder {
        TextView name;
        ImageView img;
    }
}


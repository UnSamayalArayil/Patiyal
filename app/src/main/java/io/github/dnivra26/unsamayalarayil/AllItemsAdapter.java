package io.github.dnivra26.unsamayalarayil;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AllItemsAdapter extends ArrayAdapter<Item> {
    Activity activity;
    List<Item> items;
    public AllItemsAdapter(Context context, List<Item> objects) {
        super(context, R.layout.individual_item, objects);
        this.items = objects;
        this.activity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater layoutInflater = activity.getLayoutInflater();
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.individual_item, parent, false);
        }
        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        TextView currentLevel = (TextView) convertView.findViewById(R.id.currentLevel);
        Item item = items.get(position);
        itemName.setText(item.item_name);
        currentLevel.setText(item.current_percentage + " %");
        double percent = Double.parseDouble(item.current_percentage);
        if(percent > 80){
           convertView.setBackgroundColor(activity.getResources().getColor(R.color.light_green));
        } else if(percent < 20){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.light_red));
        } else{
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.light_orange));
        }
        return convertView;
    }
}

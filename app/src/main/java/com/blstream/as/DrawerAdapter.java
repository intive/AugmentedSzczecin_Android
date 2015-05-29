package com.blstream.as;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    private Activity context;
    private View view;

    private int layout;
    private DrawerItem[] items;

    public DrawerAdapter(Context context, int resource, DrawerItem[] objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.layout = resource;
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = null;
        if (context != null) {
            inflater = context.getLayoutInflater();
        }

        view = null;
        if (inflater != null) {
            view = inflater.inflate(layout, parent, false);
            setItemIconAndText(position);
        }
        return view;
    }

    private void setItemIconAndText(int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.itemIcon);
        TextView textView = (TextView) view.findViewById(R.id.itemName);

        DrawerItem chosenItem = items[position];
        if (chosenItem != null) {
            imageView.setImageResource(chosenItem.getIcon());
            textView.setText(chosenItem.getName());
        }
    }
}

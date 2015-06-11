package com.blstream.as;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blstream.as.data.rest.model.enums.SubCategory;

import java.util.List;

/**
 * Created by Damian on 2015-06-06.
 */
public class SubcategoryFilterAdapter extends ArrayAdapter<SubCategory> {
    private List<Integer> selectedItems;
    private Context context;

    public SubcategoryFilterAdapter(Context context, int resource, SubCategory[] objects, List<Integer> selectedItems) {
        super(context, resource, objects);
        this.context = context;
        this.selectedItems = selectedItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubCategory subCategory = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.filter_popup_menu_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.filter_text);
        textView.setText(context.getResources().getString(subCategory.getIdResource()));
        if(selectedItems.contains(position)) {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        }
        return rowView;
    }
}
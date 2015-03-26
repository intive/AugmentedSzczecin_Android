package com.blstream.as.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blstream.as.R;
import com.blstream.as.rest.model.POI;

import java.util.List;

/**
 * Created by Rafal Soudani on 2015-03-25.
 */
public class PoiListAdapter extends ArrayAdapter<POI> {

    public PoiListAdapter(Context context, List<POI> items) {
        super(context, R.layout.poi_listview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.poi_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.poiName = (TextView) convertView.findViewById(R.id.poiName);
            viewHolder.poiDescription = (TextView) convertView.findViewById(R.id.poiDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        POI poi = getItem(position);
        viewHolder.poiName.setText(poi.getName());
        viewHolder.poiDescription.setText(poi.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        TextView poiName;
        TextView poiDescription;
    }
}


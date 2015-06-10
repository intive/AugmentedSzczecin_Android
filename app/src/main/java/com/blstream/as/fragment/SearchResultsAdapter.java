package com.blstream.as.fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blstream.as.R;
import com.blstream.as.data.rest.model.SearchResult;

import java.util.ArrayList;

public class SearchResultsAdapter extends ArrayAdapter<SearchResult>  {
    private ArrayList<SearchResult> searchResults;

    static class ViewHolder {
        public TextView nameTextView;
        public TextView categoryTextView;
        public TextView latitudeTextView;
        public TextView longitudeTextView;
    }

    public SearchResultsAdapter(Context context, ArrayList<SearchResult> searchResults){
        super(context, R.layout.poi_listview_item, searchResults);
        this.searchResults = searchResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.poi_listview_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.poiName);
            viewHolder.categoryTextView = (TextView)convertView.findViewById(R.id.poiCategory);
            viewHolder.latitudeTextView = (TextView)convertView.findViewById(R.id.poiLatitude);
            viewHolder.longitudeTextView = (TextView)convertView.findViewById(R.id.poiLongitude);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.nameTextView.setText(searchResults.get(position).getName());
        viewHolder.categoryTextView.setText(searchResults.get(position).getCategory());
        viewHolder.latitudeTextView.setText(String.valueOf(searchResults.get(position).getLocation().getLatitude()));
        viewHolder.longitudeTextView.setText(String.valueOf(searchResults.get(position).getLocation().getLongitude()));

        return convertView;
    }
}

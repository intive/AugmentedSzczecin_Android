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

public class SearchResultsAdapter extends ArrayAdapter<SearchResult> {
    private ArrayList<SearchResult> searchResults;

    public SearchResultsAdapter(Context context, ArrayList<SearchResult> searchResults){
        super(context, R.layout.poi_listview_item, searchResults);
        this.searchResults = searchResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(this.getContext())
                .inflate(R.layout.poi_listview_item, parent, false);

        TextView nameTextView = (TextView) rowView.findViewById(R.id.poiName);
        TextView categoryTextView = (TextView) rowView.findViewById(R.id.poiCategory);
        TextView latitudeTextView = (TextView) rowView.findViewById(R.id.poiLatitude);
        TextView longitudeTextView = (TextView) rowView.findViewById(R.id.poiLongitude);

        nameTextView.setText(searchResults.get(position).getName());
        categoryTextView.setText(searchResults.get(position).getCategory());
        latitudeTextView.setText(String.valueOf(searchResults.get(position).getLocation().getLatitude()));
        longitudeTextView.setText(String.valueOf(searchResults.get(position).getLocation().getLongitude()));

        return rowView;
    }
}

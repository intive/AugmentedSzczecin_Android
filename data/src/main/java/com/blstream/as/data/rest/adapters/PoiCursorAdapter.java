package com.blstream.as.data.rest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.blstream.as.data.R;
import com.blstream.as.data.rest.model.Address;
import com.blstream.as.data.rest.model.Location;
import com.blstream.as.data.rest.model.Poi;

/**
 * Created by Rafal Soudani on 2015-05-28.
 */
public class PoiCursorAdapter extends CursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cursor;

    public PoiCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mContext = context;
        this.cursor = cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.poi_listview_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView poiNameTv = (TextView) view.findViewById(R.id.poiName);
        TextView poiCategoryTv = (TextView) view.findViewById(R.id.poiCategory);
        TextView poiLatitudeTv = (TextView) view.findViewById(R.id.poiLatitude);
        TextView poiLongitudeTv = (TextView) view.findViewById(R.id.poiLongitude);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(Poi.NAME));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(Poi.CATEGORY));
        String locationId = cursor.getString(cursor.getColumnIndexOrThrow(Poi.LOCATION_ID));
        String addressId = cursor.getString(cursor.getColumnIndexOrThrow(Poi.ADDRESS_ID));

        Location location = Location.getLocationFromId(locationId);
        Address address = Address.getAddressFromId(addressId);

        poiNameTv.setText(name);
        poiCategoryTv.setText(category);
        poiLatitudeTv.setText(String.valueOf(location.getLatitude()));
        poiLongitudeTv.setText(String.valueOf(location.getLongitude()));

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}

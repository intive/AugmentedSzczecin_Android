package com.blstream.as.data.rest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.blstream.as.data.R;
import com.blstream.as.data.rest.model.enums.Category;
import com.blstream.as.data.rest.model.Location;
import com.blstream.as.data.rest.model.Poi;

/**
 * Created by Rafal Soudani on 2015-05-28.
 */
public class PoiCursorAdapter extends CursorAdapter {

    public PoiCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
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
        String latitude = cursor.getString(cursor.getColumnIndexOrThrow(Location.LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndexOrThrow(Location.LONGITUDE));

        poiNameTv.setText(name);
        Category cat = Category.valueOf(category);
        poiCategoryTv.setText(context.getString(cat.getIdResource()));
        poiLatitudeTv.setText(latitude);
        poiLongitudeTv.setText(longitude);

    }
}

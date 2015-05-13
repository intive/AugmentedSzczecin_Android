package com.blstream.as.data.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.BuildConfig;
import com.blstream.as.data.R;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.service.Server;

/**
 *  Created by Rafal Soudani on 2015-03-24.
 */
public class PoiFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter simpleCursorAdapter;

    public static final String TAG = PoiFragment.class.getName();

    private OnPoiSelectedListener activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSimpleCursorAdapter();
        setListAdapter(simpleCursorAdapter);
        Server.getPoiList();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        getLoaderManager().initLoader(0, null, this);
    }

    public static PoiFragment newInstance() {
        return new PoiFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Poi.class, null),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }

    private void setSimpleCursorAdapter() {
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.poi_listview_item, null, new String[]{Poi.NAME, Poi.CATEGORY, Poi.LATITUDE, Poi.LONGITUDE},
                new int[]{R.id.poiName, R.id.poiCategory, R.id.poiLatitude, R.id.poiLongitude}, 0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = ((SimpleCursorAdapter) l.getAdapter()).getCursor();
        c.moveToPosition(position);
        String poiId = c.getString(c.getColumnIndex(Poi.POI_ID));
        activity.goToMarker(poiId);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (BuildConfig.DEBUG && (!(activity instanceof OnPoiSelectedListener)))
            throw new AssertionError("Activity: " + activity.getClass().getSimpleName() + " must implement OnPoiSelectedListener");
        this.activity = (OnPoiSelectedListener) activity;
    }

    public interface OnPoiSelectedListener {
        void goToMarker(String poiId);
    }
}

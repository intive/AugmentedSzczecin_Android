package com.blstream.as.data.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.activeandroid.Cache;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.blstream.as.data.BuildConfig;
import com.blstream.as.data.rest.adapters.PoiCursorAdapter;
import com.blstream.as.data.rest.model.Location;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.service.MyContentProvider;
import com.blstream.as.data.rest.service.Server;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public class PoiFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private PoiCursorAdapter poiCursorAdapter;

    public static final String TAG = PoiFragment.class.getName();
    private static final int URL_LOADER = 0;

    private OnPoiSelectedListener activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCursorAdapter();
        setListAdapter(poiCursorAdapter);
        Server.getPoiList();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    public static PoiFragment newInstance() {
        return new PoiFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MyContentProvider.createUri(Poi.class, null),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        poiCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        poiCursorAdapter.swapCursor(null);
    }

    private void setCursorAdapter() {
        From query = new Select(Poi.TABLE_NAME + ".*", Location.TABLE_NAME + ".*")
                .from(Poi.class).as(Poi.TABLE_NAME)
                .leftJoin(Location.class).as(Location.TABLE_NAME)
                .on(Poi.TABLE_NAME + "." + Poi.LOCATION_ID + " = " + Location.TABLE_NAME + "." + BaseColumns._ID);

        Cursor cursor = Cache.openDatabase().rawQuery(query.toSql(), query.getArguments());



        poiCursorAdapter = new PoiCursorAdapter(getActivity(), cursor);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = ((PoiCursorAdapter) l.getAdapter()).getCursor();
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

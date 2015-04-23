package com.blstream.as.data.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.R;
import com.blstream.as.data.listeners.EndlessScrollListener;
import com.blstream.as.data.rest.model.Endpoint;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.service.PoiApi;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *  Created by Rafal Soudani on 2015-03-24.
 */
public class PoiFragment extends ListFragment implements Endpoint, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FIRST_PAGE = 1;
    private static final boolean PAGINATION = false;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Callback<ArrayList<Poi>> poiCallback;
    private PoiApi poiApi;
    private RestAdapter restAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSimpleCursorAdapter();
        setListAdapter(simpleCursorAdapter);


        setRestAdapter();
        poiApi = restAdapter.create(PoiApi.class);
        poiCallback = new PoiCallback();
        poiApi.getPoiList(poiCallback);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        if (PAGINATION == true){
            getListView().setOnScrollListener(new EndlessScrollListener(this));
        }

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

    public void getPage(int page) {
        poiApi.getPoiList(poiCallback);
    }

    private void setSimpleCursorAdapter() {
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.poi_listview_item, null, new String[]{Poi.NAME, Poi.CATEGORY, Poi.DESCRIPTION},
                new int[]{R.id.poiName,R.id.poiCategory, R.id.poiDescription}, 0);
    }

    private void setRestAdapter() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    private class PoiCallback implements Callback<ArrayList<Poi>> {
        @Override
        public void success(ArrayList<Poi> p, Response response) {
            ActiveAndroid.beginTransaction();
            try {
                for (Poi poi : p) {
                    poi.setLongitudeAndLatitude();
                    poi.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.w(PoiFragment.class.getSimpleName(), "Retrofit fail: " + retrofitError.getMessage());
        }

    }
}

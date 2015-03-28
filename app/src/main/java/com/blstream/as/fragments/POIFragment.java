package com.blstream.as.fragments;

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
import com.blstream.as.R;
import com.blstream.as.listeners.EndlessScrollListener;
import com.blstream.as.rest.model.Endpoint;
import com.blstream.as.rest.model.POI;
import com.blstream.as.rest.model.Page;
import com.blstream.as.rest.service.POIApi;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public class POIFragment extends ListFragment implements Endpoint, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FIRST_PAGE = 1;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Callback callback;
    private POIApi poiApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.poi_listview_item, null, new String[]{POI.NAME, POI.DESCRIPTION},
                new int[]{R.id.poiName, R.id.poiDescription}, 0);
        setListAdapter(simpleCursorAdapter);


        RestAdapter restAdapter = setRestAdapter();
        poiApi = restAdapter.create(POIApi.class);
        callback = createCallback();
        poiApi.getPoiList(FIRST_PAGE, callback);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        getListView().setOnScrollListener(new EndlessScrollListener(this));
    }

    public void getPage(int page) {
        poiApi.getPoiList(page, callback);
    }

    private RestAdapter setRestAdapter() {

        return new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    public static POIFragment newInstance() {
        return new POIFragment();
    }

    private Callback createCallback() {
        return new Callback() {

            @Override
            public void success(Object o, Response response) {
                ActiveAndroid.beginTransaction();
                try {
                    for (POI poi : ((Page) o).getPois()) {
                        poi.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.w(POIFragment.class.getSimpleName(), "Retrofit fail: " + retrofitError.getMessage());
            }
        };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(POI.class, null),
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
}

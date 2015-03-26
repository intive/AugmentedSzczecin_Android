package com.blstream.as.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;

import com.activeandroid.query.Select;
import com.blstream.as.adapters.PoiListAdapter;
import com.blstream.as.listeners.EndlessScrollListener;
import com.blstream.as.rest.model.Endpoint;
import com.blstream.as.rest.model.POI;
import com.blstream.as.rest.model.Page;
import com.blstream.as.rest.service.POIApi;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public class POIFragment extends ListFragment implements Endpoint {

    private static final int FIRST_PAGE = 1;
    private final List<POI> pois = new ArrayList<>();
    private PoiListAdapter poiListAdapter;
    private Callback callback;
    private POIApi poiApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        poiListAdapter = new PoiListAdapter(getActivity(), pois);
        setListAdapter(poiListAdapter);


        RestAdapter restAdapter = setRestAdapter();
        poiApi = restAdapter.create(POIApi.class);
        callback = createCallback();
        poiApi.getPoiList(FIRST_PAGE, callback);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private Callback createCallback() {
        return new Callback() {

            @Override
            public void success(Object o, Response response) {
                for (POI poi : ((Page) o).getPois()) {
                    poi.save();
                }
                updatePoiList();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.w(POIFragment.class.getSimpleName(), "Retrofit fail: " + retrofitError.getMessage());
            }
        };
    }

    private void updatePoiList() {
        List<POI> queryResults = new Select().from(POI.class).execute();
        poiListAdapter.clear();
        poiListAdapter.addAll(queryResults);
        poiListAdapter.notifyDataSetChanged();
    }
}

package com.blstream.as.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.blstream.as.adapters.PoiListAdapter;
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
    private List<POI> pois = new ArrayList<>();
    private PoiListAdapter poiListAdapter;
    Callback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        poiListAdapter = new PoiListAdapter(getActivity(), pois);
        setListAdapter(poiListAdapter);

        RestAdapter restAdapter = setRestAdapter();
        POIApi poiApi = restAdapter.create(POIApi.class);
        callback = createCallback();
        poiApi.getPoiList(FIRST_PAGE, callback);


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
                for (POI p : ((Page) o).getPois()) {
                    pois.add(p);
                }
                poiListAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.w(POIFragment.class.getSimpleName(), "Retrofit fail: " + retrofitError.getMessage());
            }
        };
    }
}

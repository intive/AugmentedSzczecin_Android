package com.blstream.as;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.blstream.as.rest.model.Endpoint;
import com.blstream.as.rest.model.POI;
import com.blstream.as.rest.service.POIApi;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public class POIFragment extends ListFragment implements Endpoint {

    private List<POI> pois;
    private RestAdapter restAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restAdapter = setRestAdapter();
        POIApi poiApi = restAdapter.create(POIApi.class);


        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                for (POI p : (List<POI>) o) {
                    Log.d(POIFragment.class.getSimpleName().toString(), p.toString());
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.w(POIFragment.class.getSimpleName().toString(), "Retrofit fail: " + retrofitError.getMessage());
            }
        };


        poiApi.getPoiList(callback);


    }

    private RestAdapter setRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(BASEURL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }
}

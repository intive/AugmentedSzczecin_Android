package com.blstream.as.rest.service;

import com.blstream.as.rest.model.Endpoint;
import com.blstream.as.rest.model.POI;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public interface POIApi {

    @GET(Endpoint.POI_LIST)
    public void getPoi(@Path("id") int id, Callback<POI> callback);

    @GET(Endpoint.POI_LIST)
    public void getPoiList(Callback<List<POI>> callback);
}

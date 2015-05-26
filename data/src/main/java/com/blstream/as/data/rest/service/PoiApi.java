package com.blstream.as.data.rest.service;

import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.SimplePoi;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import static com.blstream.as.data.rest.model.Endpoint.POI_ADD;
import static com.blstream.as.data.rest.model.Endpoint.POI_LIST;
import static com.blstream.as.data.rest.model.Endpoint.POI_SINGLE;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public interface PoiApi {

    @GET(POI_LIST)
    void getPoiList(Callback<ArrayList<Poi>> callback);

    @POST(POI_ADD)
    void addPoi(@Body SimplePoi poi, Callback<Poi> callback);

    @DELETE(POI_SINGLE)
    void deletePoi(@Path("id") String id, Callback<Poi> poiCallback);

}

package com.blstream.as.data.rest.service;

import com.blstream.as.data.fragments.PoiFragment;
import com.blstream.as.data.rest.model.Endpoint;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.Page;
import com.blstream.as.data.rest.model.SimplePoi;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

import static com.blstream.as.data.rest.model.Endpoint.*;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public interface PoiApi{

    /**
     * @param id id of single poi to be returned
     */
    @GET(POI_SINGLE)
    public void getPoi(@Path("id") int id, Callback<Poi> callback);


    @GET(POI_LIST)
    public void getPoiList(Callback<ArrayList<Poi>> callback);

    @POST(POI_ADD)
    public void addPoi(@Body SimplePoi poi, Callback<Poi> callback);

}

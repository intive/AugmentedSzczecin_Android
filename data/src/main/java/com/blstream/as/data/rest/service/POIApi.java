package com.blstream.as.data.rest.service;

import com.blstream.as.data.rest.model.POI;
import com.blstream.as.data.rest.model.Page;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import static com.blstream.as.data.rest.model.Endpoint.POI_LIST;
import static com.blstream.as.data.rest.model.Endpoint.POI_SINGLE;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public interface POIApi {

    /**
     * @param id id of single poi to be returned
     */
    @GET(POI_SINGLE)
    public void getPoi(@Path("id") int id, Callback<POI> callback);

    /**
     * @param pageNumber Page Number with poi objects to be returned from the server
     */
    @GET(POI_LIST)
    public void getPoiList(@Path("page") int pageNumber, Callback<Page> callback);

}

package com.blstream.as.data.rest.service;

import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.SearchResults;
import com.blstream.as.data.rest.model.SimplePoi;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import static com.blstream.as.data.rest.model.Endpoint.COMMERCIAL_ADD;
import static com.blstream.as.data.rest.model.Endpoint.COMMERCIAL_LIST;
import static com.blstream.as.data.rest.model.Endpoint.COMMERCIAL_SINGLE;
import static com.blstream.as.data.rest.model.Endpoint.EVENT_ADD;
import static com.blstream.as.data.rest.model.Endpoint.EVENT_LIST;
import static com.blstream.as.data.rest.model.Endpoint.EVENT_SINGLE;
import static com.blstream.as.data.rest.model.Endpoint.PERSON_ADD;
import static com.blstream.as.data.rest.model.Endpoint.PERSON_LIST;
import static com.blstream.as.data.rest.model.Endpoint.PERSON_SINGLE;
import static com.blstream.as.data.rest.model.Endpoint.PLACE_ADD;
import static com.blstream.as.data.rest.model.Endpoint.PLACE_LIST;
import static com.blstream.as.data.rest.model.Endpoint.PLACE_SINGLE;
import static com.blstream.as.data.rest.model.Endpoint.POI_SEARCH;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public interface PoiApi {

    // ** PACES **//

    @GET(PLACE_LIST)
    void getPlacesList(Callback<ArrayList<Poi>> callback);

    @POST(PLACE_ADD)
    void addPlace(@Body SimplePoi poi, Callback<Poi> callback);

    @DELETE(PLACE_SINGLE)
    void deletePlace(@Path("id") String id, Callback<Poi> poiCallback);

    // ** EVENTS ** //

    @GET(EVENT_LIST)
    void getEventsList(Callback<ArrayList<Poi>> callback);

    @POST(EVENT_ADD)
    void addEvent(@Body SimplePoi poi, Callback<Poi> callback);

    @DELETE(EVENT_SINGLE)
    void deleteEvent(@Path("id") String id, Callback<Poi> poiCallback);

    // ** PEOPLE ** //

    @GET(PERSON_LIST)
    void getPesronsList(Callback<ArrayList<Poi>> callback);

    @POST(PERSON_ADD)
    void addPerson(@Body SimplePoi poi, Callback<Poi> callback);

    @DELETE(PERSON_SINGLE)
    void deletePerson(@Path("id") String id, Callback<Poi> poiCallback);

    // ** COMMERCIAL ** //

    @GET(COMMERCIAL_LIST)
    void getCommercialList(Callback<ArrayList<Poi>> callback);

    @POST(COMMERCIAL_ADD)
    void addCommercial(@Body SimplePoi poi, Callback<Poi> callback);

    @DELETE(COMMERCIAL_SINGLE)
    void deleteCommercial(@Path("id") String id, Callback<Poi> poiCallback);

    // ** SEARCH ** //
    @GET(POI_SEARCH)
    void search(@Query("lg") String lg, @Query("lt") String lt, @Query("radius") String radius,
                @Query("cat") String cat, @Query("name") String name,
                @Query("tag") List<String> tag, @Query("street") String street,
                @Query("subcat") String subcat, @Query("open") Boolean open,
                @Query("paid") Boolean paid, Callback<SearchResults> callback);
}

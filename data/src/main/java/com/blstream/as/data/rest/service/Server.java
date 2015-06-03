package com.blstream.as.data.rest.service;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.blstream.as.data.rest.model.Endpoint;
import com.blstream.as.data.rest.model.Category;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.SimplePoi;
import com.blstream.as.data.rest.model.User;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rafał Soudani on 2015-04-25.
 * <p/>
 * Class to communicate with server
 */
public final class Server implements Endpoint {
    private static Callback<ArrayList<Poi>> poiListCallback;
    private static PoiApi poiApi;
    private static RestAdapter restAdapter;

    static {
        setRestAdapter();
        poiApi = restAdapter.create(PoiApi.class);
        poiListCallback = new PoiListCallback();
    }

    private Server() {
    }

    public static void refreshPoiList() {
        refreshPlacesList();
        refreshEventsList();
        refreshPesronsList();
        //refreshCommercialList(); TODO: odkomentowac gdy commercial bedzie dzialac na serwerze
    }

    private static void refreshPlacesList() {
        poiApi.getPlacesList(poiListCallback);
    }

    private static void refreshEventsList() {
        poiApi.getEventsList(poiListCallback);
    }

    private static void refreshPesronsList() {
        poiApi.getPesronsList(poiListCallback);
    }

    private static void refreshCommercialList() {
        poiApi.getCommercialList(poiListCallback);
    }

    public static void addPoi(String name, Double latitude, Double longitude, Category category) {
        SimplePoi poi = new SimplePoi(name, latitude, longitude);
        switch (category){
            case PLACE:
                poiApi.addPlace(poi, new PoiCallback());
                break;
            case COMMERCIAL:
                poiApi.addCommercial(poi, new PoiCallback());
                break;
            case EVENT:
                poiApi.addEvent(poi, new PoiCallback());
                break;
            case PERSON:
                poiApi.addPerson(poi, new PoiCallback());
                break;
        }

    }

    public static void deletePoi(String poiId) {
        Poi poi = Poi.getPoiFromId(poiId);
        Category category = Category.valueOf(poi.getCategory());
        Log.w("AAAAAAASSSS", poi.getCategory()); //TODO: delete
        poi.delete();
        switch (category) {
            case PLACE:
                poiApi.deletePlace(poiId, new PoiCallback());
                break;
            case COMMERCIAL:
                poiApi.deleteCommercial(poiId, new PoiCallback());
                break;
            case EVENT:
                poiApi.deleteEvent(poiId, new PoiCallback());
                break;
            case PERSON:
                poiApi.deletePerson(poiId, new PoiCallback());
                break;
        }
    }

    private static class PoiCallback implements Callback<Poi> {
        @Override
        public void success(Poi poi, Response response) {
            refreshPoiList();
            //TODO: show success add poi message
        }

        @Override
        public void failure(RetrofitError error) {
            //TODO: show fail add poi message
        }
    }

    private static class PoiListCallback implements Callback<ArrayList<Poi>> {
        @Override
        public void success(ArrayList<Poi> p, Response response) {
            ActiveAndroid.beginTransaction();
            try {
                for (Poi poi : p) {
                    poi = poi.bindIdWithDatabase();
                    poi.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Log.w(Server.class.getSimpleName(), "Retrofit fail: " + retrofitError.getMessage());
        }

    }

    private static void setRestAdapter() {
        User user = new User();  //TODO: change to actual user when login on server will work
        user.setUsername("asd");
        user.setPassword("zxc");

        ApiRequestInterceptor requestInterceptor = new ApiRequestInterceptor();
        requestInterceptor.setUser(user);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }
}

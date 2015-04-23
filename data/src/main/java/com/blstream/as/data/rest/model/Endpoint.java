package com.blstream.as.data.rest.model;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public abstract interface Endpoint {
    public static final String BASE_URL = "http://78.133.154.62:1080/";
    public static final String POI_SINGLE = "/places/{id}";
    public static final String POI_LIST = "/places";
}

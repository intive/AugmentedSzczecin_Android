package com.blstream.as.data.rest.model;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public abstract interface Endpoint {
    public static final String BASE_URL = "http://asia1234.cba.pl/patronat";
    public static final String POI_SINGLE = "/poi/{id}";
    public static final String POI_LIST = "/poi/page/{page}";
}

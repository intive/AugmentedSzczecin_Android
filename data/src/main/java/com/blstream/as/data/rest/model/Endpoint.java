package com.blstream.as.data.rest.model;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public abstract interface Endpoint {
    public static final String BASE_URL = "http://78.133.154.62:1080/";

    public static final String PLACE_SINGLE = "/places/{id}";
    public static final String PLACE_LIST = "/places";
    public static final String PLACE_ADD = "/places";

    public static final String EVENT_SINGLE = "/events/{id}";
    public static final String EVENT_LIST = "/events";
    public static final String EVENT_ADD = "/events";

    public static final String PERSON_SINGLE = "/people/{id}";
    public static final String PERSON_LIST = "/people";
    public static final String PERSON_ADD = "/people";

    public static final String COMMERCIAL_SINGLE = "/commercial/{id}";
    public static final String COMMERCIAL_LIST = "/commercial";
    public static final String COMMERCIAL_ADD = "/commercial";
}

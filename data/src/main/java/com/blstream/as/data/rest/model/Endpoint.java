package com.blstream.as.data.rest.model;

/**
 * Created by Rafal Soudani on 2015-03-24.
 */
public interface Endpoint {
    String BASE_URL = "http://78.133.154.62:1080/";

    String PLACE_SINGLE = "/places/{id}";
    String PLACE_LIST = "/places";
    String PLACE_ADD = "/places";

    String EVENT_SINGLE = "/events/{id}";
    String EVENT_LIST = "/events";
    String EVENT_ADD = "/events";

    String PERSON_SINGLE = "/people/{id}";
    String PERSON_LIST = "/people";
    String PERSON_ADD = "/people";

    String COMMERCIAL_SINGLE = "/commercials/{id}";
    String COMMERCIAL_LIST = "/commercials";
    String COMMERCIAL_ADD = "/commercials";

    String POI_SEARCH = "/q";
}

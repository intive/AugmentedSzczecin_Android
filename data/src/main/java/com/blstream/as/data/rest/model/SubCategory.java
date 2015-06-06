package com.blstream.as.data.rest.model;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum SubCategory {
    SCHOOL(R.string.school,R.string.ser_school),
    HOSPITAL(R.string.hospital,R.string.ser_hospital),
    PARK(R.string.park,R.string.ser_park),
    MONUMENT(R.string.monument,R.string.ser_monument),
    MUSEUM(R.string.museum,R.string.ser_museum),
    OFFICE(R.string.office,R.string.ser_office),
    BUS_STATION(R.string.bus_station,R.string.ser_bus_station),
    TRAIN_STATION(R.string.train_station,R.string.ser_train_station),
    POST_OFFICE(R.string.post_office,R.string.ser_post_office),
    CHURCH(R.string.church,R.string.ser_church);

    private int idResource;
    private int idServerResource;

    SubCategory(int idResource,int idServerResource) {
        this.idResource = idResource;
        this.idServerResource = idServerResource;
    }

    public int getIdResource() {
        return idResource;
    }
    public int getIdServerResource() {
        return idServerResource;
    }
}

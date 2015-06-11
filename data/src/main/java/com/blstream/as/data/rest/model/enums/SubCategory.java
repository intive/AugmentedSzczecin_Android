package com.blstream.as.data.rest.model.enums;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum SubCategory implements EnumWithoutName{
    SCHOOL(R.string.school),
    HOSPITAL(R.string.hospital),
    PARK(R.string.park),
    MONUMENT(R.string.monument),
    MUSEUM(R.string.museum),
    OFFICE(R.string.office),
    BUS_STATION(R.string.bus_station),
    TRAIN_STATION(R.string.train_station),
    POST_OFFICE(R.string.post_office),
    CHURCH(R.string.church);

    private int idResource;

    SubCategory(int idResource) {
        this.idResource = idResource;
    }

    @Override
    public int getIdResource() {
        return idResource;
    }
}

package com.blstream.as.data.rest.model.enums;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum SubCategory implements EnumWithoutName{
    SCHOOL(R.string.school,R.drawable.school),
    HOSPITAL(R.string.hospital,R.drawable.hospital),
    PARK(R.string.park,R.drawable.park),
    MONUMENT(R.string.monument,R.drawable.monument),
    MUSEUM(R.string.museum,R.drawable.museum),
    OFFICE(R.string.office,R.drawable.office),
    BUS_STATION(R.string.bus_station,R.drawable.bus_station),
    TRAIN_STATION(R.string.train_station,R.drawable.train_station),
    POST_OFFICE(R.string.post_office,R.drawable.post_office),
    CHURCH(R.string.church,R.drawable.church);

    private int idStringResource;
    private int idDrawableResource;

    SubCategory(int idStringResource, int idDrawableResource) {
        this.idStringResource = idStringResource;
        this.idDrawableResource = idDrawableResource;
    }

    @Override
    public int getIdStringResource() {
        return idStringResource;
    }

    public int getIdDrawableResource() {
        return idDrawableResource;
    }
}

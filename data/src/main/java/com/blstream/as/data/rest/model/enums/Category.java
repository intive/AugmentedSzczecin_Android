package com.blstream.as.data.rest.model.enums;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum Category implements EnumWithoutName{
    PLACE(R.string.public_places,R.drawable.place),
    COMMERCIAL(R.string.companies_and_services, R.drawable.commercial),
    EVENT(R.string.events,R.drawable.event),
    PERSON(R.string.friends,R.drawable.person);

    private int idStringResource;
    private int idDrawableResource;

    Category(int idStringResource,int idDrawableResource) {
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

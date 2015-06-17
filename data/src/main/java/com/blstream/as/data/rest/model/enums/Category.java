package com.blstream.as.data.rest.model.enums;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum Category implements EnumWithoutName{
    PLACE(R.string.public_places,R.drawable.miejsca_publiczne),
    COMMERCIAL(R.string.companies_and_services, R.drawable.firmy_i_uslugi),
    EVENT(R.string.events,R.drawable.wydarzenia),
    PERSON(R.string.friends,R.drawable.ulubione);

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

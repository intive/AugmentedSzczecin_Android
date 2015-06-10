package com.blstream.as.data.rest.model;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum Category {
    PLACE(R.string.public_places,R.drawable.miejsca_publiczne),
    //COMMERCIAL(R.string.companies_and_services,R.drawable.firmy_i_uslugi), TODO: odkomentowac gdy commercial bedzie dzialac na serwerze
    EVENT(R.string.events,R.drawable.wydarzenia),
    PERSON(R.string.friends,R.drawable.ulubione);

    private int idStringResource;
    private int idDrawableResource;

    Category(int idStringResource,int idDrawableResource) {
        this.idStringResource = idStringResource;
        this.idDrawableResource = idDrawableResource;
    }

    public int getIdStringResource() {
        return idStringResource;
    }

    public int getIdDrawableResource() {
        return idDrawableResource;
    }
}

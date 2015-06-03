package com.blstream.as.data.rest.model;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum Category {
    PLACE(R.string.public_places), COMMERCIAL(R.string.companies_and_services), EVENT(R.string.events), PERSON(R.string.friends);

    private int descriptionId;

    Category(int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }
}

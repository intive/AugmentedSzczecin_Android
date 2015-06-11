package com.blstream.as.data.rest.model.enums;

import com.blstream.as.data.R;

/**
 * Created by Rafal Soudani on 2015-06-10.
 */
public enum Price implements EnumWithoutName{
    UNASSIGNED(R.string.unnasigned_price), PAID(R.string.paid), FREE(R.string.free);

    private int idResource;

    Price(int idResource) {
        this.idResource = idResource;
    }

    @Override
    public int getIdResource() {
        return idResource;
    }
}

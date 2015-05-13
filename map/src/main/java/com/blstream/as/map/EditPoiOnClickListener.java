package com.blstream.as.map;

import android.view.View;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Rafal Soudani on 2015-05-13.
 */
public class EditPoiOnClickListener implements View.OnClickListener {

    Marker marker;
    MapsFragment.Callbacks activityConnector;
    boolean poiAddingMode;

    public EditPoiOnClickListener(Marker marker, boolean poiAddingMode) {
        this.marker = marker;
        this.poiAddingMode = poiAddingMode;
    }

    public EditPoiOnClickListener(Marker marker, boolean poiAddingMode, MapsFragment.Callbacks activityConnector) {
        this(marker, poiAddingMode);
        this.activityConnector = activityConnector;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.editPoiButton) { //cant use switch because from ADT 14 the final modifier was removed from id's in the R class. http://tools.android.com/tips/non-constant-fields
            activityConnector.showEditPoiWindow(marker);
        } else if (id == R.id.deletePoiButton) {
            activityConnector.confirmDeletePoi(marker);
        }
    }
}

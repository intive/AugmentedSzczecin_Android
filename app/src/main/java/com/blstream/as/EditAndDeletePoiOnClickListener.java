package com.blstream.as;

import android.view.View;

import com.blstream.as.fragment.PreviewPoiFragment;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Rafal Soudani on 2015-05-13.
 */
public class EditAndDeletePoiOnClickListener implements View.OnClickListener {

    String poiId;
    PreviewPoiFragment.Callbacks activityConnector;
    boolean poiAddingMode;

    public EditAndDeletePoiOnClickListener(String poiId, boolean poiAddingMode) {
        this.poiId = poiId;
        this.poiAddingMode = poiAddingMode;
    }

    public EditAndDeletePoiOnClickListener(String poiId, boolean poiAddingMode, PreviewPoiFragment.Callbacks activityConnector) {
        this(poiId, poiAddingMode);
        this.activityConnector = activityConnector;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.editPoiButton) { //cant use switch because from ADT 14 the final modifier was removed from id's in the R class. http://tools.android.com/tips/non-constant-fields
            activityConnector.showEditPoiWindow(MapsFragment.getMarkerFromPoiId(poiId));
        } else if (id == R.id.deletePoiButton) {
            activityConnector.confirmDeletePoi(poiId);
        }
    }
}

package com.blstream.as.maps2d;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Konrad on 2015-04-29.
 */
public class ClusterItem implements com.google.maps.android.clustering.ClusterItem {

    private final LatLng clusterPosition;

    public ClusterItem(double lat, double lng) {
        clusterPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return clusterPosition;
    }
}

package com.blstream.as.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Konrad on 2015-05-27.
 */
public class ClusterItem implements com.google.maps.android.clustering.ClusterItem {

    private final String clusterItemName;
    private final String clusterItemCategory;

    private  LatLng clusterItemPosition;

    public ClusterItem(double lat, double lng, String name, String category) {
        clusterItemPosition = new LatLng(lat, lng);
        clusterItemCategory = category;
        clusterItemName = name;
    }

    @Override
    public LatLng getPosition() {
        return clusterItemPosition;
    }
    public void setPosition(LatLng position) {
        this.clusterItemPosition = position;
    }
    public String getClusterItemCategory() {
        return clusterItemCategory;
    }

    public String getTitle() {
        return clusterItemName;
    }
}

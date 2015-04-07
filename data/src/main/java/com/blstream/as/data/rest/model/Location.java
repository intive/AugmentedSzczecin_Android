package com.blstream.as.data.rest.model;


/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public class Location {

    private float latitude;
    private float longitude;

    /**
     * @return The latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("latitude:%s, longitude:%s", latitude, longitude);
    }
}

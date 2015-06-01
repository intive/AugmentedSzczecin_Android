package com.blstream.as.data.rest.model;


import com.google.gson.annotations.Expose;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
public class Location {

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    public Location() {
        this(0,0);
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return The latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("latitude:%s, longitude:%s", latitude, longitude);
    }
}

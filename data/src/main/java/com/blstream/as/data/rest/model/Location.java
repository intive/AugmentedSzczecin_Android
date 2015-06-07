package com.blstream.as.data.rest.model;


import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
@Table(name = Location.TABLE_NAME, id = BaseColumns._ID)
public class Location extends Model {

    public static final String TABLE_NAME = "Locations";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";

    @Expose
    @Column(name = LATITUDE)
    private double latitude;

    @Expose
    @Column(name = LONGITUDE)
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

    public static Location getLocationFromId(String locationId) {
        return new Select().from(Location.class).where(BaseColumns._ID + " = ?", locationId).executeSingle();
    }
}

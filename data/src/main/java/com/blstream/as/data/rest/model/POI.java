package com.blstream.as.data.rest.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
@Table(name = "Pois", id = BaseColumns._ID)
public class POI extends Model {
    public static final String POI_ID = "PoiId";
    public static final String NAME = "Name";
    public static final String CATEGORY = "Category";
    public static final String DESCRIPTION = "Description";
    public static final String LOCATION = "Location";
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";

    @Column(name = POI_ID)
    @SerializedName("id")
    private int poiId;

    @Column(name = NAME)
    private String name;

    @Column(name = CATEGORY)
    private String category;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = LOCATION)
    private Location location;

    @Column(name = LONGITUDE)
    private String longitude;

    @Column(name = LATITUDE)
    private String latitude;

    /**
     * @return The id
     */
    public int getPoiId() {
        return poiId;
    }

    /**
     * @param poiId The id
     */
    public void setId(int poiId) {
        this.poiId = poiId;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLongitudeAndLatitude() {
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
    }

    @Override
    public String toString() {
        return String.format("Poi id:%d, name:%s, type:%s, description:%s, location:%s", poiId, name, category, description, location);
    }
}

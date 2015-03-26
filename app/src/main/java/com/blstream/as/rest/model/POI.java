package com.blstream.as.rest.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
@Table(name = "Pois")
public class POI extends Model {

    @Column(name = "PoiId")
    @SerializedName("id")
    private int poiId;

    @Column(name = "Name")
    private String name;

    @Column(name = "Type")
    private String type;

    @Column(name = "Description")
    private String description;

    @Column(name = "Location")
    private Location location;

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
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return String.format("POI id:%d, name:%s, type:%s, description:%s, location:%s", poiId, name, type, description, location);
    }
}

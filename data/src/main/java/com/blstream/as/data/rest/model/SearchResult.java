package com.blstream.as.data.rest.model;


import com.google.gson.annotations.SerializedName;


public class SearchResult {
    @SerializedName("id")
    private String poiId;
    private String name;
    private Location location;
    private String category;

    public String getPoiId() {
        return poiId;
    }

    public void setId(String poiId) {
        this.poiId = poiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

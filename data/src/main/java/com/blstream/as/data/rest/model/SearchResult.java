package com.blstream.as.data.rest.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResult {
    @SerializedName("id")
    private String poiId;
    private String name;
    private String description;
    private Location location;
    private Address address;
    private ArrayList<String> tags;
    private String www;
    private String phone;
    private String media;
    private String wiki;
    private String subcategory;
    private ArrayList<Opening> opening;
    private Boolean paid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public ArrayList<Opening> getOpening() {
        return opening;
    }

    public void setOpening(ArrayList<Opening> opening) {
        this.opening = opening;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

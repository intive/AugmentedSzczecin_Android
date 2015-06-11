package com.blstream.as.ar;


public class PointOfInterest {
    private String id;
    private String name;
    private String categoryName;
    private String subcategoryName;
    private double latitude;
    private double longitude;

    public PointOfInterest(String id, String name, String categoryName, String subcategoryName, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.subcategoryName = subcategoryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subcategoryName;
    }

    public void setSubCategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }
}

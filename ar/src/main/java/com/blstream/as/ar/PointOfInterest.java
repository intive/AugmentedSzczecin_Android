package com.blstream.as.ar;


public class PointOfInterest {
    private String id;
    private String name;
    private String categoryName;
    private int imageResId;
    private double latitude;
    private double longitude;

    public PointOfInterest(String id, String name, String categoryName, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
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
}

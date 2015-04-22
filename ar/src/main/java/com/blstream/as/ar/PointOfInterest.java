package com.blstream.as.ar;


public class PointOfInterest {
    private int id;
    private String name;
    private String type;
    private String description;
    private int imageResId;
    private double latitude;
    private double longitude;

    public PointOfInterest(int id, String name, String type, String description, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
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
}

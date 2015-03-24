package com.blstream.as;

public class PoiInfo {
    private int categoryName;
    private String name;
    private double length;
    private double latitude;

    public PoiInfo(int categoryName, String name, double length, double latitude) {
        this.categoryName = categoryName;
        this.name = name;
        this.length = length;
        this.latitude = latitude;
    }

    public int getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(int categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }


}

package com.blstream.as.data.rest.model;

/**
 * Created by Rafał Soudani on 2015-04-25.
 */
public class SimplePoi{
    String name;
    private SimpleLocation location;

    public SimplePoi(String name, Double latitude, Double longitude) {
        this.name = name;
        location = new SimpleLocation(latitude, longitude);
    }

    private class SimpleLocation{
        double latitude;
        double longitude;

        SimpleLocation(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

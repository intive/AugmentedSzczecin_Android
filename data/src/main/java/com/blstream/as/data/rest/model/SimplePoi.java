package com.blstream.as.data.rest.model;

/**
 * Created by Rafał Soudani on 2015-04-25.
 */
public class SimplePoi {
    String name, description;
    String[] tags;
    String subcategory;
    private SimpleLocation location;
    private SimpleAddress address;

    public SimplePoi(String name, String description, String street, String postalCode, String city, String streetNumber, String houseNumber, String[] tags, Double latitude, Double longitude, SubCategory subCategory) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        location = new SimpleLocation(latitude, longitude);
        address = new SimpleAddress(city, street, streetNumber, houseNumber, postalCode);
        if (subCategory != null) {
            this.subcategory = subCategory.toString();
        }
    }

    public SimplePoi(String name, String description, String street, String postalCode, String city, String streetNumber, String houseNumber, String[] tags, Double latitude, Double longitude) {
        this(name, description, street, postalCode, city, streetNumber, houseNumber, tags, latitude, longitude, null);
    }

    private class SimpleLocation {
        double latitude;
        double longitude;

        SimpleLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private class SimpleAddress {
        String street, city, streetNumber, zipcode;
        String houseNumber;

        public SimpleAddress(String city, String street, String streetNumber, String houseNumber, String zipcode) {
            this.street = street;
            this.zipcode = zipcode;
            this.city = city;
            this.streetNumber = streetNumber;
            this.houseNumber = houseNumber;
        }
    }
}

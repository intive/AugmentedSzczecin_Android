package com.blstream.as.data.rest.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Damian on 2015-05-27.
 */
public class Address {
    @Expose
    private String city;

    @Expose
    private String street;

    @Expose
    private String streetNumber;

    @Expose
    private String zipcode;

    public Address() {
        this("","","","");
    }

    public Address(String city, String street, String streetNumber, String zipcode) {
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return String.format("city:%s, street:%s, street_number:%s, zipcode:%s",city,street,streetNumber,zipcode);
    }
}

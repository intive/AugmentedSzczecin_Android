package com.blstream.as.data.rest.model.simpleModel;

/**
 * Created by Rafal Soudani on 2015-06-10.
 */
public class SimpleAddress {
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

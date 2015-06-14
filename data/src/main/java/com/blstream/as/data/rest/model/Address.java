package com.blstream.as.data.rest.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

/**
 * Created by Rafal Soudani on 2015-05-28.
 */
@Table(name = Address.TABLE_NAME, id = BaseColumns._ID)
public class Address extends Model {

    public static final String TABLE_NAME = "Addresses";
    public static final String CITY = "City";
    public static final String STREET = "Street";
    public static final String ZIPCODE= "Zipcode";
    public static final String STREET_NUMBER = "StreetNumber";
    public static final String HOUSE_NUMBER = "HouseNumber";

    @Expose
    @Column(name = CITY)
    private String city;
    @Expose
    @Column(name = STREET)
    private String street;
    @Expose
    @Column(name = ZIPCODE)
    private String zipcode;
    @Expose
    @Column(name = STREET_NUMBER)
    private String streetNumber;
    @Expose
    @Column(name = HOUSE_NUMBER)
    private String houseNumber;

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street The street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return The zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * @param zipcode The zipcode
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * @return The streetNumber
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * @param streetNumber The streetNumber
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public static Address getAddressFromId(String addressId) {
        return new Select().from(Address.class).where(BaseColumns._ID + " = ?", addressId).executeSingle();
    }

    /**
     *
     * @return
     * The houseNumber
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     *
     * @param houseNumber
     * The houseNumber
     */
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }


}

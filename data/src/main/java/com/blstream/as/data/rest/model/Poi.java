package com.blstream.as.data.rest.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
@Table(name = "Pois", id = BaseColumns._ID)
public class Poi extends Model {
    public static final String POI_ID = "PoiId";
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String CATEGORY = "Category";
    public static final String LOCATION = "Location";
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";

    public static final String ADDRESS = "Address";
    public static final String CITY = "City";
    public static final String STREET = "Street";
    public static final String STREET_NUMBER = "StreetNumber";
    public static final String ZIPCODE = "Zipcode";
    /*
    public static final String TAGS = "Tags";
    public static final String WWW = "Www";
    public static final String PHONE = "Phone";
    public static final String MEDIA = "Media";
    public static final String OPENING = "Opening";
    public static final String DAY = "Day";
    public static final String OPEN = "Open";
    public static final String CLOSE = "Close";
    public static final String SUBCATEGORY = "Subcategory";
    */

    @Column(name = POI_ID, unique = true)
    @SerializedName("id")
    private String poiId;

    @Column(name = NAME)
    private String name;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = CATEGORY)
    private String category;

    @Column(name = LOCATION)
    private Location location;

    @Column(name = LONGITUDE)
    private String longitude;

    @Column(name = LATITUDE)
    private String latitude;

    @Column(name = ADDRESS)
    private Address address;

    @Column(name = CITY)
    private String city;

    @Column(name = STREET)
    private String street;

    @Column(name = STREET_NUMBER)
    private String streetNumber;

    @Column(name = ZIPCODE)
    private String zipcode;

    /*

    @Column(name = TAGS)
    private String tags;

    @Column(name = WWW)
    private String www;

    @Column(name = PHONE)
    private String phone;

    @Column(name = MEDIA)
    private String media;

    @Column(name = OPENING)
    private Opening opening;

    @Column(name = DAY)
    private String day;

    @Column(name = OPEN)
    private String open;

    @Column(name = CLOSE)
    private String close;

    @Column(name = SUBCATEGORY)
    private String subcategory;
    */
    /**
     * @return The id
     */
    public String getPoiId() {
        return poiId;
    }

    /**
     * @param poiId The id
     */
    public void setId(String poiId) {
        this.poiId = poiId;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLongitudeAndLatitude() {
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The description
     */

    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    public void setFullAddress() {
        if(address == null) {
            address = new Address();
        }
        this.city = address.getCity();
        this.street = address.getStreet();
        this.streetNumber = address.getStreetNumber();
        this.zipcode = address.getZipcode();
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
/*
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
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

    public Opening getOpening() {
        return opening;
    }

    public void setOpening(Opening opening) {
        this.opening = opening;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
*/
    public Poi bindIdWithDatabase() {
        Poi tempPoi = new Select().from(Poi.class).where(POI_ID + " = ?",  this.getPoiId() ).executeSingle();
        if (tempPoi != null) {
            tempPoi.location = new Location(Double.parseDouble(tempPoi.getLatitude()), Double.parseDouble(tempPoi.getLongitude()));
            tempPoi.address = new Address(tempPoi.city,tempPoi.street,tempPoi.streetNumber,tempPoi.zipcode);
            return tempPoi;
        } else {
            this.setLongitudeAndLatitude();
            this.setFullAddress();
            return this;
        }
    }

    @Override
    public String toString() {
        return String.format("Poi id:%s, name:%s, description:%s, category:%s, location:%s, address:%s", poiId, name, description, category, location.toString(),address.toString());
    }

    public static Poi getPoiFromId(String poiId) {
        return new Select().from(Poi.class).where(POI_ID + " = ?", poiId).executeSingle();
    }
}

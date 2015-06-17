package com.blstream.as.data.rest.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafal Soudani on 2015-03-23.
 */
@Table(name = Poi.TABLE_NAME, id = BaseColumns._ID)
public class Poi extends Model {
    public static final String TABLE_NAME = "Pois";
    public static final String POI_ID = "PoiId";
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String CATEGORY = "Category";
    public static final String SUB_CATEGORY = "SubCategory";
    public static final String LOCATION = "Location";
    public static final String LOCATION_ID = "LocationId";
    public static final String ADDRESS = "Address";
    public static final String ADDRESS_ID = "AddressId";

    public static final String OWNER = "Owner";
    public static final String TAGS = "Tags";
    public static final String WWW = "Www";
    public static final String PHONE = "Phone";
    public static final String OPENING = "Opening";
    public static final String WIKI = "Wiki";
    public static final String FANPAGE = "Fanpage";
    public static final String PAID = "Paid";


    @Column(name = POI_ID, unique = true)
    @SerializedName("id")
    private String poiId;

    @Column(name = NAME)
    private String name;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = CATEGORY)
    private String category;

    @Column(name = SUB_CATEGORY)
    private String subcategory;

    @Column(name = ADDRESS)
    private Address address;

    @Column(name = ADDRESS_ID)
    private Long addressId;

    @Column(name = LOCATION)
    private Location location;

    @Column(name = LOCATION_ID)
    private Long locationId;

    @Column(name = OWNER)
    private Owner owner;

    @Column(name = TAGS)
    private List<String> tags = new ArrayList<String>();

    @Column(name = WWW)
    private String www;

    @Column(name = PHONE)
    private String phone;

    @Column(name = OPENING)
    private List<Opening> opening = new ArrayList<Opening>();

    @Column(name = WIKI)
    private String wiki;

    @Column(name = FANPAGE)
    private String fanpage;

    @Column(name = PAID)
    private String paid;




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


    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
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
        location.save();
        this.locationId = location.getId();
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public List<Opening> getOpening() {
        return opening;
    }

    public void setOpening(List<Opening> opening) {
        this.opening = opening;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Poi bindIdWithDatabase() {
        if (null != (new Select().from(Poi.class).where(POI_ID + " = ?", this.getPoiId()).executeSingle())) {
            return new Select().from(Poi.class).where(POI_ID + " = ?", this.getPoiId()).executeSingle();
        } else {
            if (location != null) {
                location.save();
                setLocationId(location.getId());
            }

            if (address != null) {
                address.save();
                setAddressId(address.getId());
            }
            return this;
        }
    }

    public static Poi getPoiFromId(String poiId) {
        return new Select().from(Poi.class).where(POI_ID + " = ?", poiId).executeSingle();
    }
}

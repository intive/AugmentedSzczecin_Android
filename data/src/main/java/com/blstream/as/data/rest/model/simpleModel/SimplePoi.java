package com.blstream.as.data.rest.model.simpleModel;

import com.blstream.as.data.rest.model.enums.SubCategory;

/**
 * Created by Rafał Soudani on 2015-04-25.
 */
public class SimplePoi {
    private String name, description;
    private String[] tags;
    private String subcategory;
    private String www;
    private String phone;
    private String wiki;
    private String fanpage;
    private SimpleLocation location;
    private SimpleAddress address;
    private SimpleOpening[] opening;
    private Boolean paid;

    public SimplePoi(String name,
                     String description,
                     SimpleAddress address,
                     String[] tags,
                     SimpleLocation location,
                     SubCategory subcategory,
                     String www,
                     String phone,
                     String wiki,
                     String fanpage,
                     SimpleOpening[] opening,
                     Boolean paid) {

        this.name = name;
        this.description = description;
        this.tags = tags;
        if (subcategory != null) {
            this.subcategory = subcategory.toString();
        }
        this.www = www;
        this.phone = phone;
        this.wiki = wiki;
        this.fanpage = fanpage;
        this.location = location;
        this.address = address;
        this.opening = opening;
        this.paid = paid;
    }

}

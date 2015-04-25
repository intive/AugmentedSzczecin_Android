package com.blstream.as.data.rest.model;

import com.activeandroid.Model;

/**
 * Created by Rafał Soudani on 2015-04-25.
 */
public class SimplePoi{
    String name;
    Location location;

    public SimplePoi() {
    }

    public SimplePoi(String name, Location location) {
        this.name = name;
        this.location = location;
    }
}

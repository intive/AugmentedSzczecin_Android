package com.blstream.as.data.rest.model;

/**
 * Created by Rafal Soudani on 2015-06-02.
 */
public enum Kategory {
    PLACE("Miejsca publiczne"), COMMERCIAL("Firmy i us³ugi"), EVENT("Wydarzenia"), PERSON("Znajomi");

    private String description;

    Kategory(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

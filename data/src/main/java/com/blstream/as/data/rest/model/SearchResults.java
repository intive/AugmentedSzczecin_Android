package com.blstream.as.data.rest.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResults {
    @SerializedName("places")
    private ArrayList<SearchResult> placesList;
    @SerializedName("commercials")
    private ArrayList<SearchResult> commercialsList;
    @SerializedName("people")
    private ArrayList<SearchResult> peopleList;
    @SerializedName("events")
    private ArrayList<SearchResult> eventsList;

    public ArrayList<SearchResult> getCommercialsList() {
        return commercialsList;
    }

    public void setCommercialsList(ArrayList<SearchResult> commercialsList) {
        this.commercialsList = commercialsList;
    }

    public ArrayList<SearchResult> getPeopleList() {
        return peopleList;
    }

    public void setPeopleList(ArrayList<SearchResult> peopleList) {
        this.peopleList = peopleList;
    }

    public ArrayList<SearchResult> getEventsList() {
        return eventsList;
    }

    public void setEventsList(ArrayList<SearchResult> eventsList) {
        this.eventsList = eventsList;
    }

    public void setPlacesList(ArrayList<SearchResult> placesList){
        this.placesList = placesList;
    }

    public ArrayList<SearchResult> getPlacesList(){
        return placesList;
    }
}

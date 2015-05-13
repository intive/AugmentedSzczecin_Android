package com.blstream.as.data.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafal Soudani on 2015-03-25.
 * <p/>
 * Helper class used only for getting data from server (not saved in database)
 */
public class Page {

    @SerializedName("num_results")
    private int numResults;
    private int page;
    @SerializedName("num_pages")
    private int numPages;
    @SerializedName("data")
    private List<Poi> pois = new ArrayList<>();

    /**
     * @return The numResults
     */
    public int getNumResults() {
        return numResults;
    }

    /**
     * @param numResults The num_results
     */
    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    /**
     * @return The page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page The page
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return The numPages
     */
    public int getNumPages() {
        return numPages;
    }

    /**
     * @param numPages The num_pages
     */
    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    /**
     * @return The data
     */
    public List<Poi> getPois() {
        return pois;
    }

    /**
     * @param pois The data
     */
    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

}

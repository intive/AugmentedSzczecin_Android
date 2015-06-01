package com.blstream.as.data.rest.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Damian on 2015-05-27.
 */
public class Opening {
    @Expose
    private String day;

    @Expose
    private String open;

    @Expose
    private String close;

    public Opening() {
        this("","","");
    }

    public Opening(String day, String open, String close) {
        this.day = day;
        this.open = open;
        this.close = close;
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
}

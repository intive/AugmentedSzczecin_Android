package com.blstream.as.data.rest.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Created by Damian on 2015-05-27.
 * Edited by Rafal Soudani
 */
@Table(name = Opening.TABLE_NAME, id = BaseColumns._ID)
public class Opening {

    public static final String TABLE_NAME = "Openings";
    public static final String DAY = "Day";
    public static final String OPEN = "Open";
    public static final String CLOSE = "Close";

    @Column(name = DAY)
    private String day;

    @Column(name = OPEN)
    private String open;

    @Column(name = CLOSE)
    private String close;

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

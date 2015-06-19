package com.blstream.as.data.rest.model.simpleModel;

/**
 * Created by Rafal Soudani on 2015-06-10.
 */
public class SimpleOpening {
    private String day;
    private String open;
    private String close;


    public SimpleOpening(String day, String open, String close) {
        if (day != null) {
            this.day = day;
        }
        if (open != null) {
            this.open = open;
        }
        if (close != null) {
            this.close = close;
        }
    }
}

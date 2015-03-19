package com.blstream.as;

public class Utils {

    private static Utils utils = new Utils();

    private Utils() {

    }

    public static Utils getInstance() {
        return utils;
    }

    public float computeAcosToDegrees(float value) {
        return Math.round(Math.toDegrees(Math.acos(value)));
    }
}

package com.blstream.as;

public final class Utils {

    public static double computeDistanceInMeters(double longitude1, double latitude1,
                                                 double longitude2, double latitude2) {

        final int earthR = 6371000;

        double deltaLongitude = Math.toRadians(longitude1 - longitude2);
        double deltaLatitude = Math.toRadians(latitude1 - latitude2);

        double a = (Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                (Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2));
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthR * c;
    }
}

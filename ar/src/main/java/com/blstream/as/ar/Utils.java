package com.blstream.as.ar;

import android.graphics.PointF;

public final class Utils {

    public static double computeDistanceInMeters(double longitude1, double latitude1,
                                                 double longitude2, double latitude2) {

        final int earthRadius = 6371000;

        double deltaLongitude = Math.toRadians(longitude1 - longitude2);
        double deltaLatitude = Math.toRadians(latitude1 - latitude2);

        double a = (Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                (Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2));
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    public static double normalizeAngle(double angle) {
        if (angle < -180.0) {
            angle += 360.0;
        }

        if (angle > 180.0) {
            angle -= 360.0;
        }
        return angle;
    }

    public static PointF getPointInDistanceAtAngle(double longitude, double latitude, double distance, double angle) {
        double earthRadius = 6371000.0;
        double longitudeInRadians = Math.toRadians(longitude);
        double latitudeInRadians = Math.toRadians(latitude);
        double angularDistance = distance / earthRadius;
        double angleInRadians = Math.toRadians(angle);

        double pointLatitude = Math.asin(Math.sin(latitudeInRadians) * Math.cos(angularDistance) +
                Math.cos(latitudeInRadians) * Math.sin(angularDistance) * Math.cos(angleInRadians));

        double deltaLongitude = Math.atan2(Math.sin(angleInRadians) * Math.sin(angularDistance) * Math.cos(latitudeInRadians),
                Math.cos(angularDistance) - Math.sin(latitudeInRadians) * Math.sin(pointLatitude));

        double pointLongitude = ((longitudeInRadians + deltaLongitude + Math.PI) % (Math.PI * 2.0)) - Math.PI;

        pointLatitude = Math.toDegrees(pointLatitude);
        pointLongitude = Math.toDegrees(pointLongitude);

        return new PointF((float) pointLatitude, (float) pointLongitude);
    }
}

package com.blstream.as;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.LinkedList;
import java.util.Queue;

public class Engine extends View implements SensorEventListener, LocationListener {
    private final double MAX_DISTANCE = 0.005;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    private final int UPDATE_TIME = 50;
    private final int MAX_UPDATE_TIME = 60000;
    private final int MAX_UPDATE_DISTANCE = 1;

    Queue<Double> rotationCos;
    Queue<Double> rotationSin;

    private float[] accelerometer;
    private float[] magnetic;

    private double longitude;
    private double latitude;
    private double angle;

    private double cameraFov;

    private int currentIndex = 0;

    private double totalCos = 0.0;
    private double totalSin = 0.0;
    private double averageAngle = 0.0;

    public Engine(Context context) {
        super(context);
        rotationSin = new LinkedList<>();
        rotationCos = new LinkedList<>();
    }
    public void register(WindowManager windowManager, SensorManager sensorManager, LocationManager locationManager) {
        this.windowManager = windowManager;
        this.sensorManager = sensorManager;
        this.locationManager = locationManager;

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
    }
    public void unRegister() {
        if (locationManager != null && sensorManager != null) {
            sensorManager.unregisterListener(this);
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometer = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetic = event.values.clone();
                break;
        }

        if (accelerometer != null && magnetic != null) {
            float[] rotationMatrix = new float[9];
            float[] inclinationMatrix = new float[9];
            float[] directions = new float[3];

            SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, accelerometer, magnetic);
            SensorManager.getOrientation(rotationMatrix, directions);

            directions[0] = (float) Math.toDegrees(directions[0]);
            directions[0] += windowManager.getDefaultDisplay().getRotation() * 90.0f;

            directions[0] = (float) Math.toRadians(directions[0]);

            rotationSin.add(Math.sin(directions[0]));
            rotationCos.add(Math.cos(directions[0]));

            totalSin += Math.sin(directions[0]);
            totalCos += Math.cos(directions[0]);

            currentIndex++;

            if (currentIndex > UPDATE_TIME) {

                totalSin -= rotationSin.remove();
                totalCos -= rotationCos.remove();

                averageAngle = Math.toDegrees(Math.atan2(totalSin, totalCos));

                currentIndex--;
                invalidate();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /* Returns the fraction of the screen in which the POI should be drawn.
     * If the result is not between 0 and 1, that means the POI is out of sight.w
     */
    protected double computeXCoordinate(double poiLongitude, double poiLatitude) {

        angle = Math.toDegrees(Math.atan2(longitude - poiLongitude, latitude - poiLatitude)) + 180.0;

        angle -= averageAngle;
        if (angle < -180.0) {
            angle += 360.0;
        }

        if (angle > 180.0) {
            angle -= 360.0;
        }

        return (cameraFov / 2 + angle) / cameraFov;
    }
    protected double computeYCoordinate(double poiLongitude, double poiLatitude) {
        double vecX, vecY;
        vecX = poiLongitude - longitude;
        vecY = poiLatitude - latitude;
        double distance = Math.sqrt((vecX * vecX) + (vecY * vecY));
        return distance / MAX_DISTANCE;
    }
    public double getCameraFov() {
        return cameraFov;
    }

    public void setCameraFov(double cameraFov) {
        this.cameraFov = cameraFov;
    }
}

package com.blstream.as.ar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;


public class Engine extends View implements SensorEventListener, com.google.android.gms.location.LocationListener {
    private static final double MIN_DISTANCE_OF_POI_RELOAD = 100.0;
    private static final int ROTATION_MATRIX_SIZE = 9;
    private static final int DIRECTION_SIZE = 3;
    private static final float ALPHA = 0.95f;

    private WindowManager windowManager;
    private SensorManager sensorManager;

    private float[] accelerometer;
    private float[] magnetic;

    private double longitude;
    private double latitude;

    private double oldLongitude;
    private double oldLatitude;

    private double cameraFov;

    private double totalCos = 0.0;
    private double totalSin = 0.0;
    private double averageAngle = Double.NEGATIVE_INFINITY;

    private Callbacks fragmentConnector;

    public interface Callbacks {
        void restartLoader();
    }

    public Engine(Context context) {
        super(context);
        accelerometer = new float[DIRECTION_SIZE];
        magnetic = new float[DIRECTION_SIZE];
    }
    public void register(WindowManager windowManager, SensorManager sensorManager) {
        this.windowManager = windowManager;
        this.sensorManager = sensorManager;

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
    }
    public void unRegister() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < DIRECTION_SIZE; i++) {
                    accelerometer[i] = ALPHA * accelerometer[i] + (1.0f - ALPHA) * event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < DIRECTION_SIZE; i++) {
                    magnetic[i] = ALPHA * magnetic[i] + (1.0f - ALPHA) * event.values[i];
                }
                break;
        }

        if (accelerometer != null && magnetic != null) {
            float[] rotationMatrix = new float[ROTATION_MATRIX_SIZE];
            float[] directions = new float[DIRECTION_SIZE];

            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer, magnetic);
            SensorManager.getOrientation(rotationMatrix, directions);
            float xDirection = directions[0];

            int rotation = windowManager.getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_90:
                    xDirection += 0.5 * Math.PI;
                    break;
                case Surface.ROTATION_180:
                    xDirection += Math.PI;
                    break;
                case Surface.ROTATION_270:
                    xDirection += 1.5 * Math.PI;
                    break;
            }

            totalSin = Math.sin(xDirection);
            totalCos = Math.cos(xDirection);
            averageAngle = Math.toDegrees(Math.atan2(totalSin, totalCos));
            invalidate();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onLocationChanged(Location location) {

        longitude = location.getLongitude();
        latitude = location.getLatitude();
        if (Utils.computeDistanceInMeters(longitude, latitude, oldLongitude, oldLatitude) > MIN_DISTANCE_OF_POI_RELOAD) {
            oldLatitude = latitude;
            oldLongitude = longitude;
            fragmentConnector.restartLoader();
        }
    }

    /* Returns the fraction of the x coordinate of the screen in which the POI should be drawn.
     * If the result is not between 0 and 1, that means the POI is out of sight.
     */
    protected double computeXCoordinate(double poiLongitude, double poiLatitude) {
        double angle = Math.toDegrees(Math.atan2(longitude - poiLongitude, latitude - poiLatitude)) + 180.0;

        angle -= averageAngle;
        angle = Utils.normalizeAngle(angle);

        return (cameraFov / 2 + angle) / cameraFov;
    }

    /* Returns the fraction of the x coordinate of the screen in which the POI should be drawn.
     * If the result is not between 0 and 1, that means the POI is out of distance.
    */
    protected double computeYCoordinate(double poiLongitude, double poiLatitude, double minDistance, double maxDistance) {
        double distance = Utils.computeDistanceInMeters(poiLongitude, poiLatitude, longitude, latitude);
        return (distance - minDistance) / (maxDistance - minDistance);
    }

    public void setCameraFov(double cameraFov) {
        this.cameraFov = cameraFov;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void attachFragment(Callbacks callbacks) {
        this.fragmentConnector = callbacks;
    }
}

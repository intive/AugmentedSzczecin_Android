package com.blstream.as;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

public class ArFragment extends Fragment implements SensorEventListener, LocationListener {

    private static SensorManager sensorManager;
    private static LocationManager locationManager;

    private final int UPDATE_TIME = 250;
    private final int MAX_UPDATE_TIME = 60000;
    private final int MAX_UPDATE_DISTANCE = 1;
    private final float FOV = 140.0f;

    Activity activity;

    Queue<Double> rotationCos;
    Queue<Double> rotationSin;

    private float[] accelerometer;
    private float[] magnetic;

    private TextView testPoint1;
    private TextView testPoint2;
    private TextView testPoint3;

    private String point1Name = "Filharmonia: ";
    private String point2Name = "Brama królewska: ";
    private String point3Name = "Radisson: ";

    private double[] point1Coordinate = {14.557922, 53.429131};
    private double[] point2Coordinate = {14.556736, 53.428348};
    private double[] point3Coordinate = {14.556371, 53.431925};

    private double longitude;
    private double latitude;
    private double angle;

    private int currentIndex = 0;

    private double totalCos = 0.0;
    private double totalSin = 0.0;
    private double averageAngle = 0.0;

    public ArFragment() {

    }

    public static ArFragment newInstance() {
        return new ArFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ar, container, false);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        testPoint1 = (TextView) view.findViewById(R.id.test_point1);
        testPoint2 = (TextView) view.findViewById(R.id.test_point2);
        testPoint3 = (TextView) view.findViewById(R.id.test_point3);

        rotationSin = new LinkedList<>();
        rotationCos = new LinkedList<>();

        if (sensorManager == null) {
            sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        }

        if (locationManager == null) {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }

        if (locationManager == null || sensorManager == null) {
            testPoint1.setText("Błąd odczytu lokalizacji.");
        }

        else {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
        }

        return view;
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
            directions[0] +=
                    ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation() * 90.0f;

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

                if (longitude != 0.0 && latitude != 0.0) {

                    computeXCoordinate(FOV, point1Coordinate[0], point1Coordinate[1]);
                    testPoint1.setText(point1Name + String.valueOf((int) angle));

                    computeXCoordinate(FOV, point2Coordinate[0], point2Coordinate[1]);
                    testPoint2.setText(point2Name + String.valueOf((int) angle));

                    computeXCoordinate(FOV, point3Coordinate[0], point3Coordinate[1]);
                    testPoint3.setText(point3Name + String.valueOf((int) angle));
                }

                currentIndex--;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationManager != null && sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null && sensorManager != null) {
            sensorManager.unregisterListener(this);
            locationManager.removeUpdates(this);
        }
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    /* Returns the fraction of the screen in which the POI should be drawn.
     * If the result is not between 0 and 1, that means the POI is out of sight.w
     */
    protected double computeXCoordinate(double fov, double poiLongitude, double poiLatitude) {

        angle = Math.toDegrees(Math.atan2(longitude - poiLongitude, latitude - poiLatitude)) + 180.0;

        angle -= averageAngle;
        if (angle < -180.0) {
            angle += 360.0;
        }

        if (angle > 180.0) {
            angle -= 360.0;
        }

        return (fov / 2 + angle) / fov;
    }
}

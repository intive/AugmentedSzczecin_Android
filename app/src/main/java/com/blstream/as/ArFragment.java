package com.blstream.as;

import android.app.Fragment;
import android.content.Context;
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

public class ArFragment extends Fragment implements SensorEventListener, LocationListener {

    private float[] accelerometer;
    private float[] magnetic;
    private static SensorManager sensorManager;
    private static LocationManager locationManager;
    private TextView sensorsInfo;
    private TextView gpsInfo;

    private final float FONT_SIZE = 24.0f;
    private final int MAX_UPDATE_TIME = 60000;
    private final int MAX_UPDATE_DISTANCE = 1;

    public static ArFragment newInstance() {
        return new ArFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ar, container, false);

        sensorsInfo = (TextView) view.findViewById(R.id.rotation);
        sensorsInfo.setTextSize(FONT_SIZE);

        gpsInfo = (TextView) view.findViewById(R.id.gps_location);
        gpsInfo.setTextSize(FONT_SIZE);

        if (sensorManager == null)
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (locationManager == null)
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);

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
            directions[0] = (int) Math.toDegrees(directions[0]);
            directions[0] +=
                    ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation() * 90;

            float inclinationX = Utils.getInstance().computeAcosToDegrees(rotationMatrix[7]);
            float inclinationY = Utils.getInstance().computeAcosToDegrees(rotationMatrix[8]);

            sensorsInfo.setText("X: " + String.valueOf(inclinationX) +
                    "\nY: " + String.valueOf(inclinationY) +
                    "\nZ: " + String.valueOf(directions[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String locationInfo = "";
        locationInfo += " Dlugosc: " + String.valueOf(location.getLongitude());
        locationInfo += "\n Szerokosc: " + String.valueOf(location.getLatitude());
        locationInfo += "\n Wysokosc: " + String.valueOf(location.getAltitude());

        gpsInfo.setText(locationInfo);
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
}

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArActivity extends Fragment implements SensorEventListener, LocationListener {

    private float[] accelerometer;
    private float[] magnetic;
    private static SensorManager sensorManager;
    private static LocationManager locationManager;
    private static LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        TextView sensors = new TextView(getActivity());
        sensors.setId(R.id.rotation);
        sensors.setText("Test");
        sensors.setTextSize(24.0f);

        TextView gps = new TextView(getActivity());
        gps.setId(R.id.gps);
        gps.setText("Test");
        gps.setTextSize(24.0f);
        if (sensorManager == null)
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (locationManager == null)
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        if (linearLayout == null) {
            linearLayout = new LinearLayout(getActivity());

            linearLayout.addView(sensors);
            linearLayout.addView(gps);
        }

        return linearLayout;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
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
                float inclinationX = Math.round(Math.toDegrees(Math.acos(rotationMatrix[7])));
                float inclinationY = Math.round(Math.toDegrees(Math.acos(rotationMatrix[8])));



                TextView sensors = (TextView) getView().findViewById(R.id.rotation);
                sensors.setText("X: " + String.valueOf(inclinationX) +
                        "\nY: " + String.valueOf(inclinationY) +
                        "\nZ: " + String.valueOf(directions[0]));


            }
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

        TextView gps = (TextView) getView().findViewById(R.id.gps);
        gps.setText(locationInfo);
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

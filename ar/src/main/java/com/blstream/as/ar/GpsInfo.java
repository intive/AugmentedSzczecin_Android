package com.blstream.as.ar;

import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Damian on 2015-05-02.
 */
public class GpsInfo {
    public interface ArCallback {
        public void enableAugmentedReality();
        public void disableAugmentedReality();
        public void showGpsUnavailable();
        public void showSearchingSignal();
    }
    private LocationManager locationManager;
    private ArCallback arCallback;
    private GpsStatusListener gpsStatusListener;
    private boolean isAvailable;
    private boolean isLocated;

    public GpsInfo() {
        isAvailable = isLocated = false;
    }
    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
        isAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isAvailable) {
            initGps();
        } else
        {
            arCallback.showGpsUnavailable();
        }
    }
    private void initGps() {
        if(locationManager != null) {
            gpsStatusListener = new GpsStatusListener();
            isAvailable = locationManager.addGpsStatusListener(gpsStatusListener);
        }
    }
    private class GpsStatusListener implements GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {
            switch(event)
            {
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    isLocated = false;
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    isLocated = true;

                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    if(isLocated) {
                        arCallback.enableAugmentedReality();
                    }else {
                        arCallback.disableAugmentedReality();
                    }
                    break;
            }
        }
    }
    public void setArCallback(ArCallback arCallback) {
        this.arCallback = arCallback;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
    public boolean isLocated() {
        return isLocated;
    }
}

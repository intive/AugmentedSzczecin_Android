package com.blstream.as.ar;

import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Damian on 2015-05-02.
 */
public class GpsInfo {
    private static final int SEARCHING_SIGNAL_TIME = 5000;
    public interface ArCallback {
        public void enableAugmentedReality();
        public void disableAugmentedReality();
        public void showGpsUnavailable();
        public void showSearchingSignal();
        public void hideSearchingSignal();
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
    }
    private void initGps() {
        if(locationManager != null) {
            gpsStatusListener = new GpsStatusListener();
            locationManager.addGpsStatusListener(gpsStatusListener);
        }
    }
    private class GpsStatusListener implements GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {
            switch(event)
            {
                case GpsStatus.GPS_EVENT_STARTED:
                    isLocated = false;
                    arCallback.disableAugmentedReality();
                    arCallback.showSearchingSignal();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:

                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    isLocated = true;
                    arCallback.enableAugmentedReality();
                    arCallback.hideSearchingSignal();
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    break;
            }
        }
    }
    public void attachArCallback(ArCallback arCallback) {
        this.arCallback = arCallback;
        if(locationManager == null) {
            isAvailable = false;
            return;
        }
        isAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isAvailable) {
            initGps();
        } else
        {
            arCallback.showGpsUnavailable();
        }
    }
    public void detachArCallback() {
        if(locationManager != null) {
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public boolean isLocated() {
        return isLocated;
    }
}

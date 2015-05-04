package com.blstream.as.ar;

import android.location.GpsStatus;
import android.location.LocationManager;

/**
 * Created by Damian on 2015-05-02.
 */
public class GpsSignalResponder {
    public interface Callback {
        public void enableAugmentedReality();
        public void disableAugmentedReality();
        public void showGpsUnavailable();
        public void showSearchingSignal();
        public void hideSearchingSignal();
    }
    private LocationManager locationManager;
    private Callback callbackFromGps;
    private GpsStatusListener gpsStatusListener;
    private boolean isAvailable;
    private boolean isLocated;

    public GpsSignalResponder() {
        isAvailable = isLocated = false;
    }
    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    private class GpsStatusListener implements GpsStatus.Listener {
        @Override
        public void onGpsStatusChanged(int event) {
            switch(event)
            {
                case GpsStatus.GPS_EVENT_STARTED:
                    if(isLocated)
                        break;
                    callbackFromGps.disableAugmentedReality();
                    callbackFromGps.showSearchingSignal();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:

                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    isLocated = true;
                    callbackFromGps.enableAugmentedReality();
                    callbackFromGps.hideSearchingSignal();
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    break;
            }
        }
    }
    public void attachCallback(Callback callbackFromGps) {
        this.callbackFromGps = callbackFromGps;
        if(locationManager == null) {
            isAvailable = false;
            return;
        }
        isAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isAvailable) {
            initGps();
        } else
        {
            callbackFromGps.showGpsUnavailable();
        }
    }
    private void initGps() {
        if(locationManager != null) {
            gpsStatusListener = new GpsStatusListener();
            locationManager.addGpsStatusListener(gpsStatusListener);
        }
    }
    public void detachCallback() {
        if(locationManager != null) {
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
    }

    public boolean isLocated() {
        return isLocated;
    }
}

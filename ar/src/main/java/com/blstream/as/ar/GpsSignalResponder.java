package com.blstream.as.ar;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

/**
 * Created by Damian on 2015-05-02.
 */
public class GpsSignalResponder {
    public interface Callback {
        void enableAugmentedReality();
        void disableAugmentedReality();
        void showGpsUnavailable();
        void showSearchingSignal();
        void hideSearchingSignal();
    }
    private LocationManager locationManager;
    private Callback callbackFromGps;
    private GpsStatusListener gpsStatusListener;
    private boolean isAvailable;
    private boolean isLocated;
    private boolean isFirstFixed;

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
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    GpsStatus status = locationManager.getGpsStatus(null);
                    Iterable<GpsSatellite> allSatellites = status.getSatellites();
                    int numFixedSatellites = 0;
                    boolean areFixed = false;
                    for(GpsSatellite satellite : allSatellites) {
                        if(satellite.usedInFix()) {
                            ++numFixedSatellites;
                        }
                    }
                    if(numFixedSatellites >= 3) { //FIXME: magic value, zmien np. na MINIMUM_SATELLITES czy cos takiego, do tego mozesz dolozyc drugi warunek sprawdzajacy czy areFixed nie jest juz true
                        areFixed = true;
                    }
                    if(isLocated != areFixed || !isFirstFixed) { //FIXME: cos tu jest za bardzo zamotane z tymi booleanami, wszystkie sa potrzebne? nie da sie prosciej?
                        isLocated = areFixed;
                        isFirstFixed = true;
                        if(isLocated) {
                            callbackFromGps.enableAugmentedReality();
                            callbackFromGps.hideSearchingSignal();
                        } else {
                            callbackFromGps.disableAugmentedReality();
                            callbackFromGps.showSearchingSignal();
                        }
                    }
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

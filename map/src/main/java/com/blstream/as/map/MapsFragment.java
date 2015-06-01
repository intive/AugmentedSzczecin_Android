package com.blstream.as.map;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.service.Server;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener, com.google.android.gms.location.LocationListener {

    public static final String TAG = MapsFragment.class.getSimpleName();

    private static final float ZOOM = 14;
    private static final LatLng defaultPosition = new LatLng(53.424173, 14.555959);
    private static final int TIME_LOCATION_UPDATE = 10000;
    private static final int FASTEST_TIME_LOCATION_UPDATE = 5000;

    private static HashMap<String, Marker> markerHashMap = new HashMap<>();
    private static HashMap<Marker,String> poiIdHashMap = new HashMap<>();

    private GoogleMap googleMap;
    private boolean poiAddingMode = false;
    private boolean cameraSet = false;

    private Marker markerTarget;
    private Marker userPositionMarker;
    
    private Callbacks activityConnector;

    private View rootView;
    private ImageView arSwitcher;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    public static MapsFragment newInstance(GoogleApiClient googleApiClient) {
        MapsFragment newFragment = new MapsFragment();
        newFragment.googleApiClient = googleApiClient;
        return newFragment;
    }

    public void moveToMarker(Marker marker) {
        if (googleMap != null && marker != null) {
            cameraSet = true;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
            marker.showInfoWindow();
        }
    }

    public interface Callbacks {
        void switchToAr();

        void showConfirmPoiWindow(Marker marker);

        void dismissConfirmAddPoiWindow();

        void showLocationUnavailable();

        void showPoiPreview(Marker marker);

        void hidePoiPreview();

        void deletePoi(Marker marker);

    }

    public void setPoiAddingMode(boolean poiAddingMode) {
        this.poiAddingMode = poiAddingMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
    }

    private void setArSwitcher() {
        if (rootView != null) {
            arSwitcher = (ImageView) rootView.findViewById(R.id.ar_switch);
        }

        if (arSwitcher != null) {
            arSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activityConnector != null) {
                        activityConnector.switchToAr();
                    }
                }
            });
        }
    }


    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(TIME_LOCATION_UPDATE);
        locationRequest.setFastestInterval(FASTEST_TIME_LOCATION_UPDATE);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
            setArSwitcher();
        }
        setUpMapIfNeeded();
        return rootView;
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            googleMap = mapFragment.getMap();
            if (googleMap != null) {
                Log.v(TAG, "Map loaded");
                setUpMap();
                googleMap.setOnMapClickListener(this);
                googleMap.setOnMarkerDragListener(this);
            }
        }
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(false);
        googleMap.setOnMarkerClickListener(this);

        if (userPositionMarker == null) {
            BitmapDescriptor userPositionIcon = BitmapDescriptorFactory.fromResource(R.drawable.user_icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(userPositionIcon);
            markerOptions.position(defaultPosition);
            userPositionMarker = googleMap.addMarker(markerOptions);
        }
        moveToMarker(userPositionMarker);
    }
    public void setUpLocation() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation == null) {
            activityConnector.showLocationUnavailable();
            return;
        }
        if(userPositionMarker != null) {
            userPositionMarker.setPosition(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            moveToMarker(userPositionMarker);
        }
    }
 
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            activityConnector = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MapFragment.Callbacks");
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "Starting loading");
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Poi.class, null),
                null, null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        removeAllMarkers();

        int poiIdIndex = cursor.getColumnIndex(Poi.POI_ID);
        int nameIndex = cursor.getColumnIndex(Poi.NAME);
        int longitudeIndex = cursor.getColumnIndex(Poi.LONGITUDE);
        int latitudeIndex = cursor.getColumnIndex(Poi.LATITUDE);

        if (cursor.moveToFirst()) {
            do {
                if (googleMap != null) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .title(cursor.getString(nameIndex))
                                    .position(new LatLng(Double.parseDouble(cursor.getString(latitudeIndex))
                                            , Double.parseDouble(cursor.getString(longitudeIndex))))
                    );
                    markerHashMap.put(cursor.getString(poiIdIndex), marker);
                    poiIdHashMap.put(marker,cursor.getString(poiIdIndex));
                    Log.v(TAG, "Loaded: " + marker.getTitle() + ", id: " + marker.getId());
                }
            } while (cursor.moveToNext());
        }
    }

    private void removeAllMarkers() {
        for (Marker marker : markerHashMap.values()) {
            marker.remove();
            poiIdHashMap.remove(marker);
        }

    }
    /**
     * @param poiId Poi id on server,
     * @return Marker created from Poi with given ID, or null if there is not such marker
     */
    public static Marker getMarkerFromPoiId(String poiId) {
        if (markerHashMap != null) {
            return markerHashMap.get(poiId);
        } else {
            return null;
        }
    }

    public static String getPoiIdFromMarker(Marker marker) {
        if (poiIdHashMap != null) {
            return poiIdHashMap.get(marker);
        } else {
            return null;
        }
    }

    public void deletePoi(Marker marker) {
        if (markerHashMap != null) {
            for (String poId : markerHashMap.keySet()) {
                if (marker.equals(getMarkerFromPoiId(poId))) {
                    marker.remove();
                    if(poiIdHashMap != null) {
                        poiIdHashMap.remove(marker);
                    }
                    Server.deletePoi(poId);
                    activityConnector.hidePoiPreview();
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(userPositionMarker)) {
            return true;
        } else if (markerIsNew(marker)) {
            activityConnector.showConfirmPoiWindow(marker);
        } else {
            activityConnector.showPoiPreview(marker);
        }
        return false;
    }

    private boolean markerIsNew(Marker marker) {
        return (marker.getTitle() == null || marker.getTitle().equals(""));
    }

    public Marker getMarkerTarget() {
        return markerTarget;
    }

    public void setMarkerTarget(Marker markerTarget) {
        this.markerTarget = markerTarget;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location updated");
        Log.v(TAG, location.getLatitude() + ", " + location.getLongitude());
        LatLng googleLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (userPositionMarker != null) {
            userPositionMarker.setPosition(googleLocation);
            if (!cameraSet && googleMap != null) {
                moveToMarker(userPositionMarker);
                cameraSet = true;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        cameraSet = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (markerTarget != null) {
            markerTarget.remove();
        }
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        setPoiAddingMode(false);
        activityConnector.dismissConfirmAddPoiWindow();
        if (googleMap != null) {
            googleMap.setOnMarkerClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    public void moveToActiveMarker() {
        if (markerTarget == null) {
            moveToMarker(userPositionMarker);
        } else {
            moveToMarker(markerTarget);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        activityConnector.dismissConfirmAddPoiWindow();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM), new AnimateCameraCallbacks());

    }

    @Override
    public void onMapClick(LatLng latLng) {
        activityConnector.dismissConfirmAddPoiWindow();
        if (poiAddingMode) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
            markerTarget = marker;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM), new AnimateCameraCallbacks());
            marker.setDraggable(true);
            setPoiAddingMode(false);
        }
        activityConnector.hidePoiPreview();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        //TODO poi preview height can be set
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        activityConnector.dismissConfirmAddPoiWindow();
    }

    public class AnimateCameraCallbacks implements GoogleMap.CancelableCallback {

        @Override
        public void onFinish() {
            activityConnector.showConfirmPoiWindow(markerTarget);
        }

        @Override
        public void onCancel() {

        }
    }

}

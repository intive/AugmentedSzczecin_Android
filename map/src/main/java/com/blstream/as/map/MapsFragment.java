package com.blstream.as.map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.rest.model.Poi;
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

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener {

    public static final String TAG = MapsFragment.class.getSimpleName();

    private static final float ZOOM = 14;
    private static final int MAX_UPDATE_TIME = 1000;
    private static final int MAX_UPDATE_DISTANCE = 1;
    private static final LatLng defaultPosition = new LatLng(0.0, 0.0);

    private static HashMap<String, Marker> markerHashMap = new HashMap<>();

    private GoogleMap googleMap;
    private boolean addingPoi = false;
    private boolean gpsChecked;
    private boolean cameraSet = false;

    private Marker markerTarget;
    private Marker userPositionMarker;
    private Button homeButton;
    private Button arButton;
    private Callbacks activityConnector;

    private View rootView;
    private LocationManager locationManager;

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    public void moveToMarker(Marker marker) {
        if (googleMap != null && marker != null){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
            marker.showInfoWindow();
        }
    }

    public interface Callbacks {
        void switchToAr();

        void switchToHome();

        void gpsLost();

        boolean isUserLogged();

        void showConfirmPoiWindow(Marker marker);

        void dismissConfirmAddPoiWindow();

        void showSlider(String title);

        void hideSlider();
    }

    public void setAddingPoi(boolean addingPoi) {
        this.addingPoi = addingPoi;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);

        gpsChecked = false;
        setUpMapIfNeeded();
        setButtons(rootView);

        if (!activityConnector.isUserLogged()) {
            disableButtons();
        }
        setButtonsListeners();

        return rootView;
    }

    private void disableButtons() {
        arButton.setVisibility(View.INVISIBLE);
    }

    private void setButtonsListeners() {
        setArButtonListener();
        setHomeButtonListener();
    }

    private void setArButtonListener() {
        arButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityConnector.switchToAr();
            }
        });
    }

    private void setHomeButtonListener() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityConnector.switchToHome();
            }
        });
    }

    private void setButtons(View view) {
        arButton = (Button) view.findViewById(R.id.arButton);
        homeButton = (Button) view.findViewById(R.id.homeButton);
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
            markerOptions.position(defaultPosition);
            markerOptions.icon(userPositionIcon);
            userPositionMarker = googleMap.addMarker(markerOptions);
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
                    Log.v(TAG, "Loaded: " + marker.getTitle());
                }
            } while (cursor.moveToNext());
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(userPositionMarker)) {
            return true;
        } else if (markerIsNew(marker)) {
            activityConnector.showConfirmPoiWindow(marker);
        } else {
            activityConnector.showSlider(marker.getTitle());
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
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !gpsChecked) {
            gpsChecked = true;
            activityConnector.gpsLost();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (markerTarget != null) {
            markerTarget.remove();
        }
        setAddingPoi(false);
        activityConnector.dismissConfirmAddPoiWindow();
        googleMap.setOnMarkerClickListener(this);
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
        if (addingPoi) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
            markerTarget = marker;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM), new AnimateCameraCallbacks());
            marker.setDraggable(true);
            setAddingPoi(false);
        }
        activityConnector.hideSlider();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
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

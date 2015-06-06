package com.blstream.as.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.RelativeLayout;

import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.SubCategory;
import com.blstream.as.data.rest.service.MyContentProvider;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener, com.google.android.gms.location.LocationListener,
        MapNavigation.MapNavigationCallbacks {

    public static final String TAG = MapsFragment.class.getSimpleName();

    private static final int LOADER_ID = 1;
    private static final float ZOOM = 14;
    private static final LatLng defaultPosition = new LatLng(53.424173, 14.555959);
    private static final int TIME_LOCATION_UPDATE = 10000;
    private static final int FASTEST_TIME_LOCATION_UPDATE = 5000;
    private static final float NAVIGATION_LINE_WIDTH = 5.0f;

    private static HashMap<String, Marker> markerHashMap = new HashMap<>();
    private static HashMap<Marker, String> poiIdHashMap = new HashMap<>();

    private GoogleMap googleMap;
    private boolean poiAddingMode = false;
    private boolean cameraSet = false;

    private MapNavigation mapNavigation;
    private Polyline navigationLine;
    private ProgressDialog navigationInProgress;
    private boolean inNavigationState = false;

    private Marker markerTarget;
    private Marker userPositionMarker;
    private ScaleBar scaleBar;

    private Callbacks activityConnector;

    private View rootView;
    private ImageView arSwitcher;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private List<Integer> selectedSubcategories;

    public static MapsFragment newInstance(GoogleApiClient googleApiClient, List<Integer> selectedSubcategories) {
        MapsFragment newFragment = new MapsFragment();
        newFragment.googleApiClient = googleApiClient;
        newFragment.selectedSubcategories = selectedSubcategories;
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

        void deletePoi(String marker);

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

    private void setScaleBar() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        scaleBar = new ScaleBar(getActivity(), googleMap);
        scaleBar.setLayoutParams(params);

        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.mapLayout);
        relativeLayout.addView(scaleBar);
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            googleMap = mapFragment.getMap();
            if (googleMap != null) {
                Log.v(TAG, "Map loaded");
                setUpMap();
                setScaleBar();
                googleMap.setOnMapClickListener(this);
                googleMap.setOnMarkerDragListener(this);
                googleMap.setOnCameraChangeListener(this);
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

    public void navigateToPoi(String poiId) {
        inNavigationState = true;
        mapNavigation = new MapNavigation(this);
        Marker marker = markerHashMap.get(poiId);
        navigationInProgress = ProgressDialog.show(getActivity(), null, getString(R.string.navigation_in_progress), true);

        mapNavigation.execute(userPositionMarker.getPosition(), marker.getPosition());
    }

    @Override
    public void onRouteGenerated(Document document) {
        if (document != null && mapNavigation != null) {
            ArrayList<LatLng> directionPoints = mapNavigation.getDirection(document);
            PolylineOptions rectLine = new PolylineOptions()
                    .width(NAVIGATION_LINE_WIDTH).color(Color.BLUE);

            for (int i = 0; i < directionPoints.size(); i++) {
                rectLine.add(directionPoints.get(i));
            }
            if (navigationLine != null) {
                navigationLine.remove();
            }
            navigationLine = googleMap.addPolyline(rectLine);
            navigationInProgress.dismiss();
        } else {
            navigationInProgress.dismiss();
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.navigation_error_title)
                    .setMessage(R.string.navigation_error_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public void cancelNavigation() {
        inNavigationState = false;
        if (navigationLine != null) {
            navigationLine.remove();
        }
    }

    public void setUpLocation() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation == null) {
            activityConnector.showLocationUnavailable();
            return;
        }
        if (userPositionMarker != null) {
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
        String query = null;
        if (selectedSubcategories != null && selectedSubcategories.size() > 0) {
            query = String.format("%s IN (" + makeSelectedCategory() + ")", Poi.SUB_CATEGORY);
        }
        return new CursorLoader(getActivity(),
                MyContentProvider.createUri(Poi.class, null),
                null, query, null, null
        );
    }

    private String makeSelectedCategory() {
        SubCategory[] subCategories = SubCategory.values();
        String selectedSubcategoryName = getString(subCategories[selectedSubcategories.get(0)].getIdServerResource());
        StringBuilder stringBuilder = new StringBuilder("'" + selectedSubcategoryName + "'");
        for (int i = 1; i < selectedSubcategories.size(); ++i) {
            selectedSubcategoryName = getString(subCategories[selectedSubcategories.get(i)].getIdServerResource());
            stringBuilder.append(",'").append(selectedSubcategoryName).append("'");
        }
        return stringBuilder.toString();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        removeAllMarkers();

        int poiIdIndex = cursor.getColumnIndex(Poi.POI_ID);
        int nameIndex = cursor.getColumnIndex(Poi.NAME);
        int longitudeIndex = cursor.getColumnIndex(com.blstream.as.data.rest.model.Location.LONGITUDE);
        int latitudeIndex = cursor.getColumnIndex(com.blstream.as.data.rest.model.Location.LATITUDE);

        if (cursor.moveToFirst()) {
            do {
                if (googleMap != null) {
                    if (cursor.getString(nameIndex) != null && cursor.getString(latitudeIndex) != null && cursor.getString(longitudeIndex) != null) {
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .title(cursor.getString(nameIndex))
                                        .position(new LatLng(Double.parseDouble(cursor.getString(latitudeIndex))
                                                , Double.parseDouble(cursor.getString(longitudeIndex))))
                        );
                        markerHashMap.put(cursor.getString(poiIdIndex), marker);
                        poiIdHashMap.put(marker, cursor.getString(poiIdIndex));
                        Log.v(TAG, "Loaded: " + marker.getTitle() + ", id: " + marker.getId());
                    }
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

    public void deletePoi(String poiId) {
        Server.deletePoi(poiId);
        if (getMarkerFromPoiId(poiId) != null) {
            getMarkerFromPoiId(poiId).remove();
        }
        activityConnector.hidePoiPreview();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(userPositionMarker) || inNavigationState) {
            return true;
        } else if (markerIsNew(marker)) {
            activityConnector.showConfirmPoiWindow(marker);
        } else if (!inNavigationState) {
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

    public void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
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
    public void onResume() {
        super.onResume();
        if (googleMap != null) {
            googleMap.setOnCameraChangeListener(this);
        }
        getLoaderManager().restartLoader(LOADER_ID, null, this);
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
        if (!inNavigationState) {
            activityConnector.hidePoiPreview();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (scaleBar != null) {
            scaleBar.invalidate();
        }
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

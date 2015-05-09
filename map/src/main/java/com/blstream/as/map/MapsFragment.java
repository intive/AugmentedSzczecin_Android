package com.blstream.as.map;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.rest.model.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, LocationListener, GoogleMap.OnMarkerClickListener {

    public static final String TAG = MapsFragment.class.getSimpleName();
    private static final float ZOOM = 14;
    private static final int MAX_UPDATE_TIME = 1000;
    private static final int MAX_UPDATE_DISTANCE = 1;
    private static final int DEFAULT_POI_PANEL_HEIGHT = 200;

    private static GoogleMap googleMap;
    private static HashMap<String, Marker> markerHashMap = new HashMap<>();

    private int layoutHeight;

    private static Marker userPositionMarker;
    private ScrollView scrollView;
    private SlidingUpPanelLayout poiPreviewLayout;
    private LinearLayout poiToolbar;

    private Button homeButton;
    private Button arButton;
    private Callbacks activityConnector;

    public static Marker markerTarget;
    private boolean gpsChecked;
    public static boolean isCameraSet = false;
    private View rootView;

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }


    public interface Callbacks {
        void switchToAr();
        void switchToHome();
        void gpsLost();
        boolean isUserLogged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        }
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_UPDATE_TIME, MAX_UPDATE_DISTANCE, this);

        gpsChecked = false;
        setUpMapIfNeeded();
        setButtons(rootView);
        setPoiPreview();

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
                poiPreviewLayout.setPanelHeight(0);
                activityConnector.switchToAr();
            }
        });
    }

    private void setHomeButtonListener() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poiPreviewLayout.setPanelHeight(0);
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
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (poiPreviewLayout != null) {
                        poiPreviewLayout.setPanelHeight(0);
                    }
                }
            });
            Log.v(TAG, "Map loaded");
        }
        setUpMap();

    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(false);
        googleMap.setOnMarkerClickListener(this);

        if (userPositionMarker == null) {
            LatLng defaultPosition = new LatLng(0.0, 0.0);
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

    private void setPoiPreview() {

        poiPreviewLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingUpPanel);
        poiPreviewLayout.setTouchEnabled(false);
        poiPreviewLayout.setPanelHeight(0);

        View poiPreviewView = rootView.findViewById(R.id.poiPreviewLayout);
        scrollView = (ScrollView) poiPreviewView.findViewById(R.id.poiScrollView);
        poiToolbar = (LinearLayout) poiPreviewView.findViewById(R.id.poiToolbar);

        setToolbarOnTouchListener();
        setSliderListener();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        layoutHeight = displaymetrics.heightPixels;
    }

    private void setToolbarOnTouchListener() {
        poiToolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int y = (int) event.getRawY();
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                    int toolbarHeight = poiToolbar.getHeight();
                    int panelHeight = layoutHeight - y + toolbarHeight / 2;
                    if (panelHeight > layoutHeight - toolbarHeight) {
                        panelHeight = layoutHeight - toolbarHeight;
                    }
                    if (panelHeight < 0) {
                        panelHeight = 0;
                    }
                    poiPreviewLayout.setPanelHeight(panelHeight);
                }
                return true;
            }
        });
    }

    private void setSliderListener() {
        poiPreviewLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                resizeScrollView(view, v);
            }

            @Override
            public void onPanelCollapsed(View view) {

            }

            @Override
            public void onPanelExpanded(View view) {
                resizeScrollView(view, 0.0f);
            }

            @Override
            public void onPanelAnchored(View view) {
                resizeScrollView(view, 0.0f);
            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
    }

    private void resizeScrollView(View panel, float slideOffset) {
        // The scrollViewHeight calculation would need to change based on
        // what views you have in your sliding panel. The calculation below
        // works because your layout has 2 child views.
        // 1) The row with the drag view which is layout.getPanelHeight() high.
        // 2) The ScrollView.
        final int scrollViewHeight =
                (int) ((panel.getHeight() - poiPreviewLayout.getPanelHeight()) * (1.0f - slideOffset));
        scrollView.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        scrollViewHeight));
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int poiIdIndex = cursor.getColumnIndex(Poi.POI_ID);
        int nameIndex = cursor.getColumnIndex(Poi.NAME);
        int categoryIndex = cursor.getColumnIndex(Poi.CATEGORY);
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
                }

                // String category = cursor.getString(categoryIndex);      to implement when we will have UI
                Log.v(TAG, "Loaded");
            } while (cursor.moveToNext());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(userPositionMarker)) {
            return true;
        }
        setPoiPreviewInfo(marker);
        poiPreviewLayout.setPanelHeight(DEFAULT_POI_PANEL_HEIGHT);
        return false;
    }

    //Most data here is only for testing purposes
    private void setPoiPreviewInfo(Marker marker) {
        View poiPreviewView = rootView.findViewById(R.id.poiPreviewLayout);

        TextView category = (TextView) poiPreviewView.findViewById(R.id.categoryTextView);

        category.setText("Kategoria"); //Hardcoded - uzywane tylko do testow
        TextView name = (TextView) poiPreviewView.findViewById(R.id.nameTextView);
        name.setText(marker.getTitle());
        TextView description = (TextView) poiPreviewView.findViewById(R.id.descriptionTextView);


        String position = "";
        position += "Longitude: " + marker.getPosition().longitude; //Hardcoded - uzywane tylko do testow
        position += "\nLatitude: " + marker.getPosition().latitude; //Hardcoded - uzywane tylko do testow
        description.setText(position);

        ImageView image = (ImageView) poiPreviewView.findViewById(R.id.imageView);
        image.setImageResource(R.drawable.splash);
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
        }
        if (!isCameraSet) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPositionMarker.getPosition(), ZOOM));
            isCameraSet = true;
        }

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
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
        poiPreviewLayout.setPanelHeight(0);
    }

    public void setMarker() {
        if (markerTarget == null) {
           googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPositionMarker.getPosition(), ZOOM));
        }
        else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerTarget.getPosition(), ZOOM));
        }
    }
}

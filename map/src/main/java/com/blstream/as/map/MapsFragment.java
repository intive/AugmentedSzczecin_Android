package com.blstream.as.map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.rest.model.Poi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MapsFragment.class.getSimpleName();
    private static final float ZOOM = 14;

    private static GoogleMap googleMap;
    private static HashMap<String, Marker> markerHashMap = new HashMap<>();

    private Button homeButton;
    private Button arButton;
    private Callbacks activityConnector;

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    public interface Callbacks {
        public void switchToAr();
        public boolean isUserLogged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        getLoaderManager().restartLoader(0, null, this);
        setUpMapIfNeeded();
        setButtons(rootView);
        if (!activityConnector.isUserLogged()) {
            disableButtons();
        }
        else {
            setButtonsListeners();
        }

        return rootView;
    }

    private void disableButtons() {
        arButton.setVisibility(View.INVISIBLE);
        homeButton.setVisibility(View.INVISIBLE);
    }

    private void setButtonsListeners() {
        setArButtonListener();
        setHomeButtonListener();
    }

    private void setArButtonListener() {
        arButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                activityConnector.switchToAr();
            }
        });
    }

    private void setHomeButtonListener() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                FragmentManager fragmentManager = getFragmentManager();
                int count = fragmentManager.getBackStackEntryCount();
                for(int i = 0; i < count; ++i) {
                    fragmentManager.popBackStack();
                }
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
            setUpMap();
        }
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.v(TAG, "GPS enabled");
        } else {
            Log.v(TAG, "GPS disabled");
        }
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "Starting loading");
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Poi.class, null),
                null, null, null, null
        );
    }

    /**
     * @param poiId Poi id on server,
     * @return Marker created from Poi with given ID, or null if there is not such marker
     */
    public static Marker getMarkerFromPoiId(String poiId) {
        if (markerHashMap != null) {
            return markerHashMap.get(poiId);
        }else{
            return null;
        }
    }

    public static void moveToMarker(Marker marker){
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM));
        }
        marker.showInfoWindow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment fragment = (getChildFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
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
    public void onLoaderReset(Loader<Cursor> loader) {
    }



}

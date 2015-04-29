package com.blstream.as.maps2d;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.PoiPreviewLayout;
import com.blstream.as.R;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.fragment.PreviewPoiFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MapsFragment.class.getSimpleName();
    public static final String gpsWarningDialogTitle = "GPS Warning Dialog";

    private static final int POI_PREVIEW_DEFAULT_HEIGHT = 300;

    private PoiPreviewLayout slidingUpPanelLayout;

    private PoiMapActivity activity; //FIXME Change to interface
    private static GoogleMap googleMap;
    private static HashMap<String, Marker> markerHashMap = new HashMap<>();

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        getLoaderManager().initLoader(0, null, this);
        setUpMapIfNeeded();
        setPoiPreviewButton();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (PoiMapActivity) activity; //FIXME Change to interface
    }

    private void setPoiPreviewButton() {
        Button poiButton = (Button) getActivity().findViewById(R.id.previewPoiButton);
        slidingUpPanelLayout = (PoiPreviewLayout) getActivity().findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(false);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingUpPanelLayout.setPanelHeight(0);
        slidingUpPanelLayout.setOverlayed(false);

        poiButton.setOnClickListener(poiButtonClick);
    }

    private View.OnClickListener poiButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            slidingUpPanelLayout.setPanelHeight(POI_PREVIEW_DEFAULT_HEIGHT);
            slidingUpPanelLayout.setPoiActivated(true);
        }
    };

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(mapClick);
        Log.v(TAG, String.valueOf(activity.getMarkerList().size()));

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.v(TAG, "GPS enabled");
        } else runGpsWarningDialog();

    }

    private GoogleMap.OnMapClickListener mapClick = new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng latLng) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            slidingUpPanelLayout.setPanelHeight(0);
            slidingUpPanelLayout.setPoiActivated(false);
        }
    };

    private void runGpsWarningDialog() {
        FragmentManager gpsWarningDialogFragmentManager =
                getFragmentManager();
        new GpsWarningDialog().
                show(gpsWarningDialogFragmentManager, gpsWarningDialogTitle);
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
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));
        }
        marker.showInfoWindow();
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

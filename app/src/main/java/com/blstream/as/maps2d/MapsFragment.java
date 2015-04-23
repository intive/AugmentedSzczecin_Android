package com.blstream.as.maps2d;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.R;
import com.blstream.as.data.rest.model.Poi;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MapsFragment.class.getSimpleName();
    public static final String gpsWarningDialogTitle = "GPS Warning Dialog";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PoiMapActivity activity; //FIXME Change to interface
    private GoogleMap googleMap;

    public static MapsFragment newInstance(int sectionNumber) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        getLoaderManager().initLoader(0, null, this);
        setUpMapIfNeeded();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (PoiMapActivity) activity; //FIXME Change to interface
        this.activity.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }


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
        Log.v(TAG, String.valueOf(activity.getMarkerList().size()));

        LocationManager lm =(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.v(TAG,"GPS enabled");
        }else runGpsWarningDialog();

    }

    private void runGpsWarningDialog(){
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(TAG, "Loaded");
        cursor.moveToFirst();

        StringBuilder res=new StringBuilder();
        while (!cursor.isAfterLast()) {
            googleMap.addMarker(new MarkerOptions().
                            position(getLocation(cursor)).
                            title(cursor.getString(3)).
                            snippet(cursor.getString(1))
            );

                cursor.moveToNext();

        }

        Log.e(TAG, res.toString());
    }

    private LatLng getLocation (Cursor cursor){
        return new LatLng(Double.valueOf(cursor.getString(4)), Double.valueOf(cursor.getString(4)));
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

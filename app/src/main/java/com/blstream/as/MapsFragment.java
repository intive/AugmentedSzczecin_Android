package com.blstream.as;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PoiMapActivity activity; //FIXME Change to interface
    GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    public MapsFragment() {
    }

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
        setUpMapIfNeeded();
        googleMap.setMyLocationEnabled(true);
        createMarkerMap();
//            checkLocationService();
        return rootView;
    }

    @Override
    public void onResume() {
        createMarkerMap();
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (PoiMapActivity) activity; //FIXME Change to interface
        this.activity.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
//        createMarkerMap(); //FIXME Map is null here
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        for (double i = 0; i < 300; i++) {
            activity.setMarkerList(new MarkerOptions()                      //tylko do debugowania
                    .position(new LatLng(53.395344 + (i / 5000), 14.5500801 - Math.sin(10 * i) / 20))
                    .title("Marker " + i));
        }
        Log.v("Marker", "List filled with Markers " + activity.getMarkerList().size());
    }

    boolean checkLocationService() {

        String locationProviders = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {

            Toast.makeText(activity, "GPS is turned off!!", Toast.LENGTH_LONG)
                    .show();
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return false;
        } else {

        }
        return true;
    }

    void createMarkerMap() {

        Log.i("Marker", "Tworze markery" + String.valueOf(activity.getMarkerList().size()));
        for (int i = 0; i < activity.getMarkerList().size(); i++) {
            this.googleMap.addMarker(activity.getMarkerList().get(i));
        }
    }

    void addNewMarker(MarkerOptions markerOptions) {
        this.googleMap.addMarker(markerOptions);
    }
}

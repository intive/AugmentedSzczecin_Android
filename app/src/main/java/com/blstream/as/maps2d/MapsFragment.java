package com.blstream.as.maps2d;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blstream.as.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsFragment extends Fragment {

    static final String TAG = MapsFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PoiMapActivity activity; //FIXME Change to interface
    private GoogleMap googleMap;

    public MapsFragment() {
    }

    //FIXME Remove parameter if not used
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
        googleMap.setMyLocationEnabled(true); //FIXME Possibility of NPE
        createMarkerMap();
//            checkLocationService();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (PoiMapActivity) activity; //FIXME Change to interface
        this.activity.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
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
        Log.v(TAG,String.valueOf(activity.getMarkerList().size()));
    }

    boolean checkLocationService() {

        String locationProviders = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {

            //TODO Show dialog and ask about go to settings
            Toast.makeText(activity, "GPS is turned off!!", Toast.LENGTH_LONG)
                    .show();
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return false;
        } else {
            //FIXME ??
        }
        return true;
    }

    void createMarkerMap() {
        Log.i(TAG, String.valueOf(activity.getMarkerList().size()));
        for (int i = 0; i < activity.getMarkerList().size(); i++) {
            this.googleMap.addMarker(activity.getMarkerList().get(i));
        }
    }

}

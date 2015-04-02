package com.blstream.as;

import android.app.Activity;
import android.content.Intent;
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

        GoogleMap googleMap; // Might be null if Google Play services APK is not available.
        BaseActivity baseActivity = new BaseActivity();
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static MapsFragment newInstance(int sectionNumber) {
            MapsFragment fragment = new MapsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public MapsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_base, container, false);
            setUpMapIfNeeded();
            googleMap.setMyLocationEnabled(true);
            refreshGoogleMap();
//            checkLocationService();
            return rootView;
        }

    @Override
    public void onResume() {
        refreshGoogleMap();
        super.onResume();
    }

    @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((BaseActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        refreshGoogleMap();
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
            for(double i=0; i<300; i++){
                baseActivity.setMarkerList(new MarkerOptions()                      //tylko do debugowania
                        .position(new LatLng(53.395344 + (i/5000), 14.5500801 - Math.sin(10*i)/20))
                        .title("Marker " + i));
            }
            Log.v("Marker", "List filled with Markers " + baseActivity.getMarkerList().size());
        }

        boolean checkLocationService(){
            BaseActivity baseActivity = new BaseActivity();
            String locationProviders = Settings.Secure.getString(baseActivity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (locationProviders == null || locationProviders.equals("")) {

                Toast.makeText(baseActivity, "GPS is turned off!!", Toast.LENGTH_LONG)
                        .show();
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return false;
            }

            else {

            }
            return true;
        }
            void refreshGoogleMap(){

            Log.i("Marker",String.valueOf(baseActivity.getMarkerList().size()));
            for(int i=0; i<baseActivity.getMarkerList().size(); i++){
                this.googleMap.addMarker(baseActivity.getMarkerList().get(i));
            }
        }
    }

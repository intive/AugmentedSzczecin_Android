package com.blstream.as;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

    public class MapsFragment extends Fragment {

        private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

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
            checkLocationService();
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((BaseActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
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
            googleMap.addMarker(new MarkerOptions().position(new LatLng(53.424744, 14.5500801)).title("Marker"));
        }

        void checkLocationService(){
            BaseActivity baseActivity = new BaseActivity();
            String locationProviders = Settings.Secure.getString(baseActivity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (locationProviders == null || locationProviders.equals("")) {

                Toast.makeText(baseActivity, "GPS is turned off!!", Toast.LENGTH_LONG)
                        .show();

//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            else {

            }
        }
    }

package com.blstream.as.maps2d;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.blstream.as.LoginUtils;
import com.blstream.as.OnPoiAdd;
import com.blstream.as.R;
import com.blstream.as.ar.ArFragment;
import com.blstream.as.data.fragments.PoiFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class PoiMapActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnPoiAdd,
        ArFragment.Callbacks,
        PoiFragment.OnPoiSelectedListener {

    public static final String mockDialogTitle = "mock";
    private static final int MAP2D = 0;
    private static final int AR = 1;
    private static final int POI_LIST = 2;
    private static final int LOGOUT = 3;


    List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
    private NavigationDrawerFragment navigationDrawerFragment;
    private Toolbar toolbar;
    private CharSequence navigationDrawerTitle;


    private SharedPreferences pref;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (LoginUtils.isUserLogged(PoiMapActivity.this)) {
            navigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            navigationDrawerTitle = getTitle();

            navigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    public List<MarkerOptions> getMarkerList() {
        return markerList;
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String[] titleSection = getResources().getStringArray(R.array.title_section);
        navigationDrawerTitle = titleSection[position];
        switch (position) {
            case MAP2D:
                switchToMaps2D();
                break;
            case AR:
                if (fragmentManager.findFragmentByTag(MapsFragment.TAG) != null) {
                    getSupportFragmentManager().popBackStack(ArFragment.TAG,
                            0);
                } else {
                    fragmentTransaction
                            .replace(R.id.container, ArFragment.newInstance())
                            .addToBackStack(ArFragment.TAG)
                            .commit();
                }
                break;
            case POI_LIST:
                if (fragmentManager.findFragmentByTag(PoiFragment.TAG) != null) {
                    getSupportFragmentManager().popBackStack(PoiFragment.TAG,
                            0);
                } else {
                    fragmentTransaction
                            .replace(R.id.container, PoiFragment.newInstance())
                            .addToBackStack(PoiFragment.TAG)
                            .commit();
                }
                break;

            case LOGOUT:
                logout();
                break;
        }
    }

    public void restoreToolBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(navigationDrawerTitle);

    }

    public void logout() {
        pref = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(USER_EMAIL);
        editor.remove(USER_PASS);
        editor.putBoolean(USER_LOGIN_STATUS, false);
        editor.apply();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (LoginUtils.isUserLogged(PoiMapActivity.this)) {
            if (!navigationDrawerFragment.isDrawerOpen()) {
                // Only show items in the action bar relevant to this screen
                // if the drawer is not showing. Otherwise, let the drawer
                // decide what to show in the action bar.
                getMenuInflater().inflate(R.menu.base, menu);
                restoreToolBar();
                return true;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_example) {
            FragmentManager dialogFragmentManager = getSupportFragmentManager();
            MockDialog mockDialog = new MockDialog();
            mockDialog.show(dialogFragmentManager, "mock"); //FIXME Move tag to constant
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void sendPOIfromDialog(MarkerOptions dialogMarkerOption) {

        GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        googleMap.addMarker(dialogMarkerOption);

    }

    @Override
    public void switchToMaps2D() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(MapsFragment.TAG) != null) {
            fragmentManager.popBackStack(MapsFragment.TAG, 0);
        } else {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, MapsFragment.newInstance())
                    .addToBackStack(MapsFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void goToMarker(String poiId) {
        onNavigationDrawerItemSelected(MAP2D); //change fragment, avoid duplication of code
        Marker marker = MapsFragment.getMarkerFromPoiId(poiId);
        if (marker != null) {
            MapsFragment.moveToMarker(marker);
        }
    }
}

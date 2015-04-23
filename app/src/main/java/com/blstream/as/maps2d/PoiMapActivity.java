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

import com.blstream.as.ArFragment;
import com.blstream.as.OnPoiAdd;
import com.blstream.as.R;
import com.blstream.as.data.fragments.PoiFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class PoiMapActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnPoiAdd {

    public static final String mockDialogTitle= "mock";

    List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
    private NavigationDrawerFragment navigationDrawerFragment;
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

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerTitle = getTitle();

        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        pref = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
    }

    public List<MarkerOptions> getMarkerList(){
        return markerList;
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MapsFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (number) {
            case 1:
                navigationDrawerTitle = getString(R.string.title_section1);
                break;
            case 2:
                navigationDrawerTitle = getString(R.string.title_section2);

                ArFragment arFragment = ArFragment.newInstance();
                fragmentTransaction.replace(R.id.container, arFragment);
                fragmentTransaction.commit();

                break;
            case 3:
                navigationDrawerTitle = getString(R.string.title_section3);

                PoiFragment fragment = PoiFragment.newInstance();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
                break;

            case 4:
                navigationDrawerTitle = getString(R.string.title_section4);

                //FIXME Move to method
                SharedPreferences.Editor editor = pref.edit();
                editor.remove(USER_EMAIL);
                editor.remove(USER_PASS);
                editor.putBoolean(USER_LOGIN_STATUS, false);
                editor.apply();

                finish();
                break;
        }
    }

    public void restoreToolBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(navigationDrawerTitle);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.base, menu);
            restoreToolBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_example) {
            FragmentManager dialogFragmentManager = getSupportFragmentManager();
            MockDialog mockDialog = new MockDialog();
            mockDialog.show(dialogFragmentManager, mockDialogTitle);
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
}

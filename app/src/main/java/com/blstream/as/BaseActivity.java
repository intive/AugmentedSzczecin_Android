package com.blstream.as;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, addPOI {

    List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
    private NavigationDrawerFragment navigationDrawerFragment;
    private CharSequence navigationDrawerTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navigationDrawerTitle = getTitle();

        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    public List<MarkerOptions> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(MarkerOptions markerOptions) {
        this.markerList.add(markerOptions);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MapsFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                navigationDrawerTitle = getString(R.string.title_section1);
                break;
            case 2:
                navigationDrawerTitle = getString(R.string.title_section2);
                break;
            case 3:
                navigationDrawerTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(navigationDrawerTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.base, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_example) {
            FragmentManager dialogFragmentManager = getSupportFragmentManager();
            MockDialog mockDialog = new MockDialog();
            mockDialog.show(dialogFragmentManager, "mock");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void sendPOIfromDialog(MarkerOptions dialogMarkerOption) {

        GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        googleMap.addMarker(dialogMarkerOption);
/*        MapsFragment mapsFragment = (MapsFragment)getSupportFragmentManager().findFragmentByTag("");
        mapsFragment.addNewMarker(dialogMarkerOption);*/
    }
}

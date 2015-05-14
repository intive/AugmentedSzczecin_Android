package com.blstream.as;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blstream.as.ar.ArFragment;
import com.blstream.as.data.fragments.PoiFragment;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.dialogs.AddPoiDialog;
import com.blstream.as.dialogs.ConfirmAddPoiWindow;
import com.blstream.as.dialogs.SettingsDialog;
import com.blstream.as.fragment.HomeFragment;
import com.blstream.as.fragment.NavigationDrawerFragment;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.maps.model.Marker;

public class HomeActivity extends ActionBarActivity implements
        ArFragment.Callbacks,
        MapsFragment.Callbacks,
        PoiFragment.OnPoiSelectedListener,
        HomeFragment.Callbacks,
        NetworkStateReceiver.NetworkStateReceiverListener,
        AddPoiDialog.OnAddPoiListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    public final static String TAG = HomeActivity.class.getSimpleName();

    private MapsFragment mapsFragment;
    private NetworkStateReceiver networkStateReceiver;
    private Toolbar toolbar;

    private static ConfirmAddPoiWindow confirmAddPoiWindow;
    private static final int X_OFFSET = 0;
    private static final int Y_OFFSET = 100;

    private SharedPreferences pref;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    private enum FragmentType {
        MAP_2D, AR, POI_LIST, HOME
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Server.getPoiList();
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        setViews();
        switchToMaps2D();
        centerOnUserPosition();
    }

    private void setViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (navigationDrawerFragment != null) {
            navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    @Override
    public void switchToMaps2D() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        switchFragment(FragmentType.MAP_2D);
    }

    @Override
    public void switchToPoiList() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        switchFragment(FragmentType.POI_LIST);
    }

    @Override
    public void switchToLogout() {
        if (LoginUtils.isUserLogged(this)) {
            pref = getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove(USER_EMAIL);
            editor.remove(USER_PASS);
            editor.putBoolean(USER_LOGIN_STATUS, false);
            editor.apply();
        }
        finish();
    }

    @Override
    public void switchToSettings() {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setCancelable(true);
        settingsDialog.show(getSupportFragmentManager(), SettingsDialog.TAG);
    }

    @Override
    public void centerOnUserPosition() {
        if (mapsFragment != null) {
            mapsFragment.setMarkerTarget(null);
            mapsFragment.moveToActiveMarker();
        }
    }

    @Override
    public void switchToAr() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        switchFragment(FragmentType.AR);
    }

    @Override
    public void switchToHome() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        switchFragment(FragmentType.HOME);
    }

    @Override
    public void gpsLost() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.gps_lost_title)
                .setMessage(R.string.gps_lost_description)
                .setPositiveButton(R.string.wifi_lost_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.wifi_lost_settings, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean isUserLogged() {
        return LoginUtils.isUserLogged(this);
    }

    @Override
    public void switchToPoiAdd() {
        switchToMaps2D();
        centerOnUserPosition();
        if (mapsFragment != null) {
            mapsFragment.setAddingPoi(true);
        }
    }

    @Override
    public void switchToMap() {
        switchToMaps2D();
        centerOnUserPosition();
    }

    @Override
    public void showConfirmPoiWindow(Marker marker) {
        if (findViewById(R.id.confirm_poi_dialog) == null) {
            LayoutInflater layoutInflater
                    = (LayoutInflater) getBaseContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.confirm_add_poi, new LinearLayout(this), false);

            confirmAddPoiWindow = new ConfirmAddPoiWindow(getSupportFragmentManager(), popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            confirmAddPoiWindow.showAtLocation(findViewById(R.id.container), Gravity.CENTER, X_OFFSET, Y_OFFSET);
        }
    }

    @Override
    public void dismissConfirmAddPoiWindow() {
        if (confirmAddPoiWindow != null) {
            confirmAddPoiWindow.dismiss();
        }
    }

    @Override
    public void goToMarker(String poiId) {
        switchToMaps2D();
        if (mapsFragment != null) {
            mapsFragment.moveToMarker(MapsFragment.getMarkerFromPoiId(poiId));
        }
    }

    @Override
    public void networkAvailable() {
        Log.v(TAG, "Internet dostepny!");
    }

    @Override
    public void networkUnavailable() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_lost_title)
                .setMessage(R.string.network_lost_description)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void wifiOr3gConnected() {
        Log.v(TAG, "Wifi lub 3G podlaczane!");
    }

    @Override
    public void wifiOr3gDisconnected() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.wifi_lost_title)
                .setMessage(R.string.wifi_lost_description)
                .setPositiveButton(R.string.wifi_lost_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.wifi_lost_settings, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showAddPoiResultMessage(Boolean state) {
        if (state) {
            Toast.makeText(this, getString(R.string.add_poi_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.add_poi_missing_title), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        toolbar.setVisibility(View.VISIBLE);
        if (isLastFragmentOnStack()) {
            switchToHome();
        }
        else {
            FragmentManager.BackStackEntry backStackEntry = getSecondFragmentOnStack();
            String fragmentName = backStackEntry.getName();
            if (fragmentName.equals(MapsFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                toolbar.setTitle(R.string.map_2d);
            }
            else if (fragmentName.equals(ArFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                toolbar.setVisibility(View.GONE);
            }
            else if (fragmentName.equals(HomeFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                toolbar.setTitle(R.string.home_screen);
            }
            else if (fragmentName.equals(PoiFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                toolbar.setTitle(R.string.poi_list);
            }
            super.onBackPressed();
        }
    }

    private FragmentManager.BackStackEntry getSecondFragmentOnStack() {
        FragmentManager fragmentManager = getSupportFragmentManager(); // FIXME: wiele razy wywolujesz ta linijke kodu za kazdym razem tworzac nowy obiekt, zrob z tego pole klasy
        return fragmentManager.getBackStackEntryAt(getBackStackEntryCount() - 2);
    }

    private boolean isLastFragmentOnStack() {
        return (getBackStackEntryCount() == 1);
    }

    private int getBackStackEntryCount() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.getBackStackEntryCount();
    }

    private void switchFragment(FragmentType fragmentType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        toolbar.setVisibility(View.VISIBLE);

        switch (fragmentType) {
            case MAP_2D:
                toolbar.setTitle(R.string.map_2d);
                if (mapsFragment == null) {
                    mapsFragment = (MapsFragment) fragmentManager.findFragmentByTag(MapsFragment.TAG);
                }
                if (mapsFragment == null) {
                    mapsFragment = MapsFragment.newInstance();
                    fragmentTransaction.replace(R.id.container, mapsFragment, MapsFragment.TAG);
                    fragmentTransaction.addToBackStack(MapsFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(MapsFragment.TAG, 0);
                }
                break;
            case AR:
                toolbar.setVisibility(View.GONE);
                if (fragmentManager.findFragmentByTag(ArFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, ArFragment.newInstance(), ArFragment.TAG);
                    fragmentTransaction.addToBackStack(ArFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(ArFragment.TAG, 0);
                }
                break;
            case POI_LIST:
                toolbar.setTitle(R.string.poi_list);
                if (fragmentManager.findFragmentByTag(PoiFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, PoiFragment.newInstance(), PoiFragment.TAG);
                    fragmentTransaction.addToBackStack(PoiFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(PoiFragment.TAG, 0);
                }
                break;
            case HOME:
                toolbar.setTitle(R.string.home_screen);
                if (fragmentManager.findFragmentByTag(HomeFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, HomeFragment.newInstance(), HomeFragment.TAG);
                    fragmentTransaction.addToBackStack(HomeFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(HomeFragment.TAG, 0);
                }
                break;
        }
    }
}

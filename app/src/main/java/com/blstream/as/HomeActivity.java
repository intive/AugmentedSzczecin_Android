package com.blstream.as;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blstream.as.ar.ArFragment;
import com.blstream.as.data.fragments.PoiListFragment;
import com.blstream.as.data.rest.model.SearchResult;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.dialogs.AddOrEditPoiDialog;
import com.blstream.as.dialogs.ConfirmAddPoiWindow;
import com.blstream.as.dialogs.ConfirmDeletePoiDialog;
import com.blstream.as.dialogs.FilterListDialog;
import com.blstream.as.dialogs.SettingsDialog;
import com.blstream.as.fragment.NavigationDrawerFragment;
import com.blstream.as.fragment.PoiSearchFragment;
import com.blstream.as.fragment.PreviewPoiFragment;
import com.blstream.as.fragment.SearchResultsFragment;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class HomeActivity extends ActionBarActivity implements
        ArFragment.Callbacks,
        MapsFragment.Callbacks,
        PoiListFragment.OnPoiSelectedListener,
        NetworkStateReceiver.NetworkStateReceiverListener,
        AddOrEditPoiDialog.OnAddPoiListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        PreviewPoiFragment.Callbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        AdapterView.OnItemClickListener,
        PopupWindow.OnDismissListener,
        PoiSearchFragment.PoiSearchListener,
        SearchResultsFragment.OnPoiSelectedListener {

    public final static String TAG = HomeActivity.class.getSimpleName();

    private MapsFragment mapsFragment;
    private NetworkStateReceiver networkStateReceiver;
    private Toolbar toolbar;
    private FilterListDialog filterListDialog;
    private FragmentManager fragmentManager;

    private static ConfirmAddPoiWindow confirmAddPoiWindow;
    private static final int X_OFFSET = 0;
    private static final int Y_OFFSET = 100;

    private SharedPreferences pref;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";

    private static final int DEFAULT_FULL_PANEL_HEIGHT = 600;
    private static final int PANEL_HIDDEN = 0;
    private static final int TRANSPARENT_TOOLBAR = 0;
    private static final int NO_TRANSPARENT_TOOLBAR = 255;

    private DisplayMetrics displayMetrics;
    private SlidingUpPanelLayout poiPreviewLayout;
    private boolean isPanelFullExpand;
    private LinearLayout poiPreviewHeader;
    private LinearLayout poiPreviewToolbar;
    private NavigationDrawerFragment navigationDrawerFragment;
    private PreviewPoiFragment previewPoiFragment;

    private AlertDialog internetConnectionLostDialog;
    private AlertDialog wifiOr3gConnectionDialog;
    private AlertDialog gpsLostDialog;
    private AlertDialog unknownLocationDialog;

    private GoogleApiClient googleApiClient;
    private float fullPoiPreviewHeight;
    private float semiPoiPreviewHeight;

    private ImageView searchImageView;
    private TextView filterButton;

    public enum FragmentType {
        MAP_2D, POI_LIST, ADD_POI, SETTINGS, LOGOUT, SEARCH
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Server.refreshPoiList();
        fragmentManager = getSupportFragmentManager();
        displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        createSliderUp();
        createGoogleApiClient();
        setViews();
        switchToMaps2D();
        centerOnUserPosition();
    }

    private void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        filterListDialog = new FilterListDialog(this);
        filterButton = (TextView) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterListDialog.show(v);
            }
        });
        setSearchIcon();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (navigationDrawerFragment != null) {
            navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    public void setSearchIcon(){
        searchImageView = (ImageView)findViewById(R.id.search_icon);
        if (LoginUtils.isUserLogged(this)){
            searchImageView.setVisibility(View.VISIBLE);
            setSearchListener();
        }
        else {
            searchImageView.setVisibility(View.GONE);
        }
    }

    public void setSearchListener(){
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToPoiSearch();
            }
        });
    }

    public void setStatusBarColour(int colour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(colour));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        filterListDialog.checkItem(i, view);
    }

    @Override
    public void onDismiss() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof MapsFragment) {
            MapsFragment mapsFragment = (MapsFragment) fragment;
            mapsFragment.restartLoader();
        }
        if (fragment instanceof ArFragment) {
            ArFragment arFragment = (ArFragment) fragment;
            arFragment.restartLoader();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (networkStateReceiver == null) {
            networkStateReceiver = new NetworkStateReceiver();
            networkStateReceiver.addListener(this);
            registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof MapsFragment) {
            MapsFragment mapsFragment = (MapsFragment) fragment;
            mapsFragment.setUpLocation();
        }
        if (fragment instanceof ArFragment) {
            ArFragment arFragment = (ArFragment) fragment;
            arFragment.enableAugmentedReality();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void showLocationUnavailable() {
        if (unknownLocationDialog == null) {
            unknownLocationDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.lastLocationTitle)
                    .setMessage(R.string.unknownLastLocationMessage)
                    .setPositiveButton(R.string.dialogContinue, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            unknownLocationDialog = null;
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void createSliderUp() {
        poiPreviewLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingUpPanel);
        poiPreviewLayout.setTouchEnabled(false);
        poiPreviewLayout.setOverlayed(true);
        poiPreviewLayout.setPanelHeight(PANEL_HIDDEN);
        setSliderUpListener();
    }

    @Override
    public void switchToMaps2D() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        switchFragment(FragmentType.MAP_2D);
        createPoiPreviewFragment();

    }

    public void switchToPoiSearch(){
        switchFragment(FragmentType.SEARCH);
    }

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
        hidePoiPreview();
        setStatusBarColour(R.color.transparent);
        cancelNavigation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        if (googleApiClient != null && googleApiClient.isConnected()) {
            if (fragmentManager.findFragmentByTag(ArFragment.TAG) == null) {
                toolbar.getBackground().setAlpha(TRANSPARENT_TOOLBAR);
                filterButton.setVisibility(View.VISIBLE);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, ArFragment.newInstance(googleApiClient, filterListDialog.getSelectedItems()), ArFragment.TAG);
                fragmentTransaction.addToBackStack(ArFragment.TAG);
                fragmentTransaction.commit();
            } else {
                getSupportFragmentManager().popBackStack(ArFragment.TAG, 0);
            }
        }
    }

    public void switchToPoiAdd() {
        switchToMaps2D();
        centerOnUserPosition();
        if (mapsFragment != null) {
            mapsFragment.setPoiAddingMode(true);
        }
    }

    @Override
    public void showConfirmPoiWindow(Marker marker) {
        if (findViewById(R.id.confirm_poi_dialog) == null) {
            LayoutInflater layoutInflater
                    = (LayoutInflater) getBaseContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.confirm_add_poi, new LinearLayout(this), false);

            confirmAddPoiWindow = new ConfirmAddPoiWindow(getSupportFragmentManager(), marker, popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    getApplicationContext());
            confirmAddPoiWindow.showAtLocation(findViewById(R.id.container), Gravity.CENTER, X_OFFSET, Y_OFFSET);
        }
    }

    @Override
    public void confirmDeletePoi(String poiId) {
        ConfirmDeletePoiDialog deletePoiDialog = ConfirmDeletePoiDialog.newInstance(this, poiId);
        deletePoiDialog.show(getSupportFragmentManager(), ConfirmDeletePoiDialog.TAG);
    }

    @Override
    public void navigateToPoi(String poiId) {
        switchToMaps2D();
        centerOnUserPosition();
        if (mapsFragment != null) {
            mapsFragment.navigateToPoi(poiId);
        }
    }

    @Override
    public void cancelNavigation() {
        switchToMaps2D();
        if (mapsFragment != null) {
            mapsFragment.cancelNavigation();
        }
        if (previewPoiFragment != null) {
            previewPoiFragment.cancelNavigation();
        }
    }

    @Override
    public void deletePoi(String poiId) {
        mapsFragment.deletePoi(poiId);
        Toast.makeText(this, getString(R.string.poi_was_deleted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEditPoiWindow(Marker marker) {
        AddOrEditPoiDialog editPoiDialog = AddOrEditPoiDialog.newInstance(marker, true, getApplicationContext());
        editPoiDialog.show(getSupportFragmentManager(), AddOrEditPoiDialog.TAG);
    }

    @Override
    public void dismissConfirmAddPoiWindow() {
        if (confirmAddPoiWindow != null) {
            confirmAddPoiWindow.dismiss();
        }
    }

    @Override
    public void setPoiPreviewHeader(LinearLayout poiPreviewHeader) {
        this.poiPreviewHeader = poiPreviewHeader;
        setPoiPreviewHeaderListener();
    }

    @Override
    public void setPoiPreviewToolbar(LinearLayout poiPreviewToolbar) {
        this.poiPreviewToolbar = poiPreviewToolbar;
    }

    private void setPoiPreviewHeaderListener() {
        poiPreviewHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isPanelFullExpand) {
                    collapsePoiPreview();
                } else {
                    expandPoiPreview();
                }
                return true;
            }
        });
    }

    public void expandPoiPreview() {
        fullPoiPreviewHeight = DEFAULT_FULL_PANEL_HEIGHT / (float) displayMetrics.heightPixels;
        poiPreviewLayout.setAnchorPoint(fullPoiPreviewHeight);
        poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void collapsePoiPreview() {
        poiPreviewLayout.setAnchorPoint(semiPoiPreviewHeight);
        poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        isPanelFullExpand = false;
    }

    @Override
    public void showPoiPreview(Marker marker) {
        if (poiPreviewLayout != null) {
            semiPoiPreviewHeight = (poiPreviewHeader.getHeight() + poiPreviewToolbar.getHeight()) / (float) displayMetrics.heightPixels;
            poiPreviewLayout.setAnchorPoint(semiPoiPreviewHeight);
            poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            isPanelFullExpand = false;
            FragmentManager fragmentManager = getSupportFragmentManager();
            previewPoiFragment = (PreviewPoiFragment) fragmentManager.findFragmentByTag(PreviewPoiFragment.TAG);
            if (previewPoiFragment != null) {
                previewPoiFragment.loadPoi(marker, MapsFragment.getPoiIdFromMarker(marker));
            }
        }
    }

    @Override
    public void hidePoiPreview() {
        if (poiPreviewLayout != null) {
            poiPreviewLayout.setAnchorPoint(PANEL_HIDDEN);
            poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            isPanelFullExpand = false;
        }
    }

    private void setSliderUpListener() {
        poiPreviewLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelExpanded(View panel) {
                isPanelFullExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
    }

    @Override
    public void goToMarker(String poiId) {
        switchToMaps2D();
        if (mapsFragment != null) {
            Marker marker = MapsFragment.getMarkerFromPoiId(poiId);
            mapsFragment.moveToMarker(marker);
            showPoiPreview(marker);
        }
    }

    @Override
    public void networkAvailable() {
        if (internetConnectionLostDialog != null) {
            internetConnectionLostDialog.dismiss();
            internetConnectionLostDialog = null;
        }
    }

    @Override
    public void networkUnavailable() {
        if (internetConnectionLostDialog == null) {
            internetConnectionLostDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.network_lost_title)
                    .setMessage(R.string.network_lost_description)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            internetConnectionLostDialog = null;
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public void showLocationServicesUnavailable() {
        if (gpsLostDialog == null) {
            gpsLostDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.gps_lost_title)
                    .setMessage(R.string.gps_lost_description)
                    .setPositiveButton(R.string.gps_lost_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            gpsLostDialog = null;
                        }
                    })
                    .setNegativeButton(R.string.gps_lost_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            gpsLostDialog = null;
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void showLocationServicesAvailable() {
        if (gpsLostDialog != null) {
            gpsLostDialog.dismiss();
            gpsLostDialog = null;
        }
    }

    @Override
    public void wifiOr3gConnected() {
        if (wifiOr3gConnectionDialog != null) {
            wifiOr3gConnectionDialog.dismiss();
            wifiOr3gConnectionDialog = null;
        }
    }

    @Override
    public void wifiOr3gDisconnected() {
        if (wifiOr3gConnectionDialog == null) {
            wifiOr3gConnectionDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.wifi_lost_title)
                    .setMessage(R.string.wifi_lost_description)
                    .setPositiveButton(R.string.wifi_lost_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            wifiOr3gConnectionDialog = null;
                        }
                    })
                    .setNegativeButton(R.string.wifi_lost_settings, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                            dialog.cancel();
                            wifiOr3gConnectionDialog = null;

                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void showAddPoiResultMessage(Boolean state, String wrongFields) {
        if (state) {
            Toast.makeText(this, getString(R.string.add_poi_success), Toast.LENGTH_SHORT).show();
        } else {
            String message = String.format(getString(R.string.add_poi_missing_fields), wrongFields);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment != null && navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else if (isPanelFullExpand) {
            collapsePoiPreview();
        } else if (isLastFragmentOnStack()) {
            switchToLogout();
        } else {
            FragmentManager.BackStackEntry backStackEntry = getSecondFragmentOnStack();
            String fragmentName = backStackEntry.getName();
            if (fragmentName.equals(MapsFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                toolbar.getBackground().setAlpha(NO_TRANSPARENT_TOOLBAR);
                toolbar.setTitle("");
                filterButton.setVisibility(View.VISIBLE);
            } else if (fragmentName.equals(ArFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                toolbar.getBackground().setAlpha(TRANSPARENT_TOOLBAR);
                toolbar.setTitle("");
                filterButton.setVisibility(View.VISIBLE);
            } else if (fragmentName.equals(PoiListFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                toolbar.getBackground().setAlpha(NO_TRANSPARENT_TOOLBAR);
                toolbar.setTitle(R.string.poi_list);
                filterButton.setVisibility(View.GONE);
            }
            super.onBackPressed();
        }
    }

    private FragmentManager.BackStackEntry getSecondFragmentOnStack() {
        return fragmentManager.getBackStackEntryAt(getBackStackEntryCount() - 2);
    }

    private boolean isLastFragmentOnStack() {
        return (getBackStackEntryCount() == 1);
    }

    private int getBackStackEntryCount() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.getBackStackEntryCount();
    }

    @Override
    public void onNavigationDrawerItemSelected(FragmentType fragmentType) {
        if (fragmentType != FragmentType.MAP_2D) {
            hidePoiPreview();
            switchFragment(fragmentType);
        } else {
            switchToMaps2D();
            centerOnUserPosition();
        }
    }

    @Override
    public ActionBar getActivityActionBar() {
        return getSupportActionBar();
    }

    private void switchFragment(FragmentType fragmentType) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        toolbar.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        toolbar.getBackground().setAlpha(NO_TRANSPARENT_TOOLBAR);
        switch (fragmentType) {
            case MAP_2D:
                toolbar.setTitle("");
                filterButton.setVisibility(View.VISIBLE);
                setStatusBarColour(R.color.dark_blue);
                if (mapsFragment == null) {
                    mapsFragment = (MapsFragment) fragmentManager.findFragmentByTag(MapsFragment.TAG);
                }
                if (mapsFragment == null) {
                    mapsFragment = MapsFragment.newInstance(googleApiClient, filterListDialog.getSelectedItems());
                    fragmentTransaction.replace(R.id.container, mapsFragment, MapsFragment.TAG);
                    fragmentTransaction.addToBackStack(MapsFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(MapsFragment.TAG, 0);
                }
                break;
            case POI_LIST:
                cancelNavigation();
                toolbar.setTitle(R.string.poi_list);
                filterButton.setVisibility(View.GONE);
                setStatusBarColour(R.color.dark_blue);
                if (fragmentManager.findFragmentByTag(PoiListFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, PoiListFragment.newInstance(), PoiListFragment.TAG);
                    fragmentTransaction.addToBackStack(PoiListFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(PoiListFragment.TAG, 0);
                }
                break;
            case ADD_POI:
                cancelNavigation();
                setStatusBarColour(R.color.dark_blue);
                switchToPoiAdd();
                break;
            case LOGOUT:
                cancelNavigation();
                setStatusBarColour(R.color.dark_blue);
                switchToLogout();
                break;
            case SETTINGS:
                setStatusBarColour(R.color.dark_blue);
                switchToSettings();
                break;
            case SEARCH:
                hidePoiPreview();
                cancelNavigation();
                toolbar.setTitle(getString(R.string.search));
                filterButton.setVisibility(View.GONE);
                setStatusBarColour(R.color.dark_blue);
                if (fragmentManager.findFragmentByTag(PoiSearchFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, PoiSearchFragment.newInstance(), PoiSearchFragment.TAG);
                    fragmentTransaction.addToBackStack(PoiSearchFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(PoiSearchFragment.TAG, 0);
                }
                break;
        }
    }

    private void createPoiPreviewFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag(PreviewPoiFragment.TAG) == null) {
            fragmentTransaction.replace(R.id.container_slider, PreviewPoiFragment.newInstance(), PreviewPoiFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void showSearchResults(ArrayList<SearchResult> results) {
        switchToSearchResults(results);
    }

    public void switchToSearchResults(ArrayList<SearchResult> results) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag(SearchResultsFragment.TAG) == null) {
            fragmentTransaction.replace(R.id.container, SearchResultsFragment.newInstance(results), SearchResultsFragment.TAG);
            fragmentTransaction.addToBackStack(SearchResultsFragment.TAG);
            fragmentTransaction.commit();
        } else {
            getSupportFragmentManager().popBackStack(SearchResultsFragment.TAG, 0);
        }
    }
}

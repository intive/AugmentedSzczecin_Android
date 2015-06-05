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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blstream.as.ar.ArFragment;
import com.blstream.as.data.fragments.PoiListFragment;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.dialogs.AddOrEditPoiDialog;
import com.blstream.as.dialogs.ConfirmAddPoiWindow;
import com.blstream.as.dialogs.ConfirmDeletePoiDialog;
import com.blstream.as.dialogs.SettingsDialog;
import com.blstream.as.fragment.NavigationDrawerFragment;
import com.blstream.as.fragment.PreviewPoiFragment;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashSet;
import java.util.Set;

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
        AdapterView.OnItemClickListener {
    
    public final static String TAG = HomeActivity.class.getSimpleName();

    private MapsFragment mapsFragment;
    private NetworkStateReceiver networkStateReceiver;
    private Toolbar toolbar;
    private PopupWindow filterPopupWindow;
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

    private GoogleApiClient googleApiClient;
    private float fullPoiPreviewHeight;
    private float semiPoiPreviewHeight;

    private Set<String> selectedCategorySet;

    public enum FragmentType {
        MAP_2D, POI_LIST, ADD_POI, SETTINGS, LOGOUT
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Server.refreshPoiList();
        fragmentManager = getSupportFragmentManager();
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        createSliderUp();
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
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
        filterPopupWindow = createPopupWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (navigationDrawerFragment != null) {
            navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }
    private PopupWindow createPopupWindow() {
        TextView textView = (TextView) findViewById(R.id.filter_button);
        PopupWindow popupWindow = new PopupWindow(this);
        ListView listView = new ListView(this);
        listView.setAdapter(filterAdapter(getResources().getStringArray(R.array.category_name_from_user)));
        listView.setOnItemClickListener(this);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);

        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filter_popup_shape));
        popupWindow.setContentView(listView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPopupWindow.showAsDropDown(view);
            }
        });
        selectedCategorySet = new HashSet<>();
        return popupWindow;
    }

    private ArrayAdapter<String> filterAdapter(String categoryNameArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.filter_popup_menu_item, categoryNameArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String categoryName = getItem(position);
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.filter_popup_menu_item, parent, false);
                TextView textView = (TextView) rowView.findViewById(R.id.filter_text);
                textView.setText(categoryName);
                return rowView;
            }
        };
        return adapter;
    }

    public void setStatusBarColour(int colour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(colour));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String[] categoryNameArray = getResources().getStringArray(R.array.category_name_from_server);
        if(selectedCategorySet.contains(categoryNameArray[i])) {
            selectedCategorySet.remove(categoryNameArray[i]);
            view.setBackgroundColor(getResources().getColor(R.color.white));
        }
        else {
            selectedCategorySet.add(categoryNameArray[i]);
            view.setBackgroundColor(getResources().getColor(R.color.light_gray));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
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
        AlertDialog.Builder unknownLastLocation = new AlertDialog.Builder(this);
        unknownLastLocation.setTitle(R.string.lastLocationTitle);
        unknownLastLocation.setMessage(R.string.unknownLastLocationMessage);
        unknownLastLocation.setPositiveButton(R.string.dialogContinue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        unknownLastLocation.show();
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setStatusBarColour(R.color.transparent);
        cancelNavigation();
        //TODO if is require?
        if(googleApiClient != null && googleApiClient.isConnected()) {
            if (fragmentManager.findFragmentByTag(ArFragment.TAG) == null) {
                toolbar.getBackground().setAlpha(TRANSPARENT_TOOLBAR);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, ArFragment.newInstance(googleApiClient), ArFragment.TAG);
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
            if(previewPoiFragment != null) {
                previewPoiFragment.loadPoi(marker,MapsFragment.getPoiIdFromMarker(marker));
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
        Log.v(TAG, "Internet dostepny!");
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

    @Override
    public void wifiOr3gConnected() {
        if (wifiOr3gConnectionDialog != null) {
            wifiOr3gConnectionDialog.dismiss();
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
                            wifiOr3gConnectionDialog = null;
                            finish();
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
        if (networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment != null && navigationDrawerFragment.isDrawerOpen()) {
            navigationDrawerFragment.closeDrawer();
        } else if (isPanelFullExpand) {
            collapsePoiPreview();
        } else if (isLastFragmentOnStack()) {
            switchToMaps2D();
            centerOnUserPosition();
        } else {
            FragmentManager.BackStackEntry backStackEntry = getSecondFragmentOnStack();
            String fragmentName = backStackEntry.getName();
            if (fragmentName.equals(MapsFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                toolbar.getBackground().setAlpha(NO_TRANSPARENT_TOOLBAR);
                toolbar.setTitle("");
            }
            else if (fragmentName.equals(ArFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                toolbar.getBackground().setAlpha(TRANSPARENT_TOOLBAR);
                toolbar.setTitle("");
            }
            else if (fragmentName.equals(PoiListFragment.TAG)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                toolbar.getBackground().setAlpha(NO_TRANSPARENT_TOOLBAR);
                toolbar.setTitle(R.string.poi_list);
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
        }
        else {
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
                setStatusBarColour(R.color.dark_blue);
                if (mapsFragment == null) {
                    mapsFragment = (MapsFragment) fragmentManager.findFragmentByTag(MapsFragment.TAG);
                }
                if (mapsFragment == null) {
                    mapsFragment = MapsFragment.newInstance(googleApiClient);
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
}

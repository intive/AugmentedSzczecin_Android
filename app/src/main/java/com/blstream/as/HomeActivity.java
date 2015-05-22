package com.blstream.as;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blstream.as.ar.ArFragment;
import com.blstream.as.data.fragments.PoiFragment;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.dialogs.AddOrEditPoiDialog;
import com.blstream.as.dialogs.ConfirmAddPoiWindow;
import com.blstream.as.dialogs.ConfirmDeletePoiDialog;
import com.blstream.as.dialogs.SettingsDialog;
import com.blstream.as.fragment.HomeFragment;
import com.blstream.as.map.MapsFragment;
import com.blstream.as.fragment.PreviewPoiFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class HomeActivity extends ActionBarActivity implements
        ArFragment.Callbacks,
        MapsFragment.Callbacks,
        PoiFragment.OnPoiSelectedListener,
        HomeFragment.Callbacks,
        NetworkStateReceiver.NetworkStateReceiverListener,
        AddOrEditPoiDialog.OnAddPoiListener,
        PreviewPoiFragment.Callbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public final static String TAG = HomeActivity.class.getSimpleName();

    private MapsFragment mapsFragment;
    private NetworkStateReceiver networkStateReceiver;

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

    private DisplayMetrics displayMetrics;
    private SlidingUpPanelLayout poiPreviewLayout;
    private boolean isPanelFullExpand;
    private LinearLayout poiPreviewHeader;
    private LinearLayout poiPreviewToolbar;

    private GoogleApiClient googleApiClient;

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
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        createSliderUp();
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        createGoogleApiClient();
        switchToMaps2D();
    }
    private void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment instanceof MapsFragment) {
            MapsFragment mapsFragment = (MapsFragment) fragment;
            mapsFragment.setUpLocation();
        }
        if(fragment instanceof ArFragment) {
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //FIXME W nowej wersji jest tutaj SCREEN_ORIENTATION_UNSPECIFIED, zmiana ta powoduje drobne bledy przy obracaniu telefonu z otwartym podgladem POI
        switchFragment(FragmentType.MAP_2D);
        createPoiPreviewFragment();

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
        hidePoiPreview();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if(googleApiClient != null && googleApiClient.isConnected()) {
            switchFragment(FragmentType.AR);
        }
    }

    @Override
    public void switchToHome() {
        hidePoiPreview();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        switchFragment(FragmentType.HOME);
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
            mapsFragment.setPoiAddingMode(true);
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

            confirmAddPoiWindow = new ConfirmAddPoiWindow(getSupportFragmentManager(), marker, popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            confirmAddPoiWindow.showAtLocation(findViewById(R.id.container), Gravity.CENTER, X_OFFSET, Y_OFFSET);
        }
    }

    @Override
    public void confirmDeletePoi(Marker marker) {
        ConfirmDeletePoiDialog.newInstance(this, marker).show(getSupportFragmentManager(), ConfirmDeletePoiDialog.TAG); //FIXME Lepiej byloby rozdzielic operacje tworzenia obiektu od jego pokazywania - tak jak jest to w metodzie showEditPoiWindow
    }

    @Override
    public void deletePoi(Marker marker) {
        mapsFragment.deletePoi(marker);
        Toast.makeText(this, getString(R.string.poi_was_deleted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showEditPoiWindow(Marker marker) {
        AddOrEditPoiDialog editPoiDialog = AddOrEditPoiDialog.newInstance(marker, true);
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
                if(isPanelFullExpand) {
                    collapsePoiPreview();
                } else {
                    expandPoiPreview();
                }
                return true;
            }
        });
    }
    public void expandPoiPreview() {
        poiPreviewLayout.setAnchorPoint(DEFAULT_FULL_PANEL_HEIGHT/(float)displayMetrics.heightPixels); //FIXME Warto by przeniesc wynik tego dzialania do jakiejs zmiennej, zeby nie trzeba bylo za kazdym razem tego liczyc
        poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
    public void collapsePoiPreview() {
        poiPreviewLayout.setAnchorPoint((poiPreviewHeader.getHeight()+poiPreviewToolbar.getHeight())/(float)displayMetrics.heightPixels); //FIXME jw., poza tym troche magiczne
        poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        isPanelFullExpand = false;
    }
    @Override
    public void showPoiPreview(Marker marker) {
        if (poiPreviewLayout != null) {
            poiPreviewLayout.setAnchorPoint((poiPreviewHeader.getHeight()+poiPreviewToolbar.getHeight())/(float)displayMetrics.heightPixels); //FIXME jw.
            poiPreviewLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            isPanelFullExpand = false;
            FragmentManager fragmentManager = getSupportFragmentManager();
            PreviewPoiFragment fragment = (PreviewPoiFragment) fragmentManager.findFragmentByTag(PreviewPoiFragment.TAG);
            if(fragment != null) {
                fragment.loadPoi(marker);
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
        if (isLastFragmentOnStack()) {
            switchToHome();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isLastFragmentOnStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return (fragmentManager.getBackStackEntryCount() == 1);
    }

    private void switchFragment(FragmentType fragmentType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (fragmentType) {
            case MAP_2D:
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
            case AR:
                if (fragmentManager.findFragmentByTag(ArFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, ArFragment.newInstance(googleApiClient), ArFragment.TAG);
                    fragmentTransaction.addToBackStack(ArFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(ArFragment.TAG, 0);
                }
                break;
            case POI_LIST:
                if (fragmentManager.findFragmentByTag(PoiFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.container, PoiFragment.newInstance(), PoiFragment.TAG);
                    fragmentTransaction.addToBackStack(PoiFragment.TAG);
                    fragmentTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack(PoiFragment.TAG, 0);
                }
                break;
            case HOME:
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
    private void createPoiPreviewFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag(PreviewPoiFragment.TAG) == null) {
            fragmentTransaction.replace(R.id.container_slider, PreviewPoiFragment.newInstance(), PreviewPoiFragment.TAG);
            fragmentTransaction.commit();
        }
    }
}

package com.blstream.as;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blstream.as.ar.ArFragment;
import com.blstream.as.data.fragments.PoiFragment;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.map.MapsFragment;
import com.blstream.as.maps2d.MockDialog;

import com.google.android.gms.maps.model.MarkerOptions;

public class HomeScreenActivity extends ActionBarActivity implements
        OnPoiAdd,
        ArFragment.Callbacks,
        MapsFragment.Callbacks,
        PoiFragment.OnPoiSelectedListener,
        NetworkStateReceiver.NetworkStateReceiverListener {

    public final static String TAG = HomeScreenActivity.class.getSimpleName();

    private final static int NUM_IMAGES = 5;
    private PoiImageSlider viewPagerAdapter;
    private ViewPager viewPager;
    private TextView nearbyPoiButton;
    private TextView ownPlacesButton;
    private TextView addPoiButton;
    private TextView settingsButton;
    private TextView logoutButton;

    private NetworkStateReceiver networkStateReceiver;

    private int[] images;

    private SharedPreferences pref;
    private static final String LOGIN_PREFERENCES = "LoginPreferences";
    private static final String USER_LOGIN_STATUS = "UserLoginStatus";
    private static final String USER_EMAIL = "UserEmail";
    private static final String USER_PASS = "UserPass";

    public HomeScreenActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Server.getPoiList();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home_screen);
        setImages();
        viewPagerAdapter = new PoiImageSlider(this);
        viewPager = (ViewPager) findViewById(R.id.imageViewPager);
        viewPager.setAdapter(viewPagerAdapter);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        setButtons();
        setButtonsListeners();
        switchToMaps2D(true);
    }

    //Only for testing
    private void setImages() {
        images = new int[NUM_IMAGES];
        images[0] = R.drawable.splash;
        images[1] = R.drawable.splash;
        images[2] = R.drawable.splash;
        images[3] = R.drawable.splash;
        images[4] = R.drawable.splash;
    }

    private void setButtons() {
        nearbyPoiButton = (TextView) findViewById(R.id.nearby);
        addPoiButton = (TextView) findViewById(R.id.add_poi);
        settingsButton = (TextView) findViewById(R.id.settings);
        ownPlacesButton = (TextView) findViewById(R.id.own_places);
        logoutButton = (TextView) findViewById(R.id.logout);
        if (!LoginUtils.isUserLogged(this)){
            addPoiButton.setVisibility(View.GONE);
            ownPlacesButton.setVisibility(View.GONE);
            logoutButton.setText(getString(R.string.home_screen_exit));
        }
    }

    private void setButtonsListeners() {
        setNearbyPoiListener();
        setAddPoiListener();
        setSettingsListener();
        setOwnPlacesListener();
        setLogoutListener();
    }

    private void setNearbyPoiListener() {
        nearbyPoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToMaps2D(true);
            }
        });
    }

    private void setAddPoiListener() {
        addPoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.mock_dialog) == null) {
                    FragmentManager dialogFragmentManager = getSupportFragmentManager();
                    MockDialog mockDialog = new MockDialog();
                    mockDialog.show(dialogFragmentManager, "mock");
                }
            }
        });
    }

    private void setSettingsListener() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO settings tab
            }
        });
    }

    private void setOwnPlacesListener() {
        ownPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.findFragmentByTag(PoiFragment.TAG) == null) {
                    fragmentTransaction.addToBackStack(PoiFragment.TAG);
                }

                fragmentTransaction.replace(R.id.container, PoiFragment.newInstance());
                fragmentTransaction.commit();
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
                frameLayout.setVisibility(FrameLayout.VISIBLE);
            }
        });
    }

    void setLogoutListener(){
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                logout();
            }
        });
    }

    public void logout() {
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
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void switchToMaps2D(boolean centerOnPosition) {
        if (centerOnPosition) {
            MapsFragment.markerTarget = null;
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapsFragment mapsFragment = (MapsFragment) fragmentManager.findFragmentByTag(MapsFragment.TAG);
        if (mapsFragment == null) {
            fragmentTransaction.replace(R.id.container, MapsFragment.newInstance(), MapsFragment.TAG);
            fragmentTransaction.addToBackStack(MapsFragment.TAG);
            fragmentTransaction.commit();
        }
        else {
            getSupportFragmentManager().popBackStack(MapsFragment.TAG, 0);
            mapsFragment.setMarker();
        }
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
        frameLayout.setVisibility(FrameLayout.VISIBLE);
    }

    @Override
    public void switchToHome() {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
        frameLayout.setVisibility(FrameLayout.GONE);
    }

    @Override
    public void sendPOIfromDialog(MarkerOptions markerOptions) {
    }

    @Override
    public void goToMarker(String poiId) {
        MapsFragment.markerTarget = MapsFragment.getMarkerFromPoiId(poiId);
        switchToMaps2D(false);
    }

    @Override
    public void onBackPressed() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        switchToHome();
    }

    @Override
    public void switchToAr() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag(ArFragment.TAG) == null) {
            fragmentTransaction.replace(R.id.container, ArFragment.newInstance(), ArFragment.TAG);
            fragmentTransaction.addToBackStack(ArFragment.TAG);
            fragmentTransaction.commit();
        }
        else {
            getSupportFragmentManager().popBackStack(ArFragment.TAG, 0);
        }

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
        frameLayout.setVisibility(FrameLayout.VISIBLE);
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

    private class PoiImageSlider extends PagerAdapter {

        final Context context;
        final LayoutInflater layoutInflater;

        public PoiImageSlider(Context context) {
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = layoutInflater.inflate(R.layout.view_pager_image, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView.setImageResource(images[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
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
}

package com.blstream.as;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.blstream.as.fragment.ActionBarConnector;
import com.blstream.as.fragment.SplashScreenFragment;
import com.blstream.as.fragment.StartScreenFragment;


public class MainActivity extends ActionBarActivity implements ActionBarConnector, NetworkStateReceiver.NetworkStateReceiverListener {

    private static final Integer SPLASH_TIME = 5;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private NetworkStateReceiver networkStateReceiver;
    private AlertDialog wifiOr3gConnectionDialog;
    private AlertDialog internetConnectionLostDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, SplashScreenFragment.newInstance())
                .commit();

        handler.postDelayed(new Runnable() {
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, StartScreenFragment.newInstance())
                        .commit();
                if (LoginUtils.isUserLogged(MainActivity.this)) {
                    //FIXME Quick fix for modules marge
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }
            }
        }, SPLASH_TIME);
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
    protected void onStart() {
        super.onStart();
        if (networkStateReceiver == null) {
            networkStateReceiver = new NetworkStateReceiver();
            networkStateReceiver.addListener(this);
            registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isBackAfterLogout()){
            goToStartScreen();
        }
    }

    public boolean isBackAfterLogout(){
        return (fragmentManager.getBackStackEntryCount()>1);
    }

    public void goToStartScreen(){
        fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void networkAvailable() {

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
}

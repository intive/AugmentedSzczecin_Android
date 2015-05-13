package com.blstream.as;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;

import com.blstream.as.fragment.ActionBarConnector;
import com.blstream.as.fragment.SplashScreenFragment;
import com.blstream.as.fragment.StartScreenFragment;


public class MainActivity extends ActionBarActivity implements ActionBarConnector, NetworkStateReceiver.NetworkStateReceiverListener{

    private static final Integer SPLASH_TIME = 5;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
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
    public void onResume() {
        super.onResume();

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate(getFragmentManager().getBackStackEntryAt(0).getId(), android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void hideActionBar(){
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void networkAvailable() {

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

    }

    @Override
    public void wifiOr3gDisconnected() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.wifi_lost_title)
                .setMessage(R.string.wifi_lost_description)
                .setPositiveButton(R.string.wifi_lost_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
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
}

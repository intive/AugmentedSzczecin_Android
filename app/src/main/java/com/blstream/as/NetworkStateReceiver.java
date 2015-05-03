package com.blstream.as;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver {

    protected List<NetworkStateReceiverListener> listeners;
    protected Boolean connected;
    protected Boolean previousConnected;
    protected Boolean wifiOr3g;
    protected Boolean previousWifi;

    public NetworkStateReceiver() {
        listeners = new ArrayList<>();
        connected = null;
        wifiOr3g = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null && mobile != null) {
            wifiOr3g = (wifi.isAvailable() || mobile.isAvailable());
        }

        if((ni != null && ni.getState() == NetworkInfo.State.CONNECTED) || !wifiOr3g) {
            connected = true;
        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            connected = false;
        }

        if (previousConnected != connected || previousWifi != wifiOr3g) {
            notifyStateToAll();
        }
        previousWifi = wifiOr3g;
        previousConnected = connected;
    }

    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected) {
            listener.networkAvailable();
        }
        else {
            listener.networkUnavailable();
        }

        if (wifiOr3g == null)
            return;
        if (wifiOr3g) {
            listener.wifiOr3gConnected();
        }
        else {
            listener.wifiOr3gDisconnected();
        }
    }

    public void addListener(NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        listeners.remove(l);
    }


    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
        void wifiOr3gConnected();
        void wifiOr3gDisconnected();
    }
}
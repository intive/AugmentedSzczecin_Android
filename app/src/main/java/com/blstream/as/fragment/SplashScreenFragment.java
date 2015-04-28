package com.blstream.as.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blstream.as.MainActivity;
import com.blstream.as.R;

public class SplashScreenFragment extends Fragment {
    ActionBarConnector actionBarConnector;

    public SplashScreenFragment(){

    }

    public static SplashScreenFragment newInstance(){
        return new SplashScreenFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen_fragment, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actionBarConnector = (ActionBarConnector) activity;
        actionBarConnector.hideActionBar();
    }
}

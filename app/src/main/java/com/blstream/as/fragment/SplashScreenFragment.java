package com.blstream.as.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blstream.as.R;

public class SplashScreenFragment extends Fragment {

    public SplashScreenFragment(){

    }

    public static SplashScreenFragment newInstance(){
        return new SplashScreenFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen_fragment, container, false);
    }
}

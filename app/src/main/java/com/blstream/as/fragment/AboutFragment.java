package com.blstream.as.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blstream.as.R;


public class AboutFragment extends Fragment {

    public AboutFragment(){

    }

    public static final AboutFragment newInstance(){
        AboutFragment aboutFragment = new AboutFragment();
        return aboutFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View aboutView = inflater.inflate(R.layout.about_fragment, container, false);

        return aboutView;
    }
}

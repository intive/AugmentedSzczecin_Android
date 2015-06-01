package com.blstream.as.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blstream.as.R;

public class NotLoggedInFragment extends Fragment {
    public NotLoggedInFragment(){

    }

    public static NotLoggedInFragment newInstance(){
        return new NotLoggedInFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.not_logged_in_fragment, container, false);
    }
}

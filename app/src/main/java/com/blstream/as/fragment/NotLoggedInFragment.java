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

    public static final NotLoggedInFragment newInstance(){
        NotLoggedInFragment notLoggedInFragment = new NotLoggedInFragment();
        return notLoggedInFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View notLoggedInView = inflater.inflate(R.layout.not_logged_in_fragment, container, false);

        return notLoggedInView;
    }
}

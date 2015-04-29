package com.blstream.as.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blstream.as.R;

/**
 * Created by Damian on 2015-04-26.
 */
public class PreviewPoiFragment extends Fragment {
    public static PreviewPoiFragment newInstance() {
        return new PreviewPoiFragment();
    }
    public PreviewPoiFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_poi, container, false);
    }
}

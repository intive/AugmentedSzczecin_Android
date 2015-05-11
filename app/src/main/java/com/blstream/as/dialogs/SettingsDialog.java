package com.blstream.as.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blstream.as.BuildConfig;
import com.blstream.as.R;
import com.blstream.as.debug.BuildType;

/**
 * Created by Rafal Soudani on 2015-05-11.
 */
public class SettingsDialog extends DialogFragment {

    public static final String TAG = SettingsDialog.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View startScreenView = inflater.inflate(R.layout.settings_fragment, container, false);
        showVersionDebugInfo(startScreenView);
        return startScreenView;
    }

    private void showVersionDebugInfo(View rootView) {
        if (BuildConfig.BUILD_TYPE.contains(BuildType.DEBUG.buildName) && rootView != null) {
            TextView appVersionDebug = (TextView) rootView.findViewById(R.id.appVersionDebugInSettings);
            appVersionDebug.setVisibility(View.VISIBLE);
            String s = String.format("Commit SHA %s \nVersion: %s (%s)", BuildConfig.COMMIT_SHA, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
            appVersionDebug.setText(s);
        }
    }
}

package com.blstream.as.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blstream.as.BuildConfig;
import com.blstream.as.HomeActivity;
import com.blstream.as.R;
import com.blstream.as.debug.BuildType;

public class StartScreenFragment extends Fragment {

    private Button newAccountButton;
    private Button loginButton;
    private Button skipButton;

    public StartScreenFragment(){

    }

    public static StartScreenFragment newInstance(){
        return new StartScreenFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View startScreenView = inflater.inflate(R.layout.start_screen_fragment, container, false);

        newAccountButton = (Button)startScreenView.findViewById(R.id.newAccountButton);
        loginButton = (Button)startScreenView.findViewById(R.id.loginButton);
        skipButton = (Button)startScreenView.findViewById(R.id.skipButton);

        setNewAccountListener();
        setLoginListener();
        setSkipListener();

        showVersionDebugInfo(startScreenView);

        return startScreenView;
    }

    void setNewAccountListener() {
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(android.R.id.content, RegisterFragment.newInstance());
                fragmentTransaction.commit();
            }
        });
    }

    void setLoginListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(android.R.id.content, LoginScreenFragment.newInstance());
                fragmentTransaction.commit();
            }
        });
    }

    void setSkipListener() {
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
    }

    private void showVersionDebugInfo(View rootView) {
        if (BuildConfig.BUILD_TYPE.contains(BuildType.DEBUG.buildName) && rootView != null) {
            TextView appVersionDebug = (TextView) rootView.findViewById(R.id.appVersionDebug);
            appVersionDebug.setVisibility(View.VISIBLE);
            appVersionDebug.setText("Commit SHA " + BuildConfig.COMMIT_SHA + "\nVersion: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
        }
    }
}

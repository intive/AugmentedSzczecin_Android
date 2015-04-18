package com.blstream.as.ar;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.List;

import blstream.com.as.ar.R;

public class ArFragment extends Fragment {
    private static final String TAG = ArFragment.class.getName();
    private static final int ROTATION_STEP_IN_DEGREES = 90;
    private static final int FULL_ROTATION = 360;

    //android api components
    private Camera camera;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    //view components
    private CameraPreview cameraSurface;
    private Overlay overlaySurfaceWithEngine;
    private List<PointOfInterest> pointOfInterestList;

    //view
    private RelativeLayout arPreview;
    private Button categoryButton;
    private Button map2dButton;

    private View.OnClickListener onClickCategoryButton = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(getActivity(), v);
            popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    categoryButton.setText(item.getTitle());
                    return true;
                }
            });
            popup.show();
        }
    };
    private View.OnClickListener onClickMap2dButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO Cant see maps module
        }
    };

    public static ArFragment newInstance() {
        return new ArFragment();
    }

    public ArFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraSurface = new CameraPreview(getActivity());
        overlaySurfaceWithEngine = new Overlay(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_ar, container, false);
        arPreview = (RelativeLayout) fragmentView.findViewById(R.id.arSurface);

        arPreview.addView(cameraSurface);

        arPreview.addView(overlaySurfaceWithEngine);
        categoryButton = (Button) fragmentView.findViewById(R.id.categoryButton);
        categoryButton.setOnClickListener(onClickCategoryButton);
        categoryButton.bringToFront();
        map2dButton = (Button) fragmentView.findViewById(R.id.map2dButton);
        map2dButton.setOnClickListener(onClickMap2dButton);
        map2dButton.bringToFront();
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPoi();
        initSensorManagers();
        initCamera();
        initEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        releaseEngine();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseEngine();
        releaseCamera();
    }

    private boolean initCamera() {
        try {
            camera = null;
            camera = Camera.open();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        int displayRotation;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        if(windowManager != null) {
            displayRotation = windowManager.getDefaultDisplay().getRotation();
        }
        else
            displayRotation = 0;
        displayRotation *= ROTATION_STEP_IN_DEGREES;
        displayRotation = (info.orientation - displayRotation + FULL_ROTATION) % FULL_ROTATION;
        cameraSurface.setCamera(camera,displayRotation);
        return true;
    }
    private boolean initEngine() {
        try {
            overlaySurfaceWithEngine.register(windowManager, sensorManager, locationManager);
            overlaySurfaceWithEngine.setCameraFov(camera.getParameters().getHorizontalViewAngle());
            overlaySurfaceWithEngine.setupPaint();
            overlaySurfaceWithEngine.setPointOfInterestList(pointOfInterestList);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }
    private boolean initSensorManagers() {
        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return true;
    }
    public void loadPoi() {
        pointOfInterestList = new ArrayList<>();
        PointOfInterest newPoi = new PointOfInterest(0,"Zespol szkol nr 2","Hotel","opis",15.007831,53.339102);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"62","Jedzenie","opis",15.236306,53.411480);
        pointOfInterestList.add(newPoi);

    }

    private void releaseEngine() {
        if (overlaySurfaceWithEngine != null) {
            overlaySurfaceWithEngine.unRegister();
        }
    }
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

    }
}
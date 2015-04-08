package com.blstream.as;


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


public class ArFragment extends Fragment {
    private static final String TAG = "ArFragment";
    //android api components
    private Camera camera;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    //view
    RelativeLayout arPreview;
    Button categoryButton;
    Button map2dButton;

    //view components
    private CameraPreview cameraSurface;
    private Overlay overlaySurfaceWithEngine;
    private List<PointOfInterest> pointOfInterestList;

    public static ArFragment newInstance() {
        return new ArFragment();
    }

    public ArFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ar, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        arPreview = (RelativeLayout) getView().findViewById(R.id.arSurface);
        loadPoi();
        initCamera();
        initEngine();
        initOtherView();
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
        releaseCamera();
        releaseEngine();
    }

    private boolean initCamera() {
        try {
            // Create an instance of Camera
            camera = null;
            camera = Camera.open();
            // Create our Preview view and set it as the content of our fragment.
            cameraSurface = new CameraPreview(getActivity(), camera);
            arPreview.addView(cameraSurface);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private boolean initEngine() {
        try {
            windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            overlaySurfaceWithEngine = new Overlay(getActivity());
            overlaySurfaceWithEngine.register(windowManager, sensorManager, locationManager);
            overlaySurfaceWithEngine.setCameraFov(camera.getParameters().getHorizontalViewAngle());
            overlaySurfaceWithEngine.setupPaint();
            overlaySurfaceWithEngine.setPointOfInterestList(pointOfInterestList);

            arPreview.addView(overlaySurfaceWithEngine);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }
    public void loadPoi() {
        pointOfInterestList = new ArrayList<>();
        PointOfInterest newPoi = new PointOfInterest(0,"Zespol szkol nr 2","Hotel","opis",15.007831,53.339102);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"62","Jedzenie","opis",15.236306,53.411480);
        pointOfInterestList.add(newPoi);

    }
    private void initOtherView() {
        categoryButton = (Button) getView().findViewById(R.id.categoryButton);
        categoryButton.bringToFront();
        categoryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /** Instantiating PopupMenu class */
                PopupMenu popup = new PopupMenu(getActivity(), v);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        categoryButton.setText(item.getTitle());
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();
            }
        });
        map2dButton = (Button) getView().findViewById(R.id.map2dButton);
        map2dButton.bringToFront();
        map2dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO powrot do mapy
            }
        });
        getView().invalidate();
    }

    private void releaseEngine() {
        if (overlaySurfaceWithEngine != null) {
            overlaySurfaceWithEngine.unRegister();
            arPreview.removeView(overlaySurfaceWithEngine);
            overlaySurfaceWithEngine = null;
        }
    }
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (cameraSurface != null) {
            arPreview.removeView(cameraSurface);
            cameraSurface = null;
        }
    }
}
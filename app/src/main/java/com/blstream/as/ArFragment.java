package com.blstream.as;


import android.content.Context;
import android.graphics.Color;
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


public class ArFragment extends Fragment {
    private static final String TAG = "ArFragment"; //FIXME Avoid unneccessary objects creating
    //android api components
    private Camera camera;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    //view
    //FIXME Change to private
    RelativeLayout arPreview;
    Button categoryButton;
    Button map2dButton;
    //view components
    private CameraPreview cameraSurface;
    private Overlay overlaySurfaceWithEngine;

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
        //FIXME Move view set to onCreateView
        arPreview = (RelativeLayout) getView().findViewById(R.id.arSurface);
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
        } catch (Exception e) { //FIXME Make it more specific
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
            overlaySurfaceWithEngine.setupPaint(25.0f, Color.WHITE, Color.RED); //FIXME Magic value
            overlaySurfaceWithEngine.loadPoi();
            arPreview.addView(overlaySurfaceWithEngine);

        } catch (Exception e) { //FIXME Make it more specific
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private void initOtherView() {
        //FIXME Move view set to onCreateView
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
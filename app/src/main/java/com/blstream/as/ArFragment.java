package com.blstream.as;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class ArFragment extends Fragment {
    private static final String TAG = "ArFragment";
    //android api components
    private Camera camera;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    //view
    FrameLayout arPreview;
    //view components
    private CameraPreview cameraSurface;
    private Engine overlaySurfaceWithEngine;

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
        arPreview = (FrameLayout) getView().findViewById(R.id.arSurface);
        initCamera();
        initEngine();
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
            overlaySurfaceWithEngine = new Engine(getActivity(), windowManager, sensorManager, locationManager, camera);
            arPreview.addView(overlaySurfaceWithEngine);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
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
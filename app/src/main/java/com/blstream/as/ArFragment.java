package com.blstream.as;


import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class ArFragment extends Fragment {
    private static final String TAG = "ArFragment";
    private Camera camera;
    private CameraPreview cameraPreview;
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
        safeCameraOpenInView();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseCamera();
    }
    private boolean safeCameraOpenInView() {
        // Create an instance of Camera
        try {
            camera = null;
            camera = Camera.open(); // attempt to get a Camera instance
            // Create our Preview view and set it as the content of our activity.
            cameraPreview = new CameraPreview(getActivity(), camera);
            FrameLayout preview = (FrameLayout) getView().findViewById(R.id.cameraSurface);
            preview.addView(cameraPreview);
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
            return false;
        }
        return true;
    }
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (cameraPreview != null) {
            FrameLayout preview = (FrameLayout) getView().findViewById(R.id.cameraSurface);
            preview.removeView(cameraPreview);
            cameraPreview = null;
        }
    }
}

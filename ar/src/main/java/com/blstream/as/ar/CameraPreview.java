package com.blstream.as.ar;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getName();
    private static final int ROTATION_STEP_IN_DEGREES = 90;
    private static final int FULL_ROTATION = 360;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public CameraPreview(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        camera = null;
    }

    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        disable();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (surfaceHolder.getSurface() == null || camera == null){
            return;
        }
        try {
            camera.stopPreview();

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }
    public void enable() {
        try {
            camera = Camera.open();
            if(camera == null) {
                return;
            }
            try {
                Camera.Parameters parameters = camera.getParameters();
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            }
            camera.startPreview();
        } catch(RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    public void disable() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    public void setOrientation( WindowManager windowManager) {
        if (camera == null)
            return;
        int displayRotation;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        if(windowManager != null) {
            displayRotation = windowManager.getDefaultDisplay().getRotation();
        }
        else {
            displayRotation = 0;
        }
        displayRotation *= ROTATION_STEP_IN_DEGREES;
        displayRotation = (info.orientation - displayRotation + FULL_ROTATION) % FULL_ROTATION;
        this.camera.stopPreview();
        this.camera.setDisplayOrientation(displayRotation);
        this.camera.startPreview();
    }
}
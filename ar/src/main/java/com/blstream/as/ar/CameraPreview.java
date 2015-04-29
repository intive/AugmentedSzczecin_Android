package com.blstream.as.ar;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

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
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (surfaceHolder.getSurface() == null){
            return;
        }
        try {
            camera.stopPreview(); //Nie rzuca bardziej szczegoleowego wyjatku ani nie zwraca wartosci
        } catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

        try {
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(params);
        } catch(NullPointerException e) {
            Log.e(TAG,e.getMessage());
        } catch(RuntimeException e) {
            Log.e(TAG,e.getMessage());
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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
        this.camera.setDisplayOrientation(displayRotation);
    }
}
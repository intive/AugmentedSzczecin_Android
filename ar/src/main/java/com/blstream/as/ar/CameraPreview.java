package com.blstream.as.ar;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getName();
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
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (surfaceHolder.getSurface() == null){
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e){ //FIXME Make it more specific
            Log.e(TAG,e.getMessage());
        }
        /*TODO That code set optimal camera preview proportion, but can take more time. If preview is good to remove that code.
        Camera.Parameters params = camera.getParameters();
        params.set("orientation", "portrait");
        Camera.Size optimalSize=getOptimalPreviewSize(params.getSupportedPreviewSizes(),  getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        params.setPreviewSize(optimalSize.width, optimalSize.height);
        camera.setParameters(params);
        */
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (Exception e){ //FIXME Make it more specific
            Log.e(TAG,e.getMessage());
        }
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w/h;

        if (sizes==null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera, int displayRotation) {
        this.camera = camera;
        this.camera.setDisplayOrientation(displayRotation);
    }

}
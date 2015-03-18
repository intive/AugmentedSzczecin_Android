package blstream.com.as.ar;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import component.ar.augmentedszczecin.R;


public class ARFragment extends Fragment {
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ar, container, false);
        return rootView;
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

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    private boolean safeCameraOpenInView() {
        // Create an instance of Camera
        mCamera = getCameraInstance();
        if(mCamera == null)
        {

            return false;
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getActivity(), mCamera);
        FrameLayout preview = (FrameLayout) getView().findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        return true;
    }
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            FrameLayout preview = (FrameLayout) getView().findViewById(R.id.cameraPreview);
            preview.removeView(mPreview);
            mPreview = null;
        }
    }
}

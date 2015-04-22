package com.blstream.as.ar;


import android.content.Context;
import android.database.Cursor;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.rest.model.Endpoint;
import com.blstream.as.data.rest.model.POI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import blstream.com.as.ar.R;

public class ArFragment extends Fragment implements Endpoint, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArFragment.class.getName();
    private static final int ROTATION_STEP_IN_DEGREES = 90;
    private static final int FULL_ROTATION = 360;
    private static final double HORIZONTAL_FOV = 55.0;
    private static final int LOADER_ID = 1;
    private static final double MAX_DISTANCE = 5000.0;

    //android api components
    private Camera camera;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    //view components
    private CameraPreview cameraSurface;
    private Overlay overlaySurfaceWithEngine;
    private List<PointOfInterest> pointOfInterestList;
    private Set<Integer> poisIds;

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
        pointOfInterestList = new ArrayList<>();
        poisIds = new HashSet<>();
        cameraSurface = new CameraPreview(getActivity());
        overlaySurfaceWithEngine = new Overlay(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_ar, container, false);
        arPreview = (RelativeLayout) fragmentView.findViewById(R.id.arSurface);
        RollView rollView = (RollView) fragmentView.findViewById(R.id.rollView);
        overlaySurfaceWithEngine.setRollView(rollView);
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
        initSensorManagers();
        initCamera();
        initEngine();

        overlaySurfaceWithEngine.setGpsCallback(new GpsCallback() {
            @Override
            public void positionChanged() {
                createLoader();
            }
        });
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
            overlaySurfaceWithEngine.setCameraFov(HORIZONTAL_FOV);
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

    private void createLoader() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        double longitude = overlaySurfaceWithEngine.getLongitude();
        double latitude = overlaySurfaceWithEngine.getLatitude();

        PointF north = Utils.getPointInDistanceAtAngle(longitude, latitude, MAX_DISTANCE, 0);
        PointF east = Utils.getPointInDistanceAtAngle(longitude, latitude, MAX_DISTANCE, 90);
        PointF south = Utils.getPointInDistanceAtAngle(longitude, latitude, MAX_DISTANCE, 180);
        PointF west = Utils.getPointInDistanceAtAngle(longitude, latitude, MAX_DISTANCE, 270);

        double maxLongitude = east.y;
        double minLongitude = west.y;
        double maxLatitude = north.x;
        double minLatitude = south.x;
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(POI.class, null),
                null, "(Longitude BETWEEN " + minLongitude + " AND " + maxLongitude +
                ") AND (Latitude BETWEEN " + minLatitude + " AND " + maxLatitude + ")", null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int idIndex = cursor.getColumnIndex("PoiId");
        int nameIndex = cursor.getColumnIndex("Name");
        int categoryIndex = cursor.getColumnIndex("Category");
        int descriptionIndex = cursor.getColumnIndex("Description");
        int longitudeIndex = cursor.getColumnIndex("Longitude");
        int latitudeIndex = cursor.getColumnIndex("Latitude");

        double userLongitude = overlaySurfaceWithEngine.getLongitude();
        double userLatitude = overlaySurfaceWithEngine.getLatitude();

        for (Iterator<PointOfInterest> i = pointOfInterestList.iterator(); i.hasNext(); ) {
            PointOfInterest p = i.next();
            if (Utils.computeDistanceInMeters(userLongitude, userLatitude, p.getLongitude(), p.getLatitude()) > MAX_DISTANCE) {
                poisIds.remove(p.getId());
                i.remove();
            }
        }

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(idIndex));
                String name = cursor.getString(nameIndex);
                String category = cursor.getString(categoryIndex);
                String description = cursor.getString(descriptionIndex);
                double longitude = Double.parseDouble(cursor.getString(longitudeIndex));
                double latitude = Double.parseDouble(cursor.getString(latitudeIndex));

                PointOfInterest newPoi = new PointOfInterest(id, name, category, description, longitude, latitude);
                if (!poisIds.contains(id)) {
                    pointOfInterestList.add(newPoi);
                    poisIds.add(id);
                }

            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
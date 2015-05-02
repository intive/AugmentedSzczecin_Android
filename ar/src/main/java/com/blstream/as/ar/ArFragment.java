package com.blstream.as.ar;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import com.blstream.as.data.rest.model.Poi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import blstream.com.as.ar.R;

public class ArFragment extends Fragment implements Endpoint, LoaderManager.LoaderCallbacks<Cursor>, Engine.LocationCallback, GpsInfo.ArCallback {
    public static final String TAG = ArFragment.class.getName();
    private static final double HORIZONTAL_FOV = 55.0;
    private static final int LOADER_ID = 1;
    private static final double MAX_DISTANCE = 10000.0;

    //android api components
    private Camera camera;
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    //view components
    private CameraPreview cameraSurface;
    private Overlay overlaySurfaceWithEngine;
    private Button categoryButton;

    private List<PointOfInterest> pointOfInterestList;
    private List<PointOfInterest> pointOfInterestAfterApplyFilterList;
    private Set<String> poisIds;
    private Callbacks activityConnector;
    private GpsInfo gpsInfo;
    private boolean isLocationAvailable;



    public static ArFragment newInstance() {
        return new ArFragment();
    }

    public ArFragment() {

    }
    public interface Callbacks {
        public void switchToMaps2D();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointOfInterestList = new ArrayList<>();
        pointOfInterestAfterApplyFilterList = new ArrayList<>();
        poisIds = new HashSet<>();
        loadSensorManagers();
        cameraSurface = new CameraPreview(getActivity());
        overlaySurfaceWithEngine = new Overlay(getActivity());
        overlaySurfaceWithEngine.setLocationCallback(this);
        overlaySurfaceWithEngine.setCameraFov(HORIZONTAL_FOV);
        overlaySurfaceWithEngine.setupPaint();
        overlaySurfaceWithEngine.setPointOfInterestList(pointOfInterestAfterApplyFilterList);
        overlaySurfaceWithEngine.disableOverlay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_ar, container, false);
        RelativeLayout arPreview = (RelativeLayout) fragmentView.findViewById(R.id.arSurface);
        RollView rollView = (RollView) fragmentView.findViewById(R.id.rollView);
        rollView.setMaxDistance(MAX_DISTANCE);
        arPreview.addView(cameraSurface);
        overlaySurfaceWithEngine.setRollView(rollView);
        arPreview.addView(overlaySurfaceWithEngine);
        categoryButton = (Button) fragmentView.findViewById(R.id.categoryButton);
        categoryButton.setOnClickListener(onClickCategoryButton);
        updatePoiCategoryList(getResources().getString(R.string.allCategories));
        categoryButton.bringToFront();
        Button map2dButton;
        map2dButton = (Button) fragmentView.findViewById(R.id.map2dButton);
        map2dButton.setOnClickListener(onClickMap2dButton);
        map2dButton.bringToFront();
        return fragmentView;
    }

    private View.OnClickListener onClickCategoryButton = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(getActivity(), v);
            popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());
            for(String itemTitle : getResources().getStringArray(R.array.categoryNameArray)) {
                popup.getMenu().add(itemTitle);
            }
            popup.setOnMenuItemClickListener(onClickCategoryMenuItem);
            popup.show();
        }
    };

    private PopupMenu.OnMenuItemClickListener onClickCategoryMenuItem =  new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            categoryButton.setText(item.getTitle());
            updatePoiCategoryList(item.getTitle().toString());
            return true;
        }
    };

    private View.OnClickListener onClickMap2dButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activityConnector.switchToMaps2D();
        }
    };

    @Override
    public void onResume() {
        super.onStart();
        enableEngine();
        enableAugmentedReality();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAugmentedReality();
        disableEngine();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            activityConnector = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.Callbacks");
        }
    }

    private void enableCamera() {
        if(camera != null)
            return;
        try {
            camera = Camera.open();
            cameraSurface.setCamera(camera);
            cameraSurface.setOrientation(windowManager);
        } catch(RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private void enableEngine() {
        try {
            overlaySurfaceWithEngine.register(windowManager, sensorManager, locationManager);
        } catch(IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        } catch(SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private void enableOverlay() {
        overlaySurfaceWithEngine.enableOverlay();
    }
    private void disableOverlay() {
        overlaySurfaceWithEngine.disableOverlay();
    }
    private void loadSensorManagers() {
        if(windowManager == null)
            windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if(sensorManager == null)
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if(locationManager == null)
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(gpsInfo == null) {
            gpsInfo = new GpsInfo();
            gpsInfo.setArCallback(this);
            gpsInfo.setLocationManager(locationManager);
        }

    }
    @Override
    public void positionChanged() {
        //TODO add loaderReaload
    }
    public void enableAugmentedReality() {
        if(!gpsInfo.isLocated()) {
            return;
        }
        createLoader();
        enableCamera();
        enableOverlay();
    }
    public void disableAugmentedReality() {
        disableCamera();
        disableOverlay();
    }

    @Override
    public void showGpsUnavailable() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void showSearchingSignal() {

    }

    private void disableEngine() {
        if (overlaySurfaceWithEngine != null) {
            overlaySurfaceWithEngine.unRegister();
        }
    }
    private void disableCamera() {
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

        String maxLongitude = String.valueOf(east.y);
        String minLongitude = String.valueOf(west.y);
        String maxLatitude = String.valueOf(north.x);
        String minLatitude = String.valueOf(south.x);
        //TODO change query
        String query = String.format("(%s BETWEEN %s AND %s) AND (%s BETWEEN %s AND %s)",
                Poi.LONGITUDE, minLongitude, maxLongitude, Poi.LATITUDE, minLatitude, maxLatitude);
        return new CursorLoader(getActivity(), ContentProvider.createUri(Poi.class, null), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int idIndex = 0;
        int nameIndex = 0;
        int categoryIndex = 0;
        int longitudeIndex = 0;
        int latitudeIndex = 0;
        try {
            idIndex = cursor.getColumnIndex(Poi.POI_ID);
            nameIndex = cursor.getColumnIndex(Poi.NAME);
            categoryIndex = cursor.getColumnIndex(Poi.CATEGORY);
            longitudeIndex = cursor.getColumnIndex(Poi.LONGITUDE);
            latitudeIndex = cursor.getColumnIndex(Poi.LATITUDE);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
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
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String category = cursor.getString(categoryIndex);
                double longitude = Double.parseDouble(cursor.getString(longitudeIndex));
                double latitude = Double.parseDouble(cursor.getString(latitudeIndex));

                PointOfInterest newPoi = new PointOfInterest(id, name, category, longitude, latitude);
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
    private void updatePoiCategoryList(String categoryName) {
        pointOfInterestAfterApplyFilterList.clear();
        if(categoryName.equals(getResources().getStringArray(R.array.categoryNameArray)[0])) {
            for(PointOfInterest poi : pointOfInterestList) {
                pointOfInterestAfterApplyFilterList.add(poi);
            }
        }
        else {
            for(PointOfInterest poi : pointOfInterestList) {
                if(poi.getCategoryName().equals(categoryName)) {
                    pointOfInterestAfterApplyFilterList.add(poi);
                }
            }
        }
    }
}
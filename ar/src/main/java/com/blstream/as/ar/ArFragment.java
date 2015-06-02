package com.blstream.as.ar;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.PointF;
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
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.data.rest.model.Endpoint;
import com.blstream.as.data.rest.model.Location;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.service.MyContentProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import blstream.com.as.ar.R;

public class ArFragment extends Fragment implements Endpoint, LoaderManager.LoaderCallbacks<Cursor>, Engine.LocationCallback, GpsSignalResponder.Callback {
    public static final String TAG = ArFragment.class.getName();
    private static final double HORIZONTAL_FOV = 55.0;
    private static final int LOADER_ID = 1;
    private static final double MAX_DISTANCE = 1000.0;
    private static final int LANDSCAPE_ANGLE = 90;
    private static final int LANDSCAPE_REVERSE_ANGLE = 270;


    //android api components
    private WindowManager windowManager;
    private SensorManager sensorManager;
    private LocationManager locationManager;

    //view components
    private RelativeLayout arPreview;
    private CameraPreview cameraSurface;
    private Overlay overlaySurfaceWithEngine;
    private Button categoryButton;
    private ProgressDialog waitingForGpsDialog = null;

    private List<PointOfInterest> pointOfInterestList;
    private List<PointOfInterest> pointOfInterestAfterApplyFilterList;
    private Set<String> poisIds;
    private Callbacks activityConnector;
    private GpsSignalResponder gpsSignalResponder;
    private OrientationEventListener orientationEventListener;

    public static ArFragment newInstance() {
        return new ArFragment();
    }

    public ArFragment() {

    }

    public interface Callbacks {
        void switchToMaps2D();
        void centerOnUserPosition();
        void switchToHome();
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

    private void loadSensorManagers() {
        if(windowManager == null)
            windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if(sensorManager == null) {
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            setOrientationEventListener();
        }
        if(locationManager == null)
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(gpsSignalResponder == null) {
            gpsSignalResponder = new GpsSignalResponder();
            gpsSignalResponder.setLocationManager(locationManager);
        }

    }
    private void setOrientationEventListener() {
        orientationEventListener = new OrientationEventListener(getActivity(),SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if(cameraSurface != null && (orientation == LANDSCAPE_ANGLE || orientation == LANDSCAPE_REVERSE_ANGLE)) {
                    cameraSurface.setOrientation(windowManager);
                }
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_ar, container, false);
        arPreview = (RelativeLayout) fragmentView.findViewById(R.id.arSurface);
        RollView rollView = (RollView) fragmentView.findViewById(R.id.rollView);
        rollView.setMaxDistance(MAX_DISTANCE);
        cameraSurface.setOrientation(windowManager);
        arPreview.addView(cameraSurface);
        overlaySurfaceWithEngine.setRollView(rollView);
        arPreview.addView(overlaySurfaceWithEngine);
        categoryButton = (Button) fragmentView.findViewById(R.id.categoryButton);
        categoryButton.setOnClickListener(onClickCategoryButton);
        updatePoiCategoryList(getResources().getString(R.string.allCategories));
        moveButtonsToFront();
        return fragmentView;
    }

    void moveButtonsToFront() {
        categoryButton.bringToFront();
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
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            activityConnector.switchToMaps2D();
            activityConnector.centerOnUserPosition();
        }
    };

    private View.OnClickListener onClickHomeButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activityConnector.switchToHome();
        }
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        arPreview.removeView(cameraSurface);
        arPreview.removeView(overlaySurfaceWithEngine);

    }

    @Override
    public void onResume() {
        super.onStart();
        enableEngine();
        gpsSignalResponder.attachCallback(this);
        enableAugmentedReality();
    }
    
    public void enableAugmentedReality() {
        if(!gpsSignalResponder.isLocated()) {
            return;
        }
        overlaySurfaceWithEngine.updateLocation();
        createLoader();
        enableCamera();
        enableOverlay();
        Toast.makeText(getActivity(), R.string.arEnabledMessage, Toast.LENGTH_LONG).show();
    }

    private void createLoader() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void enableCamera() {
        cameraSurface.enable();
        if(orientationEventListener.canDetectOrientation())
            orientationEventListener.enable();
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
    @Override
    public void onPause() {
        super.onPause();
        disableAugmentedReality();
        gpsSignalResponder.detachCallback();
        disableEngine();
    }
    public void disableAugmentedReality() {
        disableCamera();
        disableOverlay();
        Toast.makeText(getActivity(),R.string.arDisabledMessage,Toast.LENGTH_LONG).show();
    }
    private void disableEngine() {
        if (overlaySurfaceWithEngine != null) {
            overlaySurfaceWithEngine.unRegister();
        }
    }

    private void disableCamera() {
        cameraSurface.disable();
        orientationEventListener.disable();
    }

    private void disableOverlay() {
        overlaySurfaceWithEngine.disableOverlay();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            activityConnector = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement MyListFragment.Callbacks");
        }
    }

    @Override
    public void positionChanged() {
        createLoader();
    }

    @Override
    public void showGpsUnavailable() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.gpsSignalSearchingTitle);
        alertDialog.setMessage(R.string.gpsEnabledMessage);
        alertDialog.setPositiveButton(R.string.gpsToSettingMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(R.string.gpsCancelMessage, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                activityConnector.switchToMaps2D();
                activityConnector.centerOnUserPosition();
            }
        });
        alertDialog.show();
    }

    @Override
    public void showSearchingSignal() {
        if(waitingForGpsDialog == null) {
            waitingForGpsDialog = new ProgressDialog(getActivity());
            waitingForGpsDialog.setMessage(getString(R.string.gpsSignalSearchingMessage));
            waitingForGpsDialog.setTitle(R.string.gpsSignalSearchingTitle);
            waitingForGpsDialog.setCanceledOnTouchOutside(false);
            waitingForGpsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    activityConnector.switchToMaps2D();
                    activityConnector.centerOnUserPosition();
                }
            });
            waitingForGpsDialog.show();
        }
    }

    @Override
    public void hideSearchingSignal() {
        if(waitingForGpsDialog != null) {
            waitingForGpsDialog.dismiss();
            waitingForGpsDialog = null;
        }
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
        String query = String.format("(%s BETWEEN %s AND %s) AND (%s BETWEEN %s AND %s)",
                Location.LONGITUDE, minLongitude, maxLongitude, Location.LATITUDE, minLatitude, maxLatitude);
        return new CursorLoader(getActivity(), MyContentProvider.createUri(Poi.class, null), null, query, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int idIndex;
        int nameIndex;
        int categoryIndex;
        int longitudeIndex;
        int latitudeIndex;
        if(cursor == null) {
            return;
        }
        idIndex = cursor.getColumnIndex(Poi.POI_ID);
        nameIndex = cursor.getColumnIndex(Poi.NAME);
        categoryIndex = cursor.getColumnIndex(Poi.CATEGORY);
        longitudeIndex = cursor.getColumnIndex(Location.LONGITUDE);
        latitudeIndex = cursor.getColumnIndex(Location.LATITUDE);
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
                if (cursor.getString(longitudeIndex) != null) {
                    double longitude = Double.parseDouble(cursor.getString(longitudeIndex));
                    double latitude = Double.parseDouble(cursor.getString(latitudeIndex));


                    PointOfInterest newPoi = new PointOfInterest(id, name, category, longitude, latitude);
                    if (!poisIds.contains(id)) {
                        pointOfInterestList.add(newPoi);
                        poisIds.add(id);
                    }
                }
            } while (cursor.moveToNext());
        }
        updatePoiCategoryList(getResources().getString(R.string.allCategories));
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
package com.blstream.as.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blstream.as.EditAndDeletePoiOnClickListener;
import com.blstream.as.LoginUtils;
import com.blstream.as.R;
import com.blstream.as.data.rest.model.Address;
import com.blstream.as.data.rest.model.enums.Category;
import com.blstream.as.data.rest.model.Poi;
import com.blstream.as.data.rest.model.enums.SubCategory;
import com.blstream.as.data.rest.service.MyContentProvider;
import com.google.android.gms.maps.model.Marker;


/**
 * Created by Damian on 2015-04-26.
 */
public class PreviewPoiFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = PreviewPoiFragment.class.getName();
    private static final int LOADER_ID = 1;

    private String poiId;

    private LinearLayout poiPreviewHeader;
    private LinearLayout poiPreviewToolbar;

    private Button navigationButton;
    private boolean inNavigationState = false;

    private Callbacks activityConnector;
    private ImageView galleryImageView;
    private TextView subcategoryTextView;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView addressTextView;
    private TextView fanpageTextView;
    private TextView phoneTextView;


    public interface Callbacks {
        void setPoiPreviewHeader(LinearLayout poiPreviewHeader);
        void setPoiPreviewToolbar(LinearLayout poiPreviewToolbar);
        void showEditPoiWindow(Marker marker);
        void confirmDeletePoi(String poiId);
        void navigateToPoi(String poiId);
        void cancelNavigation();
    }

    public static PreviewPoiFragment newInstance() {
        return new PreviewPoiFragment();
    }
    public PreviewPoiFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_preview_poi, container, false);
        poiPreviewHeader = (LinearLayout) fragmentView.findViewById(R.id.poiPreviewHeader);
        poiPreviewToolbar = (LinearLayout) fragmentView.findViewById(R.id.poiPreviewToolbar);
        subcategoryTextView = (TextView) fragmentView.findViewById(R.id.subcategoryTextView);
        nameTextView = (TextView) fragmentView.findViewById(R.id.nameTextView);
        descriptionTextView = (TextView) fragmentView.findViewById(R.id.descriptionTextView);
        addressTextView = (TextView) fragmentView.findViewById(R.id.addressTextView);
        fanpageTextView = (TextView) fragmentView.findViewById(R.id.fanpageTextView);
        phoneTextView = (TextView) fragmentView.findViewById(R.id.phoneTextView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityConnector.setPoiPreviewHeader(poiPreviewHeader);
        activityConnector.setPoiPreviewToolbar(poiPreviewToolbar);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            activityConnector = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement PreviewPoiFragment.Callbacks");
        }
    }
    public void loadPoi(Marker marker, final String poiId) {
        if(marker == null || poiId == null || getView() == null) {
            Log.e(TAG,getResources().getString(R.string.preview_poi_load_error));
            return;
        }
        LinearLayout toolbar = (LinearLayout) getView().findViewById(R.id.poiPreviewToolbar);
        if(LoginUtils.isUserLogged(getActivity())) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        this.nameTextView.setText(marker.getTitle());
        Button editPoiButton = (Button) getView().findViewById(R.id.editPoiButton);
        Button deletePoiButton = (Button) getView().findViewById(R.id.deletePoiButton);
        EditAndDeletePoiOnClickListener editPoiOnClickListener = new EditAndDeletePoiOnClickListener(poiId, false, activityConnector);
        navigationButton = (Button) getView().findViewById(R.id.navigationButton);

        editPoiButton.setOnClickListener(editPoiOnClickListener);
        deletePoiButton.setOnClickListener(editPoiOnClickListener);

        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inNavigationState) {
                    activityConnector.cancelNavigation();
                }
                else {
                    inNavigationState = true;
                    navigationButton.setText(getResources().getText(R.string.cancel_navigation));
                    activityConnector.navigateToPoi(poiId);
                }
            }
        });

        this.poiId = poiId;
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
        ScrollView contentScroll = (ScrollView) getView().findViewById(R.id.poiScrollView);
        contentScroll.fullScroll(ScrollView.FOCUS_UP);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = String.format("%s = '%s'", Poi.POI_ID,poiId);
        return new CursorLoader(getActivity(), MyContentProvider.createUri(Poi.class, null), null, query, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String subcategoryName = cursor.getString(cursor.getColumnIndex(Poi.SUB_CATEGORY));
            if(subcategoryName != null) {
                SubCategory subCategory = SubCategory.valueOf(subcategoryName);
                subcategoryTextView.setText(getActivity().getString(subCategory.getIdStringResource()));
            }
            else {
                String categoryName = cursor.getString(cursor.getColumnIndex(Poi.CATEGORY));
                if(categoryName == null) {
                    categoryName = "NIEOKREŚLONA KATEGORIA";
                }
                else {
                    Category category = Category.valueOf(categoryName);
                    categoryName = getActivity().getString(category.getIdStringResource());
                }
                subcategoryTextView.setText(categoryName);
            }
            descriptionTextView.setText(cursor.getString(cursor.getColumnIndex(Poi.DESCRIPTION)));
            String address = "";
            address += cursor.getString(cursor.getColumnIndex(Address.CITY)) + " ";
            address += cursor.getString(cursor.getColumnIndex(Address.STREET)) + " ";
            address += cursor.getString(cursor.getColumnIndex(Address.STREET_NUMBER)) + " ";
            address += cursor.getString(cursor.getColumnIndex(Address.ZIPCODE));
            addressTextView.setText(address);
            String phoneText = cursor.getString(cursor.getColumnIndex(Poi.PHONE));
            if(phoneText == null || phoneText.equals("")) {
                phoneText = getResources().getString(R.string.no_information);
            }
            phoneTextView.setText(phoneText);
            String fanpageText = cursor.getString(cursor.getColumnIndex(Poi.FANPAGE));
            if(fanpageText == null || fanpageText.equals("")) {
                fanpageText = getResources().getString(R.string.no_information);
            }
            fanpageTextView.setText(fanpageText);
        }
    }

    public void cancelNavigation() {
        inNavigationState = false;
        navigationButton.setText(getResources().getText(R.string.navigate_to_poi));
    }
}

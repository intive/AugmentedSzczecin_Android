package com.blstream.as.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.blstream.as.R;
import com.blstream.as.data.rest.model.Poi;


/**
 * Created by Damian on 2015-04-26.
 */
public class PreviewPoiFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = PreviewPoiFragment.class.getName();
    private static final int LOADER_ID = 1;

    private LinearLayout sliderToolbar;

    private Callbacks activityConnector;
    private ImageView galleryImageView;
    private TextView categoryTextView;
    private TextView nameTextView;
    private TextView descriptionTextView;

    public interface Callbacks {
        void setSliderToolbar(LinearLayout sliderToolbar);
    }

    public static PreviewPoiFragment newInstance() {
        return new PreviewPoiFragment();
    }
    public PreviewPoiFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_preview_poi, container, false);
        sliderToolbar = (LinearLayout) fragmentView.findViewById(R.id.poiToolbar);
        galleryImageView = (ImageView) fragmentView.findViewById(R.id.poiImage);
        categoryTextView = (TextView) fragmentView.findViewById(R.id.categoryTextView);
        nameTextView = (TextView) fragmentView.findViewById(R.id.nameTextView);
        descriptionTextView = (TextView) fragmentView.findViewById(R.id.descriptionTextView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityConnector.setSliderToolbar(sliderToolbar);
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
    public void loadPoi(String namePoi) {
        this.nameTextView.setText(namePoi);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = String.format("%s = '%s'", Poi.NAME,nameTextView.getText());
        return new CursorLoader(getActivity(),ContentProvider.createUri(Poi.class, null), null, query, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        categoryTextView.setText(getResources().getString(R.string.default_preview_category));
        descriptionTextView.setText(getResources().getString(R.string.default_preview_description));
        galleryImageView.setImageResource(R.drawable.splash);
        int poiCategoryIndex = cursor.getColumnIndex(Poi.CATEGORY);
        if (cursor.moveToFirst()) {
            categoryTextView.setText(cursor.getString(poiCategoryIndex));
        }
    }
}

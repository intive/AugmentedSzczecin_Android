package com.blstream.as.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blstream.as.R;
import com.blstream.as.data.BuildConfig;
import com.blstream.as.data.rest.model.Category;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Konrad on 2015-03-26.
 * Edited by Rafal Soudani
 */
public class AddOrEditPoiDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    public static final String TAG = AddOrEditPoiDialog.class.getSimpleName();

    private EditText titleEditText;
    private TextView longitudeTextView, latitudeTextView;
    private MapsFragment mapsFragment;

    private Marker marker;

    private boolean editingMode;

    private OnAddPoiListener activityConnector;

    public static AddOrEditPoiDialog newInstance(Marker marker, boolean editingMode) {
        AddOrEditPoiDialog addOrEditPoiDialog = new AddOrEditPoiDialog();
        addOrEditPoiDialog.setMarker(marker);
        addOrEditPoiDialog.setEditingMode(editingMode);
        return addOrEditPoiDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_poi_dialog, container, false);

        latitudeTextView = (TextView) view.findViewById(R.id.latitude);
        longitudeTextView = (TextView) view.findViewById(R.id.longitude);
        titleEditText = (EditText) view.findViewById(R.id.titleEditText);

        if (getActivity().getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG) instanceof MapsFragment) {
            mapsFragment = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG);
        }
        latitudeTextView.setText(getLatitude(marker));
        longitudeTextView.setText(getLongitude(marker));

        Button okButton = (Button) view.findViewById(R.id.acceptAddPoi);
        Button cancelButton = (Button) view.findViewById(R.id.cancelAddPoi);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        if (isEditingMode()){
            titleEditText.setText(marker.getTitle());
            okButton.setVisibility(View.GONE);

            Button editOkButton = (Button) view.findViewById(R.id.acceptEditPoi);
            editOkButton.setVisibility(View.VISIBLE);
            editOkButton.setOnClickListener(this);
        }

        setCancelable(true);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (BuildConfig.DEBUG && (!(activity instanceof OnAddPoiListener)))
            throw new AssertionError("Activity: " + activity.getClass().getSimpleName() + " must implement OnPoiSelectedListener");
        activityConnector = (OnAddPoiListener) activity;
    }

    private String getLongitude(Marker marker) {
        return String.valueOf(marker.getPosition().longitude);
    }

    private String getLatitude(Marker marker) {
        return String.valueOf(marker.getPosition().latitude);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acceptAddPoi:
                if (isEmpty(titleEditText)) {
                    activityConnector.showAddPoiResultMessage(false);
                } else {
                    Server.addPoi(stringValue(titleEditText), doubleValue(latitudeTextView), doubleValue(longitudeTextView), Category.PLACE); //TODO: rozne kategorie
                    marker.remove();
                    if (mapsFragment != null) {
                        mapsFragment.setMarkerTarget(null);
                    }
                    activityConnector.showAddPoiResultMessage(true);
                    dismiss();
                }
                break;

            case R.id.acceptEditPoi:
                //TODO: edycja punktu, gdy serwer bedzie na to pozwalal
                Toast.makeText(getActivity(), "Funkcjonalnosc jeszcze nie zaimplementowana", Toast.LENGTH_SHORT).show();
                break;

            case R.id.cancelAddPoi:
                dismiss();
                break;
        }
    }

    private Double doubleValue(TextView textView) {
        return Double.parseDouble(String.valueOf(textView.getText()));
    }

    private String stringValue(EditText editText) {
        return editText.getText().toString();
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    public interface OnAddPoiListener {
        /**
         * @param state true if successful, false if failed
         */
        void showAddPoiResultMessage(Boolean state);
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public boolean isEditingMode() {
        return editingMode;
    }

    public void setEditingMode(boolean editingMode) {
        this.editingMode = editingMode;
    }
}

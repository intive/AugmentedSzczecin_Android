package com.blstream.as.maps2d;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blstream.as.OnPoiAdd;
import com.blstream.as.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Konrad on 2015-03-26.
 */
public class MockDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private EditText latitudeEditText, longitudeEditText, titleEditText;
    private Button okButton, cancelButton;
    private OnPoiAdd sendPoiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mock_dialog_layout, null); //FIXME Use constructor with 3 parameters http://possiblemobile.com/2013/05/layout-inflation-as-intended/
        latitudeEditText = (EditText) view.findViewById(R.id.editLat);
        longitudeEditText = (EditText) view.findViewById(R.id.editLng);
        titleEditText = (EditText) view.findViewById(R.id.editDialogTitle);

        okButton = (Button) view.findViewById(R.id.buttonOK);
        cancelButton = (Button) view.findViewById(R.id.button);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        setCancelable(false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sendPoiInterface = (OnPoiAdd) activity;
    }

    @Override
    public void onClick(View v) {
        if (sendPoiInterface != null || v.getId() == R.id.buttonOK) {
            if (latitudeEditText.getText() != null || longitudeEditText.getText() != null) {
                LatLng latLng = new LatLng(Double.valueOf(longitudeEditText.getText().toString())
                                , Double.valueOf(latitudeEditText.getText().toString()));
                        sendPoiInterface.sendPOIfromDialog(new MarkerOptions()
                                        .position(latLng)
                                        .title(titleEditText.getText().toString())
                        );
                }
            dismiss();
        }
    }
}

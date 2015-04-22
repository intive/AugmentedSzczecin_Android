package com.blstream.as.maps2d;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

    private EditText lat, lng, title; //FIXME Explain more in variable name
    private Button OK, cancel; //FIXME Use camelCase
    private OnPoiAdd sendPoi; //FIXME Use camelCase

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mock_dialog_layout, null); //FIXME Use constructor with 3 parameters http://possiblemobile.com/2013/05/layout-inflation-as-intended/
        lat = (EditText) view.findViewById(R.id.editLat);
        lng = (EditText) view.findViewById(R.id.editLng);
        title = (EditText) view.findViewById(R.id.editDialogTitle);

        OK = (Button) view.findViewById(R.id.buttonOK);
        cancel = (Button) view.findViewById(R.id.button);

        OK.setOnClickListener(this);
        cancel.setOnClickListener(this);

        setCancelable(false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sendPoi = (OnPoiAdd) activity;
    }

    @Override
    public void onClick(View v) {
        if (sendPoi != null) { //FIXME To much if inside if inside if.....
            if (v.getId() == R.id.buttonOK) {
                if (lat.getText() != null) {
                    if (lng.getText() != null) {
                        LatLng latLng = new LatLng(Double.valueOf(lng.getText().toString())
                                , Double.valueOf(lat.getText().toString()));
                        sendPoi.sendPOIfromDialog(new MarkerOptions()
                                        .position(latLng)
                                        .title(title.getText().toString())
                        );
                    }
                }
            }
            dismiss();
        }
    }
}

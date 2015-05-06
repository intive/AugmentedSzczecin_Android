package com.blstream.as.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blstream.as.R;
import com.blstream.as.data.rest.service.Server;
import com.blstream.as.map.MapsFragment;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Konrad on 2015-03-26.
 * Edited by Rafal Soudani
 */
public class AddPoiDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    public static final String TAG = AddPoiDialog.class.getSimpleName();

    private EditText titleEditText;
    private TextView longitudeTextView, latitudeTextView;
    Marker marker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_poi_dialog, container, false);

        latitudeTextView = (TextView) view.findViewById(R.id.latitude);
        longitudeTextView = (TextView) view.findViewById(R.id.longitude);
        titleEditText = (EditText) view.findViewById(R.id.titleEditText);

        marker = MapsFragment.getMarkerTarget();
        latitudeTextView.setText(getLatitude(marker));
        longitudeTextView.setText(getLongitude(marker));

        Button okButton = (Button) view.findViewById(R.id.acceptAddPoi);
        Button cancelButton = (Button) view.findViewById(R.id.cancelAddPoi);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        setCancelable(true);
        return view;
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
                    Toast.makeText(getActivity(), getString(R.string.add_poi_missing_title), Toast.LENGTH_SHORT).show();
                } else {
                    Server.addPoi(stringValue(titleEditText), doubleValue(latitudeTextView), doubleValue(longitudeTextView));
                    marker.remove();
                    MapsFragment.setMarkerTarget(null);
                    Toast.makeText(getActivity(), getString(R.string.add_poi_success), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
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
}

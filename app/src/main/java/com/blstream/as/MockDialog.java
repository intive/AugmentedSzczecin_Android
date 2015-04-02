package com.blstream.as;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Konrad on 2015-03-26.
 */
public class MockDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {
    EditText lat, lng, title;
    Button OK, cancel;
    BaseActivity baseActivity = new BaseActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mock_dialog_layout, null);
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
    public void onClick(View v) {
        if (v.getId() == R.id.buttonOK) {

            if (lat.getText() != null) {
                if (lng.getText() != null) {
                    LatLng latLng = new LatLng(Double.valueOf(lng.getText().toString())
                            , Double.valueOf(lat.getText().toString()));
                    baseActivity.setMarkerList(new MarkerOptions()
                                    .position(latLng)
                                    .title(title.getText().toString())
                    );

                    Log.i("Marker","Ilosć markerów = " + baseActivity.getMarkerList().size());
                    Log.i("Marker","Nazwa pierwszego markera: " + baseActivity.getMarkerList().get(0).getTitle());

                }
                dismiss();
            }
        } else {
            dismiss();

        }

    }
}

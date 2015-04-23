package com.blstream.as.maps2d;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blstream.as.R;

/**
 * Created by Konrad on 2015-04-21.
 */
public class GpsWarningDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private TextView gpsDialogTextView;
    private Button changeSettingsButton, cancelButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gps_warning_dialog_layout, null);
        changeSettingsButton = (Button) view.findViewById(R.id.goToSettingsButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        changeSettingsButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        setCancelable(false);

        return view;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.goToSettingsButton){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        dismiss();
    }
}

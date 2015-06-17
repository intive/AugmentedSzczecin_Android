package com.blstream.as.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.blstream.as.R;
import com.blstream.as.map.MapsFragment;

/**
 * Created by Rafal Soudani on 2015-05-13.
 */
public class ConfirmDeletePoiDialog extends DialogFragment implements View.OnClickListener {


    public static final String TAG = SettingsDialog.class.getSimpleName();

    private MapsFragment.Callbacks activityConnector;
    private String poiId;

    public static ConfirmDeletePoiDialog newInstance(MapsFragment.Callbacks activityConnector, String poiId) {

        ConfirmDeletePoiDialog confirmDeletePoiDialog = new ConfirmDeletePoiDialog();

        confirmDeletePoiDialog.activityConnector = activityConnector;
        confirmDeletePoiDialog.poiId = poiId;

        return confirmDeletePoiDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View startScreenView = inflater.inflate(R.layout.confirm_delete_poi, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button btnDismiss = (Button) startScreenView.findViewById(R.id.cancelDeletePoi);
        Button btnAccept = (Button) startScreenView.findViewById(R.id.acceptDeletePoi);
        btnDismiss.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

        btnAccept.setText(getString(R.string.delete));

        return startScreenView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptDeletePoi:
                activityConnector.deletePoi(poiId);
                dismiss();
                break;

            case R.id.cancelDeletePoi:
                dismiss();
        }
    }
}

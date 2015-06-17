package com.blstream.as.dialogs;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blstream.as.R;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Rafal Soudani on 2015-05-06.
 */
public class ConfirmAddPoiWindow extends PopupWindow implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private Marker marker;
    private Context context;

    public ConfirmAddPoiWindow(FragmentManager fragmentManager, Marker marker, View contentView, int width, int height, Context context) {
        super(contentView, width, height);

        this.fragmentManager = fragmentManager;
        this.marker = marker;
        this.context = context;

        Button btnDismiss = (Button) contentView.findViewById(R.id.cancelAddPoi);
        Button btnAccept = (Button) contentView.findViewById(R.id.acceptAddPoi);
        btnDismiss.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptAddPoi:
                AddOrEditPoiDialog addPoiDialog = AddOrEditPoiDialog.newInstance(marker, false, context);
                addPoiDialog.show(fragmentManager, AddOrEditPoiDialog.TAG);
                dismiss();
                break;

            case R.id.cancelAddPoi:
                dismiss();
        }
    }
}

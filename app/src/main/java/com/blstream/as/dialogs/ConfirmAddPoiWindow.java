package com.blstream.as.dialogs;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.blstream.as.R;

/**
 *  Created by Rafal Soudani on 2015-05-06.
 */
public class ConfirmAddPoiWindow extends PopupWindow implements View.OnClickListener {

    private FragmentManager fragmentManager;

    public ConfirmAddPoiWindow(FragmentManager fragmentManager, View contentView, int width, int height) {
        super(contentView, width, height);

        this.fragmentManager = fragmentManager;
        Button btnDismiss = (Button) contentView.findViewById(R.id.cancelAddPoi);
        btnDismiss.setOnClickListener(this);
        Button btnAccept = (Button) contentView.findViewById(R.id.acceptAddPoi);
        btnAccept.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptAddPoi:
                AddPoiDialog addPoiDialog = new AddPoiDialog();
                addPoiDialog.show(fragmentManager, AddPoiDialog.TAG);
                dismiss();
                break;

            case R.id.cancelAddPoi:
                dismiss();
        }
    }
}

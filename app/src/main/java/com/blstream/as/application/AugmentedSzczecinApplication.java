package com.blstream.as.application;

import android.app.Application;

import com.blstream.as.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Rafal Soudani on 2015-06-01.
 */
@ReportsCrashes(
        mailTo = "pat2015-szn-android-list@blstream.com",
        customReportContent = {ReportField.APP_VERSION_CODE,ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,ReportField.BRAND,ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,ReportField.USER_COMMENT,ReportField.STACK_TRACE},
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.action_acra)


public class AugmentedSzczecinApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}

package com.blstream.as.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

public class ScaleBar extends ImageView {

    private static final float SCALE_BAR_WIDTH = 3.0f;

    private GoogleMap googleMap;

    private float dimensionX;
    private float dimensionY;

    private float offsetX;
    private float offsetY;


    public ScaleBar(Context context, GoogleMap googleMap) {
        super(context);
        this.googleMap = googleMap;
        dimensionX = context.getResources().getDisplayMetrics().xdpi;
        dimensionY = context.getResources().getDisplayMetrics().ydpi;
        offsetX = getResources().getDimension(R.dimen.scale_bar_offset_x);
        offsetY = getResources().getDimension(R.dimen.scale_bar_offset_x);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        drawScaleBar(canvas);
        canvas.restore();
    }

    private void drawScaleBar(Canvas canvas) {

        final Paint barPaint = new Paint();
        barPaint.setColor(Color.BLACK);
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(SCALE_BAR_WIDTH);

        final Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(getResources().getDimension(R.dimen.scale_bar_text_size));

        drawXMetric(canvas, textPaint, barPaint);
    }

    private void drawXMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        if (googleMap != null) {
            Projection projection = googleMap.getProjection();

            LatLng p1 = projection.fromScreenLocation(new Point((int) ((getWidth() / 2) - (dimensionX / 2)), getHeight() / 2));
            LatLng p2 = projection.fromScreenLocation(new Point((int) ((getWidth() / 2) + (dimensionX / 2)), getHeight() / 2));

            Location locationP1 = new Location("ScaleBar location 1");
            Location locationP2 = new Location("ScaleBar location 2");

            locationP1.setLatitude(p1.latitude);
            locationP2.setLatitude(p2.latitude);
            locationP1.setLongitude(p1.longitude);
            locationP2.setLongitude(p2.longitude);

            float xMetersPerInch = locationP1.distanceTo(locationP2);

            String scaleBarValue = scaleBarLengthText(xMetersPerInch);
            Rect xTextRect = new Rect();
            textPaint.getTextBounds(scaleBarValue, 0, scaleBarValue.length(), xTextRect);

            int textSpacing = (int) (xTextRect.height() / 5.0);

            canvas.drawRect(offsetX, offsetY, offsetX + dimensionX, offsetY + SCALE_BAR_WIDTH, barPaint);
            canvas.drawRect(offsetX + dimensionX, offsetY, offsetX + dimensionX + SCALE_BAR_WIDTH, offsetY +
                    xTextRect.height() + SCALE_BAR_WIDTH + textSpacing, barPaint);

            canvas.drawRect(offsetX, offsetY, offsetX + SCALE_BAR_WIDTH, offsetY +
                    xTextRect.height() + SCALE_BAR_WIDTH + textSpacing, barPaint);
            canvas.drawText(scaleBarValue, (offsetX + dimensionX / 2 - xTextRect.width() / 2),
                    (offsetY + xTextRect.height() + SCALE_BAR_WIDTH + textSpacing), textPaint);
        }
    }

    private String scaleBarLengthText(float meters) {
        int integerMeters = (int)(meters / 100) * 100;
        if (integerMeters >= 1000) {
            return ((integerMeters / 1000)) + "km";
        } else {
            return integerMeters + "m";
        }
    }
}

package com.blstream.as.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blstream.com.as.ar.R;


public class Overlay extends Engine {
    public final static String TAG = Overlay.class.getSimpleName();

    private static final float SCREEN_HEIGHT_PROPORTIONS = 3.0f/4.0f;
    private static final float DISTANCE_POINT_RADIUS = 20.0f;
    private static final float DISTANCE_TEXT_SIZE = 17.0f;
    private static final int DISTANCE_TEXT_COLOR = Color.WHITE;
    private static final float MARKER_POINT_RADIUS = 4.0f;
    private static final float LINE_WIDTH = 2.0f;
    private static final int MARKER_POINT_AND_LINE_COLOR = Color.RED;
    private static final int OVERLAY_POI_COLOR = Color.BLUE;
    private static final float NUM_OF_POI_TEXT_SIZE = 15.0f;
    private static final int NUM_OF_POI_TEXT_COLOR = Color.WHITE;

    private List<PointOfInterest> pointOfInterestList;
    private Map<String,Bitmap> drawablesMap;
    private Paint pointPaint;
    private Paint linePaint;
    private Paint distanceTextPaint;
    private Paint overlayTextPaint;
    private Paint overlayStylePaint;

    private boolean isOverlayEnabled;

    public Overlay(Context context) {
        super(context);

    }
    public void setupPaint() {
        overlayStylePaint = new Paint();
        overlayStylePaint.setColor(OVERLAY_POI_COLOR);
        overlayTextPaint = new Paint();
        overlayTextPaint.setColor(NUM_OF_POI_TEXT_COLOR);
        overlayTextPaint.setTextSize(NUM_OF_POI_TEXT_SIZE);
        overlayTextPaint.setTextAlign(Paint.Align.CENTER);
        pointPaint = new Paint();
        pointPaint.setColor(MARKER_POINT_AND_LINE_COLOR);
        linePaint = new Paint();
        linePaint.setColor(MARKER_POINT_AND_LINE_COLOR);
        linePaint.setStrokeWidth(LINE_WIDTH);
        distanceTextPaint = new Paint();
        distanceTextPaint.setTextSize(DISTANCE_TEXT_SIZE);
        distanceTextPaint.setColor(DISTANCE_TEXT_COLOR);
        distanceTextPaint.setTextAlign(Paint.Align.CENTER);

        setupBitmaps();
    }
    private void setupBitmaps() {
        drawablesMap = new HashMap<>();
        String[] categoryNames = getResources().getStringArray(R.array.categoryNameArray);
        drawablesMap.put(categoryNames[0],BitmapFactory.decodeResource(getResources(), R.drawable.miejsca_publiczne));
        drawablesMap.put(categoryNames[1],BitmapFactory.decodeResource(getResources(), R.drawable.ulubione));
        drawablesMap.put(categoryNames[2],BitmapFactory.decodeResource(getResources(), R.drawable.firmy_i_uslugi));
        drawablesMap.put(categoryNames[3],BitmapFactory.decodeResource(getResources(), R.drawable.miejsca_publiczne));

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isOverlayEnabled) {
            return;
        }
        int numOfPoiDraw = 0;
        for(PointOfInterest poi : pointOfInterestList) {
            int screenX = (int) (computeXCoordinate(poi.getLongitude(),poi.getLatitude()) * canvas.getWidth());
            int screenY = (int) ((1.0-computeYCoordinate(poi.getLongitude(),poi.getLatitude(), 0, 1800))*canvas.getHeight()* SCREEN_HEIGHT_PROPORTIONS);
            if((screenX < 0 || screenX > canvas.getWidth()) || (screenY < 0 || screenY > canvas.getHeight() * SCREEN_HEIGHT_PROPORTIONS))
                continue;
            //drawing POI's
            canvas.drawCircle(screenX,canvas.getHeight()* SCREEN_HEIGHT_PROPORTIONS, MARKER_POINT_RADIUS,pointPaint);
            canvas.drawLine(screenX,canvas.getHeight()* SCREEN_HEIGHT_PROPORTIONS,screenX,screenY,linePaint);
            drawBitmap(canvas,screenX,screenY);
            canvas.drawCircle(screenX,screenY, DISTANCE_POINT_RADIUS,pointPaint);
            canvas.drawText(Integer.toString((int)Utils.computeDistanceInMeters(poi.getLongitude(), poi.getLatitude(), getLongitude(), getLatitude())),screenX,screenY + DISTANCE_TEXT_SIZE / 2,distanceTextPaint);
            numOfPoiDraw++;
        }
        String numOfPoiNoDraw = Integer.toString(pointOfInterestList.size()-numOfPoiDraw);

        //canvas.drawText(numOfPoiNoDraw,,overlayTextPaint);

    }
    private void drawBitmap(Canvas canvas,int x, int y) {
        Bitmap bitmap = drawablesMap.get("Miejsca publiczne");
        canvas.drawBitmap(bitmap,x - bitmap.getWidth() / 2,y - bitmap.getHeight(),overlayStylePaint);
    }

    public void setPointOfInterestList(List<PointOfInterest> pointOfInterestList) {
        this.pointOfInterestList = pointOfInterestList;
    }

    public void enableOverlay() {
        isOverlayEnabled = true;
    }
    public void disableOverlay() {
        isOverlayEnabled = false;
    }
}

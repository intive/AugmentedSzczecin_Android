package com.blstream.as.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;


public class Overlay extends Engine implements View.OnTouchListener {
    private static final double DEFAULT_RANGE_DISTANCE = 200.0;
    private static final float SCREEN_HEIGHT_PROPORTIONS = 3.0f/4.0f;
    private static final float[] NUM_OF_POI_ICON = {0.1f,0.8f,0.2f,1.0f}; //left, top, right, bottom
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
    //dynamic zoom
    private double rangeDistance;
    private double startDistance;
    private float currentScrollY;
    private float scrollY;
    private boolean isTouched;
    //paint
    private Paint pointPaint;
    private Paint linePaint;
    private Paint distanceTextPaint;
    private Paint overlayTextPaint;
    private Paint overlayStylePaint;

    public Overlay(Context context) {
        super(context);
        startDistance = DEFAULT_RANGE_DISTANCE;
        rangeDistance = DEFAULT_RANGE_DISTANCE;
        scrollY = 0.0f;
        isTouched = false;
        this.setOnTouchListener(this);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int numOfPoiDraw = 0;
        double maxDistance = startDistance + scrollY;
        for(PointOfInterest poi : pointOfInterestList) {
            int screenX = (int) (computeXCoordinate(poi.getLongitude(),poi.getLatitude()) * canvas.getWidth());
            int screenY = (int) ((1.0-computeYCoordinate(poi.getLongitude(),poi.getLatitude(), maxDistance - rangeDistance, maxDistance))*canvas.getHeight()* SCREEN_HEIGHT_PROPORTIONS);
            if((screenX < 0 || screenX > canvas.getWidth()) || (screenY < 0 || screenY > canvas.getHeight() * SCREEN_HEIGHT_PROPORTIONS))
                continue;
            //drawing POI's
            canvas.drawCircle(screenX,canvas.getHeight()* SCREEN_HEIGHT_PROPORTIONS, MARKER_POINT_RADIUS,pointPaint);
            canvas.drawLine(screenX,canvas.getHeight()* SCREEN_HEIGHT_PROPORTIONS,screenX,screenY,linePaint);
            canvas.drawCircle(screenX,screenY, DISTANCE_POINT_RADIUS,pointPaint);
            canvas.drawText(Integer.toString((int)Utils.computeDistanceInMeters(poi.getLongitude(), poi.getLatitude(), getLongitude(), getLatitude())),screenX,screenY,distanceTextPaint);
            //TODO Change circle on bitmap
            canvas.drawCircle(screenX, screenY - (DISTANCE_POINT_RADIUS / 2) - 30, 30, overlayStylePaint);
            numOfPoiDraw++;
        }
        String numOfPoiNoDraw = Integer.toString(pointOfInterestList.size()-numOfPoiDraw);
        canvas.drawRect(NUM_OF_POI_ICON[0]*canvas.getWidth(), NUM_OF_POI_ICON[1]*getHeight(), NUM_OF_POI_ICON[2]*canvas.getWidth(), NUM_OF_POI_ICON[3]*getHeight(),overlayStylePaint);
        canvas.drawText(numOfPoiNoDraw,(NUM_OF_POI_ICON[2]+ NUM_OF_POI_ICON[0]) / 2.0f * canvas.getWidth(),(NUM_OF_POI_ICON[3]+ NUM_OF_POI_ICON[1]) / 2.0f * canvas.getHeight(),overlayTextPaint);
        canvas.drawText( Double.toString(maxDistance),(NUM_OF_POI_ICON[2]+ NUM_OF_POI_ICON[0]) / 2.0f * canvas.getWidth()+250,(NUM_OF_POI_ICON[3]+ NUM_OF_POI_ICON[1]) / 2.0f * canvas.getHeight(),overlayTextPaint);
        canvas.drawText( Double.toString(maxDistance-rangeDistance),(NUM_OF_POI_ICON[2]+ NUM_OF_POI_ICON[0]) / 2.0f * canvas.getWidth()+250,(NUM_OF_POI_ICON[3]+ NUM_OF_POI_ICON[1]) / 2.0f * canvas.getHeight() - 100,overlayTextPaint);
    }
    

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN && !isTouched) {
            currentScrollY = event.getY();
            isTouched = true;
        }
        if(event.getAction()==MotionEvent.ACTION_MOVE && isTouched) {
            scrollY = (event.getY()- currentScrollY);

        }
        if(event.getAction()==MotionEvent.ACTION_UP && isTouched) {
            currentScrollY = 0;
            startDistance += scrollY;
            scrollY = 0;
            isTouched = false;
        }
        return true;
    }

    public List<PointOfInterest> getPointOfInterestList() {
        return pointOfInterestList;
    }

    public void setPointOfInterestList(List<PointOfInterest> pointOfInterestList) {
        this.pointOfInterestList = pointOfInterestList;
    }
}

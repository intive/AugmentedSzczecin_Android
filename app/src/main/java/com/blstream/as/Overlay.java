package com.blstream.as;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by Damian on 2015-04-01.
 */
public class Overlay extends Engine implements View.OnTouchListener {
    private static double defaultRangeDistance = 200.0;
    private static float screenHeightProp = 3.0f/4.0f;
    private static float[] rect = {0.1f,0.8f,0.2f,1.0f}; //left, top, right, bottom
    private static float distancePointRadius = 20.0f;
    private static float distanceTextSize = 17.0f;
    private static int distanceTextColor = Color.WHITE;
    private static float markerPointRadius = 4.0f;
    private static float lineWidth = 2.0f;
    private static int pointAndLineColor = Color.RED;
    private static int overlayColor = Color.BLUE;
    private static float overlayTextSize = 15.0f;
    private static int overlayTextColor = Color.WHITE;

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
        startDistance = defaultRangeDistance;
        rangeDistance = defaultRangeDistance;
        scrollY = 0.0f;
        isTouched = false;
        this.setOnTouchListener(this);
    }
    public void setupPaint() {
        overlayStylePaint = new Paint();
        overlayStylePaint.setColor(overlayColor);
        overlayTextPaint = new Paint();
        overlayTextPaint.setColor(overlayTextColor);
        overlayTextPaint.setTextSize(overlayTextSize);
        overlayTextPaint.setTextAlign(Paint.Align.CENTER);
        pointPaint = new Paint();
        pointPaint.setColor(pointAndLineColor);
        linePaint = new Paint();
        linePaint.setColor(pointAndLineColor);
        linePaint.setStrokeWidth(lineWidth);
        distanceTextPaint = new Paint();
        distanceTextPaint.setTextSize(distanceTextSize);
        distanceTextPaint.setColor(distanceTextColor);
        distanceTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //TODO change in release
        longitude = 15.236368;
        latitude = 53.411822;
        int numOfPoiDraw = 0;
        double maxDistance = startDistance + scrollY;
        for(PointOfInterest poi : pointOfInterestList) {
            int screenX = (int) (computeXCoordinate(poi.getLongitude(),poi.getLatitude()) * canvas.getWidth());
            int screenY = (int) ((1.0-computeYCoordinate(poi.getLongitude(),poi.getLatitude(), maxDistance - rangeDistance, maxDistance))*canvas.getHeight()*screenHeightProp);
            if((screenX < 0 || screenX > canvas.getWidth()) || (screenY < 0 || screenY > canvas.getHeight() * screenHeightProp))
                continue;

            //drawing POI's
            canvas.drawCircle(screenX,canvas.getHeight()*screenHeightProp, markerPointRadius,pointPaint);
            canvas.drawLine(screenX,canvas.getHeight()*screenHeightProp,screenX,screenY,linePaint);
            canvas.drawCircle(screenX,screenY,distancePointRadius,pointPaint);
            canvas.drawText(Integer.toString((int)Utils.computeDistanceInMeters(poi.getLongitude(), poi.getLatitude(), longitude, latitude)),screenX,screenY,distanceTextPaint);
            //TODO Change circle on bitmap
            canvas.drawCircle(screenX,screenY-(distancePointRadius/2)-30,30,overlayStylePaint);
            numOfPoiDraw++;
        }
        String numOfPoiNoDraw = Integer.toString(pointOfInterestList.size()-numOfPoiDraw);
        canvas.drawRect(rect[0]*canvas.getWidth(),rect[1]*getHeight(),rect[2]*canvas.getWidth(),rect[3]*getHeight(),overlayStylePaint);
        canvas.drawText(numOfPoiNoDraw,(rect[2]+rect[0]) / 2.0f * canvas.getWidth(),(rect[3]+rect[1]) / 2.0f * canvas.getHeight(),overlayTextPaint);
        canvas.drawText( Double.toString(maxDistance),(rect[2]+rect[0]) / 2.0f * canvas.getWidth()+250,(rect[3]+rect[1]) / 2.0f * canvas.getHeight(),overlayTextPaint);
        canvas.drawText( Double.toString(maxDistance-rangeDistance),(rect[2]+rect[0]) / 2.0f * canvas.getWidth()+250,(rect[3]+rect[1]) / 2.0f * canvas.getHeight() - 100,overlayTextPaint);
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

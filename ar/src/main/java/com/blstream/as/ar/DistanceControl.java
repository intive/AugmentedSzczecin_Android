package com.blstream.as.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DistanceControl extends View implements View.OnTouchListener {
    private static final double DEFAULT_MIN_DISTANCE = 0.0;
    private static final double DEFAULT_CURRENT_DISTANCE = 200.0;
    private static final double DEFAULT_MAX_DISTANCE = 1000.0;
    private static final double DEFAULT_RANGE_DISTANCE = 0.0;
    private static final float DEFAULT_DISTANCE_TEXT_SIZE = 15.0f;
    private static final int DEFAULT_DISTANCE_STEP = 20;
    private double rangeDistance;
    private double minDistance;
    private double maxDistance;
    private double lastDistance;
    private double currentDistance;

    private float currentScrollY;
    private float scrollY;
    private boolean isTouched;

    private Paint distanceTextPaint;

    public DistanceControl(Context context) {
        super(context);
        initRoll();
        setupPaint();
    }

    public DistanceControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRoll();
        setupPaint();
    }
    private void setupPaint() {
        distanceTextPaint = new Paint();
        distanceTextPaint.setTextAlign(Paint.Align.CENTER);
        distanceTextPaint.setTextSize(DEFAULT_DISTANCE_TEXT_SIZE);
    }
    private void initRoll() {
        minDistance = DEFAULT_MIN_DISTANCE;
        maxDistance = DEFAULT_MAX_DISTANCE;
        lastDistance = currentDistance = DEFAULT_CURRENT_DISTANCE;
        rangeDistance = DEFAULT_RANGE_DISTANCE;
        scrollY = 0.0f;
        isTouched = false;
        this.setOnTouchListener(this);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int rollValue = (int)getCurrentDistance();
        rollValue = (rollValue / DEFAULT_DISTANCE_STEP) * DEFAULT_DISTANCE_STEP;
        canvas.drawText( Integer.toString(rollValue),getWidth()/2,getHeight()/2,distanceTextPaint);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN && !isTouched) {
            currentScrollY = event.getY();
            isTouched = true;
        }
        if(event.getAction()==MotionEvent.ACTION_MOVE && isTouched) {
            scrollY = (event.getY()- currentScrollY);
            currentDistance = lastDistance + scrollY;
            invalidate();
        }
        if(event.getAction()==MotionEvent.ACTION_UP && isTouched) {
            currentScrollY = 0;
            lastDistance += scrollY;
            if(lastDistance - rangeDistance < minDistance)
                lastDistance = minDistance + rangeDistance;
            if(lastDistance + rangeDistance > maxDistance)
                lastDistance = maxDistance - rangeDistance;
            isTouched = false;
        }
        return true;
    }
    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getLowCurrentDistance() {
        if(currentDistance - rangeDistance < minDistance)
            return minDistance;
        return currentDistance - rangeDistance;
    }
    public double getCurrentDistance() {
        if(currentDistance + rangeDistance > maxDistance)
            return maxDistance - rangeDistance;
        if(currentDistance - rangeDistance < minDistance)
            return minDistance + rangeDistance;
        return currentDistance;
    }
    public double getHighCurrentDistance() {
        if(currentDistance + rangeDistance > maxDistance)
            return maxDistance;
        return currentDistance + rangeDistance;
    }
    public void setDistanceTextSize(float textSize) {
        distanceTextPaint.setTextSize(textSize);
    }
}


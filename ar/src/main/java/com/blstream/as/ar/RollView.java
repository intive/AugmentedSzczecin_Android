package com.blstream.as.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RollView extends View implements View.OnTouchListener {
    private static final double DEFAULT_MIN_DISTANCE = 0.0;
    private static final double DEFAULT_CURRENT_DISTANCE = 200.0;
    private static final double DEFAULT_MAX_DISTANCE = 1000.0;
    private static final double DEFAULT_RANGE_DISTANCE = 100.0;
    private static final float DEFAULT_DISTANCE_TEXT_SIZE = 15.0f;
    private static final double DEFAULT_DISTANCE_STEP = 20.0;
    private double rangeDistance;
    private double minDistance;
    private double maxDistance;
    private double lastDistance;
    private double currentDistance;

    private float currentScrollY;
    private float scrollY;
    private boolean isTouched;

    private Paint distanceTextPaint;

    public RollView(Context context) {
        super(context);
        initRoll();
        setupPaint();
    }

    public RollView(Context context, AttributeSet attrs) {
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
        canvas.drawText( Integer.toString((int)currentDistance),getWidth()/2,getHeight()/2,distanceTextPaint);

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
            if(currentDistance - rangeDistance < minDistance)
                currentDistance = minDistance + rangeDistance;
            if(currentDistance + rangeDistance > maxDistance)
                currentDistance = maxDistance - rangeDistance;
            if(Math.abs(lastDistance-currentDistance) > DEFAULT_DISTANCE_STEP) {
                lastDistance += scrollY;
                currentScrollY = 0;
                invalidate();
            }

        }
        if(event.getAction()==MotionEvent.ACTION_UP && isTouched) {
            currentScrollY = 0;
            lastDistance += scrollY;
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

        return currentDistance - rangeDistance;
    }
    public double getHighCurrentDistance() {

        return currentDistance + rangeDistance;
    }
    public void setDistanceTextSize(float textSize) {
        distanceTextPaint.setTextSize(textSize);
    }
}

package com.blstream.as.ar;

import android.graphics.Paint;

public class DistanceControl {
    public static final double DEFAULT_MIN_DISTANCE = 0.0;
    public static final double DEFAULT_MAX_DISTANCE = 1700.0;
    private static final float DEFAULT_DISTANCE_TEXT_SIZE = 15.0f;
    private double minDistance;
    private double maxDistance;
    private double currentDistance;

    private Paint distanceTextPaint;

    public DistanceControl() {
        init();
        setupPaint();
    }

    private void setupPaint() {
        distanceTextPaint = new Paint();
        distanceTextPaint.setTextAlign(Paint.Align.CENTER);
        distanceTextPaint.setTextSize(DEFAULT_DISTANCE_TEXT_SIZE);
    }
    private void init() {
        minDistance = DEFAULT_MIN_DISTANCE;
        maxDistance = DEFAULT_MAX_DISTANCE;
        currentDistance = DEFAULT_MAX_DISTANCE;
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

    public double getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(double currentDistance) {
        this.currentDistance = currentDistance;
    }
}


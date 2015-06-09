package com.blstream.as.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

import com.blstream.as.data.rest.model.Category;
import com.blstream.as.data.rest.model.SubCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blstream.com.as.ar.R;


public class Overlay extends Engine {
    public final static String TAG = Overlay.class.getSimpleName();

    private static final float SCREEN_HEIGHT_PROPORTIONS = 3.0f / 4.0f;
    private static final float DISTANCE_POINT_RADIUS = 20.0f;
    private static final float DISTANCE_TEXT_SIZE = 17.0f;
    private static final int DISTANCE_TEXT_COLOR = Color.WHITE;
    private static final float MARKER_POINT_RADIUS = 4.0f;
    private static final float LINE_WIDTH = 2.0f;
    private static final int MARKER_POINT_AND_LINE_COLOR = Color.RED;
    private static final float NUM_OF_POI_TEXT_SIZE = 21.0f;
    private static final int NUM_OF_POI_TEXT_COLOR = Color.WHITE;
    private static final int TRIANGLE_WIDTH = 80;
    private static final int NUM_OF_POI_TEXT_PADDING = 20;

    private List<PointOfInterest> pointOfInterestList;
    private Map<String, Bitmap> drawablesMap;
    private Paint pointPaint;
    private Paint linePaint;
    private Paint distanceTextPaint;
    private Paint overlayTextPaint;
    private Paint overlayStylePaint;
    private Path triangle;

    private boolean isOverlayEnabled;

    private DistanceControl distanceControl;

    public Overlay(Context context) {
        super(context);
    }

    public void setupPaint() {
        overlayStylePaint = new Paint();
        overlayStylePaint.setColor(getResources().getColor(R.color.red));
        overlayTextPaint = new Paint();
        overlayTextPaint.setColor(NUM_OF_POI_TEXT_COLOR);
        overlayTextPaint.setTextSize(NUM_OF_POI_TEXT_SIZE);
        overlayTextPaint.setTextAlign(Paint.Align.CENTER);
        overlayTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        pointPaint = new Paint();
        pointPaint.setColor(MARKER_POINT_AND_LINE_COLOR);
        linePaint = new Paint();
        linePaint.setColor(MARKER_POINT_AND_LINE_COLOR);
        linePaint.setStrokeWidth(LINE_WIDTH);
        distanceTextPaint = new Paint();
        distanceTextPaint.setTextSize(DISTANCE_TEXT_SIZE);
        distanceTextPaint.setColor(DISTANCE_TEXT_COLOR);
        distanceTextPaint.setTextAlign(Paint.Align.CENTER);
        setupShapes();
    }

    private void setupShapes() {
        drawablesMap = new HashMap<>();
        SubCategory[] subcategories = SubCategory.values();
        for(SubCategory subCategory : subcategories) {
            drawablesMap.put(subCategory.name(), BitmapFactory.decodeResource(getResources(), subCategory.getIdDrawableResource()));
        }
        Category[] categories = Category.values();
        for(Category Category : categories) {
            drawablesMap.put(Category.name(), BitmapFactory.decodeResource(getResources(), Category.getIdDrawableResource()));
        }
        triangle = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isOverlayEnabled) {
            return;
        }
        int numOfPoiDraw = 0;
        for (PointOfInterest poi : pointOfInterestList) {
            int screenX = (int) (computeXCoordinate(poi.getLongitude(), poi.getLatitude()) * getWidth());
            int screenY = (int) ((1.0 - computeYCoordinate(poi.getLongitude(), poi.getLatitude(), distanceControl.getMinDistance(), distanceControl.getCurrentDistance())) * getHeight() * SCREEN_HEIGHT_PROPORTIONS);
            if ((screenX < 0 || screenX > getWidth()) || (screenY < 0 || screenY > getHeight() * SCREEN_HEIGHT_PROPORTIONS))
                continue;
            canvas.drawCircle(screenX, getHeight() * SCREEN_HEIGHT_PROPORTIONS, MARKER_POINT_RADIUS, pointPaint);
            canvas.drawLine(screenX, getHeight() * SCREEN_HEIGHT_PROPORTIONS, screenX, screenY, linePaint);
            drawPoiSubCategoryIcon(canvas, screenX, screenY, poi);
            canvas.drawCircle(screenX, screenY, DISTANCE_POINT_RADIUS, pointPaint);
            canvas.drawText(Integer.toString((int) Utils.computeDistanceInMeters(poi.getLongitude(), poi.getLatitude(), getLongitude(), getLatitude())), screenX, screenY + DISTANCE_TEXT_SIZE / 2, distanceTextPaint);
            numOfPoiDraw++;
        }
        String numOfPoiNoDraw = Integer.toString(pointOfInterestList.size() - numOfPoiDraw);
        triangle.moveTo(0, getHeight());
        triangle.lineTo(TRIANGLE_WIDTH, getHeight());
        triangle.lineTo(0, getHeight() - TRIANGLE_WIDTH);
        triangle.close();
        canvas.drawPath(triangle, overlayStylePaint);
        canvas.drawText(numOfPoiNoDraw, NUM_OF_POI_TEXT_PADDING, getHeight() - NUM_OF_POI_TEXT_PADDING, overlayTextPaint);
    }

    private void drawPoiSubCategoryIcon(Canvas canvas, int x, int y, PointOfInterest pointOfInterest) {
        Bitmap bitmap;
        if(pointOfInterest.getSubCategoryName() != null) {
            bitmap = drawablesMap.get(pointOfInterest.getSubCategoryName());
        }
        else {
            bitmap = drawablesMap.get(pointOfInterest.getCategoryName());
        }
        canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getHeight(), overlayStylePaint);
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

    public void setDistanceControl(DistanceControl distanceControl) {
        this.distanceControl = distanceControl;
    }
}

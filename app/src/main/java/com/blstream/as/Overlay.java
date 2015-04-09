package com.blstream.as;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 2015-04-01.
 */
public class Overlay extends Engine implements View.OnTouchListener {
    private static double defaultRangeDistance = 2500.0;  //FIXME USE LARGE LETTERS FOR CONSTANTS, add final is constant
    private static float[] rect = {0.1f,0.8f,0.2f,1.0f}; //left, top, right, bottom
    private List<PointOfInterest> pointOfInterestList;
    private Paint overlayTextPaint;
    private Paint overlayStylePaint;
    //dynamic zoom
    private double rangeDistance;
    private double startDistance;
    private float currentScrollY;
    private float scrollY;
    private boolean isTouched;

    public Overlay(Context context) {
        super(context);
        startDistance = defaultRangeDistance;
        rangeDistance = defaultRangeDistance;
        scrollY = 0.0f;
        isTouched = false;
        this.setOnTouchListener(this);
    }
    public void setupPaint(float textSize,int textColor, int backgroundColor) {
        overlayTextPaint = new Paint();
        overlayTextPaint.setColor(textColor);
        overlayTextPaint.setTextSize(textSize);
        overlayTextPaint.setTextAlign(Paint.Align.CENTER);
        overlayStylePaint = new Paint();
        overlayStylePaint.setColor(backgroundColor);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int numOfPoiDraw = 0;
        double maxDistance = startDistance + scrollY;

        //FIXME Separate to methods to more complicated code
        for(PointOfInterest poi : pointOfInterestList) {
            int screenX = (int) (computeXCoordinate(poi.getLongitude(),poi.getLatitude()) * canvas.getWidth());
            int screenY = (int) (computeYCoordinate(poi.getLongitude(),poi.getLatitude(), maxDistance - rangeDistance, maxDistance)*canvas.getHeight());
            if((screenX < 0 || screenX > canvas.getWidth()) || (screenY < 0 || screenY > canvas.getHeight()))
                continue;
            poi.draw(canvas,screenX,screenY);
            numOfPoiDraw++;
        }
        String numOfPoiNoDraw = Integer.toString(pointOfInterestList.size()-numOfPoiDraw);
        canvas.drawRect(rect[0]*canvas.getWidth(),rect[1]*getHeight(),rect[2]*canvas.getWidth(),rect[3]*getHeight(),overlayStylePaint);
        canvas.drawText(numOfPoiNoDraw,(rect[2]+rect[0]) / 2.0f * canvas.getWidth(),(rect[3]+rect[1]) / 2.0f * canvas.getHeight(),overlayTextPaint);
        canvas.drawText( Double.toString(maxDistance),(rect[2]+rect[0]) / 2.0f * canvas.getWidth()+250,(rect[3]+rect[1]) / 2.0f * canvas.getHeight(),overlayTextPaint);
        canvas.drawText( Double.toString(maxDistance-rangeDistance),(rect[2]+rect[0]) / 2.0f * canvas.getWidth()+250,(rect[3]+rect[1]) / 2.0f * canvas.getHeight() - 100,overlayTextPaint);
    }
    public void loadPoi() {
        //FIXME Separate to methods to more complicated code, magic values
        pointOfInterestList = new ArrayList<>();
        PointOfInterest newPoi = new PointOfInterest(0,"Zespol szkol nr 2",getResources().getString(R.string.hotel),"opis",15.007831,53.339102);
        newPoi.setupPaint(30.0f, Color.RED,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"B14",getResources().getString(R.string.hotel),"opis",15.008942,53.338407);
        newPoi.setupPaint(30.0f, Color.GREEN,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"Poczta",getResources().getString(R.string.hotel),"opis",15.013475,53.340213);
        newPoi.setupPaint(30.0f, Color.GREEN,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"Orlen",getResources().getString(R.string.hotel),"opis",15.017123,53.339899);
        newPoi.setupPaint(30.0f, Color.GREEN,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"Dworzec",getResources().getString(R.string.hotel),"opis",15.031500,53.339618);
        newPoi.setupPaint(30.0f, Color.GREEN,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"Zloty smok",getResources().getString(R.string.hotel),"opis",15.043119,53.339297);
        newPoi.setupPaint(30.0f, Color.GREEN,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN && isTouched == false) {
            currentScrollY = -event.getY();
            isTouched = true;
        }
        if(event.getAction()==MotionEvent.ACTION_MOVE && isTouched == true) {
            scrollY = (-event.getY()- currentScrollY);

        }
        if(event.getAction()==MotionEvent.ACTION_UP && isTouched == true) {
            currentScrollY = 0;
            startDistance += scrollY;
            scrollY = 0;
            isTouched = false;
        }
        return true;
    }
}

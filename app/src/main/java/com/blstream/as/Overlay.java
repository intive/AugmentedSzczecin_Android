package com.blstream.as;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 2015-04-01.
 */
public class Overlay extends Engine {
    private List<PointOfInterest> pointOfInterestList;

    public Overlay(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(PointOfInterest poi : pointOfInterestList) {
            int screenX = (int) (computeXCoordinate(poi.getLongitude(),poi.getLatitude()) * canvas.getWidth());
            int screenY = (int)0.5*canvas.getHeight();
            poi.draw(canvas,screenX,screenY);
        }
    }
    public void loadPoi() {
        pointOfInterestList = new ArrayList<>();
        PointOfInterest newPoi = new PointOfInterest(0,"Zespol szkol nr 2",getResources().getString(R.string.hotel),"opis",15.007831,53.339102);
        newPoi.setupPaint(30.0f, Color.RED,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
        newPoi = new PointOfInterest(0,"B14",getResources().getString(R.string.hotel),"opis",15.008942,53.338407);
        newPoi.setupPaint(30.0f, Color.GREEN,2.0f,Color.BLUE);
        pointOfInterestList.add(newPoi);
    }
}

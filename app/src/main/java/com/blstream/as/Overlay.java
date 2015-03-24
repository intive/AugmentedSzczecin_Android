package com.blstream.as;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Overlay extends View implements SurfaceHolder.Callback {
    private static final String TAG = "Overlay";
    private Paint paint;
    private Canvas canvas;
    private int middleOfViewX, middleOfViewY;
    //temp solution
    private List<PoiInfo> poiList;
    public Overlay(Context context) {
        super(context);
        paint = new Paint();
        loadPoi();
    }
    //temp solution
    private void loadPoi()
    {
        poiList = new ArrayList<>();
        poiList.add(new PoiInfo(R.drawable.ar_icon,"Helios",40,50.5));
        poiList.add(new PoiInfo(R.drawable.ar_icon,"Multikino",400.0,60.0));
    }
    private Bitmap getBitmap(int resourceID) {
        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(resourceID);
        return bd.getBitmap();
    }
    private void drawPOI(PoiInfo poiObject) {
        int x = (int)poiObject.getLength(),y = 0;
        canvas.drawText(poiObject.getName(),middleOfViewX-x,middleOfViewY,paint);
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.canvas = canvas;
        middleOfViewX = canvas.getWidth()/2;
        middleOfViewY = canvas.getHeight()/2;
        for(PoiInfo poi : poiList) {
            drawPOI(poi);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        invalidate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

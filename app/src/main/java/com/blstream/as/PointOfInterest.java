package com.blstream.as;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Damian on 2015-04-01.
 */
public class PointOfInterest {
    private int id;
    private String name;
    private String type;
    private String description;
    private double latitude;
    private double longitude;

    private Paint paintPoint;
    private Paint paintLine;
    private Paint paintText;

    public void setupPaint(float textSize,int textColor, float lineWidth, int lineColor) {
        //point
        paintPoint = new Paint();
        paintPoint.setColor(Color.BLUE);
        //line
        paintLine = new Paint();
        paintLine.setColor(lineColor);
        paintLine.setStrokeWidth(lineWidth);
        //text
        paintText = new Paint();
        paintText.setColor(textColor);
        paintText.setTextSize(textSize);
        paintText.setTextAlign(Paint.Align.CENTER);
    }

    public PointOfInterest(int id, String name, String type, String description, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void draw(Canvas canvas, int screenX, int screenY) {
        canvas.drawText(name, screenX, screenY, paintText);
        canvas.drawCircle(screenX, screenY, 50, paintLine);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

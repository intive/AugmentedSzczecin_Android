package com.blstream.as;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class PoiPreviewLayout extends SlidingUpPanelLayout{

    private boolean isPoiActivated;

    public PoiPreviewLayout(Context context) {
        super(context);
    }

    public PoiPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PoiPreviewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return ev.getActionMasked() == MotionEvent.ACTION_DOWN && isPoiActivated && super.onInterceptTouchEvent(ev);
    }

    public void setPoiActivated(boolean isPoiActivated) {
        this.isPoiActivated = isPoiActivated;
    }
}

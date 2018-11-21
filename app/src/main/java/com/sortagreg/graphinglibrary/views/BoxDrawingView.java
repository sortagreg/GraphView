package com.sortagreg.graphinglibrary.views;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BoxDrawingView extends View {
    private static final String TAG = "TAGbdf";

    private Box currentBox;
    private List<Box> boxen = new ArrayList<>();

    public BoxDrawingView(Context context) {
        super(context);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "none";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ad";
                currentBox = new Box(current);
                boxen.add(currentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentBox != null) {
                    currentBox.setCurrent(current);
                    invalidate();
                }
                action = "am";
                break;
            case MotionEvent.ACTION_UP:
                action = "au";
                currentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ac";
                currentBox = null;
                break;
        }
        Log.d(TAG, "onTouchEvent: " + action + " at x:" + current.x + " y:" + current.y);
        return true;
    }
}

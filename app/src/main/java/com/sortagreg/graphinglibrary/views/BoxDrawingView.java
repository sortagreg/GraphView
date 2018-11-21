package com.sortagreg.graphinglibrary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private Paint boxPaint;
    private Paint backgroundPaint;

    public BoxDrawingView(Context context) {
        super(context);
        setPaints();
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaints();
    }

    private void setPaints() {
        boxPaint = new Paint();
        boxPaint.setColor(0x22ff0000);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundPaint);
        drawBoxes(canvas);
    }

    private void drawBoxes(Canvas canvas) {
        for (Box box : boxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, boxPaint);
        }
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

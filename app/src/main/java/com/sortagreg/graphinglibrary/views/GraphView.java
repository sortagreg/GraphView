package com.sortagreg.graphinglibrary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
    private Paint axisPaint = new Paint();
    private Paint markerPaint = new Paint();

    private int numberOfVerticalMarkers = 3;

    public GraphView(Context context) {
        super(context);
        setPaintLines();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaintLines();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawAxes(canvas);
        drawMarkers(canvas);
    }

    private void drawMarkers(Canvas canvas) {
        int graphWidth = canvas.getWidth() - 200;
        int markerSpacing = graphWidth / (numberOfVerticalMarkers + 1);
        for (int i = 1; i <= 3; i++) {
            int startX = 200 + (i * markerSpacing);
            int startY = 100;
            int endX = startX;
            int endY = canvas.getHeight() - 200 - 100;
            canvas.drawLine(startX, startY, endX, endY, markerPaint);
        }
    }

    private void setPaintLines() {
        axisPaint.setColor(0xff000000);
        axisPaint.setStrokeWidth(15.0f);
        markerPaint.setColor(0xffd3d3d3);
        markerPaint.setStrokeWidth(5.0f);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(200, 0, 200, canvas.getHeight() - 200, axisPaint);
        canvas.drawLine(200, canvas.getHeight() - 200, canvas.getWidth(), canvas.getHeight() - 200, axisPaint);
    }
}

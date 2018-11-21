package com.sortagreg.graphinglibrary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
    private Paint axisPaint = new Paint();
    private Paint markerPaint = new Paint();

    private int topAxisMargin = 100;
    private int bottomAxisMargin = 200;
    private int leftAxisMargin = 200;
    private int rightAxisMargin = 100;

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
        drawVerticalMarkers(canvas);
    }

    private void drawVerticalMarkers(Canvas canvas) {
        // width of the data portion of the graph
        int graphWidth = canvas.getWidth() - leftAxisMargin - rightAxisMargin;
        // calculate distance between markers
        int markerSpacing = graphWidth / (numberOfVerticalMarkers + 1);
        // print vertical markers
        for (int i = 1; i <= numberOfVerticalMarkers; i++) {
            int startX = leftAxisMargin + (i * markerSpacing);
            int startY = topAxisMargin + 100; // add 100 to give some distance between axis and markers
            int endX = startX;
            int endY = canvas.getHeight() - bottomAxisMargin - 100; // sub 100 for same reason add 100 earlier
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
        // vertical axis T->B
        canvas.drawLine(leftAxisMargin, topAxisMargin, leftAxisMargin, canvas.getHeight() - bottomAxisMargin, axisPaint);
        // horizontal axis L->R
        canvas.drawLine(leftAxisMargin, canvas.getHeight() - bottomAxisMargin, canvas.getWidth() - rightAxisMargin, canvas.getHeight() - bottomAxisMargin, axisPaint);
    }
}

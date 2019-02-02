package com.sortagreg.graphinglibrary.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sortagreg.graphview.GraphViewDataModel;

import java.util.ArrayList;
import java.util.List;

import static com.sortagreg.graphview.GraphViewDataModel.CONSTANT_LINE;
import static com.sortagreg.graphview.GraphViewDataModel.STANDARD_LINE;
import static com.sortagreg.graphview.GraphViewDataModel.STATE_LINE;
import static com.sortagreg.graphview.GraphViewDataModel.UNFOLDED_LINE;

//TODO update to handle empty data set list. Labels currently render wrong if there is no data.

/**
 * GraphView - Custom Graph View Class
 *
 * @author Marshall Ladd
 */
public class GraphView extends View {
    private Context context;
    private static final String TAG = "GraphView";

    // Configurables
    private String title = "";
    private String rightSideText = "";
    private String leftSideText = "";
    private String bottomText = "";

    private Paint axisPaint = new Paint();
    private Paint markerPaint = new Paint();
    private Paint dataSetPaint = new Paint();

    public static final float DEFAULT_TOP_MARGIN = 75f;
    public static final float DEFAULT_BOTTOM_MARGIN = 175f;
    public static final float DEFAULT_LEFT_MARGIN = 175f;
    public static final float DEFAULT_RIGHT_MARGIN = 175f;
    public static final float DEFAULT_GRAPH_PADDING_FACTOR = 0f;
    private float topAxisMargin;
    private float bottomAxisMargin;
    private float leftAxisMargin;
    private float rightAxisMargin;
    private float graphPaddingFactor;

    public static final int DEFAULT_NUMBER_VERT_MARKERS = 15;
    public static final int DEFAULT_NUMBER_HORI_MARKERS = 15;
    private int numberOfVerticalMarkers;
    private int numberOfHorizontalMarkers;

    public static final int DEFAULT_NUMBER_VERT_LABELS = 15;
    public static final int DEFAULT_NUMBER_HORI_LABELS = 15;
    public static final int DEFAULT_NUMBER_BOTTOM_LABELS = 15;
    public static final int DEFAULT_NUMBER_RIGHT_SIDE_LABELS = 0;
    public static final int STANDARD_LABELS = 0;
    public static final int UNFOLDED_LABELS = 1;
    public static final int CUSTOM_LABELS = 2;
    private int numberOfVerticalLabels;
    private int numberOfHorizontalLabels;
    private int numberOfRightSideLabels;
    private int numberOfBottomLabels;
    private int labelStyle;

    private boolean shouldDrawBox;

    // Calculated values
    private List<com.sortagreg.graphinglibrary.views.GraphViewDataModel> dataSetList;
    private float dataSetMinX = Float.MAX_VALUE;
    private float dataSetMaxX = Float.MIN_VALUE;
    private float dataSetMinY = Float.MAX_VALUE;
    private float dataSetMaxY = Float.MIN_VALUE;
    private float adjustedDataSetMinX;
    private float adjustedDataSetMaxX;
    private float adjustedDataSetMinY;
    private float adjustedDataSetMaxY;
    private float rangeOfXValues;
    private float rangeOfYValues;

    /**
     * Constructor for a GraphView in code.
     *
     * Will be used if the GraphView is created in code.
     *
     * @param context
     */
    public GraphView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    /**
     * Constructor for a GraphView in XML.
     *
     * Will be used if GraphView is created in an XML resource.
     * Values that can be set in an XML file are made available
     * by declaring them in the res/values/attrs.xml
     *
     * @param context
     * @param attrs values from the XML set.
     */
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    /**
     * Method used to initialize all parameters on creation.
     *
     * Doing this during creation helps keep onDraw smaller
     * which makes the View more optimized.
     *
     * @param attrs values from the XML set.
     */
    private void init(@Nullable AttributeSet attrs) {
        // Enables custom attributes to be saved across app states
        setSaveEnabled(true);

        // Init custom attributes from XML here
        if (attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.sortagreg.graphview.R.styleable.GraphView);
        numberOfHorizontalMarkers = typedArray.getInteger(com.sortagreg.graphview.R.styleable.GraphView_numberOfHorizontalMarkers, DEFAULT_NUMBER_HORI_MARKERS);
        numberOfVerticalMarkers = typedArray.getInteger(com.sortagreg.graphview.R.styleable.GraphView_numberOfVerticalMarkers, DEFAULT_NUMBER_VERT_MARKERS);
        numberOfHorizontalLabels = typedArray.getInteger(com.sortagreg.graphview.R.styleable.GraphView_numberOfHorizontalLabels, DEFAULT_NUMBER_HORI_LABELS);
        numberOfVerticalLabels = typedArray.getInteger(com.sortagreg.graphview.R.styleable.GraphView_numberOfVerticalLabels, DEFAULT_NUMBER_VERT_LABELS);
        numberOfRightSideLabels = typedArray.getInteger(com.sortagreg.graphview.R.styleable.GraphView_numberOfRightSideLabels, DEFAULT_NUMBER_RIGHT_SIDE_LABELS);
        topAxisMargin = typedArray.getFloat(com.sortagreg.graphview.R.styleable.GraphView_axisMarginTop, DEFAULT_TOP_MARGIN);
        bottomAxisMargin = typedArray.getFloat(com.sortagreg.graphview.R.styleable.GraphView_axisMarginBottom, DEFAULT_BOTTOM_MARGIN);
        rightAxisMargin = typedArray.getFloat(com.sortagreg.graphview.R.styleable.GraphView_axisMarginRight, DEFAULT_RIGHT_MARGIN);
        leftAxisMargin = typedArray.getFloat(com.sortagreg.graphview.R.styleable.GraphView_axisMarginLeft, DEFAULT_LEFT_MARGIN);
        graphPaddingFactor = typedArray.getFloat(com.sortagreg.graphview.R.styleable.GraphView_graphPaddingFactor, DEFAULT_GRAPH_PADDING_FACTOR);
        shouldDrawBox = typedArray.getBoolean(com.sortagreg.graphview.R.styleable.GraphView_shouldDrawBox, false);
        labelStyle = typedArray.getInteger(com.sortagreg.graphview.R.styleable.GraphView_labelStyle, STANDARD_LABELS);
        title = typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_title) != null ? typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_title) : "";
        bottomText = typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_bottomText) != null ? typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_bottomText) : "";
        rightSideText = typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_rightSideText) != null ? typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_rightSideText) : "";
        leftSideText = typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_leftSideText) != null ? typedArray.getString(com.sortagreg.graphview.R.styleable.GraphView_leftSideText) : "";
        typedArray.recycle();

        // Init other values here
        setPaintLines();
        dataSetList = new ArrayList<>();
    }

    /**
     * Overridden method that is called by the system.  This draws your View.
     *
     * Can be invoked by calling invalidate(), but this can be an expensive operation.
     *
     * @param canvas Canvas Object to be drawn to.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        drawVerticalMarkers(canvas);
        drawHorizontalMarkers(canvas);
        drawDataSets(canvas);
        drawAxes(canvas);
        if (!dataSetList.isEmpty()) {
            switch (labelStyle) {
                case STANDARD_LABELS:
                    drawStandardTextLabels(canvas);
                    break;
                case UNFOLDED_LABELS:
                    drawUnfoldedTextLabels(canvas);
                    break;
                case CUSTOM_LABELS:
                    Log.w(TAG, "onDraw: Custom label is not implemented yet. Using standard by default");
                    // TODO add custom label ability
                    // break;
                default:
                    drawStandardTextLabels(canvas);
            }
            drawRightSideLabels(canvas);
            drawKeyLabels(canvas);
        }
    }

    /**
     * Update the number of vertical cross markers are displayed in the GraphView
     *
     * @param numberOfVerticalMarkers int Number of vertical lines to display.
     */
    public void setNumberOfVerticalMarkers(int numberOfVerticalMarkers) {
        this.numberOfVerticalMarkers = numberOfVerticalMarkers;
        invalidate();
    }

    /**
     * Update the number of vertical cross markers are displayed in the GraphView
     *
     * @param numberOfHorizontalMarkers int Number of vertical lines to display.
     */
    public void setNumberOfHorizontalMarkers(int numberOfHorizontalMarkers) {
        this.numberOfHorizontalMarkers = numberOfHorizontalMarkers;
        invalidate();
    }

    /**
     * Update the number of labels to draw along the y axis
     *
     * @param numberOfVerticalLabels
     */
    public void setNumberOfVerticalLabels(int numberOfVerticalLabels) {
        this.numberOfVerticalLabels = numberOfVerticalLabels;
        invalidate();
    }

    public void setNumberOfRightSideLabels(int numberOfRightSideLabels) {
        this.numberOfRightSideLabels = numberOfRightSideLabels;
        invalidate();
    }

    /**
     * Update the number of labels to draw along the x axis
     *
     * @param numberOfHorizontalLabels
     */
    public void setNumberOfHorizontalLabels(int numberOfHorizontalLabels) {
        this.numberOfHorizontalLabels = numberOfHorizontalLabels;
        invalidate();
    }

    /**
     * Switch the graph between the different label styles
     *
     * @param labelStyle
     */
    public void setLabelStyle(int labelStyle) {
        this.labelStyle = labelStyle;
        invalidate();
    }

    /**
     * If true, the graph will be surrounded by a border on all 4 sides.  If false,
     * a border will only print on the X and Y axis.
     *
     * @param shouldDrawBox
     */
    public void setShouldDrawBox(boolean shouldDrawBox) {
        this.shouldDrawBox = shouldDrawBox;
        invalidate();
    }

    /**
     * Set the width of the margin on the top of the graph
     *
     * @param topAxisMargin
     */
    public void setTopAxisMargin(float topAxisMargin) {
        this.topAxisMargin = topAxisMargin;
        invalidate();
    }

    /**
     * Set the width of the margin on the bottom of the graph
     *
     * @param bottomAxisMargin
     */
    public void setBottomAxisMargin(float bottomAxisMargin) {
        this.bottomAxisMargin = bottomAxisMargin;
        invalidate();
    }

    /**
     * Set the width of the margin on the left side of the graph
     *
     * @param leftAxisMargin
     */
    public void setLeftAxisMargin(float leftAxisMargin) {
        this.leftAxisMargin = leftAxisMargin;
        invalidate();
    }

    /**
     * Set the width of the margin on the right side of the graph
     *
     * @param rightAxisMargin
     */
    public void setRightAxisMargin(float rightAxisMargin) {
        this.rightAxisMargin = rightAxisMargin;
        invalidate();
    }

    /**
     * Update how much padding a graph should have.
     *
     * @param graphPaddingFactor float value from 0.0(no padding) or greater than.
     */
    public void setGraphPaddingFactor(float graphPaddingFactor) {
        this.graphPaddingFactor = graphPaddingFactor;
        invalidate();
    }

    /**
     * Update the text value of the title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    public void setRightSideText(String rightSideText) {
        this.rightSideText = rightSideText;
        invalidate();
    }

    public void setLeftSideText(String leftSideText) {
        this.leftSideText = leftSideText;
        invalidate();
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        invalidate();
    }

    public void setNumberOfBottomLabels(int numberOfBottomLabels) {
        this.numberOfBottomLabels = numberOfBottomLabels;
        invalidate();
    }

    /**
     * Add a data set to the graph tpo be drawn.
     *
     * If you are adding more than one data set to the graph, you should use
     * addToDataSetListBulk() to be more efficient.
     *
     * @param dataSet
     */
    public void addToDataSetList(com.sortagreg.graphinglibrary.views.GraphViewDataModel dataSet) {
        this.dataSetList.add(dataSet);
        invalidate();
    }

    /**
     * Add a group of data sets to the graph to be drawn.
     *
     * @param dataSetList
     */
    public void addToDataSetListBulk(List<com.sortagreg.graphinglibrary.views.GraphViewDataModel> dataSetList) {
        this.dataSetList.addAll(dataSetList);
        invalidate();
    }

    /**
     * Draws the vertical markers to the Canvas.
     *
     * Number of markers is configurable in Java and XML. DEFAULT: 5
     *
     * @param canvas Canvas Object to be drawn to
     */
    private void drawVerticalMarkers(Canvas canvas) {
        // width of the data portion of the graph
        int graphWidth = canvas.getWidth() - (int) leftAxisMargin - (int) rightAxisMargin;
        // calculate distance between markers
        int markerSpacing = graphWidth / (numberOfVerticalMarkers + 1);
        // print vertical markers
        for (int i = 1; i <= numberOfVerticalMarkers; i++) {
            int startX = (int) leftAxisMargin + (i * markerSpacing);
            int startY = (int) topAxisMargin;// + 50; // add 50 to give some distance between axis and markers
            int endX = startX;
            int endY = canvas.getHeight() - (int) bottomAxisMargin;// - 50; // sub 50 for same reason add 100 earlier
            canvas.drawLine(startX, startY, endX, endY, markerPaint);
        }
    }

    /**
     * Draws the horizontal markers to the Canvas.
     *
     * Number of markers is configurable in Java and XML. DEFAULT: 10
     *
     * @param canvas Canvas Object to be drawn to
     */
    private void drawHorizontalMarkers(Canvas canvas) {
        int graphHeight = canvas.getHeight() - (int) topAxisMargin - (int) bottomAxisMargin;
        int markerSpacing = graphHeight / (numberOfHorizontalMarkers + 1);
        for (int i = numberOfHorizontalMarkers; i > 0; i--) {
            int startY = (int) topAxisMargin + (i * markerSpacing);
            int startX = (int) leftAxisMargin;// + 50;
            int endY = startY;
            int endX = canvas.getWidth() - (int) rightAxisMargin;
            canvas.drawLine(startX, startY, endX, endY, markerPaint);
        }
    }

    /**
     * Draws the border of the GraphView.
     *
     * @param canvas Canvas Object to be drawn to
     */
    private void drawAxes(Canvas canvas) {
        // vertical axis T->B
        canvas.drawLine(leftAxisMargin, topAxisMargin, leftAxisMargin, canvas.getHeight() - bottomAxisMargin, axisPaint);
        // horizontal axis L->R
        canvas.drawLine(leftAxisMargin, canvas.getHeight() - bottomAxisMargin, canvas.getWidth() - rightAxisMargin, canvas.getHeight() - bottomAxisMargin, axisPaint);
        if (shouldDrawBox) {
            canvas.drawLine(leftAxisMargin, topAxisMargin, canvas.getWidth() - rightAxisMargin, topAxisMargin, axisPaint);
            canvas.drawLine(canvas.getWidth() - rightAxisMargin, topAxisMargin, canvas.getWidth() - rightAxisMargin, canvas.getHeight() - bottomAxisMargin, axisPaint);
        }
    }

    /**
     * Draws the various data sets to the Canvas Object
     *
     * @param canvas Canvas Object to be drawn to
     */
    private void drawDataSets(Canvas canvas) {
        getStatsOnAllDataSets();
        for (com.sortagreg.graphinglibrary.views.GraphViewDataModel dataModel : dataSetList) {
            switch (dataModel.getGraphType()) {
                case STANDARD_LINE:
                    drawStandardLine(canvas, dataModel);
                    break;
                case UNFOLDED_LINE:
                    drawUnfoldedLine(canvas, dataModel);
                    break;
                case CONSTANT_LINE:
                    drawConstantLine(canvas, dataModel);
                    break;
                case STATE_LINE:
                    drawBinaryStateLine(canvas, dataModel);
                    break;
            }


        }
    }

    /**
     * Draws a line showing the state of something denoted by 1 or 0
     *
     * Data set for this should be an array with the y coordinate is either a 1 or 0 to denote a state
     * of something.  Graph will display a constant line near the bottom the graph for a 0 or a constant
     * line near the top for a 1.
     *
     * @param canvas
     * @param dataModel
     */
    private void drawBinaryStateLine(Canvas canvas, com.sortagreg.graphinglibrary.views.GraphViewDataModel dataModel) {
        for (int i = 0; i < dataModel.getDataSet().length - 1; i ++) {
            float pixelsPerX = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (dataModel.getDataSet().length - 1);

            PointF startPoint = new PointF(leftAxisMargin + ((float) i * pixelsPerX), dataModel.getDataSet()[i].y == 0 ? ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .15f) + topAxisMargin) : ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .85f) + topAxisMargin));
            PointF endPoint = new PointF(leftAxisMargin + ((float) (i + 1) * pixelsPerX), dataModel.getDataSet()[i + 1].y == 0 ? ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .15f) + topAxisMargin) : ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .85f) + topAxisMargin));

            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
        }
    }

    /**
     * Draws a graph incrementally, taking each point in sequence, and displays (sequence value, actual Y)
     *
     * @param canvas
     * @param dataModel
     */
    private void drawUnfoldedLine(Canvas canvas, com.sortagreg.graphinglibrary.views.GraphViewDataModel dataModel) {
        for (int i = 0; i < dataModel.getDataSet().length - 1; i++) {
            float pixelsPerX = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (dataModel.getDataSet().length - 1);
            float pixelsPerY = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;

            float startX = leftAxisMargin + (float) i * pixelsPerX;
            float startY = (float) canvas.getHeight() - bottomAxisMargin - (dataModel.getDataSet()[i].y - adjustedDataSetMinY) * pixelsPerY;
            float endX = leftAxisMargin + ((float) i + 1f) * pixelsPerX;
            float endY = (float) canvas.getHeight() - bottomAxisMargin - (dataModel.getDataSet()[i + 1].y - adjustedDataSetMinY) * pixelsPerY;

            PointF startPoint = new PointF(startX, startY);
            PointF endPoint = new PointF(endX, endY);

            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
        }
    }

    /**
     * Takes a single point in a GraphViewDataModel and displays a constant based on the point's Y
     *
     * @param canvas
     * @param dataModel
     */
    private void drawConstantLine(Canvas canvas, com.sortagreg.graphinglibrary.views.GraphViewDataModel dataModel) {
        float pixelsPerY = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / (rangeOfYValues);

        float startX = leftAxisMargin;
        float startY = (float) canvas.getHeight() - bottomAxisMargin - (dataModel.getDataSet()[0].y - adjustedDataSetMinY) * pixelsPerY;
        float endX = (float) canvas.getWidth() - rightAxisMargin;
        float endY = (float) canvas.getHeight() - bottomAxisMargin - (dataModel.getDataSet()[0].y - adjustedDataSetMinY) * pixelsPerY;

        PointF startPoint = new PointF(startX, startY);
        PointF endPoint = new PointF(endX, endY);

        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
    }

    /**
     * Draws a line from a data set, using (X,Y) pairs
     *
     * @param canvas
     * @param dataModel
     */
    private void drawStandardLine(Canvas canvas, com.sortagreg.graphinglibrary.views.GraphViewDataModel dataModel) {
        for (int i = 0; i < dataModel.getDataSet().length - 1; i++) {
            float pixelsPerX = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / rangeOfXValues;
            float pixelsPerY = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;

            float startX = (dataModel.getDataSet()[i].x - adjustedDataSetMinX) * pixelsPerX + leftAxisMargin;
            float startY = (float) canvas.getHeight() - bottomAxisMargin - (dataModel.getDataSet()[i].y - adjustedDataSetMinY) * pixelsPerY;
            float endX = (dataModel.getDataSet()[i + 1].x - adjustedDataSetMinX) * pixelsPerX + leftAxisMargin;
            float endY = (float) canvas.getHeight() - bottomAxisMargin - (dataModel.getDataSet()[i + 1].y - adjustedDataSetMinY) * pixelsPerY;

            PointF startPoint = new PointF(startX, startY);
            PointF endPoint = new PointF(endX, endY);

            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
        }
    }

    /**
     * Draws the X and Y labels base on the max and min of the data set and the title
     *
     * @param canvas
     */
    private void drawStandardTextLabels(Canvas canvas) {
        // TODO split method to drawX, drawY, drawTitle

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);
        // Y-Axis labels
        if (numberOfVerticalLabels > 0) {
            float pixelsPerLabel = (canvas.getHeight() - topAxisMargin - bottomAxisMargin) / (float) numberOfVerticalLabels;
            float valuePerStep = rangeOfYValues / numberOfVerticalLabels;
            for (int i = 1; i <= numberOfVerticalLabels; i++) {
                int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinY);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, (canvas.getHeight() - bottomAxisMargin) - ((float) i * pixelsPerLabel) + 20f, textPaint);
            }
        }
        // X-Axis labels
        if (numberOfHorizontalLabels > 0) {
            float pixelsPerLabel = (canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (float) numberOfHorizontalLabels;
            float valuePerStep = rangeOfXValues / numberOfHorizontalLabels;
            for (int i = 1; i <= numberOfHorizontalLabels; i++) {
                int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinX);
                canvas.rotate(270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
            }
        }
        // Title label
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(title, canvas.getWidth() / 2f, topAxisMargin / 2f, textPaint);
    }

    /**
     * Draws X labels based on the exact values in the first data set in the dataSetList.
     * The Y labels are drawn by min and max values.
     *
     * @param canvas
     */
    private void drawUnfoldedTextLabels(Canvas canvas) {
        // TODO split method to drawX, drawY, drawTitle

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);
        // Y-Axis labels
        if (numberOfVerticalLabels > 0) {
            float pixelsPerLabel = ((float) canvas.getHeight() - topAxisMargin -  bottomAxisMargin) / (float) numberOfVerticalLabels;
            float valuePerStep = rangeOfYValues / numberOfVerticalLabels;
            for (int i = 1; i <= numberOfVerticalLabels; i++) {
                int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinY);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, (canvas.getHeight() - bottomAxisMargin) - ((float) i * pixelsPerLabel) + 20f, textPaint);
            }
        }
        // X-Axis labels
        if (numberOfHorizontalLabels > 0) {
            float pixelsPerLabel = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (float) numberOfHorizontalLabels;
            float valuePerStep = dataSetList.get(0).getDataSet().length / numberOfHorizontalLabels;
            for (int i = 1; i <= numberOfHorizontalLabels; i++) {
                int labelValue = (int) dataSetList.get(0).getDataSet()[i * (int) valuePerStep].x;
                canvas.rotate(270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
            }
        }
        // Title label
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(title, canvas.getWidth() / 2f, topAxisMargin / 2f, textPaint);
    }

    private void drawRightSideLabels(Canvas canvas) {
        if (numberOfRightSideLabels <= 0) return;
        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);

        float pixelsPerLabel = ((float) canvas.getHeight() - topAxisMargin -  bottomAxisMargin) / (float) numberOfRightSideLabels;
        float valuePerStep = rangeOfYValues / numberOfRightSideLabels;
        for (int i = 1; i <= numberOfRightSideLabels; i++) {
            int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinY);
            canvas.drawText(String.valueOf(labelValue), canvas.getWidth() - rightAxisMargin + 10f, (canvas.getHeight() - bottomAxisMargin) - ((float) i * pixelsPerLabel) + 20f, textPaint);
        }
    }

    private void drawKeyLabels(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        canvas.drawText(title, canvas.getWidth() / 2f, topAxisMargin / 2f, textPaint);
        canvas.drawText(bottomText, canvas.getWidth() / 2f, canvas.getHeight() - 20f, textPaint);
        canvas.rotate(270, 40f, canvas.getHeight() / 2f);
        canvas.drawText(leftSideText, 40f, canvas.getHeight() / 2f, textPaint);
        canvas.rotate(-270, 40f, canvas.getHeight() / 2f);
        canvas.rotate(270, canvas.getWidth() - 20f, canvas.getHeight() / 2f);
        canvas.drawText(rightSideText, canvas.getWidth() - 20f, canvas.getHeight() / 2f, textPaint);
        canvas.rotate(-270, canvas.getWidth() - 20f, canvas.getHeight() / 2f);
    }

    /**
     * Find and set the largest and smallest values to be found in all the data sets.
     */
    private void getStatsOnAllDataSets() {
        for (com.sortagreg.graphinglibrary.views.GraphViewDataModel dataSet : dataSetList) {
            for (PointF dataPoint : dataSet.getDataSet()) {
                if (dataSet.getGraphType() == STANDARD_LINE) {
                    dataSetMaxX = Math.max(dataSetMaxX, dataPoint.x);
                    dataSetMinX = Math.min(dataSetMinX, dataPoint.x);
                }
                if (dataSet.getGraphType() != STATE_LINE) {
                    dataSetMaxY = Math.max(dataSetMaxY, dataPoint.y);
                    dataSetMinY = Math.min(dataSetMinY, dataPoint.y);
                }
            }
        }
        // Use these values when calculating range of values and converting PointF objects.
        // Otherwise, comment these variables out and replace with normal dataSetMax/Min.
        adjustedDataSetMinX = dataSetMinX - Math.abs(dataSetMinX * graphPaddingFactor);
        adjustedDataSetMinY = dataSetMinY - Math.abs(dataSetMinY * graphPaddingFactor);
        adjustedDataSetMaxX = dataSetMaxX + Math.abs(dataSetMaxX * graphPaddingFactor);
        adjustedDataSetMaxY = dataSetMaxY + Math.abs(dataSetMaxY * graphPaddingFactor);

        rangeOfXValues = adjustedDataSetMaxX - adjustedDataSetMinX;
        rangeOfYValues = adjustedDataSetMaxY - adjustedDataSetMinY;
    }

    /**
     * Initialize the Paint Objects.
     */
    private void setPaintLines() {
        // TODO Make all paints configurable
        axisPaint.setColor(0xff000000);
        axisPaint.setStrokeWidth(5.0f);
        markerPaint.setColor(0xAAd3d3d3);
        markerPaint.setStrokeWidth(2.0f);
        dataSetPaint.setColor(0xFF00A9FF);
        dataSetPaint.setStrokeWidth(5.0f);
    }

    /**
     * Overridden method to save custom attributes across states.
     *
     * Saves all the custom attributes that can be set to a custom
     * SavedState Class.
     *
     * @return Parcelable GraphViewSavedState
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        GraphView.GraphViewSavedState savedState = new GraphView.GraphViewSavedState(superState);
        savedState.numberOfVerticalMarkers = numberOfVerticalMarkers;
        savedState.numberOfHorizontalMarkers = numberOfHorizontalMarkers;
        savedState.topAxisMargin = topAxisMargin;
        savedState.bottomAxisMargin = bottomAxisMargin;
        savedState.leftAxisMargin = leftAxisMargin;
        savedState.rightAxisMargin = rightAxisMargin;
        savedState.graphPaddingFactor = graphPaddingFactor;
        savedState.shouldDrawBox = shouldDrawBox;
        savedState.labelStyle = labelStyle;
        savedState.title = title;
        savedState.numberOfVerticalLabels = numberOfVerticalLabels;
        savedState.numberOfRightSideLabels = numberOfRightSideLabels;
        savedState.numberOfHorizontalLabels = numberOfHorizontalLabels;
        savedState.numberOfBottomLabels = numberOfBottomLabels;
        savedState.bottomText = bottomText;
        savedState.leftSideText = leftSideText;
        savedState.rightSideText = rightSideText;
        return savedState;
    }

    /**
     * Overridden method to restore custom attributes across states.
     *
     * Restores all the custom attributes that can be set from a custom
     * SavedState Class.
     *
     * @return void
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        GraphView.GraphViewSavedState savedState = (GraphView.GraphViewSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setNumberOfVerticalMarkers(savedState.numberOfVerticalMarkers);
        setNumberOfHorizontalMarkers(savedState.numberOfHorizontalMarkers);
        setTopAxisMargin(savedState.topAxisMargin);
        setBottomAxisMargin(savedState.bottomAxisMargin);
        setLeftAxisMargin(savedState.leftAxisMargin);
        setRightAxisMargin(savedState.rightAxisMargin);
        setGraphPaddingFactor(savedState.graphPaddingFactor);
        setShouldDrawBox(savedState.shouldDrawBox);
        setLabelStyle(savedState.labelStyle);
        setTitle(savedState.title);
        setBottomText(savedState.bottomText);
        setRightSideText(savedState.rightSideText);
        setLeftSideText(savedState.leftSideText);
        setNumberOfHorizontalLabels(savedState.numberOfHorizontalLabels);
        setNumberOfVerticalLabels(savedState.numberOfVerticalLabels);
        setNumberOfRightSideLabels(savedState.numberOfRightSideLabels);
        setNumberOfBottomLabels(savedState.numberOfBottomLabels);
    }

    /**
     * Inner class extends BaseSavedState to save and restore GraphView configurations.
     */
    static class GraphViewSavedState extends BaseSavedState {
        private static final String NUMBER_OF_VERTICAL_MARKERS = "# of vertical markers";
        private static final String NUMBER_OF_HORIZONTAL_MARKERS = "# of horizontal markers";
        private static final String NUMBER_OF_HORIZONTAL_LABELS = "# of horizontal labels";
        private static final String NUMBER_OF_VERTICAL_LABELS = "# of vertical labels";
        private static final String NUMBER_OF_RIGHT_SIDE_LABELS = "# of right side labels";
        private static final String NUMBER_OF_BOTTOM_LABELS = "# of bottom side labels";
        private static final String TOP_AXIS_MARGIN = "top axis margin";
        private static final String BOTTOM_AXIS_MARGIN = "bottom axis margin";
        private static final String LEFT_AXIS_MARGIN = "left axis margin";
        private static final String RIGHT_AXIS_MARGIN = "right axis margin";
        private static final String GRAPH_PADDING_FACTOR = "graph padding factor";
        private static final String SHOULD_DRAW_BOX = "should draw box";
        private static final String LABEL_STYLE = "label style";
        private static final String TITLE = "title";
        private static final String RIGHT_SIDE_TEXT = "right side text";
        private static final String LEFT_SIDE_TEXT = "left side text";
        private static final String BOTTOM_TEXT = "bottom text";
        Bundle bundle;
        int numberOfVerticalMarkers;
        int numberOfHorizontalMarkers;
        int numberOfVerticalLabels;
        int numberOfHorizontalLabels;
        int numberOfRightSideLabels;
        int numberOfBottomLabels;
        float topAxisMargin;
        float bottomAxisMargin;
        float leftAxisMargin;
        float rightAxisMargin;
        float graphPaddingFactor;
        boolean shouldDrawBox;
        int labelStyle;
        String title;
        String rightSideText;
        String leftSideText;
        String bottomText;

        public GraphViewSavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Reads from the Bundle Object to restore the View.
         *
         * @param in
         */
        private GraphViewSavedState(Parcel in) {
            super(in);
            bundle = in.readBundle(getClass().getClassLoader());
            assert bundle != null;
            numberOfVerticalMarkers = bundle.getInt(NUMBER_OF_VERTICAL_MARKERS, DEFAULT_NUMBER_VERT_MARKERS);
            numberOfHorizontalMarkers = bundle.getInt(NUMBER_OF_HORIZONTAL_MARKERS, DEFAULT_NUMBER_HORI_MARKERS);
            numberOfVerticalLabels = bundle.getInt(NUMBER_OF_VERTICAL_LABELS, DEFAULT_NUMBER_VERT_LABELS);
            numberOfRightSideLabels = bundle.getInt(NUMBER_OF_RIGHT_SIDE_LABELS, DEFAULT_NUMBER_RIGHT_SIDE_LABELS);
            numberOfHorizontalLabels = bundle.getInt(NUMBER_OF_HORIZONTAL_LABELS, DEFAULT_NUMBER_HORI_LABELS);
            numberOfBottomLabels = bundle.getInt(NUMBER_OF_BOTTOM_LABELS, DEFAULT_NUMBER_BOTTOM_LABELS);
            topAxisMargin = bundle.getFloat(TOP_AXIS_MARGIN, DEFAULT_TOP_MARGIN);
            bottomAxisMargin = bundle.getFloat(BOTTOM_AXIS_MARGIN, DEFAULT_BOTTOM_MARGIN);
            leftAxisMargin = bundle.getFloat(LEFT_AXIS_MARGIN, DEFAULT_LEFT_MARGIN);
            rightAxisMargin = bundle.getFloat(RIGHT_AXIS_MARGIN, DEFAULT_RIGHT_MARGIN);
            graphPaddingFactor = bundle.getFloat(GRAPH_PADDING_FACTOR, DEFAULT_GRAPH_PADDING_FACTOR);
            shouldDrawBox = bundle.getBoolean(SHOULD_DRAW_BOX);
            labelStyle = bundle.getInt(LABEL_STYLE);
            title = bundle.getString(TITLE);
            rightSideText = bundle.getString(RIGHT_SIDE_TEXT);
            leftSideText = bundle.getString(LEFT_SIDE_TEXT);
            bottomText = bundle.getString(BOTTOM_TEXT);
        }

        /**
         * Writes values to be saved to a Bundle and attaches it to a Parcel.
         *
         * @param out
         * @param flags
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            Bundle outBundle = new Bundle();
            outBundle.putInt(NUMBER_OF_HORIZONTAL_MARKERS, numberOfHorizontalMarkers);
            outBundle.putInt(NUMBER_OF_VERTICAL_MARKERS, numberOfVerticalMarkers);
            outBundle.putInt(NUMBER_OF_VERTICAL_LABELS, numberOfVerticalLabels);
            outBundle.putInt(NUMBER_OF_HORIZONTAL_LABELS, numberOfHorizontalLabels);
            outBundle.putInt(NUMBER_OF_RIGHT_SIDE_LABELS, numberOfRightSideLabels);
            outBundle.putInt(NUMBER_OF_BOTTOM_LABELS, numberOfBottomLabels);
            outBundle.putFloat(TOP_AXIS_MARGIN, topAxisMargin);
            outBundle.putFloat(BOTTOM_AXIS_MARGIN, bottomAxisMargin);
            outBundle.putFloat(RIGHT_AXIS_MARGIN, rightAxisMargin);
            outBundle.putFloat(LEFT_AXIS_MARGIN, leftAxisMargin);
            outBundle.putFloat(GRAPH_PADDING_FACTOR, graphPaddingFactor);
            outBundle.putBoolean(SHOULD_DRAW_BOX, shouldDrawBox);
            outBundle.putInt(LABEL_STYLE, labelStyle);
            outBundle.putString(TITLE, title);
            out.writeBundle(outBundle);
        }

        public static final Creator<GraphView.GraphViewSavedState> CREATOR
                = new Creator<GraphView.GraphViewSavedState>() {
            public GraphView.GraphViewSavedState createFromParcel(Parcel in) {
                return new GraphView.GraphViewSavedState(in);
            }

            public GraphView.GraphViewSavedState[] newArray(int size) {
                return new GraphView.GraphViewSavedState[size];
            }
        };
    }
}
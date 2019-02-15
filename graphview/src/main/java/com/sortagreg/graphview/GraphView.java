package com.sortagreg.graphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.sortagreg.graphview.GraphViewDataModel.*;

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

    public static final int DEFAULT_NUMBER_VERT_MARKERS = 10;
    public static final int DEFAULT_NUMBER_HORI_MARKERS = 15;
    public static final int DEFAULT_NUMBER_X_LABELS = 15;
    public static final int DEFAULT_NUMBER_Y_LABELS = 10;
    private int numberOfVerticalMarkers;
    private int numberOfHorizontalMarkers;

//    public static final int DEFAULT_ROUNDING_FACTOR = 1;
//    public static final int DEFAULT_STEP_FACTOR = 1;
    public static final int STANDARD_LABELS = 0;
    public static final int UNFOLDED_LABELS = 1;
    public static final int CUSTOM_LABELS = 2;
    private int labelStyle;
    private boolean leftSideLabels;
//    private int leftLabelRoundingFactor;
//    private int leftLabelStepFactor;
    private boolean xAxisLabels;
//    private int xLabelRoundingFactor;
//    private int xLabelStepFactor;
    private boolean rightSideLabels;
//    private int rightLabelRoundingFactor;
//    private int rightLabelStepFactor;


    private boolean shouldDrawBox;

    // Calculated values
    private List<GraphViewDataModel> dataSetList;
    private List<GraphViewDataModel> secondaryDataSetList;

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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        numberOfHorizontalMarkers = typedArray.getInteger(R.styleable.GraphView_numberOfHorizontalMarkers, DEFAULT_NUMBER_HORI_MARKERS);
        numberOfVerticalMarkers = typedArray.getInteger(R.styleable.GraphView_numberOfVerticalMarkers, DEFAULT_NUMBER_VERT_MARKERS);

        topAxisMargin = typedArray.getFloat(R.styleable.GraphView_axisMarginTop, DEFAULT_TOP_MARGIN);
        bottomAxisMargin = typedArray.getFloat(R.styleable.GraphView_axisMarginBottom, DEFAULT_BOTTOM_MARGIN);
        rightAxisMargin = typedArray.getFloat(R.styleable.GraphView_axisMarginRight, DEFAULT_RIGHT_MARGIN);
        leftAxisMargin = typedArray.getFloat(R.styleable.GraphView_axisMarginLeft, DEFAULT_LEFT_MARGIN);
        graphPaddingFactor = typedArray.getFloat(R.styleable.GraphView_graphPaddingFactor, DEFAULT_GRAPH_PADDING_FACTOR);
        shouldDrawBox = typedArray.getBoolean(R.styleable.GraphView_shouldDrawBox, true);

        title = typedArray.getString(R.styleable.GraphView_title) != null ? typedArray.getString(R.styleable.GraphView_title) : "";
        bottomText = typedArray.getString(R.styleable.GraphView_bottomText) != null ? typedArray.getString(R.styleable.GraphView_bottomText) : "";
        rightSideText = typedArray.getString(R.styleable.GraphView_rightSideText) != null ? typedArray.getString(R.styleable.GraphView_rightSideText) : "";
        leftSideText = typedArray.getString(R.styleable.GraphView_leftSideText) != null ? typedArray.getString(R.styleable.GraphView_leftSideText) : "";

        labelStyle = typedArray.getInteger(R.styleable.GraphView_labelStyle, STANDARD_LABELS);
//        leftLabelRoundingFactor = typedArray.getInteger(R.styleable.GraphView_leftLabelRoundingFactor, DEFAULT_ROUNDING_FACTOR);
//        leftLabelStepFactor = typedArray.getInteger(R.styleable.GraphView_leftLabelStepFactor, DEFAULT_STEP_FACTOR);
        leftSideLabels = typedArray.getBoolean(R.styleable.GraphView_leftSideLabels, true);
//        xLabelRoundingFactor = typedArray.getInteger(R.styleable.GraphView_xLabelRoundingFactor, DEFAULT_ROUNDING_FACTOR);
//        xLabelStepFactor = typedArray.getInteger(R.styleable.GraphView_xLabelStepFactor, DEFAULT_STEP_FACTOR);
        xAxisLabels = typedArray.getBoolean(R.styleable.GraphView_xAxisLabels, true);
//        rightLabelRoundingFactor = typedArray.getInteger(R.styleable.GraphView_rightLabelRoundingFactor, DEFAULT_ROUNDING_FACTOR);
//        rightLabelStepFactor = typedArray.getInteger(R.styleable.GraphView_rightLabelStepFactor, DEFAULT_STEP_FACTOR);
        rightSideLabels = typedArray.getBoolean(R.styleable.GraphView_rightAxisLabels, true);

        typedArray.recycle();

        // Init other values here
        setPaintLines();
        dataSetList = new ArrayList<>();
        secondaryDataSetList = new ArrayList<>();
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
        drawDataSet(canvas, dataSetList, true);
        drawDataSet(canvas, secondaryDataSetList, false);
        drawAxes(canvas);
//        drawRightSideLabels(canvas, adjustedDataSetMinY, rangeOfYValues);
        drawKeyLabels(canvas);
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
     * @param leftSideLabels
     */
    public void setLeftSideLabels(boolean leftSideLabels) {
        this.leftSideLabels = leftSideLabels;
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


    public void setxAxisLabels(boolean xAxisLabels) {
        this.xAxisLabels = xAxisLabels;
        invalidate();
    }

    public void setRightSideLabels(boolean rightSideLabels) {
        this.rightSideLabels = rightSideLabels;
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
    public void addToDataSetList(GraphViewDataModel dataSet) {
        this.dataSetList.add(dataSet);
        invalidate();
    }

    /**
     * Add a group of data sets to the graph to be drawn.
     *
     * @param dataSetList
     */
    public void addToDataSetListBulk(List<GraphViewDataModel> dataSetList) {
        this.dataSetList.addAll(dataSetList);
        invalidate();
    }

    public void addToSecondaryDataSetList(GraphViewDataModel dataSet) {
        this.secondaryDataSetList.add(dataSet);
        invalidate();
    }

    public void addToSecondaryDataSetListBulk(List<GraphViewDataModel> dataSetList) {
        this.secondaryDataSetList.addAll(dataSetList);
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

    private void drawDataSet(Canvas canvas, List<GraphViewDataModel> dataSetList, boolean isPrimary) {
        float dataSetMinX = Float.MAX_VALUE;
        float dataSetMaxX = Float.MIN_VALUE;
        float dataSetMinY = Float.MAX_VALUE;
        float dataSetMaxY = Float.MIN_VALUE;
        float adjustedDataSetMinX;
        float adjustedDataSetMaxX;
        float adjustedDataSetMinY;
        float adjustedDataSetMaxY;
        float rangeOfXValues;
        float rangeOfYValues;

        if (dataSetList.isEmpty()) return;

        // Calculate the values for the data set
        for (GraphViewDataModel dataSet : dataSetList) {
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
        adjustedDataSetMinX = dataSetMinX - Math.abs(dataSetMaxX * graphPaddingFactor);
        adjustedDataSetMinY = dataSetMinY - Math.abs(dataSetMaxY * graphPaddingFactor);
        adjustedDataSetMaxX = dataSetMaxX + Math.abs(dataSetMaxX * graphPaddingFactor);
        adjustedDataSetMaxY = dataSetMaxY + Math.abs(dataSetMaxY * graphPaddingFactor);

        rangeOfXValues = adjustedDataSetMaxX - adjustedDataSetMinX;
        rangeOfYValues = adjustedDataSetMaxY - adjustedDataSetMinY;

        // Draw the data sets
        for (GraphViewDataModel dataModel : dataSetList) {
            switch (dataModel.getGraphType()) {
                case STANDARD_LINE:
                    drawStandardLine(canvas, dataModel, adjustedDataSetMinX, adjustedDataSetMinY, rangeOfXValues, rangeOfYValues);
                    break;
                case UNFOLDED_LINE:
                    drawUnfoldedLine(canvas, dataModel, adjustedDataSetMinY, rangeOfYValues);
                    break;
                case CONSTANT_LINE:
                    drawConstantLine(canvas, dataModel, adjustedDataSetMinY, rangeOfYValues);
                    break;
                case STATE_LINE:
                    drawBinaryStateLine(canvas, dataModel);
                    break;
            }
        }

        // Draw the labels
        if (isPrimary) {
            switch (labelStyle) {
                case STANDARD_LABELS:
                    drawStandardTextLabels(canvas, adjustedDataSetMinX, adjustedDataSetMinY, rangeOfXValues, rangeOfYValues);
                    break;
                case UNFOLDED_LABELS:
                    drawUnfoldedTextLabels(canvas, adjustedDataSetMinX, adjustedDataSetMinY, rangeOfXValues, rangeOfYValues);
                    break;
                case CUSTOM_LABELS:
                    Log.w(TAG, "onDraw: Custom label is not implemented yet. Using standard by default");
                    // TODO add custom label ability
                    // break;
                default:
                    drawStandardTextLabels(canvas, adjustedDataSetMinX, adjustedDataSetMinY, rangeOfXValues, rangeOfYValues);
            }
        } else {
            drawRightSideLabels(canvas, adjustedDataSetMinY, rangeOfYValues);
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
    private void drawBinaryStateLine(Canvas canvas, GraphViewDataModel dataModel) {
        for (int i = 0; i < dataModel.getDataSet().length - 1; i ++) {
            float pixelsPerX = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (dataModel.getDataSet().length - 1);

            PointF startPoint = new PointF(leftAxisMargin + ((float) i * pixelsPerX), dataModel.getDataSet()[i].y == 0 ? ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .15f) + topAxisMargin) : ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .85f) + topAxisMargin));
            PointF endPoint = new PointF(leftAxisMargin + ((float) (i + 1) * pixelsPerX), dataModel.getDataSet()[i + 1].y == 0 ? ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .15f) + topAxisMargin) : ((((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .85f) + topAxisMargin));

            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
        }
    }

    /**
     * Draws a graph incrementally, taking each point in sequence, and displays (sequence value, actual Y)
     *  @param canvas
     * @param dataModel
     * @param adjustedDataSetMinY
     * @param rangeOfYValues
     */
    private void drawUnfoldedLine(Canvas canvas, GraphViewDataModel dataModel, float adjustedDataSetMinY, float rangeOfYValues) {
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
     *  @param canvas
     * @param dataModel
     * @param adjustedDataSetMinY
     * @param rangeOfYValues
     */
    private void drawConstantLine(Canvas canvas, GraphViewDataModel dataModel, float adjustedDataSetMinY, float rangeOfYValues) {
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
     *  @param canvas
     * @param dataModel
     * @param adjustedDataSetMinX
     * @param adjustedDataSetMinY
     * @param rangeOfXValues
     * @param rangeOfYValues
     */
    private void drawStandardLine(Canvas canvas, GraphViewDataModel dataModel, float adjustedDataSetMinX, float adjustedDataSetMinY, float rangeOfXValues, float rangeOfYValues) {
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
     * @param adjustedDataSetMinX
     * @param adjustedDataSetMinY
     * @param rangeOfXValues
     * @param rangeOfYValues
     */
    private void drawStandardTextLabels(Canvas canvas, float adjustedDataSetMinX, float adjustedDataSetMinY, float rangeOfXValues, float rangeOfYValues) {
        // TODO split method to drawX, drawY, drawTitle

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);

        // Y-Axis labels
        if (leftSideLabels) {
            int leftLabelRoundingFactor = calculateRoundingFactor(adjustedDataSetMinY, rangeOfYValues);
            int initialLabelValue = ((int) (adjustedDataSetMinY + leftLabelRoundingFactor) / leftLabelRoundingFactor) * leftLabelRoundingFactor;

            float pixelsPerLabel = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / (float) DEFAULT_NUMBER_Y_LABELS;
            float valuePerLabel = rangeOfYValues / (float) DEFAULT_NUMBER_Y_LABELS;
            float pixelsPerValue = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;
            float initialLabelOffset = Math.abs((float) initialLabelValue - adjustedDataSetMinY) * pixelsPerValue;
            for (int i = 0; i < DEFAULT_NUMBER_Y_LABELS; i++) {
                int labelValue = ((int) ((initialLabelValue + i * valuePerLabel) / leftLabelRoundingFactor) * leftLabelRoundingFactor);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, ((float) canvas.getHeight() - bottomAxisMargin - initialLabelOffset) - ((float) i * pixelsPerLabel), textPaint);
            }
        }

        // X-Axis labels
        if (xAxisLabels) {
            int xLabelRoundingFactor = calculateRoundingFactor(adjustedDataSetMinX, rangeOfXValues);

            int initialLabelValue = ((int) (adjustedDataSetMinX + xLabelRoundingFactor) / xLabelRoundingFactor) * xLabelRoundingFactor;

            float pixelsPerLabel = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (float) DEFAULT_NUMBER_X_LABELS;
            float valuePerLabel = rangeOfXValues / (float) DEFAULT_NUMBER_X_LABELS;
            float pixelsPerValue = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / rangeOfXValues;
            float initialLabelOffset = Math.abs((float) initialLabelValue - adjustedDataSetMinX) * pixelsPerValue;
            for (int i = 1; i < DEFAULT_NUMBER_X_LABELS; i++) {
                int labelValue = ((int) ((initialLabelValue + i * valuePerLabel) / xLabelRoundingFactor) * xLabelRoundingFactor);
                canvas.rotate(270, leftAxisMargin + ((float) i * pixelsPerLabel) + initialLabelOffset, (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin + ((float) i * pixelsPerLabel) + initialLabelOffset, (float) canvas.getHeight() - bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, leftAxisMargin + ((float) i * pixelsPerLabel) + initialLabelOffset, (float) canvas.getHeight() - bottomAxisMargin + 10f);
            }
        }
    }

    private int calculateRoundingFactor(float minValue, float rangeOfValues) {
        float maxValue = minValue + rangeOfValues;
        int lengthOfValues = String.valueOf((int) minValue).length() < String.valueOf((int) maxValue).length() ? String.valueOf((int) minValue).length() : String.valueOf((int) maxValue).length();
        return (int) Math.pow(10, (lengthOfValues + 1) / 2);
    }

    /**
     * Draws X labels based on the exact values in the first data set in the dataSetList.
     * The Y labels are drawn by min and max values.
     *
     * @param canvas
     * @param adjustedDataSetMinY
     * @param rangeOfYValues
     */
    private void drawUnfoldedTextLabels(Canvas canvas, float adjustedDataSetMinX, float adjustedDataSetMinY, float rangeOfXValues, float rangeOfYValues) {
        // TODO split method to drawX, drawY, drawTitle

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);

        if (leftSideLabels) {
            int leftLabelRoundingFactor = calculateRoundingFactor(adjustedDataSetMinY, rangeOfYValues);
            int initialLabelValue = ((int) (adjustedDataSetMinY + leftLabelRoundingFactor) / leftLabelRoundingFactor) * leftLabelRoundingFactor;

            float pixelsPerLabel = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / (float) DEFAULT_NUMBER_Y_LABELS;
            float valuePerLabel = rangeOfYValues / (float) DEFAULT_NUMBER_Y_LABELS;
            float pixelsPerValue = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;
            float initialLabelOffset = Math.abs((float) initialLabelValue - adjustedDataSetMinY) * pixelsPerValue;
            for (int i = 0; i < DEFAULT_NUMBER_Y_LABELS; i++) {
                int labelValue = (((int) ((initialLabelValue + i * valuePerLabel) / leftLabelRoundingFactor)) * leftLabelRoundingFactor);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, ((float) canvas.getHeight() - bottomAxisMargin - initialLabelOffset) - ((float) i * pixelsPerLabel), textPaint);
            }
        }

        // X-Axis labels
        if (xAxisLabels) {
            // TODO actually make the number of labels in an unfolded graph configurable again
            float pixelsPerLabel = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (float) DEFAULT_NUMBER_X_LABELS; //(float) numberOfHorizontalLabels;
            float valuePerStep = dataSetList.get(0).getDataSet().length / (float) DEFAULT_NUMBER_X_LABELS; //numberOfHorizontalLabels;
            for (int i = 1; i <= DEFAULT_NUMBER_X_LABELS; i++) {
                 int xLabelRoundingFactorPower = String.valueOf((int) dataSetList.get(0).getDataSet()[i * (int) valuePerStep - 1].x).length() / 2;
                int xLabelRoundingFactor = (int) Math.pow(10, xLabelRoundingFactorPower);
                int labelValue = (((int) (dataSetList.get(0).getDataSet()[i * (int) valuePerStep - 1].x / xLabelRoundingFactor)) * xLabelRoundingFactor);
                canvas.rotate(270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
            }
        }
    }

    private void drawRightSideLabels(Canvas canvas, float adjustedDataSetMinY, float rangeOfYValues) {
        if (!rightSideLabels) return;
        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);



        int leftLabelRoundingFactor = calculateRoundingFactor(adjustedDataSetMinY, rangeOfYValues);
        int initialLabelValue = (((int) (adjustedDataSetMinY + leftLabelRoundingFactor) / leftLabelRoundingFactor) * leftLabelRoundingFactor);

        float pixelsPerLabel = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / (float) DEFAULT_NUMBER_Y_LABELS;
        float valuePerLabel = rangeOfYValues / (float) DEFAULT_NUMBER_Y_LABELS;
        float pixelsPerValue = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;
        float initialLabelOffset = Math.abs((float) initialLabelValue - adjustedDataSetMinY) * pixelsPerValue;
        for (int i = 0; i < DEFAULT_NUMBER_Y_LABELS; i++) {
            int labelValue = ((int) ((initialLabelValue + i * valuePerLabel) / leftLabelRoundingFactor) * leftLabelRoundingFactor);
                    canvas.drawText(String.valueOf(labelValue), canvas.getWidth() - rightAxisMargin + 10f, ((float) canvas.getHeight() - bottomAxisMargin - initialLabelOffset) - ((float) i * pixelsPerLabel), textPaint);

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
        savedState.bottomText = bottomText;
        savedState.leftSideText = leftSideText;
        savedState.rightSideText = rightSideText;
        savedState.leftSideLabels = leftSideLabels;
//        savedState.leftLabelRoundingFactor = leftLabelRoundingFactor;
//        savedState.leftLabelStepFactor = leftLabelStepFactor;
        savedState.xSideLabels = xAxisLabels;
//        savedState.xLabelRoundingFactor = xLabelRoundingFactor;
//        savedState.xLabelStepFactor = xLabelStepFactor;
        savedState.rightSideLabels = rightSideLabels;
//        savedState.rightLabelRoundingFactor = rightLabelRoundingFactor;
//        savedState.rightLabelStepFactor = rightLabelStepFactor;
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
        setLeftSideLabels(savedState.leftSideLabels);
//        setLeftLabelRoundingFactor(savedState.leftLabelRoundingFactor);
//        setLeftLabelStepFactor(savedState.leftLabelStepFactor);
        setRightSideLabels(savedState.rightSideLabels);
//        setRightLabelRoundingFactor(savedState.rightLabelRoundingFactor);
//        setRightLabelStepFactor(savedState.rightLabelStepFactor);
        setxAxisLabels(savedState.xSideLabels);
//        setxLabelRoundingFactor(savedState.xLabelRoundingFactor);
//        setxLabelStepFactor(savedState.xLabelStepFactor);
    }

    /**
     * Inner class extends BaseSavedState to save and restore GraphView configurations.
     */
    static class GraphViewSavedState extends BaseSavedState {
        private static final String NUMBER_OF_VERTICAL_MARKERS = "# of vertical markers";
        private static final String NUMBER_OF_HORIZONTAL_MARKERS = "# of horizontal markers";
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
        private static final String LEFT_SIDE_LABELS = "left side labels";
//        private static final String LEFT_LABEL_ROUNDING_FACTOR = "left label rounding factor";
//        private static final String LEFT_LABEL_STEP_FACTOR = "left label step factor";
        private static final String X_AXIS_LABELS = "x side labels";
//        private static final String X_LABEL_ROUNDING_FACTOR = "x label rounding factor";
//        private static final String X_LABEL_STEP_FACTOR = "x label step factor";
        private static final String RIGHT_AXIS_LABELS = "right side labels";
//        private static final String RIGHT_LABEL_ROUNDING_FACTOR = "right label rounding factor";
//        private static final String RIGHT_LABEL_STEP_FACTOR = "right label step factor";
        Bundle bundle;
        int numberOfVerticalMarkers;
        int numberOfHorizontalMarkers;
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
        boolean leftSideLabels;
//        int leftLabelRoundingFactor;
//        int leftLabelStepFactor;
        boolean xSideLabels;
//        int xLabelRoundingFactor;
//        int xLabelStepFactor;
        boolean rightSideLabels;
//        int rightLabelRoundingFactor;
//        int rightLabelStepFactor;

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
//            leftLabelRoundingFactor = bundle.getInt(LEFT_LABEL_ROUNDING_FACTOR);
//            leftLabelStepFactor = bundle.getInt(LEFT_LABEL_STEP_FACTOR);
            leftSideLabels = bundle.getBoolean(LEFT_SIDE_LABELS);
//            xLabelRoundingFactor = bundle.getInt(X_LABEL_ROUNDING_FACTOR);
//            xLabelStepFactor = bundle.getInt(X_LABEL_STEP_FACTOR);
            xSideLabels = bundle.getBoolean(X_AXIS_LABELS);
//            rightLabelRoundingFactor = bundle.getInt(RIGHT_LABEL_ROUNDING_FACTOR);
//            rightLabelStepFactor = bundle.getInt(RIGHT_LABEL_STEP_FACTOR);
            rightSideLabels = bundle.getBoolean(RIGHT_AXIS_LABELS);
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
            outBundle.putFloat(TOP_AXIS_MARGIN, topAxisMargin);
            outBundle.putFloat(BOTTOM_AXIS_MARGIN, bottomAxisMargin);
            outBundle.putFloat(RIGHT_AXIS_MARGIN, rightAxisMargin);
            outBundle.putFloat(LEFT_AXIS_MARGIN, leftAxisMargin);
            outBundle.putFloat(GRAPH_PADDING_FACTOR, graphPaddingFactor);
            outBundle.putBoolean(SHOULD_DRAW_BOX, shouldDrawBox);
            outBundle.putInt(LABEL_STYLE, labelStyle);
            outBundle.putString(TITLE, title);
//            outBundle.putInt(LEFT_LABEL_ROUNDING_FACTOR, leftLabelRoundingFactor);
//            outBundle.putInt(LEFT_LABEL_STEP_FACTOR, leftLabelStepFactor);
            outBundle.putBoolean(LEFT_SIDE_LABELS, leftSideLabels);
//            outBundle.putInt(X_LABEL_ROUNDING_FACTOR, xLabelRoundingFactor);
//            outBundle.putInt(X_LABEL_STEP_FACTOR, xLabelStepFactor);
            outBundle.putBoolean(X_AXIS_LABELS, xSideLabels);
//            outBundle.putInt(RIGHT_LABEL_ROUNDING_FACTOR, rightLabelRoundingFactor);
//            outBundle.putInt(RIGHT_LABEL_STEP_FACTOR, rightLabelStepFactor);
            outBundle.putBoolean(RIGHT_AXIS_LABELS, rightSideLabels);
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
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

    public static final int STANDARD_LABELS = 0;
    public static final int UNFOLDED_LABELS = 1;
    public static final int CUSTOM_LABELS = 2;
    private int labelStyle;
    private boolean leftSideLabels;
    private boolean xAxisLabels;
    private boolean rightSideLabels;

    private boolean shouldDrawBox;

    private static final int DEFAULT_NUMBER_X_LABELS = 15;
    private static final int DEFAULT_NUMBER_Y_LABELS = 10;

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
        leftSideLabels = typedArray.getBoolean(R.styleable.GraphView_leftSideLabels, true);
        xAxisLabels = typedArray.getBoolean(R.styleable.GraphView_xAxisLabels, true);
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
        drawDataSet(canvas, dataSetList, true);
        drawDataSet(canvas, secondaryDataSetList, false);
        drawAxes(canvas);
        drawKeyLabels(canvas);
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

            PointF startPoint;
            if (dataModel.getDataSet()[i].y == 0) {
                startPoint = new PointF(leftAxisMargin + (float) i * pixelsPerX, (((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .15f) + topAxisMargin);
            } else {
                startPoint = new PointF(leftAxisMargin + (float) i * pixelsPerX, (((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .85f) + topAxisMargin);
            }

            PointF endPoint;
            if (dataModel.getDataSet()[i + 1].y == 0) {
                endPoint = new PointF(leftAxisMargin + (float) (i + 1) * pixelsPerX, (((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .15f) + topAxisMargin);
            } else {
                endPoint = new PointF(leftAxisMargin + (float) (i + 1) * pixelsPerX, (((float) canvas.getHeight() - bottomAxisMargin - topAxisMargin) * .85f) + topAxisMargin);
            }

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
            ArrayList<Integer> labelValues = generateLabelValues((int) adjustedDataSetMinY, (int) rangeOfYValues, DEFAULT_NUMBER_Y_LABELS);
            float pixelsPerValue = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;
            float initialLabelOffset = adjustedDataSetMinY * pixelsPerValue;
            for (int labelValue : labelValues) {
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue), textPaint);
                canvas.drawLine((int) leftAxisMargin, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue),
                         canvas.getWidth() - rightAxisMargin, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue), markerPaint);
            }
        }

        // X-Axis labels
        if (xAxisLabels) {
            ArrayList<Integer> labelValues = generateLabelValues((int) adjustedDataSetMinX, (int) rangeOfXValues, DEFAULT_NUMBER_X_LABELS);
            float pixelsPerValue = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / rangeOfXValues;
            float initialLabelOffset = adjustedDataSetMinX * pixelsPerValue;

            for (int labelValue : labelValues) {
                canvas.rotate(270, leftAxisMargin + ((float) labelValue * pixelsPerValue) - initialLabelOffset, (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin + ((float) labelValue * pixelsPerValue) - initialLabelOffset, (float) canvas.getHeight() - bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, leftAxisMargin + ((float) labelValue * pixelsPerValue) - initialLabelOffset, (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawLine(leftAxisMargin + ((float) labelValue * pixelsPerValue) - initialLabelOffset, canvas.getHeight() - (int) bottomAxisMargin,
                        leftAxisMargin + ((float) labelValue * pixelsPerValue) - initialLabelOffset, (int) topAxisMargin,
                        markerPaint);
            }
        }
    }

    private ArrayList<Integer> generateLabelValues (int minValue, int rangeOfValues, int numberOfLabels) {
        ArrayList<Integer> labels = new ArrayList<>();
        int valuePerLabel = rangeOfValues / (numberOfLabels + 1);
        int roundingFactor = (int) Math.pow(10, String.valueOf(valuePerLabel).length() / 2);
        valuePerLabel = (valuePerLabel / roundingFactor) * roundingFactor;
        int initialLabelValue = ((minValue / roundingFactor) * roundingFactor) + (roundingFactor);
        if (initialLabelValue > minValue - (valuePerLabel / 2) && initialLabelValue < minValue + (valuePerLabel / 2) ) {
            initialLabelValue = ((initialLabelValue + valuePerLabel) / roundingFactor) * roundingFactor;
        }
        int labelValue = initialLabelValue;
        do {
            labels.add(labelValue);
            labelValue = labelValue + valuePerLabel;
        } while (labelValue < minValue + rangeOfValues);

        return labels;
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

        // Y-Axis labels
        if (leftSideLabels) {
            ArrayList<Integer> labelValues = generateLabelValues((int) adjustedDataSetMinY, (int) rangeOfYValues, DEFAULT_NUMBER_Y_LABELS);
            float pixelsPerValue = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;
            float initialLabelOffset = adjustedDataSetMinY * pixelsPerValue;
            for (int labelValue : labelValues) {
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue), textPaint);
                canvas.drawLine((int) leftAxisMargin, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue),
                        canvas.getWidth() - rightAxisMargin, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue), markerPaint);
            }
        }

        // X-Axis labels
        if (xAxisLabels) {
            // TODO actually make the number of labels in an unfolded graph configurable again
            float pixelsPerLabel = ((float) canvas.getWidth() - leftAxisMargin - rightAxisMargin) / (float) DEFAULT_NUMBER_X_LABELS; //(float) numberOfHorizontalLabels;
            float valuePerStep = dataSetList.get(0).getDataSet().length / (float) DEFAULT_NUMBER_X_LABELS; //numberOfHorizontalLabels;
            for (int i = 1; i <= DEFAULT_NUMBER_X_LABELS; i++) {
                 int xLabelRoundingFactorPower = String.valueOf((int) valuePerStep).length() / 2;
                int xLabelRoundingFactor = (int) Math.pow(10, xLabelRoundingFactorPower);
                int labelValue = (((int) (dataSetList.get(0).getDataSet()[i * (int) valuePerStep - 1].x / xLabelRoundingFactor)) * xLabelRoundingFactor);
                canvas.rotate(270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, leftAxisMargin - 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - bottomAxisMargin + 10f);
                canvas.drawLine(leftAxisMargin - 10f + (i * pixelsPerLabel), canvas.getHeight() - (int) bottomAxisMargin,
                        leftAxisMargin - 10f + (i * pixelsPerLabel), (int) topAxisMargin,
                        markerPaint);
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

        ArrayList<Integer> labelValues = generateLabelValues((int) adjustedDataSetMinY, (int) rangeOfYValues, DEFAULT_NUMBER_Y_LABELS);
        float pixelsPerValue = ((float) canvas.getHeight() - topAxisMargin - bottomAxisMargin) / rangeOfYValues;
        float initialLabelOffset = adjustedDataSetMinY * pixelsPerValue;
        for (int labelValue : labelValues) {
            canvas.drawText(String.valueOf(labelValue), canvas.getWidth() - rightAxisMargin + 10f, ((float) canvas.getHeight() - bottomAxisMargin + initialLabelOffset) - ((float) labelValue * pixelsPerValue), textPaint);
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
        savedState.xSideLabels = xAxisLabels;
        savedState.rightSideLabels = rightSideLabels;
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
        setRightSideLabels(savedState.rightSideLabels);
        setxAxisLabels(savedState.xSideLabels);
    }

    /**
     * Inner class extends BaseSavedState to save and restore GraphView configurations.
     */
    static class GraphViewSavedState extends BaseSavedState {
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
        private static final String X_AXIS_LABELS = "x side labels";
        private static final String RIGHT_AXIS_LABELS = "right side labels";
        Bundle bundle;
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
        boolean xSideLabels;
        boolean rightSideLabels;

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
            leftSideLabels = bundle.getBoolean(LEFT_SIDE_LABELS);
            xSideLabels = bundle.getBoolean(X_AXIS_LABELS);
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
            outBundle.putFloat(TOP_AXIS_MARGIN, topAxisMargin);
            outBundle.putFloat(BOTTOM_AXIS_MARGIN, bottomAxisMargin);
            outBundle.putFloat(RIGHT_AXIS_MARGIN, rightAxisMargin);
            outBundle.putFloat(LEFT_AXIS_MARGIN, leftAxisMargin);
            outBundle.putFloat(GRAPH_PADDING_FACTOR, graphPaddingFactor);
            outBundle.putBoolean(SHOULD_DRAW_BOX, shouldDrawBox);
            outBundle.putInt(LABEL_STYLE, labelStyle);
            outBundle.putString(TITLE, title);
            outBundle.putBoolean(LEFT_SIDE_LABELS, leftSideLabels);
            outBundle.putBoolean(X_AXIS_LABELS, xSideLabels);
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
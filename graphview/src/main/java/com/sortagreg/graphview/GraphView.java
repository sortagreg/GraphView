package com.sortagreg.graphview;

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


import java.util.ArrayList;
import java.util.List;

import static com.sortagreg.graphview.GraphViewDataModel.CONSTANT_LINE;
import static com.sortagreg.graphview.GraphViewDataModel.STANDARD_LINE;
import static com.sortagreg.graphview.GraphViewDataModel.STATE_LINE;
import static com.sortagreg.graphview.GraphViewDataModel.UNFOLDED_LINE;


/**
 * GraphView - Custom Graph View Class
 *
 * @author Marshall Ladd
 */
public class GraphView extends View {
    private Context context;
    private static final String TAG = "GraphView";

    private String title = "";

    private Paint backgroundPaint = new Paint();
    private Paint axisPaint = new Paint();
    private Paint markerPaint = new Paint();
    private Paint dataSetPaint = new Paint();

    public static final int DEFAULT_TOP_MARGIN = 75;
    public static final int DEFAULT_BOTTOM_MARGIN = 175;
    public static final int DEFAULT_LEFT_MARGIN = 175;
    public static final int DEFAULT_RIGHT_MARGIN = 75;
    private int topAxisMargin;
    private int bottomAxisMargin;
    private int leftAxisMargin;
    private int rightAxisMargin;

    public static final int DEFAULT_NUMBER_VERT_MARKERS = 15;
    public static final int DEFAULT_NUMBER_HORI_MARKERS = 15;
    private int numberOfVerticalMarkers;
    private int numberOfHorizontalMarkers;

    public static final int DEFAULT_NUMBER_VERT_LABELS = 15;
    public static final int DEFAULT_NUMBER_HORI_LABELS = 15;
    public static final int STANDARD_LABELS = 0;
    public static final int UNFOLDED_LABELS = 1;
    public static final int CUSTOM_LABELS = 2;
    private int numberOfVerticalLabels;
    private int numberOfHorizontalLabels;
    private int labelStyle;

    private boolean shouldDrawBox;

    private List<GraphViewDataModel> dataSetList;
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        numberOfHorizontalMarkers = typedArray.getInteger(R.styleable.GraphView_numberOfHorizontalMarkers, DEFAULT_NUMBER_HORI_MARKERS);
        numberOfVerticalMarkers = typedArray.getInteger(R.styleable.GraphView_numberOfVerticalMarkers, DEFAULT_NUMBER_VERT_MARKERS);
        numberOfHorizontalLabels = typedArray.getInteger(R.styleable.GraphView_numberOfHorizontalLabels, DEFAULT_NUMBER_HORI_LABELS);
        numberOfVerticalLabels = typedArray.getInteger(R.styleable.GraphView_numberOfVerticalLabels, DEFAULT_NUMBER_VERT_LABELS);
        topAxisMargin = typedArray.getInteger(R.styleable.GraphView_axisMarginTop, DEFAULT_TOP_MARGIN);
        bottomAxisMargin = typedArray.getInteger(R.styleable.GraphView_axisMarginBottom, DEFAULT_BOTTOM_MARGIN);
        rightAxisMargin = typedArray.getInteger(R.styleable.GraphView_axisMarginRight, DEFAULT_RIGHT_MARGIN);
        leftAxisMargin = typedArray.getInteger(R.styleable.GraphView_axisMarginLeft, DEFAULT_LEFT_MARGIN);
        shouldDrawBox = typedArray.getBoolean(R.styleable.GraphView_shouldDrawBox, false);
        labelStyle = typedArray.getInteger(R.styleable.GraphView_labelStyle, STANDARD_LABELS);
        title = typedArray.getString(R.styleable.GraphView_title) != null ? typedArray.getString(R.styleable.GraphView_title) : "";
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
        drawAxes(canvas);
        drawDataSets(canvas);
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
            default: drawStandardTextLabels(canvas);
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

    public void setNumberOfVerticalLabels(int numberOfVerticalLabels) {
        this.numberOfVerticalLabels = numberOfVerticalLabels;
        invalidate();
    }

    public void setNumberOfHorizontalLabels(int numberOfHorizontalLabels) {
        this.numberOfHorizontalLabels = numberOfHorizontalLabels;
        invalidate();
    }

    public void setLabelStyle(int labelStyle) {
        this.labelStyle = labelStyle;
        invalidate();
    }

    public void setShouldDrawBox(boolean shouldDrawBox) {
        this.shouldDrawBox = shouldDrawBox;
        invalidate();
    }

    public void setTopAxisMargin(int topAxisMargin) {
        this.topAxisMargin = topAxisMargin;
        invalidate();
    }

    public void setBottomAxisMargin(int bottomAxisMargin) {
        this.bottomAxisMargin = bottomAxisMargin;
        invalidate();
    }

    public void setLeftAxisMargin(int leftAxisMargin) {
        this.leftAxisMargin = leftAxisMargin;
        invalidate();
    }

    public void setRightAxisMargin(int rightAxisMargin) {
        this.rightAxisMargin = rightAxisMargin;
        invalidate();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    public void addToDataSetList(GraphViewDataModel dataSet) {
        this.dataSetList.add(dataSet);
        invalidate();
    }

    public void addToDataSetListBulk(List<GraphViewDataModel> dataSetList) {
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
        int graphWidth = canvas.getWidth() - leftAxisMargin - rightAxisMargin;
        // calculate distance between markers
        int markerSpacing = graphWidth / (numberOfVerticalMarkers + 1);
        // print vertical markers
        for (int i = 1; i <= numberOfVerticalMarkers; i++) {
            int startX = leftAxisMargin + (i * markerSpacing);
            int startY = topAxisMargin;// + 50; // add 50 to give some distance between axis and markers
            int endX = startX;
            int endY = canvas.getHeight() - bottomAxisMargin;// - 50; // sub 50 for same reason add 100 earlier
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
        int graphHeight = canvas.getHeight() - topAxisMargin - bottomAxisMargin;
        int markerSpacing = graphHeight / (numberOfHorizontalMarkers + 1);
        for (int i = numberOfHorizontalMarkers; i > 0; i--) {
            int startY = topAxisMargin + (i * markerSpacing);
            int startX = leftAxisMargin;// + 50;
            int endY = startY;
            int endX = canvas.getWidth() - rightAxisMargin;
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
        getMinMaxOfDataSets();
        rangeOfXValues = adjustedDataSetMaxX - adjustedDataSetMinX;
        rangeOfYValues = adjustedDataSetMaxY - adjustedDataSetMinY;
        for (GraphViewDataModel dataModel : dataSetList) {
            switch (dataModel.getGraphType()) {
                case STANDARD_LINE:
                    for (int i = 0; i < dataModel.getDataSet().length - 1; i++) {
                        float pixelsPerX = ((float) canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (rangeOfXValues);
                        float pixelsPerY = ((float) canvas.getHeight() - (float) topAxisMargin - (float) bottomAxisMargin) / (rangeOfYValues);
                        PointF startPoint = convertXYtoPx(dataModel.getDataSet()[i], canvas, pixelsPerX, pixelsPerY);
                        PointF endPoint = convertXYtoPx(dataModel.getDataSet()[i + 1], canvas, pixelsPerX, pixelsPerY);
                        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
                    }
                    break;
                case UNFOLDED_LINE:
                    for (int i = 0; i < dataModel.getDataSet().length - 1; i++) {
                        float pixelsPerX = ((float) canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (dataModel.getDataSet().length);
                        float pixelsPerY = ((float) canvas.getHeight() - (float) topAxisMargin - (float) bottomAxisMargin) / (rangeOfYValues);
                        PointF startPoint = dataModel.getDataSet()[i];
                        PointF endPoint = dataModel.getDataSet()[i + 1];
                        canvas.drawLine((float) leftAxisMargin + ((float) i * pixelsPerX), (float) canvas.getHeight() - (float) bottomAxisMargin + (adjustedDataSetMinY * pixelsPerY) - ((adjustedDataSetMaxY - dataSetMaxY) * pixelsPerY / 2) - ((float) startPoint.y * pixelsPerY), (float) leftAxisMargin + (((float) i + 1f)  * pixelsPerX), (float) canvas.getHeight() - (float) bottomAxisMargin - ((float) endPoint.y * pixelsPerY) + (adjustedDataSetMinY * pixelsPerY) - ((adjustedDataSetMaxY - dataSetMaxY) * pixelsPerY / 2), dataModel.getPaint());
                    }
                    break;
                case CONSTANT_LINE:
                    if (true) {
                        float pixelsPerX = ((float) canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (rangeOfXValues);
                        float pixelsPerY = ((float) canvas.getHeight() - (float) topAxisMargin - (float) bottomAxisMargin) / (rangeOfYValues);
                        PointF startPoint = convertXYtoPx(new PointF((float) dataSetMinX, dataModel.getDataSet()[0].y), canvas, pixelsPerX, pixelsPerY);
                        PointF endPoint = convertXYtoPx(new PointF((float) dataSetMaxX, dataModel.getDataSet()[0].y), canvas, pixelsPerX, pixelsPerY);
                        canvas.drawLine((float) leftAxisMargin, startPoint.y, (float) canvas.getWidth() - (float) rightAxisMargin, endPoint.y, dataModel.getPaint());
                    }
                    break;
                case STATE_LINE: // TODO implement state lines
                    for (int i = 0; i < dataModel.getDataSet().length - 2; i ++) {
                        float pixelsPerX = ((float) canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (dataModel.getDataSet().length);
                        PointF startPoint = new PointF((float) leftAxisMargin + ((float) i * pixelsPerX), dataModel.getDataSet()[i].y == 0 ? ((((float) canvas.getHeight() - (float) bottomAxisMargin - (float) topAxisMargin) * .15f) + topAxisMargin) : ((((float) canvas.getHeight() - (float) bottomAxisMargin - (float) topAxisMargin) * .6f) + topAxisMargin));
                        PointF endPoint = new PointF((float) leftAxisMargin + ((float) (i + 1) * pixelsPerX), dataModel.getDataSet()[i + 1].y == 0 ? ((((float) canvas.getHeight() - (float) bottomAxisMargin - (float) topAxisMargin) * .85f) + topAxisMargin) : ((((float) canvas.getHeight() - (float) bottomAxisMargin - (float) topAxisMargin) * .6f) + topAxisMargin));
                        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
                    }
                    break;
            }


        }
    }

    private void drawStandardTextLabels(Canvas canvas) {
        // TODO split method to drawX, drawY, drawTitle

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);
        // Y-Axis labels
        if (numberOfVerticalLabels > 0) {
            float pixelsPerLabel = (canvas.getHeight() - (float) topAxisMargin - (float) bottomAxisMargin) / (float) numberOfVerticalLabels;
            float valuePerStep = rangeOfYValues / numberOfVerticalLabels;
            for (int i = 0; i < numberOfVerticalLabels; i++) {
                int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinY);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, (canvas.getHeight() - (float) bottomAxisMargin) - ((float) i * pixelsPerLabel), textPaint);
            }
        }
        // X-Axis labels
        if (numberOfHorizontalLabels > 0) {
            float pixelsPerLabel = (canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (float) numberOfHorizontalLabels;
            float valuePerStep = rangeOfXValues / numberOfHorizontalLabels;
            for (int i = 0; i < numberOfHorizontalLabels; i++) {
                int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinX);
                canvas.rotate(270, (float) leftAxisMargin + 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - (float) bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), (float) leftAxisMargin + 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - (float) bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, (float) leftAxisMargin + 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - (float) bottomAxisMargin + 10f);
            }
        }
        // Title label
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(title, canvas.getWidth() / 2f, 50f, textPaint);
    }

    private void drawUnfoldedTextLabels(Canvas canvas) {
        // TODO split method to drawX, drawY, drawTitle

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000); // TODO paint color should be configurable
        textPaint.setTextSize(30f); // TODO text size should be configurable
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);
        // Y-Axis labels
        if (numberOfVerticalLabels > 0) {
            float pixelsPerLabel = (canvas.getHeight() - (float) topAxisMargin - (float) bottomAxisMargin) / (float) numberOfVerticalLabels;
            float valuePerStep = rangeOfYValues / numberOfVerticalLabels;
            for (int i = 0; i < numberOfVerticalLabels; i++) {
                int labelValue = (int) Math.floor((valuePerStep * i) + adjustedDataSetMinY);
                canvas.drawText(String.valueOf(labelValue), leftAxisMargin - 10f, (canvas.getHeight() - (float) bottomAxisMargin) - ((float) i * pixelsPerLabel), textPaint);
            }
        }
        // X-Axis labels
        if (numberOfHorizontalLabels > 0) {
            float pixelsPerLabel = (canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (float) numberOfHorizontalLabels;
            float valuePerStep = dataSetList.get(0).getDataSet().length / numberOfHorizontalLabels;
            for (int i = 0; i < numberOfHorizontalLabels; i++) {
                int labelValue = (int) dataSetList.get(0).getDataSet()[i * (int) valuePerStep].x;
                canvas.rotate(270, (float) leftAxisMargin + 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - (float) bottomAxisMargin + 10f);
                canvas.drawText(String.valueOf(labelValue), (float) leftAxisMargin + 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - (float) bottomAxisMargin + 10f, textPaint);
                canvas.rotate(-270, (float) leftAxisMargin + 10f + (i * pixelsPerLabel), (float) canvas.getHeight() - (float) bottomAxisMargin + 10f);
            }
        }
        // Title label
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(title, canvas.getWidth() / 2f, 50f, textPaint);
    }

    /**
     * Method converts an (X,Y) pair to its appropriate pixel location values.
     *
     * @param rawDataPoint PointF data point to be converted.
     * @param canvas Canvas Object to be drawn to.
     * @param pixelsPerX float Calculated pixel per X value from all data sets.
     * @param pixelsPerY float Calculated pixel per Y value from all data sets.
     * @return PointF with the literal pixel coordinates of the inputs (X,Y) values.
     */
    public PointF convertXYtoPx(PointF rawDataPoint, Canvas canvas, float pixelsPerX, float pixelsPerY) {
        float newX = (float) leftAxisMargin + ((float) rawDataPoint.x * pixelsPerX) - (adjustedDataSetMinX * pixelsPerX) + ((adjustedDataSetMaxX - dataSetMaxX) * pixelsPerX / 2);
        float newY = (float) canvas.getHeight() - (float) bottomAxisMargin - ((float) rawDataPoint.y * pixelsPerY) + (adjustedDataSetMinY * pixelsPerY) - ((adjustedDataSetMaxY - dataSetMaxY) * pixelsPerY / 2);
        return new PointF(newX, newY);
    }

    /**
     * Find and set the largest and smallest values to be found in all the data sets.
     */
    private void getMinMaxOfDataSets() {
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
        // TODO extract constant to a variable to adjust graph padding programmatically and in XML
        adjustedDataSetMinX = dataSetMinX - (dataSetMinX * .1f);
        adjustedDataSetMinY = dataSetMinY - (dataSetMinY * .1f);
        adjustedDataSetMaxX = dataSetMaxX + (dataSetMaxX * .1f);
        adjustedDataSetMaxY = dataSetMaxY + (dataSetMaxY * .1f);
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
        GraphViewSavedState savedState = new GraphViewSavedState(superState);
        savedState.numberOfVerticalMarkers = numberOfVerticalMarkers;
        savedState.numberOfHorizontalMarkers = numberOfHorizontalMarkers;
        savedState.topAxisMargin = topAxisMargin;
        savedState.bottomAxisMargin = bottomAxisMargin;
        savedState.leftAxisMargin = leftAxisMargin;
        savedState.rightAxisMargin = rightAxisMargin;
        savedState.shouldDrawBox = shouldDrawBox;
        savedState.labelStyle = labelStyle;
        savedState.title = title;
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
        GraphViewSavedState savedState = (GraphViewSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setNumberOfVerticalMarkers(savedState.numberOfVerticalMarkers);
        setNumberOfHorizontalMarkers(savedState.numberOfHorizontalMarkers);
        setTopAxisMargin(savedState.topAxisMargin);
        setBottomAxisMargin(savedState.bottomAxisMargin);
        setLeftAxisMargin(savedState.leftAxisMargin);
        setRightAxisMargin(savedState.rightAxisMargin);
        setShouldDrawBox(savedState.shouldDrawBox);
        setLabelStyle(savedState.labelStyle);
        setTitle(savedState.title);
    }

    /**
     * Inner class extends BaseSavedState to save and restore GraphView configurations.
     */
    static class GraphViewSavedState extends BaseSavedState {
        private static final String NUMBER_OF_VERTICAL_MARKERS = "# of vertical markers";
        private static final String NUMBER_OF_HORIZONTAL_MARKERS = "# of horizontal markers";
        private static final String NUMBER_OF_HORIZONTAL_LABELS = "# of horizontal labels";
        private static final String NUMBER_OF_VERTICAL_LABELS = "# of vertical labels";
        private static final String TOP_AXIS_MARGIN = "top axis margin";
        private static final String BOTTOM_AXIS_MARGIN = "bottom axis margin";
        private static final String LEFT_AXIS_MARGIN = "left axis margin";
        private static final String RIGHT_AXIS_MARGIN = "right axis margin";
        private static final String SHOULD_DRAW_BOX = "should draw box";
        private static final String LABEL_STYLE = "label style";
        private static final String TITLE = "title";
        Bundle bundle;
        int numberOfVerticalMarkers;
        int numberOfHorizontalMarkers;
        int numberOfVerticalLabels;
        int numberOfHorizontalLabels;
        int topAxisMargin;
        int bottomAxisMargin;
        int leftAxisMargin;
        int rightAxisMargin;
        boolean shouldDrawBox;
        int labelStyle;
        String title;

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
            numberOfHorizontalLabels = bundle.getInt(NUMBER_OF_HORIZONTAL_LABELS, DEFAULT_NUMBER_HORI_LABELS);
            topAxisMargin = bundle.getInt(TOP_AXIS_MARGIN, DEFAULT_TOP_MARGIN);
            bottomAxisMargin = bundle.getInt(BOTTOM_AXIS_MARGIN, DEFAULT_BOTTOM_MARGIN);
            leftAxisMargin = bundle.getInt(LEFT_AXIS_MARGIN, DEFAULT_LEFT_MARGIN);
            rightAxisMargin = bundle.getInt(RIGHT_AXIS_MARGIN, DEFAULT_RIGHT_MARGIN);
            shouldDrawBox = bundle.getBoolean(SHOULD_DRAW_BOX);
            labelStyle = bundle.getInt(LABEL_STYLE);
            title = bundle.getString(TITLE);
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
            outBundle.putInt(TOP_AXIS_MARGIN, topAxisMargin);
            outBundle.putInt(BOTTOM_AXIS_MARGIN, bottomAxisMargin);
            outBundle.putInt(RIGHT_AXIS_MARGIN, rightAxisMargin);
            outBundle.putInt(LEFT_AXIS_MARGIN, leftAxisMargin);
            outBundle.putBoolean(SHOULD_DRAW_BOX, shouldDrawBox);
            outBundle.putInt(LABEL_STYLE, labelStyle);
            outBundle.putString(TITLE, title);
            out.writeBundle(outBundle);
        }

        public static final Parcelable.Creator<GraphViewSavedState> CREATOR
                = new Parcelable.Creator<GraphViewSavedState>() {
            public GraphViewSavedState createFromParcel(Parcel in) {
                return new GraphViewSavedState(in);
            }

            public GraphViewSavedState[] newArray(int size) {
                return new GraphViewSavedState[size];
            }
        };
    }
}

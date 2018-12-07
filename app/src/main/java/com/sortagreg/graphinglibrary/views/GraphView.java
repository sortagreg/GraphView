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
import android.view.View;

import com.sortagreg.graphinglibrary.R;
import com.sortagreg.graphinglibrary.models.GraphViewDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * GraphView - Custom Graph View Class
 *
 * @author Marshall Ladd
 */
public class GraphView extends View {
    private Context context;

    public static final int STANDARD_GRAPH = 1;
    public static final int UNFOLDED_GRAPH = 2;
    public static final int CONSTANT_GRAPH = 3;
    public static final int STATE_GRAPH = 4;

    private String graphTitle = "Test Graph Title";

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
    private int numberOfVerticalLabels;
    private int numberOfHorizontalLabels;

    private boolean shouldDrawBox;

//    private List<PointF[]> standardDataSetList;
//    private List<PointF> constantLineDataSetList;
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
    private float pixelsPerX;
    private float pixelsPerY;

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
        typedArray.recycle();

        // Init other values here
        setPaintLines();
//        standardDataSetList = new ArrayList<>();
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
        drawStandardDataSets(canvas);
        drawStandardTextLabels(canvas);
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
    private void drawStandardDataSets(Canvas canvas) {
        getMinMaxOfDataSets();
        rangeOfXValues = adjustedDataSetMaxX - adjustedDataSetMinX;
        rangeOfYValues = adjustedDataSetMaxY - adjustedDataSetMinY;
        pixelsPerX = ((float) canvas.getWidth() - (float) leftAxisMargin - (float) rightAxisMargin) / (rangeOfXValues);
        pixelsPerY = ((float) canvas.getHeight() - (float) topAxisMargin - (float) bottomAxisMargin) / (rangeOfYValues);

        for (GraphViewDataModel dataModel : dataSetList) {
            switch (dataModel.getGraphType()) {
                case STANDARD_GRAPH:
                    for (int i = 0; i < dataModel.getDataSet().length - 1; i++) {
                        PointF startPoint = convertXYtoPx(dataModel.getDataSet()[i], canvas, pixelsPerX, pixelsPerY);
                        PointF endPoint = convertXYtoPx(dataModel.getDataSet()[i + 1], canvas, pixelsPerX, pixelsPerY);
                        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
                    }
                    break;
                case UNFOLDED_GRAPH:

                    break;
                case CONSTANT_GRAPH:
                    PointF startPoint = convertXYtoPx(new PointF((float) dataSetMinX , dataModel.getDataSet()[0].y), canvas, pixelsPerX, pixelsPerY);
                    PointF endPoint = convertXYtoPx(new PointF((float) dataSetMaxX , dataModel.getDataSet()[0].y), canvas, pixelsPerX, pixelsPerY);
                    canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, dataModel.getPaint());
                    break;
                case STATE_GRAPH:
                    break;
            }


        }
    }

    private void drawStandardTextLabels(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(30f);
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
        canvas.drawText(graphTitle, canvas.getWidth() / 2f, 50f, textPaint);
    }

    /**
     * Draws 3 red test points on the graph in fixed locations, bottom left, center, top right.
     *
     * Method used to help debug issues and test algorithms.  These three dots will always be drawn
     * at their respective places as they are determined from the canvas size, and not by any
     * derived calculation.
     *
     * @param canvas
     * @param pixelsPerX
     * @param pixelsPerY
     */
    private void drawTestPoints(Canvas canvas, float pixelsPerX, float pixelsPerY) {
        Paint testPaint = new Paint();
        // KNOWN ORIGIN
        testPaint.setColor(0xAAAA0000);//Red
        canvas.drawCircle((float) leftAxisMargin, (float) canvas.getHeight() - (float) bottomAxisMargin, 30f, testPaint);
        // KNOWN (MAX X, MAX Y)
        canvas.drawCircle((float) canvas.getWidth() - (float) rightAxisMargin, (float) topAxisMargin, 30f, testPaint);
        // KNOWN (MID, MID)
        canvas.drawCircle(leftAxisMargin + ((float) (canvas.getWidth() - leftAxisMargin - rightAxisMargin) / 2f), canvas.getHeight() - bottomAxisMargin - ((float) (canvas.getHeight() - bottomAxisMargin -topAxisMargin) / 2f), 30f, testPaint);
        // TEST conversion method (2,2)
        testPaint.setColor(0xAA0000AA);
        PointF dataPoint = new PointF(2f, 2f);
        PointF convertedDP = convertXYtoPx(dataPoint, canvas, pixelsPerX, pixelsPerY);
        canvas.drawCircle(convertedDP.x, convertedDP.y, 15f, testPaint);
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
                if (dataSet.getGraphType() == GraphView.STANDARD_GRAPH) {
                    dataSetMaxX = Math.max(dataSetMaxX, dataPoint.x);
                    dataSetMinX = Math.min(dataSetMinX, dataPoint.x);
                }
                dataSetMaxY = Math.max(dataSetMaxY, dataPoint.y);
                dataSetMinY = Math.min(dataSetMinY, dataPoint.y);
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

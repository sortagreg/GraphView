package com.sortagreg.graphview;

import android.graphics.Paint;
import android.graphics.PointF;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data class for a GraphView.
 *
 * @author Marshall Ladd
 */
public class GraphViewDataModel {

    /**
     * Used to specify which type of graph a data set is.
     */
    public static final int STANDARD_LINE = 1;
    public static final int UNFOLDED_LINE = 2;
    public static final int CONSTANT_LINE = 3;
    public static final int STATE_LINE = 4;

    private PointF[] dataSet;
    private Paint paint;
    private Integer graphType;

    /**
     * Constructor for a data set, used in GraphView
     *
     * @param dataSet PointF[] of points to be drawn
     * @param paint Paint object to specify the properties of how the data should be drawn
     * @param graphType Type of graph to draw this data set as
     */
    public GraphViewDataModel(PointF[] dataSet, Paint paint, Integer graphType) {
        this.dataSet = dataSet;
        this.paint = paint;
        this.graphType = graphType;
    }

    /**
     * Constructor for a data set, used in GraphView
     *
     * @param paint Paint object to specify the properties of how the data should be drawn
     * @param graphType Type of graph to draw this data set as
     */
    public GraphViewDataModel(long[] xSet, long[] ySet, Paint paint, Integer graphType) {
        this.dataSet = new PointF[ySet.length];
        for (int i = 0; i < this.dataSet.length; i++) {
            dataSet[i] = new PointF(xSet[i], ySet[i]);
        }
        this.paint = paint;
        this.graphType = graphType;
    }

    /**
     * Returns the data set
     *
     * @return PointF[] data set
     */
    public PointF[] getDataSet() {
        return dataSet;
    }

    /**
     * Set the data set
     *
     * @param dataSet
     */
    public void setDataSet(PointF[] dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Get the Paint object assigned to the data model
     *
     * @return Paint
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     *
     * @param paint
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Get the Integer flag that specifies which typ of graph to draw this data set as
     *
     * @return
     */
    public Integer getGraphType() {
        return graphType;
    }

    /**
     * Sets which type of graph to draw the data as.
     * Use the constant values in this class.
     *
     * @param graphType
     */
    public void setGraphType(Integer graphType) {
        this.graphType = graphType;
    }

    @Override
    public String toString() {
        return "GraphViewDataModel{" +
                "dataSet=" + Arrays.toString(dataSet) +
                ", paint=" + paint +
                ", graphType=" + graphType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphViewDataModel that = (GraphViewDataModel) o;
        return Arrays.equals(dataSet, that.dataSet) &&
                Objects.equals(paint, that.paint) &&
                Objects.equals(graphType, that.graphType);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(paint, graphType);
        result = 31 * result + Arrays.hashCode(dataSet);
        return result;
    }
}

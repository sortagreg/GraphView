package com.sortagreg.graphinglibrary.models;

import android.graphics.Paint;
import android.graphics.PointF;

import java.util.Arrays;
import java.util.Objects;

public class GraphViewDataModel {

    private PointF[] dataSet;
    private Paint paint;
    private Integer graphType;

    public GraphViewDataModel(PointF[] dataSet, Paint paint, Integer graphType) {
        this.dataSet = dataSet;
        this.paint = paint;
        this.graphType = graphType;
    }

    public PointF[] getDataSet() {
        return dataSet;
    }

    public void setDataSet(PointF[] dataSet) {
        this.dataSet = dataSet;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Integer getGraphType() {
        return graphType;
    }

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

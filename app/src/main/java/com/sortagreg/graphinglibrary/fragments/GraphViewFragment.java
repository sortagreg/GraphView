package com.sortagreg.graphinglibrary.fragments;


import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sortagreg.graphinglibrary.R;
import com.sortagreg.graphinglibrary.views.GraphView;
import com.sortagreg.graphinglibrary.views.GraphViewDataModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Demo Fragment for GraphView
 *
 * @author Marshall Ladd
 */
public class GraphViewFragment extends Fragment {

    List<GraphViewDataModel> dataSetList = new ArrayList<>();
    List<com.sortagreg.graphview.GraphViewDataModel> libraryDataSetList = new ArrayList<>();
    int DATA_SET_LENGTH = 50;

    @BindView(R.id.graphView)
    GraphView graphView;
    @BindView(R.id.libraryGraphView)
    com.sortagreg.graphview.GraphView libraryGraphView;
    public GraphViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_view, container, false);
        ButterKnife.bind(this, view);

        drawUnfoldedDataSet();

//        drawCrossHairs();

//        drawExponentialCurves();

//        drawBinaryStateLine();

//        drawConstants();

        graphView.addToDataSetListBulk(dataSetList);
        libraryGraphView.addToDataSetListBulk(libraryDataSetList);

        return view;
    }

    private void drawUnfoldedDataSet() {
        Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFF0000FF);
        PointF[] cyclicGraph = new PointF[]{new PointF(1,5), new PointF(2,10), new PointF(3,15), new PointF(4,15), new PointF(5,15), new PointF(6,20), new PointF(5,25), new PointF(4, 30), new PointF(3,30), new PointF(3,30), new PointF(3,30), new PointF(3,25), new PointF(2, 20), new PointF(1,5), new PointF(1,10), new PointF(1,5), new PointF(1,10)};
        GraphViewDataModel cyclicDataModel = new GraphViewDataModel(cyclicGraph, paint, GraphViewDataModel.UNFOLDED_LINE);
        com.sortagreg.graphview.GraphViewDataModel libraryCyclicDataModelFolded = new com.sortagreg.graphview.GraphViewDataModel(cyclicGraph, paint, GraphViewDataModel.STANDARD_LINE);
        graphView.addToDataSetList(cyclicDataModel);
        libraryGraphView.addToDataSetList(libraryCyclicDataModelFolded);
        libraryGraphView.setTitle("Standard Graph & Labels");
        graphView.setTitle("Incremental Graph & Labels");
//        dataSetList.add(cyclicDataModelFolded);
//        libraryDataSetList.add(libraryCyclicDataModelFolded);
    }

    private void drawExponentialCurves() {
        PointF[] exponentialCurve = new PointF[DATA_SET_LENGTH];
        for (int i = 0; i <= DATA_SET_LENGTH - 1; i++) {
            float x = i - 25;
            PointF point = new PointF(x, x * x * x);
            exponentialCurve[i] = point;
        }
        Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFFFF0000);
        GraphViewDataModel expCurve = new GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
        com.sortagreg.graphview.GraphViewDataModel libExpCurve = new com.sortagreg.graphview.GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(expCurve);
        libraryDataSetList.add(libExpCurve);

        exponentialCurve = new PointF[DATA_SET_LENGTH];
        for (int i = 0; i <= DATA_SET_LENGTH - 1; i++) {
            float x = i - 25;
            PointF point = new PointF(x, -(x * x * x));
            exponentialCurve[i] = point;
        }
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFF00FF00);
        GraphViewDataModel inverseExpCurve = new GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
        com.sortagreg.graphview.GraphViewDataModel libInvExp = new com.sortagreg.graphview.GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(inverseExpCurve);
        libraryDataSetList.add(libInvExp);

//        exponentialCurve = new PointF[DATA_SET_LENGTH];
//        for (int i = 0; i <= DATA_SET_LENGTH - 1; i++) {
//            float x = i;
//            PointF point = new PointF(x, x * x);
//            exponentialCurve[i] = point;
//        }
//        paint = new Paint();
//        paint.setStrokeWidth(5f);
//        paint.setColor(0xFF0000FF);
//        GraphViewDataModel squareCurve = new GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
//        dataSetList.add(squareCurve);
    }

    private void drawBinaryStateLine() {
        Paint paint = new Paint();
        paint.setColor(0xFFFF00FF);
        paint.setStrokeWidth(5f);
        boolean state = false;
        PointF[] stateLine = new PointF[DATA_SET_LENGTH];
        for (int i = 0; i <= stateLine.length - 1; i++) {
            stateLine[i] = new PointF(0f, state ? 1 : 0);
            if (i % 3 == 0) state = !state;
        }
        GraphViewDataModel graphViewDataModel = new GraphViewDataModel(stateLine, paint, GraphViewDataModel.STATE_LINE);
        dataSetList.add(graphViewDataModel);
    }

    private void drawConstants() {
        PointF constantLine = new PointF(0f, 80f);
        PointF[] dataSet = new PointF[]{constantLine};
        Paint paint = new Paint();
        paint.setColor(0xFF00FF00);
        paint.setStrokeWidth(5f);
        GraphViewDataModel graphViewDataModel = new GraphViewDataModel(dataSet, paint, GraphViewDataModel.CONSTANT_LINE);
        dataSetList.add(graphViewDataModel);
    }

    private void drawCrossHairs() {
        Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFFFF0000);
        PointF[] negativeValueArray = new PointF[]{new PointF(-1, -1), new PointF(-10, -10)};
        GraphViewDataModel negativeDataSet = new GraphViewDataModel(negativeValueArray, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(negativeDataSet);
        PointF[] inverseNegativeValueArray = new PointF[]{new PointF(-1, 1), new PointF(-10, 10)};
        GraphViewDataModel inverseNegativeDataSet = new GraphViewDataModel(inverseNegativeValueArray, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(inverseNegativeDataSet);

        PointF[] positiveValueArray = new PointF[]{new PointF(1, 1), new PointF(10, 10)};
        GraphViewDataModel positiveDataSet = new GraphViewDataModel(positiveValueArray, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(positiveDataSet);
        PointF[] inversePositiveValueArray = new PointF[]{new PointF(1, -1), new PointF(10, -10)};
        GraphViewDataModel inversePositiveDataSet = new GraphViewDataModel(inversePositiveValueArray, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(inversePositiveDataSet);
    }

}

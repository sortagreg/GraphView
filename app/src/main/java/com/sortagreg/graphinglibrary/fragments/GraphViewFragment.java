package com.sortagreg.graphinglibrary.fragments;


import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sortagreg.graphinglibrary.R;
import com.sortagreg.graphview.GraphView;
import com.sortagreg.graphview.GraphViewDataModel;

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

    @BindView(R.id.graphViewSingleVar)
    GraphView graphViewSingleVar;
    @BindView(R.id.graphView)
    GraphView graphView;
    public GraphViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_view, container, false);
        ButterKnife.bind(this, view);
        List<GraphViewDataModel> dataSetList = new ArrayList<>();
        int DATA_SET_LENGTH = 50;
        Paint paint;

        PointF[] exponentialCurve = new PointF[DATA_SET_LENGTH];
        for (int i = 0; i <= DATA_SET_LENGTH - 1; i++) {
            float x = i - 25;
            PointF point = new PointF(x, x * x * x);
            exponentialCurve[i] = point;
        }
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFFFF3355);
        GraphViewDataModel expCurve = new GraphViewDataModel(exponentialCurve, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(expCurve);


//        // State line
//        paint = new Paint();
//        paint.setColor(0xFFFF00FF);
//        paint.setStrokeWidth(5f);
//        boolean state = false;
//        PointF[] stateLine = new PointF[DATA_SET_LENGTH];
//        for (int i = 0; i <= stateLine.length - 1; i++) {
//            stateLine[i] = new PointF(0f, state ? 1 : 0);
//            if (i % 10 == 0) state = !state;
//        }
//        GraphViewDataModel graphViewDataModel = new GraphViewDataModel(stateLine, paint, GraphViewDataModel.STATE_LINE);
//        dataSetList.add(graphViewDataModel);
//
//        // Draw two constant lines
//        PointF constantLine = new PointF(0f, 876f);
//        PointF[] bigDataSet = new PointF[1];
//        bigDataSet[0] = constantLine;
//        paint = new Paint();
//        paint.setColor(0xFF00FF00);
//        paint.setStrokeWidth(5f);
//        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.CONSTANT_LINE);
//        dataSetList.add(graphViewDataModel);
//
//        constantLine = new PointF(0f, 4140f);
//        bigDataSet = new PointF[1];
//        bigDataSet[0] = constantLine;
//        paint = new Paint();
//        paint.setColor(0xFF00FFFF);
//        paint.setStrokeWidth(5f);
//        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.CONSTANT_LINE);
//        dataSetList.add(graphViewDataModel);

        graphViewSingleVar.addToDataSetListBulk(dataSetList);
        graphView.addToDataSetListBulk(dataSetList);

        return view;
    }

}

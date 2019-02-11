package com.sortagreg.graphinglibrary.fragments;


import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sortagreg.graphinglibrary.R;
import com.sortagreg.graphview.GraphView;
import com.sortagreg.graphview.GraphViewDataModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Demo Fragment for GraphView
 *
 * @author Marshall Ladd
 */
public class GraphViewFragment extends Fragment {

    List<GraphViewDataModel> dataSetList = new ArrayList<>();
    List<com.sortagreg.graphview.GraphViewDataModel> libraryDataSetList = new ArrayList<>();
    int DATA_SET_LENGTH = 50;

    com.sortagreg.graphview.GraphView graphViewTop;

    com.sortagreg.graphview.GraphView graphViewBottom;
    public GraphViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_view, container, false);

        graphViewTop = (GraphView) view.findViewById(R.id.graphViewTop);
        graphViewBottom = (GraphView) view.findViewById(R.id.graphViewBottom);


//        drawCrossHairs();

//        drawExponentialCurves();


//        drawConstants();

//        graphViewTop.addToDataSetListBulk(dataSetList);
//        graphViewBottom.addToDataSetListBulk(libraryDataSetList);
//        graphViewTop.setLeftSideText("TEST");
//        graphViewTop.setRightSideText("TEST");
//        graphViewTop.setBottomText("TEST");

        drawUnfoldedDataSet();
        drawBinaryStateLine();

        return view;
    }

    private void drawUnfoldedDataSet() {
        float[] asp1000 = {4500,4300,3700,3000,2400,1800,1300,900,600,400,200,100,100,200,400,600,1000,1400,1900,2500,3100,3800,4700,5500,6500,7500,8700,9800,11100,12600,14000,15500,17000,18500,20100,21800,23500,25200,27000,28800,30700,32600,34500,36400,38400,40400,42400,44400,46400,48400,50500,52500,54600,56600,58700,60800,62800,64800,66900,68900,70900,72900,74900,76900,78800,80800,82700,84600,86500,88400,90200,92000,93800,95600,97400,99100,100800,102500,104200,105900,107500,109100,110600,112200,113700,115200,116600,118100,119500,120900,122200,123500,124800,126100,127300,128500,129700,130800,131900,132900,133900,134900,135800,136700,137600,138400,139100,139800,140500,141100,141600,142100,142500,142900,143200,143400,143600,143700,143700,143700,143600,143300,143000,142700,142200,141600,141000,140200,139400,138500,137500,136500,135300,134100,132700,131300,129800,128300,126600,124900,123200,121400,119500,117500,115600,113500,111500,109300,107200,105000,102800,100500,98300,96000,93700,91400,89000,86700,84400,82000,79600,77300,74900,72500,70200,67800,65500,63200,60800,58500,56300,54000,51800,49600,47400,45200,43100,41000,39000,36900,34900,32900,31000,29100,27300,25500,23800,22100,20400,18800,17300,15800,14400,13000,11700,10400,9200,8100,7100,5400};
        float[] asl1001 = {7679,7690,7739,7791,7826,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,8099,8443,8733,8979,9206,9459,9735,10004,10226,10444,10810,11160,11472,11789,12154,12547,12930,13299,13686,14054,14398,14720,14952,14991,14787,14514,14261,14024,13807,13610,13425,13342,13410,13579,13744,13873,13995,14121,14198,14220,14203,14133,13993,13864,13770,13696,13629,13582,13587,13602,13653,13725,13777,13824,13842,13862,13863,13825,13784,13736,13687,13648,13616,13581,13571,13560,13581,13626,13638,13615,13633,13654,13640,13625,13612,13584,13543,13524,13523,13493,13449,13444,13460,13445,13427,13424,13430,13404,13390,13393,13356,13314,13310,13310,13310,13310,13310,13310,13341,13341,13341,13341,13341,13341,13341,13341,13212,12711,12340,12071,11841,11567,11272,10997,10741,10438,10123,9813,9470,9092,8696,8320,7909,7453,7009,6626,6227,5916,5863,6036,6327,6636,6920,7222,7515,7728,7836,7792,7596,7373,7150,6942,6764,6611,6522,6542,6660,6830,7021,7184,7318,7468,7554,7557,7521,7441,7315,7201,7119,7045,6997,6981,7034,7116,7219,7314,7413,7501,7556,7605,7615,7574,7535,7491,7433,7392,7417,7441,7433,7466,7623};


        Paint paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFF0000FF);
        GraphViewDataModel testGraph = new GraphViewDataModel(asp1000, asl1001, paint, GraphViewDataModel.STANDARD_LINE);
        GraphViewDataModel testGraph2 = new GraphViewDataModel(asl1001, asp1000, paint, GraphViewDataModel.STANDARD_LINE);

        com.sortagreg.graphview.GraphViewDataModel libraryCyclicDataModelUnfolded = new com.sortagreg.graphview.GraphViewDataModel(asp1000, asl1001, paint, GraphViewDataModel.UNFOLDED_LINE);
        graphViewBottom.addToDataSetList(libraryCyclicDataModelUnfolded);
        graphViewBottom.setTitle("Incremental Graph & Labels");
        graphViewTop.addToDataSetList(testGraph);
        graphViewTop.addToSecondaryDataSetList(testGraph2);
        graphViewTop.setTitle("Standard Graph & Labels");
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

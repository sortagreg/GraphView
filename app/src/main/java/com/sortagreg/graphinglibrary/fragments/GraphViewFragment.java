package com.sortagreg.graphinglibrary.fragments;


import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sortagreg.graphinglibrary.R;
import com.sortagreg.graphinglibrary.models.GraphViewDataModel;
import com.sortagreg.graphinglibrary.views.GraphView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphViewFragment extends Fragment {

    @BindView(R.id.graphView)
    GraphView graphView;
    @BindView(R.id.verticalMarkerInput)
    EditText verticalMarkerInput;
    @BindView(R.id.verticalMarkerButton)
    Button verticalMarkerButton;

    public GraphViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_view, container, false);
        ButterKnife.bind(this, view);
        List<GraphViewDataModel> dataSetList = new ArrayList<>();
//        PointF[] dataSet = {new PointF(0.0f,0.0f), new PointF(4f, 4f)};
//        dataSetList.add(dataSet);
//        PointF[] dataSet2 = {new PointF(1.0f,0.0f), new PointF(4f, 3f)};
//        dataSetList.add(dataSet2);
//        PointF[] dataSet3 = {new PointF(0.0f,-1.0f), new PointF(1.0f,1.0f), new PointF(2f, 2f), new PointF(3f, 3f), new PointF(4f, 4f), new PointF(2f, 4f), new PointF(1f, 3f)};
//        dataSetList.add(dataSet3);

        // Draw two cyclic data sets
        int[] asp1000 = {4500,4300,3700,3000,2400,1800,1300,900,600,400,200,100,100,200,400,600,1000,1400,1900,2500,3100,3800,4700,5500,6500,7500,8700,9800,11100,12600,14000,15500,17000,18500,20100,21800,23500,25200,27000,28800,30700,32600,34500,36400,38400,40400,42400,44400,46400,48400,50500,52500,54600,56600,58700,60800,62800,64800,66900,68900,70900,72900,74900,76900,78800,80800,82700,84600,86500,88400,90200,92000,93800,95600,97400,99100,100800,102500,104200,105900,107500,109100,110600,112200,113700,115200,116600,118100,119500,120900,122200,123500,124800,126100,127300,128500,129700,130800,131900,132900,133900,134900,135800,136700,137600,138400,139100,139800,140500,141100,141600,142100,142500,142900,143200,143400,143600,143700,143700,143700,143600,143300,143000,142700,142200,141600,141000,140200,139400,138500,137500,136500,135300,134100,132700,131300,129800,128300,126600,124900,123200,121400,119500,117500,115600,113500,111500,109300,107200,105000,102800,100500,98300,96000,93700,91400,89000,86700,84400,82000,79600,77300,74900,72500,70200,67800,65500,63200,60800,58500,56300,54000,51800,49600,47400,45200,43100,41000,39000,36900,34900,32900,31000,29100,27300,25500,23800,22100,20400,18800,17300,15800,14400,13000,11700,10400,9200,8100,7100,5400};
        int[] asl1001 = {7679,7690,7739,7791,7826,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,7823,8099,8443,8733,8979,9206,9459,9735,10004,10226,10444,10810,11160,11472,11789,12154,12547,12930,13299,13686,14054,14398,14720,14952,14991,14787,14514,14261,14024,13807,13610,13425,13342,13410,13579,13744,13873,13995,14121,14198,14220,14203,14133,13993,13864,13770,13696,13629,13582,13587,13602,13653,13725,13777,13824,13842,13862,13863,13825,13784,13736,13687,13648,13616,13581,13571,13560,13581,13626,13638,13615,13633,13654,13640,13625,13612,13584,13543,13524,13523,13493,13449,13444,13460,13445,13427,13424,13430,13404,13390,13393,13356,13314,13310,13310,13310,13310,13310,13310,13341,13341,13341,13341,13341,13341,13341,13341,13212,12711,12340,12071,11841,11567,11272,10997,10741,10438,10123,9813,9470,9092,8696,8320,7909,7453,7009,6626,6227,5916,5863,6036,6327,6636,6920,7222,7515,7728,7836,7792,7596,7373,7150,6942,6764,6611,6522,6542,6660,6830,7021,7184,7318,7468,7554,7557,7521,7441,7315,7201,7119,7045,6997,6981,7034,7116,7219,7314,7413,7501,7556,7605,7615,7574,7535,7491,7433,7392,7417,7441,7433,7466,7623};
        PointF[] bigDataSet = new PointF[asp1000.length];
        for (int i = 0; i < asp1000.length; i++) {
            PointF dataPoint = new PointF((float) asp1000[i], (float) asl1001[i]);
            bigDataSet[i] = dataPoint;
        }
        Paint paint = new Paint();
        paint.setColor(0xFF0000FF);
        paint.setStrokeWidth(5f);
        GraphViewDataModel graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(graphViewDataModel);
        // Unfolded
//        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.UNFOLDED_LINE);
//        dataSetList.add(graphViewDataModel);


        int[] adp1002 = {9800,8600,8600,7400,6200,5200,5200,5200,5200,5200,5200,5200,5200,5200,5200,5200,5200,5200,5200,5200,4200,3200,2400,1600,1100,700,300,200,0,100,200,300,400,600,700,900,1000,1200,1400,1600,2000,2400,3000,4100,5500,7300,9400,11700,14200,16700,19300,21800,24100,26200,28200,30000,31700,33500,35200,37000,38900,40900,43100,45200,47500,49700,52000,54100,56200,58200,60200,62000,63800,65500,67300,69000,70800,72500,74300,76100,77900,79700,81500,83200,84900,86600,88200,89700,91200,92600,94100,95400,96900,98200,99600,100900,102300,103600,104800,106100,107300,108500,109600,110700,111700,112800,113700,114500,115400,116200,117000,117000,117000,117000,117000,117000,117000,117000,117000,117000,117000,117000,117000,117000,117000,117700,118500,119300,120000,120600,121100,121400,121700,121900,122000,122000,122000,121900,121700,121600,121400,121200,121000,120800,120600,120300,119900,119400,118400,116900,115000,112700,110100,107300,104400,101400,98500,95800,93400,91200,89200,87300,85400,83600,81700,79600,77300,74900,72300,69600,66900,64200,61500,59000,56500,54200,52100,50100,48100,46200,44300,42400,40400,38300,36200,34100,32100,29900,27900,25900,24000,22200,20600,19100,17600,16200,14900,13500,12300,11000};
        int[] adl1003 = {-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,-51,438,835,1576,1890,2150,2472,2842,3108,3502,3788,4152,4517,4783,5173,5423,5621,5575,5668,5680,5771,5760,5733,5599,5603,5676,5584,5695,5605,5634,5596,5656,5720,5680,5702,5602,5655,5658,5633,5589,5587,5620,5538,5488,5613,5563,5594,5531,5501,5587,5593,5544,5504,5563,5540,5562,5469,5496,5508,5466,5494,5517,5484,5419,5527,5502,5438,5444,5492,5467,5444,5426,5456,5419,5389,5466,5369,5400,5370,5400,5400,5400,5400,5400,5400,5400,5400,5400,5400,5400,5400,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,5335,4901,4381,3947,3564,3257,2893,2534,2176,1859,1367,1047,698,331,-36,-168,-192,-239,-314,-389,-352,-285,-203,-129,-187,-225,-231,-234,-246,-266,-336,-335,-362,-383,-328,-353,-323,-259,-265,-227,-176,-180,-185,-208,-202,-226,-223,-197,-248,-286,-207,-188,-202,-232,-176,-115,-104,-131,-98,-81,-128,-124,-89,-26,-85,-126,-152,-12};
        bigDataSet = new PointF[adp1002.length];
        for (int i = 0; i < adp1002.length; i++) {
            PointF dataPoint = new PointF((float) adp1002[i], (float) adl1003[i]);
            bigDataSet[i] = dataPoint;
        }
        paint = new Paint();
        paint.setColor(0xFFFF0000);
        paint.setStrokeWidth(5f);
        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.STANDARD_LINE);
        dataSetList.add(graphViewDataModel);
        // Unfolded
//        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.UNFOLDED_LINE);
//        dataSetList.add(graphViewDataModel);

        // Draw two constant lines
        PointF constantLine = new PointF(0f, 10876f);
        bigDataSet = new PointF[1];
        bigDataSet[0] = constantLine;
        paint = new Paint();
        paint.setColor(0xFF00FF00);
        paint.setStrokeWidth(5f);
        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.CONSTANT_LINE);
        dataSetList.add(graphViewDataModel);

        constantLine = new PointF(0f, 4140f);
        bigDataSet = new PointF[1];
        bigDataSet[0] = constantLine;
        paint = new Paint();
        paint.setColor(0xFF00FFFF);
        paint.setStrokeWidth(5f);
        graphViewDataModel = new GraphViewDataModel(bigDataSet, paint, GraphViewDataModel.CONSTANT_LINE);
        dataSetList.add(graphViewDataModel);

        graphView.addToDataSetListBulk(dataSetList);

        return view;
    }

    @OnClick(R.id.verticalMarkerButton)
    public void verticalMarkerButtonOnClick() {
        if (!verticalMarkerInput.getText().toString().equals("")) {
            graphView.setNumberOfVerticalMarkers(Integer.valueOf(verticalMarkerInput.getText().toString()));
        } else Toast.makeText(getContext(), "Must input a value.", Toast.LENGTH_LONG).show();
    }

}

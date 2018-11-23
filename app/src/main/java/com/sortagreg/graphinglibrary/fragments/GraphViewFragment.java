package com.sortagreg.graphinglibrary.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sortagreg.graphinglibrary.R;
import com.sortagreg.graphinglibrary.views.GraphView;

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
        return view;
    }

    @OnClick(R.id.verticalMarkerButton)
    public void verticalMarkerButtonOnClick() {
        if (!verticalMarkerInput.getText().toString().equals("")) {
            graphView.setNumberOfVerticalMarkers(Integer.valueOf(verticalMarkerInput.getText().toString()));
        } else Toast.makeText(getContext(), "Must input a value.", Toast.LENGTH_LONG).show();
    }

}

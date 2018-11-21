package com.sortagreg.graphinglibrary.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sortagreg.graphinglibrary.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoxDrawingFragment extends Fragment {

    public BoxDrawingFragment() {
        // Required empty public constructor
    }

    public static Bundle newBundle() {
        Bundle bundle = new Bundle();
        // TODO add variables to Bundle
        return bundle;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_box_drawing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}

package com.lng.lngattendancesystem.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lng.lngattendancesystem.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragments extends Fragment {

    View view;

    public HomeFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.home_fragment, container, false);
        return view;
    }

}

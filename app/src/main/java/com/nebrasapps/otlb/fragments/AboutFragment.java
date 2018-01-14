package com.nebrasapps.otlb.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nebrasapps.otlb.R;



/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class AboutFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragm, container, false);
        view.findViewById(R.id.img_nebras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loading company website on click of logo
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nebrasapps.com"));
                startActivity(browserIntent);
            }
        });


        return view;
    }


}

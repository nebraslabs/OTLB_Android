package com.nebrasapps.otlb.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nebrasapps.otlb.R;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


@SuppressLint("AppCompatCustomView")
public class CustomText extends TextView {
    public CustomText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        try {
            setTypeface(Typeface.createFromAsset(getContext().getAssets(), getContext().getResources().getString(R.string.fontStyle)));
        } catch (Exception e) {
        }
    }
}

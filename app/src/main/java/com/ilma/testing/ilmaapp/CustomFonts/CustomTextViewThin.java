package com.ilma.testing.ilmaapp.CustomFonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by fazal on 8/22/2017.
 */

public class CustomTextViewThin extends TextView {

    public CustomTextViewThin(Context context) {
        super(context);
    }

    public CustomTextViewThin(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextViewThin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "roboto_thin.ttf");
            setTypeface(tf);
        }
    }
}

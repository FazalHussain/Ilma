package com.ilma.testing.ilmaapp.CustomFonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by fazal on 8/21/2017.
 */

public class CustomTextViewMedium extends TextView {
    public CustomTextViewMedium(Context context) {
        super(context);
        init();
    }

    public CustomTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextViewMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "roboto_medium.ttf");
            setTypeface(tf);
        }
    }
}

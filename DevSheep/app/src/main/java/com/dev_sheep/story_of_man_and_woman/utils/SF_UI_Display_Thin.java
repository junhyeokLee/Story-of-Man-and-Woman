package com.dev_sheep.story_of_man_and_woman.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class SF_UI_Display_Thin extends AppCompatTextView {

    public SF_UI_Display_Thin(Context context) {
        super(context);
        init();
    }

    public SF_UI_Display_Thin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SF_UI_Display_Thin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLineSpacing(0, 0.9f);

        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            setTypeface(tf);
        }
    }


}

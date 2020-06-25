package com.dev_sheep.story_of_man_and_woman.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class Bold_TextView  extends AppCompatTextView {
    public Bold_TextView(Context context) {
        super(context);
        init();
    }

    public Bold_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Bold_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLineSpacing(0, 0.9f);

        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
            setTypeface(tf);
        }
    }
}

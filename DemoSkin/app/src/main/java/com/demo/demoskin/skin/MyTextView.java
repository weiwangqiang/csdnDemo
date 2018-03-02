package com.demo.demoskin.skin;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * class description here
 *
 *  自定义控件
 */

public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
    }
    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

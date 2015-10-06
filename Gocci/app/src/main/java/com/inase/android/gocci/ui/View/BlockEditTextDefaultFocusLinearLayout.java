package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by kinagafuji on 15/08/25.
 */
public class BlockEditTextDefaultFocusLinearLayout extends LinearLayout {

    public BlockEditTextDefaultFocusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }
}

package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by kinagafuji on 15/08/25.
 */
public class BlockEditTextDefaultFocusFrameLayout extends FrameLayout {

    public BlockEditTextDefaultFocusFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }
}

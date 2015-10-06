package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by kinagafuji on 15/08/25.
 */
public class BlockEditTextDefaultFocusRelativeLayout extends RelativeLayout {

    public BlockEditTextDefaultFocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }
}

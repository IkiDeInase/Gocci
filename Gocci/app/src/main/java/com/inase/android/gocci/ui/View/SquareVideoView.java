package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;


public class SquareVideoView extends VideoView {
    private AttributeSet mAttributeSet;

    public SquareVideoView(final Context context) {
        super(context);
    }

    public SquareVideoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mAttributeSet = attrs;
    }

    public SquareVideoView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttributeSet = attrs;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }

    public AttributeSet getAttributes() {
        return mAttributeSet;
    }
}

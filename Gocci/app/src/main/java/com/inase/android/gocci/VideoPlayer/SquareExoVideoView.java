package com.inase.android.gocci.VideoPlayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by kinagafuji on 15/07/16.
 */
public class SquareExoVideoView extends SurfaceView {

    public SquareExoVideoView(final Context context) {
        super(context);
    }

    public SquareExoVideoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }
}

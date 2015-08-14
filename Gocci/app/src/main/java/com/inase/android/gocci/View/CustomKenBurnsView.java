package com.inase.android.gocci.View;

import android.content.Context;
import android.util.AttributeSet;

import com.flaviofaria.kenburnsview.KenBurnsView;

/**
 * Created by kinagafuji on 15/08/13.
 */
public class CustomKenBurnsView extends KenBurnsView {

    private static final float DEFAULT_RATIO = 1.618f;

    private float ratio;
    private boolean autoScale;

    public CustomKenBurnsView(Context context) {
        super(context);
    }

    public CustomKenBurnsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);
    }
}

package com.inase.android.gocci.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.inase.android.gocci.R;

/**
 * Progress bar on the top of screen
 *
 * @author xiaodong
 */
public class ProgressView extends View {

    private final Paint mPaint = new Paint();
    private int shouldBeWidth = 0;

    public void setWidth(int width) {
        shouldBeWidth = width;
    }

    public int getCurrentWidth() {
        return shouldBeWidth;
    }

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        init();
    }

    public ProgressView(Context context, AttributeSet paramAttributeSet,
                        int paramInt) {
        super(context, paramAttributeSet, paramInt);
        init();
    }

    private void init() {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(getResources().getColor(R.color.gocci_progress));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.shouldBeWidth > 0) {
            canvas.drawRect(0.0F, 0.0F, this.shouldBeWidth,
                    getMeasuredHeight(), mPaint);
        }
    }


}

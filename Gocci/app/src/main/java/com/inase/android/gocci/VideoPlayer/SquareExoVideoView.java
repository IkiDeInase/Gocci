package com.inase.android.gocci.VideoPlayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.inase.android.gocci.common.Const;

/**
 * Created by kinagafuji on 15/07/16.
 */
public class SquareExoVideoView extends SurfaceView {

    private AttributeSet mAttributeSet;

    public SquareExoVideoView(final Context context) {
        super(context);
    }

    public SquareExoVideoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
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

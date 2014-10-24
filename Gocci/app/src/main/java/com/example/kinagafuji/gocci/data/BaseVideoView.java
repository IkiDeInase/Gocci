package com.example.kinagafuji.gocci.data;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.VideoView;

public class BaseVideoView extends VideoView implements SurfaceHolder.Callback {

    MediaPlayer mediaPlayer;

    public BaseVideoView(Context context) {
        super(context);
    }
    public BaseVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public BaseVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaPlayer.setDisplay(null);
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

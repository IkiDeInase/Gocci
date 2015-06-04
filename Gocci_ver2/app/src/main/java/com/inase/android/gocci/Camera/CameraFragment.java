package com.inase.android.gocci.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Base.CircleProgressBar;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.ProgressView;

import java.io.IOException;
import java.util.Date;

public class CameraFragment extends Fragment {
    private static final boolean DEBUG = true;    // TODO set false on releasing
    private static final String TAG = "GocciCamera";

    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;

    private ImageButton stopButton;
    /**
     * button for start/stop recording
     */
    private TLMediaVideoEncoder mVideoEncoder;
    private TLMediaAudioEncoder mAudioEncoder;
    private TLMediaMovieBuilder mMuxer;
    private boolean mIsRecording;
    private String mMovieName;

    private Runnable progressRunnable = null;
    private Handler handler = null;
    private ProgressView progressView;

    public static final int MAX_TIME = 7000;
    private boolean isMax = false;
    private long videoStartTime;
    private int totalTime = 0;
    private boolean isStart = false;
    private boolean isFinish = false;

    public CameraFragment() {
        // need default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        Effectstype effect = Effectstype.SlideBottom;
        dialogBuilder
                .withTitle("Gocciカメラ")
                .withMessage("このカメラはボタンをタップしている間だけ再生されます。縦向きで投稿するようにしてください！")
                .withDuration(500)                                          //def
                .withEffect(effect)
                .isCancelableOnTouchOutside(true)
                .show();

        mCameraView = (CameraGLView) rootView.findViewById(R.id.cameraView);
        mCameraView.setVideoSize(1280, 720);
        mCameraView.setOnTouchListener(mOnTouchListener);
        progressView = (ProgressView) rootView.findViewById(R.id.progress);
        //mRecordButton.setOnTouchListener(mOnTouchListener);
        //progress = (CircleProgressBar) rootView.findViewById(R.id.circleProgress);
        stopButton = (ImageButton) rootView.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int total = ((ViewGroup) progressView.getParent())
                        .getMeasuredWidth();
                if (msg.arg1 < 7000) {

                } else {
                    if (!isFinish) {
                        Log.e("終了ログ", String.valueOf(msg.arg1));
                        progressView.setBackgroundResource(R.color.gocci_progress);
                        isFinish = true;
                    }
                    //Toast.makeText(getActivity(), "finishRecord", Toast.LENGTH_SHORT).show();
                }
                double length = msg.arg1 * 1.0 / 7000 * total;
                progressView.setWidth((int) length);
                progressView.invalidate();
                super.handleMessage(msg);
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) Log.v(TAG, "onStart:");
        if (!mIsRecording) {
            startRecording();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        //stopRecording();
        mCameraView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.v(TAG, "onDestroy:");
        handler.removeCallbacks(progressRunnable);
    }

    /*
     *
     */
    public final void fixedScreenOrientation(final boolean fixed) {
        getActivity().setRequestedOrientation(
                fixed ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * method when touch record button
     */
    /*
    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.record_button:
                    if (!mIsRecording) {
                        startRecording();
                    } else {
                        stopRecording();
                    }
                    break;
            }
        }
    };
    */

    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mIsRecording) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        resumeRecording();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        pauseRecording();
                        break;
                }
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * start recording
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because preparing
     * of encoder may be heavy work on some devices
     */
    private void startRecording() {
        if (mIsRecording) return;
        if (isMax) return;

        if (DEBUG) Log.v(TAG, "start:");
        try {
            //mRecordButton.setColorFilter(0xffffff00);    // turn yellow
            mMovieName = TAG + System.nanoTime();
            if (true) {
                // for video capturing
                mVideoEncoder = new TLMediaVideoEncoder(getActivity(), mMovieName, mMediaEncoderListener);
                try {
                    mVideoEncoder.setFormat(mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
                    mVideoEncoder.prepare();
                } catch (Exception e) {
                    Log.e(TAG, "startRecording:", e);
                    mVideoEncoder.release();
                    mVideoEncoder = null;
                    throw e;
                }
            }
            if (true) {
                // for audio capturing
                mAudioEncoder = new TLMediaAudioEncoder(getActivity(), mMovieName, mMediaEncoderListener);
                try {
                    mAudioEncoder.prepare();
                } catch (Exception e) {
                    Log.e(TAG, "startRecording:", e);
                    mAudioEncoder.release();
                    mAudioEncoder = null;
                    throw e;
                }
            }
            if (mVideoEncoder != null) {
                mVideoEncoder.start(true);
            }
            if (mAudioEncoder != null) {
                mAudioEncoder.start(true);
            }
            mIsRecording = true;

        } catch (Exception e) {
            //mRecordButton.setColorFilter(0);
            Log.e(TAG, "startCapture:", e);
        }
        fixedScreenOrientation(mIsRecording);
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (!mIsRecording) return;
        isMax = false;
        totalTime = 0;
        isStart = false;
        handler.removeCallbacks(progressRunnable);

        if (DEBUG) Log.v(TAG, "stop");
        mIsRecording = false;
        //mRecordButton.setColorFilter(0);    // return to default color
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
        }
        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
        }
        fixedScreenOrientation(mIsRecording);
        try {
            mMuxer = new TLMediaMovieBuilder(getActivity(), mMovieName);
            mMuxer.build(mTLMediaMovieBuilderCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * resume recording
     */
    private void resumeRecording() {
        if (!mIsRecording) return;
        if (isMax) return;

        //mRecordButton.setColorFilter(0xffff0000);    // turn red
        try {
            if (mVideoEncoder != null) {
                if (mVideoEncoder.isPaused())
                    mVideoEncoder.resume();
            }
            if (mAudioEncoder != null) {
                if (mAudioEncoder.isPaused())
                    mAudioEncoder.resume();
            }
            isStart = true;
            videoStartTime = new Date().getTime();
        } catch (IOException e) {
            stopRecording();
        }
    }

    /**
     * pause recording
     */
    private void pauseRecording() {
        if (!mIsRecording) return;
        if (!isMax) {
            totalTime += new Date().getTime() - videoStartTime;
            videoStartTime = 0;
        }
        isStart = false;

        //mRecordButton.setColorFilter(0xffffff00);    // turn yellow
        if ((mVideoEncoder != null) && !mVideoEncoder.isPaused())
            try {
                mVideoEncoder.pause();
            } catch (Exception e) {
                Log.e(TAG, "pauseRecording:", e);
                mVideoEncoder.release();
                mVideoEncoder = null;
            }
        if ((mAudioEncoder != null) && !mAudioEncoder.isPaused())
            try {
                mAudioEncoder.pause();
            } catch (Exception e) {
                Log.e(TAG, "pauseRecording:", e);
                mAudioEncoder.release();
                mAudioEncoder = null;
            }
    }

    /**
     * callback methods from encoder
     */
    private final TLMediaEncoder.MediaEncoderListener mMediaEncoderListener
            = new TLMediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
        }

        @Override
        public void onStopped(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
        }

        @Override
        public void onResume(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onResume:encoder=" + encoder);
            if (encoder instanceof TLMediaVideoEncoder)
                mCameraView.setVideoEncoder((TLMediaVideoEncoder) encoder);
        }

        @Override
        public void onPause(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPause:encoder=" + encoder);
            if (encoder instanceof TLMediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };

    /**
     * callback methods from TLMediaMovieBuilder
     */
    private TLMediaMovieBuilder.TLMediaMovieBuilderCallback mTLMediaMovieBuilderCallback
            = new TLMediaMovieBuilder.TLMediaMovieBuilderCallback() {

        @Override
        public void onFinished(String output_path) {
            if (DEBUG) Log.v(TAG, "onFinished:");
            mMuxer = null;
            if (!TextUtils.isEmpty(output_path)) {
                final Activity activity = CameraFragment.this.getActivity();
                if ((activity == null) || activity.isFinishing()) return;
                // add movie to gallery
                MediaScannerConnection.scanFile(activity, new String[]{output_path}, null, null);
            }
        }

        @Override
        public void onError(Exception e) {
            if (DEBUG) Log.v(TAG, "onError:" + e.getMessage());
        }
    };

    private class ProgressRunnable implements Runnable {

        @Override
        public void run() {
            int time = 0;
            time = checkIfMax(new Date().getTime());
            Message message = new Message();
            message.arg1 = time;
            handler.sendMessage(message);
            // System.out.println(time);
            handler.postDelayed(this, 10);
        }
    }

    public int checkIfMax(long timeNow) {
        int during = 0;
        if (isStart) {
            during = (int) (totalTime + (timeNow - videoStartTime));
            if (during >= MAX_TIME) {
                during = MAX_TIME;
                isMax = true;
            }
        } else {
            during = totalTime;
            if (during >= MAX_TIME) {
                during = MAX_TIME;
            }
        }
        return during;
    }

    class stopRecordingHandler implements Runnable {
        public void run() {
            stopRecording();
        }
    }
}

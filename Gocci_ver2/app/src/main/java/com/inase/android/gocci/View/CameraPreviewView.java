package com.inase.android.gocci.View;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.inase.android.gocci.Activity.CameraActivity;
import com.inase.android.gocci.Activity.GocciTimelineActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;

public class CameraPreviewView extends BlurDialogFragment {

    private RippleView toukouButton;

    private SquareVideoView previewVideo;

    private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";
    private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";
    private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";
    private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";
    private static final String BUNDLE_KEY_BLURRED_ACTION_BAR = "bundle_key_blurred_action_bar";
    private static final String BUNDLE_KEY_ISEXIST = "bundle_key_isExist";
    private static final String BUNDLE_KEY_LATITUDE = "bundle_key_latitude";
    private static final String BUNDLE_KEY_LONGITUDE = "bundle_key_longitude";
    private static final String BUNDLE_KEY_RESTNAME = "restname";
    private static final String BUNDLE_KEY_VIDEOPATH = "videopath";

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;
    private boolean mBlurredActionBar;

    private String mRestname;
    private boolean isNormal;
    private double mLatitude;
    private double mLongitude;

    private String mVideoPath = null;

    private File mVideoFile;

    private AsyncHttpClient httpClient;
    private RequestParams loginParam;
    private RequestParams toukouParam;

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sMovieurl = "http://api-gocci.jp/android_movie/";
    private ProgressWheel mPostProgress;

    private Application_Gocci gocci;

    public static CameraPreviewView newInstance(int radius,
                                                float downScaleFactor,
                                                boolean dimming,
                                                boolean debug,
                                                boolean mBlurredActionBar,
                                                String restname,
                                                boolean isNormal,
                                                double latitude,
                                                double longitude,
                                                String videoPath
    ) {
        CameraPreviewView fragment = new CameraPreviewView();
        Bundle args = new Bundle();
        args.putInt(
                BUNDLE_KEY_BLUR_RADIUS,
                radius
        );
        args.putFloat(
                BUNDLE_KEY_DOWN_SCALE_FACTOR,
                downScaleFactor
        );
        args.putBoolean(
                BUNDLE_KEY_DIMMING,
                dimming
        );
        args.putBoolean(
                BUNDLE_KEY_DEBUG,
                debug
        );
        args.putBoolean(
                BUNDLE_KEY_BLURRED_ACTION_BAR,
                mBlurredActionBar
        );
        args.putString(
                BUNDLE_KEY_RESTNAME,
                restname
        );
        args.putBoolean(
                BUNDLE_KEY_ISEXIST,
                isNormal
        );
        args.putDouble(
                BUNDLE_KEY_LATITUDE,
                latitude
        );
        args.putDouble(
                BUNDLE_KEY_LONGITUDE,
                longitude
        );
        args.putString(
                BUNDLE_KEY_VIDEOPATH,
                videoPath
        );

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        mBlurredActionBar = args.getBoolean(BUNDLE_KEY_BLURRED_ACTION_BAR);
        mRestname = args.getString(BUNDLE_KEY_RESTNAME);
        isNormal = args.getBoolean(BUNDLE_KEY_ISEXIST);
        mLatitude = args.getDouble(BUNDLE_KEY_LATITUDE);
        mLongitude = args.getDouble(BUNDLE_KEY_LONGITUDE);
        mVideoPath = args.getString(BUNDLE_KEY_VIDEOPATH);
        mVideoFile = new File(mVideoPath);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gocci = (Application_Gocci) getActivity().getApplication();
        loginParam = new RequestParams();
        loginParam.put("user_name",gocci.getLoginName());
        loginParam.put("picture", gocci.getLoginPicture());

        toukouParam = new RequestParams();
        toukouParam.put("restname", mRestname);

        try {
            toukouParam.put("movie", mVideoFile);
            Log.e("入れたファイル", String.valueOf(mVideoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("失敗ファイル", String.valueOf(mVideoFile));
        }

        if (!isNormal) {
            //店舗情報あり
            toukouParam.put("latitude", mLatitude);
            toukouParam.put("longitude", mLongitude);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_camera_preview, null);

        toukouButton = (RippleView) view.findViewById(R.id.toukou_button_Ripple);
        mPostProgress = (ProgressWheel) view.findViewById(R.id.cameraprogress_wheel);
        previewVideo = (SquareVideoView) view.findViewById(R.id.previewVideoView);

        previewVideo.setVideoPath(mVideoPath);

        previewVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

            }
        });

        toukouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSignupAsync(getActivity());
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return mBlurredActionBar;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }

    private void postSignupAsync(final Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mPostProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postRestAsync(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mPostProgress.setVisibility(View.GONE);
                Toast.makeText(context, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postRestAsync(final Context context) {
        Log.e("中身は！？！？", String.valueOf(toukouParam));
        httpClient.post(context, sMovieurl, toukouParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, "投稿が完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "投稿に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mPostProgress.setVisibility(View.GONE);
                //成功しても失敗してもホーム画面に戻る。
                Intent intent = new Intent(context, GocciTimelineActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}

package com.inase.android.gocci.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Activity.CameraPreviewActivity;
import com.inase.android.gocci.Activity.GocciCameraActivity;
import com.inase.android.gocci.Base.CircleProgressBar;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class up18CameraFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private static final boolean DEBUG = true;    // TODO set false on releasing
    private static final String TAG = "GocciCamera";

    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;

    private ImageButton toukouButton;
    private Button cancelButton;
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
    private ProgressWheel cameraProgress;
    private CircleProgressBar progress;

    private String mRest_name;
    private String mFinalVideoUrl;
    private String mValue;
    private String mCategory;
    private String mAtmosphere;
    private String mComment;
    private boolean mIsnewRestname = false;

    private double latitude;
    private double longitude;
    private String mSearch_mapUrl;

    private static final String TAG_REST_NAME = "restname";

    private boolean onScroll = false;

    private String timeStamp;

    private boolean isPlaying = false;
    private boolean isFirst = true;

    private ViewPagerItemAdapter adapter;

    public static final int MAX_TIME = 7000;
    private boolean isMax = false;
    private long videoStartTime;
    private int totalTime = 0;
    private boolean isStart = false;
    private boolean isFinish = false;

    private MaterialBetterSpinner restname_spinner;
    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner mood_spinner;
    private MaterialEditText edit_value;
    private MaterialEditText edit_comment;
    private ImageButton restaddButton;

    public up18CameraFragment() {
        // need default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_up18, container, false);
        isFirst = true;

        getLocation(getActivity());

        for (int i = 0; i < 30; i++) {
            GocciCameraActivity.restname[i] = "";
        }

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
        //mCameraView.setVideoSize(1280, 720);
        mCameraView.setVideoSize(640, 480);
        //mCameraView.setOnTouchListener(mOnTouchListener);
        cameraProgress = (ProgressWheel) rootView.findViewById(R.id.cameraprogress_wheel);
        //progress = (CircleProgressBar) rootView.findViewById(R.id.circleProgress);
        //mRecordButton.setOnTouchListener(mOnTouchListener);
        //progress = (CircleProgressBar) rootView.findViewById(R.id.circleProgress);
        //recordButton = (ImageButton) rootView.findViewById(R.id.toukouButton);
        //recordButton.setOnTouchListener(mOnTouchListener);
        cancelButton = (Button) rootView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("確認")
                        .content("この動画は初期化されますがよろしいですか？")
                        .positiveText("戻る")
                        .negativeText("いいえ")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                isPlaying = false;
                                getActivity().finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                            }
                        }).show();
            }
        });
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        SmartTabLayout viewPagerTab = (SmartTabLayout) rootView.findViewById(R.id.viewpagertab);

        adapter = new ViewPagerItemAdapter(ViewPagerItems.with(getActivity())
                .add(R.string.lat, R.layout.view_camera_1)
                .add(R.string.lon, R.layout.view_camera_2)
                .create());

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.arg1 < 7000) {

                } else {
                    cameraProgress.setVisibility(View.VISIBLE);
                    //onFinishPressed();
                    if (!isFinish) {
                        Log.e("終了ログ", String.valueOf(msg.arg1));
                        progress.setProgress(100);
                        pauseRecording();
                        stopRecording();
                        isFinish = true;
                    }
                    //Toast.makeText(getActivity(), "finishRecord", Toast.LENGTH_SHORT).show();
                }
                int circle = (int) (msg.arg1 * 1.0 / 70);

                if (circle > 50) {
                    progress.setProgress(circle + 1);
                } else {
                    progress.setProgress(circle);
                }
                //progress.invalidate();
                super.handleMessage(msg);
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);
        return rootView;
    }

    private void getLocation(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mSearch_mapUrl = "http://api-gocci.jp/dist/?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&limit=30";
                    getTenpoJson(context, mSearch_mapUrl);
                } else {
                    Log.e("からでしたー", "locationupdated");
                }
            }
        });
    }

    private void getTenpoJson(final Context context, String url) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        final String rest_name = jsonObject.getString(TAG_REST_NAME);

                        GocciCameraActivity.restname[i] = rest_name;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void startPlay() {
        //recorderManager.reset();

        //ここで記入済みの値を持って行こう
        if (onScroll) {
            if (restname_spinner.getText().length() == 0) {
                mRest_name = "";
            } else {
                mRest_name = restname_spinner.getText().toString();
            }
            if (category_spinner.getText().length() == 0) {
                mCategory = "";
            } else {
                mCategory = category_spinner.getText().toString();
            }
            if (mood_spinner.getText().length() == 0) {
                mAtmosphere = "";
            } else {
                mAtmosphere = mood_spinner.getText().toString();
            }
            if (edit_value.getText().length() == 0) {
                mValue = "";
            } else {
                mValue = edit_value.getText().toString();
            }
            if (edit_comment.getText().length() == 0) {
                mComment = "";
            } else {
                mComment = edit_comment.getText().toString();
            }
        } else {
            mRest_name = "";
            mCategory = "";
            mAtmosphere = "";
            mValue = "";
            mComment = "";
        }

        Intent intent = new Intent(getActivity(), CameraPreviewActivity.class);
        intent.putExtra("restname", mRest_name);
        intent.putExtra("video_url", mFinalVideoUrl);
        intent.putExtra("category", mCategory);
        intent.putExtra("mood", mAtmosphere);
        intent.putExtra("comment", mComment);
        intent.putExtra("value", mValue);
        intent.putExtra("isNewRestname", mIsnewRestname);
        intent.putExtra("lat", latitude);
        intent.putExtra("lon", longitude);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

        //cameraProgress.setVisibility(View.GONE);
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
        mCameraView.onFinish();
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
                final Activity activity = up18CameraFragment.this.getActivity();
                if ((activity == null) || activity.isFinishing()) return;
                // add movie to gallery
                MediaScannerConnection.scanFile(activity, new String[]{output_path}, null, null);

                mCameraView.onFinish();
                mFinalVideoUrl = output_path;
                startPlay();
                //画面遷移するときにやる
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isFirst && position == 0) {
            View page = adapter.getPage(position);
            toukouButton = (ImageButton) page.findViewById(R.id.toukouButton);
            progress = (CircleProgressBar) page.findViewById(R.id.circleProgress);

            toukouButton.setOnTouchListener(mOnTouchListener);

            isFirst = false;
        }
    }

    @Override
    public void onPageSelected(int position) {
        View page = adapter.getPage(position);

        switch (position) {
            case 0:

                break;
            case 1:
                onScroll = true;
                EventDateRecorder recorder = EventDateRecorder.load(getActivity(), "use_camera_tab");
                if (!recorder.didRecorded()) {
                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                    Effectstype effect = Effectstype.SlideBottom;
                    dialogBuilder
                            .withTitle("タグ画面")
                            .withMessage("店名以外にもタグを付けて投稿してみませんか？")
                            .withDuration(500)                                          //def
                            .withEffect(effect)
                            .isCancelableOnTouchOutside(true)
                            .show();
                    recorder.record();
                }

                restname_spinner = (MaterialBetterSpinner) page.findViewById(R.id.restname_spinner);
                category_spinner = (MaterialBetterSpinner) page.findViewById(R.id.category_spinner);
                mood_spinner = (MaterialBetterSpinner) page.findViewById(R.id.mood_spinner);
                edit_value = (MaterialEditText) page.findViewById(R.id.edit_value);
                edit_comment = (MaterialEditText) page.findViewById(R.id.edit_comment);

                restaddButton = (ImageButton) page.findViewById(R.id.restaddButton);

                restname_spinner.setIconRight(R.drawable.ic_arrow_drop_down_white_24dp);
                category_spinner.setIconRight(R.drawable.ic_arrow_drop_down_white_24dp);
                mood_spinner.setIconRight(R.drawable.ic_arrow_drop_down_white_24dp);

                String[] CATEGORY = getResources().getStringArray(R.array.list_category);
                String[] MOOD = getResources().getStringArray(R.array.list_mood);

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, CATEGORY);
                category_spinner.setAdapter(categoryAdapter);

                ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, MOOD);
                mood_spinner.setAdapter(moodAdapter);

                ArrayAdapter<String> restAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, GocciCameraActivity.restname);
                restname_spinner.setAdapter(restAdapter);

                restaddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //店舗追加のダイアログ
                        createTenpo();
                    }
                });

                break;
        }

    }

    private void createTenpo() {
        new MaterialDialog.Builder(getActivity())
                .title("店舗追加")
                .content("あなたのいるお店の名前を入力してください。※位置情報は現在の位置を使います。")
                .input("店舗名", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText("送信する")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mRest_name = dialog.getInputEditText().getText().toString();

                        RequestParams params = new RequestParams();
                        params.put("restname", mRest_name);
                        params.put("lat", latitude);
                        params.put("lon", longitude);

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(getActivity(), Const.URL_INSERT_REST, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                cameraProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.e("ジェイソン成功", String.valueOf(response));

                                try {
                                    String message = response.getString("message");

                                    if (message.equals("店舗追加完了しました")) {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        //店名をセット
                                        mIsnewRestname = true;
                                        restname_spinner.setText(mRest_name);
                                    } else {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFinish() {
                                cameraProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (ViewPager.SCROLL_STATE_DRAGGING == state) {
            //ボタンクリックできないように
            toukouButton.setClickable(false);
        } else {
            toukouButton.setClickable(true);
        }

    }
}

package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.ui.view.SquareVideoView;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ReUploadActivity extends AppCompatActivity {

    @Bind(R.id.tool_bar)
    Toolbar toolBar;
    @Bind(R.id.video)
    SquareVideoView video;
    @Bind(R.id.memo)
    EditText memo;
    @Bind(R.id.restnameEdit)
    MaterialEditText restnameEdit;
    @Bind(R.id.areaEdit)
    MaterialEditText areaEdit;
    @Bind(R.id.category_spinner)
    MaterialBetterSpinner categorySpinner;
    @Bind(R.id.edit_value)
    MaterialEditText editValue;
    @Bind(R.id.feedback)
    EditText feedback;
    @Bind(R.id.overlay)
    View mOverlay;
    @Bind(R.id.progress_wheel)
    CircleProgressView mProgressWheel;
    @Bind(R.id.toukou_button_ripple)
    RippleView mToukouButtonRipple;
    @Bind(R.id.videoText)
    TextView mVideoText;
    @Bind(R.id.videoFrameView)
    View mVideoFrameView;
    @Bind(R.id.videoFrame)
    FrameLayout mVideoFrame;

    private int mCategory_id = 1;
    private String mRestname = "";
    private String mArea = "";
    private int mCheer_flag = 0;
    private String mAwsPostName;
    private String mValue = "";
    private String mMemo = "";
    private String mFeedback = "";

    private File mVideoFile = null;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private boolean isMax = false;

    public static void startReUploadActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, ReUploadActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_upload);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle(getString(R.string.setting_support_reupload));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressWheel.setValue(0);
        mProgressWheel.setBarColor(getResources().getColor(R.color.gocci_1), getResources().getColor(R.color.gocci_2), getResources().getColor(R.color.gocci_3), getResources().getColor(R.color.gocci_4));
        mProgressWheel.setOnAnimationStateChangedListener(new AnimationStateChangedListener() {
            @Override
            public void onAnimationStateChanged(AnimationState _animationState) {
                if (_animationState == AnimationState.IDLE && isMax) {
                    mProgressWheel.setVisibility(View.INVISIBLE);
                    Toast.makeText(ReUploadActivity.this, "お問い合わせを受け付けました！早急に対応させていただきます！", Toast.LENGTH_LONG).show();

                    if (!mFeedback.isEmpty()) {
                        API3PostUtil.setFeedbackAsync(ReUploadActivity.this, mFeedback);
                    }

                    Intent intent = new Intent(ReUploadActivity.this, TimelineActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mProgressWheel.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                if (value == 100.0) {
                    isMax = true;
                }
            }
        });

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategory_id = position + 2;
            }
        });

        mToukouButtonRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.getConnectedState(ReUploadActivity.this) != Util.NetworkStatus.OFF) {
                    if (mVideoFile != null) {
                        if (restnameEdit.getText().length() != 0) {
                            mRestname = restnameEdit.getText().toString();
                        } else {
                            Toast.makeText(ReUploadActivity.this, "店名を入力してください", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (areaEdit.getText().length() != 0) {
                            mArea = areaEdit.getText().toString();
                        } else {
                            Toast.makeText(ReUploadActivity.this, "だいたいの場所を入力してください", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (editValue.getText().length() != 0) {
                            mValue = editValue.getText().toString();
                        }
                        if (memo.getText().length() != 0) {
                            mMemo = memo.getText().toString();
                        }
                        if (feedback.getText().length() != 0) {
                            mFeedback = feedback.getText().toString();
                        }
                        mAwsPostName = Util.getDateTimeString() + "_" + SavedData.getServerUserId(ReUploadActivity.this);
                        API3PostUtil.setPostCrashAsync(ReUploadActivity.this, Const.ActivityCategory.REUPLOAD, mAwsPostName, mRestname, mArea, mCategory_id, mValue, mMemo, mFeedback, mCheer_flag);
                    } else {
                        Toast.makeText(ReUploadActivity.this, "再投稿する動画を選んでください", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReUploadActivity.this, getString(R.string.bad_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mVideoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, 1001);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:
                if (resultCode == RESULT_OK) {
                    mVideoFrameView.setVisibility(View.INVISIBLE);
                    mVideoText.setVisibility(View.INVISIBLE);

                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(data.getData(), new String[] { MediaStore.MediaColumns.DATA }, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        String path = cursor.getString(0);
                        mVideoFile = new File(path);
                        cursor.close();
                    }

                    video.setVisibility(View.VISIBLE);
                    video.setVideoURI(data.getData());
                    video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            mp.setLooping(true);
                        }
                    });
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        BusHolder.get().unregister(this);
        if (video != null) {
            if (video.isPlaying()) {
                video.pause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("ReUpload");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(this);
        if (video != null) {
            if (!video.isPlaying()) {
                video.start();
            }
        }
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.REUPLOAD) {
            if (event.apiCategory == Const.APICategory.SET_POST_CRASH) {
                switch (event.callback) {
                    case SUCCESS:
                        mProgressWheel.setVisibility(View.VISIBLE);
                        mOverlay.setVisibility(View.VISIBLE);
                        Application_Gocci.postingVideoToS3(ReUploadActivity.this, mAwsPostName, mVideoFile, mProgressWheel, Const.ActivityCategory.REUPLOAD);
                        break;
                    case LOCALERROR:
                    case GLOBALERROR:
                        mProgressWheel.setVisibility(View.INVISIBLE);
                        mOverlay.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, getString(R.string.videoposting_failure), Toast.LENGTH_LONG).show();
                        if (!event.id.isEmpty()) {
                            API3PostUtil.setFeedbackAsync(this, event.id);
                        }
                        break;
                }
            }
        }
    }
}

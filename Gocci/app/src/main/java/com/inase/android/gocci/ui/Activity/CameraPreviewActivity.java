package com.inase.android.gocci.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.datasource.repository.NearRepository;
import com.inase.android.gocci.datasource.repository.NearRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.usecase.NearDataUseCase;
import com.inase.android.gocci.domain.usecase.NearDataUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.presenter.ShowCameraPresenter;
import com.inase.android.gocci.ui.view.SquareVideoView;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.TwitterUtil;
import com.inase.android.gocci.utils.Util;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class CameraPreviewActivity extends AppCompatActivity implements ShowCameraPresenter.ShowCameraView {

    @Bind(R.id.preview_video)
    SquareVideoView mPreviewVideo;
    @Bind(R.id.restname_spinner)
    MaterialBetterSpinner mRestnameSpinner;
    @Bind(R.id.category_spinner)
    MaterialBetterSpinner mCategorySpinner;
    @Bind(R.id.add_rest_text)
    TextView mAddRestText;
    @Bind(R.id.edit_value)
    MaterialEditText mEditValue;
    @Bind(R.id.edit_comment)
    MaterialEditText mEditComment;
    @Bind(R.id.check_cheer)
    CheckBox mCheckCheer;
    @Bind(R.id.toukou_button_ripple)
    RippleView mToukouButtonRipple;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;
    @Bind(R.id.check_twitter)
    CheckBox mCheckTwitter;
    @Bind(R.id.check_facebook)
    CheckBox mCheckFacebook;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;
    @Bind(R.id.preview_view)
    ScrollView mPreviewView;

    @OnClick(R.id.add_rest_text)
    public void restAdd() {
        createTenpo();
    }

    @OnClick(R.id.edit_twitter)
    public void edit_twitter() {

    }

    @OnClick(R.id.edit_facebook)
    public void edit_facebook() {

    }

    @OnClick(R.id.edit_instagram)
    public void edit_instagram() {

    }

    @OnClick(R.id.check_instagram)
    public void instagram() {
        Uri uri = Uri.fromFile(mVideoFile);
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        share.setType("video/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.instagram.android");
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private String mRest_id;
    private int mCategory_id;
    private int mCheer_flag = 0;
    private String mRestname;
    private String mVideoUrl;
    private String mAwsPostName;
    private String mValue;
    private String mMemo;
    private boolean mIsnewRestname;
    private double mLatitude;
    private double mLongitude;

    private File mVideoFile;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private static MobileAnalyticsManager analytics;

    private ArrayAdapter<String> restAdapter;

    private ShowCameraPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    Const.ANALYTICS_ID, //Amazon Mobile Analytics App ID
                    Const.IDENTITY_POOL_ID //Amazon Cognito Identity Pool ID
            );
        } catch (InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }

        final API3 api3Impl = API3.Impl.getRepository();
        NearRepository nearRepositoryImpl = NearRepositoryImpl.getRepository(api3Impl);
        NearDataUseCase neardataUseCaseImpl = NearDataUseCaseImpl.getUseCase(nearRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowCameraPresenter(neardataUseCaseImpl);
        mPresenter.setCameraView(this);

        setContentView(R.layout.activity_camera_preview);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        mSlidingLayout.setAnchorPoint(0.7f);

        Intent intent = getIntent();
        mRestname = intent.getStringExtra("restname");
        mRest_id = intent.getStringExtra("rest_id");
        mVideoUrl = intent.getStringExtra("video_url");
        mAwsPostName = intent.getStringExtra("aws") + "_" + SavedData.getServerUserId(this);
        mCategory_id = intent.getIntExtra("category_id", 1);
        mMemo = intent.getStringExtra("memo");
        mValue = intent.getStringExtra("value");
        mIsnewRestname = intent.getBooleanExtra("isNewRestname", false);
        mLatitude = intent.getDoubleExtra("lat", 0.0);
        mLongitude = intent.getDoubleExtra("lon", 0.0);

        SavedData.setPostVideoPreview(this, mRestname, mRest_id, mVideoUrl, mAwsPostName, mCategory_id, mMemo, mValue, mIsnewRestname, mLongitude, mLatitude);

        if (mLatitude == 0.0 && mLongitude == 0.0) {
            //もう一度位置取る？
            SmartLocation.with(this).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    API3.Util.GetNearLocalCode localCode = API3.Impl.getRepository().get_near_parameter_regex(mLongitude, mLatitude);
                    if (localCode == null) {
                        mPresenter.getNearData(Const.APICategory.GET_NEAR_FIRST, API3.Util.getGetNearAPI(mLongitude, mLatitude));
                    } else {
                        Toast.makeText(CameraPreviewActivity.this, API3.Util.getNearLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                    SavedData.setLat(CameraPreviewActivity.this, mLatitude);
                    SavedData.setLon(CameraPreviewActivity.this, mLongitude);
                }
            });
        }

        mVideoFile = new File(mVideoUrl);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(CameraPreviewActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(CameraPreviewActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(CameraPreviewActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        if (mIsnewRestname || !mRestname.equals("")) {
            mAddRestText.setVisibility(View.GONE);
        }

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        mCategorySpinner.setAdapter(categoryAdapter);
        mCategorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategory_id = position + 2;
                SavedData.setCategory_id(CameraPreviewActivity.this, mCategory_id);
            }
        });

        restAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CameraActivity.rest_nameArray);
        mRestnameSpinner.setAdapter(restAdapter);
        mRestnameSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRest_id = CameraActivity.rest_idArray.get(position);
                mRestname = CameraActivity.rest_nameArray.get(position);
                SavedData.setRest_id(CameraPreviewActivity.this, mRest_id);
                SavedData.setRestname(CameraPreviewActivity.this, mRestname);
            }
        });

        mRestnameSpinner.setText(mRestname);
        mCategorySpinner.setText(mCategory_id == 1 ? "" : CATEGORY[mCategory_id - 2]);
        mEditValue.setText(mValue);
        mEditComment.setText(mMemo);

        mPreviewVideo.setVideoPath(mVideoUrl);
        mPreviewVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });

        mToukouButtonRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (Util.getConnectedState(CameraPreviewActivity.this) != Util.NetworkStatus.OFF) {
                    if (!mRest_id.equals("1")) {
                        if (mEditValue.getText().length() != 0) {
                            mValue = mEditValue.getText().toString();
                        } else {
                            mValue = "0";
                        }
                        SavedData.setValue(CameraPreviewActivity.this, mValue);
                        if (mEditComment.getText().length() != 0) {
                            mMemo = mEditComment.getText().toString();
                        } else {
                            mMemo = "none";
                        }
                        SavedData.setMemo(CameraPreviewActivity.this, mMemo);
                        if (mCheckCheer.isChecked()) {
                            mCheer_flag = 1;
                        }
                        if (mCheckTwitter.isChecked()) {
                            TwitterSession session =
                                    Twitter.getSessionManager().getActiveSession();
                            if (session != null) {
                                if (mVideoFile.length() < 1024 * 1024 * 15) {
                                    try {
                                        final TwitterAuthToken authToken = session.getAuthToken();
                                        TwitterUtil.performShare(CameraPreviewActivity.this, authToken.token, authToken.secret, mVideoFile, mMemo + " #Gocci", new TwitterUtil.TwitterShareCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), "シェアしました", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(String message) {
                                                Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), "エラー:" + message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        mProgressWheel.setVisibility(View.VISIBLE);
                        API3PostUtil.postMovieAsync(CameraPreviewActivity.this, Const.ActivityCategory.CAMERA_PREVIEW, mRest_id, mAwsPostName, mCategory_id, mValue, mMemo, mCheer_flag);
                        Application_Gocci.postingVideoToS3(CameraPreviewActivity.this, mAwsPostName, mVideoFile);
                    } else {
                        Toast.makeText(CameraPreviewActivity.this, getString(R.string.please_input_restname), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CameraPreviewActivity.this, getString(R.string.bad_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }

        BusHolder.get().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }

        BusHolder.get().register(this);
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.CAMERA_PREVIEW) {
            if (event.apiCategory == Const.APICategory.POST_RESTADD) {
                mIsnewRestname = true;
                mRestnameSpinner.setText(mRestname);
                mRest_id = event.id;
                mRestnameSpinner.setClickable(false);
                SavedData.setIsNewRestname(CameraPreviewActivity.this, mIsnewRestname);
                SavedData.setRestname(CameraPreviewActivity.this, mRestname);
                SavedData.setRest_id(CameraPreviewActivity.this, mRest_id);
            } else if (event.apiCategory == Const.APICategory.POST_POST) {
                mProgressWheel.setVisibility(View.GONE);
                switch (event.callback) {
                    case SUCCESS:
                        SharedPreferences prefs = getSharedPreferences("movie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(this, TimelineActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingLayout != null &&
                (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            new MaterialDialog.Builder(this)
                    .content(getString(R.string.check_videoposting_cancel))
                    .contentColorRes(R.color.nameblack)
                    .positiveText(getString(R.string.check_videoposting_yeah))
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(getString(R.string.check_videoposting_no))
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            CameraPreviewActivity.this.finish();
                        }
                    }).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SavedData.setPostVideoPreview(this, mRestname, mRest_id, mVideoUrl, mAwsPostName, mCategory_id, mMemo, mValue, mIsnewRestname,
                mLongitude, mLatitude);
    }

    private void createTenpo() {
        new MaterialDialog.Builder(CameraPreviewActivity.this)
                .content(getString(R.string.add_restname))
                .contentColorRes(R.color.nameblack)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.nameblack)
                .positiveText("送信")
                .positiveColorRes(R.color.gocci_header)
                .input("", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        mRestname = charSequence.toString();
                        API3PostUtil.postRestAddAsync(CameraPreviewActivity.this, Const.ActivityCategory.CAMERA_PREVIEW, mRestname, mLongitude, mLatitude);
                    }
                }).show();
    }

    @Override
    public void showNoResultCase(Const.APICategory api) {

    }

    @Override
    public void hideNoResultCase() {

    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray) {
        CameraActivity.restname = restnames;
        CameraActivity.rest_nameArray.addAll(restnameArray);
        CameraActivity.rest_idArray.addAll(restIdArray);
        restAdapter.addAll(CameraActivity.restname);
    }
}

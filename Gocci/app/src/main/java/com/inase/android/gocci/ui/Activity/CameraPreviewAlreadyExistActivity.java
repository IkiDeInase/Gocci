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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowCameraPresenter;
import com.inase.android.gocci.ui.view.GocciTwitterLoginButton;
import com.inase.android.gocci.ui.view.SquareVideoView;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.share.FacebookUtil;
import com.inase.android.gocci.utils.share.TwitterUtil;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.ArrayList;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class CameraPreviewAlreadyExistActivity extends AppCompatActivity implements ShowCameraPresenter.ShowCameraView {

    @Bind(R.id.preview_video)
    SquareVideoView mPreviewVideo;
    @Bind(R.id.restname_spinner)
    MaterialBetterSpinner mRestnameSpinner;
    @Bind(R.id.category_spinner)
    MaterialBetterSpinner mCategorySpinner;
    @Bind(R.id.edit_value)
    MaterialEditText mEditValue;
    @Bind(R.id.edit_comment)
    MaterialEditText mEditComment;
    @Bind(R.id.check_cheer)
    CheckBox mCheckCheer;
    @Bind(R.id.toukou_button_ripple)
    RippleView mToukouButtonRipple;
    @Bind(R.id.progress_wheel)
    CircleProgressView mProgressWheel;
    @Bind(R.id.add_rest_text)
    TextView mAddRestText;
    @Bind(R.id.check_twitter)
    CheckBox mCheckTwitter;
    @Bind(R.id.check_facebook)
    CheckBox mCheckFacebook;
    @Bind(R.id.check_instagram)
    CheckBox mCheckInstagram;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;
    @Bind(R.id.preview_view)
    ScrollView mPreviewView;
    @Bind(R.id.edit_twitter_fab)
    FloatingActionButton mTwitterEdit;
    @Bind(R.id.edit_facebook_fab)
    FloatingActionButton mFacebookEdit;
    @Bind(R.id.edit_instagram_fab)
    FloatingActionButton mInstagramEdit;
    @Bind(R.id.login_button)
    LoginButton mFacebookLoginButton;
    @Bind(R.id.twitter_login_button)
    GocciTwitterLoginButton mTwitterLoginButton;
    @Bind(R.id.overlay)
    View mOverlay;

    @OnClick(R.id.add_rest_text)
    public void restAdd() {
        createTenpo();
    }

    @OnClick(R.id.edit_twitter_fab)
    public void edit_twitter() {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.edit_twitter))
                .contentColorRes(R.color.nameblack)
                .contentGravity(GravityEnum.CENTER)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.twitter_background)
                .positiveText(getString(R.string.complete))
                .positiveColorRes(R.color.gocci_header)
                .inputRangeRes(6, 115, R.color.gocci_header)
                .input("", mTwitterMemo.isEmpty() ? getMessage() : mTwitterMemo, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        mTwitterMemo = charSequence.toString();
                    }
                }).show();
    }

    @OnClick(R.id.edit_facebook_fab)
    public void edit_facebook() {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.edit_facebook))
                .contentColorRes(R.color.nameblack)
                .contentGravity(GravityEnum.CENTER)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.facebook_background)
                .positiveText(getString(R.string.complete))
                .positiveColorRes(R.color.gocci_header)
                .input("", mFacebookMemo.isEmpty() ? "" : mFacebookMemo, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        mFacebookMemo = charSequence.toString();
                    }
                }).show();
    }

    @OnClick(R.id.edit_instagram_fab)
    public void edit_instagram() {

    }

    @OnClick(R.id.check_twitter)
    public void twitter() {
        if (mTwitterEdit.isHidden()) {
            TwitterSession session =
                    Twitter.getSessionManager().getActiveSession();
            if (session != null) {
                mTwitterEdit.show(true);
            } else {
                mTwitterLoginButton.performClick();
            }
        } else {
            mTwitterEdit.hide(true);
        }
    }

    @OnClick(R.id.check_facebook)
    public void facebook() {
        if (mFacebookEdit.isHidden()) {
            //Profile profile = Profile.getCurrentProfile();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                if (accessToken.getPermissions().contains("publish_actions")) {
                    mFacebookEdit.show(true);
                } else {
                    ArrayList<String> permissions = new ArrayList<>();
                    permissions.add("publish_actions");
                    LoginManager.getInstance().logInWithPublishPermissions(this, permissions);
                }
            } else {
                mFacebookLoginButton.performClick();
            }
        } else {
            mFacebookEdit.hide(true);
        }
//        if (mCheckFacebook.isChecked()) {
//            Profile profile = Profile.getCurrentProfile();
//            if (profile != null) {
//                Uri uri = Uri.fromFile(mVideoFile);
//                if (ShareDialog.canShow(ShareVideoContent.class)) {
//                    ShareVideo video = new ShareVideo.Builder()
//                            .setLocalUrl(uri)
//                            .build();
//                    ShareVideoContent content = new ShareVideoContent.Builder()
//                            .setVideo(video)
//                            .build();
//                    shareDialog.show(content);
//                } else {
//                    // ...sharing failed, handle error
//                    Toast.makeText(this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                mFacebookLoginButton.performClick();
//            }
//        }
    }

    @OnClick(R.id.check_instagram)
    public void instagram() {
        if (mCheckInstagram.isChecked()) {
            mTracker = applicationGocci.getDefaultTracker();
            mTracker.setScreenName("CameraPreviewAlready");
            mTracker.send(new HitBuilders.SocialBuilder().setNetwork("Instagram").setAction("Share").setTarget(mAwsPostName).build());
            Uri uri = Uri.fromFile(mVideoFile);
            Intent share = new Intent(Intent.ACTION_SEND);
            // Set the MIME type
            share.setType("video/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setPackage("com.instagram.android");
            startActivity(Intent.createChooser(share, "Share to"));
        }
    }

    private String mRest_id;
    private int mCategory_id;
    private int mCheer_flag = 0;
    private String mRestname;
    private String mVideoUrl;
    private String mAwsPostName;
    private String mValue;
    private String mMemo;
    private String mTwitterMemo = "";
    private String mFacebookMemo = "";
    private boolean mIsnewRestname;
    private String mLatitude;
    private String mLongitude;

    private boolean isPostApiFinished;

    private ArrayList<String> rest_nameList = new ArrayList<>();
    private ArrayList<String> rest_idList = new ArrayList<>();

    private File mVideoFile;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private ArrayAdapter<String> restAdapter;

    private ShowCameraPresenter mPresenter;

    private boolean isMax = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final API3 api3Impl = API3.Impl.getRepository();
        NearRepository nearRepositoryImpl = NearRepositoryImpl.getRepository(api3Impl);
        NearDataUseCase neardataUseCaseImpl = NearDataUseCaseImpl.getUseCase(nearRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowCameraPresenter(neardataUseCaseImpl);
        mPresenter.setCameraView(this);

        setContentView(R.layout.activity_camera_preview_already_exist);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        mTwitterEdit.hide(false);
        mFacebookEdit.hide(false);
        mInstagramEdit.hide(false);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        mSlidingLayout.setAnchorPoint(0.5f);

        mRest_id = SavedData.getRest_id(this);
        mRestname = SavedData.getRestname(this);
        mVideoUrl = SavedData.getVideoUrl(this);
        mAwsPostName = SavedData.getAwsPostname(this);
        mCategory_id = SavedData.getCategory_id(this);
        mMemo = SavedData.getMemo(this);
        mValue = SavedData.getValue(this);
        mIsnewRestname = SavedData.getIsNewRestname(this);
        mLatitude = SavedData.getLat(this);
        mLongitude = SavedData.getLon(this);

        isPostApiFinished = SavedData.getPostFinished(this);

        mVideoFile = new File(mVideoUrl);

        mProgressWheel.setValue(0);
        mProgressWheel.setBarColor(getResources().getColor(R.color.gocci_1), getResources().getColor(R.color.gocci_2), getResources().getColor(R.color.gocci_3), getResources().getColor(R.color.gocci_4));
        mProgressWheel.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                if (value == 100.0) {
                    isMax = true;
                }
            }
        });

        mProgressWheel.setOnAnimationStateChangedListener(new AnimationStateChangedListener() {
            @Override
            public void onAnimationStateChanged(AnimationState _animationState) {
                if (_animationState == AnimationState.IDLE && isMax) {
                    mProgressWheel.setVisibility(View.INVISIBLE);
                    Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.videoposting_message), Toast.LENGTH_LONG).show();

                    SharedPreferences prefs = getSharedPreferences("movie", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(CameraPreviewAlreadyExistActivity.this, TimelineActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mFacebookEdit.show(true);
                mCheckFacebook.setChecked(true);
                API3PostUtil.setSnsLinkAsync(CameraPreviewAlreadyExistActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.CAMERA_PREVIEW_ALREADY, Const.APICategory.SET_FACEBOOK_LINK);
            }

            @Override
            public void onCancel() {
                mFacebookEdit.hide(true);
            }

            @Override
            public void onError(FacebookException error) {
                mFacebookEdit.hide(true);
                mCheckFacebook.setChecked(false);
                Toast.makeText(CameraPreviewAlreadyExistActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        mCategorySpinner.setAdapter(categoryAdapter);
        mCategorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategory_id = position + 2;
                SavedData.setCategory_id(CameraPreviewAlreadyExistActivity.this, mCategory_id);
            }
        });

        restAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, rest_nameList);
        mRestnameSpinner.setAdapter(restAdapter);
        mRestnameSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRest_id = rest_idList.get(position);
                mRestname = rest_nameList.get(position);
                SavedData.setRest_id(CameraPreviewAlreadyExistActivity.this, mRest_id);
                SavedData.setRestname(CameraPreviewAlreadyExistActivity.this, mRestname);
            }
        });

        if (!mRestname.isEmpty()) {
            mRestnameSpinner.setClickable(false);
        } else {
            if (mLatitude.isEmpty() && mLongitude.isEmpty()) {
                SmartLocation.with(this).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        mLatitude = String.valueOf(location.getLatitude());
                        mLongitude = String.valueOf(location.getLongitude());
                        API3.Util.GetNearLocalCode localCode = API3.Impl.getRepository().GetNearParameterRegex(mLatitude, mLongitude);
                        if (localCode == null) {
                            mPresenter.getNearData(Const.APICategory.GET_NEAR_FIRST, API3.Util.getGetNearAPI(mLatitude, mLongitude));
                        } else {
                            Toast.makeText(CameraPreviewAlreadyExistActivity.this, API3.Util.GetNearLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                        }
                        SavedData.setLat(CameraPreviewAlreadyExistActivity.this, mLatitude);
                        SavedData.setLon(CameraPreviewAlreadyExistActivity.this, mLongitude);
                    }
                });
            } else {
                API3.Util.GetNearLocalCode localCode = API3.Impl.getRepository().GetNearParameterRegex(mLatitude, mLongitude);
                if (localCode == null) {
                    mPresenter.getNearData(Const.APICategory.GET_NEAR_FIRST, API3.Util.getGetNearAPI(mLatitude, mLongitude));
                } else {
                    Toast.makeText(CameraPreviewAlreadyExistActivity.this, API3.Util.GetNearLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                }
            }
        }

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

        mToukouButtonRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.getConnectedState(CameraPreviewAlreadyExistActivity.this) != Util.NetworkStatus.OFF) {
                    if (!mRest_id.equals("1")) {
                        if (!isPostApiFinished) {
                            if (mEditValue.getText().length() != 0) {
                                mValue = mEditValue.getText().toString();
                                SavedData.setValue(CameraPreviewAlreadyExistActivity.this, mValue);
                            }
                            if (mEditComment.getText().length() != 0) {
                                mMemo = mEditComment.getText().toString();
                                SavedData.setMemo(CameraPreviewAlreadyExistActivity.this, mMemo);
                            }
                            if (mCheckCheer.isChecked()) {
                                mCheer_flag = 1;
                            }
                            if (mCheckTwitter.isChecked()) {
                                mTracker = applicationGocci.getDefaultTracker();
                                mTracker.setScreenName("CameraPreviewAlready");
                                mTracker.send(new HitBuilders.SocialBuilder().setNetwork("Twitter").setAction("Share").setTarget(mAwsPostName).build());
                                TwitterSession session =
                                        Twitter.getSessionManager().getActiveSession();
                                if (session != null) {
                                    if (mVideoFile.length() < 1024 * 1024 * 15) {
                                        try {
                                            final TwitterAuthToken authToken = session.getAuthToken();
                                            if (mTwitterMemo.isEmpty()) {
                                                mTwitterMemo = getMessage();
                                            }
                                            TwitterUtil.performShare(CameraPreviewAlreadyExistActivity.this, authToken.token, authToken.secret, mVideoFile, mTwitterMemo);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (mCheckFacebook.isChecked()) {
                                mTracker = applicationGocci.getDefaultTracker();
                                mTracker.setScreenName("CameraPreviewAlready");
                                mTracker.send(new HitBuilders.SocialBuilder().setNetwork("Facebook").setAction("Share").setTarget(mAwsPostName).build());
                                if (mFacebookMemo.isEmpty()) {
                                    mFacebookMemo = getMessage();
                                }
                                FacebookUtil.performShare(CameraPreviewAlreadyExistActivity.this, AccessToken.getCurrentAccessToken().getToken(), mVideoFile, mFacebookMemo);
                            }
                            API3PostUtil.setPostAsync(CameraPreviewAlreadyExistActivity.this, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY, mRest_id, mAwsPostName, mCategory_id, mValue, mMemo, mCheer_flag);
                        } else {
                            mProgressWheel.setVisibility(View.VISIBLE);
                            mOverlay.setVisibility(View.VISIBLE);
                            Application_Gocci.postingVideoToS3(CameraPreviewAlreadyExistActivity.this, mAwsPostName, mVideoFile, mProgressWheel, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY);
                        }
                    } else {
                        Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.please_input_restname), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.bad_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (mIsnewRestname || !mRestname.isEmpty()) {
            mAddRestText.setVisibility(View.GONE);
        }

        mFacebookLoginButton.setPublishPermissions("publish_actions");
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Uri uri = Uri.fromFile(mVideoFile);
//                if (ShareDialog.canShow(ShareVideoContent.class)) {
//                    ShareVideo video = new ShareVideo.Builder()
//                            .setLocalUrl(uri)
//                            .build();
//                    ShareVideoContent content = new ShareVideoContent.Builder()
//                            .setVideo(video)
//                            .build();
//                    shareDialog.show(content);
//                } else {
//                    // ...sharing failed, handle error
//                    Toast.makeText(CameraPreviewAlreadyExistActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
//                }
                mFacebookEdit.show(true);
                mCheckFacebook.setChecked(true);
                API3PostUtil.setSnsLinkAsync(CameraPreviewAlreadyExistActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.CAMERA_PREVIEW_ALREADY, Const.APICategory.SET_FACEBOOK_LINK);
                //Profile profile = Profile.getCurrentProfile();
                //String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";
                //String post_date = SavedData.getServerUserId(CameraPreviewAlreadyExistActivity.this) + "_" + Util.getDateTimeString();
                //API3PostUtil.setProfileImgAsync(CameraPreviewAlreadyExistActivity.this, post_date, profile_img, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY);
            }

            @Override
            public void onCancel() {
                mFacebookEdit.hide(true);
                mCheckFacebook.setChecked(false);
            }

            @Override
            public void onError(FacebookException e) {
                mFacebookEdit.hide(true);
                mCheckFacebook.setChecked(false);
                Toast.makeText(CameraPreviewAlreadyExistActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mTwitterEdit.show(true);
                mCheckTwitter.setChecked(true);
                TwitterAuthToken authToken = result.data.getAuthToken();
                API3PostUtil.setSnsLinkAsync(CameraPreviewAlreadyExistActivity.this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY, Const.APICategory.SET_TWITTER_LINK);
                //String username = result.data.getUserName();
                //String profile_img = "http://www.paper-glasses.com/api/twipi/" + username;
                //String post_date = SavedData.getServerUserId(CameraPreviewAlreadyExistActivity.this) + "_" + Util.getDateTimeString();
                //API3PostUtil.setProfileImgAsync(CameraPreviewAlreadyExistActivity.this, post_date, profile_img, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY);
            }

            @Override
            public void failure(TwitterException exception) {
                mTwitterEdit.hide(true);
                mCheckTwitter.setChecked(false);
                Toast.makeText(CameraPreviewAlreadyExistActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        mPresenter.pause();
        BusHolder.get().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("CameraPreviewAlready");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mPresenter.resume();
        BusHolder.get().register(this);
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.CAMERA_PREVIEW_ALREADY) {
            if (event.apiCategory == Const.APICategory.SET_RESTADD) {
                mIsnewRestname = true;
                mRestnameSpinner.setText(mRestname);
                mRest_id = event.id;
                mRestnameSpinner.setClickable(false);
                SavedData.setIsNewRestname(CameraPreviewAlreadyExistActivity.this, mIsnewRestname);
                SavedData.setRestname(CameraPreviewAlreadyExistActivity.this, mRestname);
                SavedData.setRest_id(CameraPreviewAlreadyExistActivity.this, mRest_id);
            } else if (event.apiCategory == Const.APICategory.SET_POST) {
                switch (event.callback) {
                    case SUCCESS:
                        mProgressWheel.setVisibility(View.VISIBLE);
                        mOverlay.setVisibility(View.VISIBLE);
                        isPostApiFinished = true;
                        SavedData.setPostFinished(CameraPreviewAlreadyExistActivity.this, true);
                        Application_Gocci.postingVideoToS3(CameraPreviewAlreadyExistActivity.this, mAwsPostName, mVideoFile, mProgressWheel, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY);
                        break;
                    case LOCALERROR:
                    case GLOBALERROR:
                        mProgressWheel.setVisibility(View.INVISIBLE);
                        mOverlay.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, getString(R.string.videoposting_failure), Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    private String getMessage() {
        StringBuilder builder = new StringBuilder();
        if (!mEditComment.getText().toString().isEmpty())
            builder.append(mEditComment.getText().toString());
        builder.append(" #").append(mRestname.replaceAll("\\s+", ""));
        if (!mCategorySpinner.getText().toString().isEmpty())
            builder.append(" #").append(mCategorySpinner.getText().toString());
        if (!mEditValue.getText().toString().isEmpty())
            builder.append(" #").append(mEditValue.getText().toString()).append("円");
        builder.append(" #").append("Gocci");
        return builder.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SavedData.setPostVideoPreview(this, mRestname, mRest_id, mVideoUrl, mAwsPostName, mCategory_id, mMemo, mValue, mIsnewRestname,
                mLongitude, mLatitude);
    }

    @Override
    public void onBackPressed() {
        if (mSlidingLayout != null &&
                (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void createTenpo() {
        new MaterialDialog.Builder(CameraPreviewAlreadyExistActivity.this)
                .content(getString(R.string.add_restname))
                .contentColorRes(R.color.nameblack)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.nameblack)
                .positiveText(getString(R.string.send))
                .positiveColorRes(R.color.gocci_header)
                .input("", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        mRestname = charSequence.toString();
                        API3PostUtil.setRestAsync(CameraPreviewAlreadyExistActivity.this, Const.ActivityCategory.CAMERA_PREVIEW_ALREADY, mRestname, mLongitude, mLatitude);
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
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    @Override
    public void showResult(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray) {
        rest_nameList.addAll(restnameArray);
        rest_idList.addAll(restIdArray);
        restAdapter.addAll(rest_nameList);
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case SET_POST:
                API3PostUtil.setPostAsync(CameraPreviewAlreadyExistActivity.this, Const.ActivityCategory.CAMERA_PREVIEW, mRest_id, mAwsPostName, mCategory_id, mValue, mMemo, mCheer_flag);
                break;
            case SET_RESTADD:
                API3PostUtil.setRestAsync(CameraPreviewAlreadyExistActivity.this, Const.ActivityCategory.CAMERA_PREVIEW, mRestname, mLongitude, mLatitude);
                break;
            default:
                break;
        }
    }
}

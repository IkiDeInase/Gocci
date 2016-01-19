package com.inase.android.gocci.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.GochiRepository;
import com.inase.android.gocci.datasource.repository.GochiRepositoryImpl;
import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.datasource.repository.PostDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.GochiUseCaseImpl;
import com.inase.android.gocci.domain.usecase.PostPageUseCase;
import com.inase.android.gocci.domain.usecase.PostPageUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowPostPagePresenter;
import com.inase.android.gocci.ui.view.GochiLayout;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.SquareExoVideoView;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.video.HlsRendererBuilder;
import com.inase.android.gocci.utils.video.VideoPlayer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class PostActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener,
        ShowPostPagePresenter.ShowPostView {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.gochi_layout)
    GochiLayout mGochi;

    @Bind(R.id.circle_image)
    public ImageView mCircleImage;
    @Bind(R.id.username)
    public TextView mUsername;
    @Bind(R.id.restname)
    public TextView mRestname;
    @Bind(R.id.locality)
    public TextView mLocality;
    @Bind(R.id.user_time_text)
    public TextView mUserTimeText;
    @Bind(R.id.rest_time_text)
    public TextView mRestTimeText;
    @Bind(R.id.menu_ripple)
    public RippleView mMenuRipple;
    @Bind(R.id.video_thumbnail)
    public SquareImageView mVideoThumbnail;
    @Bind(R.id.square_video_exo)
    public SquareExoVideoView mSquareVideoExo;
    @Bind(R.id.category)
    public TextView mCategory;
    @Bind(R.id.value)
    public TextView mValue;
    @Bind(R.id.comment)
    public TextView mComment;
    @Bind(R.id.likes_number)
    public TextView mLikesNumber;
    @Bind(R.id.likes_Image)
    public ImageView mLikesImage;
    @Bind(R.id.comments_number)
    public TextView mCommentsNumber;
    @Bind(R.id.likes_ripple)
    public RippleView mLikesRipple;
    @Bind(R.id.comments_ripple)
    public RippleView mCommentsRipple;
    @Bind(R.id.share_ripple)
    public RippleView mShareRipple;
    @Bind(R.id.video_frame)
    public AspectRatioFrameLayout mVideoFrame;

    private float pointX;
    private float pointY;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private Point mDisplaySize;

    private String mPost_id;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private ShowPostPagePresenter mPresenter;

    private PostData mPostData;

    public static void startPostActivity(String post_id, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, PostActivity.class);
        intent.putExtra("post_id", post_id);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void startPostActivityOnContext(String post_id, Context context) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("post_id", post_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);
        audioCapabilitiesReceiver.register();

        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(PostActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(PostActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(PostActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        final API3 api3Impl = API3.Impl.getRepository();
        PostDataRepository postDataRepositoryImpl = PostDataRepositoryImpl.getRepository(api3Impl);
        GochiRepository gochiRepository = GochiRepositoryImpl.getRepository(api3Impl);
        PostPageUseCase postPageUseCaseImpl = PostPageUseCaseImpl.getUseCase(postDataRepositoryImpl, UIThread.getInstance());
        GochiUseCase gochiUseCase = GochiUseCaseImpl.getUseCase(gochiRepository, UIThread.getInstance());
        mPresenter = new ShowPostPagePresenter(postPageUseCaseImpl, gochiUseCase);
        mPresenter.setPostView(this);

        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        Intent intent = getIntent();
        mPost_id = intent.getStringExtra("post_id");

        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        mToolBar.setTitle("投稿ページ");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        API3.Util.GetPostLocalCode localCode = api3Impl.GetPostParameterRegex(mPost_id);
        if (localCode == null) {
            mPresenter.getPostData(Const.APICategory.GET_POST, API3.Util.getGetPostAPI(mPost_id));
        } else {
            Toast.makeText(this, API3.Util.GetPostLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }

        mGochi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                //final float y = Util.getScreenHeightInPx(TimelineActivity.this) - event.getRawY();
                pointX = event.getX();
                pointY = event.getY();
                return false;
            }
        });
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Override
    public final void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);

        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        mVideoThumbnail.setVisibility(View.VISIBLE);
        mPresenter.pause();
    }

    @Override
    public final void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("PostPage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        BusHolder.get().register(this);

        if (mPostData != null) {
            if (player == null) {
                releasePlayer();
                if (Util.isMovieAutoPlay(this)) {
                    preparePlayer(getVideoPath());
                }
            } else {
                player.setBackgrounded(false);
            }
        }
        mPresenter.resume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        releasePlayer();
        if (Util.isMovieAutoPlay(this)) {
            preparePlayer(getVideoPath());
        }
        player.setBackgrounded(false);
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

    private String getVideoPath() {
        return mPostData.getHls_movie();
    }

    private void preparePlayer(String path) {
        if (player == null) {
            mTracker = applicationGocci.getDefaultTracker();
            mTracker.setScreenName("PostPage");
            mTracker.send(new HitBuilders.EventBuilder().setAction("PlayCount").setCategory("Movie").setValue(Long.parseLong(mPost_id)).build());

            player = new VideoPlayer(new HlsRendererBuilder(this, com.google.android.exoplayer.util.Util.getUserAgent(this, "Gocci"), path));
            player.addListener(new VideoPlayer.Listener() {
                @Override
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case VideoPlayer.STATE_BUFFERING:
                            break;
                        case VideoPlayer.STATE_ENDED:
                            mTracker = applicationGocci.getDefaultTracker();
                            mTracker.setScreenName("PostPage");
                            mTracker.send(new HitBuilders.EventBuilder().setAction("PlayCount").setCategory("Movie").setValue(Long.parseLong(mPost_id)).build());
                            player.seekTo(0);
                            break;
                        case VideoPlayer.STATE_IDLE:
                            break;
                        case VideoPlayer.STATE_PREPARING:
                            break;
                        case VideoPlayer.STATE_READY:
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (e instanceof UnsupportedDrmException) {
                        // Special case DRM failures.
                        UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
                        int stringId = com.google.android.exoplayer.util.Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                                : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                                ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
                        Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
                    }
                    playerNeedsPrepare = true;
                }

                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
                    mVideoThumbnail.setVisibility(View.GONE);
                    mVideoFrame.setAspectRatio(
                            height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
                }
            });
            //player.seekTo(playerPosition);
            playerNeedsPrepare = true;
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(mSquareVideoExo.getHolder().getSurface());
        player.setPlayWhenReady(true);

        if (SavedData.getSettingMute(this) == -1) {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, -1);
        } else {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, 0);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            //playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    private void changeMovie() {
        // TODO:実装
        final String path = mPostData.getHls_movie();
        releasePlayer();
        if (Util.isMovieAutoPlay(this)) {
            preparePlayer(path);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showResult(Const.APICategory api, final PostData mPostData) {
        this.mPostData = mPostData;

        mUsername.setText(mPostData.getUsername());
        mRestname.setText(mPostData.getRestname());
        mLocality.setText(mPostData.getLocality());

        mUserTimeText.setText(mPostData.getPost_date());
        mRestTimeText.setVisibility(View.INVISIBLE);

        if (!mPostData.getMemo().equals("none")) {
            mComment.setText(mPostData.getMemo());
        } else {
            mComment.setText("");
        }

        Picasso.with(this)
                .load(mPostData.getProfile_img())
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(mCircleImage);

        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfActivity.startUserProfActivity(mPostData.getPost_user_id(), mPostData.getUsername(), PostActivity.this);
            }
        });

        mCircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfActivity.startUserProfActivity(mPostData.getPost_user_id(), mPostData.getUsername(), PostActivity.this);
            }
        });

        mRestname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TenpoActivity.startTenpoActivity(mPostData.getPost_rest_id(), mPostData.getRestname(), PostActivity.this);
            }
        });

        mLocality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TenpoActivity.startTenpoActivity(mPostData.getPost_rest_id(), mPostData.getRestname(), PostActivity.this);
            }
        });

        mMenuRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheet.Builder(PostActivity.this, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.violation:
                                Util.setPostBlockDialog(PostActivity.this, mPostData.getPost_id());
                                break;
                            case R.id.close:
                                dialog.dismiss();
                        }
                    }
                }).show();
            }
        });
        Picasso.with(this)
                .load(mPostData.getThumbnail())
                .placeholder(R.color.videobackground)
                .into(mVideoThumbnail);
        mVideoThumbnail.setVisibility(View.VISIBLE);

        mVideoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    } else {
                        player.getPlayerControl().start();
                    }
                } else {
                    if (!Util.isMovieAutoPlay(PostActivity.this)) {
                        releasePlayer();
                        preparePlayer(getVideoPath());
                    }
                }
            }
        });

        if (!mPostData.getCategory().equals(getString(R.string.nothing_tag))) {
            mCategory.setText(mPostData.getCategory());
        } else {
            mCategory.setText("　　　　");
        }
        if (!mPostData.getValue().equals("0")) {
            mValue.setText(mPostData.getValue() + "円");
        } else {
            mValue.setText("　　　　");
        }

        mLikesNumber.setText(String.valueOf(mPostData.getGochi_num()));
        mCommentsNumber.setText(String.valueOf(mPostData.getComment_num()));

        if (!mPostData.isGochi_flag()) {
            mLikesImage.setImageResource(R.drawable.ic_icon_beef);
        } else {
            mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
        }
        mLikesRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPostData.isGochi_flag()) {
                    setGochiLayout();
                    API3.Util.SetGochiLocalCode postGochiLocalCode = API3.Impl.getRepository().SetGochiParameterRegex(mPost_id);
                    if (postGochiLocalCode == null) {
                        mPresenter.postGochi(Const.APICategory.SET_GOCHI, API3.Util.getSetGochiAPI(mPost_id), mPost_id);
                    } else {
                        Toast.makeText(PostActivity.this, API3.Util.SetGochiLocalCodeMessageTable(postGochiLocalCode), Toast.LENGTH_SHORT).show();
                    }
                    mPostData.setGochi_flag(true);
                    mPostData.setGochi_num(mPostData.getGochi_num() + 1);
                    mLikesNumber.setText(String.valueOf((mPostData.getGochi_num())));
                    mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
                } else {
                    API3.Util.UnsetGochiLocalCode unpostGochiLocalCode = API3.Impl.getRepository().UnsetGochiParameterRegex(mPost_id);
                    if (unpostGochiLocalCode == null) {
                        mPresenter.postGochi(Const.APICategory.UNSET_GOCHI, API3.Util.getUnsetGochiAPI(mPost_id), mPost_id);
                    } else {
                        Toast.makeText(PostActivity.this, API3.Util.UnsetGochiLocalCodeMessageTable(unpostGochiLocalCode), Toast.LENGTH_SHORT).show();
                    }
                    mPostData.setGochi_flag(false);
                    mPostData.setGochi_num(mPostData.getGochi_num() - 1);
                    mLikesNumber.setText(String.valueOf((mPostData.getGochi_num())));
                    mLikesImage.setImageResource(R.drawable.ic_icon_beef);
                }
            }
        });

        mCommentsRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                CommentActivity.startCommentActivity(mPostData.getPost_id(), false, PostActivity.this);
            }
        });

        mShareRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Application_Gocci.getShareTransfer() != null) {
                    new BottomSheet.Builder(PostActivity.this, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.facebook_share:
                                    Toast.makeText(PostActivity.this, getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);
                                        } else {
                                            Util.facebookVideoShare(PostActivity.this, shareDialog, mPostData.getMovie());
                                        }
                                    } else {
                                        Util.facebookVideoShare(PostActivity.this, shareDialog, mPostData.getMovie());
                                    }
                                    break;
                                case R.id.twitter_share:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 26);
                                        } else {
                                            TwitterSession session = Twitter.getSessionManager().getActiveSession();
                                            if (session != null) {
                                                TwitterAuthToken authToken = session.getAuthToken();
                                                Util.twitterShare(PostActivity.this, "#" + mPostData.getRestname().replaceAll("\\s+", "") + " #Gocci", mPostData.getMovie(), authToken);
                                            } else {
                                                Toast.makeText(PostActivity.this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        TwitterSession session = Twitter.getSessionManager().getActiveSession();
                                        if (session != null) {
                                            TwitterAuthToken authToken = session.getAuthToken();
                                            Util.twitterShare(PostActivity.this, "#" + mPostData.getRestname().replaceAll("\\s+", "") + " #Gocci", mPostData.getMovie(), authToken);
                                        } else {
                                            Toast.makeText(PostActivity.this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;
                                case R.id.other_share:
                                    Toast.makeText(PostActivity.this, getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 27);
                                        } else {
                                            Util.instaVideoShare(PostActivity.this, mPostData.getMovie());
                                        }
                                    } else {
                                        Util.instaVideoShare(PostActivity.this, mPostData.getMovie());
                                    }
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                } else {
                    Toast.makeText(PostActivity.this, getString(R.string.preparing_share_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        changeMovie();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 25:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.facebookVideoShare(this, shareDialog, mPostData.getMovie());
                } else {
                    Toast.makeText(PostActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                }
                break;
            case 26:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    if (session != null) {
                        TwitterAuthToken authToken = session.getAuthToken();
                        Util.twitterShare(this, "#" + mPostData.getRestname().replaceAll("\\s+", "") + " #Gocci", mPostData.getMovie(), authToken);
                    } else {
                        Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PostActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                }
                break;
            case 27:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.instaVideoShare(this, mPostData.getMovie());
                } else {
                    Toast.makeText(PostActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gochiSuccess(Const.APICategory api, String post_id) {

    }

    @Override
    public void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        if (api == Const.APICategory.SET_GOCHI) {
            mPostData.setGochi_flag(false);
            mPostData.setGochi_num(mPostData.getGochi_num() - 1);
            mLikesImage.setImageResource(R.drawable.ic_icon_beef);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            mPostData.setGochi_flag(true);
            mPostData.setGochi_num(mPostData.getGochi_num() + 1);
            mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
        }
        mLikesNumber.setText(String.valueOf(mPostData.getGochi_num()));
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
    }

    @Override
    public void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        if (api == Const.APICategory.SET_GOCHI) {
            mPostData.setGochi_flag(false);
            mPostData.setGochi_num(mPostData.getGochi_num() - 1);
            mLikesImage.setImageResource(R.drawable.ic_icon_beef);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            mPostData.setGochi_flag(true);
            mPostData.setGochi_num(mPostData.getGochi_num() + 1);
            mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
        }
        mLikesNumber.setText(String.valueOf(mPostData.getGochi_num()));
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void setGochiLayout() {
        final float y = Util.getScreenHeightInPx(this) - pointY;
        mGochi.post(new Runnable() {
            @Override
            public void run() {
                mGochi.addGochi(pointX, y);
            }
        });
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_POST:
                mPresenter.getPostData(event.api, API3.Util.getGetPostAPI(mPost_id));
                break;
            default:
                break;
        }
    }
}

package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.ApiConst;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.CommentActionRepository;
import com.inase.android.gocci.datasource.repository.CommentActionRepositoryImpl;
import com.inase.android.gocci.datasource.repository.CommentDataRepository;
import com.inase.android.gocci.datasource.repository.CommentDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.CommentPageUseCase;
import com.inase.android.gocci.domain.usecase.CommentPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.CommentPostUseCase;
import com.inase.android.gocci.domain.usecase.CommentPostUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.presenter.ShowCommentPagePresenter;
import com.inase.android.gocci.ui.adapter.CommentAdapter;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.video.HlsRendererBuilder;
import com.inase.android.gocci.utils.video.VideoPlayer;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class CommentActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener, ObservableScrollViewCallbacks,
        ShowCommentPagePresenter.ShowCommentView, CommentAdapter.CommentCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.list)
    ObservableRecyclerView mCommentRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.comment_button)
    FloatingActionButton mCommentButton;

    @OnClick(R.id.comment_button)
    public void comment() {
        new MaterialDialog.Builder(CommentActivity.this)
                .title(getString(R.string.comment))
                .titleColorRes(R.color.namegrey)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
                .inputMaxLength(140)
                .input(null, null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        mPresenter.postComment(Const.getPostCommentAPI(mPost_id, input.toString()), Const.getCommentAPI(mPost_id));
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(getString(R.string.post_comment))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    private LinearLayoutManager mLayoutManager;
    private ArrayList<HeaderData> mCommentusers = new ArrayList<>();
    private CommentAdapter mCommentAdapter;

    private CommentActivity self = this;

    private Point mDisplaySize;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.ExoViewHolder, String> mViewHolderHash;  // Value: PosterId

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private PostData headerPost;
    private String mPost_id;
    private String title;

    private Drawer result;
    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private static MobileAnalyticsManager analytics;

    private boolean isExist = false;
    private boolean isSee = false;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private ShowCommentPagePresenter mPresenter;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (isSee) {
                changeMovie();
            }
            if (mPlayingPostId != null) {
                mCommentRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommentActivity activity
                    = (CommentActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_TIMELINE:
                    activity.startActivity(new Intent(activity, GocciTimelineActivity.class));
                    activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    break;
                case Const.INTENT_TO_MYPAGE:
                    GocciMyprofActivity.startMyProfActivity(activity);
                    break;
                case Const.INTENT_TO_ADVICE:
                    Util.setAdviceDialog(activity);
                    break;
                case Const.INTENT_TO_SETTING:
                    SettingActivity.startSettingActivity(activity);
                    break;
            }
        }
    };

    public static void startCommentActivity(int post_id, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, CommentActivity.class);
        intent.putExtra("title", startingActivity.getLocalClassName());
        intent.putExtra("post_id", post_id);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void startCommentActivityOnContext(int post_id, Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("title", context.getString(R.string.comment));
        intent.putExtra("post_id", post_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPlayBlockFlag = false;
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

        CommentDataRepository commentDataRepositoryImpl = CommentDataRepositoryImpl.getRepository();
        CommentActionRepository commentActionRepositoryImpl = CommentActionRepositoryImpl.getRepository();
        CommentPageUseCase commentPageUseCaseImpl = CommentPageUseCaseImpl.getUseCase(commentDataRepositoryImpl, UIThread.getInstance());
        CommentPostUseCase commentPostUseCaseImpl = CommentPostUseCaseImpl.getUseCase(commentActionRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowCommentPagePresenter(commentPageUseCaseImpl, commentPostUseCaseImpl);
        mPresenter.setCommentView(this);

        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);
        audioCapabilitiesReceiver.register();
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(CommentActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(CommentActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(CommentActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        mPost_id = String.valueOf(intent.getIntExtra("post_id", 0));

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        setSupportActionBar(mToolBar);
        if (title.equals("Activity.GocciMyprofActivity")) {
            getSupportActionBar().setTitle(getString(R.string.mypage));
        } else {
            getSupportActionBar().setTitle(getString(R.string.comment));
        }

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolBar)
                .withHeader(new DrawerProfHeader(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.timeline)).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(getString(R.string.mypage)).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.send_advice)).withIcon(GoogleMaterial.Icon.gmd_send).withSelectable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName(SavedData.getSettingMute(this) == 0 ? getString(R.string.setting_support_mute) : getString(R.string.setting_support_unmute)).withIcon(GoogleMaterial.Icon.gmd_volume_mute).withSelectable(false).withIdentifier(5),
                        new PrimaryDrawerItem().withName(getString(R.string.settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, CommentActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, CommentActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, CommentActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, CommentActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(CommentActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(CommentActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));

                                        if (player != null) {
                                            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, -1);
                                        }
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(CommentActivity.this, 0);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_mute)));

                                        if (player != null) {
                                            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, 0);
                                        }
                                        break;
                                }
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(-1)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        finish();
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        return true;
                    }
                })
                .build();

        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mCommentRecyclerView.setLayoutManager(mLayoutManager);
        mCommentRecyclerView.setHasFixedSize(true);
        mCommentRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mCommentRecyclerView.setScrollViewCallbacks(this);

        mPresenter.getCommentData(ApiConst.COMMENT_FIRST, Const.getCommentAPI(mPost_id));

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(CommentActivity.this) != Util.NetworkStatus.OFF) {
                    mPresenter.getCommentData(ApiConst.COMMENT_REFRESH, Const.getCommentAPI(mPost_id));
                } else {
                    Toast.makeText(CommentActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });

        mCommentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    // スクロールしていない
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //mBusy = false;
                        if (isSee) {
                            changeMovie();
                        } else {
                            final Const.ExoViewHolder oldViewHolder = getPlayingViewHolder();
                            if (oldViewHolder != null) {
                                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
                                releasePlayer();
                            }
                            mPlayingPostId = null;
                        }
                        break;
                    // スクロール中
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //mBusy = true;
                        break;
                    // はじいたとき
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //mBusy = true;
                        break;
                }

                visibleItemCount = mCommentRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                //投稿はある
//投稿がない
                isExist = totalItemCount != 1;

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    loading = true;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(self);
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        if (player == null) {
            if (mPlayingPostId != null) {
                if (Util.isMovieAutoPlay(this)) {
                    preparePlayer(getPlayingViewHolder(), headerPost.getMovie());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(self);

        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }

        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        if (getPlayingViewHolder() != null) {
            getPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);
        }
        mPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        //Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        if (mPlayingPostId != null) {
            releasePlayer();
            if (Util.isMovieAutoPlay(this)) {
                preparePlayer(getPlayingViewHolder(), headerPost.getMovie());
            }
        }
        player.setBackgrounded(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    private void preparePlayer(final Const.ExoViewHolder viewHolder, String path) {
        if (player == null) {
            player = new VideoPlayer(new HlsRendererBuilder(this, com.google.android.exoplayer.util.Util.getUserAgent(this, "Gocci"), path));
            player.addListener(new VideoPlayer.Listener() {
                @Override
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case VideoPlayer.STATE_BUFFERING:
                            break;
                        case VideoPlayer.STATE_ENDED:
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
                    viewHolder.mVideoThumbnail.setVisibility(View.GONE);
                    viewHolder.mVideoFrame.setAspectRatio(
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
        player.setSurface(viewHolder.mSquareVideoExo.getHolder().getSurface());
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
        if (!headerPost.getPost_id().equals(mPlayingPostId)) {

            // 前回の動画再生停止処理
            final Const.ExoViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = headerPost.getPost_id();
            final Const.ExoViewHolder currentViewHolder = getPlayingViewHolder();
            if (mPlayBlockFlag) {
                Log.d("DEBUG", "startMovie play block status");
                return;
            }

            final String path = headerPost.getMovie();
            releasePlayer();
            if (Util.isMovieAutoPlay(this)) {
                preparePlayer(currentViewHolder, path);
            }
        }
    }

    private Const.ExoViewHolder getPlayingViewHolder() {
        Const.ExoViewHolder viewHolder = null;
        if (mPlayingPostId != null) {
            for (Map.Entry<Const.ExoViewHolder, String> entry : mViewHolderHash.entrySet()) {
                if (entry.getValue().equals(mPlayingPostId)) {
                    viewHolder = entry.getKey();
                    break;
                }
            }
        }
        return viewHolder;
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {
        isSee = i < 750;
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            mCommentButton.hide();
        } else if (scrollState == ScrollState.DOWN) {
            mCommentButton.show();
        }
    }

    @Override
    public void onUserClick(int user_id, String user_name) {
        FlexibleUserProfActivity.startUserProfActivity(user_id, user_name, this);
    }

    @Override
    public void onRestClick(int rest_id, String rest_name) {
        FlexibleTenpoActivity.startTenpoActivity(rest_id, rest_name, this);
    }

    @Override
    public void onCommentPostClick(String postUrl) {
        mPresenter.postComment(postUrl, Const.getCommentAPI(mPost_id));
    }

    @Override
    public void onVideoFrameClick() {
        if (player != null) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
            } else {
                player.getPlayerControl().start();
            }
        } else {
            if (!Util.isMovieAutoPlay(this)) {
                releasePlayer();
                preparePlayer(getPlayingViewHolder(), headerPost.getMovie());
            }
        }
    }

    @Override
    public void onFacebookShare(String share) {
        Util.facebookVideoShare(this, shareDialog, share);
    }

    @Override
    public void onTwitterShare(SquareImageView view, String rest_name) {
        Util.twitterShare(this, view, rest_name);
    }

    @Override
    public void onInstaShare(String share, String rest_name) {
        Util.instaVideoShare(this, rest_name, share);
    }

    @Override
    public void onHashHolder(Const.ExoViewHolder holder, String post_id) {
        mViewHolderHash.put(holder, post_id);
        changeMovie();
    }

    @Override
    public void showLoading() {
        mSwipeContainer.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void showNoResultCase(int api, PostData postData) {
        headerPost = postData;
        switch (api) {
            case ApiConst.COMMENT_FIRST:
                mCommentAdapter = new CommentAdapter(this, mPost_id, headerPost, mCommentusers);
                mCommentAdapter.setCommentCallback(this);
                mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                break;
            case ApiConst.COMMENT_REFRESH:
                mCommentusers.clear();
                mCommentAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void hideNoResultCase() {

    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(int api, PostData postData, ArrayList<HeaderData> commentData) {
        headerPost = postData;
        mCommentusers.clear();
        mCommentusers.addAll(commentData);
        switch (api) {
            case ApiConst.COMMENT_FIRST:
                mCommentAdapter = new CommentAdapter(this, mPost_id, headerPost, mCommentusers);
                mCommentAdapter.setCommentCallback(this);
                mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                break;
            case ApiConst.COMMENT_REFRESH:
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mCommentAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void postCommented(PostData postData, ArrayList<HeaderData> commentData) {
        headerPost = postData;
        mCommentusers.clear();
        mCommentusers.addAll(commentData);
        mPlayingPostId = null;
        mViewHolderHash.clear();
        mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        mCommentAdapter.notifyDataSetChanged();
    }

    @Override
    public void postCommentEmpty(PostData postData) {
        headerPost = postData;
        mCommentusers.clear();
        mCommentAdapter.notifyDataSetChanged();
    }

    @Override
    public void postCommentFailed() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }
}

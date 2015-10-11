package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.ApiConst;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.RestPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.presenter.ShowRestPagePresenter;
import com.inase.android.gocci.ui.adapter.RestPageAdapter;
import com.inase.android.gocci.ui.view.CustomKenBurnsView;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class FlexibleTenpoActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener,
        ObservableScrollViewCallbacks, AppBarLayout.OnOffsetChangedListener,
        ShowRestPagePresenter.ShowRestView, RestPageAdapter.RestPageCallback {

    @Bind(R.id.background_image)
    CustomKenBurnsView mBackgroundImage;
    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.list)
    ObservableRecyclerView mTenpoRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.cheer_flag)
    ImageView mCheerFlag;
    @Bind(R.id.cheer_number)
    TextView mCheerNumber;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private int mWant_flag;
    private int mTotal_cheer_num;
    private TextView cheer_number;

    private ArrayList<PostData> mTenpousers = new ArrayList<PostData>();
    private RestPageAdapter mRestPageAdapter;
    private LinearLayoutManager mLayoutManager;

    private HeaderData mHeaderRestData;

    private int mRest_id;

    private AttributeSet mVideoAttr;
    private Point mDisplaySize;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.ExoViewHolder, String> mViewHolderHash;  // Value: PosterId

    private boolean isExist = false;
    private boolean isSee = false;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private Drawer result;

    private final FlexibleTenpoActivity self = this;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private static MobileAnalyticsManager analytics;

    private ShowRestPagePresenter mPresenter;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (isSee) {
                changeMovie();
            }
            if (mPlayingPostId != null && !isExist) {
                mTenpoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FlexibleTenpoActivity activity
                    = (FlexibleTenpoActivity) msg.obj;
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

    public static void startTenpoActivity(int rest_id, String restname, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, FlexibleTenpoActivity.class);
        intent.putExtra("rest_id", rest_id);
        intent.putExtra("rest_name", restname);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
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
                Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        UserAndRestDataRepository userAndRestDataRepositoryImpl = UserAndRestDataRepositoryImpl.getRepository();
        UserAndRestUseCase userAndRestUseCaseImpl = RestPageUseCaseImpl.getUseCase(userAndRestDataRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowRestPagePresenter(userAndRestUseCaseImpl);
        mPresenter.setRestView(this);

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        setContentView(R.layout.activity_flexible_tenpo);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mRest_id = intent.getIntExtra("rest_id", 0);

        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(mToolBar);

        mLayoutManager = new LinearLayoutManager(this);
        mTenpoRecyclerView.setLayoutManager(mLayoutManager);
        mTenpoRecyclerView.setHasFixedSize(true);
        mTenpoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTenpoRecyclerView.setScrollViewCallbacks(this);

        mPresenter.getRestData(ApiConst.RESTPAGE_FIRST, Const.getRestpageAPI(mRest_id));

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
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, FlexibleTenpoActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, FlexibleTenpoActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, FlexibleTenpoActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, FlexibleTenpoActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(FlexibleTenpoActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(FlexibleTenpoActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));

                                        if (player != null) {
                                            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, -1);
                                        }
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(FlexibleTenpoActivity.this, 0);
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

        mCollapsingToolbar.setTitle(intent.getStringExtra("rest_name"));
        mBackgroundImage.setTransitionGenerator(new RandomTransitionGenerator(4500, new AccelerateDecelerateInterpolator()));

        mCheerNumber.setText(String.valueOf(mTotal_cheer_num));
        mTenpoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    // スクロールしていない
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //mBusy = false;
                        if (isSee) {
                            changeMovie();
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

                visibleItemCount = mTenpoRecyclerView.getChildCount();
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

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(FlexibleTenpoActivity.this) != Util.NetworkStatus.OFF) {
                    mPresenter.getRestData(ApiConst.RESTPAGE_REFRESH, Const.getRestpageAPI(mRest_id));
                } else {
                    Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public final void onDestroy() {
        if (mRestPageAdapter != null && mRestPageAdapter.getMapView() != null) {
            mRestPageAdapter.getMapView().onDestroy();
        }
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();

    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        if (mRestPageAdapter != null && mRestPageAdapter.getMapView() != null) {
            mRestPageAdapter.getMapView().onLowMemory();
        }
    }

    @Override
    public final void onPause() {
        if (mRestPageAdapter != null && mRestPageAdapter.getMapView() != null) {
            mRestPageAdapter.getMapView().onPause();
        }
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        BusHolder.get().unregister(self);

        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        if (getPlayingViewHolder() != null) {
            getPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);
        }
        mAppBar.removeOnOffsetChangedListener(this);
        mPresenter.pause();
    }

    @Override
    public final void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);
        if (mRestPageAdapter != null && mRestPageAdapter.getMapView() != null) {
            mRestPageAdapter.getMapView().onResume();
        }

        if (player == null) {
            if (mPlayingPostId != null && !isExist) {
                releasePlayer();
                if (Util.isMovieAutoPlay(this)) {
                    preparePlayer(getPlayingViewHolder(), getVideoPath());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
        mAppBar.addOnOffsetChangedListener(this);
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
        if (mPlayingPostId != null && !isExist) {
            releasePlayer();
            if (Util.isMovieAutoPlay(this)) {
                preparePlayer(getPlayingViewHolder(), getVideoPath());
            }
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

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
        if (mRestPageAdapter != null && mRestPageAdapter.getMapView() != null) {
            mRestPageAdapter.getMapView().onSaveInstanceState(outState);
        }
    }

    private String getVideoPath() {
        final int position = mTenpoRecyclerView.getChildAdapterPosition(mTenpoRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mRestPageAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        //return mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        return userData.getMovie();
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
        final int position = mTenpoRecyclerView.getChildAdapterPosition(mTenpoRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        if (mRestPageAdapter.isEmpty()) {
            return;
        }
        if (position - 1 < 0) {
            return;
        }

        final PostData userData = mRestPageAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {

            // 前回の動画再生停止処理
            final Const.ExoViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final Const.ExoViewHolder currentViewHolder = getPlayingViewHolder();
            if (mPlayBlockFlag) {
                return;
            }

            final String path = userData.getMovie();
            releasePlayer();
            if (Util.isMovieAutoPlay(this)) {
                preparePlayer(currentViewHolder, path);
            }
        }
    }

    /**
     * 現在再生中のViewHolderを取得
     *
     * @return
     */
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
        //ヘッダー通り過ぎた
        isSee = i > 250;
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeContainer.setEnabled(i == 0);
    }

    @Override
    public void onCallClick(Uri tel) {
        Intent intent = new Intent(Intent.ACTION_DIAL, tel);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onGoHereClick(Uri uri) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onHomePageClick(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onUserClick(int user_id, String user_name) {
        FlexibleUserProfActivity.startUserProfActivity(user_id, user_name, this);
    }

    @Override
    public void onCommentClick(String post_id) {
        CommentActivity.startCommentActivity(Integer.parseInt(post_id), this);

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
                preparePlayer(getPlayingViewHolder(), getVideoPath());
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
    public void showNoResultCase(int api, HeaderData restData) {
        mHeaderRestData = restData;
        if (api == ApiConst.RESTPAGE_FIRST) {
            mBackgroundImage.setImageResource(R.drawable.ic_background_login);
            mRestPageAdapter = new RestPageAdapter(this, mHeaderRestData, mTenpousers);
            mRestPageAdapter.setRestPageCallback(this);
            mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
            mTenpoRecyclerView.setAdapter(mRestPageAdapter);
        } else if (api == ApiConst.USERPAGE_REFRESH) {
            mTenpousers.clear();
            mRestPageAdapter.setData(mHeaderRestData, mTenpousers);
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
    public void showResult(int api, HeaderData restData, ArrayList<PostData> mPostData) {
        mHeaderRestData = restData;
        mTenpousers.clear();
        mTenpousers.addAll(mPostData);
        switch (api) {
            case ApiConst.RESTPAGE_FIRST:
                Picasso.with(this).load(mTenpousers.get(0).getThumbnail()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mBackgroundImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                mRestPageAdapter = new RestPageAdapter(this, mHeaderRestData, mTenpousers);
                mRestPageAdapter.setRestPageCallback(this);
                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoRecyclerView.setAdapter(mRestPageAdapter);
                break;
            case ApiConst.RESTPAGE_REFRESH:
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mRestPageAdapter.setData(mHeaderRestData, mTenpousers);
                break;
        }
    }
}

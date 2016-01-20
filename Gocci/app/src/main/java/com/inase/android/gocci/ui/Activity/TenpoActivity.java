package com.inase.android.gocci.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.GochiRepository;
import com.inase.android.gocci.datasource.repository.GochiRepositoryImpl;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.GochiUseCaseImpl;
import com.inase.android.gocci.domain.usecase.RestPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowRestPagePresenter;
import com.inase.android.gocci.ui.adapter.RestPageAdapter;
import com.inase.android.gocci.ui.view.CustomKenBurnsView;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.GochiLayout;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class TenpoActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener,
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
    @Bind(R.id.gochi_layout)
    GochiLayout mGochi;

    private int mWant_flag;
    private int mTotal_cheer_num;
    private TextView cheer_number;

    private ArrayList<PostData> mTenpousers = new ArrayList<PostData>();
    private ArrayList<String> mPost_ids = new ArrayList<>();
    private RestPageAdapter mRestPageAdapter;
    private LinearLayoutManager mLayoutManager;

    private HeaderData mHeaderRestData;

    private String mRest_id;

    private Point mDisplaySize;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.StreamRestViewHolder, String> mViewHolderHash;  // Value: PosterId

    private boolean isExist = false;
    private boolean isSee = false;

    private float pointX;
    private float pointY;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private Drawer result;

    private final TenpoActivity self = this;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private ShowRestPagePresenter mPresenter;

    private String mShareShare;
    private String mShareRestname;

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

    private static Handler sHandler = new Handler();

    public static void startTenpoActivity(String rest_id, String restname, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TenpoActivity.class);
        intent.putExtra("rest_id", rest_id);
        intent.putExtra("rest_name", restname);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    public final void onCreate(Bundle savedInstanceState) {
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
                Toast.makeText(TenpoActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(TenpoActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(TenpoActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        final API3 api3Impl = API3.Impl.getRepository();
        UserAndRestDataRepository userAndRestDataRepositoryImpl = UserAndRestDataRepositoryImpl.getRepository(api3Impl);
        GochiRepository gochiRepository = GochiRepositoryImpl.getRepository(api3Impl);
        UserAndRestUseCase userAndRestUseCaseImpl = RestPageUseCaseImpl.getUseCase(userAndRestDataRepositoryImpl, UIThread.getInstance());
        GochiUseCase gochiUseCase = GochiUseCaseImpl.getUseCase(gochiRepository, UIThread.getInstance());
        mPresenter = new ShowRestPagePresenter(userAndRestUseCaseImpl, gochiUseCase);
        mPresenter.setRestView(this);

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        setContentView(R.layout.activity_tenpo);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        Intent intent = getIntent();
        mRest_id = intent.getStringExtra("rest_id");

        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(mToolBar);

        mLayoutManager = new LinearLayoutManager(this);
        mTenpoRecyclerView.setLayoutManager(mLayoutManager);
        mTenpoRecyclerView.setHasFixedSize(true);
        mTenpoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTenpoRecyclerView.setScrollViewCallbacks(this);

        API3.Util.GetRestLocalCode localCode = api3Impl.GetRestParameterRegex(mRest_id);
        if (localCode == null) {
            mPresenter.getRestData(Const.APICategory.GET_REST_FIRST, API3.Util.getGetRestAPI(mRest_id));
        } else {
            Toast.makeText(this, API3.Util.GetRestLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(TenpoActivity.this, TimelineActivity.class));
                                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyprofActivity.startMyProfActivity(TenpoActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.setFeedbackDialog(TenpoActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingActivity.startSettingActivity(TenpoActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(TenpoActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(TenpoActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));

                                        if (player != null) {
                                            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, -1);
                                        }
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(TenpoActivity.this, 0);
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
                if (Util.getConnectedState(TenpoActivity.this) != Util.NetworkStatus.OFF) {
                    API3.Util.GetRestLocalCode localCode = api3Impl.GetRestParameterRegex(mRest_id);
                    if (localCode == null) {
                        mPresenter.getRestData(Const.APICategory.GET_REST_REFRESH, API3.Util.getGetRestAPI(mRest_id));
                    } else {
                        Toast.makeText(TenpoActivity.this, API3.Util.GetRestLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TenpoActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });

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
        BusHolder.get().unregister(self);
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
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
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("TenpoPage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
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
        return userData.getHls_movie();
    }

    private void preparePlayer(final Const.StreamRestViewHolder viewHolder, String path) {
        if (player == null) {
            mTracker = applicationGocci.getDefaultTracker();
            mTracker.setScreenName("TenpoPage");
            mTracker.send(new HitBuilders.EventBuilder().setAction("PlayCount").setCategory("Movie").setLabel(mPlayingPostId).build());

            player = new VideoPlayer(new HlsRendererBuilder(this, com.google.android.exoplayer.util.Util.getUserAgent(this, "Gocci"), path));
            player.addListener(new VideoPlayer.Listener() {
                @Override
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case VideoPlayer.STATE_BUFFERING:
                            break;
                        case VideoPlayer.STATE_ENDED:
                            player.seekTo(0);
                            mTracker = applicationGocci.getDefaultTracker();
                            mTracker.setScreenName("TenpoPage");
                            mTracker.send(new HitBuilders.EventBuilder().setAction("PlayCount").setCategory("Movie").setLabel(mPlayingPostId).build());
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
            final Const.StreamRestViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final Const.StreamRestViewHolder currentViewHolder = getPlayingViewHolder();
            if (mPlayBlockFlag) {
                return;
            }

            final String path = userData.getHls_movie();
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
    private Const.StreamRestViewHolder getPlayingViewHolder() {
        Const.StreamRestViewHolder viewHolder = null;
        if (mPlayingPostId != null) {
            for (Map.Entry<Const.StreamRestViewHolder, String> entry : mViewHolderHash.entrySet()) {
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
    public void onUserClick(String user_id, String user_name) {
        UserProfActivity.startUserProfActivity(user_id, user_name, this);
    }

    @Override
    public void onCommentClick(String post_id) {
        CommentActivity.startCommentActivity(post_id, false, this);
    }

    @Override
    public void onGochiTap() {
        setGochiLayout();
    }

    @Override
    public void onGochiClick(String post_id, Const.APICategory apiCategory) {
        if (apiCategory == Const.APICategory.SET_GOCHI) {
            API3.Util.SetGochiLocalCode postGochiLocalCode = API3.Impl.getRepository().SetGochiParameterRegex(post_id);
            if (postGochiLocalCode == null) {
                mPresenter.postGochi(Const.APICategory.SET_GOCHI, API3.Util.getSetGochiAPI(post_id), post_id);
            } else {
                Toast.makeText(this, API3.Util.SetGochiLocalCodeMessageTable(postGochiLocalCode), Toast.LENGTH_SHORT).show();
            }
        } else if (apiCategory == Const.APICategory.UNSET_GOCHI) {
            API3.Util.UnsetGochiLocalCode unpostGochiLocalCode = API3.Impl.getRepository().UnsetGochiParameterRegex(post_id);
            if (unpostGochiLocalCode == null) {
                mPresenter.postGochi(Const.APICategory.UNSET_GOCHI, API3.Util.getUnsetGochiAPI(post_id), post_id);
            } else {
                Toast.makeText(this, API3.Util.UnsetGochiLocalCodeMessageTable(unpostGochiLocalCode), Toast.LENGTH_SHORT).show();
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mShareShare = share;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 25);
            } else {
                Util.facebookVideoShare(this, shareDialog, share);
            }
        } else {
            Util.facebookVideoShare(this, shareDialog, share);
        }
    }

    @Override
    public void onTwitterShare(String share, String rest_name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mShareShare = share;
                mShareRestname = rest_name;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 26);
            } else {
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                if (session != null) {
                    TwitterAuthToken authToken = session.getAuthToken();
                    Util.twitterShare(this, "#" + rest_name.replaceAll("\\s+", "") + " #Gocci", share, authToken);
                } else {
                    Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            TwitterSession session = Twitter.getSessionManager().getActiveSession();
            if (session != null) {
                TwitterAuthToken authToken = session.getAuthToken();
                Util.twitterShare(this, "#" + rest_name.replaceAll("\\s+", "") + " #Gocci", share, authToken);
            } else {
                Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onInstaShare(String share) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mShareShare = share;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 27);
            } else {
                Util.instaVideoShare(this, share);
            }
        } else {
            Util.instaVideoShare(this, share);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 25:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.facebookVideoShare(this, shareDialog, mShareShare);
                } else {
                    Toast.makeText(TenpoActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                }
                break;
            case 26:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    if (session != null) {
                        TwitterAuthToken authToken = session.getAuthToken();
                        Util.twitterShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, authToken);
                    } else {
                        Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TenpoActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                }
                break;
            case 27:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.instaVideoShare(this, mShareShare);
                } else {
                    Toast.makeText(TenpoActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onHashHolder(Const.StreamRestViewHolder holder, String post_id) {
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
    public void showEmpty(Const.APICategory api, HeaderData restData) {
        mHeaderRestData = restData;
        switch (api) {
            case GET_REST_FIRST:
                mBackgroundImage.setImageResource(R.drawable.ic_background_login);
                mRestPageAdapter = new RestPageAdapter(this, mHeaderRestData, mTenpousers);
                mRestPageAdapter.setRestPageCallback(this);
                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoRecyclerView.setAdapter(mRestPageAdapter);
                break;
            case GET_REST_REFRESH:
                mTenpousers.clear();
                mPost_ids.clear();
                mRestPageAdapter.setData(mHeaderRestData);
                break;
        }
    }

    @Override
    public void hideEmpty() {

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
    public void showResult(Const.APICategory api, HeaderData restData, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mHeaderRestData = restData;
        mTenpousers.clear();
        mTenpousers.addAll(mPostData);
        mPost_ids.clear();
        mPost_ids.addAll(post_ids);
        switch (api) {
            case GET_REST_FIRST:
                Picasso.with(this).load(mTenpousers.get(0).getThumbnail()).into(mBackgroundImage);
                mRestPageAdapter = new RestPageAdapter(this, mHeaderRestData, mTenpousers);
                mRestPageAdapter.setRestPageCallback(this);
                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoRecyclerView.setAdapter(mRestPageAdapter);
                break;
            case GET_REST_REFRESH:
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mRestPageAdapter.setData(mHeaderRestData);
                break;
        }
    }

    @Override
    public void gochiSuccess(Const.APICategory api, String post_id) {

    }

    @Override
    public void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        PostData data = mTenpousers.get(mPost_ids.indexOf(post_id));
        if (api == Const.APICategory.SET_GOCHI) {
            data.setGochi_flag(false);
            data.setGochi_num(data.getGochi_num() - 1);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            data.setGochi_flag(true);
            data.setGochi_num(data.getGochi_num() + 1);
        }
        mRestPageAdapter.notifyItemChanged(mPost_ids.indexOf(post_id));
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
    }

    @Override
    public void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        PostData data = mTenpousers.get(mPost_ids.indexOf(post_id));
        if (api == Const.APICategory.SET_GOCHI) {
            data.setGochi_flag(false);
            data.setGochi_num(data.getGochi_num() - 1);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            data.setGochi_flag(true);
            data.setGochi_num(data.getGochi_num() + 1);
        }
        mRestPageAdapter.notifyItemChanged(mPost_ids.indexOf(post_id));
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
            case GET_REST_FIRST:
            case GET_REST_REFRESH:
                mPresenter.getRestData(event.api, API3.Util.getGetRestAPI(mRest_id));
                break;
            default:
                break;
        }
    }
}

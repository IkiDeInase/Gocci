package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.HlsRendererBuilder;
import com.inase.android.gocci.VideoPlayer.VideoPlayer;
import com.inase.android.gocci.View.CustomKenBurnsView;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.TextHttpResponseHandler;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;

public class FlexibleTenpoActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener, ObservableScrollViewCallbacks, AppBarLayout.OnOffsetChangedListener {

    private String mTenpoUrl;

    private MapView mapView;

    private int mWant_flag;
    private int mTotal_cheer_num;
    private TextView cheer_number;

    private ArrayList<PostData> mTenpousers = new ArrayList<PostData>();
    private TenpoAdapter mTenpoAdapter;
    private ObservableRecyclerView mTenpoRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private HeaderData headerTenpoData;

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

    private String addUrl;

    private Drawer result;

    private final FlexibleTenpoActivity self = this;

    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CustomKenBurnsView kenBurnsView;
    private AppBarLayout appBarLayout;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private long playerPosition;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;

    private static MobileAnalyticsManager analytics;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (isSee) {
                changeMovie();
            }
            if (mPlayingPostId != null || !isExist) {
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

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        setContentView(R.layout.activity_flexible_tenpo);

        Intent intent = getIntent();
        mRest_id = intent.getIntExtra("rest_id", 0);

        mTenpoUrl = Const.getRestpageAPI(mRest_id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(toolbar);

        mTenpoRecyclerView = (ObservableRecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mTenpoRecyclerView.setLayoutManager(mLayoutManager);
        mTenpoRecyclerView.setHasFixedSize(true);
        mTenpoRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTenpoRecyclerView.setScrollViewCallbacks(this);

        mTenpoAdapter = new TenpoAdapter(FlexibleTenpoActivity.this);

        getSignupAsync(FlexibleTenpoActivity.this);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
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
                                            player.selectTrack(VideoPlayer.TYPE_AUDIO, -1);
                                        }
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(FlexibleTenpoActivity.this, 0);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_mute)));

                                        if (player != null) {
                                            player.selectTrack(VideoPlayer.TYPE_AUDIO, 0);
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

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(intent.getStringExtra("rest_name"));

        kenBurnsView = (CustomKenBurnsView) findViewById(R.id.background_Image);
        kenBurnsView.setTransitionGenerator(new RandomTransitionGenerator(4500, new AccelerateDecelerateInterpolator()));

        cheer_number = (TextView) findViewById(R.id.cheer_number);
        cheer_number.setText(String.valueOf(mTotal_cheer_num));
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Util.getConnectedState(FlexibleTenpoActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(FlexibleTenpoActivity.this);
                } else {
                    Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    @Override
    public final void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        releasePlayer();
        super.onDestroy();

    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public final void onPause() {
        if (mapView != null) {
            mapView.onPause();
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
        audioCapabilitiesReceiver.unregister();

        appBarLayout.removeOnOffsetChangedListener(this);

    }

    @Override
    public final void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);
        if (mapView != null) {
            mapView.onResume();
        }

        audioCapabilitiesReceiver.register();
        appBarLayout.addOnOffsetChangedListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            if (mPlayingPostId != null && isSee) {
                this.audioCapabilities = audioCapabilities;
                releasePlayer();
                if (Util.isMovieAutoPlay(this)) {
                    preparePlayer(getPlayingViewHolder(), getVideoPath());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
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
        Snackbar.make(coordinatorLayout, event.mMessage, android.support.design.widget.Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mTenpoUrl, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("restaurants");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerTenpoData = HeaderData.createTenpoHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mTenpousers.add(PostData.createPostData(post));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!mTenpousers.isEmpty()) {
                    Picasso.with(context).load(mTenpousers.get(0).getThumbnail()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            kenBurnsView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                } else {
                    kenBurnsView.setImageResource(R.drawable.ic_background_login);
                }

                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoRecyclerView.setAdapter(mTenpoAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mTenpoUrl, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mTenpousers.clear();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("restaurants");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerTenpoData = HeaderData.createTenpoHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mTenpousers.add(PostData.createPostData(post));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTenpoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private String getVideoPath() {
        final int position = mTenpoRecyclerView.getChildAdapterPosition(mTenpoRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mTenpoAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        //return mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        return userData.getMovie();
    }

    private void preparePlayer(final Const.ExoViewHolder viewHolder, String path) {
        if (player == null) {
            player = new VideoPlayer(new HlsRendererBuilder(this, com.google.android.exoplayer.util.Util.getUserAgent(this, "Gocci"), path,
                    audioCapabilities));
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
                    playerNeedsPrepare = true;
                }

                @Override
                public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
                    viewHolder.mVideoThumbnail.setVisibility(View.GONE);
                    viewHolder.videoFrame.setAspectRatio(
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
        player.setSurface(viewHolder.movie.getHolder().getSurface());
        player.setPlayWhenReady(true);

        if (SavedData.getSettingMute(this) == -1) {
            player.selectTrack(VideoPlayer.TYPE_AUDIO, -1);
        } else {
            player.selectTrack(VideoPlayer.TYPE_AUDIO, 0);
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
        if (mTenpoAdapter.isEmpty()) {
            return;
        }
        if (position - 1 < 0) {
            return;
        }

        final PostData userData = mTenpoAdapter.getItem(position - 1);
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
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    public class TenpoHeaderViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        private TextView tenpo_category;
        private RippleView checkRipple;
        private ImageView check_Image;
        private TextView check_text;
        private RippleView callRipple;
        private RippleView gohereRipple;
        private RippleView etcRipple;
        private MapView mMapView;
        private GoogleMap mGoogleMap;

        public TenpoHeaderViewHolder(View view) {
            super(view);
            tenpo_category = (TextView) view.findViewById(R.id.category);
            checkRipple = (RippleView) view.findViewById(R.id.checkRipple);
            check_Image = (ImageView) view.findViewById(R.id.check_image);
            check_text = (TextView) view.findViewById(R.id.check_text);
            callRipple = (RippleView) view.findViewById(R.id.callRipple);
            gohereRipple = (RippleView) view.findViewById(R.id.gohereRipple);
            etcRipple = (RippleView) view.findViewById(R.id.etcRipple);
            mMapView = (MapView) view.findViewById(R.id.map);

            if (mMapView != null) {
                mMapView.onCreate(null);
                mMapView.onResume();
                mapView = mMapView;
                mMapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            mGoogleMap = googleMap;

            LatLng lng = new LatLng(headerTenpoData.getLat(), headerTenpoData.getLon());
            mGoogleMap.getUiSettings().setCompassEnabled(false);
            mGoogleMap.addMarker(new MarkerOptions().position(lng).title(headerTenpoData.getRestname()));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lng)
                    .zoom(15)
                    .tilt(50)
                    .build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public class TenpoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_TENPO_HEADER = 0;
        public static final int TYPE_POST = 1;

        private Context context;

        public TenpoAdapter(Context context) {
            this.context = context;
        }

        public PostData getItem(int position) {
            return mTenpousers.get(position);
        }

        public boolean isEmpty() {
            return mTenpousers.isEmpty();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_TENPO_HEADER;
            } else {
                return TYPE_POST;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (TYPE_TENPO_HEADER == viewType) {
                final View view = LayoutInflater.from(context).inflate(R.layout.cell_tenpo_header, parent, false);
                return new TenpoHeaderViewHolder(view);
            } else {
                final View view = LayoutInflater.from(context).inflate(R.layout.cell_exo_timeline, parent, false);
                return new Const.ExoViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (TYPE_TENPO_HEADER == viewType) {
                bindHeader((TenpoHeaderViewHolder) holder);
            } else {
                PostData users = mTenpousers.get(position - 1);
                bindPost((Const.ExoViewHolder) holder, position - 1, users);
            }
        }

        private void bindHeader(final TenpoHeaderViewHolder holder) {
            if (headerTenpoData.getWant_flag() == 0) {
                holder.check_Image.setImageResource(R.drawable.ic_like_white);
                holder.check_text.setText(getString(R.string.add_want));
            } else {
                holder.check_Image.setImageResource(R.drawable.ic_favorite_orange);
                holder.check_text.setText(getString(R.string.remove_want));
            }

            holder.checkRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.check_text.getText().toString().equals(getString(R.string.add_want))) {
                        holder.check_Image.setImageResource(R.drawable.ic_favorite_orange);
                        holder.check_text.setText(getString(R.string.remove_want));

                        Util.wantAsync(context, headerTenpoData);
                    } else {
                        holder.check_Image.setImageResource(R.drawable.ic_like_white);
                        holder.check_text.setText(getString(R.string.add_want));

                        Util.unwantAsync(context, headerTenpoData);
                    }
                }
            });
            holder.callRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    Intent intent = new Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + headerTenpoData.getTell()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            holder.gohereRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + headerTenpoData.getLat() + "," + headerTenpoData.getLon() + "&mode=w");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            holder.etcRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (!headerTenpoData.getHomepage().equals("none")) {
                        new MaterialDialog.Builder(FlexibleTenpoActivity.this)
                                .items(R.array.list_tenpo_menu)
                                .itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(com.afollestad.materialdialogs.MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                        if (charSequence.toString().equals(getString(R.string.seeHomepage))) {
                                            Uri uri = Uri.parse(headerTenpoData.getHomepage());
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                        }
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.nothing_etc), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (holder.mGoogleMap == null) {
                holder.mGoogleMap = holder.mMapView.getMap();
            }
            if (holder.mGoogleMap != null) {
                //move map to the 'location'
                LatLng lng = new LatLng(headerTenpoData.getLat(), headerTenpoData.getLon());
                holder.mGoogleMap.getUiSettings().setCompassEnabled(false);
                holder.mGoogleMap.addMarker(new MarkerOptions().position(lng).title(headerTenpoData.getRestname()));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(lng)
                        .zoom(15)
                        .tilt(50)
                        .build();
                holder.mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            holder.tenpo_category.setText(headerTenpoData.getRest_category());
        }

        private void bindPost(final Const.ExoViewHolder holder, final int position, final PostData user) {
            holder.user_name.setText(user.getUsername());

            holder.datetime.setText(user.getPost_date());

            if (!user.getMemo().equals("none")) {
                holder.comment.setText(user.getMemo());
            } else {
                holder.comment.setText("");
            }

            Picasso.with(FlexibleTenpoActivity.this)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.circleImage);

            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getPost_user_id(), user.getUsername(), FlexibleTenpoActivity.this);
                }
            });

            holder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getPost_user_id(), user.getUsername(), FlexibleTenpoActivity.this);
                }
            });

            holder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(FlexibleTenpoActivity.this, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.violation:
                                    Util.setViolateDialog(FlexibleTenpoActivity.this, user.getPost_id());
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                }
            });
            Picasso.with(context)
                    .load(user.getThumbnail())
                    .placeholder(R.color.videobackground)
                    .into(holder.mVideoThumbnail);
            holder.mVideoThumbnail.setVisibility(View.VISIBLE);

            holder.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player != null) {
                        if (player.getPlayerControl().isPlaying()) {
                            player.getPlayerControl().pause();
                        } else {
                            player.getPlayerControl().start();
                        }
                    } else {
                        if (!Util.isMovieAutoPlay(context)) {
                            releasePlayer();
                            preparePlayer(holder, user.getMovie());
                        }
                    }
                }
            });


            holder.rest_name.setText(user.getRestname());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getCategory().equals(getString(R.string.nothing_tag))) {
                holder.category.setText(user.getCategory());
            } else {
                holder.category.setText("　　　　");
            }
            if (!user.getTag().equals(getString(R.string.nothing_tag))) {
                holder.atmosphere.setText(user.getTag());
            } else {
                holder.atmosphere.setText("　　　　");
            }
            if (!user.getValue().equals("0")) {
                holder.value.setText(user.getValue() + "円");
            } else {
                holder.value.setText("　　　　");
            }

            final int currentgoodnum = user.getGochi_num();
            final int currentcommentnum = user.getComment_num();

            holder.likes.setText(String.valueOf(currentgoodnum));
            holder.comments.setText(String.valueOf(currentcommentnum));

            if (user.getGochi_flag() == 0) {
                holder.likes_ripple.setClickable(true);
                holder.likes_Image.setImageResource(R.drawable.ic_icon_beef);

                holder.likes_ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setGochi_flag(1);
                        user.setGochi_num(currentgoodnum + 1);

                        holder.likes.setText(String.valueOf((currentgoodnum + 1)));
                        holder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                        holder.likes_ripple.setClickable(false);

                        Util.postGochiAsync(context, user);
                    }
                });
            } else {
                holder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                holder.likes_ripple.setClickable(false);
            }

            holder.comments_ripple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    CommentActivity.startCommentActivity(Integer.parseInt(user.getPost_id()), FlexibleTenpoActivity.this);
                }
            });

            holder.share_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.getShareTransfer() != null) {
                        new BottomSheet.Builder(context, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.facebook_share:
                                        Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                        Util.facebookVideoShare(context, shareDialog, user.getShare());
                                        break;
                                    case R.id.twitter_share:
                                        Util.twitterShare(context, holder.mVideoThumbnail, user.getRestname());
                                        break;
                                    case R.id.other_share:
                                        Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                        Util.instaVideoShare(context, user.getRestname(), user.getShare());
                                        break;
                                    case R.id.close:
                                        dialog.dismiss();
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(FlexibleTenpoActivity.this, getString(R.string.preparing_share_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mViewHolderHash.put(holder, user.getPost_id());
        }

        @Override
        public int getItemCount() {
            return mTenpousers.size() + 1;
        }
    }
}

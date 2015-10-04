package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.inase.android.gocci.application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.HlsRendererBuilder;
import com.inase.android.gocci.VideoPlayer.VideoPlayer;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.CommentUserData;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class CommentActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener, ObservableScrollViewCallbacks, AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.list)
    ObservableRecyclerView mCommentRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
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
                        // Do something
                        postCommentAsync(CommentActivity.this, Const.getPostCommentAPI(mPost_id, input.toString()));
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

    private PostData headerUser;
    private String mPost_id;
    private String mCommentUrl;
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

        mCommentAdapter = new CommentAdapter(CommentActivity.this);

        mCommentUrl = Const.getCommentAPI(mPost_id);
        getSignupAsync(this);

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(CommentActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(CommentActivity.this);
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
                    preparePlayer(getPlayingViewHolder(), headerUser.getMovie());
                }
            }
        } else {
            player.setBackgrounded(false);
        }

        mAppBar.addOnOffsetChangedListener(this);
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

        mAppBar.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
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
                preparePlayer(getPlayingViewHolder(), headerUser.getMovie());
            }
        }
        player.setBackgrounded(false);
    }

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mCommentUrl, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                mSwipeContainer.setRefreshing(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject json = new JSONObject(responseString);
                    JSONArray array = new JSONArray(json.getString("comments"));
                    JSONObject obj = new JSONObject(json.getString("post"));

                    headerUser = PostData.createPostData(obj);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        String comment = jsonObject.getString("comment");
                        if (!comment.equals("none")) {
                            mCommentusers.add(HeaderData.createCommentHeaderData(jsonObject));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.reverse(mCommentusers);

                mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                //changeMovie();
            }

            @Override
            public void onFinish() {
                mSwipeContainer.setRefreshing(false);
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mCommentUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mCommentusers.clear();
                try {
                    JSONObject json = new JSONObject(responseString);
                    JSONArray array = new JSONArray(json.getString("comments"));
                    JSONObject obj = new JSONObject(json.getString("post"));

                    headerUser = PostData.createPostData(obj);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        String comment = jsonObject.getString("comment");
                        if (!comment.equals("none")) {
                            mCommentusers.add(HeaderData.createCommentHeaderData(jsonObject));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.reverse(mCommentusers);

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
//                mTimelineDialog.dismiss();
                mSwipeContainer.setRefreshing(false);
            }
        });
    }

    private void postCommentAsync(final Context context, String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                mSwipeContainer.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
                Const.asyncHttpClient.get(context, mCommentUrl, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        mCommentusers.clear();
                        try {
                            JSONObject json = new JSONObject(responseString);
                            JSONArray array = new JSONArray(json.getString("comments"));
                            JSONObject obj = new JSONObject(json.getString("post"));

                            headerUser = PostData.createPostData(obj);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                String comment = jsonObject.getString("comment");
                                if (!comment.equals("none")) {
                                    mCommentusers.add(HeaderData.createCommentHeaderData(jsonObject));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Collections.reverse(mCommentusers);

                        mPlayingPostId = null;
                        mViewHolderHash.clear();
                        mCommentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                        mCommentAdapter.notifyDataSetChanged();
                        //changeMovie();
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
//                mTimelineDialog.dismiss();
                mSwipeContainer.setRefreshing(false);
            }
        });
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
        if (!headerUser.getPost_id().equals(mPlayingPostId)) {

            // 前回の動画再生停止処理
            final Const.ExoViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = headerUser.getPost_id();
            final Const.ExoViewHolder currentViewHolder = getPlayingViewHolder();
            if (mPlayBlockFlag) {
                Log.d("DEBUG", "startMovie play block status");
                return;
            }

            final String path = headerUser.getMovie();
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeContainer.setEnabled(i == 0);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.comment_user_image)
        ImageView mCommentUserImage;
        @Bind(R.id.user_name)
        TextView mUserName;
        @Bind(R.id.date_time)
        TextView mDateTime;
        @Bind(R.id.user_comment)
        TextView mUserComment;
        @Bind(R.id.re_user)
        LinearLayout mReUser;
        @Bind(R.id.reply_button)
        ImageButton mReplyButton;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_COMMENT_HEADER = 0;
        public static final int TYPE_COMMENT = 1;

        private Context context;

        public CommentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_COMMENT_HEADER;
            } else {
                return TYPE_COMMENT;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (TYPE_COMMENT_HEADER == viewType) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_comment_header, parent, false);
                return new Const.ExoViewHolder(view);
            } else {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_comment_activity, parent, false);
                return new CommentViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (TYPE_COMMENT_HEADER == viewType) {
                bindHeader((Const.ExoViewHolder) holder, headerUser);
            } else {
                HeaderData users = mCommentusers.get(position - 1);
                bindComment((CommentViewHolder) holder, users);
            }
        }

        private void bindHeader(final Const.ExoViewHolder holder, final PostData user) {
            holder.mUserName.setText(user.getUsername());
            holder.mTimeText.setText(user.getPost_date());

            if (!user.getMemo().equals("none")) {
                holder.mComment.setText(user.getMemo());
            } else {
                holder.mComment.setText("");
            }

            Picasso.with(context)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.mCircleImage);

            holder.mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getPost_user_id(), user.getUsername(), CommentActivity.this);
                }
            });

            holder.mCircleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getPost_user_id(), user.getUsername(), CommentActivity.this);
                }
            });

            holder.mMenuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(context, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.violation:
                                    Util.setViolateDialog(context, user.getPost_id());
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

            holder.mVideoFrame.setOnClickListener(new View.OnClickListener() {
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

            holder.mRestname.setText(user.getRestname());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getCategory().equals(getString(R.string.nothing_tag))) {
                holder.mCategory.setText(user.getCategory());
            } else {
                holder.mCategory.setText("　　　　");
            }
            if (!user.getTag().equals(getString(R.string.nothing_tag))) {
                holder.mMood.setText(user.getTag());
            } else {
                holder.mMood.setText("　　　　");
            }
            if (!user.getValue().equals("0")) {
                holder.mValue.setText(user.getValue() + "円");
            } else {
                holder.mValue.setText("　　　　");
            }

            //リップルエフェクトを見せてからIntentを飛ばす
            holder.mTenpoRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getPost_rest_id(), user.getRestname(), CommentActivity.this);
                }
            });

            final int currentgoodnum = user.getGochi_num();
            final int currentcommentnum = user.getComment_num();

            holder.mLikesNumber.setText(String.valueOf(currentgoodnum));
            holder.mCommentsNumber.setText(String.valueOf(currentcommentnum));

            if (user.getGochi_flag() == 0) {
                holder.mLikesRipple.setClickable(true);
                holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef);

                holder.mLikesRipple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setGochi_flag(1);
                        user.setGochi_num(currentgoodnum + 1);

                        holder.mLikesNumber.setText(String.valueOf((currentgoodnum + 1)));
                        holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
                        holder.mLikesRipple.setClickable(false);

                        Util.postGochiAsync(CommentActivity.this, user);
                    }
                });
            } else {
                holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
                holder.mLikesRipple.setClickable(false);
            }

            holder.mCommentsRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    mCommentButton.performClick();
                }
            });

            holder.mShareRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.getShareTransfer() != null) {
                        new BottomSheet.Builder(context, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.facebook_share:
                                        Toast.makeText(context, getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                        Util.facebookVideoShare(context, shareDialog, user.getShare());
                                        break;
                                    case R.id.twitter_share:
                                        Util.twitterShare(context, holder.mVideoThumbnail, user.getRestname());
                                        break;
                                    case R.id.other_share:
                                        Toast.makeText(context, getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                        Util.instaVideoShare(context, user.getRestname(), user.getShare());
                                        break;
                                    case R.id.close:
                                        dialog.dismiss();
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(context, getString(R.string.preparing_share_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mViewHolderHash.put(holder, user.getPost_id());
            changeMovie();
        }

        private void bindComment(final CommentViewHolder holder, final HeaderData users) {
            Picasso.with(context)
                    .load(users.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.mCommentUserImage);
            holder.mUserName.setText(users.getUsername());
            holder.mDateTime.setText(users.getComment_date());
            holder.mUserComment.setText(users.getComment());

            holder.mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(users.getComment_user_id(), users.getUsername(), CommentActivity.this);
                }
            });

            holder.mCommentUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(users.getComment_user_id(), users.getUsername(), CommentActivity.this);
                }
            });

            holder.mReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, Arrays.asList(users.getComment_user_data()).toString(), Toast.LENGTH_SHORT).show();
                    final StringBuilder user_name = new StringBuilder();
                    final StringBuilder user_id = new StringBuilder();
                    user_name.append("@" + users.getUsername() + " ");
                    user_id.append(users.getComment_user_id());
                    for (CommentUserData data : users.getComment_user_data()) {
                        user_name.append("@" + data.getUserName() + " ");
                        user_id.append("," + data.getUser_id());
                    }
                    new MaterialDialog.Builder(CommentActivity.this)
                            .title(getString(R.string.comment))
                            .titleColorRes(R.color.namegrey)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
                            .inputMaxLength(140)
                            .input(null, user_name.toString(), false, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // Do something
                                    String comment = input.toString().replace(user_name.toString(), "");
                                    postCommentAsync(CommentActivity.this, Const.getPostCommentWithNoticeAPI(mPost_id, comment, user_id.toString()));
                                }
                            })
                            .widgetColorRes(R.color.gocci_header)
                            .positiveText(getString(R.string.post_comment))
                            .positiveColorRes(R.color.gocci_header)
                            .show();
                }
            });

            if (!users.getComment_user_data().isEmpty()) {
                for (final CommentUserData data : users.getComment_user_data()) {
                    TextView userText = new TextView(context);
                    userText.setText(" @" + data.getUserName());
                    userText.setSingleLine();
                    userText.setTextSize(12);
                    userText.setTextColor(getResources().getColor(R.color.gocci_header));
                    userText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FlexibleUserProfActivity.startUserProfActivity(data.getUser_id(), data.getUserName(), CommentActivity.this);
                        }
                    });
                    holder.mReUser.addView(userText, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mCommentusers.size() + 1;
        }

    }
}

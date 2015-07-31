package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.coremedia.iso.boxes.Container;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.HlsRendererBuilder;
import com.inase.android.gocci.VideoPlayer.VideoPlayer;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;

public class FlexibleUserProfActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener {

    private String mProfUrl;

    private RecyclerView mUserProfRecyclerView;
    private UserProfAdapter mUserProfAdapter;
    private ArrayList<PostData> mUserProfusers = new ArrayList<PostData>();
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private HeaderData headerUserData;

    private int mUser_id;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private AttributeSet mVideoAttr;

    private Point mDisplaySize;
    private CacheManager mCacheManager;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.ExoViewHolder, String> mViewHolderHash;  // Value: PosterId

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private final FlexibleUserProfActivity self = this;

    private Drawer result;

    private Toolbar toolbar;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private long playerPosition;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.e("DEBUG", "onGlobalLayout called: " + mPlayingPostId);
            changeMovie();
            Log.e("DEBUG", "onGlobalLayout  changeMovie called: " + mPlayingPostId);
            if (mPlayingPostId != null) {
                mUserProfRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FlexibleUserProfActivity activity
                    = (FlexibleUserProfActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_TIMELINE:
                    activity.startActivity(new Intent(activity, GocciTimelineActivity.class));
                    activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    break;
                case Const.INTENT_TO_MYPAGE:
                    GocciMyprofActivity.startMyProfActivity(activity);
                    break;
                case Const.INTENT_TO_RESTPAGE:
                    FlexibleTenpoActivity.startTenpoActivity(msg.arg1, activity);
                    break;
                case Const.INTENT_TO_COMMENT:
                    CommentActivity.startCommentActivity(msg.arg1, activity);
                    break;
                case Const.INTENT_TO_POLICY:
                    WebViewActivity.startWebViewActivity(1, activity);
                    break;
                case Const.INTENT_TO_LICENSE:
                    WebViewActivity.startWebViewActivity(2, activity);
                    break;
                case Const.INTENT_TO_ADVICE:
                    Util.setAdviceDialog(activity);
                    break;
                case Const.INTENT_TO_LIST:
                    ListActivity.startListActivity(msg.arg1, 0, msg.arg2, activity);
                    break;
            }
        }
    };

    public static void startUserProfActivity(int user_id, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, FlexibleUserProfActivity.class);
        intent.putExtra("user_id", user_id);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheManager = CacheManager.getInstance(getApplicationContext());
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        setContentView(R.layout.activity_flexible_user_prof);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(FlexibleUserProfActivity.this, "シェアが完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(FlexibleUserProfActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(FlexibleUserProfActivity.this, "シェアに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        Intent userintent = getIntent();
        mUser_id = userintent.getIntExtra("user_id", 0);

        mProfUrl = Const.getUserpageAPI(mUser_id);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(new DrawerProfHeader(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイページ").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withCheckable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("要望を送る").withIcon(GoogleMaterial.Icon.gmd_send).withCheckable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName("利用規約とポリシー").withIcon(GoogleMaterial.Icon.gmd_visibility).withCheckable(false).withIdentifier(4),
                        new PrimaryDrawerItem().withName("ライセンス情報").withIcon(GoogleMaterial.Icon.gmd_build).withCheckable(false).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_POLICY, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_LICENSE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
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

        mUserProfRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mUserProfRecyclerView.setLayoutManager(mLayoutManager);
        mUserProfRecyclerView.setHasFixedSize(true);
        mUserProfRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mUserProfRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    // スクロールしていない
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //mBusy = false;
                        Log.d("DEBUG", "SCROLL_STATE_IDLE");
                        changeMovie();
                        break;
                    // スクロール中
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //mBusy = true;
                        Log.d("DEBUG", "SCROLL_STATE_DRAGGING");
                        break;
                    // はじいたとき
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //mBusy = true;
                        Log.d("DEBUG", "SCROLL_STATE_SETTLING");
                        break;
                }

                visibleItemCount = mUserProfRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Util.getConnectedState(FlexibleUserProfActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(FlexibleUserProfActivity.this);
                } else {
                    Toast.makeText(FlexibleUserProfActivity.this, "通信に失敗しました", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        getSignupAsync(FlexibleUserProfActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.get().unregister(self);
        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        audioCapabilitiesReceiver.unregister();
        getPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(self);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public final void onDestroy() {
        releasePlayer();
        super.onDestroy();

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

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mUserProfRecyclerView, event.mMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            if (mPlayingPostId != null) {
                this.audioCapabilities = audioCapabilities;
                releasePlayer();
                preparePlayer(getPlayingViewHolder(), getVideoPath());
            }
        } else {
            player.setBackgrounded(false);
        }
    }

    private String getVideoPath() {
        final int position = mUserProfRecyclerView.getChildAdapterPosition(mUserProfRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mUserProfAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        //return mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        return userData.getMovie();
    }

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mProfUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleUserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerUserData = HeaderData.createUserHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mUserProfusers.add(PostData.createPostData(post));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                toolbar.setTitle(headerUserData.getUsername());
                mUserProfRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mUserProfAdapter = new UserProfAdapter(FlexibleUserProfActivity.this);
                mUserProfRecyclerView.setAdapter(mUserProfAdapter);
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mProfUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleUserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mUserProfusers.clear();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerUserData = HeaderData.createUserHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mUserProfusers.add(PostData.createPostData(post));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mUserProfRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mUserProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });
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
    }

    private void releasePlayer() {
        if (player != null) {
            //playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    private void changeMovie() {
        Log.e("DEBUG", "changeMovie called");
        // TODO:実装
        final int position = mUserProfRecyclerView.getChildAdapterPosition(mUserProfRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        if (mUserProfAdapter.isEmpty()) {
            return;
        }
        if (position - 1 < 0) {
            return;
        }

        final PostData userData = mUserProfAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            Log.d("DEBUG", "postId change");

            mPlayingPostId = userData.getPost_id();
            final Const.ExoViewHolder currentViewHolder = getPlayingViewHolder();
            Log.d("DEBUG", "MOVIE::changeMovie 動画再生処理開始 postId:" + mPlayingPostId);
            if (mPlayBlockFlag) {
                Log.d("DEBUG", "startMovie play block status");
                return;
            }

            final String path = userData.getMovie();
            Log.e("DEBUG", "[ProgressBar GONE] cache Path: " + path);
            releasePlayer();
            preparePlayer(currentViewHolder, path);
        }
    }

    /**
     * 現在再生中のViewHolderを取得
     *
     * @return
     */
    private Const.ExoViewHolder getPlayingViewHolder() {
        Const.ExoViewHolder viewHolder = null;
        Log.d("DEBUG", "getPlayingViewHolder :" + mPlayingPostId);
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

    static class UserProfHeaderViewHolder extends RecyclerView.ViewHolder {
        private ImageView userprof_background;
        private ImageView userprof_picture;
        private TextView userprof_username;
        private RippleView userprof_follow;
        private TextView follow_num;
        private TextView follower_num;
        private TextView usercheer_num;
        private TextView follow_text;
        private RippleView followRipple;
        private RippleView followerRipple;
        private RippleView usercheerRipple;

        public UserProfHeaderViewHolder(View view) {
            super(view);
            userprof_background = (ImageView) view.findViewById(R.id.userprof_background);
            userprof_picture = (ImageView) view.findViewById(R.id.userprof_picture);
            userprof_username = (TextView) view.findViewById(R.id.userprof_username);
            userprof_follow = (RippleView) view.findViewById(R.id.userprof_follow);
            follow_num = (TextView) view.findViewById(R.id.follow_num);
            follower_num = (TextView) view.findViewById(R.id.follower_num);
            usercheer_num = (TextView) view.findViewById(R.id.usercheer_num);
            follow_text = (TextView) view.findViewById(R.id.followText);
            followRipple = (RippleView) view.findViewById(R.id.followRipple);
            followerRipple = (RippleView) view.findViewById(R.id.followerRipple);
            usercheerRipple = (RippleView) view.findViewById(R.id.usercheerRipple);
        }
    }

    public class UserProfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;

        public static final int TYPE_PROFILE_HEADER = 0;
        public static final int TYPE_POST = 1;

        public UserProfAdapter(Context context) {
            mContext = context;
        }

        public PostData getItem(int position) {
            return mUserProfusers.get(position);
        }

        public boolean isEmpty() {
            return mUserProfusers.isEmpty();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PROFILE_HEADER;
            } else {
                return TYPE_POST;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (TYPE_PROFILE_HEADER == viewType) {
                final View view = LayoutInflater.from(mContext).inflate(R.layout.view_header_userprof, parent, false);
                return new UserProfHeaderViewHolder(view);
            } else {
                final View view = LayoutInflater.from(mContext).inflate(R.layout.cell_exo_timeline, parent, false);
                return new Const.ExoViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (TYPE_PROFILE_HEADER == viewType) {
                bindHeader((UserProfHeaderViewHolder) holder);
            } else {
                PostData users = mUserProfusers.get(position - 1);
                bindPost((Const.ExoViewHolder) holder, position, users);
            }
        }

        private void bindHeader(final UserProfHeaderViewHolder holder) {
            holder.userprof_username.setText(headerUserData.getUsername());

            holder.follow_num.setText(String.valueOf(headerUserData.getFollow_num()));
            holder.follower_num.setText(String.valueOf(headerUserData.getFollower_num()));
            holder.usercheer_num.setText(String.valueOf(headerUserData.getCheer_num()));

            holder.followRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_LIST, headerUserData.getUser_id(), Const.CATEGORY_FOLLOW, FlexibleUserProfActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            holder.followerRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_LIST, headerUserData.getUser_id(), Const.CATEGORY_FOLLOWER, FlexibleUserProfActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            holder.usercheerRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_LIST, headerUserData.getUser_id(), Const.CATEGORY_USER_CHEER, FlexibleUserProfActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            Picasso.with(FlexibleUserProfActivity.this)
                    .load(headerUserData.getProfile_img())
                    .fit()
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.userprof_picture);

            if (headerUserData.getFollow_flag() == 0) {
                holder.follow_text.setText("フォローする");
            } else {
                holder.follow_text.setText("フォロー解除する");
            }

            if (headerUserData.getUsername().equals(SavedData.getServerName(mContext))) {
                holder.follow_text.setText("これはあなたです");
            }

            holder.userprof_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //お気に入りするときの処理
                    switch (holder.follow_text.getText().toString()) {
                        case "フォローする":
                            Util.followAsync(FlexibleUserProfActivity.this, headerUserData);
                            holder.follow_text.setText("フォロー解除する");
                            break;
                        case "フォロー解除する":
                            Util.unfollowAsync(FlexibleUserProfActivity.this, headerUserData);
                            holder.follow_text.setText("フォローする");
                            break;
                        case "これはあなたです":
                            break;
                    }

                }
            });
        }

        private void bindPost(final Const.ExoViewHolder viewHolder, final int position, final PostData user) {
            viewHolder.user_name.setText(user.getUsername());
            viewHolder.datetime.setText(user.getPost_date());
            viewHolder.comment.setText(user.getMemo());

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.circleImage);

            viewHolder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(FlexibleUserProfActivity.this, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.violation:
                                    Util.setViolateDialog(FlexibleUserProfActivity.this, user.getPost_id());
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                }
            });

            Picasso.with(mContext)
                    .load(user.getThumbnail())
                    .placeholder(R.color.videobackground)
                    .into(viewHolder.mVideoThumbnail);
            viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);

            viewHolder.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player != null) {
                        if (player.getPlayerControl().isPlaying()) {
                            player.getPlayerControl().pause();
                        } else {
                            player.getPlayerControl().start();
                            viewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        releasePlayer();
                        preparePlayer(viewHolder, user.getMovie());
                    }
                }
            });

            viewHolder.rest_name.setText(user.getRestname());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getCategory().equals("none")) {
                viewHolder.category.setText(user.getCategory());
            } else {
                viewHolder.category.setText("タグなし");
            }
            if (!user.getTag().equals("none")) {
                viewHolder.atmosphere.setText(user.getTag());
            } else {
                viewHolder.atmosphere.setText("タグなし");
            }
            if (!user.getValue().equals("0")) {
                viewHolder.value.setText(user.getValue());
            } else {
                viewHolder.value.setText("タグなし");
            }

            //リップルエフェクトを見せてからIntentを飛ばす
            viewHolder.tenpoRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_RESTPAGE, user.getPost_rest_id(), user.getPost_rest_id(), FlexibleUserProfActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            final int currentgoodnum = user.getGochi_num();
            final int currentcommentnum = user.getComment_num();

            viewHolder.likes.setText(String.valueOf(currentgoodnum));
            viewHolder.comments.setText(String.valueOf(currentcommentnum));

            if (user.getGochi_flag() == 0) {
                viewHolder.likes_ripple.setClickable(true);
                viewHolder.likes_Image.setImageResource(R.drawable.ic_icon_beef);

                viewHolder.likes_ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("いいねをクリック", user.getPost_id());

                        user.setGochi_flag(1);
                        user.setGochi_num(currentgoodnum + 1);
                        viewHolder.likes.setText(String.valueOf(currentgoodnum + 1));
                        viewHolder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                        viewHolder.likes_ripple.setClickable(false);

                        Util.postGochiAsync(FlexibleUserProfActivity.this, user);
                    }
                });
            } else {
                viewHolder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                viewHolder.likes_ripple.setClickable(false);
            }

            viewHolder.comments_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_COMMENT, Integer.parseInt(user.getPost_id()), Integer.parseInt(user.getPost_id()), FlexibleUserProfActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            viewHolder.share_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.transferUtility != null) {
                        new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.facebook_share:
                                        Toast.makeText(FlexibleUserProfActivity.this, "シェアの準備をしています", Toast.LENGTH_LONG).show();
                                        Util.facebookVideoShare(FlexibleUserProfActivity.this, shareDialog, user.getShare());
                                        break;
                                    case R.id.twitter_share:
                                        Util.twitterShare(FlexibleUserProfActivity.this, viewHolder.mVideoThumbnail, user.getRestname());
                                        break;
                                    case R.id.other_share:
                                        Toast.makeText(FlexibleUserProfActivity.this, "シェアの準備をしています", Toast.LENGTH_LONG).show();
                                        Util.instaVideoShare(FlexibleUserProfActivity.this, user.getRestname(), user.getShare());
                                        break;
                                    case R.id.close:
                                        dialog.dismiss();
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(FlexibleUserProfActivity.this, "もうちょっと待ってから押してみましょう", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mViewHolderHash.put(viewHolder, user.getPost_id());
        }

        @Override
        public int getItemCount() {
            return mUserProfusers.size() + 1;
        }

    }
}

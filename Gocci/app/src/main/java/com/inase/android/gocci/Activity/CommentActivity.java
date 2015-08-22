package com.inase.android.gocci.Activity;

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
import android.widget.AdapterView;
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
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.HlsRendererBuilder;
import com.inase.android.gocci.VideoPlayer.VideoPlayer;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;

public class CommentActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener, ObservableScrollViewCallbacks, AppBarLayout.OnOffsetChangedListener {

    private ObservableRecyclerView mCommentRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<HeaderData> mCommentusers = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressWheel mCommentProgress;

    private FloatingActionButton mCommentButton;

    private CommentActivity self = this;

    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

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

    private long playerPosition;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;

    private static MobileAnalyticsManager analytics;

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
        intent.putExtra("title", "コメント");
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
                Toast.makeText(CommentActivity.this, "シェアが完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(CommentActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(CommentActivity.this, "シェアに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        mPost_id = String.valueOf(intent.getIntExtra("post_id", 0));

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (title.equals("Activity.GocciMyprofActivity")) {
            getSupportActionBar().setTitle("マイページ");
        } else {
            getSupportActionBar().setTitle("コメント");
        }

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(new DrawerProfHeader(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイページ").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withCheckable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("要望を送る").withIcon(GoogleMaterial.Icon.gmd_send).withCheckable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName("設定").withIcon(GoogleMaterial.Icon.gmd_settings).withCheckable(false).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
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

        mCommentProgress = (ProgressWheel) findViewById(R.id.commentprogress_wheel);
        mCommentRecyclerView = (ObservableRecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mCommentRecyclerView.setLayoutManager(mLayoutManager);
        mCommentRecyclerView.setHasFixedSize(true);
        mCommentRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mCommentRecyclerView.setScrollViewCallbacks(this);
        mCommentButton = (FloatingActionButton) findViewById(R.id.commentButton);
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(CommentActivity.this)
                        .title("コメント画面")
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
                        .positiveText("送る")
                        .show();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Util.getConnectedState(CommentActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(CommentActivity.this);
                } else {
                    Toast.makeText(CommentActivity.this, "通信に失敗しました", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        mCommentUrl = Const.getCommentAPI(mPost_id);
        getSignupAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(self);
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        audioCapabilitiesReceiver.register();

        appBarLayout.addOnOffsetChangedListener(this);
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
        audioCapabilitiesReceiver.unregister();
        //getPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);

        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();

    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(coordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
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
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            if (mPlayingPostId != null) {
                this.audioCapabilities = audioCapabilities;
                releasePlayer();
                if (Util.isMovieAutoPlay(this)) {
                    preparePlayer(getPlayingViewHolder(), headerUser.getMovie());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
    }

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mCommentUrl, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                mCommentProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("COMMENTACTIVITY", responseString);
                    JSONObject json = new JSONObject(responseString);
                    JSONArray array = new JSONArray(json.getString("comments"));
                    JSONObject obj = new JSONObject(json.getString("post"));

                    headerUser = PostData.createPostData(obj);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        mCommentusers.add(HeaderData.createCommentHeaderData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.reverse(mCommentusers);

                mCommentAdapter = new CommentAdapter(CommentActivity.this);
                mCommentRecyclerView.setAdapter(mCommentAdapter);
                //changeMovie();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getTimeline finish");
//                mTimelineDialog.dismiss();
                mCommentProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mCommentUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mCommentusers.clear();
                try {
                    Log.e("COMMENTACTIVITY", responseString);
                    JSONObject json = new JSONObject(responseString);
                    JSONArray array = new JSONArray(json.getString("comments"));
                    JSONObject obj = new JSONObject(json.getString("post"));

                    headerUser = PostData.createPostData(obj);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        mCommentusers.add(HeaderData.createCommentHeaderData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.reverse(mCommentusers);

                mCommentAdapter.notifyDataSetChanged();
                //changeMovie();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getTimeline finish");
//                mTimelineDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void postCommentAsync(final Context context, String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                mCommentProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
                Const.asyncHttpClient.get(context, mCommentUrl, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        mCommentusers.clear();
                        try {
                            Log.e("COMMENTACTIVITY", responseString);
                            JSONObject json = new JSONObject(responseString);
                            JSONArray array = new JSONArray(json.getString("comments"));
                            JSONObject obj = new JSONObject(json.getString("post"));

                            headerUser = PostData.createPostData(obj);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                mCommentusers.add(HeaderData.createCommentHeaderData(jsonObject));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Collections.reverse(mCommentusers);

                        mPlayingPostId = null;
                        mViewHolderHash.clear();
                        mCommentAdapter.notifyDataSetChanged();
                        //changeMovie();
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "コメント送信に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getTimeline finish");
//                mTimelineDialog.dismiss();
                mCommentProgress.setVisibility(View.GONE);
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
        if (!headerUser.getPost_id().equals(mPlayingPostId)) {
            Log.d("DEBUG", "postId change");

            mPlayingPostId = headerUser.getPost_id();
            final Const.ExoViewHolder currentViewHolder = getPlayingViewHolder();
            Log.d("DEBUG", "MOVIE::changeMovie 動画再生処理開始 postId:" + mPlayingPostId);
            if (mPlayBlockFlag) {
                Log.d("DEBUG", "startMovie play block status");
                return;
            }

            final String path = headerUser.getMovie();
            Log.e("DEBUG", "[ProgressBar GONE] cache Path: " + path);
            releasePlayer();
            if (Util.isMovieAutoPlay(this)) {
                preparePlayer(currentViewHolder, path);
            }
        }
    }

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

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

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
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView commentUserImage;
        private TextView user_name;
        private TextView date_time;
        private TextView usercomment;

        public CommentViewHolder(View view) {
            super(view);
            commentUserImage = (ImageView) view.findViewById(R.id.commentUserImage);
            user_name = (TextView) view.findViewById(R.id.user_name);
            date_time = (TextView) view.findViewById(R.id.date_time);
            usercomment = (TextView) view.findViewById(R.id.usercomment);
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
            holder.user_name.setText(user.getUsername());
            holder.datetime.setText(user.getPost_date());

            if (!user.getMemo().equals("none")) {
                holder.comment.setText(user.getMemo());
            } else {
                holder.comment.setText("");
            }

            Picasso.with(context)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.circleImage);

            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getPost_user_id(), user.getUsername(), CommentActivity.this);
                }
            });

            holder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getPost_user_id(), user.getUsername(), CommentActivity.this);
                }
            });

            holder.menuRipple.setOnClickListener(new View.OnClickListener() {
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

            if (!user.getCategory().equals("タグなし")) {
                holder.category.setText(user.getCategory());
            } else {
                holder.category.setText("　　　　");
            }
            if (!user.getTag().equals("タグなし")) {
                holder.atmosphere.setText(user.getTag());
            } else {
                holder.atmosphere.setText("　　　　");
            }
            if (!user.getValue().equals("0")) {
                holder.value.setText(user.getValue() + "円");
            } else {
                holder.value.setText("　　　　");
            }

            //リップルエフェクトを見せてからIntentを飛ばす
            holder.tenpoRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getPost_rest_id(), user.getRestname(), CommentActivity.this);
                }
            });

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
                        Log.e("いいねをクリック", user.getPost_id());
                        user.setGochi_flag(1);
                        user.setGochi_num(currentgoodnum + 1);

                        holder.likes.setText(String.valueOf((currentgoodnum + 1)));
                        holder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                        holder.likes_ripple.setClickable(false);

                        Util.postGochiAsync(CommentActivity.this, user);
                    }
                });
            } else {
                holder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                holder.likes_ripple.setClickable(false);
            }

            holder.comments_ripple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    mCommentButton.performClick();
                }
            });

            holder.share_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.getTransfer(context) != null) {
                        new BottomSheet.Builder(context, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.facebook_share:
                                        Toast.makeText(context, "シェアの準備をしています", Toast.LENGTH_LONG).show();
                                        Util.facebookVideoShare(context, shareDialog, user.getShare());
                                        break;
                                    case R.id.twitter_share:
                                        Util.twitterShare(context, holder.mVideoThumbnail, user.getRestname());
                                        break;
                                    case R.id.other_share:
                                        Toast.makeText(context, "シェアの準備をしています", Toast.LENGTH_LONG).show();
                                        Util.instaVideoShare(context, user.getRestname(), user.getShare());
                                        break;
                                    case R.id.close:
                                        dialog.dismiss();
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(context, "もうちょっと待ってから押してみましょう", Toast.LENGTH_SHORT).show();
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
                    .into(holder.commentUserImage);
            holder.user_name.setText(users.getUsername());
            holder.date_time.setText(users.getComment_date());
            holder.usercomment.setText(users.getComment());

            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(users.getUser_id(), users.getUsername(), CommentActivity.this);
                }
            });

            holder.commentUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(users.getUser_id(), users.getUsername(), CommentActivity.this);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCommentusers.size() + 1;
        }

    }
}

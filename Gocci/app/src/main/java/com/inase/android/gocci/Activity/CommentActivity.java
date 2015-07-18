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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.AttributeSet;
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
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;

public class CommentActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener {

    private ObservableRecyclerView mCommentRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<HeaderData> mCommentusers = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressWheel mCommentProgress;

    private FloatingActionButton mCommentButton;

    private CommentActivity self = this;

    private AttributeSet mVideoAttr;
    private Point mDisplaySize;
    private CacheManager mCacheManager;
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

    private AppBarLayout appBarLayout;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private long playerPosition;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private AudioCapabilities audioCapabilities;

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
                case Const.INTENT_TO_USERPAGE:
                    FlexibleUserProfActivity.startUserProfActivity(msg.arg1, activity);
                    break;
                case Const.INTENT_TO_RESTPAGE:
                    FlexibleTenpoActivity.startTenpoActivity(msg.arg1, activity);
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
        setContentView(R.layout.activity_comment);

        mCacheManager = CacheManager.getInstance(getApplicationContext());
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
                                        sHandler.obtainMessage(Const.INTENT_TO_POLICY, 0, 0, CommentActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_LICENSE, 0, 0, CommentActivity.this);
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
        mCommentButton = (FloatingActionButton) findViewById(R.id.commentButton);
        mCommentButton.attachToRecyclerView(mCommentRecyclerView);
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
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        mCommentUrl = Const.getCommentAPI(mPost_id);
        getSignupAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(self);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public void onPause() {
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
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();

    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCommentButton, event.mMessage, Snackbar.LENGTH_SHORT).show();
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
                preparePlayer(getPlayingViewHolder(), headerUser.getMovie());
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
            preparePlayer(currentViewHolder, path);
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

            holder.comment.setText(user.getMemo());

            Picasso.with(context)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.circleImage);

            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_USERPAGE, user.getPost_user_id(), user.getPost_user_id(), CommentActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            holder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_USERPAGE, user.getPost_user_id(), user.getPost_user_id(), CommentActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
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
                            holder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        releasePlayer();
                        preparePlayer(holder, user.getMovie());
                    }
                }
            });

            holder.rest_name.setText(user.getRestname());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getCategory().equals("none")) {
                holder.category.setText(user.getCategory());
            } else {
                holder.category.setText("　　　　");
            }
            if (!user.getTag().equals("none")) {
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
            holder.tenpoRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_RESTPAGE, user.getPost_rest_id(), user.getPost_rest_id(), CommentActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
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

            holder.comments_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("コメントをクリック", "コメント！" + user.getPost_id());
                    mCommentButton.performClick();
                }
            });

            holder.share_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(context, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.facebook_share:
                                    final String path = mCacheManager.getCachePath(user.getPost_id(), user.getMovie());
                                    if (path != null) {
                                        try {
                                            Movie movie = MovieCreator.build(path);
                                            Container out = new DefaultMp4Builder().build(movie);
                                            File file = new File(Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_DOWNLOADS), "share_video_" + System.currentTimeMillis() + ".mp4");
                                            file.getParentFile().mkdirs();
                                            FileOutputStream fos = new FileOutputStream(file);
                                            out.writeContainer(fos.getChannel());
                                            fos.close();
                                            Uri uri = Uri.fromFile(file);
                                            if (ShareDialog.canShow(ShareVideoContent.class)) {
                                                ShareVideo video = new ShareVideo.Builder()
                                                        .setLocalUrl(uri)
                                                        .build();
                                                ShareVideoContent content = new ShareVideoContent.Builder()
                                                        .setVideo(video)
                                                        .setContentTitle(user.getRestname().replaceAll("\\s+", ""))
                                                        .build();
                                                shareDialog.show(content);
                                            } else {
                                                // ...sharing failed, handle error
                                                Toast.makeText(context, "facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(context, "動画が読み込めていません", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.twitter_share:
                                    Uri bmpUri = Util.getLocalBitmapUri(holder.mVideoThumbnail);
                                    if (bmpUri != null) {
                                        TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                                .text("#" + user.getRestname().replaceAll("\\s+", "") + " #Gocci")
                                                .image(bmpUri);

                                        builder.show();
                                    } else {
                                        // ...sharing failed, handle error
                                        Toast.makeText(context, "twitterシェアに失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.other_share:
                                    final String other_path = mCacheManager.getCachePath(user.getPost_id(), user.getMovie());
                                    if (other_path != null) {
                                        try {
                                            Movie movie = MovieCreator.build(other_path);
                                            Container out = new DefaultMp4Builder().build(movie);
                                            File file = new File(Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_DOWNLOADS), "share_video_" + System.currentTimeMillis() + ".mp4");
                                            file.getParentFile().mkdirs();
                                            FileOutputStream fos = new FileOutputStream(file);
                                            out.writeContainer(fos.getChannel());
                                            fos.close();
                                            Uri uri = Uri.fromFile(file);
                                            // Create the new Intent using the 'Send' action.
                                            Intent share = new Intent(Intent.ACTION_SEND);
                                            // Set the MIME type
                                            share.setType("video/*");
                                            // Add the URI and the caption to the Intent.
                                            share.putExtra(Intent.EXTRA_STREAM, uri);
                                            share.setPackage("com.instagram.android");
                                            share.putExtra(Intent.EXTRA_TEXT, "#" + user.getRestname() + " #Gocci #FoodPorn");
                                            // Broadcast the Intent.
                                            startActivity(Intent.createChooser(share, "Share to"));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(CommentActivity.this, "シェアに失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
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
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_USERPAGE, users.getComment_user_id(), users.getComment_user_id(), CommentActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });

            holder.commentUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg =
                            sHandler.obtainMessage(Const.INTENT_TO_USERPAGE, users.getComment_user_id(), users.getComment_user_id(), CommentActivity.this);
                    sHandler.sendMessageDelayed(msg, 750);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCommentusers.size() + 1;
        }

    }
}

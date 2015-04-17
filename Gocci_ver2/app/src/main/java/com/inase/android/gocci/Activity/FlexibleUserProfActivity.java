package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andexert.library.RippleView;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CommentView;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.drakeet.materialdialog.MaterialDialog;

public class FlexibleUserProfActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, AbsListView.OnScrollListener, CacheManager.ICacheManagerListener {

    private String mUser_name;
    private String mName;
    private String mProfUrl;
    private String mEncodeUser_name;
    private String mPictureImageUrl;
    private String mUserPictureImageUrl;

    private int mFollowerNumber;
    private int mFolloweeNumber;
    private int mCheerNumber;

    private String clickedRestname;
    private String clickedLocality;
    private double clickedLat;
    private double clickedLon;
    private String clickedHomepage;
    private String clickedPhoneNumber;
    private String clickedCategory;

    private ObservableListView mUserProfListView;
    private UserProfAdapter mUserProfAdapter;
    private SwipeRefreshLayout mUserProfSwipe;
    private ArrayList<UserData> mUserProfusers = new ArrayList<UserData>();

    private ProgressWheel userprofprogress;

    private RequestParams loginParam;

    private AttributeSet mVideoAttr;

    private Point mDisplaySize;
    private CacheManager mCacheManager;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<ViewHolder, String> mViewHolderHash;  // Value: PosterId

    private Application_Gocci gocci;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.e("DEBUG", "onGlobalLayout called: " + mPlayingPostId);
            changeMovie();
            Log.e("DEBUG", "onGlobalLayout  changeMovie called: " + mPlayingPostId);
            if (mPlayingPostId != null) {
                mUserProfListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gocci = (Application_Gocci) getApplication();

        mCacheManager = CacheManager.getInstance(getApplicationContext());
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        setContentView(R.layout.activity_flexible_user_prof);

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        mName = gocci.getName();
        mPictureImageUrl = gocci.getPicture();
        mFollowerNumber = gocci.getFollower();
        mFolloweeNumber = gocci.getFollowee();
        mCheerNumber = gocci.getCheer();

        Intent userintent = getIntent();
        mUser_name = userintent.getStringExtra("username");
        mUserPictureImageUrl = userintent.getStringExtra("picture");

        try {
            mEncodeUser_name = URLEncoder.encode(mUser_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mProfUrl = "http://api-gocci.jp/mypage/?user_name=" + mEncodeUser_name;

        EventDateRecorder profilerecorder = EventDateRecorder.load(FlexibleUserProfActivity.this, "use_first_userprofile");
        if (!profilerecorder.didRecorded()) {
            // 機能が１度も利用されてない時のみ実行したい処理を書く
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FlexibleUserProfActivity.this);
            Effectstype effect = Effectstype.SlideBottom;
            dialogBuilder
                    .withTitle("ユーザー画面")
                    .withMessage("他のユーザーはどんな店に行っているのでしょう？")
                    .withDuration(500)                                          //def
                    .withEffect(effect)
                    .isCancelableOnTouchOutside(true)
                    .show();

            profilerecorder.record();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        View header = new DrawerProfHeader(this, mName, mPictureImageUrl, mFollowerNumber, mFolloweeNumber, mCheerNumber);

        final Drawer.Result result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("ライフログ").withIcon(GoogleMaterial.Icon.gmd_event).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName("お店を検索する").withIcon(GoogleMaterial.Icon.gmd_explore).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイプロフィール").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(4).withCheckable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("アプリに関する要望を送る").withCheckable(false).withIdentifier(5),
                        new SecondaryDrawerItem().withName("お店を追加する").withCheckable(false).withIdentifier(6),
                        new SecondaryDrawerItem().withName("利用規約").withCheckable(false).withIdentifier(7),
                        new SecondaryDrawerItem().withName("プライバシーポリシー").withCheckable(false).withIdentifier(8)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Handler handler = new Handler();
                                handler.postDelayed(new timelineClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Handler handler = new Handler();
                                handler.postDelayed(new lifelogClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Handler handler = new Handler();
                                handler.postDelayed(new searchClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Handler handler = new Handler();
                                handler.postDelayed(new myprofClickHandler(), 500);
                            }
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(-1)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        finish();
                        return true;
                    }
                })
                .build();

        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginParam = new RequestParams("user_name", mName);

        userprofprogress = (ProgressWheel) findViewById(R.id.userprofprogress_wheel);

        mUserProfAdapter = new UserProfAdapter(this, 0, mUserProfusers);
        mUserProfListView = (ObservableListView) findViewById(R.id.list);
        mUserProfListView.setOnScrollListener(this);
        mUserProfListView.setScrollViewCallbacks(this);
        mUserProfListView.setDivider(null);
        // スクロールバーを表示しない
        mUserProfListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mUserProfListView.setSelector(android.R.color.transparent);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mUserProfListView.addHeaderView(inflater.inflate(R.layout.view_header_userprof, null));

        TextView userprof_username = (TextView) findViewById(R.id.userprof_username);
        ImageView userprof_picture = (ImageView) findViewById(R.id.userprof_picture);
        RippleView userprof_follow = (RippleView) findViewById(R.id.userprof_follow);
        TextView followText = (TextView) findViewById(R.id.followText);

        if (mUser_name.equals(mName)) {
            followText.setText("これはあなたです");
            userprof_follow.setClickable(false);
        }

        userprof_username.setText(mUser_name);
        Picasso.with(FlexibleUserProfActivity.this)
                .load(mUserPictureImageUrl)
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(userprof_picture);

        userprof_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //お気に入りするときの処理
                favoriteSignupAsync(FlexibleUserProfActivity.this, mUser_name);
            }
        });

        getSignupAsync(FlexibleUserProfActivity.this);

        mUserProfSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mUserProfSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mUserProfSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mUserProfSwipe.setRefreshing(true);

                getRefreshAsync(FlexibleUserProfActivity.this);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder != null) {
            stopMovie(viewHolder);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMovie();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {

            // スクロールしていない
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                //mBusy = false;
                Log.d("DEBUG", "SCROLL_STATE_IDLE");
                changeMovie();

                break;
            // スクロール中
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                //mBusy = true;
                Log.d("DEBUG", "SCROLL_STATE_TOUCH_SCROLL");

                break;
            // はじいたとき
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                //mBusy = true;
                Log.d("DEBUG", "SCROLL_STATE_FLING");

                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
// Translate overlay and image

    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void getSignupAsync(final Context context) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getUserProfJson(context, httpClient);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                userprofprogress.setVisibility(View.GONE);
                Toast.makeText(FlexibleUserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfJson(final Context context, AsyncHttpClient httpClient) {
        httpClient.get(context, mProfUrl, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mUserProfusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUserProfListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mUserProfListView.setAdapter(mUserProfAdapter);

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(FlexibleUserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                userprofprogress.setVisibility(View.GONE);
            }

        });
    }

    private void postSignupAsync(final Context context, final String post_id, final int position) {
        final AsyncHttpClient httpClient2 = new AsyncHttpClient();
        httpClient2.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postGoodAsync(context, post_id, position, httpClient2);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                final UserData user = mUserProfusers.get(position);
                user.setPushed_at(0);
                user.setgoodnum(user.getgoodnum() - 1);
                Toast.makeText(FlexibleUserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postGoodAsync(final Context context, String post_id, final int position, AsyncHttpClient httpClient2) {
        RequestParams goodParam = new RequestParams("post_id", post_id);
        httpClient2.post(context, Const.URL_GOOD_API, goodParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                final UserData user = mUserProfusers.get(position);
                user.setPushed_at(0);
                user.setgoodnum(user.getgoodnum() - 1);
                Toast.makeText(FlexibleUserProfActivity.this, "いいね送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        final AsyncHttpClient httpClient3 = new AsyncHttpClient();
        httpClient3.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getRefreshUserProfJson(context, httpClient3);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mUserProfSwipe.setRefreshing(false);
                Toast.makeText(FlexibleUserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshUserProfJson(final Context context, AsyncHttpClient httpClient3) {
        httpClient3.get(context, mProfUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                mUserProfusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mUserProfusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mUserProfListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mUserProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(FlexibleUserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mUserProfSwipe.setRefreshing(false);
            }

        });
    }

    private void favoriteSignupAsync(final Context context, final String username) {
        final AsyncHttpClient httpClient4 = new AsyncHttpClient();
        httpClient4.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                userprofprogress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postFavoriteAsync(context, username, httpClient4);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                userprofprogress.setVisibility(View.GONE);
                Toast.makeText(FlexibleUserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postFavoriteAsync(final Context context, final String username, AsyncHttpClient httpClient4) {
        RequestParams favoriteParam = new RequestParams("user_name", username);
        httpClient4.post(context, Const.URL_FAVORITE_API, favoriteParam, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    String message = response.getString("message");

                    if (message.equals("ユーザーをお気に入りしました")) {
                        gocci.addFollower();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "処理に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //mMaterialDialog.dismiss();
                userprofprogress.setVisibility(View.GONE);
            }
        });
    }

    private void unFavoriteSignupAsync(final Context context, final String username) {
        final AsyncHttpClient httpClient5 = new AsyncHttpClient();
        httpClient5.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                userprofprogress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postunFavoriteAsync(context, username, httpClient5);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                userprofprogress.setVisibility(View.GONE);
                Toast.makeText(FlexibleUserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postunFavoriteAsync(final Context context, final String username, AsyncHttpClient httpClient5) {
        RequestParams unFavoriteParam = new RequestParams("user_name", username);
        httpClient5.post(context, Const.URL_UNFAVORITE_API, unFavoriteParam, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    String message = response.getString("message");

                    if (message.equals("フォロー解除しました")) {
                        gocci.downFollower();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "処理に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //mMaterialDialog.dismiss();
                userprofprogress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void movieCacheCreated(boolean success, String postId) {
        if (success && mPlayingPostId == postId && getApplicationContext() != null) {
            Log.d("DEBUG", "MOVIE::movieCacheCreated 動画再生処理開始 postId:" + mPlayingPostId);
            startMovie();
        }
    }

    private void changeMovie() {
        Log.d("DEBUG", "changeMovie called");
        // TODO:実装
        final int position = mUserProfListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        if (mUserProfAdapter.isEmpty()) {
            return;
        }
        final UserData userData = mUserProfAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            Log.d("DEBUG", "postId change");

            // 前回の動画再生停止処理
            final ViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                Log.d("DEBUG", "MOVIE::changeMovie 再生停止 postId:" + mPlayingPostId);
                Log.e("DEBUG", "changeMovie 動画再生停止");
                stopMovie(oldViewHolder);

                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final ViewHolder currentViewHolder = getPlayingViewHolder();

            final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
            if (path != null) {
                // 動画再生開始
                Log.d("DEBUG", "MOVIE::changeMovie 動画再生処理開始 postId:" + mPlayingPostId);
                startMovie();
            } else {
                // 動画DL開始
                Log.d("DEBUG", "MOVIE::changeMovie  [ProgressBar VISIBLE] 動画DL処理開始 postId:" + mPlayingPostId);
                currentViewHolder.movieProgress.setVisibility(View.VISIBLE);
                mCacheManager.requestMovieCacheCreate(FlexibleUserProfActivity.this, userData.getMovie(), userData.getPost_id(), FlexibleUserProfActivity.this, currentViewHolder.movieProgress);

            }
        }
    }

    private void startMovie() {
        if (mPlayBlockFlag) {
            Log.d("DEBUG", "startMovie play block status");
            return;
        }
        final ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder == null) {
            Log.d("DEBUG", "startMovie viewHolder is null");
            return;
        }
        final int position = mUserProfListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        final UserData userData = mUserProfAdapter.getItem(position - 1);

        final String postId = userData.getPost_id();

        // 安定感が増すおまじないを試してみる
        refreshVideoView(viewHolder);

        final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        Log.e("DEBUG", "[ProgressBar GONE] cache Path: " + path);
        if (path == null) {
            Log.d("DEBUG", "startMovie path is null");
            return;
        }
        viewHolder.movieProgress.setVisibility(View.INVISIBLE);
        viewHolder.movie.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                Log.e("DEBUG", "VideoView::Error what:" + what + " extra:" + extra);
                return true;
            }
        });
        viewHolder.movie.setVideoPath(path);
        viewHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("DEBUG", "MOVIE::onPrepared postId: " + postId);
                if (mPlayingPostId == postId && !mPlayBlockFlag) {
                    Log.d("DEBUG", "MOVIE::onPrepared 再生開始");
                    viewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    viewHolder.movie.start();
                    Log.e("DEBUG", "onPrepared 動画再生開始: " + userData.getMovie());

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.e("DEBUG", "onPrepared onCompletion 動画再生開始");
                            mp.seekTo(0);
                            mp.start();
                        }
                    });
                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                            Log.e("DEBUG", "動画再生OnError: what:" + what + " extra:" + extra);
                            if (mPlayingPostId == postId && !mPlayBlockFlag) {
                                Log.d("DEBUG", "MOVIE::onErrorListener 再生開始");
                                mPlayingPostId = null;
                                changeMovie();
                            }
                            return true;
                        }
                    });
                } else {
                    Log.e("DEBUG", "onPrepared 動画再生停止");
                    viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
                    stopMovie(viewHolder);

                }
            }
        });
    }

    public void stopMovie(ViewHolder viewHolder) {
        if (viewHolder == null) {
            viewHolder = getPlayingViewHolder();
        }
        viewHolder.movie.pause();

    }

    /**
     * 現在再生中のViewHolderを取得
     *
     * @return
     */
    private ViewHolder getPlayingViewHolder() {
        ViewHolder viewHolder = null;
        Log.d("DEBUG", "getPlayingViewHolder :" + mPlayingPostId);
        if (mPlayingPostId != null) {
            for (Map.Entry<ViewHolder, String> entry : mViewHolderHash.entrySet()) {
                if (entry.getValue().equals(mPlayingPostId)) {
                    viewHolder = entry.getKey();
                    break;
                }
            }
        }
        return viewHolder;
    }

    private void refreshVideoView(ViewHolder viewHolder) {
        viewHolder.movie.setOnPreparedListener(null);
        viewHolder.movie.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                return true;
            }
        });

        viewHolder.movie.stopPlayback();
        ViewGroup viewgroup = (ViewGroup) viewHolder.movie.getParent();
        if (mVideoAttr == null) {
            mVideoAttr = viewHolder.movie.getAttributes();
        }
        viewHolder.movie.suspend();
        try {
            viewgroup.removeView(viewHolder.movie);
        } catch (RuntimeException runtimeexception) {
            try {
                viewgroup.removeView(viewHolder.movie);
            } catch (Exception exception) {
                Log.e("ERROR", "Weird things are happening.");
            }
        }
        viewHolder.movie = new SquareVideoView(FlexibleUserProfActivity.this, mVideoAttr);
        viewHolder.movie.setId(R.id.videoView);
        viewgroup.addView(viewHolder.movie, 0);

    }

    public static class ViewHolder {
        public ImageView circleImage;
        public TextView user_name;
        public TextView datetime;
        public SquareVideoView movie;
        public RoundCornerProgressBar movieProgress;
        public ImageView mVideoThumbnail;
        public ImageView restaurantImage;
        public TextView locality;
        public TextView rest_name;
        public RippleView tenpoRipple;
        public TextView likes;
        public ImageView likes_Image;
        public TextView comments;
        public RippleView likes_ripple;
        public RippleView comments_ripple;
    }

    public class UserProfAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater mLayoutInflater;

        public UserProfAdapter(Context context, int viewResourceId, ArrayList<UserData> userprofusers) {
            super(context, viewResourceId, userprofusers);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final UserData user = this.getItem(position);

            // ViewHolder 取得・作成処理
            ViewHolder viewHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mLayoutInflater.inflate(R.layout.cell_timeline, null);

                viewHolder = new ViewHolder();
                viewHolder.circleImage = (ImageView) convertView.findViewById(R.id.circleImage);
                viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.datetime = (TextView) convertView.findViewById(R.id.time_text);
                viewHolder.movie = (SquareVideoView) convertView.findViewById(R.id.videoView);
                viewHolder.movieProgress = (RoundCornerProgressBar) convertView.findViewById(R.id.video_progress);
                viewHolder.mVideoThumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                viewHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
                viewHolder.rest_name = (TextView) convertView.findViewById(R.id.rest_name);
                viewHolder.locality = (TextView) convertView.findViewById(R.id.locality);
                viewHolder.tenpoRipple = (RippleView) convertView.findViewById(R.id.tenpoRipple);
                viewHolder.likes = (TextView) convertView.findViewById(R.id.likes_Number);
                viewHolder.likes_Image = (ImageView) convertView.findViewById(R.id.likes_Image);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.comments_Number);
                viewHolder.likes_ripple = (RippleView) convertView.findViewById(R.id.likes_ripple);
                viewHolder.comments_ripple = (RippleView) convertView.findViewById(R.id.comments_ripple);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.user_name.setText(user.getUser_name());
            if (viewHolder.circleImage == null) {
                Log.d("DEBUG", "viewHolder.circleImage is null");
            }

            viewHolder.datetime.setText(user.getDatetime());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.circleImage);


            Picasso.with(getContext())
                    .load(user.getThumbnail())
                    .placeholder(R.color.videobackground)
                    .into(viewHolder.mVideoThumbnail);
            viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            viewHolder.movieProgress.setVisibility(View.INVISIBLE);
            viewHolder.movieProgress.setAlpha(0.5f);
            viewHolder.movieProgress.setProgress(0);
            if (viewHolder.movie.isPlaying()) {
                Log.e("DEBUG", "getView 動画再生停止");
                stopMovie(viewHolder);

            }

            viewHolder.rest_name.setText(user.getRest_name());
            viewHolder.locality.setText(user.getLocality());

            //リップルエフェクトを見せてからIntentを飛ばす
            viewHolder.tenpoRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedRestname = user.getRest_name();
                    clickedLocality = user.getLocality();
                    clickedLat = user.getLat();
                    clickedLon = user.getLon();
                    clickedPhoneNumber = user.getTell();
                    clickedHomepage = user.getHomepage();
                    clickedCategory = user.getCategory();
                    Handler handler = new Handler();
                    handler.postDelayed(new restClickHandler(), 750);
                }
            });
            final int currentgoodnum = user.getgoodnum();
            final int currentcommentnum = user.getComment_num();

            viewHolder.likes.setText(String.valueOf(currentgoodnum));
            viewHolder.comments.setText(String.valueOf(currentcommentnum));

            if (user.getPushed_at() == 0) {
                viewHolder.likes_ripple.setClickable(true);
                viewHolder.likes_Image.setImageResource(R.drawable.ic_favorite_normal);

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.likes_ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("いいねをクリック", user.getPost_id());

                        final UserData user = mUserProfusers.get(position);
                        user.setPushed_at(1);
                        user.setgoodnum(currentgoodnum + 1);
                        finalViewHolder.likes.setText(String.valueOf(currentgoodnum + 1));
                        finalViewHolder.likes_Image.setImageResource(R.drawable.ic_favorite_orange);
                        finalViewHolder.likes_ripple.setClickable(false);

                        postSignupAsync(FlexibleUserProfActivity.this, user.getPost_id(), position);
                    }
                });
            } else {
                viewHolder.likes_Image.setImageResource(R.drawable.ic_favorite_orange);
                viewHolder.likes_ripple.setClickable(false);
            }

            viewHolder.comments_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                    //投稿に対するコメントが見れるダイアログを表示
                    View commentView = new CommentView(FlexibleUserProfActivity.this, mName, user.getPost_id());

                    MaterialDialog mMaterialDialog = new MaterialDialog(FlexibleUserProfActivity.this)
                            .setContentView(commentView)
                            .setCanceledOnTouchOutside(true);
                    mMaterialDialog.show();
                }
            });

            mViewHolderHash.put(viewHolder, user.getPost_id());
            Log.e("入れたポスト", user.getPost_id());
            return convertView;
        }
    }

    class restClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleUserProfActivity.this, FlexibleTenpoActivity.class);
            intent.putExtra("restname", clickedRestname);
            intent.putExtra("locality", clickedLocality);
            intent.putExtra("lat", clickedLat);
            intent.putExtra("lon", clickedLon);
            intent.putExtra("phone", clickedPhoneNumber);
            intent.putExtra("homepage", clickedHomepage);
            intent.putExtra("category", clickedCategory);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class timelineClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleUserProfActivity.this, GocciTimelineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class lifelogClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleUserProfActivity.this, GocciLifelogActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class searchClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleUserProfActivity.this, GocciSearchTenpoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class myprofClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleUserProfActivity.this, GocciMyprofActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}

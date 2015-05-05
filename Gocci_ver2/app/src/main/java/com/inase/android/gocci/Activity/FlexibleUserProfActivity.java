package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.Session;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.DrawerHeaderRefreshEvent;
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
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

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
    private String mProfUrl;
    private String mEncodeUser_name;
    private String mUserPictureImageUrl;
    private String mUserBackground;

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

    private TextView followText;

    private MaterialDialog mViolationDialog;

    private Application_Gocci gocci;

    private Drawer.Result result;

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

        mCacheManager = CacheManager.getInstance(getApplicationContext());
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        setContentView(R.layout.activity_flexible_user_prof);

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        Intent userintent = getIntent();
        mUser_name = userintent.getStringExtra("username");
        mUserPictureImageUrl = userintent.getStringExtra("picture");
        mUserBackground = userintent.getStringExtra("background");

        try {
            mEncodeUser_name = URLEncoder.encode(mUser_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        gocci = (Application_Gocci)getApplication();

        mProfUrl = "http://api-gocci.jp/android_mypage/?user_name=" + mEncodeUser_name;

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

        View header = new DrawerProfHeader(this, gocci.getMyName(), gocci.getMypicture(), gocci.getMyBackground(), gocci.getMyFollower(), gocci.getMyFollowee(), gocci.getMyCheer());

        result = new Drawer()
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
                        new SecondaryDrawerItem().withName("プライバシーポリシー").withCheckable(false).withIdentifier(6),
                        new SecondaryDrawerItem().withName("ログアウト").withCheckable(false).withIdentifier(7)
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
                            } else if (drawerItem.getIdentifier() == 5) {
                                Handler handler = new Handler();
                                handler.postDelayed(new adviceClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 6) {
                                Handler handler = new Handler();
                                handler.postDelayed(new policyClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 7) {
                                Handler handler = new Handler();
                                handler.postDelayed(new logoutClickHandler(), 500);
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

        loginParam = new RequestParams();
        loginParam.put("user_name", gocci.getLoginName());
        loginParam.put("picture", gocci.getLoginPicture());

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
        ImageView userprof_background = (ImageView) findViewById(R.id.userprof_background);
        RippleView userprof_follow = (RippleView) findViewById(R.id.userprof_follow);
        followText = (TextView) findViewById(R.id.followText);

        userprof_username.setText(mUser_name);
        Picasso.with(FlexibleUserProfActivity.this)
                .load(mUserPictureImageUrl)
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(userprof_picture);
        Picasso.with(FlexibleUserProfActivity.this)
                .load(mUserBackground)
                .fit()
                .into(userprof_background);

        userprof_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //お気に入りするときの処理
                switch (followText.getText().toString()) {
                    case "このユーザーをフォローする":
                        favoriteSignupAsync(FlexibleUserProfActivity.this, mUser_name);
                        break;
                    case "このユーザーをフォロー解除する":
                        unFavoriteSignupAsync(FlexibleUserProfActivity.this, mUser_name);
                        break;
                    case "これはあなたです":
                        break;
                }

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
        BusHolder.get().unregister(this);
        ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder != null) {
            stopMovie(viewHolder);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(this);
        startMovie();
    }

    @Subscribe
    public void subscribe(DrawerHeaderRefreshEvent event) {
        View refreshHeader = new DrawerProfHeader(this, event.refreshName, event.refreshPicture, event.refreshBackground,
                event.refreshFollower, event.refreshFollowee, event.refreshCheer);
        result.setHeader(refreshHeader);
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
        httpClient.get(context, mProfUrl, new TextHttpResponseHandler() {


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleUserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.e("ろぐ", responseString);
                    JSONObject json = new JSONObject(responseString);
                    String flag = json.getString("flag");
                    JSONArray array = new JSONArray(json.getString("mypage"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        mUserProfusers.add(UserData.createUserData(jsonObject));
                    }

                    if (flag.equals("0")) {
                        followText.setText("このユーザーをフォローする");
                    } else {
                        followText.setText("このユーザーをフォロー解除する");
                    }

                    if (mUser_name.equals(gocci.getMyName())) {
                        followText.setText("これはあなたです");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUserProfListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mUserProfListView.setAdapter(mUserProfAdapter);
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
        httpClient3.get(context, mProfUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleUserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mUserProfusers.clear();
                try {
                    JSONObject json = new JSONObject(responseString);
                    String flag = json.getString("flag");
                    JSONArray array = new JSONArray(json.getString("mypage"));

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        mUserProfusers.add(UserData.createUserData(jsonObject));
                    }

                    if (flag.equals("0")) {
                        followText.setText("このユーザーをフォローする");
                    } else {
                        followText.setText("このユーザーをフォロー解除する");
                    }

                    if (mUser_name.equals(gocci.getMyName())) {
                        followText.setText("これはあなたです");
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
                        //gocci.addFollower();
                        gocci.addFollower();
                        followText.setText("このユーザーをフォロー解除する");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                        //gocci.downFollower();
                        gocci.downFollower();
                        followText.setText("このユーザーをフォローする");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
                userprofprogress.setVisibility(View.GONE);
            }
        });
    }

    private void violateSignupAsync(final Context context, final String post_id) {
        final AsyncHttpClient httpClient5 = new AsyncHttpClient();
        httpClient5.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                userprofprogress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postViolateAsync(context, post_id, httpClient5);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                userprofprogress.setVisibility(View.GONE);
                Toast.makeText(context, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postViolateAsync(final Context context, final String post_id, AsyncHttpClient httpClient5) {
        RequestParams violateParam = new RequestParams("post_id", post_id);
        httpClient5.post(context, Const.URL_VIOLATE_API, violateParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, "違反報告が完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                Toast.makeText(context, "違反報告に失敗しました", Toast.LENGTH_SHORT).show();
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
                currentViewHolder.videoFrame.setClickable(false);
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
                    //viewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    //viewHolder.movie.start();
                    viewHolder.videoFrame.setClickable(true);
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
        viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
    }

    private void setViolateDialog(final Context context, final String post_id) {
        mViolationDialog = new MaterialDialog(FlexibleUserProfActivity.this);
        mViolationDialog.setTitle("投稿の違反報告");
        mViolationDialog.setMessage("本当にこの投稿を違反報告しますか？");
        mViolationDialog.setCanceledOnTouchOutside(true);
        mViolationDialog.setPositiveButton("はい", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViolationDialog.dismiss();
                violateSignupAsync(context, post_id);
            }
        });
        mViolationDialog.setNegativeButton("いいえ", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViolationDialog.dismiss();
            }
        });

        mViolationDialog.show();
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
        public RippleView menuRipple;
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
        public FrameLayout videoFrame;
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
                viewHolder.menuRipple = (RippleView) convertView.findViewById(R.id.menuRipple);
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
                viewHolder.videoFrame = (FrameLayout) convertView.findViewById(R.id.videoFrame);
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

            viewHolder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(FlexibleUserProfActivity.this, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_ubnormal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.violation:
                                    setViolateDialog(FlexibleUserProfActivity.this, user.getPost_id());
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                }
            });

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

            final ViewHolder videoClickViewHolder = viewHolder;
            viewHolder.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoClickViewHolder.movie.isPlaying()) {
                        videoClickViewHolder.movie.pause();
                    } else {
                        videoClickViewHolder.movie.start();
                        videoClickViewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    }
                }
            });

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
                    View commentView = new CommentView(FlexibleUserProfActivity.this, user.getPost_id(), loginParam);

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

    class adviceClickHandler implements Runnable {
        public void run() {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleUserProfActivity.this)
                    .title("アドバイスを送る")
                    .content("以下から当てはまるもの１つを選択してください。")
                    .items(R.array.single_choice_array)
                    .itemsCallbackSingleChoice(-1, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            return true;
                        }
                    })
                    .positiveText("次へ進む")
                    .positiveColorRes(R.color.gocci_header)
                    .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);

                            if (dialog.getSelectedIndex() != -1) {
                                switch (dialog.getSelectedIndex()) {
                                    case 0:
                                        dialog.cancel();
                                        setNextDialog("ご要望");
                                        break;
                                    case 1:
                                        dialog.cancel();
                                        setNextDialog("苦情");
                                        break;
                                    case 2:
                                        dialog.cancel();
                                        setNextDialog("ご意見");
                                        break;
                                }
                            } else {
                                dialog.show();
                                Toast.makeText(FlexibleUserProfActivity.this, "一つを選択してください", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .show();
        }

        private void setNextDialog(final String string) {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleUserProfActivity.this)
                    .title("アドバイスを送る")
                    .content("コメントを入力してください")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(string, null, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, CharSequence charSequence) {
                        }
                    })
                    .positiveText("送信する")
                    .positiveColorRes(R.color.gocci_header)
                    .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String message = dialog.getInputEditText().getText().toString();

                            if (!message.isEmpty()) {
                                postSignupAsync(FlexibleUserProfActivity.this, string, message);
                            } else {
                                Toast.makeText(FlexibleUserProfActivity.this, "文字を入力してください", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).show();
        }

        private void postSignupAsync(final Context context, final String category, final String message) {
            final AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e("サインアップ成功", "status=" + statusCode);
                    postAsync(context, httpClient, category, message);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void postAsync(final Context context, AsyncHttpClient client, String category, String message) {
            RequestParams sendParams = new RequestParams();
            sendParams.put("select_support", category);
            sendParams.put("content", message);
            client.post(context, Const.URL_ADVICE_API, sendParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "送信に失敗しました", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    class policyClickHandler implements Runnable {
        public void run() {
            Uri uri = Uri.parse("http://inase-inc.jp/rules/");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class logoutClickHandler implements Runnable {
        public void run() {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleUserProfActivity.this)
                    .title("確認")
                    .content("本当にログアウトしますか？")
                    .positiveText("はい")
                    .negativeText("いいえ")
                    .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);

                            SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                            String judge = pref.getString("judge", "no judge");

                            switch (judge) {
                                case "facebook":
                                    Session session = Session.getActiveSession();
                                    if (session != null) {
                                        if (!session.isClosed()) {
                                            session.closeAndClearTokenInformation();
                                            //clear your preferences if saved
                                        }
                                    } else {
                                        session = new Session(FlexibleUserProfActivity.this);
                                        Session.setActiveSession(session);

                                        session.closeAndClearTokenInformation();
                                        //clear your preferences if saved
                                    }
                                    break;
                                case "twitter":
                                    Twitter.logOut();
                                    break;
                                case "auth":
                                    break;
                                default:
                                    break;
                            }

                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.apply();

                            EventDateRecorder recorder = EventDateRecorder.load(FlexibleUserProfActivity.this, "use_first_gocci_android");
                            recorder.clear();

                            Intent intent = new Intent(FlexibleUserProfActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    }).show();
        }
    }
}

package com.inase.android.gocci.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Activity.FlexibleTenpoActivity;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.BaseFragment;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.PageChangeVideoStopEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CommentView;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import me.drakeet.materialdialog.MaterialDialog;

public class FriendTimelineFragment extends BaseFragment implements ObservableScrollViewCallbacks, AbsListView.OnScrollListener, CacheManager.ICacheManagerListener {

    private int mNowNumber = 30;

    private ProgressWheel progressWheel;
    private ObservableListView mTimelineListView;
    private ArrayList<UserData> mTimelineusers = new ArrayList<>();
    private SwipeRefreshLayout mTimelineSwipe;
    private FriendTimelineAdapter mTimelineAdapter;
    //private FloatingActionButton fab;

    private String mName;
    public String mPictureImageUrl;

    private String clickedUsername;
    private String clickedUserpicture;
    private String clickedRestname;
    private String clickedLocality;
    private String clickedPhoneNumber;
    private String clickedHomepage;
    private String clickedCategory;
    private double clickedLat;
    private double clickedLon;

    private boolean mScrolled = false;
    private int refreshNumber = 1;

    private Location mLocation = null;
    private String mTimelineUrl = null;

    private RequestParams loginParam;
    private RequestParams goodParam;
    private RequestParams violateParam;
    private RequestParams favoriteParam;
    private AttributeSet mVideoAttr;

    private Point mDisplaySize;
    private CacheManager mCacheManager;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<ViewHolder, String> mViewHolderHash;  // Value: PosterId

    private Application_Gocci gocci;
    private MaterialDialog mViolationDialog;

    private UiLifecycleHelper uiHelper;

    private Toolbar toolbar;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.e("DEBUG", "onGlobalLayout called: " + mPlayingPostId);
            changeMovie();
            Log.e("DEBUG", "onGlobalLayout  changeMovie called: " + mPlayingPostId);
            if (mPlayingPostId != null) {
                mTimelineListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheManager = CacheManager.getInstance(getActivity().getApplicationContext());
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);

        Fabric.with(getActivity(), new TweetComposer());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline, container, false);

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        mName = pref.getString("name", null);
        mPictureImageUrl = pref.getString("pictureImageUrl", null);

        loginParam = new RequestParams("user_name", mName);

        gocci = (Application_Gocci) getActivity().getApplication();
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        mTimelineListView = (ObservableListView) view.findViewById(R.id.list);
        mTimelineSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_timeline);
        //fab = (FloatingActionButton) view.findViewById(R.id.toukouButton);

        mTimelineListView.setOnScrollListener(this);
        mTimelineListView.setScrollViewCallbacks(this);
        mTimelineListView.setDivider(null);
        mTimelineListView.setVerticalScrollBarEnabled(false);
        mTimelineListView.setSelector(android.R.color.transparent);

        mTimelineAdapter = new FriendTimelineAdapter(getActivity(), 0, mTimelineusers);

        if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
            if (Util.getConnectedState(getActivity()) == Util.NetworkStatus.MOBILE) {
                Toast.makeText(getActivity(), "回線が悪いので、動画が流れなくなります", Toast.LENGTH_LONG).show();
                if (gocci.getFirstLocation() != null) {
                    mLocation = gocci.getFirstLocation();
                    Log.e("DEBUG", "アプリから位置とったよ");

                    getSignupAsync(getActivity());//サインアップとJSON

                } else {
                    SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            if (location != null) {
                                mLocation = location;
                                Log.e("とったどー", "いえい！");
                                getSignupAsync(getActivity());
                            } else {
                                Toast.makeText(getActivity(), "位置情報が読み取れませんでした", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else {
                if (gocci.getFirstLocation() != null) {
                    mLocation = gocci.getFirstLocation();
                    Log.e("DEBUG", "アプリから位置とったよ");

                    getSignupAsync(getActivity());//サインアップとJSON

                } else {
                    SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            if (location != null) {
                                mLocation = location;
                                Log.e("とったどー", "いえい！");
                                getSignupAsync(getActivity());
                            } else {
                                Toast.makeText(getActivity(), "位置情報が読み取れませんでした", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        } else {
            Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_LONG).show();
        }

        mTimelineSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mTimelineSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mTimelineSwipe.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            if (location != null) {
                                mLocation = location;
                                Log.e("とったどー", "いえい！");
                                getRefreshAsync(getActivity());
                            } else {
                                Toast.makeText(getActivity(), "位置情報が読み取れませんでした", Toast.LENGTH_SHORT).show();
                                mTimelineSwipe.setRefreshing(false);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        // Subscriberとして登録する
        BusHolder.get().register(this);

        startMovie();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        // Subscriberの登録を解除する
        BusHolder.get().unregister(this);

        ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder != null) {
            stopMovie(viewHolder);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    //一番下までスクロールした際に更新処理をかける時、スクロール位置を記憶しておく、
    private void restoreListPosition() {
        int position = mTimelineListView.getFirstVisiblePosition();
        int yOffset = mTimelineListView.getChildAt(0).getTop();
        mTimelineListView.setSelectionFromTop(position, yOffset);
    }

    private void setViolateDialog(final Context context, final String post_id) {
        mViolationDialog = new MaterialDialog(getActivity());
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

    //動画のバックグラウンド再生を止める処理のはず。。。
    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        Log.e("動画ストップ", "ポジション" + event.position);
        switch (event.position) {
            case 1:
                mPlayBlockFlag = false;
                //タイムラインが呼ばれた時の処理
                startMovie();
                Log.e("Otto発動", "動画再生復帰");
                break;
            case 0:
                mPlayBlockFlag = true;
                //タイムライン以外のfragmentが可視化している場合
                final ViewHolder viewHolder = getPlayingViewHolder();
                if (viewHolder != null) {
                    if (viewHolder.movie.isPlaying()) {
                        stopMovie(viewHolder);
                        Log.e("DEBUG", "subscribe 動画再生停止");
                    }
                }
                Log.e("Otto発動", "動画再生停止");
                break;
        }
    }

    private void getSignupAsync(final Context context) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d("DEBUG", "ProgressDialog show");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                mTimelineUrl = "http://api-gocci.jp/timeline_near/?lat=" + mLocation.getLatitude() + "&lon=" +
                        mLocation.getLongitude() + "&limit=" + mNowNumber;
                getTimelineJson(context, mTimelineUrl, httpClient);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("DEBUG", "ProgressDialog dismiss getSignup failure");
                progressWheel.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTimelineJson(final Context context, String url, final AsyncHttpClient httpClient) {
        httpClient.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTimelineusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mTimelineusers.size() != 0) {
                    mTimelineListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    mTimelineListView.setAdapter(mTimelineAdapter);
                    //getTimelineDateJson(context);
                } else {
                    getTimelineJson(getActivity(), Const.URL_TIMELINE_API, httpClient);
                    Toast.makeText(getActivity(), "近くの投稿がないので、全体のタイムラインを読み込みます", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getTimeline finish");
//                mTimelineDialog.dismiss();
                progressWheel.setVisibility(View.GONE);
            }
        });
    }

    private void postSignupAsync(final Context context, final String post_id, final int position) {
        final AsyncHttpClient httpClient2 = new AsyncHttpClient();
        httpClient2.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("いいねサインアップ成功", "status=" + statusCode);
                postGoodAsync(context, post_id, position, httpClient2);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                final UserData user = mTimelineusers.get(position);
                user.setPushed_at(0);
                user.setgoodnum(user.getgoodnum() - 1);
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postGoodAsync(final Context context, String post_id, final int position, AsyncHttpClient httpClient2) {
        goodParam = new RequestParams("post_id", post_id);
        httpClient2.post(context, Const.URL_GOOD_API, goodParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //配列のpushed_atを１にする
                Log.e("確認", "配列の中を１にしました");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                final UserData user = mTimelineusers.get(position);
                user.setPushed_at(0);
                user.setgoodnum(user.getgoodnum() - 1);
                Toast.makeText(getActivity(), "いいね送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        final AsyncHttpClient httpClient3 = new AsyncHttpClient();
        httpClient3.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                mTimelineUrl = "http://api-gocci.jp/timeline_near/?lat=" + mLocation.getLatitude() + "&lon=" +
                        mLocation.getLongitude() + "&limit=" + mNowNumber;
                getRefreshTimelineJson(context, mTimelineUrl, httpClient3);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("DEBUG", "ProgressDialog dismiss getRefresh failure");
                mTimelineSwipe.setRefreshing(false);
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void getRefreshTimelineJson(final Context context, String url, final AsyncHttpClient httpClient3) {
        httpClient3.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

                mTimelineusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTimelineusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mTimelineusers.size() != 0) {
                    mPlayingPostId = null;
                    mViewHolderHash.clear();
                    mTimelineListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    mTimelineAdapter.notifyDataSetChanged();
                } else {
                    getRefreshTimelineJson(getActivity(), Const.URL_TIMELINE_API, httpClient3);
                    Toast.makeText(getActivity(), "近くの投稿がないので、全体のタイムラインを読み込みます", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getRefresh finish");
                mTimelineSwipe.setRefreshing(false);
            }

        });
    }

    private void getAddJsonAsync(final Context context, final String url) {
        final AsyncHttpClient httpClient4 = new AsyncHttpClient();
        httpClient4.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.d("DEBUG", "ProgressDialog show AddJson");
                progressWheel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getAddTimelineJson(context, url, httpClient4);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("DEBUG", "ProgressDialog dismiss AddJson failure");
                progressWheel.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void getAddTimelineJson(final Context context, String url, AsyncHttpClient httpClient4) {
        httpClient4.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                mTimelineusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTimelineusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mTimelineListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss AddJson Finish");
                progressWheel.setVisibility(View.GONE);
            }

        });
    }

    private void violateSignupAsync(final Context context, final String post_id) {
        final AsyncHttpClient httpClient5 = new AsyncHttpClient();
        httpClient5.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressWheel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postViolateAsync(context, post_id, httpClient5);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                progressWheel.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postViolateAsync(final Context context, final String post_id, AsyncHttpClient httpClient5) {
        violateParam = new RequestParams("post_id", post_id);
        httpClient5.post(context, Const.URL_VIOLATE_API, violateParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, "違反報告が完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                Toast.makeText(getActivity(), "違反報告に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //mMaterialDialog.dismiss();
                progressWheel.setVisibility(View.GONE);
            }
        });
    }

    private void favoriteSignupAsync(final Context context, final String username) {
        final AsyncHttpClient httpClient6 = new AsyncHttpClient();
        httpClient6.post(context, Const.URL_SIGNUP_API, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressWheel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                if (username.equals(mName)) {
                    Toast.makeText(getActivity(), "それはあなたです", Toast.LENGTH_SHORT).show();
                } else {
                    postFavoriteAsync(context, username, httpClient6);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                progressWheel.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postFavoriteAsync(final Context context, final String username, AsyncHttpClient httpClient6) {
        favoriteParam = new RequestParams("user_name", username);
        httpClient6.post(context, Const.URL_FAVORITE_API, favoriteParam, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    String message = response.getString("message");

                    if (message.equals("ユーザーをお気に入りしました")) {
                        gocci.addFollower();
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
                progressWheel.setVisibility(View.GONE);
            }
        });
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
        if (totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount && !mScrolled) {
            Log.d("DEBUG", "onScroll 一番下に到達");
            mScrolled = true;
            refreshNumber = refreshNumber + 1;

            // 最後尾までスクロールしたので、何かデータ取得する処理

            mNowNumber = refreshNumber * Const.TIMELINE_LIMIT;

            String TimelineUrl = "http://api-gocci.jp/timeline_near/?lat=" + mLocation.getLatitude() + "&lon=" +
                    mLocation.getLongitude() + "&limit=" + mNowNumber;

            getAddJsonAsync(getActivity(), TimelineUrl);

            restoreListPosition();
        }

        if (totalItemCount != 0 && totalItemCount != firstVisibleItem + visibleItemCount && mScrolled) {
            Log.d("DEBUG", "onScroll 一番下ではない");
            mScrolled = false;
        }


    }

    @Override
    public void movieCacheCreated(boolean success, String postId) {
        if (success && mPlayingPostId == postId && getActivity() != null) {

            Log.d("DEBUG", "MOVIE::movieCacheCreated 動画再生処理開始 postId:" + mPlayingPostId);
            startMovie();
        }
    }

    private void changeMovie() {
        Log.d("DEBUG", "changeMovie called");
        // TODO:実装
        final int position = mTimelineListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        if (mTimelineAdapter.isEmpty()) {
            return;
        }
        final UserData userData = mTimelineAdapter.getItem(position);
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

            if (!currentViewHolder.movie.isShown()) {
                Log.e("DEBUG", "バグだよ" + currentViewHolder.movie.getVisibility());
            }

            if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.MOBILE) {
                final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
                if (path != null) {
                    // 動画再生開始
                    Log.d("DEBUG", "MOVIE::changeMovie 動画再生処理開始 postId:" + mPlayingPostId);
                    startMovie();
                } else {
                    // 動画DL開始
                    Log.d("DEBUG", "MOVIE::changeMovie  [ProgressBar VISIBLE] 動画DL処理開始 postId:" + mPlayingPostId);
                    currentViewHolder.movieProgress.setVisibility(View.VISIBLE);
                    mCacheManager.requestMovieCacheCreate(getActivity(), userData.getMovie(), userData.getPost_id(), FriendTimelineFragment.this, currentViewHolder.movieProgress);

                }
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
        final int position = mTimelineListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        final UserData userData = mTimelineAdapter.getItem(position);

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
        viewHolder.movie = new SquareVideoView(getActivity(), mVideoAttr);
        viewHolder.movie.setId(R.id.videoView);
        viewgroup.addView(viewHolder.movie, 0);

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        /*
        if (scrollState == ScrollState.UP) {
            if (fab.isVisible()) {
                fab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!fab.isVisible()) {
                fab.show();
            }
        }
        */
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
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

    public class FriendTimelineAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater mLayoutInflater;

        public FriendTimelineAdapter(Context context, int viewResourceId, ArrayList<UserData> timelineusers) {
            super(context, viewResourceId, timelineusers);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final UserData user = getItem(position);

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

            /**
             * それぞれのViewに対する設定値変更処理
             */

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

            viewHolder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedUsername = user.getUser_name();
                    clickedUserpicture = user.getPicture();
                    Handler handler = new Handler();
                    handler.postDelayed(new nameClickHandler(), 750);

                }
            });

            viewHolder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedUsername = user.getUser_name();
                    clickedUserpicture = user.getPicture();
                    Handler handler = new Handler();
                    handler.postDelayed(new nameClickHandler(), 750);

                }
            });

            final ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(getActivity(), R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.person_plus:
                                    //お気に入り追加するときの処理
                                    favoriteSignupAsync(getActivity(), user.getUser_name());
                                    break;
                                case R.id.facebook_share:

                                    if (FacebookDialog.canPresentShareDialog(getActivity().getApplicationContext(),
                                            FacebookDialog.ShareDialogFeature.VIDEO)) {
                                        String data = mCacheManager.getCachePath(user.getPost_id(), user.getMovie());
                                        File file = new File(data);
                                        // Publish the post using the Video Share Dialog
                                        FacebookDialog shareDialog = new FacebookDialog.VideoShareDialogBuilder(getActivity())
                                                .setFragment(FriendTimelineFragment.this)
                                                .addVideoFile(file)
                                                .build();
                                        uiHelper.trackPendingDialogCall(shareDialog.present());
                                    } else {
                                        // The user doesn't have the Facebook for Android app installed.
                                        // You may be able to use a fallback.
                                        Toast.makeText(getActivity(), "facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.twitter_share:
                                    Uri bmpUri = getLocalBitmapUri(finalViewHolder1.mVideoThumbnail);
                                    if (bmpUri != null) {
                                        TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                                                .text(user.getRest_name() + "/" + user.getLocality())
                                                .image(bmpUri);

                                        builder.show();
                                    } else {
                                        // ...sharing failed, handle error
                                        Toast.makeText(getActivity(), "twitterシェアに失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case R.id.violation:
                                    setViolateDialog(getActivity(), user.getPost_id());
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
                        final UserData user = mTimelineusers.get(position);
                        user.setPushed_at(1);
                        user.setgoodnum(currentgoodnum + 1);

                        finalViewHolder.likes.setText(String.valueOf((currentgoodnum + 1)));
                        finalViewHolder.likes_Image.setImageResource(R.drawable.ic_favorite_orange);
                        finalViewHolder.likes_ripple.setClickable(false);

                        postSignupAsync(getActivity(), user.getPost_id(), position);
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
                    View commentView = new CommentView(getActivity(), mName, user.getPost_id());

                    MaterialDialog mMaterialDialog = new MaterialDialog(getActivity())
                            .setContentView(commentView)
                            .setCanceledOnTouchOutside(true);
                    mMaterialDialog.show();
                }
            });

            mViewHolderHash.put(viewHolder, user.getPost_id());
            return convertView;
        }
    }

    //名前部分のViewをクリックした時の処理
    class nameClickHandler implements Runnable {
        public void run() {
            if (!clickedUsername.equals(mName)) {
                Intent userintent = new Intent(getActivity(), FlexibleUserProfActivity.class);
                userintent.putExtra("username", clickedUsername);
                userintent.putExtra("name", mName);
                userintent.putExtra("picture", clickedUserpicture);
                startActivity(userintent);
                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        }
    }

    //店舗部分のViewをクリックした時の処理
    class restClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getActivity(), FlexibleTenpoActivity.class);
            intent.putExtra("restname", clickedRestname);
            intent.putExtra("name", mName);
            intent.putExtra("locality", clickedLocality);
            intent.putExtra("lat", clickedLat);
            intent.putExtra("lon", clickedLon);
            intent.putExtra("phone", clickedPhoneNumber);
            intent.putExtra("homepage", clickedHomepage);
            intent.putExtra("category", clickedCategory);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}

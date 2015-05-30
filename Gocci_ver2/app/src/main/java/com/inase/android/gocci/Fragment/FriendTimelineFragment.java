package com.inase.android.gocci.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.inase.android.gocci.Activity.CameraActivity;
import com.inase.android.gocci.Activity.FlexibleTenpoActivity;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Base.BaseFragment;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.PageChangeVideoStopEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CommentView;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
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

public class FriendTimelineFragment extends BaseFragment implements ObservableScrollViewCallbacks, AbsListView.OnScrollListener, CacheManager.ICacheManagerListener {

    private int mNowNumber = 30;

    private ProgressWheel progressWheel;
    private ObservableListView mTimelineListView;
    private ArrayList<UserData> mTimelineusers = new ArrayList<>();
    private SwipyRefreshLayout mTimelineSwipe;
    private FriendTimelineAdapter mTimelineAdapter;
    private FloatingActionButton fab;

    private String clickedUsername;
    private String clickedUserpicture;
    private String clickedUserbackground;
    private String clickedRestname;
    private String clickedLocality;
    private String clickedPhoneNumber;
    private String clickedHomepage;
    private String clickedCategory;
    private double clickedLat;
    private double clickedLon;
    private int clickedWant_flag;
    private int clickedTotal_cheer_num;

    private int refreshNumber = 1;

    private RequestParams goodParam;
    private RequestParams violateParam;
    private RequestParams favoriteParam;
    private AttributeSet mVideoAttr;

    private Point mDisplaySize;
    private CacheManager mCacheManager;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<ViewHolder, String> mViewHolderHash;  // Value: PosterId

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

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

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(FriendTimelineFragment.this);

        Fabric.with(getActivity(), new TweetComposer());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline, container, false);

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        mTimelineListView = (ObservableListView) view.findViewById(R.id.list);
        mTimelineSwipe = (SwipyRefreshLayout) view.findViewById(R.id.swipe_timeline);
        fab = (FloatingActionButton) view.findViewById(R.id.toukouButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        mTimelineListView.setOnScrollListener(this);
        mTimelineListView.setScrollViewCallbacks(this);
        mTimelineListView.setDivider(null);
        mTimelineListView.setVerticalScrollBarEnabled(false);
        mTimelineListView.setSelector(android.R.color.transparent);

        mTimelineAdapter = new FriendTimelineAdapter(getActivity(), 0, mTimelineusers);

        if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
            if (Util.getConnectedState(getActivity()) == Util.NetworkStatus.MOBILE) {
                Toast.makeText(getActivity(), "回線が悪いので、動画が流れなくなります", Toast.LENGTH_LONG).show();
                getSignupAsync(getActivity());//サインアップとJSON
            } else {
                getSignupAsync(getActivity());//サインアップとJSON
            }
        } else {
            Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_LONG).show();
        }

        mTimelineSwipe.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                mTimelineSwipe.setRefreshing(true);
                if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
                    if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                        getRefreshAsync(getActivity());
                    } else {
                        Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_LONG).show();
                    }
                } else {
                    refreshNumber = refreshNumber + 1;
                    // 最後尾までスクロールしたので、何かデータ取得する処理

                    mNowNumber = refreshNumber * Const.TIMELINE_LIMIT;

                    String TimelineUrl = "http://api-gocci.jp/favorites_timeline/?limit=" + mNowNumber;

                    getAddJsonAsync(getActivity(), TimelineUrl);

                    restoreListPosition();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
    }

    @Override
    public void onResume() {
        super.onResume();
        // Subscriberとして登録する
        BusHolder.get().register(this);

        startMovie();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Subscriberの登録を解除する
        BusHolder.get().unregister(this);

        ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder != null) {
            stopMovie(viewHolder);
        }
    }

    //一番下までスクロールした際に更新処理をかける時、スクロール位置を記憶しておく、
    private void restoreListPosition() {
        int position = mTimelineListView.getFirstVisiblePosition();
        int yOffset = mTimelineListView.getChildAt(0).getTop();
        mTimelineListView.setSelectionFromTop(position, yOffset);
    }

    private void setViolateDialog(final Context context, final String post_id) {
        new MaterialDialog.Builder(context)
                .title("投稿の違反報告")
                .content("本当にこの投稿を違反報告しますか？")
                .positiveText("する")
                .negativeText("いいえ")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        violateSignupAsync(context, post_id);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
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
                    } else {
                        viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
                    }
                }
                Log.e("Otto発動", "動画再生停止");
                break;
        }
    }

    private void getSignupAsync(final Context context) {
        final AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.get(context, Const.URL_FAVORITES_TIMELINE_API, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                Log.e("ジェイソン", String.valueOf(timeline));
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTimelineusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!mTimelineusers.isEmpty()) {
                    mTimelineListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    mTimelineListView.setAdapter(mTimelineAdapter);
                } else {
                    Toast.makeText(getActivity(), "お気に入りユーザーの投稿がありません！", Toast.LENGTH_SHORT).show();
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
        goodParam = new RequestParams("post_id", post_id);
        final AsyncHttpClient httpClient2 = new AsyncHttpClient();
        httpClient2.setCookieStore(SavedData.getCookieStore(context));
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
        httpClient3.setCookieStore(SavedData.getCookieStore(context));
        httpClient3.get(context, Const.URL_FAVORITES_TIMELINE_API, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.e("ジェイソン", String.valueOf(timeline));
                mTimelineusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTimelineusers.add(UserData.createUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!mTimelineusers.isEmpty()) {
                    mPlayingPostId = null;
                    mViewHolderHash.clear();
                    mTimelineListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    mTimelineAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "お気に入りユーザーの投稿がありません！", Toast.LENGTH_SHORT).show();
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
        httpClient4.setCookieStore(SavedData.getCookieStore(context));
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
                mTimelineSwipe.setRefreshing(false);
            }

        });
    }

    private void violateSignupAsync(final Context context, final String post_id) {
        violateParam = new RequestParams("post_id", post_id);
        final AsyncHttpClient httpClient5 = new AsyncHttpClient();
        httpClient5.setCookieStore(SavedData.getCookieStore(context));
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
    }

    @Override
    public void movieCacheCreated(boolean success, String postId) {
        if (success && mPlayingPostId.equals(postId) && getActivity() != null) {

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
                    currentViewHolder.videoFrame.setClickable(false);
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

        if (scrollState == ScrollState.UP) {
            if (fab.isVisible()) {
                fab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!fab.isVisible()) {
                fab.show();
            }
        }

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
        public TextView comment;
        public RippleView menuRipple;
        public SquareVideoView movie;
        public RoundCornerProgressBar movieProgress;
        public ImageView mVideoThumbnail;
        //public ImageView restaurantImage;
        //public TextView locality;
        public TextView rest_name;
        public TextView category;
        public TextView value;
        public TextView atmosphere;
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
                convertView = mLayoutInflater.inflate(R.layout.cell_timeline2, null);

                viewHolder = new ViewHolder();
                viewHolder.circleImage = (ImageView) convertView.findViewById(R.id.circleImage);
                viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.datetime = (TextView) convertView.findViewById(R.id.time_text);
                viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
                viewHolder.menuRipple = (RippleView) convertView.findViewById(R.id.menuRipple);
                viewHolder.movie = (SquareVideoView) convertView.findViewById(R.id.videoView);
                viewHolder.movieProgress = (RoundCornerProgressBar) convertView.findViewById(R.id.video_progress);
                viewHolder.mVideoThumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                //viewHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
                viewHolder.rest_name = (TextView) convertView.findViewById(R.id.rest_name);
                //viewHolder.locality = (TextView) convertView.findViewById(R.id.locality);
                viewHolder.category = (TextView) convertView.findViewById(R.id.category);
                viewHolder.value = (TextView) convertView.findViewById(R.id.value);
                viewHolder.atmosphere = (TextView) convertView.findViewById(R.id.mood);
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

            viewHolder.comment.setText(user.getComment());

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
                    clickedUserbackground = user.getBackground();
                    Handler handler = new Handler();
                    handler.postDelayed(new nameClickHandler(), 750);

                }
            });

            viewHolder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedUsername = user.getUser_name();
                    clickedUserpicture = user.getPicture();
                    clickedUserbackground = user.getBackground();
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
                                case R.id.facebook_share:
                                    Uri bmpUri2 = getLocalBitmapUri(finalViewHolder1.mVideoThumbnail);
                                    if (ShareDialog.canShow(SharePhotoContent.class) && bmpUri2 != null) {
                                        SharePhoto photo = new SharePhoto.Builder()
                                                .setImageUrl(bmpUri2)
                                                .build();
                                        SharePhotoContent content = new SharePhotoContent.Builder()
                                                .addPhoto(photo)
                                                .build();
                                        shareDialog.show(content);
                                    } else {
                                        // ...sharing failed, handle error
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
                        videoClickViewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    }
                }
            });


            viewHolder.rest_name.setText(user.getRest_name());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getTagCategory().equals("none")) {
                viewHolder.category.setText(user.getTagCategory());
            } else {
                viewHolder.category.setText("タグなし");
            }
            if (!user.getAtmosphere().equals("none")) {
                viewHolder.atmosphere.setText(user.getAtmosphere());
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
                    clickedRestname = user.getRest_name();
                    clickedLocality = user.getLocality();
                    clickedLat = user.getLat();
                    clickedLon = user.getLon();
                    clickedPhoneNumber = user.getTell();
                    clickedHomepage = user.getHomepage();
                    clickedCategory = user.getCategory();
                    clickedWant_flag = user.getWant_flag();
                    clickedTotal_cheer_num = user.getTotal_cheer_num();
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
                viewHolder.likes_Image.setImageResource(R.drawable.ic_like_white);

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.likes_ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("いいねをクリック", user.getPost_id());
                        final UserData user = mTimelineusers.get(position);
                        user.setPushed_at(1);
                        user.setgoodnum(currentgoodnum + 1);

                        finalViewHolder.likes.setText(String.valueOf((currentgoodnum + 1)));
                        finalViewHolder.likes_Image.setImageResource(R.drawable.ic_like_red);
                        finalViewHolder.likes_ripple.setClickable(false);

                        postSignupAsync(getActivity(), user.getPost_id(), position);
                    }
                });
            } else {
                viewHolder.likes_Image.setImageResource(R.drawable.ic_like_red);
                viewHolder.likes_ripple.setClickable(false);
            }

            viewHolder.comments_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                    //投稿に対するコメントが見れるダイアログを表示
                    View commentView = new CommentView(getActivity(), user.getPost_id());

                    new MaterialDialog.Builder(getActivity())
                            .customView(commentView, false)
                            .show();
                }
            });

            mViewHolderHash.put(viewHolder, user.getPost_id());
            return convertView;
        }
    }

    //名前部分のViewをクリックした時の処理
    class nameClickHandler implements Runnable {
        public void run() {
            Intent userintent = new Intent(getActivity(), FlexibleUserProfActivity.class);
            userintent.putExtra("username", clickedUsername);
            userintent.putExtra("picture", clickedUserpicture);
            userintent.putExtra("background", clickedUserbackground);
            startActivity(userintent);
            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    //店舗部分のViewをクリックした時の処理
    class restClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getActivity(), FlexibleTenpoActivity.class);
            intent.putExtra("restname", clickedRestname);
            intent.putExtra("locality", clickedLocality);
            intent.putExtra("lat", clickedLat);
            intent.putExtra("lon", clickedLon);
            intent.putExtra("phone", clickedPhoneNumber);
            intent.putExtra("homepage", clickedHomepage);
            intent.putExtra("category", clickedCategory);
            intent.putExtra("want_flag", clickedWant_flag);
            intent.putExtra("total_cheer_num", clickedTotal_cheer_num);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}

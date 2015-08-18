package com.inase.android.gocci.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

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
import com.inase.android.gocci.Activity.GocciTimelineActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.Event.PageChangeVideoStopEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.HlsRendererBuilder;
import com.inase.android.gocci.VideoPlayer.VideoPlayer;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kinagafuji on 15/06/11.
 */
public class TrendTimelineFragment extends Fragment implements AudioCapabilitiesReceiver.Listener, AppBarLayout.OnOffsetChangedListener,
        ObservableScrollViewCallbacks {

    private ObservableRecyclerView mTimelineRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<PostData> mTimelineusers = new ArrayList<>();
    private TrendTimelineAdapter mTrendTimelineAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private AppBarLayout appBarLayout;

    private Point mDisplaySize;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.ExoViewHolder, String> mViewHolderHash;  // Value: PosterId

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private AttributeSet mVideoAttr;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int mNextCount = 1;
    private boolean isEndScrioll = false;

    private FloatingActionButton fab;

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
                mTimelineRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getActivity(), "シェアが完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "キャンセルしました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getActivity(), "シェアに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(getActivity(), new TweetComposer());

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity().getApplicationContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = true;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline_latest_trend, container, false);

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        mTimelineRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTimelineRecyclerView.setLayoutManager(mLayoutManager);
        mTimelineRecyclerView.setHasFixedSize(true);
        mTimelineRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTimelineRecyclerView.setScrollViewCallbacks(this);
        mTimelineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                visibleItemCount = mTimelineRecyclerView.getChildCount();
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

                    /*
                    String post_id = mTimelineusers.get(mTimelineusers.size() - 1).getPost_id();
                    addUrl = "http://api-gocci.jp/gochi_rank/?post_id=" + post_id;
                    */

                    if (!isEndScrioll) {
                        getAddJsonAsync(getActivity(), mNextCount);
                    }
                    loading = true;
                }
            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.toukouButton);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);

        mTrendTimelineAdapter = new TrendTimelineAdapter(getActivity());

        if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
            if (Util.getConnectedState(getActivity()) == Util.NetworkStatus.MOBILE) {
                Toast.makeText(getActivity(), "回線が悪いので、動画が流れなくなります", Toast.LENGTH_LONG).show();
            }
            getSignupAsync(getActivity());
        } else {
            Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_LONG).show();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(getActivity());
                } else {
                    Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
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
    public void onResume() {
        super.onResume();
        // Subscriberとして登録する
        BusHolder.get().register(this);
        audioCapabilitiesReceiver.register();

        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Subscriberの登録を解除する
        BusHolder.get().unregister(this);
        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        audioCapabilitiesReceiver.unregister();
        // getPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);

        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    //動画のバックグラウンド再生を止める処理のはず。。。
    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        switch (event.position) {
            case 1:
                mPlayBlockFlag = false;
                //path!=nullで　viewholder!=nullじゃない　
                if (player != null) {
                    player.getPlayerControl().start();
                    Log.e("Otto発動", "動画再生復帰");
                } else {
                    preparePlayer(getPlayingViewHolder(), getVideoPath());
                }
                break;
            case 0:
                mPlayBlockFlag = true;
                //タイムライン以外のfragmentが可視化している場合
                if (player.getPlayerControl().isPlaying()) {
                    player.getPlayerControl().pause();
                    Log.e("DEBUG", "subscribe 動画再生停止");
                }
                Log.e("Otto発動", "動画再生停止");
                break;
        }
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        if (event.mMessage.equals("投稿が完了しました。")) {
            getRefreshAsync(getActivity());
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            if (mPlayingPostId != null && GocciTimelineActivity.mShowPosition == 1) {
                this.audioCapabilities = audioCapabilities;
                releasePlayer();
                preparePlayer(getPlayingViewHolder(), getVideoPath());
            }
        } else {
            player.setBackgrounded(false);
        }
    }

    private String getVideoPath() {
        final int position = mTimelineRecyclerView.getChildAdapterPosition(mTimelineRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mTrendTimelineAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        return userData.getMovie();
    }

    private void preparePlayer(final Const.ExoViewHolder viewHolder, String path) {
        if (player == null) {
            player = new VideoPlayer(new HlsRendererBuilder(getActivity(), com.google.android.exoplayer.util.Util.getUserAgent(getActivity(), "Gocci"), path,
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

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPopularAPI(), new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.e("TRENDTIMELINEACTIVITY", String.valueOf(response));
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        mTimelineusers.add(PostData.createPostData(jsonObject));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineRecyclerView.setAdapter(mTrendTimelineAdapter);
                //getTimelineDateJson(context);
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPopularAPI(), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mTimelineusers.clear();
                isEndScrioll = false;
                try {
                    Log.e("LATESTTIMELINEFRAGMENT", String.valueOf(response));
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        mTimelineusers.add(PostData.createPostData(jsonObject));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTrendTimelineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getRefresh finish");
                //mTimelineSwipe.setRefreshing(false);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getAddJsonAsync(final Context context, final int call) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPopularNextApi(call), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
                //mTimelineSwipe.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.e("TRENDTIMELINEFRAGMENT", String.valueOf(response));
                    if (response.length() != 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            mTimelineusers.add(PostData.createPostData(jsonObject));
                        }

                        mPlayingPostId = null;
                        mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                        mTrendTimelineAdapter.notifyDataSetChanged();

                        mNextCount++;
                    } else {
                        isEndScrioll = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss AddJson Finish");
                //mTimelineSwipe.setRefreshing(false);
            }

        });
    }

    private void changeMovie() {
        Log.e("DEBUG", "changeMovie called");
        // TODO:実装
        final int position = mTimelineRecyclerView.getChildAdapterPosition(mTimelineRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        if (mTrendTimelineAdapter.isEmpty()) {
            return;
        }
        if (position < 0) {
            return;
        }

        final PostData userData = mTrendTimelineAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            Log.d("DEBUG", "postId change");

            final Const.ExoViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                Log.d("DEBUG", "MOVIE::changeMovie 再生停止 postId:" + mPlayingPostId);
                Log.e("DEBUG", "changeMovie 動画再生停止");
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeRefreshLayout.setEnabled(i == 0);
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
            fab.hide();
        } else if (scrollState == ScrollState.DOWN) {
            fab.show();
        }
    }

    public class TrendTimelineAdapter extends RecyclerView.Adapter<Const.ExoViewHolder> {

        private Context mContext;

        public TrendTimelineAdapter(Context context) {
            mContext = context;
        }

        public PostData getItem(int position) {
            return mTimelineusers.get(position);
        }

        public boolean isEmpty() {
            return mTimelineusers.isEmpty();
        }

        @Override
        public Const.ExoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.cell_exo_timeline, parent, false);
            return new Const.ExoViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final Const.ExoViewHolder holder, final int position) {
            final PostData user = mTimelineusers.get(position);

            holder.user_name.setText(user.getUsername());

            holder.datetime.setText(user.getPost_date());

            if (!user.getMemo().equals("none")) {
                holder.comment.setText(user.getMemo());
            } else {
                holder.comment.setText("");
            }

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.circleImage);

            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GocciTimelineActivity activity = (GocciTimelineActivity) getActivity();
                    activity.onUserClicked(user.getPost_user_id(), user.getUsername());
                }
            });

            holder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GocciTimelineActivity activity = (GocciTimelineActivity) getActivity();
                    activity.onUserClicked(user.getPost_user_id(), user.getUsername());
                }
            });

            holder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(getActivity(), R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_normal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.violation:
                                    Util.setViolateDialog(getActivity(), user.getPost_id());
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
                    GocciTimelineActivity activity = (GocciTimelineActivity) getActivity();
                    activity.onTenpoClicked(user.getPost_rest_id(), user.getRestname());
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

                        Util.postGochiAsync(getActivity(), user);
                    }
                });
            } else {
                holder.likes_Image.setImageResource(R.drawable.ic_icon_beef_orange);
                holder.likes_ripple.setClickable(false);
            }

            holder.comments_ripple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    GocciTimelineActivity activity = (GocciTimelineActivity) getActivity();
                    activity.onCommentClicked(Integer.parseInt(user.getPost_id()));
                }
            });

            holder.share_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.getTransfer(getActivity()) != null) {
                        new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.facebook_share:
                                        Toast.makeText(getActivity(), "シェアの準備をしています", Toast.LENGTH_LONG).show();
                                        Util.facebookVideoShare(getActivity(), shareDialog, user.getShare());
                                        break;
                                    case R.id.twitter_share:
                                        Util.twitterShare(getActivity(), holder.mVideoThumbnail, user.getRestname());
                                        break;
                                    case R.id.other_share:
                                        Toast.makeText(getActivity(), "シェアの準備をしています", Toast.LENGTH_LONG).show();
                                        Util.instaVideoShare(getActivity(), user.getRestname(), user.getShare());
                                        break;
                                    case R.id.close:
                                        dialog.dismiss();
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(getActivity(), "もうちょっと待ってから押してみましょう", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mViewHolderHash.put(holder, user.getPost_id());
        }

        @Override
        public int getItemCount() {
            return mTimelineusers.size();
        }

    }
}

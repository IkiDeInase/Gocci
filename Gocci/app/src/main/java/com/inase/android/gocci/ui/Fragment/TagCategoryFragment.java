package com.inase.android.gocci.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.utils.video.HlsRendererBuilder;
import com.inase.android.gocci.utils.video.VideoPlayer;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.domain.model.pojo.PostData;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.TagCategoryActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class TagCategoryFragment extends Fragment implements AudioCapabilitiesReceiver.Listener, AppBarLayout.OnOffsetChangedListener,
        ObservableScrollViewCallbacks {

    private static final String ARG_KEY = "key";

    @Bind(R.id.list)
    ObservableRecyclerView list;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.empty_text)
    TextView emptyText;
    @Bind(R.id.empty_image)
    ImageView emptyImage;

    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private LinearLayoutManager mLayoutManager;
    private ArrayList<PostData> mTimelineusers = new ArrayList<>();
    private LatestTimelineAdapter mTimelineAdapter;

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

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private int position;

    public TagCategoryFragment() {
        // Required empty public constructor
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            changeMovie();
            if (mPlayingPostId != null) {
                list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                // スクロールしていない
                case RecyclerView.SCROLL_STATE_IDLE:
                    changeMovie();
                    break;
                // スクロール中
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                // はじいたとき
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }

            visibleItemCount = list.getChildCount();
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

                if (!isEndScrioll) {
                    getAddJsonAsync(getActivity(), mNextCount);
                }

                loading = true;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_KEY);
        }

        mDisplaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(getActivity(), getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getActivity(), getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity().getApplicationContext(), this);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        View view = inflater.inflate(R.layout.fragment_tag_category, container, false);
        ButterKnife.bind(this, view);

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);

        mLayoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(mLayoutManager);
        list.setHasFixedSize(true);
        list.setOverScrollMode(View.OVER_SCROLL_NEVER);
        list.addOnScrollListener(scrollListener);
        list.setScrollViewCallbacks(this);

        mTimelineAdapter = new LatestTimelineAdapter(getActivity());

        if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
            getSignupAsync(getActivity());
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }

        swipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(getActivity());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
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
        BusHolder.get().register(this);
        appBarLayout.addOnOffsetChangedListener(this);
        if (player == null) {
            if (mPlayingPostId != null && TagCategoryActivity.mShowPosition == position) {
                releasePlayer();
                if (Util.isMovieAutoPlay(getActivity())) {
                    preparePlayer(getPlayingViewHolder(), getVideoPath());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        if (getPlayingViewHolder() != null) {
            getPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);
        }
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        if (event.position == position) {
            mPlayBlockFlag = false;
            if (player != null) {
                if (!Util.isMovieAutoPlay(getActivity())) {
                    releasePlayer();
                } else {
                    releasePlayer();
                    preparePlayer(getPlayingViewHolder(), getVideoPath());
                }
            } else {
                if (!mTimelineusers.isEmpty()) {
                    if (Util.isMovieAutoPlay(getActivity())) {
                        preparePlayer(getPlayingViewHolder(), getVideoPath());
                    }
                }
            }
        } else {
            mPlayBlockFlag = true;
            if (player != null) {
                releasePlayer();
            }
        }
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        if (event.mMessage.equals(getString(R.string.videoposting_complete))) {
            getRefreshAsync(getActivity());
        }
    }

    @Subscribe
    public void subscribe(FilterTimelineEvent event) {
        if (event.currentPage == position) {
            list.scrollVerticallyToPosition(0);
            getFilterJsonAsync(event.filterUrl);
        }
    }

    @Subscribe
    public void subscribe(TimelineMuteChangeEvent event) {
        if (player != null) {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, event.mute);
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        if (mPlayingPostId != null && TagCategoryActivity.mShowPosition == position) {
            releasePlayer();
            if (Util.isMovieAutoPlay(getActivity())) {
                preparePlayer(getPlayingViewHolder(), getVideoPath());
            }
        }
        player.setBackgrounded(false);
    }

    private String getVideoPath() {
        final int position = list.getChildAdapterPosition(list.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mTimelineAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        return userData.getMovie();
    }

    private void preparePlayer(final Const.ExoViewHolder viewHolder, String path) {
        if (player == null) {
            player = new VideoPlayer(new HlsRendererBuilder(getActivity(), com.google.android.exoplayer.util.Util.getUserAgent(getActivity(), "Gocci"), path));
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
                        Toast.makeText(getActivity().getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
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
            playerNeedsPrepare = true;
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(viewHolder.mSquareVideoExo.getHolder().getSurface());
        player.setPlayWhenReady(true);

        if (SavedData.getSettingMute(getActivity()) == -1) {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, -1);
        } else {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, 0);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void changeMovie() {
        // TODO:実装
        final int position = list.getChildAdapterPosition(list.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        if (mTimelineAdapter.isEmpty()) {
            return;
        }
        if (position < 0) {
            return;
        }

        final PostData userData = mTimelineAdapter.getItem(position);
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
            if (Util.isMovieAutoPlay(getActivity())) {
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

    private int getPositionSortId() {
        int sort_id = 0;
        switch (position) {
            case 0:
                sort_id = TagCategoryActivity.mWaSort_id;
                break;
            case 1:
                sort_id = TagCategoryActivity.mYouSort_id;
                break;
            case 2:
                sort_id = TagCategoryActivity.mTyuuSort_id;
                break;
            case 3:
                sort_id = TagCategoryActivity.mCareSort_id;
                break;
            case 4:
                sort_id = TagCategoryActivity.mRamenSort_id;
                break;
            case 5:
                sort_id = TagCategoryActivity.mTakokuSort_id;
                break;
            case 6:
                sort_id = TagCategoryActivity.mCafeSort_id;
                break;
            case 7:
                sort_id = TagCategoryActivity.mSakeSort_id;
                break;
            case 8:
                sort_id = TagCategoryActivity.mSonotaSort_id;
                break;
        }
        return sort_id;
    }

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getCustomTagCategoryAPI(position + 2, getPositionSortId(), 0.0, 0.0, 0), new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                swipeContainer.setRefreshing(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        mTimelineusers.add(PostData.createPostData(jsonObject));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                list.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                list.setAdapter(mTimelineAdapter);
                //getTimelineDateJson(context);
            }

            @Override
            public void onFinish() {
                //                mTimelineDialog.dismiss();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                TagCategoryActivity.nowLocation = location;
                Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(getActivity()));
                Const.asyncHttpClient.get(context, Const.getCustomTagCategoryAPI(position + 2, getPositionSortId(),
                        TagCategoryActivity.nowLocation != null ? TagCategoryActivity.nowLocation.getLongitude() : 0.0,
                        TagCategoryActivity.nowLocation != null ? TagCategoryActivity.nowLocation.getLatitude() : 0.0, 0), new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        mTimelineusers.clear();
                        isEndScrioll = false;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                mTimelineusers.add(PostData.createPostData(jsonObject));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mPlayingPostId = null;
                        mViewHolderHash.clear();
                        list.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                        mTimelineAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFinish() {
                        //mTimelineSwipe.setRefreshing(false);
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void getAddJsonAsync(final Context context, final int call) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getCustomTagCategoryAPI(position + 2, getPositionSortId(),
                TagCategoryActivity.nowLocation != null ? TagCategoryActivity.nowLocation.getLongitude() : 0.0,
                TagCategoryActivity.nowLocation != null ? TagCategoryActivity.nowLocation.getLatitude() : 0.0, call), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                //mTimelineSwipe.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() != 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            mTimelineusers.add(PostData.createPostData(jsonObject));
                        }

                        mPlayingPostId = null;
                        list.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                        mTimelineAdapter.notifyDataSetChanged();

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
                //mTimelineSwipe.setRefreshing(false);
            }
        });
    }

    private void getFilterJsonAsync(String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(getActivity()));
        Const.asyncHttpClient.get(getActivity(), url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                swipeContainer.setRefreshing(true);
                list.scrollVerticallyToPosition(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                //mTimelineSwipe.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mTimelineusers.clear();
                isEndScrioll = false;
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        mTimelineusers.add(PostData.createPostData(jsonObject));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                list.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        swipeContainer.setEnabled(i == 0);
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

    public class LatestTimelineAdapter extends RecyclerView.Adapter<Const.ExoViewHolder> {

        private Context mContext;

        public LatestTimelineAdapter(Context context) {
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

            holder.mUserName.setText(user.getUsername());

            holder.mTimeText.setText(user.getPost_date());

            if (!user.getMemo().equals("none")) {
                holder.mComment.setText(user.getMemo());
            } else {
                holder.mComment.setText("");
            }

            holder.mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentActivity.startCommentActivity(Integer.parseInt(user.getPost_date()), getActivity());
                }
            });

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.mCircleImage);

            holder.mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagCategoryActivity activity = (TagCategoryActivity) getActivity();
                    activity.onUserClicked(user.getPost_user_id(), user.getUsername());
                }
            });

            holder.mCircleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagCategoryActivity activity = (TagCategoryActivity) getActivity();
                    activity.onUserClicked(user.getPost_user_id(), user.getUsername());
                }
            });

            holder.mMenuRipple.setOnClickListener(new View.OnClickListener() {
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
                        if (!Util.isMovieAutoPlay(getActivity())) {
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
                    TagCategoryActivity activity = (TagCategoryActivity) getActivity();
                    activity.onTenpoClicked(user.getPost_rest_id(), user.getRestname());
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

                        Util.postGochiAsync(getActivity(), user);
                    }
                });
            } else {
                holder.mLikesImage.setImageResource(R.drawable.ic_icon_beef_orange);
                holder.mLikesRipple.setClickable(false);
            }

            holder.mCommentsRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    TagCategoryActivity activity = (TagCategoryActivity) getActivity();
                    activity.onCommentClicked(Integer.parseInt(user.getPost_id()));
                }
            });

            holder.mShareRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.getTransfer(getActivity()) != null) {
                        new BottomSheet.Builder(mContext, R.style.BottomSheet_StyleDialog).sheet(R.menu.menu_share).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.facebook_share:
                                        Toast.makeText(getActivity(), getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                        Util.facebookVideoShare(getActivity(), shareDialog, user.getShare());
                                        break;
                                    case R.id.twitter_share:
                                        Util.twitterShare(getActivity(), holder.mVideoThumbnail, user.getRestname());
                                        break;
                                    case R.id.other_share:
                                        Toast.makeText(getActivity(), getString(R.string.preparing_share), Toast.LENGTH_LONG).show();
                                        Util.instaVideoShare(getActivity(), user.getRestname(), user.getShare());
                                        break;
                                    case R.id.close:
                                        dialog.dismiss();
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.preparing_share_error), Toast.LENGTH_SHORT).show();
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

package com.inase.android.gocci.ui.fragment;

import android.content.Context;
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
import com.inase.android.gocci.ui.view.SquareImageView;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.utils.video.HlsRendererBuilder;
import com.inase.android.gocci.utils.video.VideoPlayer;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.consts.ApiConst;
import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.datasource.repository.PostDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.usecase.LatestTimelineUseCase;
import com.inase.android.gocci.domain.usecase.LatestTimelineUseCaseImpl;
import com.inase.android.gocci.presenter.ShowLatestTimelinePresenter;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.FlexibleTenpoActivity;
import com.inase.android.gocci.ui.activity.FlexibleUserProfActivity;
import com.inase.android.gocci.ui.activity.GocciTimelineActivity;
import com.inase.android.gocci.ui.adapter.TimelineAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by kinagafuji on 15/06/08.
 */
public class LatestTimelineFragment extends Fragment implements AudioCapabilitiesReceiver.Listener, AppBarLayout.OnOffsetChangedListener,
        ObservableScrollViewCallbacks, ShowLatestTimelinePresenter.ShowLatestTimelineView, TimelineAdapter.TimelineCallback {

    @Bind(R.id.list)
    ObservableRecyclerView mTimelineRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.empty_text)
    TextView mEmptyText;
    @Bind(R.id.empty_image)
    ImageView mEmptyImage;

    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private LinearLayoutManager mLayoutManager;
    private ArrayList<PostData> mTimelineusers = new ArrayList<>();
    private TimelineAdapter mTimelineAdapter;

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

    private ShowLatestTimelinePresenter mPresenter;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            changeMovie();
            if (mPlayingPostId != null) {
                mTimelineRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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

                if (!isEndScrioll) {
                    mPresenter.getLatestTimelinePostData(ApiConst.TIMELINE_ADD,
                            Const.getCustomTimelineAPI(GocciTimelineActivity.mShowPosition, GocciTimelineActivity.mLatestSort_id, GocciTimelineActivity.mFollowSort_id,
                                    GocciTimelineActivity.nowLocation != null ? GocciTimelineActivity.nowLocation.getLongitude() : 0.0,
                                    GocciTimelineActivity.nowLocation != null ? GocciTimelineActivity.nowLocation.getLatitude() : 0.0, mNextCount));
                }

                loading = true;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        PostDataRepository postDataRepositoryImpl = PostDataRepositoryImpl.getRepository();
        LatestTimelineUseCase latestTimelineUseCaseImpl = LatestTimelineUseCaseImpl.getUseCase(postDataRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowLatestTimelinePresenter(latestTimelineUseCaseImpl);
        mPresenter.setLatestTimelineView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline_latest_trend, container, false);
        ButterKnife.bind(this, view);

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mTimelineRecyclerView.setLayoutManager(mLayoutManager);
        mTimelineRecyclerView.setHasFixedSize(true);
        mTimelineRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTimelineRecyclerView.addOnScrollListener(scrollListener);
        mTimelineRecyclerView.setScrollViewCallbacks(this);

        mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
        mTimelineAdapter.setTimelineCallback(this);

        if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
            mPresenter.getLatestTimelinePostData(ApiConst.TIMELINE_FIRST, Const.getLatestAPI());
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(getActivity());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
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
            if (mPlayingPostId != null && GocciTimelineActivity.mShowPosition == 0) {
                releasePlayer();
                if (Util.isMovieAutoPlay(getActivity())) {
                    preparePlayer(getPlayingViewHolder(), getVideoPath());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
        mPresenter.resume();
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
        mPresenter.pause();
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
        switch (event.position) {
            case 0:
                mPlayBlockFlag = false;
                if (player != null) {
                    if (!Util.isMovieAutoPlay(getActivity())) {
                        releasePlayer();
                    } else {
                        player.getPlayerControl().start();
                    }
                } else {
                    if (!mTimelineusers.isEmpty()) {
                        if (Util.isMovieAutoPlay(getActivity())) {
                            preparePlayer(getPlayingViewHolder(), getVideoPath());
                        }
                    }
                }
                break;
            case 1:
                mPlayBlockFlag = true;
                if (player != null) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().pause();
                    }
                }
                break;
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
        if (event.currentPage == 0) {
            mTimelineRecyclerView.scrollVerticallyToPosition(0);
            mPresenter.getLatestTimelinePostData(ApiConst.TIMELINE_FILTER, event.filterUrl);
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
        if (mPlayingPostId != null && GocciTimelineActivity.mShowPosition == 0) {
            releasePlayer();
            if (Util.isMovieAutoPlay(getActivity())) {
                preparePlayer(getPlayingViewHolder(), getVideoPath());
            }
        }
        player.setBackgrounded(false);
    }

    private String getVideoPath() {
        final int position = mTimelineRecyclerView.getChildAdapterPosition(mTimelineRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
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
        final int position = mTimelineRecyclerView.getChildAdapterPosition(mTimelineRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
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

    private void getRefreshAsync(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                GocciTimelineActivity.nowLocation = location;
                mPresenter.getLatestTimelinePostData(ApiConst.TIMELINE_REFRESH,
                        Const.getCustomTimelineAPI(GocciTimelineActivity.mShowPosition, GocciTimelineActivity.mLatestSort_id, GocciTimelineActivity.mFollowSort_id,
                                GocciTimelineActivity.nowLocation != null ? GocciTimelineActivity.nowLocation.getLongitude() : 0.0,
                                GocciTimelineActivity.nowLocation != null ? GocciTimelineActivity.nowLocation.getLatitude() : 0.0, 0));
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeContainer.setEnabled(i == 0);
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

    @Override
    public void showLoading() {
        mSwipeContainer.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void showNoResultCase() {
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoResultCase() {
        mEmptyImage.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError() {
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        mEmptyImage.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResult(int api, ArrayList<PostData> mPostData) {
        switch (api) {
            case ApiConst.TIMELINE_FIRST:
                mTimelineusers.addAll(mPostData);
                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case ApiConst.TIMELINE_REFRESH:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineAdapter.notifyDataSetChanged();
                break;
            case ApiConst.TIMELINE_ADD:
                if (mPostData.size() != 0) {
                    mPlayingPostId = null;
                    mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                    mTimelineAdapter.notifyDataSetChanged();
                    mNextCount++;
                } else {
                    isEndScrioll = true;
                }
                break;
            case ApiConst.TIMELINE_FILTER:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void successGochi(int position) {

    }

    @Override
    public void onUserClick(int user_id, String user_name) {
        FlexibleUserProfActivity.startUserProfActivity(user_id, user_name, getActivity());
    }

    @Override
    public void onRestClick(int rest_id, String rest_name) {
        FlexibleTenpoActivity.startTenpoActivity(rest_id, rest_name, getActivity());
    }

    @Override
    public void onCommentClick(String post_id) {
        CommentActivity.startCommentActivity(Integer.parseInt(post_id), getActivity());
    }

    @Override
    public void onVideoFrameClick() {
        if (player != null) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
            } else {
                player.getPlayerControl().start();
            }
        } else {
            if (!Util.isMovieAutoPlay(getActivity())) {
                releasePlayer();
                preparePlayer(getPlayingViewHolder(), getVideoPath());
            }
        }
    }

    @Override
    public void onFacebookShare(String share) {
        Util.facebookVideoShare(getActivity(), shareDialog, share);
    }

    @Override
    public void onTwitterShare(SquareImageView view, String rest_name) {
        Util.twitterShare(getActivity(), view, rest_name);
    }

    @Override
    public void onInstaShare(String share, String rest_name) {
        Util.instaVideoShare(getActivity(), rest_name, share);
    }

    @Override
    public void onHashHolder(Const.ExoViewHolder holder, String post_id) {
        mViewHolderHash.put(holder, post_id);
    }
}

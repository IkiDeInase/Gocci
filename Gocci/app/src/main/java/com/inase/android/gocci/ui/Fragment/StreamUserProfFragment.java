package com.inase.android.gocci.ui.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.event.ProfJsonEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.MyprofActivity;
import com.inase.android.gocci.ui.activity.TenpoActivity;
import com.inase.android.gocci.ui.activity.UserProfActivity;
import com.inase.android.gocci.ui.adapter.StreamUserProfAdapter;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.video.HlsRendererBuilder;
import com.inase.android.gocci.utils.video.VideoPlayer;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kinagafuji on 15/10/22.
 */
public class StreamUserProfFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener,
        AudioCapabilitiesReceiver.Listener, StreamUserProfAdapter.UserStreamProfCallback {

    @Bind(R.id.list)
    ObservableRecyclerView mTimelineRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    private AppBarLayout appBarLayout;

    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<PostData> mUsers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();
    private StreamUserProfAdapter mStreamUserProfAdapter;
    private ConcurrentHashMap<Const.StreamUserViewHolder, String> mStreamViewHolderHash;

    private Point mDisplaySize;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    int totalItemCount;
    private boolean isExist = false;

    private UserProfActivity activity;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private RecyclerView.OnScrollListener mStreamScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    streamChangeMovie();
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    mTracker = applicationGocci.getDefaultTracker();
                    mTracker.setScreenName("UserProfStream");
                    mTracker.send(new HitBuilders.EventBuilder().setAction("ScrollCount").setCategory("Public").setLabel(SavedData.getServerUserId(getActivity())).build());
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }

            totalItemCount = mLinearLayoutManager.getItemCount();

            isExist = totalItemCount != 0;

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (MyprofActivity.mShowPosition == 0) {
                streamChangeMovie();

                if (mPlayingPostId != null && !isExist) {
                    mTimelineRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            } else {
                mTimelineRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDisplaySize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity().getApplicationContext(), this);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        applicationGocci = (Application_Gocci) getActivity().getApplication();

        mPlayingPostId = null;
        mStreamViewHolderHash = new ConcurrentHashMap<>();

        activity = (UserProfActivity) getActivity();

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mTimelineRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTimelineRecyclerView.setHasFixedSize(true);
        mTimelineRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTimelineRecyclerView.addOnScrollListener(mStreamScrollListener);
        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    releasePlayer();
                    UserProfActivity activity = (UserProfActivity) getActivity();
                    activity.refreshJson();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
        if (player == null) {
            if (mPlayingPostId != null && MyprofActivity.mShowPosition == 0) {
                releasePlayer();
                if (Util.isMovieAutoPlay(getActivity())) {
                    streamPreparePlayer(getStreamPlayingViewHolder(), getVideoPath());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        if (getStreamPlayingViewHolder() != null) {
            getStreamPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);
        }
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.USER_PAGE) {
            if (event.apiCategory == Const.APICategory.SET_GOCHI) {
                mStreamUserProfAdapter.notifyItemChanged(mPost_ids.indexOf(event.id));
            } else if (event.apiCategory == Const.APICategory.UNSET_GOCHI) {
                mStreamUserProfAdapter.notifyItemChanged(mPost_ids.indexOf(event.id));
            }
        }
    }

    @Subscribe
    public void subscribe(TimelineMuteChangeEvent event) {
        if (player != null) {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, event.mute);
        }
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
                    if (!mUsers.isEmpty()) {
                        if (Util.isMovieAutoPlay(getActivity())) {
                            streamPreparePlayer(getStreamPlayingViewHolder(), getVideoPath());
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
    public void subscribe(ProfJsonEvent event) {
        mUsers.clear();
        mUsers.addAll(event.mData);
        mPost_ids.clear();
        mPost_ids.addAll(event.mPost_Ids);
        mSwipeContainer.setRefreshing(false);
        switch (event.mApi) {
            case GET_USER_FIRST:
                mStreamUserProfAdapter = new StreamUserProfAdapter(getActivity(), mUsers);
                mStreamUserProfAdapter.setUserProfCallback(this);
                mStreamViewHolderHash.clear();
                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTimelineRecyclerView.setAdapter(mStreamUserProfAdapter);
                break;
            case GET_USER_REFRESH:
                mPlayingPostId = null;
                mStreamViewHolderHash.clear();
                mTimelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mStreamUserProfAdapter.setData();
                break;
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        if (mPlayingPostId != null && MyprofActivity.mShowPosition == 0) {
            releasePlayer();
            if (Util.isMovieAutoPlay(getActivity())) {
                streamPreparePlayer(getStreamPlayingViewHolder(), getVideoPath());
            }
        }
        player.setBackgrounded(false);
    }

    private String getVideoPath() {
        final int position = mTimelineRecyclerView.getChildAdapterPosition(mTimelineRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mStreamUserProfAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        //return mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        return userData.getHls_movie();
    }

    private void streamPreparePlayer(final Const.StreamUserViewHolder viewHolder, String path) {
        if (player == null) {
            mTracker = applicationGocci.getDefaultTracker();
            mTracker.setScreenName("UserProfStream");
            mTracker.send(new HitBuilders.EventBuilder().setAction("PlayCount").setCategory("Movie").setLabel(mPlayingPostId).build());

            player = new VideoPlayer(new HlsRendererBuilder(getActivity(), com.google.android.exoplayer.util.Util.getUserAgent(getActivity(), "Gocci"), path));
            player.addListener(new VideoPlayer.Listener() {
                @Override
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case VideoPlayer.STATE_BUFFERING:
                            break;
                        case VideoPlayer.STATE_ENDED:
                            player.seekTo(0);
                            mTracker = applicationGocci.getDefaultTracker();
                            mTracker.setScreenName("UserProfStream");
                            mTracker.send(new HitBuilders.EventBuilder().setAction("PlayCount").setCategory("Movie").setLabel(mPlayingPostId).build());
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
            //player.seekTo(playerPosition);
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

    private void streamChangeMovie() {
        // TODO:実装
        if (mStreamUserProfAdapter == null) {
            return;
        }
        final int position = mTimelineRecyclerView.getChildAdapterPosition(mTimelineRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        if (mStreamUserProfAdapter.isEmpty()) {
            return;
        }
        if (position < 0) {
            return;
        }

        final PostData userData = mStreamUserProfAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {

            // 前回の動画再生停止処理
            final Const.StreamUserViewHolder oldViewHolder = getStreamPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final Const.StreamUserViewHolder currentViewHolder = getStreamPlayingViewHolder();
            if (mPlayBlockFlag) {
                return;
            }

            final String path = userData.getHls_movie();
            releasePlayer();
            if (Util.isMovieAutoPlay(getActivity())) {
                streamPreparePlayer(currentViewHolder, path);
            }
        }
    }

    private Const.StreamUserViewHolder getStreamPlayingViewHolder() {
        Const.StreamUserViewHolder viewHolder = null;
        if (mPlayingPostId != null) {
            for (Map.Entry<Const.StreamUserViewHolder, String> entry : mStreamViewHolderHash.entrySet()) {
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
        mSwipeContainer.setEnabled(i == 0);
    }

    @Override
    public void onStreamRestClick(String rest_id, String rest_name) {
        TenpoActivity.startTenpoActivity(rest_id, rest_name, getActivity());
    }

    @Override
    public void onStreamCommentClick(String post_id) {
        CommentActivity.startCommentActivity(post_id, false, getActivity());
    }

    @Override
    public void onStreamVideoFrameClick(PostData data) {
        if (player != null) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
            } else {
                player.getPlayerControl().start();
            }
        } else {
            if (!Util.isMovieAutoPlay(getActivity())) {
                releasePlayer();
                streamPreparePlayer(getStreamPlayingViewHolder(), getVideoPath());
            }
        }
    }

    @Override
    public void onGochiTap() {
        if (activity != null) {
            activity.setGochiLayout();
        } else {
            activity = (UserProfActivity) getActivity();
            activity.setGochiLayout();
        }
    }

    @Override
    public void onGochiClick(String post_id, Const.APICategory apiCategory) {
        if (activity != null) {
            activity.postGochi(post_id, apiCategory);
        } else {
            activity = (UserProfActivity) getActivity();
            activity.postGochi(post_id, apiCategory);
        }
    }

    @Override
    public void onFacebookShare(String share, String rest_name) {
        UserProfActivity activity = (UserProfActivity) getActivity();
        activity.shareVideoPost(25, share, rest_name);
    }

    @Override
    public void onTwitterShare(String share, String rest_name) {
        UserProfActivity activity = (UserProfActivity) getActivity();
        activity.shareVideoPost(26, share, rest_name);
    }

    @Override
    public void onInstaShare(String share, String rest_name) {
        UserProfActivity activity = (UserProfActivity) getActivity();
        activity.shareVideoPost(27, share, rest_name);
    }

    @Override
    public void onStreamHashHolder(Const.StreamUserViewHolder holder, String post_id) {
        mStreamViewHolderHash.put(holder, post_id);
    }
}

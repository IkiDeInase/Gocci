package com.inase.android.gocci.ui.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.ProfJsonEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.FlexibleTenpoActivity;
import com.inase.android.gocci.ui.activity.GocciMyprofActivity;
import com.inase.android.gocci.ui.adapter.GridProfAdapter;
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
 * Created by kinagafuji on 15/10/21.
 */
public class GridMyProfFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener,
        AudioCapabilitiesReceiver.Listener, GridProfAdapter.GridProfCallback {

    @Bind(R.id.list)
    ObservableRecyclerView mTimelineRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    private AppBarLayout appBarLayout;

    private StaggeredGridLayoutManager mGridLayoutManager;
    private ArrayList<PostData> mUsers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();
    private GridProfAdapter mGridProfAdapter;
    private ConcurrentHashMap<Const.TwoCellViewHolder, String> mGridViewHolderHash;

    private Point mDisplaySize;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    int totalItemCount;
    private boolean isExist = false;

    private RecyclerView.OnScrollListener mGridScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (mPlayingPostId != null) {
                        int position = mPost_ids.indexOf(mPlayingPostId);
                        int[] array = mGridLayoutManager.findFirstVisibleItemPositions(null);
                        int[] array2 = mGridLayoutManager.findLastVisibleItemPositions(null);

                        if (array[0] >= position || position >= array2[1]) {
                            Const.TwoCellViewHolder oldViewHolder = getGridPlayingViewHolder();
                            if (oldViewHolder != null) {
                                oldViewHolder.mSquareImage.setVisibility(View.VISIBLE);
                            }
                            mPlayingPostId = null;
                            releasePlayer();
                        }
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = true;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        mPlayingPostId = null;
        mGridViewHolderHash = new ConcurrentHashMap<>();

        mGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mTimelineRecyclerView.setLayoutManager(mGridLayoutManager);
        mTimelineRecyclerView.setHasFixedSize(true);
        mTimelineRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTimelineRecyclerView.addOnScrollListener(mGridScrollListener);
        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    releasePlayer();
                    GocciMyprofActivity activity = (GocciMyprofActivity) getActivity();
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
        releasePlayer();
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
        if (getGridPlayingViewHolder() != null) {
            getGridPlayingViewHolder().mSquareImage.setVisibility(View.VISIBLE);
        }
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
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
            case 1:
                mPlayBlockFlag = false;
                releasePlayer();
                break;
            case 0:
                mPlayBlockFlag = true;
                releasePlayer();
                if (getGridPlayingViewHolder() != null) {
                    getGridPlayingViewHolder().mSquareImage.setVisibility(View.VISIBLE);
                    mPlayingPostId = null;
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
            case Const.USERPAGE_FIRST:
                mGridProfAdapter = new GridProfAdapter(getActivity(), mUsers);
                mGridProfAdapter.setMyProfCallback(this);
                mGridViewHolderHash.clear();
                mTimelineRecyclerView.setAdapter(mGridProfAdapter);
                break;
            case Const.USERPAGE_REFRESH:
                mPlayingPostId = null;
                mGridViewHolderHash.clear();
                mGridProfAdapter.setData();
                break;
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        if (mPlayingPostId != null && GocciMyprofActivity.mShowPosition == 1) {
            releasePlayer();
        }
        player.setBackgrounded(false);
    }

    private void gridPreparePlayer(final Const.TwoCellViewHolder viewHolder, String path) {
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
                    viewHolder.mSquareImage.setVisibility(View.GONE);
                    viewHolder.mAspectFrame.setAspectRatio(
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
        player.setSurface(viewHolder.mSquareExoVideo.getHolder().getSurface());
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

    private void gridChangeMovie(PostData postData) {
        // TODO:実装
        if (mPlayingPostId != null) {
            // 前回の動画再生停止処理
            final Const.TwoCellViewHolder oldViewHolder = getGridPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mSquareImage.setVisibility(View.VISIBLE);
            }

            if (mPlayingPostId.equals(postData.getPost_id())) {
                return;
            }
        }
        mPlayingPostId = postData.getPost_id();
        final Const.TwoCellViewHolder currentViewHolder = getGridPlayingViewHolder();
        if (mPlayBlockFlag) {
            return;
        }

        final String path = postData.getMovie();
        releasePlayer();
        gridPreparePlayer(currentViewHolder, path);
    }

    private Const.TwoCellViewHolder getGridPlayingViewHolder() {
        Const.TwoCellViewHolder viewHolder = null;
        if (mPlayingPostId != null) {
            for (Map.Entry<Const.TwoCellViewHolder, String> entry : mGridViewHolderHash.entrySet()) {
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
    public void onGridRestClick(int rest_id, String rest_name) {
        FlexibleTenpoActivity.startTenpoActivity(rest_id, rest_name, getActivity());
    }

    @Override
    public void onGridCommentClick(int post_id) {
        CommentActivity.startCommentActivity(post_id, true, getActivity());
    }

    @Override
    public void onGridVideoFrameClick(PostData data) {
        if (player != null && mPlayingPostId.equals(data.getPost_id())) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
            } else {
                player.getPlayerControl().start();
            }
        } else {
            gridChangeMovie(data);
        }
    }

    @Override
    public void onGridVideoFrameLongClick(String post_id, int position) {
        GocciMyprofActivity activity = (GocciMyprofActivity) getActivity();
        activity.setDeleteDialog(post_id, position);
    }

    @Override
    public void onGridHashHolder(Const.TwoCellViewHolder holder, String post_id) {
        mGridViewHolderHash.put(holder, post_id);
    }
}

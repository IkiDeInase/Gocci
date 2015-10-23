package com.inase.android.gocci.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.datasource.repository.PostDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.NearTimelineUseCase;
import com.inase.android.gocci.domain.usecase.NearTimelineUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.presenter.ShowNearTimelinePresenter;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.FlexibleTenpoActivity;
import com.inase.android.gocci.ui.activity.FlexibleUserProfActivity;
import com.inase.android.gocci.ui.activity.GocciTimelineActivity;
import com.inase.android.gocci.ui.adapter.TimelineAdapter;
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
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class NearTimelineFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, AudioCapabilitiesReceiver.Listener,
        ObservableScrollViewCallbacks, ShowNearTimelinePresenter.ShowNearTimelineView, TimelineAdapter.TimelineCallback {

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

    private StaggeredGridLayoutManager mLayoutManager;
    private ArrayList<PostData> mTimelineusers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();
    private TimelineAdapter mTimelineAdapter;

    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.TwoCellViewHolder, String> mViewHolderHash;  // Value: PosterId

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int mNextCount = 1;
    private boolean isEndScrioll = false;

    private ShowNearTimelinePresenter mPresenter;

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (mPlayingPostId != null) {
                        int position = mPost_ids.indexOf(mPlayingPostId);
                        int[] array = mLayoutManager.findFirstVisibleItemPositions(null);
                        int[] array2 = mLayoutManager.findLastVisibleItemPositions(null);

                        if (array[0] >= position || position >= array2[1]) {
                            Const.TwoCellViewHolder oldViewHolder = getPlayingViewHolder();
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
            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            int[] firstVisibleItems = null;
            firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
            if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                pastVisibleItems = firstVisibleItems[0];
            }

            if (loading) {
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    loading = false;
                    if (!isEndScrioll) {
                        mPresenter.getNearTimelinePostData(Const.TIMELINE_ADD, Const.getCustomTimelineAPI(0,
                                GocciTimelineActivity.mNearSort_id, GocciTimelineActivity.mNearCategory_id, GocciTimelineActivity.mNearValue_id,
                                GocciTimelineActivity.mLongitude, GocciTimelineActivity.mLatitude, mNextCount));
                    }
                }
            }
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

        PostDataRepository postDataRepositoryImpl = PostDataRepositoryImpl.getRepository();
        NearTimelineUseCase nearTimelineUseCaseImpl = NearTimelineUseCaseImpl.getUseCase(postDataRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowNearTimelinePresenter(nearTimelineUseCaseImpl);
        mPresenter.setNearTimelineView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mTimelineRecyclerView.setLayoutManager(mLayoutManager);
        mTimelineRecyclerView.setHasFixedSize(true);
        mTimelineRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTimelineRecyclerView.setScrollViewCallbacks(this);
        mTimelineRecyclerView.addOnScrollListener(scrollListener);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);

        if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
            getSignupAsync(getActivity());
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(getActivity()) != Util.NetworkStatus.OFF) {
                    releasePlayer();
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
        switch (requestCode) {
            case 123:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    GocciTimelineActivity.mLongitude = bundle.getDouble("lon");
                    GocciTimelineActivity.mLatitude = bundle.getDouble("lat");
                    GocciTimelineActivity.mNearCategory_id = 0;
                    GocciTimelineActivity.mNearValue_id = 0;
                    mPresenter.getNearTimelinePostData(Const.TIMELINE_REFRESH, Const.getCustomTimelineAPI(0,
                            GocciTimelineActivity.mNearSort_id, GocciTimelineActivity.mNearCategory_id, GocciTimelineActivity.mNearValue_id,
                            GocciTimelineActivity.mLongitude, GocciTimelineActivity.mLatitude, 0));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
        releasePlayer();
        appBarLayout.addOnOffsetChangedListener(this);
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
            getPlayingViewHolder().mSquareImage.setVisibility(View.VISIBLE);
        }
        mPresenter.pause();
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
        switch (event.position) {
            case 0:
                mPlayBlockFlag = false;
                releasePlayer();
                break;
            case 1:
            case 2:
                mPlayBlockFlag = true;
                releasePlayer();
                if (getPlayingViewHolder() != null) {
                    getPlayingViewHolder().mSquareImage.setVisibility(View.VISIBLE);
                    mPlayingPostId = null;
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
            mPresenter.getNearTimelinePostData(Const.TIMELINE_FILTER, event.filterUrl);
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
        }
        player.setBackgrounded(false);
    }

    private void getSignupAsync(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                GocciTimelineActivity.mLongitude = location.getLongitude();
                GocciTimelineActivity.mLatitude = location.getLatitude();
                mPresenter.getNearTimelinePostData(Const.TIMELINE_FIRST, Const.getCustomTimelineAPI(0,
                        GocciTimelineActivity.mNearSort_id, GocciTimelineActivity.mNearCategory_id, GocciTimelineActivity.mNearValue_id,
                        GocciTimelineActivity.mLongitude, GocciTimelineActivity.mLatitude, 0));
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                GocciTimelineActivity.mLongitude = location.getLongitude();
                GocciTimelineActivity.mLatitude = location.getLatitude();
                GocciTimelineActivity.mNearCategory_id = 0;
                GocciTimelineActivity.mNearValue_id = 0;
                mPresenter.getNearTimelinePostData(Const.TIMELINE_REFRESH, Const.getCustomTimelineAPI(0,
                        GocciTimelineActivity.mNearSort_id, GocciTimelineActivity.mNearCategory_id, GocciTimelineActivity.mNearValue_id,
                        GocciTimelineActivity.mLongitude, GocciTimelineActivity.mLatitude, 0));
                GocciTimelineActivity activity = (GocciTimelineActivity) getActivity();
                activity.setNowLocationTitle();
            }
        });
    }

    private void preparePlayer(final Const.TwoCellViewHolder viewHolder, String path) {
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

    private void changeMovie(PostData postData) {
        // TODO:実装
        if (mPlayingPostId != null) {
            // 前回の動画再生停止処理
            final Const.TwoCellViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mSquareImage.setVisibility(View.VISIBLE);
            }

            if (mPlayingPostId.equals(postData.getPost_id())) {
                return;
            }
        }
        mPlayingPostId = postData.getPost_id();
        final Const.TwoCellViewHolder currentViewHolder = getPlayingViewHolder();
        if (mPlayBlockFlag) {
            return;
        }

        final String path = postData.getMovie();
        releasePlayer();
        preparePlayer(currentViewHolder, path);
    }

    private Const.TwoCellViewHolder getPlayingViewHolder() {
        Const.TwoCellViewHolder viewHolder = null;
        if (mPlayingPostId != null) {
            for (Map.Entry<Const.TwoCellViewHolder, String> entry : mViewHolderHash.entrySet()) {
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
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

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
    public void showNoResultCase(int api) {
        switch (api) {
            case Const.TIMELINE_FIRST:
                mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
                mTimelineAdapter.setTimelineCallback(this);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case Const.TIMELINE_REFRESH:
                mTimelineusers.clear();
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mTimelineAdapter.setData();
                break;
            case Const.TIMELINE_FILTER:
                mTimelineusers.clear();
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mTimelineAdapter.setData();
                break;
        }
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
        Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        switch (api) {
            case Const.TIMELINE_FIRST:
                mTimelineusers.addAll(mPostData);
                mPost_ids.addAll(post_ids);
                mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
                mTimelineAdapter.setTimelineCallback(this);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case Const.TIMELINE_REFRESH:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                mPost_ids.clear();
                mPost_ids.addAll(post_ids);
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineAdapter.setData();
                break;
            case Const.TIMELINE_ADD:
                if (mPostData.size() != 0) {
                    mPlayingPostId = null;
                    mTimelineusers.addAll(mPostData);
                    mPost_ids.addAll(post_ids);
                    mTimelineAdapter.setData();
                    mNextCount++;
                } else {
                    isEndScrioll = true;
                }
                break;
            case Const.TIMELINE_FILTER:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                mPost_ids.clear();
                mPost_ids.addAll(post_ids);
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineAdapter.setData();
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
    public void onCommentClick(int post_id) {
        CommentActivity.startCommentActivity(post_id, false, getActivity());
    }

    @Override
    public void onVideoFrameClick(PostData data) {
        if (player != null && mPlayingPostId.equals(data.getPost_id())) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
            } else {
                player.getPlayerControl().start();
            }
        } else {
            changeMovie(data);
        }
    }

    @Override
    public void onHashHolder(Const.TwoCellViewHolder holder, String post_id) {
        mViewHolderHash.put(holder, post_id);
    }
}

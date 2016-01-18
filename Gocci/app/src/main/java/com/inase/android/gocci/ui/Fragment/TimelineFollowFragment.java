package com.inase.android.gocci.ui.fragment;

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
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.GochiRepository;
import com.inase.android.gocci.datasource.repository.GochiRepositoryImpl;
import com.inase.android.gocci.datasource.repository.PostsDataRepository;
import com.inase.android.gocci.datasource.repository.PostsDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.GochiUseCaseImpl;
import com.inase.android.gocci.domain.usecase.TimelineFollowUseCase;
import com.inase.android.gocci.domain.usecase.TimelineFollowUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.presenter.ShowFollowTimelinePresenter;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.TenpoActivity;
import com.inase.android.gocci.ui.activity.TimelineActivity;
import com.inase.android.gocci.ui.activity.UserProfActivity;
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

/**
 * Created by kinagafuji on 15/06/11.
 */
public class TimelineFollowFragment extends Fragment implements AudioCapabilitiesReceiver.Listener, AppBarLayout.OnOffsetChangedListener,
        ObservableScrollViewCallbacks, ShowFollowTimelinePresenter.ShowFollowTimelineView, TimelineAdapter.TimelineCallback {

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
    private ArrayList<TwoCellData> mTimelineusers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();
    private TimelineAdapter mTimelineAdapter;

    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<Const.TwoCellViewHolder, String> mViewHolderHash;  // Value: PosterId

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal;
    private int mNextCount = 1;
    private boolean isEndScrioll = false;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private ShowFollowTimelinePresenter mPresenter;

    private TimelineActivity activity;

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

                        if (array[1] >= position || position >= array2[0]) {
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
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading) {
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    loading = true;
                    if (!isEndScrioll) {
                        API3.Util.GetFollowlineLocalCode localCode = API3.Impl.getRepository().GetFollowlineParameterRegex(String.valueOf(mNextCount), TimelineActivity.mFollowCategory_id != 0 ? String.valueOf(TimelineActivity.mFollowCategory_id) : null,
                                TimelineActivity.mFollowValue_id != 0 ? String.valueOf(TimelineActivity.mFollowValue_id) : null);
                        if (localCode == null) {
                            mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_ADD, API3.Util.getGetFollowlineAPI(
                                    String.valueOf(mNextCount),
                                    TimelineActivity.mFollowCategory_id != 0 ? String.valueOf(TimelineActivity.mFollowCategory_id) : null,
                                    TimelineActivity.mFollowValue_id != 0 ? String.valueOf(TimelineActivity.mFollowValue_id) : null));
                        } else {
                            Toast.makeText(getActivity(), API3.Util.GetFollowlineLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                        }
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

        API3 api3Impl = API3.Impl.getRepository();
        PostsDataRepository postsDataRepositoryImpl = PostsDataRepositoryImpl.getRepository(api3Impl);
        TimelineFollowUseCase followtTimelineUseCaseImpl = TimelineFollowUseCaseImpl.getUseCase(postsDataRepositoryImpl, UIThread.getInstance());
        GochiRepository gochiRepository = GochiRepositoryImpl.getRepository(api3Impl);
        GochiUseCase gochiUseCase = GochiUseCaseImpl.getUseCase(gochiRepository, UIThread.getInstance());
        mPresenter = new ShowFollowTimelinePresenter(followtTimelineUseCaseImpl, gochiUseCase);
        mPresenter.setFollowTimelineView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = true;
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.bind(this, view);

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        activity = (TimelineActivity) getActivity();

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mTimelineRecyclerView.setLayoutManager(mLayoutManager);
        mTimelineRecyclerView.setHasFixedSize(true);
        mTimelineRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTimelineRecyclerView.setScrollViewCallbacks(this);
        mTimelineRecyclerView.addOnScrollListener(scrollListener);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);

        API3.Util.GetFollowlineLocalCode localCode = API3.Impl.getRepository().GetFollowlineParameterRegex(null, null, null);
        if (localCode == null) {
            mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_FIRST, API3.Util.getGetFollowlineAPI(null, null, null));
        } else {
            Toast.makeText(getActivity(), API3.Util.GetFollowlineLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
        appBarLayout.addOnOffsetChangedListener(this);
        releasePlayer();
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
        if (event.position == 2) {
            mPlayBlockFlag = false;
            releasePlayer();
        } else {
            mPlayBlockFlag = true;
            releasePlayer();
            if (getPlayingViewHolder() != null) {
                getPlayingViewHolder().mSquareImage.setVisibility(View.VISIBLE);
                mPlayingPostId = null;
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
        if (event.currentPage == 2) {
            API3.Util.GetFollowlineLocalCode localCode = API3.Impl.getRepository().GetFollowlineParameterRegex(null, TimelineActivity.mFollowCategory_id != 0 ? String.valueOf(TimelineActivity.mFollowCategory_id) : null,
                    TimelineActivity.mFollowValue_id != 0 ? String.valueOf(TimelineActivity.mFollowValue_id) : null);
            if (localCode == null) {
                mTimelineRecyclerView.scrollVerticallyToPosition(0);
                mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_FILTER, event.filterUrl);
            } else {
                Toast.makeText(getActivity(), API3.Util.GetFollowlineLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
            }
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
        if (mPlayingPostId != null && TimelineActivity.mShowPosition == 2) {
            releasePlayer();
        }
        player.setBackgrounded(false);
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

    private void getRefreshAsync(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                TimelineActivity.mLongitude = String.valueOf(location.getLongitude());
                TimelineActivity.mLatitude = String.valueOf(location.getLatitude());
                TimelineActivity.mFollowValue_id = 0;
                TimelineActivity.mFollowCategory_id = 0;

                API3.Util.GetFollowlineLocalCode localCode = API3.Impl.getRepository().GetFollowlineParameterRegex(null, null, null);
                if (localCode == null) {
                    mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_REFRESH, API3.Util.getGetFollowlineAPI(
                            null, null, null));
                } else {
                    Toast.makeText(getActivity(), API3.Util.GetFollowlineLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeMovie(TwoCellData postData) {
        if (mPlayingPostId != null) {
            // 前回の動画再生停止処理
            final Const.TwoCellViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mSquareImage.setVisibility(View.VISIBLE);
            }

            if (postData.getPost_id().equals(mPlayingPostId)) {
                return;
            }
        }
        mPlayingPostId = postData.getPost_id();
        final Const.TwoCellViewHolder currentViewHolder = getPlayingViewHolder();
        if (mPlayBlockFlag) {
            return;
        }

        final String path = postData.getHls_movie();
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
    public void showEmpty(Const.APICategory api) {
        switch (api) {
            case GET_FOLLOWLINE_FIRST:
                mTimelineAdapter = new TimelineAdapter(getActivity(), Const.TimelineCategory.FOLLOWLINE, mTimelineusers);
                mTimelineAdapter.setTimelineCallback(this);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case GET_FOLLOWLINE_REFRESH:
                mTimelineusers.clear();
                isEndScrioll = false;
                previousTotal = 0;
                mNextCount = 1;
                mPlayingPostId = null;
                mTimelineAdapter.setData();
                break;
            case GET_FOLLOWLINE_FILTER:
                mTimelineusers.clear();
                isEndScrioll = false;
                previousTotal = 0;
                mNextCount = 1;
                mPlayingPostId = null;
                mTimelineAdapter.setData();
                break;
        }
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        mEmptyImage.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResult(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        switch (api) {
            case GET_FOLLOWLINE_FIRST:
                mTimelineusers.addAll(mPostData);
                mPost_ids.addAll(post_ids);
                mTimelineAdapter = new TimelineAdapter(getActivity(), Const.TimelineCategory.FOLLOWLINE, mTimelineusers);
                mTimelineAdapter.setTimelineCallback(this);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case GET_FOLLOWLINE_REFRESH:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                mPost_ids.clear();
                mPost_ids.addAll(post_ids);
                isEndScrioll = false;
                previousTotal = 0;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineAdapter.setData();

                if (activity != null) {
                    activity.refreshSheet();
                } else {
                    activity = (TimelineActivity) getActivity();
                    activity.refreshSheet();
                }
                break;
            case GET_FOLLOWLINE_ADD:
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
            case GET_FOLLOWLINE_FILTER:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                mPost_ids.clear();
                mPost_ids.addAll(post_ids);
                isEndScrioll = false;
                previousTotal = 0;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineAdapter.setData();
                break;
        }
    }

    @Override
    public void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(getActivity(), api, globalCode);
        mSwipeContainer.setRefreshing(false);
        if (api == Const.APICategory.GET_FOLLOWLINE_FIRST) {
            mTimelineAdapter = new TimelineAdapter(getActivity(), Const.TimelineCategory.FOLLOWLINE, mTimelineusers);
            mTimelineAdapter.setTimelineCallback(this);
            mTimelineRecyclerView.setAdapter(mTimelineAdapter);
        }
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        mSwipeContainer.setRefreshing(false);
        if (api == Const.APICategory.GET_FOLLOWLINE_FIRST) {
            mTimelineAdapter = new TimelineAdapter(getActivity(), Const.TimelineCategory.FOLLOWLINE, mTimelineusers);
            mTimelineAdapter.setTimelineCallback(this);
            mTimelineRecyclerView.setAdapter(mTimelineAdapter);
        }
    }

    @Override
    public void gochiSuccess(Const.APICategory api, String post_id) {

    }

    @Override
    public void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        Application_Gocci.resolveOrHandleGlobalError(getActivity(), api, globalCode);
        if (api == Const.APICategory.SET_GOCHI) {
            mTimelineusers.get(mPost_ids.indexOf(post_id)).setGochi_flag(false);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            mTimelineusers.get(mPost_ids.indexOf(post_id)).setGochi_flag(true);
        }
        mTimelineAdapter.notifyItemChanged(mPost_ids.indexOf(post_id));
    }

    @Override
    public void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        if (api == Const.APICategory.SET_GOCHI) {
            mTimelineusers.get(mPost_ids.indexOf(post_id)).setGochi_flag(false);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            mTimelineusers.get(mPost_ids.indexOf(post_id)).setGochi_flag(true);
        }
        mTimelineAdapter.notifyItemChanged(mPost_ids.indexOf(post_id));
    }

    @Override
    public void onUserClick(String user_id, String user_name) {
        UserProfActivity.startUserProfActivity(user_id, user_name, getActivity());
    }

    @Override
    public void onRestClick(String rest_id, String rest_name) {
        TenpoActivity.startTenpoActivity(rest_id, rest_name, getActivity());
    }

    @Override
    public void onCommentClick(String post_id) {
        CommentActivity.startCommentActivity(post_id, false, getActivity());
    }

    @Override
    public void onGochiTap() {
        if (activity != null) {
            activity.setGochiLayout();
        } else {
            activity = (TimelineActivity) getActivity();
            activity.setGochiLayout();
        }
    }

    @Override
    public void onGochiClick(String post_id, Const.APICategory apiCategory) {
        if (apiCategory == Const.APICategory.SET_GOCHI) {
            API3.Util.SetGochiLocalCode postGochiLocalCode = API3.Impl.getRepository().SetGochiParameterRegex(post_id);
            if (postGochiLocalCode == null) {
                mPresenter.postGochi(Const.APICategory.SET_GOCHI, API3.Util.getSetGochiAPI(post_id), post_id);
            } else {
                Toast.makeText(getActivity(), API3.Util.SetGochiLocalCodeMessageTable(postGochiLocalCode), Toast.LENGTH_SHORT).show();
            }
        } else if (apiCategory == Const.APICategory.UNSET_GOCHI) {
            API3.Util.UnsetGochiLocalCode unpostGochiLocalCode = API3.Impl.getRepository().UnsetGochiParameterRegex(post_id);
            if (unpostGochiLocalCode == null) {
                mPresenter.postGochi(Const.APICategory.UNSET_GOCHI, API3.Util.getUnsetGochiAPI(post_id), post_id);
            } else {
                Toast.makeText(getActivity(), API3.Util.UnsetGochiLocalCodeMessageTable(unpostGochiLocalCode), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewRecycled(Const.TwoCellViewHolder holder) {

    }

    @Override
    public void onVideoFrameClick(TwoCellData data) {
        if (player != null && data.getPost_id().equals(mPlayingPostId)) {
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

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_FOLLOWLINE_FIRST:
                mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_FIRST, API3.Util.getGetFollowlineAPI(null, null, null));
                break;
            case GET_FOLLOWLINE_REFRESH:
                mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_REFRESH, API3.Util.getGetFollowlineAPI(
                        null, null, null));
                break;
            case GET_FOLLOWLINE_ADD:
                mPresenter.getFollowTimelinePostData(Const.APICategory.GET_FOLLOWLINE_ADD, API3.Util.getGetFollowlineAPI(
                        String.valueOf(mNextCount),
                        TimelineActivity.mFollowCategory_id != 0 ? String.valueOf(TimelineActivity.mFollowCategory_id) : null,
                        TimelineActivity.mFollowValue_id != 0 ? String.valueOf(TimelineActivity.mFollowValue_id) : null));
                break;
            case GET_FOLLOWLINE_FILTER:

                break;
            default:
                break;
        }
    }
}

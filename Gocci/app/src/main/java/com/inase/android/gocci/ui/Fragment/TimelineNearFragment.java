package com.inase.android.gocci.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.datasource.repository.PostDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.GochiUseCaseImpl;
import com.inase.android.gocci.domain.usecase.TimelineNearUseCase;
import com.inase.android.gocci.domain.usecase.TimelineNearUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.presenter.ShowNearTimelinePresenter;
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

public class TimelineNearFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, AudioCapabilitiesReceiver.Listener,
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
    private ArrayList<TwoCellData> mTimelineusers = new ArrayList<>();
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
                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    loading = false;
                    if (!isEndScrioll) {
                        API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                        if (localCode == null) {
                            mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_ADD, API3.Util.getGetNearlineCustomAPI(
                                    TimelineActivity.mLongitude, TimelineActivity.mLatitude, mNextCount,
                                    TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                        } else {
                            Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
        PostDataRepository postDataRepositoryImpl = PostDataRepositoryImpl.getRepository(api3Impl);
        TimelineNearUseCase timelineNearUseCaseImpl = TimelineNearUseCaseImpl.getUseCase(postDataRepositoryImpl, UIThread.getInstance());
        GochiRepository gochiRepository = GochiRepositoryImpl.getRepository(api3Impl);
        GochiUseCase gochiUseCase = GochiUseCaseImpl.getUseCase(gochiRepository, UIThread.getInstance());
        mPresenter = new ShowNearTimelinePresenter(timelineNearUseCaseImpl, gochiUseCase);
        mPresenter.setNearTimelineView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mPlayBlockFlag = false;
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
                    TimelineActivity.mLongitude = bundle.getDouble("lon");
                    TimelineActivity.mLatitude = bundle.getDouble("lat");
                    TimelineActivity.mNearCategory_id = 0;
                    TimelineActivity.mNearValue_id = 0;

                    API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                    if (localCode == null) {
                        mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_REFRESH, API3.Util.getGetNearlineCustomAPI(
                                TimelineActivity.mLongitude, TimelineActivity.mLatitude, 0,
                                TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                    } else {
                        Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
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
            API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
            if (localCode == null) {
                mTimelineRecyclerView.scrollVerticallyToPosition(0);
                mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_FILTER, event.filterUrl);
            } else {
                Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
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
        if (mPlayingPostId != null && TimelineActivity.mShowPosition == 0) {
            releasePlayer();
        }
        player.setBackgrounded(false);
    }

    private void getSignupAsync(final Context context) {
        if (PermissionChecker.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new MaterialDialog.Builder(getActivity())
                        .title("権限許可のお願い")
                        .titleColorRes(R.color.namegrey)
                        .content("近い店を表示するには位置情報を取得する必要があります。位置情報を許可しますか？")
                        .contentColorRes(R.color.nameblack)
                        .positiveText("許可する")
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText("いいえ")
                        .negativeColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 38);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                onNegativeActionCausedByM();
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 38);
            }
        } else {

            SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    TimelineActivity.mLongitude = location.getLongitude();
                    TimelineActivity.mLatitude = location.getLatitude();

                    API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                    if (localCode == null) {
                        mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_FIRST, API3.Util.getGetNearlineAPI(TimelineActivity.mLongitude, TimelineActivity.mLatitude));

                    } else {
                        Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getRefreshAsync(final Context context) {
        if (PermissionChecker.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new MaterialDialog.Builder(getActivity())
                        .content("近い店を表示するには位置情報を取得する必要があります。位置情報を許可しますか？")
                        .contentColorRes(R.color.nameblack)
                        .positiveText("許可する")
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText("いいえ")
                        .negativeColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 39);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                Toast.makeText(getActivity(), "近くの店は表示できなくなります", Toast.LENGTH_SHORT).show();
                                mEmptyImage.setVisibility(View.VISIBLE);
                                mEmptyText.setVisibility(View.VISIBLE);
                                mSwipeContainer.setRefreshing(false);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 39);
            }
        } else {
            SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    TimelineActivity.mLongitude = location.getLongitude();
                    TimelineActivity.mLatitude = location.getLatitude();
                    TimelineActivity.mNearCategory_id = 0;
                    TimelineActivity.mNearValue_id = 0;

                    API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                    if (localCode == null) {
                        mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_REFRESH, API3.Util.getGetNearlineCustomAPI(
                                TimelineActivity.mLongitude, TimelineActivity.mLatitude, 0,
                                TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                        TimelineActivity activity = (TimelineActivity) getActivity();
                        activity.setNowLocationTitle();
                    } else {
                        Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 38:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                TimelineActivity.mLongitude = location.getLongitude();
                                TimelineActivity.mLatitude = location.getLatitude();

                                API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                                if (localCode == null) {
                                    mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_FIRST, API3.Util.getGetNearlineAPI(TimelineActivity.mLongitude, TimelineActivity.mLatitude));
                                } else {
                                    Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            onNegativeActionCausedByM();
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("権限許可のお願い")
                                    .titleColorRes(R.color.namegrey)
                                    .content("近い店を表示するには位置情報を取得する必要があるため、設定を変更する必要があります")
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText("変更する")
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText("いいえ")
                                    .negativeColorRes(R.color.gocci_header)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null); //Fragmentの場合はgetContext().getPackageName()
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            onNegativeActionCausedByM();
                                        }
                                    }).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "近くの店は表示できなくなります", Toast.LENGTH_SHORT).show();
                    } else {
                        SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                TimelineActivity.mLongitude = location.getLongitude();
                                TimelineActivity.mLatitude = location.getLatitude();

                                API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                                if (localCode == null) {
                                    mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_FIRST, API3.Util.getGetNearlineAPI(TimelineActivity.mLongitude, TimelineActivity.mLatitude));
                                } else {
                                    Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                break;
            case 39:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                TimelineActivity.mLongitude = location.getLongitude();
                                TimelineActivity.mLatitude = location.getLatitude();
                                TimelineActivity.mNearCategory_id = 0;
                                TimelineActivity.mNearValue_id = 0;

                                API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                                if (localCode == null) {
                                    mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_REFRESH, API3.Util.getGetNearlineCustomAPI(
                                            TimelineActivity.mLongitude, TimelineActivity.mLatitude, 0,
                                            TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                                    TimelineActivity activity = (TimelineActivity) getActivity();
                                    activity.setNowLocationTitle();
                                } else {
                                    Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Toast.makeText(getActivity(), "近くの店は表示できなくなります", Toast.LENGTH_SHORT).show();
                            mEmptyImage.setVisibility(View.VISIBLE);
                            mEmptyText.setVisibility(View.VISIBLE);
                            mSwipeContainer.setRefreshing(false);
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title("権限許可のお願い")
                                    .titleColorRes(R.color.namegrey)
                                    .content("近い店を表示するには位置情報を取得する必要があるため、設定を変更する必要があります")
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText("変更する")
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText("いいえ")
                                    .negativeColorRes(R.color.gocci_header)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null); //Fragmentの場合はgetContext().getPackageName()
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Toast.makeText(getActivity(), "近くの店は表示できなくなります", Toast.LENGTH_SHORT).show();
                                            mEmptyImage.setVisibility(View.VISIBLE);
                                            mEmptyText.setVisibility(View.VISIBLE);
                                            mSwipeContainer.setRefreshing(false);
                                        }
                                    }).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "近くの店は表示できなくなります", Toast.LENGTH_SHORT).show();
                    } else {
                        SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location) {
                                TimelineActivity.mLongitude = location.getLongitude();
                                TimelineActivity.mLatitude = location.getLatitude();
                                TimelineActivity.mNearCategory_id = 0;
                                TimelineActivity.mNearValue_id = 0;

                                API3.Util.GetTimelineLocalCode localCode = API3.Impl.getRepository().get_nearline_parameter_regex(TimelineActivity.mLongitude, TimelineActivity.mLatitude);
                                if (localCode == null) {
                                    mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_REFRESH, API3.Util.getGetNearlineCustomAPI(
                                            TimelineActivity.mLongitude, TimelineActivity.mLatitude, 0,
                                            TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                                    TimelineActivity activity = (TimelineActivity) getActivity();
                                    activity.setNowLocationTitle();
                                } else {
                                    Toast.makeText(getActivity(), API3.Util.getTimelineLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    private void onNegativeActionCausedByM() {
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
        mSwipeContainer.setRefreshing(false);
        mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
        mTimelineAdapter.setTimelineCallback(this);
        mTimelineRecyclerView.setAdapter(mTimelineAdapter);
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

    private void changeMovie(TwoCellData postData) {
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
    public void showEmpty(Const.APICategory api) {
        switch (api) {
            case GET_TIMELINE_FIRST:
                mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
                mTimelineAdapter.setTimelineCallback(this);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case GET_TIMELINE_REFRESH:
                mTimelineusers.clear();
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mTimelineAdapter.setData();
                break;
            case GET_TIMELINE_FILTER:
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
    public void hideEmpty() {
        mEmptyImage.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResult(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        switch (api) {
            case GET_TIMELINE_FIRST:
                mTimelineusers.addAll(mPostData);
                mPost_ids.addAll(post_ids);
                mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
                mTimelineAdapter.setTimelineCallback(this);
                mTimelineRecyclerView.setAdapter(mTimelineAdapter);
                break;
            case GET_TIMELINE_REFRESH:
                mTimelineusers.clear();
                mTimelineusers.addAll(mPostData);
                mPost_ids.clear();
                mPost_ids.addAll(post_ids);
                isEndScrioll = false;
                mNextCount = 1;
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTimelineAdapter.setData();

                if (activity != null) {
                    activity.refreshSheet();
                } else {
                    activity = (TimelineActivity)getActivity();
                    activity.refreshSheet();
                }
                break;
            case GET_TIMELINE_ADD:
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
            case GET_TIMELINE_FILTER:
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
    public void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
        mSwipeContainer.setRefreshing(false);
        if (api == Const.APICategory.GET_TIMELINE_FIRST) {
            mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
            mTimelineAdapter.setTimelineCallback(this);
            mTimelineRecyclerView.setAdapter(mTimelineAdapter);
        }
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        mSwipeContainer.setRefreshing(false);
        if (api == Const.APICategory.GET_TIMELINE_FIRST) {
            mTimelineAdapter = new TimelineAdapter(getActivity(), mTimelineusers);
            mTimelineAdapter.setTimelineCallback(this);
            mTimelineRecyclerView.setAdapter(mTimelineAdapter);
        }
    }

    @Override
    public void gochiSuccess(Const.APICategory api, String post_id) {

    }

    @Override
    public void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
        mTimelineusers.get(mPost_ids.indexOf(post_id)).setGochi_flag(0);
        mTimelineAdapter.notifyItemChanged(mPost_ids.indexOf(post_id));
    }

    @Override
    public void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        mTimelineusers.get(mPost_ids.indexOf(post_id)).setGochi_flag(0);
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
        }
    }

    @Override
    public void onGochiClick(String post_id) {
        API3.Util.PostGochiLocalCode postGochiLocalCode = API3.Impl.getRepository().post_gochi_parameter_regex(post_id);
        if (postGochiLocalCode == null) {
            mPresenter.postGochi(Const.APICategory.POST_GOCHI, API3.Util.getPostGochiAPI(post_id), post_id);
        } else {
            Toast.makeText(getActivity(), API3.Util.postGochiLocalErrorMessageTable(postGochiLocalCode), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewRecycled(Const.TwoCellViewHolder holder) {

    }

    @Override
    public void onVideoFrameClick(TwoCellData data) {
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

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_TIMELINE_FIRST:
                mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_FIRST, API3.Util.getGetNearlineAPI(TimelineActivity.mLongitude, TimelineActivity.mLatitude));
                break;
            case GET_TIMELINE_REFRESH:
                mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_REFRESH, API3.Util.getGetNearlineCustomAPI(
                        TimelineActivity.mLongitude, TimelineActivity.mLatitude, 0,
                        TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                TimelineActivity activity = (TimelineActivity) getActivity();
                activity.setNowLocationTitle();
                break;
            case GET_TIMELINE_ADD:
                mPresenter.getNearTimelinePostData(Const.APICategory.GET_TIMELINE_ADD, API3.Util.getGetNearlineCustomAPI(
                        TimelineActivity.mLongitude, TimelineActivity.mLatitude, mNextCount,
                        TimelineActivity.mNearCategory_id, TimelineActivity.mNearValue_id));
                break;
            case GET_TIMELINE_FILTER:

                break;
            default:
                break;
        }
    }
}

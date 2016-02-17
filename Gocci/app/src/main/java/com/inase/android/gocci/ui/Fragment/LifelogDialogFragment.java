package com.inase.android.gocci.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.ui.activity.CommentActivity;
import com.inase.android.gocci.ui.activity.MyprofActivity;
import com.inase.android.gocci.ui.activity.TenpoActivity;
import com.inase.android.gocci.ui.activity.TimelineActivity;
import com.inase.android.gocci.ui.adapter.LifelogAdapter;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.video.HlsRendererBuilder;
import com.inase.android.gocci.utils.video.VideoPlayer;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Created by kinagafuji on 16/02/17.
 */
public class LifelogDialogFragment extends SupportBlurDialogFragment implements AudioCapabilitiesReceiver.Listener,
        LifelogAdapter.LifelogCallback {

    private ObservableRecyclerView mLifelogRecyclerView;

    private static ArrayList<PostData> sPostData = new ArrayList<>();
    private static ArrayList<String> sPostIdData = new ArrayList<>();

    private LinearLayoutManager mLinearLayoutManager;
    private LifelogAdapter mLifelogAdapter;

    private Point mDisplaySize;
    private String mPlayingPostId;
    private ConcurrentHashMap<Const.StreamUserViewHolder, String> mStreamViewHolderHash;

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    int totalItemCount;
    private boolean isExist = false;

    private MyprofActivity activity;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    public static LifelogDialogFragment newInstance(ArrayList<PostData> postData) {
        sPostData.clear();
        sPostData.addAll(postData);
        LifelogDialogFragment fragment = new LifelogDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

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
                    mTracker.setScreenName("MyProfStream");
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
            if (MyprofActivity.mShowPosition == 2) {
                streamChangeMovie();

                if (mPlayingPostId != null && !isExist) {
                    mLifelogRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            } else {
                mLifelogRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            if (mPlayingPostId != null && MyprofActivity.mShowPosition == 2) {
                releasePlayer();
                if (Util.isMovieAutoPlay(getActivity())) {
                    streamPreparePlayer(getStreamPlayingViewHolder(), getVideoPath());
                }
            }
        } else {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();
        if (getStreamPlayingViewHolder() != null) {
            getStreamPlayingViewHolder().mVideoThumbnail.setVisibility(View.VISIBLE);
        }
        //mPresenter.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.MY_PAGE) {
            if (event.apiCategory == Const.APICategory.SET_GOCHI) {
                mLifelogAdapter.notifyItemChanged(sPostIdData.indexOf(event.id));
            } else if (event.apiCategory == Const.APICategory.UNSET_GOCHI) {
                mLifelogAdapter.notifyItemChanged(sPostIdData.indexOf(event.id));
            }
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

    private String getVideoPath() {
        final int position = mLifelogRecyclerView.getChildAdapterPosition(mLifelogRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        final PostData userData = mLifelogAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            return null;
        }
        //return mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        return userData.getHls_movie();
    }

    private void streamPreparePlayer(final Const.StreamUserViewHolder viewHolder, String path) {
        if (player == null) {
            viewHolder.mProgress.showNow();

            mTracker = applicationGocci.getDefaultTracker();
            mTracker.setScreenName("MyProfStream");
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
                            mTracker.setScreenName("MyProfStream");
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
                    viewHolder.mProgress.hideNow();
                }

                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
                    viewHolder.mVideoThumbnail.setVisibility(View.GONE);
                    viewHolder.mProgress.hideNow();
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
        if (mLifelogAdapter == null) {
            return;
        }
        final int position = mLifelogRecyclerView.getChildAdapterPosition(mLifelogRecyclerView.findChildViewUnder(mDisplaySize.x / 2, mDisplaySize.y / 2));
        if (mLifelogAdapter.isEmpty()) {
            return;
        }
        if (position < 0) {
            return;
        }

        final PostData userData = mLifelogAdapter.getItem(position);
        if (!userData.getPost_id().equals(mPlayingPostId)) {

            // 前回の動画再生停止処理
            final Const.StreamUserViewHolder oldViewHolder = getStreamPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final Const.StreamUserViewHolder currentViewHolder = getStreamPlayingViewHolder();

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_lifelog_dialog, null);

        applicationGocci = (Application_Gocci) getActivity().getApplication();

        mPlayingPostId = null;
        mStreamViewHolderHash = new ConcurrentHashMap<>();

        activity = (MyprofActivity) getActivity();

        mLifelogRecyclerView = (ObservableRecyclerView) view.findViewById(R.id.list);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLifelogRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLifelogRecyclerView.setHasFixedSize(true);
        mLifelogRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mLifelogRecyclerView.addOnScrollListener(mStreamScrollListener);

        mLifelogAdapter = new LifelogAdapter(getActivity(), sPostData);
        mLifelogAdapter.setLifelogCallback(this);
        mLifelogRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        mLifelogRecyclerView.setAdapter(mLifelogAdapter);

        builder.setView(view);
        return builder.create();
    }

    @Override
    protected boolean isDebugEnable() {
        return false;
    }

    @Override
    protected boolean isDimmingEnable() {
        return false;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return false;
    }

    @Override
    protected float getDownScaleFactor() {
        return 5.0f;
    }

    @Override
    protected int getBlurRadius() {
        return 4;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return false;
    }

    @Override
    public void onStreamRestClick(String rest_id, String rest_name) {
        TenpoActivity.startTenpoActivity(rest_id, rest_name, getActivity());
    }

    @Override
    public void onStreamCommentClick(String post_id) {
        CommentActivity.startCommentActivity(post_id, true, getActivity());
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
    public void onStreamDeleteClick(String post_id) {
        MyprofActivity activity = (MyprofActivity) getActivity();
        activity.setDeleteDialog(post_id);
        dismiss();
    }

    @Override
    public void onGochiTap() {
        if (activity != null) {
            activity.setGochiLayout();
        } else {
            activity = (MyprofActivity) getActivity();
            activity.setGochiLayout();
        }
    }

    @Override
    public void onGochiClick(String post_id, Const.APICategory apiCategory) {
        if (activity != null) {
            activity.postGochi(post_id, apiCategory);
        } else {
            activity = (MyprofActivity) getActivity();
            activity.postGochi(post_id, apiCategory);
        }
    }

    @Override
    public void onFacebookShare(String share, String rest_name) {
        MyprofActivity activity = (MyprofActivity) getActivity();
        activity.shareVideoPost(25, share, rest_name);
    }

    @Override
    public void onTwitterShare(String share, String rest_name) {
        MyprofActivity activity = (MyprofActivity) getActivity();
        activity.shareVideoPost(26, share, rest_name);
    }

    @Override
    public void onInstaShare(String share, String rest_name) {
        MyprofActivity activity = (MyprofActivity) getActivity();
        activity.shareVideoPost(27, share, rest_name);
    }

    @Override
    public void onStreamHashHolder(Const.StreamUserViewHolder holder, String post_id) {
        mStreamViewHolderHash.put(holder, post_id);
    }
}
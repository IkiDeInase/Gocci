package com.inase.android.gocci.VideoPlayer;

import android.media.MediaCodec;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.google.android.exoplayer.CodecCounters;
import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.chunk.MultiTrackChunkSource;
import com.google.android.exoplayer.dash.DashChunkSource;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.PlayerControl;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by kinagafuji on 15/07/14.
 */
public class VideoPlayer implements ExoPlayer.Listener, ChunkSampleSource.EventListener, HlsSampleSource.EventListener,
        DefaultBandwidthMeter.EventListener, MediaCodecVideoTrackRenderer.EventListener,
        MediaCodecAudioTrackRenderer.EventListener, StreamingDrmSessionManager.EventListener,
        DashChunkSource.EventListener, DebugTextViewHelper.Provider {

    public interface RendererBuilder {
        void buildRenderers(VideoPlayer player, RendererBuilderCallback callback);
    }

    public interface RendererBuilderCallback {
        void onRenderers(String[][] trackNames, MultiTrackChunkSource[] multiTrackSources,
                         TrackRenderer[] renderers, BandwidthMeter bandwidthMeter);

        void onRenderersError(Exception e);
    }

    public interface Listener {
        void onStateChanged(boolean playWhenReady, int playbackState);

        void onError(Exception e);

        void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio);
    }

    public interface InternalErrorListener {
        void onRendererInitializationError(Exception e);

        void onAudioTrackInitializationError(AudioTrack.InitializationException e);

        void onAudioTrackWriteError(AudioTrack.WriteException e);

        void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e);

        void onCryptoError(MediaCodec.CryptoException e);

        void onLoadError(int sourceId, IOException e);

        void onDrmSessionManagerError(Exception e);
    }

    /**
     * A listener for debugging information.
     */
    public interface InfoListener {
        void onVideoFormatEnabled(Format format, int trigger, int mediaTimeMs);

        void onAudioFormatEnabled(Format format, int trigger, int mediaTimeMs);

        void onDroppedFrames(int count, long elapsed);

        void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate);

        void onLoadStarted(int sourceId, long length, int type, int trigger, Format format,
                           int mediaStartTimeMs, int mediaEndTimeMs);

        void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format,
                             int mediaStartTimeMs, int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs);

        void onDecoderInitialized(String decoderName, long elapsedRealtimeMs,
                                  long initializationDurationMs);

        void onSeekRangeChanged(TimeRange seekRange);
    }

    public static final int STATE_IDLE = ExoPlayer.STATE_IDLE;
    public static final int STATE_PREPARING = ExoPlayer.STATE_PREPARING;
    public static final int STATE_BUFFERING = ExoPlayer.STATE_BUFFERING;
    public static final int STATE_READY = ExoPlayer.STATE_READY;
    public static final int STATE_ENDED = ExoPlayer.STATE_ENDED;

    public static final int DISABLED_TRACK = -1;
    public static final int PRIMARY_TRACK = 0;

    public static final int RENDERER_COUNT = 3;
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_TIMED_METADATA = 3;
    public static final int TYPE_DEBUG = 4;

    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;

    private final RendererBuilder rendererBuilder;
    private final ExoPlayer player;
    private final PlayerControl playerControl;
    private final Handler mainHandler;
    private final CopyOnWriteArrayList<Listener> listeners;

    private int rendererBuildingState;
    private int lastReportedPlaybackState;
    private boolean lastReportedPlayWhenReady;

    private Surface surface;
    private InternalRendererBuilderCallback builderCallback;
    private TrackRenderer videoRenderer;
    private CodecCounters codecCounters;
    private Format videoFormat;
    private int videoTrackToRestore;

    private BandwidthMeter bandwidthMeter;
    private MultiTrackChunkSource[] multiTrackSources;
    private String[][] trackNames;
    private int[] selectedTracks;
    private boolean backgrounded;

    private InternalErrorListener internalErrorListener;
    private InfoListener infoListener;

    public VideoPlayer(RendererBuilder rendererBuilder) {
        this.rendererBuilder = rendererBuilder;
        player = ExoPlayer.Factory.newInstance(RENDERER_COUNT, 1000, 5000);
        player.addListener(this);
        playerControl = new PlayerControl(player);
        mainHandler = new Handler();
        listeners = new CopyOnWriteArrayList<Listener>();
        lastReportedPlaybackState = STATE_IDLE;
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        selectedTracks = new int[RENDERER_COUNT];
        // Disable text initially.
        selectedTracks[TYPE_TEXT] = DISABLED_TRACK;
    }

    public PlayerControl getPlayerControl() {
        return playerControl;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void setInternalErrorListener(InternalErrorListener listener) {
        internalErrorListener = listener;
    }

    public void setInfoListener(InfoListener listener) {
        infoListener = listener;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        pushSurface(false);
    }

    public Surface getSurface() {
        return surface;
    }

    public void blockingClearSurface() {
        surface = null;
        pushSurface(true);
    }

    public int getTrackCount(int type) {
        return !player.getRendererHasMedia(type) ? 0 : trackNames[type].length;
    }

    public String getTrackName(int type, int index) {
        return trackNames[type][index];
    }

    public int getSelectedTrackIndex(int type) {
        return selectedTracks[type];
    }

    public void selectTrack(int type, int index) {
        if (selectedTracks[type] == index) {
            return;
        }
        selectedTracks[type] = index;
        pushTrackSelection(type, true);
    }

    public void setBackgrounded(boolean backgrounded) {
        if (this.backgrounded == backgrounded) {
            return;
        }
        this.backgrounded = backgrounded;
        if (backgrounded) {
            videoTrackToRestore = getSelectedTrackIndex(TYPE_VIDEO);
            selectTrack(TYPE_VIDEO, DISABLED_TRACK);
            blockingClearSurface();
        } else {
            selectTrack(TYPE_VIDEO, videoTrackToRestore);
        }
    }

    public void prepare() {
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT) {
            player.stop();
        }
        if (builderCallback != null) {
            builderCallback.cancel();
        }
        videoFormat = null;
        videoRenderer = null;
        multiTrackSources = null;
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILDING;
        maybeReportPlayerState();
        builderCallback = new InternalRendererBuilderCallback();
        rendererBuilder.buildRenderers(this, builderCallback);
    }

    void onRenderers(String[][] trackNames,
                     MultiTrackChunkSource[] multiTrackSources, TrackRenderer[] renderers,
                     BandwidthMeter bandwidthMeter) {
        builderCallback = null;
        // Normalize the results.
        if (trackNames == null) {
            trackNames = new String[RENDERER_COUNT][];
        }
        if (multiTrackSources == null) {
            multiTrackSources = new MultiTrackChunkSource[RENDERER_COUNT];
        }
        for (int rendererIndex = 0; rendererIndex < RENDERER_COUNT; rendererIndex++) {
            if (renderers[rendererIndex] == null) {
                // Convert a null renderer to a dummy renderer.
                renderers[rendererIndex] = new DummyTrackRenderer();
            }
            if (trackNames[rendererIndex] == null) {
                // Convert a null trackNames to an array of suitable length.
                int trackCount = multiTrackSources[rendererIndex] != null
                        ? multiTrackSources[rendererIndex].getTrackCount() : 1;
                trackNames[rendererIndex] = new String[trackCount];
            }
        }
        // Complete preparation.
        this.trackNames = trackNames;
        this.videoRenderer = renderers[TYPE_VIDEO];
        this.codecCounters = videoRenderer instanceof MediaCodecTrackRenderer
                ? ((MediaCodecTrackRenderer) videoRenderer).codecCounters
                : renderers[TYPE_AUDIO] instanceof MediaCodecTrackRenderer
                ? ((MediaCodecTrackRenderer) renderers[TYPE_AUDIO]).codecCounters : null;
        this.multiTrackSources = multiTrackSources;
        this.bandwidthMeter = bandwidthMeter;
        pushSurface(false);
        pushTrackSelection(TYPE_VIDEO, true);
        pushTrackSelection(TYPE_AUDIO, true);
        pushTrackSelection(TYPE_TEXT, true);
        player.prepare(renderers);
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILT;
    }

    void onRenderersError(Exception e) {
        builderCallback = null;
        if (internalErrorListener != null) {
            internalErrorListener.onRendererInitializationError(e);
        }
        for (Listener listener : listeners) {
            listener.onError(e);
        }
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        maybeReportPlayerState();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        player.setPlayWhenReady(playWhenReady);
    }

    public void seekTo(long positionMs) {
        player.seekTo(positionMs);
    }

    public void release() {
        if (builderCallback != null) {
            builderCallback.cancel();
            builderCallback = null;
        }
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        surface = null;
        player.release();
    }


    public int getPlaybackState() {
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILDING) {
            return ExoPlayer.STATE_PREPARING;
        }
        int playerState = player.getPlaybackState();
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT
                && rendererBuildingState == RENDERER_BUILDING_STATE_IDLE) {
            // This is an edge case where the renderers are built, but are still being passed to the
            // player's playback thread.
            return ExoPlayer.STATE_PREPARING;
        }
        return playerState;
    }

    @Override
    public Format getFormat() {
        return videoFormat;
    }

    @Override
    public BandwidthMeter getBandwidthMeter() {
        return bandwidthMeter;
    }

    @Override
    public CodecCounters getCodecCounters() {
        return codecCounters;
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public long getDuration() {
        return player.getDuration();
    }

    public int getBufferedPercentage() {
        return player.getBufferedPercentage();
    }

    public boolean getPlayWhenReady() {
        return player.getPlayWhenReady();
    }

    Looper getPlaybackLooper() {
        return player.getPlaybackLooper();
    }

    Handler getMainHandler() {
        return mainHandler;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        maybeReportPlayerState();
    }

    @Override
    public void onPlayerError(ExoPlaybackException exception) {
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        for (Listener listener : listeners) {
            listener.onError(exception);
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        for (Listener listener : listeners) {
            listener.onVideoSizeChanged(width, height, pixelWidthHeightRatio);
        }
    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
        if (infoListener != null) {
            infoListener.onDroppedFrames(count, elapsed);
        }
    }

    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
        if (infoListener != null) {
            infoListener.onBandwidthSample(elapsedMs, bytes, bitrateEstimate);
        }
    }

    @Override
    public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, int mediaTimeMs) {
        if (infoListener == null) {
            return;
        }
        if (sourceId == TYPE_VIDEO) {
            videoFormat = format;
            infoListener.onVideoFormatEnabled(format, trigger, mediaTimeMs);
        } else if (sourceId == TYPE_AUDIO) {
            infoListener.onAudioFormatEnabled(format, trigger, mediaTimeMs);
        }
    }

    @Override
    public void onDrmSessionManagerError(Exception e) {
        if (internalErrorListener != null) {
            internalErrorListener.onDrmSessionManagerError(e);
        }
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onDecoderInitializationError(e);
        }
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onAudioTrackInitializationError(e);
        }
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onAudioTrackWriteError(e);
        }
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onCryptoError(e);
        }
    }

    @Override
    public void onDecoderInitialized(
            String decoderName,
            long elapsedRealtimeMs,
            long initializationDurationMs) {
        if (infoListener != null) {
            infoListener.onDecoderInitialized(decoderName, elapsedRealtimeMs, initializationDurationMs);
        }
    }

    @Override
    public void onLoadError(int sourceId, IOException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onLoadError(sourceId, e);
        }
    }

    @Override
    public void onSeekRangeChanged(TimeRange seekRange) {
        if (infoListener != null) {
            infoListener.onSeekRangeChanged(seekRange);
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {
        // Do nothing.
    }

    @Override
    public void onDrawnToSurface(Surface surface) {
        // Do nothing.
    }

    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format,
                              int mediaStartTimeMs, int mediaEndTimeMs) {
        if (infoListener != null) {
            infoListener.onLoadStarted(sourceId, length, type, trigger, format, mediaStartTimeMs,
                    mediaEndTimeMs);
        }
    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format,
                                int mediaStartTimeMs, int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
        if (infoListener != null) {
            infoListener.onLoadCompleted(sourceId, bytesLoaded, type, trigger, format, mediaStartTimeMs,
                    mediaEndTimeMs, elapsedRealtimeMs, loadDurationMs);
        }
    }

    @Override
    public void onLoadCanceled(int sourceId, long bytesLoaded) {
        // Do nothing.
    }

    @Override
    public void onUpstreamDiscarded(int sourceId, int mediaStartTimeMs, int mediaEndTimeMs) {
        // Do nothing.
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = player.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
            for (Listener listener : listeners) {
                listener.onStateChanged(playWhenReady, playbackState);
            }
            lastReportedPlayWhenReady = playWhenReady;
            lastReportedPlaybackState = playbackState;
        }
    }

    private void pushSurface(boolean blockForSurfacePush) {
        if (videoRenderer == null) {
            return;
        }

        if (blockForSurfacePush) {
            player.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        } else {
            player.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        }
    }

    private void pushTrackSelection(int type, boolean allowRendererEnable) {
        if (multiTrackSources == null) {
            return;
        }

        int trackIndex = selectedTracks[type];
        if (trackIndex == DISABLED_TRACK) {
            player.setRendererEnabled(type, false);
        } else if (multiTrackSources[type] == null) {
            player.setRendererEnabled(type, allowRendererEnable);
        } else {
            boolean playWhenReady = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
            player.setRendererEnabled(type, false);
            player.sendMessage(multiTrackSources[type], MultiTrackChunkSource.MSG_SELECT_TRACK,
                    trackIndex);
            player.setRendererEnabled(type, allowRendererEnable);
            player.setPlayWhenReady(playWhenReady);
        }
    }

    private class InternalRendererBuilderCallback implements RendererBuilderCallback {

        private boolean canceled;

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRenderers(String[][] trackNames, MultiTrackChunkSource[] multiTrackSources,
                                TrackRenderer[] renderers, BandwidthMeter bandwidthMeter) {
            if (!canceled) {
                VideoPlayer.this.onRenderers(trackNames, multiTrackSources, renderers, bandwidthMeter);
            }
        }

        @Override
        public void onRenderersError(Exception e) {
            if (!canceled) {
                VideoPlayer.this.onRenderersError(e);
            }
        }

    }
}

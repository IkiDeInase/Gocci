package com.inase.android.gocci.VideoPlayer;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Handler;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.chunk.VideoFormatSelectorUtil;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;

import java.io.IOException;

/**
 * Created by kinagafuji on 15/07/14.
 */
public class HlsRendererBuilder implements VideoPlayer.RendererBuilder, ManifestFetcher.ManifestCallback<HlsPlaylist> {

    private static final int REQUESTED_BUFFER_SIZE = 18 * 1024 * 1024;
    private static final long REQUESTED_BUFFER_DURATION_MS = 40000;

    private final Context context;
    private final String userAgent;
    private final String url;
    private final AudioCapabilities audioCapabilities;

    private VideoPlayer player;
    private VideoPlayer.RendererBuilderCallback callback;

    public HlsRendererBuilder(Context context, String userAgent, String url, AudioCapabilities audioCapabilities) {
        this.context = context;
        this.userAgent = userAgent;
        this.url = url;
        this.audioCapabilities = audioCapabilities;
    }

    @Override
    public void buildRenderers(VideoPlayer player, VideoPlayer.RendererBuilderCallback callback) {
        this.player = player;
        this.callback = callback;
        HlsPlaylistParser parser = new HlsPlaylistParser();
        ManifestFetcher<HlsPlaylist> playlistFetcher = new ManifestFetcher<HlsPlaylist>(url,
                new DefaultUriDataSource(context, userAgent), parser);
        playlistFetcher.singleLoad(player.getMainHandler().getLooper(), this);
    }

    @Override
    public void onSingleManifestError(IOException e) {
        callback.onRenderersError(e);
    }

    @Override
    public void onSingleManifest(HlsPlaylist manifest) {
        Handler mainHandler = player.getMainHandler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        int[] variantIndices = null;
        if (manifest instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
            try {
                variantIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(
                        context, masterPlaylist.variants, null, false);
            } catch (MediaCodecUtil.DecoderQueryException e) {
                callback.onRenderersError(e);
                return;
            }
        }

        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
        HlsChunkSource chunkSource = new HlsChunkSource(dataSource, url, manifest, bandwidthMeter,
                variantIndices, HlsChunkSource.ADAPTIVE_MODE_SPLICE, audioCapabilities);
        HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, true, 3, REQUESTED_BUFFER_SIZE,
                REQUESTED_BUFFER_DURATION_MS, mainHandler, player, VideoPlayer.TYPE_VIDEO);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
                MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, mainHandler, player, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);

        TrackRenderer[] renderers = new TrackRenderer[VideoPlayer.RENDERER_COUNT];
        renderers[VideoPlayer.TYPE_VIDEO] = videoRenderer;
        renderers[VideoPlayer.TYPE_AUDIO] = audioRenderer;
        callback.onRenderers(null, null, renderers);
    }
}

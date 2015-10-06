package com.inase.android.gocci.utils.camera;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Encoder class to encode audio data with AAC encoder and save into intermediate files
 */
public final class TLMediaAudioEncoder extends AbstractTLMediaAudioEncoder {
    private static final boolean DEBUG = false;
    private static final String TAG = "TLMediaAudioEncoder";

    private static final int SAMPLES_PER_FRAME = 1024;    // AAC, bytes/frame/channel
    private static final int FRAMES_PER_BUFFER = 25;    // AAC, frame/buffer/sec

    /**
     * Constructor(this class only support monaural audio source)
     *
     * @param context
     * @param base_path
     * @param listener
     */
    public TLMediaAudioEncoder(final Context context, final String base_path, final MediaEncoderListener listener) {
        super(context, base_path, listener, DEFAULT_SAMPLE_RATE, DEFAULT_BIT_RATE);
    }

    /**
     * Constructor(this class only support monaural audio source)
     *
     * @param context
     * @param base_path
     * @param listener
     * @param sample_rate default value is 44100(44.1kHz, 44.1KHz is only guarantee value on all devices)
     * @param bit_rate    default value is 64000(64kbps)
     */
    public TLMediaAudioEncoder(final Context context, final String base_path, final MediaEncoderListener listener,
                               final int sample_rate, final int bit_rate) {
        super(context, base_path, listener, sample_rate, bit_rate);
    }

    @Override
    protected void recordingLoop() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        try {
            final int min_buffer_size = AudioRecord.getMinBufferSize(
                    mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
            if (buffer_size < min_buffer_size)
                buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

            final AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC, mSampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
            try {
                if ((audioRecord.getState() == AudioRecord.STATE_INITIALIZED) && (mIsRunning)) {
                    if (DEBUG) Log.v(TAG, "AudioThread:start_from_encoder audio recording");
                    final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                    int readBytes;
                    audioRecord.startRecording();
                    try {
                        while (mIsRunning && isRecording()) {
                            // read audio data from internal mic
                            buf.clear();
                            readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                            if (readBytes > 0) {
                                // set audio data to encoder
                                encode(buf, readBytes, getPTSUs());
                                frameAvailableSoon();
                            }
                        }
                        frameAvailableSoon();
                    } finally {
                        audioRecord.stop();
                    }
                }
            } finally {
                audioRecord.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "AudioThread#run", e);
        } finally {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
        }
    }

}

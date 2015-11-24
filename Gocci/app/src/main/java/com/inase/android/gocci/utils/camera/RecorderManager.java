package com.inase.android.gocci.utils.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Recorder controller, used to start,stop record, and combine all the videos together
 *
 * @author xiaodong
 */
public class RecorderManager {
    private MediaRecorder mediaRecorder = null;
    private CameraManager cameraManager = null;
    private String parentPath = null;
    private List<String> videoTempFiles = new ArrayList<String>();
    private SurfaceView mySurfaceView = null;
    public static final int MAX_TIME = 7000;
    private boolean isMax = false;
    private long videoStartTime;
    private int totalTime = 0;
    private boolean isStart = false;
    Activity mainActivity = null;
    private final Semaphore semp = new Semaphore(1);

    private int OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;
    private int AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC;
    private int VIDEO_SOURCE = MediaRecorder.VideoSource.CAMERA;
    private int VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264;
    private int mBitrate;

    public RecorderManager(CameraManager cameraManager,
                           SurfaceView mySurfaceView, Activity mainActivity) {
        this.cameraManager = cameraManager;
        this.mySurfaceView = mySurfaceView;
        this.mainActivity = mainActivity;
        parentPath = generateParentFolder();
        reset();
    }

    private Camera getCamera() {
        return cameraManager.getCamera();
    }

    public boolean isStart() {
        return isStart;
    }

    public long getVideoStartTime() {
        return videoStartTime;
    }

    public int checkIfMax(long timeNow) {
        int during = 0;
        if (isStart) {
            during = (int) (totalTime + (timeNow - videoStartTime));
            if (during >= MAX_TIME) {
                stopRecord();
                during = MAX_TIME;
                isMax = true;
            }
        } else {
            during = totalTime;
            if (during >= MAX_TIME) {
                during = MAX_TIME;
            }
        }

        return during;
    }

    public void reset() {
        for (String file : videoTempFiles) {
            File tempFile = new File(file);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        videoTempFiles = new ArrayList<String>();
        isStart = false;
        totalTime = 0;
        isMax = false;
    }

    public List<String> getVideoTempFiles() {
        return videoTempFiles;
    }

    public String getVideoParentpath() {
        return parentPath;
    }

    public void startRecord(boolean isFirst) {
        if (isMax) {
            return;
        }
        semp.acquireUninterruptibly();
        getCamera().stopPreview();
        mediaRecorder = new MediaRecorder();
        cameraManager.getCamera().unlock();
        mediaRecorder.setCamera(cameraManager.getCamera());
        if (cameraManager.isUseBackCamera()) {
            mediaRecorder.setOrientationHint(90);
        } else {
            mediaRecorder.setOrientationHint(90 + 180);
        }
        Size defaultSize = cameraManager.getDefaultSize();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile baseProfile = getBaseRecordingProfile();
        baseProfile.fileFormat = OUTPUT_FORMAT;
        baseProfile.videoBitRate = mBitrate;
        baseProfile.audioCodec = AUDIO_ENCODER;
        baseProfile.videoCodec = VIDEO_ENCODER;
        mediaRecorder.setProfile(baseProfile);

        if (defaultSize != null) {
            mediaRecorder.setVideoSize(defaultSize.width, defaultSize.height);
        } else {
            mediaRecorder.setVideoSize(640, 480);
        }

        // camera.getParameters().setPictureSize(cameraSize.width,
        // cameraSize.height);
        // camera.setParameters(parameters);
        String fileName = parentPath + "/" + videoTempFiles.size() + ".mp4";
        mediaRecorder.setOutputFile(fileName);
        videoTempFiles.add(fileName);
        mediaRecorder.setPreviewDisplay(mySurfaceView.getHolder().getSurface());
        try {
            mediaRecorder.prepare();

        } catch (Exception e) {
            e.printStackTrace();
            stopRecord();
        }

        try {
            mediaRecorder.start();
            isStart = true;
            videoStartTime = new Date().getTime();
        } catch (Exception e) {
            e.printStackTrace();
            if (isFirst) {
                startRecord(false);
            } else {
                stopRecord();
            }
        }
        //cameraManager.getCamera();
        //getCamera();
    }

    public void stopRecord() {
        if (!isMax) {
            totalTime += new Date().getTime() - videoStartTime;
            videoStartTime = 0;
        }
        //
        isStart = false;

        //
        if (mediaRecorder == null) {
            return;
        }
        try {
            mediaRecorder.setPreviewDisplay(null);
            mediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                getCamera().reconnect();
            } catch (Exception e) {
                // TODO: handle this exception...
            }
            getCamera().lock();
            semp.release();
        }

    }

    public String generateParentFolder() {
        String parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                .getAbsolutePath() + "/Gocci";
        File tempFile = new File(parentPath);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        return parentPath;
    }

    public CamcorderProfile getBaseRecordingProfile() {
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
            Log.d("解像度入りました", "1080P");
            mBitrate = 8000000;
            return CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            Log.d("解像度入りました", "720P");
            mBitrate = 5000000;
            return CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            Log.d("解像度入りました", "480P");
            mBitrate = 2500000;
            return CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
            Log.d("解像度入りました", "high");
            mBitrate = 1000000;
            return CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        } else {
            Log.d("解像度入りました", "low");
            mBitrate = 1000000;
            return CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        }
    }

}

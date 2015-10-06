package com.inase.android.gocci.utils.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Camera manager that control the start,stop and refresh the camera
 *
 * @author xiaodong
 */
public class CameraManager {
    private Camera camera = null;
    private Size defaultSize = null;
    private int cameraFacingType = CameraInfo.CAMERA_FACING_BACK;
    private boolean isRecording = false;

    public void startCamera(SurfaceHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == cameraFacingType) {
                    camera = Camera.open(i);
                    break;
                }
            }
        } else {
            camera = Camera.open();
        }
        Parameters parameters = camera.getParameters();

        try {
            List<Size> sizeList = parameters.getSupportedPreviewSizes();
            int width = 0;
            for (Size s : sizeList) {
                System.out.println(s.width + "," + s.height);
                if (s.width > width && s.width <= 800) {
                    width = s.width;
                    defaultSize = s;

                }
            }
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            camera.release();
            camera = null;
        }
        try {
            parameters.setPreviewSize(640, 480);
            camera.setParameters(parameters);
            defaultSize = null;
        } catch (Exception e) {
            e.printStackTrace();
            parameters.setPreviewSize(defaultSize.width, defaultSize.height);
            camera.setParameters(parameters);
        }

    }

    public Size getDefaultSize() {
        return defaultSize;
    }

    public void closeCamera() {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void rePreview() {
        // Parameters parameters = camera.getParameters();
        // parameters.setPictureSize(300, 300);
        // camera.setParameters(parameters);
        camera.stopPreview();
        camera.startPreview();
        camera.autoFocus(null);
    }

    public boolean isUseBackCamera() {
        return cameraFacingType == CameraInfo.CAMERA_FACING_BACK;
    }

    public Camera getCamera() {
        return camera;
    }

}

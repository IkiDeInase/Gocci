package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.inase.android.gocci.Camera.down18CameraFragment;
import com.inase.android.gocci.Camera.up18CameraFragment;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;

import java.util.ArrayList;

public class GocciCameraActivity extends AppCompatActivity {

    public static ArrayList<String> restname = new ArrayList<>();
    public static ArrayList<Integer> rest_id = new ArrayList<>();
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static boolean isLocationOnOff = false;

    private static MobileAnalyticsManager analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    Const.ANALYTICS_ID, //Amazon Mobile Analytics App ID
                    Const.IDENTITY_POOL_ID //Amazon Cognito Identity Pool ID
            );
        } catch (InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }


        setContentView(R.layout.activity_gocci_camera);

        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new up18CameraFragment()).commit();
            } else {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new down18CameraFragment()).commit();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("DEBUG", "User agreed to make required location settings changes.");
                        //firstLocation();
                        isLocationOnOff = true;
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d("DEBUG", "User chose not to make required location settings changes.");
                        //ダイアログをキャンセルした
                        Toast.makeText(this, getString(R.string.finish_camera_location_error), Toast.LENGTH_LONG).show();
                        isLocationOnOff = false;
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.check_videoposting_cancel))
                .positiveText(getString(R.string.check_videoposting_yeah))
                .positiveColorRes(R.color.gocci_header)
                .negativeText(getString(R.string.check_videoposting_no))
                .negativeColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        GocciCameraActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }
}


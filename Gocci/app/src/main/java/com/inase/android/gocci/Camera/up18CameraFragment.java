package com.inase.android.gocci.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.inase.android.gocci.Activity.CameraPreviewActivity;
import com.inase.android.gocci.Activity.GocciCameraActivity;
import com.inase.android.gocci.Base.CircleProgressBar;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import me.next.slidebottompanel.SlideBottomPanel;

public class up18CameraFragment extends Fragment implements SensorEventListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult> {
    private static final boolean DEBUG = true;    // TODO set false on releasing
    private static final String TAG = "GocciCamera";

    private SensorManager mSensorManager;
    private boolean mIsMagSensor;
    private boolean mIsAccSensor;

    private NiftyDialogBuilder dialogBuilder;

    private static final int MATRIX_SIZE = 16;
    /* 回転行列 */
    float[] inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[] I = new float[MATRIX_SIZE];

    /* センサーの値 */
    float[] orientationValues = new float[3];
    float[] magneticValues = new float[3];
    float[] accelerometerValues = new float[3];

    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;

    private ImageButton toukouButton;
    private Button cancelButton;
    /**
     * button for start/stop recording
     */
    private TLMediaVideoEncoder mVideoEncoder;
    private TLMediaAudioEncoder mAudioEncoder;
    private TLMediaMovieBuilder mMuxer;
    private boolean mIsRecording;
    private String mMovieName;

    private Runnable progressRunnable = null;
    private Handler handler = null;
    private ProgressWheel cameraProgress;
    private CircleProgressBar progress;

    private int mRest_id = 1;
    private int mCategory_id = 1;
    private int mTag_id = 1;
    private String mRest_name;
    private String mFinalVideoUrl;
    private String mAwsPostName;
    private String mValue = "";
    private String mMemo = "";
    private boolean mIsnewRestname = false;

    private double latitude;
    private double longitude;

    public static final int MAX_TIME = 7000;
    private boolean isMax = false;
    private long videoStartTime;
    private int totalTime = 0;
    private boolean isStart = false;
    private boolean isFinish = false;

    private MaterialBetterSpinner restname_spinner;
    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner mood_spinner;
    private MaterialEditText edit_value;
    private MaterialEditText edit_comment;
    private ImageButton restaddButton;

    private ArrayAdapter<String> restAdapter;

    private SlideBottomPanel sbv;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static int DISPLACEMENT = 10;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private boolean isLocationUpdating = false;

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    private LocationManager mLocationManager;

    public up18CameraFragment() {
        // need default constructor
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();

            checkLocationSettings();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_up18, container, false);

        dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle("Gocciカメラ")
                .withMessage("このカメラはボタンをタップしている間だけ録画されます。縦向きで投稿するようにしてください！")
                .withDuration(500)                                          //def
                .withEffect(Effectstype.SlideBottom)
                .show();

        mCameraView = (CameraGLView) rootView.findViewById(R.id.cameraView);
        //mCameraView.setVideoSize(1280, 720);
        mCameraView.setVideoSize(640, 480);
        //mCameraView.setOnTouchListener(mOnTouchListener);
        cameraProgress = (ProgressWheel) rootView.findViewById(R.id.cameraprogress_wheel);
        //progress = (CircleProgressBar) rootView.findViewById(R.id.circleProgress);
        //mRecordButton.setOnTouchListener(mOnTouchListener);
        //progress = (CircleProgressBar) rootView.findViewById(R.id.circleProgress);
        //recordButton = (ImageButton) rootView.findViewById(R.id.toukouButton);
        //recordButton.setOnTouchListener(mOnTouchListener);
        cancelButton = (Button) rootView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title("確認")
                        .content("すでに録画中の場合、その動画は初期化されますがよろしいですか？")
                        .positiveText("戻る")
                        .negativeText("いいえ")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                getActivity().finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                            }
                        }).show();
            }
        });

        toukouButton = (ImageButton) rootView.findViewById(R.id.toukouButton);
        progress = (CircleProgressBar) rootView.findViewById(R.id.circleProgress);

        toukouButton.setOnTouchListener(mOnTouchListener);

        sbv = (SlideBottomPanel) rootView.findViewById(R.id.sbv);

        restname_spinner = (MaterialBetterSpinner) rootView.findViewById(R.id.restname_spinner);
        category_spinner = (MaterialBetterSpinner) rootView.findViewById(R.id.category_spinner);
        mood_spinner = (MaterialBetterSpinner) rootView.findViewById(R.id.mood_spinner);
        edit_value = (MaterialEditText) rootView.findViewById(R.id.edit_value);
        edit_comment = (MaterialEditText) rootView.findViewById(R.id.edit_comment);

        restaddButton = (ImageButton) rootView.findViewById(R.id.restaddButton);

        final String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        final String[] MOOD = getResources().getStringArray(R.array.list_mood);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        category_spinner.setAdapter(categoryAdapter);
        category_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategory_id = position + 2;
            }
        });

        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, MOOD);
        mood_spinner.setAdapter(moodAdapter);
        mood_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTag_id = position + 2;
            }
        });

        restAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        restname_spinner.setAdapter(restAdapter);
        restname_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRest_id = GocciCameraActivity.rest_id.get(position);
            }
        });

        restaddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTenpo();
            }
        });
        /*
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        SmartTabLayout viewPagerTab = (SmartTabLayout) rootView.findViewById(R.id.viewpagertab);

        adapter = new ViewPagerItemAdapter(ViewPagerItems.with(getActivity())
                .add(R.string.lat, R.layout.view_camera_1)
                .add(R.string.lon, R.layout.view_camera_2)
                .create());

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(this);

*/
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.arg1 < 7000) {

                } else {
                    cameraProgress.setVisibility(View.VISIBLE);
                    //onFinishPressed();
                    if (!isFinish) {
                        Log.e("終了ログ", String.valueOf(msg.arg1));
                        progress.setProgress(100);
                        pauseRecording();
                        stopRecording();
                        isFinish = true;
                    }
                    //Toast.makeText(getActivity(), "finishRecord", Toast.LENGTH_SHORT).show();
                }
                int circle = (int) (msg.arg1 * 1.0 / 70);

                if (circle > 50) {
                    progress.setProgress(circle + 1);
                } else {
                    progress.setProgress(circle);
                }
                //progress.invalidate();
                super.handleMessage(msg);
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);
        return rootView;
    }

    private void getLocation(final Context context, Location location) {
        Log.e("ログ", "get位置呼ばれたよ");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        String mSearch_mapUrl = Const.getNearAPI(latitude, longitude);
        getTenpoJson(context, mSearch_mapUrl);
    }

    private void getTenpoJson(final Context context, String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                GocciCameraActivity.restname.clear();
                GocciCameraActivity.rest_id.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        final String rest_name = jsonObject.getString("restname");
                        int rest_id = jsonObject.getInt("rest_id");

                        GocciCameraActivity.restname.add(rest_name);
                        GocciCameraActivity.rest_id.add(rest_id);
                    }
                    restAdapter.addAll(GocciCameraActivity.restname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void startPlay() {
        //recorderManager.reset();

        //ここで記入済みの値を持って行こう
        if (edit_value.getText().length() != 0) {
            mValue = edit_value.getText().toString();
        }
        if (edit_comment.getText().length() != 0) {
            mMemo = edit_comment.getText().toString();
        }

        Intent intent = new Intent(getActivity(), CameraPreviewActivity.class);
        intent.putExtra("rest_id", mRest_id);
        intent.putExtra("video_url", mFinalVideoUrl);
        intent.putExtra("aws", mAwsPostName);
        intent.putExtra("category_id", mCategory_id);
        intent.putExtra("tag_id", mTag_id);
        intent.putExtra("memo", mMemo);
        intent.putExtra("value", mValue);
        intent.putExtra("isNewRestname", mIsnewRestname);
        intent.putExtra("lat", latitude);
        intent.putExtra("lon", longitude);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) Log.v(TAG, "onStart:");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (!mIsRecording) {
            startRecording();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        mCameraView.onResume();
        dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // センサマネージャへリスナーを登録(implements SensorEventListenerにより、thisで登録する)
        for (Sensor sensor : sensors) {

            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                mIsMagSensor = true;
            }

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                mIsAccSensor = true;
            }
        }

        checkPlayServices();

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && !isLocationUpdating) {
                if (GocciCameraActivity.isLocationOnOff) {
                    startLocationUpdates();
                }
            }
        } else {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new MaterialDialog.Builder(getActivity())
                        .title("位置情報取得について")
                        .content("位置情報を使いたいのですが、GPSが無効になっています。" + "設定を変更しますか？")
                        .positiveText("はい")
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText("いいえ")
                        .negativeColorRes(R.color.material_drawer_primary_light)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(settingIntent);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                Toast.makeText(getActivity(), "カメラを閉じます", Toast.LENGTH_LONG).show();
                            }
                        }).show();

            } else {
                firstLocation();
            }

        }
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        //stopRecording();
        mCameraView.onPause();
        super.onPause();

        if (dialogBuilder.isShowing()) {
            dialogBuilder.dismiss();
        }

        if (isLocationUpdating) {
            stopLocationUpdates();
        }

        if (mIsMagSensor || mIsAccSensor) {
            mSensorManager.unregisterListener(this);
            mIsMagSensor = false;
            mIsAccSensor = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.v(TAG, "onDestroy:");
        handler.removeCallbacks(progressRunnable);
        mCameraView.onFinish();
    }

    /*
     *
     */
    public final void fixedScreenOrientation(final boolean fixed) {
        getActivity().setRequestedOrientation(
                fixed ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * method when touch record button
     */
    /*
    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.record_button:
                    if (!mIsRecording) {
                        startRecording();
                    } else {
                        stopRecording();
                    }
                    break;
            }
        }
    };
    */

    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mIsRecording) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        resumeRecording();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        pauseRecording();
                        break;
                }
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * start recording
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because preparing
     * of encoder may be heavy work on some devices
     */
    private void startRecording() {
        if (mIsRecording) return;
        if (isMax) return;

        if (DEBUG) Log.v(TAG, "start:");
        try {
            //mRecordButton.setColorFilter(0xffffff00);    // turn yellow
            mMovieName = TAG + System.nanoTime();
            if (true) {
                // for video capturing
                mVideoEncoder = new TLMediaVideoEncoder(getActivity(), mMovieName, mMediaEncoderListener);
                try {
                    mVideoEncoder.setFormat(mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
                    //画質あげる
                    mVideoEncoder.prepare();
                } catch (Exception e) {
                    Log.e(TAG, "startRecording:", e);
                    mVideoEncoder.release();
                    mVideoEncoder = null;
                    throw e;
                }
            }
            if (true) {
                // for audio capturing
                mAudioEncoder = new TLMediaAudioEncoder(getActivity(), mMovieName, mMediaEncoderListener);
                try {
                    mAudioEncoder.prepare();
                } catch (Exception e) {
                    Log.e(TAG, "startRecording:", e);
                    mAudioEncoder.release();
                    mAudioEncoder = null;
                    throw e;
                }
            }
            if (mVideoEncoder != null) {
                mVideoEncoder.start(true);
            }
            if (mAudioEncoder != null) {
                mAudioEncoder.start(true);
            }
            mIsRecording = true;

        } catch (Exception e) {
            //mRecordButton.setColorFilter(0);
            Log.e(TAG, "startCapture:", e);
        }
        fixedScreenOrientation(mIsRecording);
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (!mIsRecording) return;
        isMax = false;
        totalTime = 0;
        isStart = false;
        handler.removeCallbacks(progressRunnable);

        if (DEBUG) Log.v(TAG, "stop");
        mIsRecording = false;
        //mRecordButton.setColorFilter(0);    // return to default color
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
        }
        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
        }
        fixedScreenOrientation(mIsRecording);
        try {
            mMuxer = new TLMediaMovieBuilder(getActivity(), mMovieName);
            mMuxer.build(mTLMediaMovieBuilderCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * resume recording
     */
    private void resumeRecording() {
        if (!mIsRecording) return;
        if (isMax) return;

        //mRecordButton.setColorFilter(0xffff0000);    // turn red
        try {
            if (mVideoEncoder != null) {
                if (mVideoEncoder.isPaused())
                    mVideoEncoder.resume();
            }
            if (mAudioEncoder != null) {
                if (mAudioEncoder.isPaused())
                    mAudioEncoder.resume();
            }
            isStart = true;
            videoStartTime = new Date().getTime();
        } catch (IOException e) {
            stopRecording();
        }
    }

    /**
     * pause recording
     */
    private void pauseRecording() {
        if (!mIsRecording) return;
        if (!isMax) {
            totalTime += new Date().getTime() - videoStartTime;
            videoStartTime = 0;
        }
        isStart = false;

        //mRecordButton.setColorFilter(0xffffff00);    // turn yellow
        if ((mVideoEncoder != null) && !mVideoEncoder.isPaused())
            try {
                mVideoEncoder.pause();
            } catch (Exception e) {
                Log.e(TAG, "pauseRecording:", e);
                mVideoEncoder.release();
                mVideoEncoder = null;
            }
        if ((mAudioEncoder != null) && !mAudioEncoder.isPaused())
            try {
                mAudioEncoder.pause();
            } catch (Exception e) {
                Log.e(TAG, "pauseRecording:", e);
                mAudioEncoder.release();
                mAudioEncoder = null;
            }
    }

    /**
     * callback methods from encoder
     */
    private final TLMediaEncoder.MediaEncoderListener mMediaEncoderListener
            = new TLMediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
        }

        @Override
        public void onStopped(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
        }

        @Override
        public void onResume(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onResume:encoder=" + encoder);
            if (encoder instanceof TLMediaVideoEncoder)
                mCameraView.setVideoEncoder((TLMediaVideoEncoder) encoder);
        }

        @Override
        public void onPause(TLMediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPause:encoder=" + encoder);
            if (encoder instanceof TLMediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };

    /**
     * callback methods from TLMediaMovieBuilder
     */
    private TLMediaMovieBuilder.TLMediaMovieBuilderCallback mTLMediaMovieBuilderCallback
            = new TLMediaMovieBuilder.TLMediaMovieBuilderCallback() {

        @Override
        public void onFinished(String output_path, String awspostname) {
            if (DEBUG) Log.v(TAG, "onFinished:");
            mMuxer = null;
            if (!TextUtils.isEmpty(output_path)) {
                final Activity activity = up18CameraFragment.this.getActivity();
                if ((activity == null) || activity.isFinishing()) return;
                // add movie to gallery
                MediaScannerConnection.scanFile(activity, new String[]{output_path}, null, null);

                mCameraView.onFinish();
                mFinalVideoUrl = output_path;
                mAwsPostName = awspostname;
                startPlay();
                //画面遷移するときにやる
            }
        }

        @Override
        public void onError(Exception e) {
            if (DEBUG) Log.v(TAG, "onError:" + e.getMessage());
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
        }

        if (magneticValues != null && accelerometerValues != null) {

            SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);

            //Activityの表示が縦固定の場合。横向きになる場合、修正が必要です
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, orientationValues);

            int degree = radianToDegree(orientationValues[2]);

            if (degree <= -60) {
                if (!dialogBuilder.isShowing()) {
                    dialogBuilder
                            .withTitle("Gocciカメラ")
                            .withMessage("このカメラでは、縦向きで投稿するようにしてください！")
                            .withDuration(500)                                          //def
                            .withEffect(Effectstype.SlideBottom)
                            .isCancelableOnTouchOutside(false)
                            .show();
                }
            }
            if (-60 < degree && degree <= 60) {
                if (dialogBuilder.isShowing()) {
                    dialogBuilder.dismiss();
                }
            }
            if (60 < degree) {
                if (!dialogBuilder.isShowing()) {
                    dialogBuilder
                            .withTitle("Gocciカメラ")
                            .withMessage("このカメラでは、縦向きで投稿するようにしてください！")
                            .withDuration(500)                                          //def
                            .withEffect(Effectstype.SlideBottom)
                            .isCancelableOnTouchOutside(false)
                            .show();
                }
            }
        }
    }

    int radianToDegree(float rad) {
        return (int) Math.floor(Math.toDegrees(rad));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class ProgressRunnable implements Runnable {

        @Override
        public void run() {
            int time = 0;
            time = checkIfMax(new Date().getTime());
            Message message = new Message();
            message.arg1 = time;
            handler.sendMessage(message);
            // System.out.println(time);
            handler.postDelayed(this, 10);
        }
    }

    public int checkIfMax(long timeNow) {
        int during = 0;
        if (isStart) {
            during = (int) (totalTime + (timeNow - videoStartTime));
            if (during >= MAX_TIME) {
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

    private void createTenpo() {
        new MaterialDialog.Builder(getActivity())
                .title("店舗追加")
                .content("あなたのいるお店の名前を入力してください。※位置情報は現在の位置を使います。")
                .input("店舗名", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText("送信する")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mRest_name = dialog.getInputEditText().getText().toString();

                        /* TODO rest_idを発行してもらう　*/

                        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(getActivity()));
                        Const.asyncHttpClient.get(getActivity(), Const.getPostRestAddAPI(mRest_name, latitude, longitude), new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                cameraProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(getActivity(), "通信に失敗しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                try {
                                    String message = response.getString("message");

                                    if (message.equals("店舗を追加しました")) {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        //店名をセット
                                        mIsnewRestname = true;
                                        restname_spinner.setText(mRest_name);
                                        mRest_id = response.getInt("rest_id");
                                        restname_spinner.setClickable(false);
                                    } else {
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFinish() {
                                cameraProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                })
                .show();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private void firstLocation() {
        SmartLocation.with(getActivity().getApplicationContext()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    getLocation(getActivity(), location);
                } else {
                    Toast.makeText(getActivity(), "位置情報が読み取れないため、カメラを閉じます", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        Log.e("DEBUG", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void startLocationUpdates() {
        isLocationUpdating = true;
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        isLocationUpdating = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!isLocationUpdating) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e("ログ", "All location settings are satisfied.");
                //firstLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e("ログ", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("ログ", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e("ログ", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                Toast.makeText(getActivity(), "位置情報機が取れないので、カメラを終了します", Toast.LENGTH_LONG).show();
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(getActivity(), location);
        stopLocationUpdates();
    }
}

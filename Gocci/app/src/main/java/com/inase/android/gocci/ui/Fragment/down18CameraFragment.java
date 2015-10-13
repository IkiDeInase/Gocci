package com.inase.android.gocci.ui.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
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
import com.coremedia.iso.boxes.Container;
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
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.ui.activity.CameraPreviewActivity;
import com.inase.android.gocci.ui.activity.GocciCameraActivity;
import com.inase.android.gocci.ui.view.CircleProgressBar;
import com.inase.android.gocci.ui.view.MySurfaceView;
import com.inase.android.gocci.utils.camera.CameraManager;
import com.inase.android.gocci.utils.camera.RecorderManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.leakcanary.RefWatcher;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import me.next.slidebottompanel.SlideBottomPanel;

/**
 * Created by kinagafuji on 15/06/26.
 */
public class down18CameraFragment extends Fragment implements SensorEventListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult> {

    private RecorderManager recorderManager = null;
    public static CameraManager cameraManager;

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

    private ImageButton toukouButton;

    private double latitude;
    private double longitude;

    private MaterialBetterSpinner restname_spinner;
    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner mood_spinner;
    private MaterialEditText edit_value;
    private MaterialEditText edit_comment;
    private ImageButton restaddButton;

    private ArrayAdapter<String> restAdapter;

    private SlideBottomPanel sbv;

    private SensorManager mSensorManager;
    private boolean mIsMagSensor;
    private boolean mIsAccSensor;

    private static final int MATRIX_SIZE = 16;
    /* 回転行列 */
    float[] inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[] I = new float[MATRIX_SIZE];

    /* センサーの値 */
    float[] orientationValues = new float[3];
    float[] magneticValues = new float[3];
    float[] accelerometerValues = new float[3];

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

    private Snackbar bar;

    public down18CameraFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();

            checkLocationSettings();
        }
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_down18, container, false);

        cameraProgress = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        MySurfaceView videoSurface = (MySurfaceView) rootView.findViewById(R.id.camera_view);
        cameraManager = getCameraManager();
        recorderManager = new RecorderManager(getCameraManager(), videoSurface, getActivity());

        Button backButton = (Button) rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toukouButton = (ImageButton) rootView.findViewById(R.id.toukou_button);
        progress = (CircleProgressBar) rootView.findViewById(R.id.circle_progress);

        toukouButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        // sign.setPressed(true);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                cameraManager.getCamera().autoFocus(null);
                            }
                        });
                        recorderManager.startRecord(true);

                    } finally {
                        muteAll(true);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        // sign.setPressed(false);
                        recorderManager.stopRecord();
                    } finally {
                        muteAll(false);
                        //
                    }
                }
                return true;
            }
        });

        sbv = (SlideBottomPanel) rootView.findViewById(R.id.sbv);

        bar = Snackbar.make(sbv, getString(R.string.camera_alert), Snackbar.LENGTH_INDEFINITE);

        restname_spinner = (MaterialBetterSpinner) rootView.findViewById(R.id.restname_spinner);
        category_spinner = (MaterialBetterSpinner) rootView.findViewById(R.id.category_spinner);
        mood_spinner = (MaterialBetterSpinner) rootView.findViewById(R.id.mood_spinner);
        edit_value = (MaterialEditText) rootView.findViewById(R.id.edit_value);
        edit_comment = (MaterialEditText) rootView.findViewById(R.id.edit_comment);

        restaddButton = (ImageButton) rootView.findViewById(R.id.rest_add_button);

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

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1 < 7000) {
                    // System.out.println("Clickable");
                    // finishButton.setClickable(true);
                    // finishButton
                    // .setBackgroundResource(R.drawable.btn_capture_arrow);
                } else {
                    cameraProgress.setVisibility(View.VISIBLE);

                    // System.out.println("UnClickable");
                    // finishButton.setClickable(false);
                    onFinishPressed();
                    // finishButton
                    // .setBackgroundResource(R.drawable.btn_capture_arrow_pressed);
                }
                int circle = (int) (msg.arg1 * 1.0 / 70);

                if (circle > 50) {
                    progress.setProgress(circle + 1);
                } else {
                    progress.setProgress(circle);
                }
                super.handleMessage(msg);
                // //
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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
                        .title(getString(R.string.camera_location_title))
                        .content(getString(R.string.camera_location_message))
                        .positiveText(getString(R.string.camera_location_yeah))
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText(getString(R.string.camera_location_no))
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
                                Toast.makeText(getActivity(), getString(R.string.camera_location_cancel), Toast.LENGTH_LONG).show();
                            }
                        }).show();

            } else {
                firstLocation();
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();

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
                if (!bar.isShown()) {
                    bar.show();
                }
            }
            if (-60 < degree && degree <= 60) {
                if (bar.isShown()) {
                    bar.dismiss();
                }
            }
            if (60 < degree) {
                if (!bar.isShown()) {
                    bar.show();
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

    private void getLocation(final Context context, Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        String mSearch_mapUrl = Const.getNearAPI(latitude, longitude);
        getTenpoJson(context, mSearch_mapUrl);
    }

    private void getTenpoJson(final Context context, String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

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
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void muteAll(boolean isMute) {
        // ((AudioManager) this.getSystemService(Context.AUDIO_SERVICE))
        // .setStreamSolo(AudioManager.STREAM_SYSTEM, isMute);
        // ((AudioManager) this.getSystemService(Context.AUDIO_SERVICE))
        // .setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
        List<Integer> streams = new ArrayList<Integer>();
        Field[] fields = AudioManager.class.getFields();
        for (Field field : fields) {
            if (field.getName().startsWith("STREAM_")
                    && Modifier.isStatic(field.getModifiers())
                    && field.getType() == int.class) {
                Integer stream = null;
                try {
                    stream = (Integer) field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                streams.add(stream);
            }
        }
    }

    public static CameraManager getCameraManager() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
        return cameraManager;
    }


    public void onBackPressed() {
        new MaterialDialog.Builder(getActivity())
                .content(getString(R.string.check_videoposting_cancel))
                .positiveText(getString(R.string.check_videoposting_yeah))
                .positiveColorRes(R.color.gocci_header)
                .negativeText(getString(R.string.check_videoposting_no))
                .negativeColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        recorderManager.reset();
                        getActivity().finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }

    public void onFinishPressed() {
        if (recorderManager.getVideoTempFiles().size() != 0) {
            combineFiles();
        } else {
            recorderManager.reset();
        }
    }

    public void startPlay() {
        recorderManager.reset();

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

    private void combineFiles() {
        mFinalVideoUrl = getFinalVideoFileName();

        try {
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            for (String fileName : recorderManager.getVideoTempFiles()) {
                try {
                    Movie movie = MovieCreator.build(fileName);
                    for (Track t : movie.getTracks()) {
                        if (t.getHandler().equals("soun")) {
                            audioTracks.add(t);
                        }
                        if (t.getHandler().equals("vide")) {
                            videoTracks.add(t);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            Container out = new DefaultMp4Builder().build(result);

            ContentResolver contentResolver = getActivity().getContentResolver();
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Video.Media.TITLE, mAwsPostName);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, mFinalVideoUrl);
            contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            FileOutputStream fos = new FileOutputStream(new File(mFinalVideoUrl));
            out.writeContainer(fos.getChannel());
            fos.close();

            startPlay();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getFinalVideoFileName() {
        mAwsPostName = getDateTimeString();
        return recorderManager.getVideoParentpath() + "/" + mAwsPostName + ".mp4";
    }

    private static final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        return dateTimeFormat.format(now.getTime());
    }

    @Override
    public void onDestroy() {
        muteAll(false);
        super.onDestroy();
        recorderManager.reset();
        handler.removeCallbacks(progressRunnable);
        RefWatcher refWatcher = Application_Gocci.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void createTenpo() {
        new MaterialDialog.Builder(getActivity())
                .content(getString(R.string.add_restname))
                .input(getString(R.string.restname), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0);
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .alwaysCallInputCallback()
                .positiveText(getString(R.string.add_restname_post))
                .positiveColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mRest_name = dialog.getInputEditText().getText().toString();

                        Application_Gocci.getJsonAsyncHttpClient(Const.getPostRestAddAPI(mRest_name, latitude, longitude), new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                cameraProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    String message = response.getString("message");

                                    if (message.equals(getString(R.string.add_restname_complete_message))) {
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

    private class ProgressRunnable implements Runnable {

        @Override
        public void run() {
            int time = 0;
            time = recorderManager.checkIfMax(new Date().getTime());
            Message message = new Message();
            message.arg1 = time;
            handler.sendMessage(message);
            // System.out.println(time);
            handler.postDelayed(this, 10);

        }
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
                    Toast.makeText(getActivity(), getString(R.string.finish_causedby_location), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
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
                //firstLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
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
                Toast.makeText(getActivity(), getString(R.string.finish_causedby_location), Toast.LENGTH_LONG).show();
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

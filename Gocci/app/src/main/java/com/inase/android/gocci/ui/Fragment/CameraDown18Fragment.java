package com.inase.android.gocci.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.coremedia.iso.boxes.Container;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.datasource.repository.NearRepository;
import com.inase.android.gocci.datasource.repository.NearRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.usecase.NearDataUseCase;
import com.inase.android.gocci.domain.usecase.NearDataUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowCameraPresenter;
import com.inase.android.gocci.ui.activity.CameraActivity;
import com.inase.android.gocci.ui.activity.CameraPreviewActivity;
import com.inase.android.gocci.ui.view.MySurfaceView;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.camera.CameraManager;
import com.inase.android.gocci.utils.camera.RecorderManager;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class CameraDown18Fragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, ShowCameraPresenter.ShowCameraView {

    @Bind(R.id.camera_view)
    MySurfaceView mCameraView;
    @Bind(R.id.cancel_fab)
    FloatingActionButton mCancelFab;
    @Bind(R.id.circle_progress)
    CircleProgressView mCircleProgress;
    @Bind(R.id.toukou_button)
    ImageButton mToukouButton;
    @Bind(R.id.comment_action)
    FloatingActionButton mCommentAction;
    @Bind(R.id.value_action)
    FloatingActionButton mValueAction;
    @Bind(R.id.category_action)
    FloatingActionButton mCategoryAction;
    @Bind(R.id.restaurant_action)
    FloatingActionButton mRestaurantAction;
    @Bind(R.id.menu_fab)
    FloatingActionMenu mMenuFab;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;

    private RecorderManager recorderManager = null;
    public static CameraManager cameraManager;

    private Runnable progressRunnable = null;
    private Handler handler = null;

    private String mRest_id = "1";
    private int mCategory_id = 1;
    private String mRest_name = "";
    private String mFinalVideoUrl = "";
    private String mAwsPostName = "";
    private String mValue = "";
    private String mMemo = "";
    private boolean mIsnewRestname = false;

    private String latitude = "";
    private String longitude = "";

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

    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private final ExampleSpringListener mSpringListener = new ExampleSpringListener();
    private Spring mScaleSpring;

    private ShowCameraPresenter mPresenter;

    private boolean isFinish = false;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    public CameraDown18Fragment() {

    }

    @Override
    public void onDestroyView() {
        muteAll(false);
        super.onDestroyView();
        recorderManager.reset();
        ButterKnife.unbind(this);
    }

    @Override
    public void showNoResultCase(Const.APICategory api) {

    }

    @Override
    public void hideNoResultCase() {

    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(getActivity(), api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    @Override
    public void showResult(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray) {
        CameraActivity.restname = restnames;
        CameraActivity.rest_nameArray.addAll(restnameArray);
        CameraActivity.rest_idArray.addAll(restIdArray);
    }

    private class ExampleSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            // On each update of the spring value, we adjust the scale of the image view to match the
            // springs new value. We use the SpringUtil linear interpolation function mapValueFromRangeToRange
            // to translate the spring's 0 to 1 scale to a 100% to 50% scale range and apply that to the View
            // with setScaleX/Y. Note that rendering is an implementation detail of the application and not
            // Rebound itself. If you need Gingerbread compatibility consider using NineOldAndroids to update
            // your view properties in a backwards compatible manner.
            float value = (float) spring.getCurrentValue();
            float scale = 1f - (value * 0.3f);
            mCircleProgress.setScaleX(scale);
            mCircleProgress.setScaleY(scale);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();

            checkLocationSettings();
        }

        final API3 api3Impl = API3.Impl.getRepository();
        NearRepository nearRepositoryImpl = NearRepositoryImpl.getRepository(api3Impl);
        NearDataUseCase neardataUseCaseImpl = NearDataUseCaseImpl.getUseCase(nearRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowCameraPresenter(neardataUseCaseImpl);
        mPresenter.setCameraView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera_down18, container, false);
        ButterKnife.bind(this, rootView);
        applicationGocci = (Application_Gocci) getActivity().getApplication();
        cameraManager = getCameraManager();
        recorderManager = new RecorderManager(getCameraManager(), mCameraView, getActivity());

        mCircleProgress.setValue(0);
        mCircleProgress.setBarColor(getResources().getColor(R.color.gocci_1), getResources().getColor(R.color.gocci_2), getResources().getColor(R.color.gocci_3), getResources().getColor(R.color.gocci_4));

        mScaleSpring = mSpringSystem.createSpring();

        mScaleSpring.setEndValue(1);

        mCommentAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .content(getString(R.string.comment))
                        .contentColorRes(R.color.nameblack)
                        .contentGravity(GravityEnum.CENTER)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .widgetColorRes(R.color.nameblack)
                        .positiveText(getString(R.string.complete))
                        .positiveColorRes(R.color.gocci_header)
                        .input("", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                mMemo = charSequence.toString();
                                mCommentAction.setLabelText(charSequence.toString());
                            }
                        }).show();
            }
        });
        mValueAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .content(getString(R.string.value))
                        .contentGravity(GravityEnum.CENTER)
                        .contentColorRes(R.color.nameblack)
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .widgetColorRes(R.color.nameblack)
                        .positiveText(getString(R.string.complete))
                        .positiveColorRes(R.color.gocci_header)
                        .input("", "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                mValue = charSequence.toString();
                                mValueAction.setLabelText(charSequence.toString() + "円");
                            }
                        }).show();
            }
        });
        mCategoryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .content(getString(R.string.category))
                        .contentGravity(GravityEnum.CENTER)
                        .contentColorRes(R.color.nameblack)
                        .items(R.array.list_category)
                        .itemsColorRes(R.color.nameblack)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                materialDialog.dismiss();
                                mCategory_id = i + 2;
                                mCategoryAction.setLabelText(charSequence.toString());
                            }
                        }).show();
            }
        });
        mRestaurantAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraActivity.restname[0] != null) {
                    new MaterialDialog.Builder(getActivity())
                            .content(getString(R.string.restaurant))
                            .contentGravity(GravityEnum.CENTER)
                            .contentColorRes(R.color.nameblack)
                            .positiveText(getString(R.string.no_exist_restaurant))
                            .positiveColorRes(R.color.nameblack)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                    createTenpo();
                                }
                            })
                            .items(CameraActivity.restname)
                            .itemsColorRes(R.color.nameblack)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    materialDialog.dismiss();
                                    mRest_name = charSequence.toString();
                                    mRest_id = CameraActivity.rest_idArray.get(i);
                                    mRestaurantAction.setLabelText(charSequence.toString());
                                }
                            }).show();
                } else {
                    Toast.makeText(getActivity(), "位置情報を取得しています", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .content(getString(R.string.check_videoposting_cancel))
                        .positiveText(getString(R.string.check_videoposting_yeah))
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText(getString(R.string.check_videoposting_no))
                        .negativeColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                recorderManager.reset();
                                getActivity().finish();
                            }
                        }).show();
            }
        });

        mMenuFab.setClosedOnTouchOutside(true);

        mToukouButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mScaleSpring.setEndValue(0);
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
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mScaleSpring.setEndValue(1);
                        try {
                            // sign.setPressed(false);
                            recorderManager.stopRecord();
                        } finally {
                            muteAll(false);
                            //
                        }
                        break;
                }
                return true;
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1 >= 7000) {
                    mProgressWheel.setVisibility(View.VISIBLE);

                    if (!isFinish) {
                        mCircleProgress.setValue(100);
                        onFinishPressed();
                        isFinish = true;
                    }
                    // System.out.println("UnClickable");
                    // finishButton.setClickable(false);
                    // finishButton
                    // .setBackgroundResource(R.drawable.btn_capture_arrow_pressed);
                }
                int circle = (int) (msg.arg1 * 1.0 / 70);

                if (circle > 50) {
                    mCircleProgress.setValue(circle + 1);
                } else {
                    mCircleProgress.setValue(circle);
                }
                super.handleMessage(msg);
                // //
            }
        };

        progressRunnable = new ProgressRunnable();
        //handler.post(progressRunnable);

        createCustomAnimation();
        return rootView;
    }

    private void createCustomAnimation() {

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mMenuFab.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mMenuFab.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mMenuFab.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mMenuFab.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mMenuFab.getMenuIconView().setImageResource(mMenuFab.isOpened()
                        ? R.drawable.ic_clear_white_24dp : R.drawable.ic_create_white_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        mMenuFab.setIconToggleAnimatorSet(set);
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
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("CameraDown18");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mPresenter.resume();
        mScaleSpring.addListener(mSpringListener);
        checkPlayServices();

        if (handler != null && !isFinish) handler.post(progressRunnable);

        BusHolder.get().register(this);

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && !isLocationUpdating) {
                if (CameraActivity.isLocationOnOff) {
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
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(settingIntent);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
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
        mPresenter.pause();
        mScaleSpring.removeListener(mSpringListener);
        if (isLocationUpdating) {
            stopLocationUpdates();
        }
        if (handler != null) handler.removeCallbacks(progressRunnable);

        BusHolder.get().unregister(this);
        super.onPause();
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

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.CAMERA) {
            if (event.apiCategory == Const.APICategory.SET_RESTADD) {
                mIsnewRestname = true;
                mRest_id = event.id;
                mRestaurantAction.setLabelText(mRest_name);
            }
        }
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case SET_RESTADD:
                API3PostUtil.setRestAsync(getActivity(), Const.ActivityCategory.CAMERA, mRest_name, longitude, latitude);
                break;
            case GET_NEAR_FIRST:
                mPresenter.getNearData(Const.APICategory.GET_NEAR_FIRST, API3.Util.getGetNearAPI(latitude, longitude));
                break;
            default:
                break;
        }
    }

    private void getLocation(Location location) {
        CameraActivity.isLocationOnOff = true;
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        API3.Util.GetNearLocalCode localCode = API3.Impl.getRepository().GetNearParameterRegex(latitude, longitude);
        if (localCode == null) {
            mPresenter.getNearData(Const.APICategory.GET_NEAR_FIRST, API3.Util.getGetNearAPI(latitude, longitude));
        } else {
            Toast.makeText(getActivity(), API3.Util.GetNearLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
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

    public void onFinishPressed() {
        if (recorderManager.getVideoTempFiles().size() != 0) {
            combineFiles();
        } else {
            recorderManager.reset();
        }
    }

    public void startPlay() {
        recorderManager.reset();

        Intent intent = new Intent(getActivity(), CameraPreviewActivity.class);
        intent.putExtra("restname", mRest_name);
        intent.putExtra("rest_id", mRest_id);
        intent.putExtra("video_url", mFinalVideoUrl);
        intent.putExtra("aws", mAwsPostName);
        intent.putExtra("category_id", mCategory_id);
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
                        if ("soun".equals(t.getHandler())) {
                            audioTracks.add(t);
                        }
                        if ("vide".equals(t.getHandler())) {
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
        mAwsPostName = Util.getDateTimeString();
        return recorderManager.getVideoParentpath() + "/" + mAwsPostName + ".mp4";
    }

    private void createTenpo() {
        new MaterialDialog.Builder(getActivity())
                .content(getString(R.string.add_restname))
                .contentColorRes(R.color.nameblack)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.nameblack)
                .positiveText(getString(R.string.send))
                .positiveColorRes(R.color.gocci_header)
                .input("", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        mRest_name = charSequence.toString();
                        API3PostUtil.setRestAsync(getActivity(), Const.ActivityCategory.CAMERA, mRest_name, longitude, latitude);
                    }
                }).show();
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
                    getLocation(location);
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
                CameraActivity.isLocationOnOff = true;
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
                CameraActivity.isLocationOnOff = false;
                Toast.makeText(getActivity(), getString(R.string.finish_causedby_location), Toast.LENGTH_LONG).show();
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
        stopLocationUpdates();
    }
}

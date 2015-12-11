package com.inase.android.gocci.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
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
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import com.inase.android.gocci.presenter.ShowCameraPresenter;
import com.inase.android.gocci.ui.activity.CameraActivity;
import com.inase.android.gocci.ui.activity.CameraPreviewActivity;
import com.inase.android.gocci.ui.view.CameraGLView;
import com.inase.android.gocci.utils.camera.TLMediaAudioEncoder;
import com.inase.android.gocci.utils.camera.TLMediaEncoder;
import com.inase.android.gocci.utils.camera.TLMediaMovieBuilder;
import com.inase.android.gocci.utils.camera.TLMediaVideoEncoder;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class CameraUp18Fragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, ShowCameraPresenter.ShowCameraView {
    private static final boolean DEBUG = true;
    private static final String TAG = "GocciCamera";
    @Bind(R.id.camera_view)
    CameraGLView mCameraView;
    @Bind(R.id.circle_progress)
    CircleProgressView mCircleProgress;
    @Bind(R.id.toukou_button)
    ImageButton mToukouButton;
    @Bind(R.id.cancel_fab)
    FloatingActionButton mCancelFab;
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

    private TLMediaVideoEncoder mVideoEncoder;
    private TLMediaAudioEncoder mAudioEncoder;
    private TLMediaMovieBuilder mMuxer;
    private boolean mIsRecording;
    private String mMovieName;

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

    public static final int MAX_TIME = 7000;
    private boolean isMax = false;
    private long videoStartTime;
    private int totalTime = 0;
    private boolean isStart = false;
    private boolean isFinish = false;

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

    public CameraUp18Fragment() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCameraView.onFinish();
        handler.removeCallbacks(progressRunnable);
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
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
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
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
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
        final View rootView = inflater.inflate(R.layout.fragment_camera_up18, container, false);
        ButterKnife.bind(this, rootView);

        mCameraView.setVideoSize(480, 480);

        mCircleProgress.setValue(0);

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
                        .positiveText("完了")
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
                        .positiveText("完了")
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
                new MaterialDialog.Builder(getActivity())
                        .content("店舗")
                        .contentGravity(GravityEnum.CENTER)
                        .contentColorRes(R.color.nameblack)
                        .positiveText("店舗が無い場合はこちら")
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
                                getActivity().finish();
                            }
                        }).show();
            }
        });

        mMenuFab.setClosedOnTouchOutside(true);

        mToukouButton.setOnTouchListener(mOnTouchListener);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.arg1 < 7000) {

                } else {
                    mProgressWheel.setVisibility(View.VISIBLE);
                    //onFinishPressed();
                    if (!isFinish) {
                        mCircleProgress.setValue(100);
                        pauseRecording();
                        stopRecording();
                        isFinish = true;
                    }
                    //Toast.makeText(getActivity(), "finishRecord", Toast.LENGTH_SHORT).show();
                }
                int circle = (int) (msg.arg1 * 1.0 / 70);

                if (circle > 50) {
                    mCircleProgress.setValue(circle + 1);
                } else {
                    mCircleProgress.setValue(circle);
                }
                //progress.invalidate();
                super.handleMessage(msg);
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);

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

    private void getLocation(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        API3.Util.GetNearLocalCode localCode = API3.Impl.getRepository().GetNearParameterRegex(latitude, longitude);
        if (localCode == null) {
            mPresenter.getNearData(Const.APICategory.GET_NEAR_FIRST, API3.Util.getGetNearAPI(latitude, longitude));
        } else {
            Toast.makeText(getActivity(), API3.Util.GetNearLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public void startPlay() {
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

        mScaleSpring.addListener(mSpringListener);
        checkPlayServices();

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
        if (DEBUG) Log.v(TAG, "onPause:");
        //stopRecording();
        mCameraView.onPause();
        super.onPause();

        mScaleSpring.removeListener(mSpringListener);
        if (isLocationUpdating) {
            stopLocationUpdates();
        }

        BusHolder.get().unregister(this);
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

    public final void fixedScreenOrientation(final boolean fixed) {
        getActivity().setRequestedOrientation(
                fixed ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private final View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mIsRecording) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mScaleSpring.setEndValue(0);
                        resumeRecording();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mScaleSpring.setEndValue(1);
                        pauseRecording();
                        break;
                }
                return true;
            } else {
                return false;
            }
        }
    };

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

    private TLMediaMovieBuilder.TLMediaMovieBuilderCallback mTLMediaMovieBuilderCallback
            = new TLMediaMovieBuilder.TLMediaMovieBuilderCallback() {

        @Override
        public void onFinished(String output_path, String awspostname) {
            if (DEBUG) Log.v(TAG, "onFinished:");
            mMuxer = null;
            if (!TextUtils.isEmpty(output_path)) {
                final Activity activity = CameraUp18Fragment.this.getActivity();
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
            if (DEBUG) Log.v(TAG, "onIllegalError:" + e.getMessage());
        }
    };

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
                .content(getString(R.string.add_restname))
                .contentColorRes(R.color.nameblack)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .widgetColorRes(R.color.nameblack)
                .positiveText("送信")
                .positiveColorRes(R.color.gocci_header)
                .input("", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        mRest_name = charSequence.toString();
                        API3PostUtil.postRestAddAsync(getActivity(), Const.ActivityCategory.CAMERA, mRest_name, longitude, latitude);
                    }
                }).show();
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

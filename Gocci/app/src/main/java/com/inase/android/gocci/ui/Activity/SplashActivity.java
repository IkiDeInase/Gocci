package com.inase.android.gocci.ui.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.datasource.repository.LoginRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;
import com.inase.android.gocci.domain.usecase.UserLoginUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.RegIdRegisteredEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowUserLoginPresenter;
import com.inase.android.gocci.service.RegistrationIntentService;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.squareup.otto.Subscribe;


public class SplashActivity extends AppCompatActivity implements ShowUserLoginPresenter.ShowUserLogin {

    private Handler handler;
    private loginRunnable runnable;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private ShowUserLoginPresenter mPresenter;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    static final String TAG = "GCMDemo";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        applicationGocci = (Application_Gocci) getApplication();

        API3 api3Impl = API3.Impl.getRepository();
        LoginRepository loginRepositoryImpl = LoginRepositoryImpl.getRepository(api3Impl);
        UserLoginUseCase userLoginUseCaseImpl = UserLoginUseCaseImpl.getUseCase(loginRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowUserLoginPresenter(userLoginUseCaseImpl);
        mPresenter.setShowUserLoginView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("Splash");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(this);
        mPresenter.resume();

//        SavedData.setIdentityId(this, "us-east-1:b3c7e331-1f2b-476d-b58f-e23f96d97f08");

        if (Util.getConnectedState(this) != Util.NetworkStatus.OFF) {
            String mIdentityId = SavedData.getIdentityId(this);
            if (!mIdentityId.equals("no identityId")) {
                //２回目
                API3.Util.AuthLoginLocalCode localCode = API3.Impl.getRepository().AuthLoginParameterRegex(mIdentityId);
                if (localCode == null) {
                    mPresenter.loginUser(Const.APICategory.AUTH_LOGIN, API3.Util.getAuthLoginAPI(mIdentityId));
                } else {
                    Toast.makeText(this, API3.Util.AuthLoginLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checkPlayServices()) {
                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
                } else {
                    Log.e(TAG, "No valid Google Play Services APK found.");
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        BusHolder.get().unregister(this);
        mPresenter.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    @Subscribe
    public void subscribe(RegIdRegisteredEvent event) {
        handler = new Handler();
        runnable = new loginRunnable();
        handler.post(runnable);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showResult(Const.APICategory api) {
        API3PostUtil.setDeviceAsync(this, SavedData.getRegId(this), Const.OS, Build.VERSION.RELEASE, Build.MODEL);
        Intent intent = new Intent(SplashActivity.this, TimelineActivity.class);
        if (!SplashActivity.this.isFinishing()) {
            SplashActivity.this.startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            SplashActivity.this.finish();
        }
    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, final String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        handler = new Handler();
        runnable = new loginRunnable();
        handler.post(runnable);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    private class loginRunnable implements Runnable {
        @Override
        public void run() {
            Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
            if (!SplashActivity.this.isFinishing()) {
                SplashActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                SplashActivity.this.finish();
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case AUTH_LOGIN:
                mPresenter.loginUser(Const.APICategory.AUTH_LOGIN, API3.Util.getAuthLoginAPI(SavedData.getIdentityId(this)));
                break;
            default:
                break;
        }
    }
}





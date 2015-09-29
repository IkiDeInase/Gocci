package com.inase.android.gocci.ui.Activity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.facebook.AccessToken;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.BuildConfig;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.CreateProviderFinishEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.datasource.repository.LoginRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.User;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;
import com.inase.android.gocci.domain.usecase.UserLoginUseCaseImpl;
import com.inase.android.gocci.presenter.ShowUserLoginPresenter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity implements ShowUserLoginPresenter.ShowUserLogin {

    private Handler handler;
    private loginRunnable runnable;

    private final SplashActivity self = this;

    private String loginFrag;

    private static final String TAG_AUTH = "auth";
    private static final String TAG_SNS_FACEBOOK = "facebook";
    private static final String TAG_SNS_TWITTER = "twitter";
    private static final String TAG_NO_JUDGE = "no judge";

    private boolean isConversion = false;

    private static MobileAnalyticsManager analytics;

    private ShowUserLoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    Const.ANALYTICS_ID, //Amazon Mobile Analytics App ID
                    Const.IDENTITY_POOL_ID //Amazon Cognito Identity Pool ID
            );
        } catch (InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }

        setContentView(R.layout.activity_splash);

        LoginRepository loginRepositoryImpl = LoginRepositoryImpl.getRepository();

        //Domain Layer: UseCase
        UserLoginUseCase userLoginUseCaseImpl = UserLoginUseCaseImpl.getUseCase(loginRepositoryImpl, UIThread.getInstance());

        //Initialize Presenter
        mPresenter = new ShowUserLoginPresenter(userLoginUseCaseImpl);
        mPresenter.setShowUserLoginView(this);

        if (Util.getConnectedState(SplashActivity.this) != Util.NetworkStatus.OFF) {

            //SavedData.setServerName(this, "kazu0914");
            //SavedData.setServerPicture(this, "https://graph.facebook.com/100004985405636/picture");
            //SavedData.setLoginJudge(this, TAG_SNS_FACEBOOK);
            //SavedData.setRegId(this, "APA91bFlIfRuMRWjMbKfXyC5votBewFcpj71N0j4aiSEgqvHeHsoDcCjS6TuUTxdHnj13cT_40mkflrl5aqigmPGdj5VH0njkc0MM6aMgkExqZoRVZAv8BcUEFy09ZUaxoiRXNuvktee");
            //SavedData.setIdentityId(this, "us-east-1:6b195305-171c-4b83-aa51-e0b1d38de2f2");

            if (!BuildConfig.VERSION_NAME.equals(SavedData.getVersionName(this))) {
                //バージョンアップしたよね
                SavedData.setVersionName(this, BuildConfig.VERSION_NAME);
            }

            String mIdentityId = SavedData.getIdentityId(this);
            if (!mIdentityId.equals("no identityId")) {
                //２回目
                //welcomeAsync(SplashActivity.this, mIdentityId);
                mPresenter.loginUser(Const.getAuthLoginAPI(mIdentityId));
            } else {
                loginFrag = SavedData.getLoginJudge(SplashActivity.this);
                if (loginFrag.equals(TAG_NO_JUDGE)) {
                    //初回
                    handler = new Handler();
                    runnable = new loginRunnable();
                    handler.postDelayed(runnable, 2000);
                } else {
                    //保存あり
                    String url = Const.getAuthConversionAPI(SavedData.getServerName(SplashActivity.this), SavedData.getServerPicture(SplashActivity.this),
                            Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(SplashActivity.this));
                    isConversion = true;
                    getConversionRegister(this, url);
                }
            }
        }
    }

    private void welcomeAsync(final Context context, final String identity_id) {
        // DEV 0 facebook 1 twitter 2
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getAuthLoginAPI(identity_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("message")) {
                        int code = response.getInt("code");
                        String user_id = response.getString("user_id");
                        String username = response.getString("username");
                        String profile_img = response.getString("profile_img");
                        String identity_id = response.getString("identity_id");
                        int badge_num = response.getInt("badge_num");
                        String message = response.getString("message");
                        String token = response.getString("token");

                        if (code == 200) {
                            Application_Gocci.GuestInit(context, identity_id, token, user_id);
                            SavedData.setWelcome(context, username, profile_img, user_id, identity_id, badge_num);
                            //ノーマル
                            SavedData.setFlag(SplashActivity.this, 0);

                            Intent intent = new Intent(SplashActivity.this, GocciTimelineActivity.class);
                            if (!SplashActivity.this.isFinishing()) {
                                SplashActivity.this.startActivity(intent);
                                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                SplashActivity.this.finish();
                            }
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.login_failure), Toast.LENGTH_SHORT).show();
                        handler = new Handler();
                        runnable = new loginRunnable();
                        handler.postDelayed(runnable, 1000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getConversionRegister(final Context context, String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int code = response.getInt("code");
                    String user_id = response.getString("user_id");
                    String username = response.getString("username");
                    String profile_img = response.getString("profile_img");
                    String identity_id = response.getString("identity_id");
                    int badge_num = response.getInt("badge_num");
                    String message = response.getString("message");
                    String token = response.getString("token");

                    if (code == 200) {
                        SavedData.setWelcome(context, username, profile_img, user_id, identity_id, badge_num);

                        Application_Gocci.GuestInit(context, identity_id, token, user_id);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                handler = new Handler();
                runnable = new loginRunnable();
                handler.postDelayed(runnable, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);

        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        BusHolder.get().unregister(self);

        mPresenter.pause();
    }

    @Subscribe
    public void subscribe(final CreateProviderFinishEvent event) {
        if (isConversion) {
            //復帰
            switch (loginFrag) {
                case TAG_SNS_FACEBOOK:
                    if (AccessToken.getCurrentAccessToken() != null) {
                        String token = AccessToken.getCurrentAccessToken().getToken();
                        Application_Gocci.addLogins(this, Const.ENDPOINT_FACEBOOK, token, "none");
                    }
                    break;
                case TAG_SNS_TWITTER:
                    TwitterSession session =
                            Twitter.getSessionManager().getActiveSession();
                    if (session != null) {
                        TwitterAuthToken authToken = session.getAuthToken();
                        Application_Gocci.addLogins(this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, "none");
                    }
                    break;
                case TAG_AUTH:
                    break;
            }

            //ノーマル
            SavedData.setFlag(SplashActivity.this, 0);

            Intent intent = new Intent(SplashActivity.this, GocciTimelineActivity.class);
            if (!SplashActivity.this.isFinishing()) {
                SplashActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                SplashActivity.this.finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showResult(User user) {
        if (user.getCode() == 200) {
            Application_Gocci.GuestInit(this, user.getIdentityId(), user.getToken(), String.valueOf(user.getUserId()));
            SavedData.setWelcome(this, user.getUserName(), user.getProfileImg(), String.valueOf(user.getUserId()), user.getIdentityId(), user.getBadgeNum());
            //ノーマル
            SavedData.setFlag(SplashActivity.this, 0);

            Intent intent = new Intent(SplashActivity.this, GocciTimelineActivity.class);
            if (!SplashActivity.this.isFinishing()) {
                SplashActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                SplashActivity.this.finish();
            }
        } else {
            Toast.makeText(this, user.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showNoResult() {

    }

    @Override
    public void showError() {

    }

    private class loginRunnable implements Runnable {
        @Override
        public void run() {
            Intent mainIntent = new Intent(SplashActivity.this, TutorialGuideActivity.class);
            if (!SplashActivity.this.isFinishing()) {
                SplashActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                SplashActivity.this.finish();
            }
        }
    }
}





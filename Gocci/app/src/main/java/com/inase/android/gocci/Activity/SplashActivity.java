package com.inase.android.gocci.Activity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.CreateProviderFinishEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private loginRunnable runnable;

    private final SplashActivity self = this;

    private String loginFrag;

    private static final String TAG_AUTH = "auth";
    private static final String TAG_SNS_FACEBOOK = "facebook";
    private static final String TAG_SNS_TWITTER = "twitter";
    private static final String TAG_NO_JUDGE = "no judge";

    private boolean isConversion = false;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        if (Util.getConnectedState(SplashActivity.this) != Util.NetworkStatus.OFF) {

            String mIdentityId = SavedData.getIdentityId(this);
            if (!mIdentityId.equals("no identityId")) {
                //２回目
                welcomeAsync(SplashActivity.this, mIdentityId);
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
                        Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                handler = new Handler();
                runnable = new loginRunnable();
                handler.postDelayed(runnable, 1000);
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                handler = new Handler();
                runnable = new loginRunnable();
                handler.postDelayed(runnable, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(self);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.get().unregister(self);
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





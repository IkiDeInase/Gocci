package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.inase.android.gocci.Base.GocciTwitterLoginButton;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CreateAccountView;
import com.inase.android.gocci.View.SigninAccountView;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    private LoginButton facebookLoginButton;
    private GocciTwitterLoginButton twitterLoginButton;
    private RippleView createAccount;
    private RippleView signinAccount;

    private ProgressWheel progress;

    private static final String TAG_FOLLOWEE = "followee_num";
    private static final String TAG_FOLLOWER = "follower_num";
    private static final String TAG_CHEER = "cheer_num";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_BACKGROUND = "background_picture";

    private static final String TAG_AUTH = "auth";
    private static final String TAG_SNS_FACEBOOK = "facebook";
    private static final String TAG_SNS_TWITTER = "twitter";
    private static final String TAG_NO_JUDGE = "no judge";

    private AsyncHttpClient httpClient;
    private RequestParams loginParams;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String SENDER_ID = "913263613395";

    static final String TAG = "GCMDemo";

    private GoogleCloudMessaging gcm;
    private String regid;

    private boolean isNew = false;

    public void onFacebookButtonClicked() {
        facebookLoginButton.performClick();
    }

    public void onTwitterButtonClicked() {
        twitterLoginButton.performClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Log.e("msg", regid);
            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        if (Util.getConnectedState(LoginActivity.this) == Util.NetworkStatus.OFF) {
            Toast.makeText(LoginActivity.this, "通信に失敗しました", Toast.LENGTH_LONG).show();
        }

        createAccount = (RippleView) findViewById(R.id.createAccountRipple);
        signinAccount = (RippleView) findViewById(R.id.signinAccountRipple);
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        twitterLoginButton = (GocciTwitterLoginButton) findViewById(R.id.twitter_login_button);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new LoginClickHandler(), 750);
            }
        });
        signinAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new SigninClickHandler(), 750);
            }
        });

        facebookLoginButton.setReadPermissions("public_profile");
        /*
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject user,
                                    GraphResponse response) {
                                try {
                                    Log.e("レスポンスログ", user.toString());

                                    String mName = user.getString("name");
                                    String mId = user.getString("id");
                                    String mPictureImageUrl = "https://graph.facebook.com/" + mId + "/picture";

                                    postLoginAsync(LoginActivity.this, mName, mPictureImageUrl, TAG_SNS_FACEBOOK);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        */

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                String mName = result.data.getUserName();
                String mPictureImageUrl = "http://www.paper-glasses.com/api/twipi/" + mName;

                postLoginAsync(LoginActivity.this, mName, mPictureImageUrl, TAG_SNS_TWITTER);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(LoginActivity.this, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.getConnectedState(LoginActivity.this) != Util.NetworkStatus.OFF) {

            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                String mName = profile.getName();
                String mId = profile.getId();
                String mPictureImageUrl = "https://graph.facebook.com/" + mId + "/picture";

                postLoginAsync(LoginActivity.this, mName, mPictureImageUrl, TAG_SNS_FACEBOOK);
            }

            TwitterSession twisession =
                    Twitter.getSessionManager().getActiveSession();
            try {
                String mName = twisession.getUserName();
                String mPictureImageUrl = "http://www.paper-glasses.com/api/twipi/" + mName;

                postLoginAsync(LoginActivity.this, mName, mPictureImageUrl, TAG_SNS_TWITTER);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void postLoginAsync(final Context context, final String name, final String url, final String judge) {
        loginParams = new RequestParams();
        loginParams.put("user_name", name);
        loginParams.put("picture", url);
        if (isNew) {
            loginParams.put("token_id", regid);
        }
        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.post(context, Const.URL_SIGNUP_API, loginParams, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("サインアップ成功", String.valueOf(response));

                try {
                    String message = response.getString("message");
                    String code = response.getString("code");

                    if (message.equals("movie api") && code.equals("200")) {
                        String mName = response.getString(TAG_USER_NAME);
                        String mPicture = response.getString(TAG_PICTURE);
                        String mBackground = response.getString(TAG_BACKGROUND);
                        int mFollowee = response.getInt(TAG_FOLLOWEE);
                        int mFollower = response.getInt(TAG_FOLLOWER);
                        int mCheer = response.getInt(TAG_CHEER);

                        SavedData.setAccount(LoginActivity.this, mName, mPicture, mBackground, mFollowee, mFollower, mCheer);

                        Intent intent = new Intent(LoginActivity.this, TutorialGuideActivity.class);
                        intent.putExtra("judge", judge);
                        intent.putExtra("name", name);
                        intent.putExtra("picture", url);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(LoginActivity.this, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    class LoginClickHandler implements Runnable {
        public void run() {
            CreateAccountView fragment
                    = CreateAccountView.newInstance(
                    2,
                    4.0f,
                    true,
                    false,
                    false,
                    isNew,
                    regid
            );
            fragment.show(getSupportFragmentManager(), "blur_sample");
        }
    }

    class SigninClickHandler implements Runnable {
        public void run() {
            SigninAccountView signinAccountView
                    = SigninAccountView.newInstance(
                    2,
                    4.0f,
                    true,
                    false,
                    false,
                    isNew,
                    regid
            );
            signinAccountView.show(getSupportFragmentManager(), "blur_sample");
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

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.e(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.e(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences("pref",
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(LoginActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("メッセージログ", msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        isNew = true;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.e(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }
}

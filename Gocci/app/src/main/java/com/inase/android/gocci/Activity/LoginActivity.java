package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private RippleView usernameRipple;

    private ProgressWheel progress;

    private TextView login_session;

    private final LoginActivity self = this;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String SENDER_ID = "913263613395";

    static final String TAG = "GCMDemo";

    private GoogleCloudMessaging gcm;
    private String regid;

    private Application_Gocci gocci;

    private String username;
    private String profile_img;

    private boolean isOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Log.e("msg", regid);
                SavedData.setRegId(LoginActivity.this, regid);
            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        if (Util.getConnectedState(LoginActivity.this) == Util.NetworkStatus.OFF) {
            Toast.makeText(LoginActivity.this, "通信に失敗しました", Toast.LENGTH_LONG).show();
        }

        usernameRipple = (RippleView) findViewById(R.id.username_Ripple);

        usernameRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new UsernameClickHandler(), 750);
            }
        });

        login_session = (TextView) findViewById(R.id.login_session);
        login_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSessionActivity.startLoginSessionActivity(LoginActivity.this);
            }
        });
    }

    class UsernameClickHandler implements Runnable {
        public void run() {
            //ダイアログ
            setLoginDialog(LoginActivity.this);
        }

        private void setLoginDialog(final Context context) {
            new MaterialDialog.Builder(context)
                    .content("好きなユーザー名を入力しましょう")
                    .input("ユーザー名", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                            materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0);
                        }
                    })
                    .alwaysCallInputCallback()
                    .positiveText("完了")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            super.onPositive(dialog);
                            progress.setVisibility(View.VISIBLE);
                            username = dialog.getInputEditText().getText().toString();

                            String url = Const.getAuthSignupAPI(username, Build.VERSION.RELEASE, Build.MODEL, regid);
                            Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(LoginActivity.this));
                            Const.asyncHttpClient.get(LoginActivity.this, url, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.has("message")) {
                                            isOk = true;
                                            int code = response.getInt("code");
                                            String user_id = response.getString("user_id");
                                            String username = response.getString("username");
                                            String profile_img = response.getString("profile_img");
                                            String identity_id = response.getString("identity_id");
                                            int badge_num = response.getInt("badge_num");
                                            String message = response.getString("message");
                                            String token = response.getString("token");

                                            if (code == 200) {
                                                SavedData.setWelcome(LoginActivity.this, username, profile_img, user_id, identity_id, badge_num);
                                                Application_Gocci.GuestInit(LoginActivity.this, identity_id, token, user_id);
                                                progress.setVisibility(View.INVISIBLE);
                                                SavedData.setFlag(LoginActivity.this, 0);
                                                SavedData.setIdentityId(LoginActivity.this, identity_id);

                                                Intent intent = new Intent(LoginActivity.this, GocciTimelineActivity.class);
                                                LoginActivity.this.startActivity(intent);
                                                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                            } else {
                                                progress.setVisibility(View.INVISIBLE);
                                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            isOk = false;
                                            Toast.makeText(LoginActivity.this, "このユーザー名はすでに登録されています", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                    setLoginDialog(context);
                                    Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .show();
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
        SavedData.setRegId(LoginActivity.this, regid);
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

package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.GocciTwitterLoginButton;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.CreateProviderFinishEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginSessionActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    private LoginButton facebookLoginButton;
    private GocciTwitterLoginButton twitterLoginButton;
    private RippleView twitterRipple;
    private RippleView facebookRipple;

    private TextInputLayout usernameEdit;
    private TextInputLayout passwordEdit;

    private RippleView loginRipple;

    private ProgressWheel progress;

    private final LoginSessionActivity self = this;

    private Application_Gocci gocci;

    private String profile_img;
    private String token;
    private String providerName;

    private static MobileAnalyticsManager analytics;

    public void onFacebookButtonClicked() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        facebookLoginButton.performClick();
    }

    public void onTwitterButtonClicked() {
        twitterLoginButton.performClick();
    }

    public static void startLoginSessionActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, LoginSessionActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

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

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login_session);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gocci = (Application_Gocci) getApplication();

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        twitterRipple = (RippleView) findViewById(R.id.twitter_Ripple);
        facebookRipple = (RippleView) findViewById(R.id.facebook_Ripple);
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        twitterLoginButton = (GocciTwitterLoginButton) findViewById(R.id.twitter_login_button);

        usernameEdit = (TextInputLayout) findViewById(R.id.signinusernameEdit);
        usernameEdit.setErrorEnabled(true);
        passwordEdit = (TextInputLayout) findViewById(R.id.signinpassEdit);
        passwordEdit.setErrorEnabled(true);

        twitterRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTwitterButtonClicked();
            }
        });
        facebookRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFacebookButtonClicked();
            }
        });

        facebookLoginButton.setReadPermissions("public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progress.setVisibility(View.VISIBLE);

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";
                }
                token = AccessToken.getCurrentAccessToken().getToken();
                providerName = Const.ENDPOINT_FACEBOOK;

                Application_Gocci.SNSInit(LoginSessionActivity.this, Const.ENDPOINT_FACEBOOK, token);
                //Application_Gocci.addLogins("graph.facebook.com", loginResult.getAccessToken().getToken());

                //postLoginAsync(LoginActivity.this, mName, mPictureImageUrl, TAG_SNS_FACEBOOK);
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginSessionActivity.this, getString(R.string.cancel_login), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginSessionActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                progress.setVisibility(View.VISIBLE);

                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                String username = session.getUserName();
                profile_img = "http://www.paper-glasses.com/api/twipi/" + username;
                token = authToken.token + ";" + authToken.secret;
                providerName = Const.ENDPOINT_TWITTER;

                Application_Gocci.SNSInit(LoginSessionActivity.this, Const.ENDPOINT_TWITTER, token);
                //Application_Gocci.addLogins("api.twitter.com", authToken.token + ";" + authToken.secret);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(LoginSessionActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        loginRipple = (RippleView) findViewById(R.id.login_Ripple);
        loginRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEdit.setError("");
                passwordEdit.setError("");
                if (usernameEdit.getEditText().getText().toString().isEmpty() || passwordEdit.getEditText().getText().toString().isEmpty()) {
                    usernameEdit.setError(getString(R.string.cheat_input));
                    passwordEdit.setError(getString(R.string.cheat_input));
                } else {
                    passwordAsync(LoginSessionActivity.this, usernameEdit.getEditText().getText().toString(), passwordEdit.getEditText().getText().toString());
                }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        BusHolder.get().unregister(self);
    }

    @Subscribe
    public void subscribe(final CreateProviderFinishEvent event) {
        //DEV
        welcomeAsync(this, event.identityId);
    }

    private void welcomeAsync(final Context context, final String identity_id) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getAuthSNSLoginAPI(identity_id, Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(context)), new JsonHttpResponseHandler() {
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
                            SavedData.setWelcome(context, username, profile_img, user_id, identity_id, badge_num);

                            Intent intent = new Intent(context, GocciTimelineActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                            finish();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Toast.makeText(context, "まだアカウントを作成していません", Toast.LENGTH_SHORT).show();
                        //新しいAPiを叩く
                        snsConversionAsync(context, providerName, token, profile_img, Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(context));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(LoginSessionActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                //progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void passwordAsync(final Context context, String username, String password) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getAuthUsernamePasswordAPI(username, password, Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(context)), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progress.setVisibility(View.VISIBLE);
            }

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
                            SavedData.setFlag(LoginSessionActivity.this, 0);

                            Intent intent = new Intent(context, GocciTimelineActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                            finish();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.error_login), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(LoginSessionActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void snsConversionAsync(final Context context, String providerName, String token, String profile_img, String os, String model, String register_id) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getAuthSNSConversionAPI(providerName, token, profile_img, os, model, register_id), new JsonHttpResponseHandler() {
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
                            SavedData.setWelcome(context, username, profile_img, user_id, identity_id, badge_num);

                            Intent intent = new Intent(context, GocciTimelineActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                            finish();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.error_account_no_exist), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(LoginSessionActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}

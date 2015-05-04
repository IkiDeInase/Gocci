package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.GocciTwitterLoginButton;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CreateAccountView;
import com.inase.android.gocci.View.SigninAccountView;
import com.inase.android.gocci.common.Const;
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

public class LoginActivity extends ActionBarActivity {

    private UiLifecycleHelper uiHelper;

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

    private static final String TAG_AUTH = "auth";
    private static final String TAG_SNS_FACEBOOK = "facebook";
    private static final String TAG_SNS_TWITTER = "twitter";
    private static final String TAG_NO_JUDGE = "no judge";

    private AsyncHttpClient httpClient;
    private RequestParams loginParams;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (Util.getConnectedState(LoginActivity.this) != Util.NetworkStatus.OFF) {
            if (state.isOpened()) {
                fetchUserInfo(session);

            } else if (state.isClosed()) {
                //Toast.makeText(LoginActivity.this, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchUserInfo(final Session session) {
        if (session != null && session.isOpened()) {
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser me, Response response) {
                    if (response.getRequest().getSession() == session) {
                        String mName = me.getName();
                        String mId = me.getId();
                        String mPictureImageUrl = "https://graph.facebook.com/" + mId + "/picture";

                        postLoginAsync(LoginActivity.this, mName, mPictureImageUrl, TAG_SNS_FACEBOOK);
                    }
                }
            });
            request.executeAsync();

        }
    }

    public void onFacebookButtonClicked() {
        facebookLoginButton.performClick();
    }

    public void onTwitterButtonClicked() {
        twitterLoginButton.performClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

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
        uiHelper.onResume();

        if (Util.getConnectedState(LoginActivity.this) != Util.NetworkStatus.OFF) {

            Session session = Session.getActiveSession();
            if (session != null &&
                    (session.isOpened() || session.isClosed())) {
                onSessionStateChange(session, session.getState(), null);
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
        uiHelper.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void postLoginAsync(final Context context, final String name, final String url, final String judge) {
        loginParams = new RequestParams();
        loginParams.put("user_name", name);
        loginParams.put("picture", url);
        httpClient = new AsyncHttpClient();
        httpClient.post(context, Const.URL_SIGNUP_API, loginParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("サインアップ成功", String.valueOf(response));

                try {
                    String message = response.getString("message");
                    String code = response.getString("code");

                    if (message.equals("movie api") && code.equals("200")) {
                        Application_Gocci.mName = response.getString(TAG_USER_NAME);
                        Application_Gocci.mPicture = response.getString(TAG_PICTURE);
                        Application_Gocci.mFollowee = response.getInt(TAG_FOLLOWEE);
                        Application_Gocci.mFollower = response.getInt(TAG_FOLLOWER);
                        Application_Gocci.mCheer = response.getInt(TAG_CHEER);

                        Intent intent = new Intent(LoginActivity.this, TutorialGuideActivity.class);
                        intent.putExtra("judge", judge);
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
                    false
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
                    false
            );
            signinAccountView.show(getSupportFragmentManager(), "blur_sample");
        }
    }
}

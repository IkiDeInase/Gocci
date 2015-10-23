package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.datasource.repository.LoginRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.User;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;
import com.inase.android.gocci.domain.usecase.UserLoginUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.CreateProviderFinishEvent;
import com.inase.android.gocci.presenter.ShowUserLoginPresenter;
import com.inase.android.gocci.ui.view.GocciTwitterLoginButton;
import com.inase.android.gocci.utils.SavedData;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginSessionActivity extends AppCompatActivity implements ShowUserLoginPresenter.ShowUserLogin {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.signin_username_edit)
    TextInputLayout mSigninUsernameEdit;
    @Bind(R.id.signin_pass_edit)
    TextInputLayout mSigninPassEdit;
    @Bind(R.id.login_ripple)
    RippleView mLoginRipple;
    @Bind(R.id.login_button)
    LoginButton mFacebookLoginButton;
    @Bind(R.id.twitter_login_button)
    GocciTwitterLoginButton mTwitterLoginButton;
    @Bind(R.id.twitter_ripple)
    RippleView mTwitterRipple;
    @Bind(R.id.facebook_ripple)
    RippleView mFacebookRipple;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;

    private CallbackManager callbackManager;

    private final LoginSessionActivity self = this;

    private String profile_img;
    private String token;
    private String providerName;

    private static MobileAnalyticsManager analytics;

    private ShowUserLoginPresenter mPresenter;

    public void onFacebookButtonClicked() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        mFacebookLoginButton.performClick();
    }

    public void onTwitterButtonClicked() {
        mTwitterLoginButton.performClick();
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

        LoginRepository loginRepositoryImpl = LoginRepositoryImpl.getRepository();
        UserLoginUseCase userLoginUseCaseImpl = UserLoginUseCaseImpl.getUseCase(loginRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowUserLoginPresenter(userLoginUseCaseImpl);
        mPresenter.setShowUserLoginView(this);

        setContentView(R.layout.activity_login_session);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(getString(R.string.login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSigninUsernameEdit.setErrorEnabled(true);
        mSigninPassEdit.setErrorEnabled(true);

        mTwitterRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTwitterButtonClicked();
            }
        });

        mFacebookRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFacebookButtonClicked();
            }
        });

        mFacebookLoginButton.setReadPermissions("public_profile");
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
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

        mLoginRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSigninUsernameEdit.setError("");
                mSigninPassEdit.setError("");
                if (mSigninUsernameEdit.getEditText().getText().toString().isEmpty() || mSigninPassEdit.getEditText().getText().toString().isEmpty()) {
                    mSigninUsernameEdit.setError(getString(R.string.cheat_input));
                    mSigninPassEdit.setError(getString(R.string.cheat_input));
                } else {
                    mPresenter.loginUser(Const.LOGIN_NAME_PASS, Const.getAuthUsernamePasswordAPI(mSigninUsernameEdit.getEditText().getText().toString(),
                            mSigninPassEdit.getEditText().getText().toString(), Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(LoginSessionActivity.this)));
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
        mPresenter.loginUser(Const.LOGIN_SNS_WELCOME, Const.getAuthSNSLoginAPI(event.identityId, Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(this)));
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
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showLoading() {
        mProgressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressWheel.setVisibility(View.GONE);
    }

    @Override
    public void showResult(int api, User user) {
        switch (api) {
            case Const.LOGIN_SNS_WELCOME:
                break;
            case Const.LOGIN_NAME_PASS:
            case Const.LOGIN_SNS_CONVERSION:
                if (user.getCode() == 200) {
                    Application_Gocci.GuestInit(this, user.getIdentityId(), user.getToken(), String.valueOf(user.getUserId()));
                }
                break;
        }
        if (user.getCode() == 200) {
            SavedData.setWelcome(this, user.getUserName(), user.getProfileImg(), String.valueOf(user.getUserId()), user.getIdentityId(), user.getBadgeNum());

            Intent intent = new Intent(this, TimelineActivity.class);
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();

            overridePendingTransition(0, 0);
            startActivity(intent);
        } else {
            Toast.makeText(this, user.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showNoResult(int api) {
        switch (api) {
            case Const.LOGIN_SNS_WELCOME:
                mPresenter.loginUser(Const.LOGIN_SNS_CONVERSION,
                        Const.getAuthSNSConversionAPI(providerName, token, profile_img, Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(this)));
                break;
            case Const.LOGIN_NAME_PASS:
                Toast.makeText(this, getString(R.string.login_error_pass), Toast.LENGTH_SHORT).show();
                break;
            case Const.LOGIN_SNS_CONVERSION:
                Toast.makeText(this, getString(R.string.error_account_no_exist), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }
}

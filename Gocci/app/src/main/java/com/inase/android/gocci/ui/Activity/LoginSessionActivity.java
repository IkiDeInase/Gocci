package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.inase.android.gocci.event.RetryApiEvent;
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

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

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
        callbackManager = CallbackManager.Factory.create();

        final API3 api3Impl = API3.Impl.getRepository();
        LoginRepository loginRepositoryImpl = LoginRepositoryImpl.getRepository(api3Impl);
        UserLoginUseCase userLoginUseCaseImpl = UserLoginUseCaseImpl.getUseCase(loginRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowUserLoginPresenter(userLoginUseCaseImpl);
        mPresenter.setShowUserLoginView(this);

        setContentView(R.layout.activity_login_session);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(getString(R.string.login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSigninUsernameEdit.setErrorEnabled(true);
        mSigninPassEdit.setErrorEnabled(true);

        mTwitterRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                onTwitterButtonClicked();
            }
        });

        mFacebookRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                onFacebookButtonClicked();
            }
        });

        mFacebookLoginButton.setReadPermissions("public_profile");
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Application_Gocci.SNSInit(LoginSessionActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), new Application_Gocci.SNSAsync.SNSAsyncCallback() {
                    @Override
                    public void preExecute() {
                        mProgressWheel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPostExecute(String identity_id) {
                        API3.Util.AuthLoginLocalCode localCode = api3Impl.AuthLoginParameterRegex(identity_id);
                        if (localCode == null) {
                            SavedData.setIdentityId(LoginSessionActivity.this, identity_id);
                            mPresenter.loginUser(Const.APICategory.AUTH_FACEBOOK_LOGIN, API3.Util.getAuthLoginAPI(identity_id));
                        } else {
                            Toast.makeText(LoginSessionActivity.this, API3.Util.AuthLoginLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();

                Application_Gocci.SNSInit(LoginSessionActivity.this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, new Application_Gocci.SNSAsync.SNSAsyncCallback() {
                    @Override
                    public void preExecute() {
                        mProgressWheel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPostExecute(String identity_id) {
                        API3.Util.AuthLoginLocalCode localCode = api3Impl.AuthLoginParameterRegex(identity_id);
                        if (localCode == null) {
                            SavedData.setIdentityId(LoginSessionActivity.this, identity_id);
                            mPresenter.loginUser(Const.APICategory.AUTH_TWITTER_LOGIN, API3.Util.getAuthLoginAPI(identity_id));
                        } else {
                            Toast.makeText(LoginSessionActivity.this, API3.Util.AuthLoginLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                    API3.Util.AuthPasswordLocalCode localCode = api3Impl.AuthPasswordParameterRegex(mSigninUsernameEdit.getEditText().getText().toString(), mSigninPassEdit.getEditText().getText().toString());
                    if (localCode == null) {
                        mPresenter.loginUser(Const.APICategory.AUTH_PASS_LOGIN, API3.Util.getAuthPasswordAPI(mSigninUsernameEdit.getEditText().getText().toString(), mSigninPassEdit.getEditText().getText().toString()));
                    } else {
                        Toast.makeText(LoginSessionActivity.this, API3.Util.AuthPasswordLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("LoginSession");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(this);
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        BusHolder.get().unregister(this);
        mPresenter.pause();
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

    }

    @Override
    public void hideLoading() {
        mProgressWheel.setVisibility(View.GONE);
    }

    @Override
    public void showResult(Const.APICategory api) {
        API3PostUtil.setDeviceAsync(this, SavedData.getRegId(this), Const.OS, Build.VERSION.RELEASE, Build.MODEL);

        Intent intent = new Intent(this, TimelineActivity.class);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case AUTH_FACEBOOK_LOGIN:
                mFacebookLoginButton.performClick();
                break;
            case AUTH_TWITTER_LOGIN:
                mTwitterLoginButton.performClick();
                break;
            case AUTH_PASS_LOGIN:
                mLoginRipple.performClick();
                break;
            default:
                break;
        }
    }
}

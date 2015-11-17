package com.inase.android.gocci.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.ui.activity.TimelineActivity;
import com.inase.android.gocci.ui.view.GocciTwitterLoginButton;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kinagafuji on 15/08/05.
 */
public class LoginSocialAuthenticationFragment extends Fragment {

    @Bind(R.id.login_button)
    LoginButton mFacebookLoginButton;
    @Bind(R.id.twitter_login_button)
    GocciTwitterLoginButton mTwitterLoginButton;
    @Bind(R.id.set_password_Ripple)
    RippleView mSetPasswordRipple;
    @Bind(R.id.skip_Ripple)
    RippleView mSkipRipple;

    @OnClick(R.id.twitter_ripple)
    public void twitter() {
        if (Application_Gocci.getLoginProvider() != null) {
            new MaterialDialog.Builder(getActivity()).
                    content(getString(R.string.add_sns_message))
                    .positiveText(getString(R.string.add_sns_yeah))
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(getString(R.string.add_sns_no))
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            mTwitterLoginButton.performClick();
                        }
                    }).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.facebook_ripple)
    public void facebook() {
        if (Application_Gocci.getLoginProvider() != null) {
            new MaterialDialog.Builder(getActivity()).
                    content(getString(R.string.add_sns_message))
                    .positiveText(getString(R.string.add_sns_yeah))
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(getString(R.string.add_sns_no))
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                            mFacebookLoginButton.performClick();
                        }
                    }).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
        }
    }

    private CallbackManager callbackManager;

    public static LoginSocialAuthenticationFragment newInstance() {
        LoginSocialAuthenticationFragment pane = new LoginSocialAuthenticationFragment();
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_tutorial5, container, false);
        ButterKnife.bind(this, rootView);

        callbackManager = CallbackManager.Factory.create();

        mFacebookLoginButton.setReadPermissions("public_profile");
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), getString(R.string.preparing_authorize), Toast.LENGTH_SHORT).show();
                Profile profile = Profile.getCurrentProfile();
                String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";

                snsAsync(Const.APICategory.POST_FACEBOOK, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), profile_img);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.cancel_login), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getActivity(), getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getActivity(), getString(R.string.preparing_authorize), Toast.LENGTH_SHORT).show();
                TwitterAuthToken authToken = result.data.getAuthToken();

                String username = result.data.getUserName();
                String profile_img = "http://www.paper-glasses.com/api/twipi/" + username;

                snsAsync(Const.APICategory.POST_TWITTER, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, profile_img);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getActivity(), getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        mSetPasswordRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (Application_Gocci.getLoginProvider() != null) {
                    new MaterialDialog.Builder(getActivity())
                            .content(getString(R.string.password_message))
                            .contentColorRes(R.color.namegrey)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                                    String password = charSequence.toString();
                                    if (!password.isEmpty()) {
                                        com.inase.android.gocci.utils.Util.passwordAsync(getActivity(), password);
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.cheat_input_password), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .widgetColorRes(R.color.gocci_header)
                            .positiveText(getString(R.string.password_yeah))
                            .positiveColorRes(R.color.gocci_header)
                            .show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSkipRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (Application_Gocci.getLoginProvider() != null) {
                    goTimeline();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void goTimeline() {
        Intent intent = new Intent(getActivity(), TimelineActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void snsAsync(final Const.APICategory api, final String providerName, String token, String profile_img) {
        API3.Util.PostSnsLocalCode localCode = API3.Impl.getRepository().post_sns_parameter_regex(providerName, token, profile_img);
        if (localCode == null) {
            Application_Gocci.addLogins(API3.Util.getPostSnsAPI(providerName, token, profile_img), new Application_Gocci.AddLoginAsync.AddLoginAsyncCallback() {
                @Override
                public void preExecute() {

                }

                @Override
                public void onPostExecute() {
                    goTimeline();
                }

                @Override
                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                    Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
                }

                @Override
                public void onLocalError(String errorMessage) {
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), API3.Util.postSnsLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case POST_FACEBOOK:
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";

                    snsAsync(Const.APICategory.POST_FACEBOOK, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), profile_img);
                }
                break;
            case POST_TWITTER:
                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                if (session != null) {
                    TwitterAuthToken authToken = session.getAuthToken();
                    String username = session.getUserName();
                    String profile_img = "http://www.paper-glasses.com/api/twipi/" + username;

                    snsAsync(Const.APICategory.POST_TWITTER, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, profile_img);
                }
                break;
            default:
                break;
        }
    }
}

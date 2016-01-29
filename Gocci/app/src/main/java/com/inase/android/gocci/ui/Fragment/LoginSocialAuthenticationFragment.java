package com.inase.android.gocci.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.ui.activity.TimelineActivity;
import com.inase.android.gocci.ui.activity.TutorialActivity;
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
            TutorialActivity activity = (TutorialActivity) getActivity();
            activity.backSlide();
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
            TutorialActivity activity = (TutorialActivity) getActivity();
            activity.backSlide();
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
                API3PostUtil.setSnsLinkAsync(getActivity(), Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.TUTORIAL, Const.APICategory.SET_FACEBOOK_LINK);
                //Profile profile = Profile.getCurrentProfile();
                //String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";
                //String post_date = SavedData.getServerUserId(getActivity()) + "_" + Util.getDateTimeString();
                //API3PostUtil.setProfileImgAsync(getActivity(), post_date, profile_img, Const.ActivityCategory.TUTORIAL);
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
                TwitterAuthToken authToken = result.data.getAuthToken();
                API3PostUtil.setSnsLinkAsync(getActivity(), Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.TUTORIAL, Const.APICategory.SET_TWITTER_LINK);
                //String username = result.data.getUserName();
                //String profile_img = "http://www.paper-glasses.com/api/twipi/" + username;
                //String post_date = SavedData.getServerUserId(getActivity()) + "_" + Util.getDateTimeString();
                //API3PostUtil.setProfileImgAsync(getActivity(), post_date, profile_img, Const.ActivityCategory.TUTORIAL);
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
                                        API3PostUtil.setPasswordAsync(getActivity(), password, Const.ActivityCategory.TUTORIAL, Const.APICategory.SET_PASSWORD);
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
                    TutorialActivity activity = (TutorialActivity) getActivity();
                    activity.backSlide();
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
                    TutorialActivity activity = (TutorialActivity) getActivity();
                    activity.backSlide();
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
        BusHolder.get().unregister(this);
        super.onPause();
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

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.TUTORIAL) {
            switch (event.apiCategory) {
                case SET_FACEBOOK_LINK:
                case SET_TWITTER_LINK:
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Application_Gocci.getLoginProvider().refresh();
                            return null;
                        }
                    }.execute();
                    goTimeline();
                    break;
                case SET_PASSWORD:
                    goTimeline();
                    break;
            }
        }
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case SET_FACEBOOK_LINK:
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    API3PostUtil.setSnsLinkAsync(getActivity(), Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.TUTORIAL, Const.APICategory.SET_FACEBOOK_LINK);
                }
                break;
            case SET_TWITTER_LINK:
                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                if (session != null) {
                    TwitterAuthToken authToken = session.getAuthToken();
                    API3PostUtil.setSnsLinkAsync(getActivity(), Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.TUTORIAL, Const.APICategory.SET_TWITTER_LINK);
                }
                break;
            default:
                break;
        }
    }
}

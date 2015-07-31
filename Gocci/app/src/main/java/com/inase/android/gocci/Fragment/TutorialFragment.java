package com.inase.android.gocci.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.inase.android.gocci.Activity.GocciTimelineActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.GocciTwitterLoginButton;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by kinagafuji on 15/07/31.
 */
public class TutorialFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";

    private CallbackManager callbackManager;

    public static TutorialFragment newInstance(int layoutId) {
        TutorialFragment pane = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getArguments().getInt(LAYOUT_ID, -1);
        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);

        if (layoutId == R.layout.view_tutorial5) {
            callbackManager = CallbackManager.Factory.create();
            final LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
            final GocciTwitterLoginButton twitter_loginButton = (GocciTwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
            RippleView twitter_ripple = (RippleView) rootView.findViewById(R.id.twitter_Ripple);
            RippleView facebook_ripple = (RippleView) rootView.findViewById(R.id.facebook_Ripple);
            RippleView skip_ripple = (RippleView) rootView.findViewById(R.id.skip_Ripple);

            loginButton.setReadPermissions("public_profile");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";
                        //API叩く
                    }
                    Application_Gocci.addLogins(Const.ENDPOINT_FACEBOOK, loginResult.getAccessToken().getToken());

                    Intent intent = new Intent(getActivity(), GocciTimelineActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    getActivity().finish();
                }

                @Override
                public void onCancel() {
                    Log.e("ログ", "キャンセル");
                }

                @Override
                public void onError(FacebookException e) {
                    Toast.makeText(getActivity(), "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                }
            });

            twitter_loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    TwitterSession session =
                            Twitter.getSessionManager().getActiveSession();
                    TwitterAuthToken authToken = session.getAuthToken();

                    String username = session.getUserName();
                    String profile_img = "http://www.paper-glasses.com/api/twipi/" + username;
                    Application_Gocci.addLogins("api.twitter.com", authToken.token + ";" + authToken.secret);

                    Intent intent = new Intent(getActivity(), GocciTimelineActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    getActivity().finish();
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    Toast.makeText(getActivity(), "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                }
            });

            twitter_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.credentialsProvider != null) {
                        twitter_loginButton.performClick();
                    } else {
                        Toast.makeText(getActivity(), "もう少し待ってから押してみよう", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            facebook_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.credentialsProvider != null) {
                        loginButton.performClick();
                    } else {
                        Toast.makeText(getActivity(), "もう少し待ってから押してみよう", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            skip_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Application_Gocci.credentialsProvider != null) {
                        Intent intent = new Intent(getActivity(), GocciTimelineActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "もう少し待ってから押してみよう", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        return rootView;
    }
}

package com.inase.android.gocci.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.inase.android.gocci.Activity.GocciTimelineActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.GocciTwitterLoginButton;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.SNSMatchFinishEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.squareup.otto.Subscribe;
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

    private GocciTwitterLoginButton twitter_loginButton;
    private LoginButton loginButton;

    private boolean isFirst = true;

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
            loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
            twitter_loginButton = (GocciTwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
            RippleView twitter_ripple = (RippleView) rootView.findViewById(R.id.twitter_Ripple);
            RippleView facebook_ripple = (RippleView) rootView.findViewById(R.id.facebook_Ripple);
            RippleView skip_ripple = (RippleView) rootView.findViewById(R.id.skip_Ripple);

            loginButton.setReadPermissions("public_profile");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Profile profile = Profile.getCurrentProfile();
                    String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";
                    Application_Gocci.addLogins(getActivity(), Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), profile_img);

                    /*
                    Intent intent = new Intent(getActivity(), GocciTimelineActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    getActivity().finish();
                    */
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
                    Application_Gocci.addLogins(getActivity(), "api.twitter.com", authToken.token + ";" + authToken.secret, profile_img);

                    /*
                    Intent intent = new Intent(getActivity(), GocciTimelineActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    getActivity().finish();
                    */
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
                        goTimeline(500);
                    } else {
                        Toast.makeText(getActivity(), "もう少し待ってから押してみよう", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
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

    @Subscribe
    public void subscribe(SNSMatchFinishEvent event) {
        if (isFirst) {
            isFirst = false;
            SavedData.setServerPicture(getActivity(), event.profile_img);
            Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
            goTimeline(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitter_loginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void goTimeline(int time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), GocciTimelineActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                getActivity().finish();
            }
        }, time);
    }
}

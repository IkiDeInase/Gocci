package com.inase.android.gocci.View;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.inase.android.gocci.Activity.LoginActivity;
import com.inase.android.gocci.Activity.TutorialGuideActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class SigninAccountView extends SupportBlurDialogFragment implements View.OnClickListener {

    private EditText usernameEdit;
    private EditText passEdit;

    private RippleView facebookSigninButton;
    private RippleView twitterSigninButton;
    private RippleView accountSigninButton;

    private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";
    private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";
    private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";
    private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";
    private static final String BUNDLE_KEY_BLURRED_ACTION_BAR = "bundle_key_blurred_action_bar";

    private static final String TAG_FOLLOWEE = "followee_num";
    private static final String TAG_FOLLOWER = "follower_num";
    private static final String TAG_CHEER = "cheer_num";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_BACKGROUND = "background_picture";

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;
    private boolean mBlurredActionBar;

    private AsyncHttpClient httpClient;

    private Application_Gocci gocci;

    public static SigninAccountView newInstance(int radius,
                                                float downScaleFactor,
                                                boolean dimming,
                                                boolean debug,
                                                boolean mBlurredActionBar
    ) {
        SigninAccountView fragment = new SigninAccountView();
        Bundle args = new Bundle();
        args.putInt(
                BUNDLE_KEY_BLUR_RADIUS,
                radius
        );
        args.putFloat(
                BUNDLE_KEY_DOWN_SCALE_FACTOR,
                downScaleFactor
        );
        args.putBoolean(
                BUNDLE_KEY_DIMMING,
                dimming
        );
        args.putBoolean(
                BUNDLE_KEY_DEBUG,
                debug
        );
        args.putBoolean(
                BUNDLE_KEY_BLURRED_ACTION_BAR,
                mBlurredActionBar
        );

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle args = getArguments();
        mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        mBlurredActionBar = args.getBoolean(BUNDLE_KEY_BLURRED_ACTION_BAR);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_signin_account, null);
        usernameEdit = (EditText) view.findViewById(R.id.signinusernameEdit);
        passEdit = (EditText) view.findViewById(R.id.signinpassEdit);

        gocci = (Application_Gocci)getActivity().getApplication();

        accountSigninButton = (RippleView) view.findViewById(R.id.signin_login_Ripple);
        facebookSigninButton = (RippleView) view.findViewById(R.id.signin_facebook_Ripple);
        twitterSigninButton = (RippleView) view.findViewById(R.id.signin_twitter_Ripple);

        facebookSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new FacebookClickHandler(), 750);
            }
        });

        twitterSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new TwitterClickHandler(), 750);
            }
        });

        accountSigninButton.setOnClickListener(this);
        //facebookLoginButton.setOnClickListener(this);
        //twitterLoginButton.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return mBlurredActionBar;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }

    private void postSigninAsync(final Context context, RequestParams params) {
        httpClient = new AsyncHttpClient();
        httpClient.post(context, Const.URL_AUTH_API, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                Log.e("JSON出た", timeline.toString());

                try {
                    String message = timeline.getString("message");

                    if (message.equals("movie api")) {
                        String mName = timeline.getString(TAG_USER_NAME);
                        String mPicture = timeline.getString(TAG_PICTURE);
                        String mBackground = timeline.getString(TAG_BACKGROUND);
                        int mFollowee = timeline.getInt(TAG_FOLLOWEE);
                        int mFollower = timeline.getInt(TAG_FOLLOWER);
                        int mCheer = timeline.getInt(TAG_CHEER);

                        gocci.setAccount(mName, mPicture, mBackground, mFollowee, mFollower, mCheer);

                        Toast.makeText(getActivity(), "ログインに成功しました", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), TutorialGuideActivity.class);
                        intent.putExtra("judge", "auth");
                        intent.putExtra("name", mName);
                        intent.putExtra("picture", mPicture);
                        startActivity(intent);
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "失敗です", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        String username = usernameEdit.getText().toString();
        String password = passEdit.getText().toString();

        if (username.equals("") || password.equals("")) {

            Toast.makeText(getActivity(), "入力が不正なようです", Toast.LENGTH_SHORT).show();

        } else {
            if (password.length() > 6) {
                RequestParams accountParam = new RequestParams();
                accountParam.put("user_name", username);
                accountParam.put("password", password);

                postSigninAsync(getActivity(), accountParam);
            } else {
                Toast.makeText(getActivity(), "パスワードは６文字以上です", Toast.LENGTH_SHORT).show();
            }
        }

    }

    class FacebookClickHandler implements Runnable {
        public void run() {
            ((LoginActivity) getActivity()).onFacebookButtonClicked();
            dismiss();
        }
    }

    class TwitterClickHandler implements Runnable {
        public void run() {
            ((LoginActivity) getActivity()).onTwitterButtonClicked();
            dismiss();
        }
    }
}

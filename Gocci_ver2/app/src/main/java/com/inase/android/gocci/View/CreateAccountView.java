package com.inase.android.gocci.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.inase.android.gocci.Activity.LoginActivity;
import com.inase.android.gocci.Activity.TutorialGuideActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

public class CreateAccountView extends SupportBlurDialogFragment implements View.OnClickListener {

    private EditText usernameEdit;
    private EditText emailEdit;
    private EditText passEdit;

    private RippleView facebookLoginButton;
    private RippleView twitterLoginButton;
    private RippleView accountLoginButton;

    private CheckBox checkPolicy;

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

    private static final String emailPattern = "^[a-zA-Z0-9\\._\\-\\+]+@[a-zA-Z0-9_\\-]+\\.[a-zA-Z\\.]+[a-zA-Z]$";

    public static boolean isEmail(String input) {
        Pattern p = Pattern.compile(emailPattern);
        Matcher m = p.matcher(input);
        p = null;
        input = null;
        return m.matches();
    }

    public static CreateAccountView newInstance(int radius,
                                                float downScaleFactor,
                                                boolean dimming,
                                                boolean debug,
                                                boolean mBlurredActionBar
    ) {
        CreateAccountView fragment = new CreateAccountView();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_create_account, null);
        usernameEdit = (EditText) view.findViewById(R.id.usernameEdit);
        emailEdit = (EditText) view.findViewById(R.id.emailEdit);
        passEdit = (EditText) view.findViewById(R.id.passEdit);

        checkPolicy = (CheckBox) view.findViewById(R.id.checkPolicy);

        accountLoginButton = (RippleView) view.findViewById(R.id.account_login_Ripple);
        facebookLoginButton = (RippleView) view.findViewById(R.id.account_facebook_Ripple);
        twitterLoginButton = (RippleView) view.findViewById(R.id.account_twitter_Ripple);

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPolicy.isChecked()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new FacebookClickHandler(), 750);
                } else {
                    Toast.makeText(getActivity(), "利用規約を確認してください", Toast.LENGTH_SHORT).show();
                }

            }
        });

        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPolicy.isChecked()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new TwitterClickHandler(), 750);
                } else {
                    Toast.makeText(getActivity(), "利用規約を確認してください", Toast.LENGTH_SHORT).show();
                }
            }
        });

        accountLoginButton.setOnClickListener(this);

        checkPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://inase-inc.jp/rules/");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
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

    private void postAccountAsync(final Context context, RequestParams params) {
        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.post(context, Const.URL_REGISTER_API, params, new JsonHttpResponseHandler() {

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

                        SavedData.setAccount(getActivity(), mName, mPicture, mBackground, mFollowee, mFollower, mCheer);

                        Toast.makeText(getActivity(), "アカウントを作成しました！", Toast.LENGTH_SHORT).show();

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
        String email = emailEdit.getText().toString();
        String password = passEdit.getText().toString();

        if (username.equals("") || email.equals("") || password.equals("")) {

            Toast.makeText(getActivity(), "入力が不正なようです", Toast.LENGTH_SHORT).show();

        } else {
            if (isEmail(email)) {
                if (password.length() > 6) {
                    if (checkPolicy.isChecked()) {
                        RequestParams accountParam = new RequestParams();
                        accountParam.put("user_name", username);
                        accountParam.put("email", email);
                        accountParam.put("password", password);

                        postAccountAsync(getActivity(), accountParam);
                    } else {
                        Toast.makeText(getActivity(), "利用規約を確認してください", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "パスワードは６文字以上入力してください", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "メールアドレスが不正です", Toast.LENGTH_SHORT).show();
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

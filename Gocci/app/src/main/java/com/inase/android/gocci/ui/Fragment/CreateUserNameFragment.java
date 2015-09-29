package com.inase.android.gocci.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.inase.android.gocci.ui.activity.TutorialGuideActivity;
import com.inase.android.gocci.ui.activity.WebViewActivity;
import com.inase.android.gocci.application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kinagafuji on 15/08/05.
 */
public class CreateUserNameFragment extends Fragment implements FABProgressListener {

    @Bind(R.id.username_textInput)
    TextInputLayout mUsernameTextInput;
    @Bind(R.id.created_username)
    TextView mCreatedUsername;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.fab_progress_circle)
    FABProgressCircle mFabProgressCircle;

    @OnClick(R.id.fab)
    public void fab() {
        if (mUsernameTextInput.getEditText().getText().length() != 0) {
            mFabProgressCircle.show();
            mFab.setClickable(false);
            setLogin(getActivity());
        } else {
            Toast.makeText(getActivity(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rule)
    public void rule() {
        WebViewActivity.startWebViewActivity(0, getActivity());
    }

    @OnClick(R.id.policy)
    public void policy() {
        WebViewActivity.startWebViewActivity(1, getActivity());
    }

    public static CreateUserNameFragment newInstance() {
        CreateUserNameFragment pane = new CreateUserNameFragment();
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_tutorial4, container, false);
        ButterKnife.bind(this, rootView);

        mUsernameTextInput.setErrorEnabled(true);
        mUsernameTextInput.getEditText().setHintTextColor(getResources().getColor(R.color.namegrey));
        mUsernameTextInput.getEditText().setHighlightColor(getResources().getColor(R.color.namegrey));
        mUsernameTextInput.getEditText().setTextColor(getResources().getColor(R.color.namegrey));

        mCreatedUsername.setAlpha(0);

        mFabProgressCircle.attachListener(this);

        return rootView;
    }

    @Override
    public void onFABProgressAnimationEnd() {
        mCreatedUsername.setText(SavedData.getServerName(getActivity()) + "さん");
        mFabProgressCircle.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFabProgressCircle.setVisibility(View.INVISIBLE);
            }
        }).setStartDelay(200);
        mUsernameTextInput.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUsernameTextInput.setVisibility(View.INVISIBLE);
                startVisibleUsername();
            }
        }).setStartDelay(200);
    }

    private void startVisibleUsername() {
        mCreatedUsername.animate().alphaBy(100).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                TutorialGuideActivity activity = (TutorialGuideActivity) getActivity();
                activity.mPager.setCurrentItem(4, true);
            }
        }).setStartDelay(200);
    }

    private void setLogin(final Context context) {
        mUsernameTextInput.setError("");
        String username = mUsernameTextInput.getEditText().getText().toString();
        String url = Const.getAuthSignupAPI(username, Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(context));
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {

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
                            Application_Gocci.GuestInit(context, identity_id, token, user_id);
                            SavedData.setFlag(context, 0);
                            mFabProgressCircle.beginFinalAnimation();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mUsernameTextInput.setError(getString(R.string.multiple_username));
                        mFabProgressCircle.hide();
                        mFab.setClickable(true);
                        //setLoginDialog(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                mFab.setClickable(true);
                mFabProgressCircle.hide();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

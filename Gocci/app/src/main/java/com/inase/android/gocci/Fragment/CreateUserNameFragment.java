package com.inase.android.gocci.Fragment;

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
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kinagafuji on 15/08/05.
 */
public class CreateUserNameFragment extends Fragment implements FABProgressListener {

    private FABProgressCircle circle;
    private TextView createdUsername;
    private TextInputLayout inputText;
    private FloatingActionButton fab;

    public static CreateUserNameFragment newInstance() {
        CreateUserNameFragment pane = new CreateUserNameFragment();
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_tutorial4, container, false);

        inputText = (TextInputLayout) rootView.findViewById(R.id.usernameTextInput);
        inputText.setErrorEnabled(true);
        inputText.getEditText().setHintTextColor(getResources().getColor(R.color.namegrey));
        inputText.getEditText().setHighlightColor(getResources().getColor(R.color.namegrey));
        inputText.getEditText().setTextColor(getResources().getColor(R.color.namegrey));

        createdUsername = (TextView) rootView.findViewById(R.id.createdUsername);
        createdUsername.setAlpha(0);

        circle = (FABProgressCircle) rootView.findViewById(R.id.fabProgressCircle);
        circle.attachListener(this);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputText.getEditText().getText().length() != 0) {
                    circle.show();
                    fab.setClickable(false);
                    setLogin(getActivity());
                } else {
                    Toast.makeText(getActivity(), "希望のユーザー名を入力してください", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onFABProgressAnimationEnd() {
        createdUsername.setText(SavedData.getServerName(getActivity()) + "さん");
        circle.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                circle.setVisibility(View.INVISIBLE);
            }
        }).setStartDelay(200);
        inputText.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inputText.setVisibility(View.INVISIBLE);
                startVisibleUsername();
            }
        }).setStartDelay(200);
    }

    private void startVisibleUsername() {
        createdUsername.animate().alphaBy(100).setDuration(500).setStartDelay(200);
    }

    private void setLogin(final Context context) {
        inputText.setError("");
        String username = inputText.getEditText().getText().toString();
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
                            circle.beginFinalAnimation();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        inputText.setError("このユーザー名はすでに登録されています");
                        circle.hide();
                        fab.setClickable(true);
                        //setLoginDialog(context);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                fab.setClickable(true);
                circle.hide();
            }
        });
    }
}

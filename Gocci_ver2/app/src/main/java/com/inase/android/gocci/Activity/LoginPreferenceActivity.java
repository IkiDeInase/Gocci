package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPreferenceActivity extends ActionBarActivity {

    private ProgressWheel progress;

    private static final String TAG_FOLLOWEE = "followee_num";
    private static final String TAG_FOLLOWER = "follower_num";
    private static final String TAG_CHEER = "cheer_num";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_BACKGROUND = "background_picture";

    private static final String TAG_AUTH = "auth";
    private static final String TAG_SNS = "SNS";
    private static final String TAG_NO_JUDGE = "no judge";

    private AsyncHttpClient httpClient;
    private RequestParams loginParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_preference);

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        if (Util.getConnectedState(LoginPreferenceActivity.this) == Util.NetworkStatus.OFF) {
            Toast.makeText(LoginPreferenceActivity.this, "通信に失敗しました", Toast.LENGTH_LONG).show();
        }

        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String name = pref.getString("name", "dummy");
        String picture = pref.getString("picture", "http://api-gocci.jp/img/s_1.png");
        String judge = pref.getString("judge", TAG_NO_JUDGE);

        if (!name.equals("dummy")) {
            Log.e("ダミーじゃ無いよ", name);
            postLoginAsync(this, name, picture, judge);
        } else {
            Log.e("ダミだったよ", name);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void postLoginAsync(final Context context, final String name, final String url, final String judge) {
        loginParams = new RequestParams();
        loginParams.put("user_name", name);
        loginParams.put("picture", url);
        httpClient = new AsyncHttpClient();
        httpClient.post(context, Const.URL_SIGNUP_API, loginParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("サインアップ成功", String.valueOf(response));

                try {
                    String message = response.getString("message");

                    if (message.equals("movie api")) {
                        String mName = response.getString(TAG_USER_NAME);
                        String mPicture = response.getString(TAG_PICTURE);
                        String mBackground = response.getString(TAG_BACKGROUND);
                        int mFollowee = response.getInt(TAG_FOLLOWEE);
                        int mFollower = response.getInt(TAG_FOLLOWER);
                        int mCheer = response.getInt(TAG_CHEER);

                        SavedData.setAccount(LoginPreferenceActivity.this, mName, mPicture, mBackground, mFollowee, mFollower, mCheer);

                        Intent intent = new Intent(LoginPreferenceActivity.this, TutorialGuideActivity.class);
                        intent.putExtra("judge", judge);
                        intent.putExtra("name", name);
                        intent.putExtra("picture", url);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginPreferenceActivity.this, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginPreferenceActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(LoginPreferenceActivity.this, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginPreferenceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }

}

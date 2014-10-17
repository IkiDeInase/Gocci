package com.example.kinagafuji.gocci;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.PagerTabStripActivity;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.data.TwitterAsyncTask;
import com.example.kinagafuji.gocci.data.TwitterResult;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.googlecode.javacv.cpp.opencv_stitching;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;


public class LoginActivity extends Activity {

    // ログインボタン
    private Button mFbloginBtn;
    // ライフサイクルヘルパー
    private UiLifecycleHelper mUiHelper;
    // セッションステートコールバックメソッド
    private Session.StatusCallback mFacebookCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private static final String CONSUMER_KEY = "co9pGQdqavnWr1lgzBwfvIG6W";
    private static final String CONSUMER_SECRET = "lgNOyQTEA4AXrxlDsP0diEkmChm5ji2B4QoXwsldpHzI0mfJTg";
    private static final String CALLBACK = "myapp://test";
    private OAuthAuthorization mOauth;

    private static final String TAG_NAME = "name";
    private static final String TAG_ID = "id";

    public String name;
    public String id;

    private String Dataurl = "https://codelecture.com/gocci/signup.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        initFacebook(savedInstanceState);

        //Oauth認証用のUri作成
        ConfigurationBuilder conf = new ConfigurationBuilder()
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET);
        mOauth = new OAuthAuthorization(conf.build());
        mOauth.setOAuthAccessToken(null);

        Button buttonauth2 = (Button) findViewById(R.id.buttonAuth2);
        buttonauth2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TwitterAsyncTask<Object, Void, String>(new TwitterAsyncTask.TwitterPreExecute() {
                    @Override
                    public void run() {
                    }
                }, new TwitterAsyncTask.TwitterAction<TwitterResult<String>, Object>() {
                    @Override
                    public TwitterResult<String> run(Object param) {
                        TwitterResult<String> r = new TwitterResult<String>();

                        try {
                            r.setResult(mOauth.getOAuthRequestToken(CALLBACK).getAuthorizationURL());
                        } catch (TwitterException e) {
                            r.setError(e);
                        }

                        return r;
                    }
                }, new TwitterAsyncTask.TwitterCallback<String>() {
                    @Override
                    public void run(String result) {
                        //Oauth認証開始
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        startActivity(intent);
                    }
                }, new TwitterAsyncTask.TwitterOnError() {
                    @Override
                    public void run(TwitterException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Twitterとの接続に失敗しました", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                        .execute(new Object());

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(CALLBACK)) {

            new TwitterAsyncTask<Uri, Void, AccessToken>(new TwitterAsyncTask.TwitterPreExecute() {
                @Override
                public void run() {
                }
            }, new TwitterAsyncTask.TwitterAction<TwitterResult<AccessToken>, Uri>() {
                @Override
                public TwitterResult<AccessToken> run(Uri param) {
                    TwitterResult<AccessToken> r = new TwitterResult<AccessToken>();
                    //AccessTokenの取得
                    String verifier = param.getQueryParameter("oauth_verifier");
                    try {
                        r.setResult(mOauth.getOAuthAccessToken(verifier));
                    } catch (TwitterException e) {
                        r.setError(e);
                    }

                    return r;
                }
            }, new TwitterAsyncTask.TwitterCallback<AccessToken>() {
                @Override
                public void run(AccessToken result) {
                    // TODO Oauth認証に成功した後の処理
                    Intent intent = new Intent(LoginActivity.this, PagerTabStripActivity.class);
                    startActivity(intent);
                }
            }, new TwitterAsyncTask.TwitterOnError() {
                @Override
                public void run(TwitterException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Oauth認証に失敗しました", Toast.LENGTH_LONG).show();
                    finish();
                }
            }).execute(uri);
        }
    }


    private void initFacebook(Bundle savedInstanceState) {
        // ライフサイクルヘルパーの初期化
        mUiHelper = new UiLifecycleHelper(this, mFacebookCallback);
        mUiHelper.onCreate(savedInstanceState);
        // ログインボタンの初期化
        mFbloginBtn = (Button) findViewById(R.id.buttonAuth);
        mFbloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFacebook();


                //データ解析してIntentで送る。ifでログイン出来たら遷移する。
                Intent intent = new Intent(LoginActivity.this, PagerTabStripActivity.class);
                startActivity(intent);
            }
        });

    }




    private void loginFacebook() {
        // リクエストの生成
        Session.OpenRequest openRequest = new Session.OpenRequest(this).setCallback(mFacebookCallback);
        // emailを要求するパーミッションを設定
        openRequest.setPermissions(Arrays.asList("email"));
        // セッションを生成
        Session session = new Session.Builder(this).build();
        // アクティブセッションとする。
        Session.setActiveSession(session);
        // 認証を要求する。
        session.openForRead(openRequest);

    }

    private void logoutFacebook() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            // セッションとトークン情報を廃棄する。
            session.closeAndClearTokenInformation();
        }
    }

    public class SignupTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost method = new HttpPost(Dataurl);

            String pictureUrl = "https://graph.facebook.com/" + id + "/picture";

            ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
            contents.add(new BasicNameValuePair("user_nama", name));
            contents.add(new BasicNameValuePair("picture", pictureUrl));
            Log.d("読み取り",name + pictureUrl);

            String body = null;
            try {
                method.setEntity(new UrlEncodedFormEntity(contents, "utf-8"));
                HttpResponse res = client.execute(method);
                Log.d("TAGだよ", "反応");
                HttpEntity entity = res.getEntity();
                body = EntityUtils.toString(entity, "UTF-8");
                Log.d("bodyの中身だよ", body);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client.getConnectionManager().shutdown();

            return null;
        }
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {
            // GraphAPIのmeリクエストを呼び出す。
            Request.newMeRequest(session, new FacebookGraphUserCallback("wait...") {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    super.onCompleted(user, response);

                    Log.d("TAG", "user = " + user.getInnerJSONObject());
                    try {

                        name = user.getInnerJSONObject().getString(TAG_NAME);
                        id = user.getInnerJSONObject().getString(TAG_ID);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error", String.valueOf(e));
                    }

                    SignupTask task = new SignupTask();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



                }
            }).executeAsync();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUiHelper.onSaveInstanceState(outState);
    }

    public class FacebookGraphUserCallback implements Request.GraphUserCallback {

        private ProgressDialog mProgress = null;

        public FacebookGraphUserCallback(String message) {
            mProgress = new ProgressDialog(LoginActivity.this);
            mProgress.setMessage(message);
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.show();

        }

        @Override
        public void onCompleted(GraphUser user, Response response) {
            mProgress.dismiss();
        }
    }
}




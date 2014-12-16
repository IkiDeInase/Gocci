package com.example.kinagafuji.gocci.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.example.kinagafuji.gocci.Activity.SlidingTabActivity;
import com.example.kinagafuji.gocci.Application_Gocci;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.PopupHelper;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginFragment extends BaseFragment {

    private Application_Gocci application_gocci;

    private static final String TAG = "LoginFragment";

    private static final String sDataurl = "http://api-gocci.jp/login/";
    private static final String TAG_NAME = "name";
    private static final String TAG_ID = "id";
    private CustomProgressDialog mloginDialog;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper uiHelper;
    private String mName;
    private String mId;
    private String mPictureImageUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application_gocci = (Application_Gocci) getActivity().getApplication();

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        final View view = inflater.inflate(R.layout.fragment_login, container, false);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        // ディスプレイのインスタンス生成
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        final int width = size.x;
        final int height = size.y;

        Log.d("幅", String.valueOf(width + "と" + height));

        ImageButton account = (ImageButton) view.findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //win1.setAttributes(params1);	//ウインドウにパラメータを渡す
                final PopupWindow window = PopupHelper.newBasicPopupWindow(getActivity(), width, height);

                View inflateView = inflater.inflate(R.layout.fragment_account, container, false);

                ImageButton closebutton = (ImageButton) inflateView.findViewById(R.id.closeButton);
                closebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });

                LoginButton mfacebookButtonm = (LoginButton) inflateView.findViewById(R.id.authButton);
                mfacebookButtonm.setFragment(LoginFragment.this);
                mfacebookButtonm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginFacebook();
                    }
                });

                ImageButton accountButton = (ImageButton) inflateView.findViewById(R.id.accountButton);
                accountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SlidingTabActivity.class);
                        startActivity(intent);
                    }
                });

                window.setContentView(inflateView);
                //int totalHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                PopupHelper.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);
            }
        });


        /*
        ImageButton signin = (ImageButton) view.findViewById(R.id.signin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupWindow window = PopupHelper.newBasicPopupWindow(getActivity(), width, height);

                View inflateView = inflater.inflate(R.layout.fragment_signup, container, false);

                ImageButton closebutton = (ImageButton) inflateView.findViewById(R.id.closeButton);
                closebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                    }
                });

                ImageButton mfacebookButtonm = (ImageButton) inflateView.findViewById(R.id.facebookButton);
                mfacebookButtonm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uiHelper = new UiLifecycleHelper(getActivity(), callback);
                        //PopupHelperloginFacebook();
                    }
                });

                window.setContentView(inflateView);
                //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                view.getLocationOnScreen(location);

                PopupHelper.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);
            }
        });
        */

        return view;
    }


    private void loginFacebook() {

        // リクエストの生成
        Session.OpenRequest openRequest = new Session.OpenRequest(this)
                .setCallback(callback);
        // emailを要求するパーミッションを設定
        openRequest.setPermissions(Arrays.asList("public_profile"));
        // セッションを生成
        // Session session = new Builder(this).build();
        Session session = Session.getActiveSession();

        // アクティブセッションとする。
        Session.setActiveSession(session);
        // 認証を要求する。
        session.openForRead(openRequest);

    }



    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Request.newMeRequest(session, new FacebookGraphUserCallback("..Wait") {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    super.onCompleted(user, response);

                    Log.d("TAG", "user = " + user.getInnerJSONObject());
                    try {

                        //必要なJSONデータを出力
                        mName = user.getInnerJSONObject().getString(TAG_NAME);
                        mId = user.getInnerJSONObject().getString(TAG_ID);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error", String.valueOf(e));
                    }

                    mPictureImageUrl = "https://graph.facebook.com/" + mId + "/picture";


                    new SignupTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sDataurl);


                }
            }).executeAsync();

            Intent intent = new Intent(getActivity(), SlidingTabActivity.class);
            startActivity(intent);


        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);

    }


    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    public class SignupTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String param = params[0];

            HttpClient client = new DefaultHttpClient();

            HttpPost method = new HttpPost(param);

            ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
            contents.add(new BasicNameValuePair("user_name", mName));
            contents.add(new BasicNameValuePair("picture", mPictureImageUrl));
            Log.d("読み取り", mName + "と" + mPictureImageUrl);

            try {
                method.setEntity(new UrlEncodedFormEntity(contents, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse res = client.execute(method);
            } catch (IOException e) {
                e.printStackTrace();
            }


            SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString("name", mName);
            editor.putString("pictureImageUrl", mPictureImageUrl);


            editor.apply();

            return null;
        }

    }


    public class FacebookGraphUserCallback implements Request.GraphUserCallback {

        public FacebookGraphUserCallback(String message) {
            mloginDialog = new CustomProgressDialog(getActivity());
            mloginDialog.setCancelable(false);
            mloginDialog.show();
        }

        @Override
        public void onCompleted(GraphUser user, Response response) {
            mloginDialog.dismiss();
        }
    }


}

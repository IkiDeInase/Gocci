package com.inase.android.gocci.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.inase.android.gocci.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class AddRestForumView extends LinearLayout {

    private EditText restnameEdit;
    private EditText localityEdit;
    private EditText tabelogEdit;
    private Button sendButton;

    private String mName;
    private String mRestnameString = null;
    private String mLocalityString = null;
    private String mTabelogString = null;

    private AsyncHttpClient httpClient;
    private RequestParams loginParam;
    private RequestParams restParams;

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sPostUrl = "";

    public AddRestForumView(Context context, String name) {
        super(context);
        mName = name;
        //チュートリアル・使い方ガイドを実装しようとしているクラス(未完成)

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_addrest, this);

        loginParam = new RequestParams("user_name", mName);
        restParams = new RequestParams();

        restnameEdit = (EditText) inflateView.findViewById(R.id.restnameEdit);
        localityEdit = (EditText) inflateView.findViewById(R.id.localityEdit);
        tabelogEdit = (EditText) inflateView.findViewById(R.id.tabelogUrlEdit);
        sendButton = (Button) inflateView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRestnameString = restnameEdit.getText().toString();
                mLocalityString = localityEdit.getText().toString();
                mTabelogString = tabelogEdit.getText().toString();

                if (mRestnameString != null && mLocalityString != null) {
                    //処理を書く
                    sendButton.setClickable(false);

                    postSignupAsync(getContext());

                } else {
                    Toast.makeText(getContext(), "入力が不正なようです", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void postSignupAsync(final Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postAsync(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postAsync(final Context context) {
        //restParams.put();
        httpClient.post(context, sPostUrl, restParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "送信が完了しました", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "送信に失敗しました", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public AddRestForumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddRestForumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

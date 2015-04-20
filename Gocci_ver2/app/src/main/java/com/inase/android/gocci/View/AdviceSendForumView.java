package com.inase.android.gocci.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.inase.android.gocci.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class AdviceSendForumView extends LinearLayout {

    private final String[] CATEGORY_ADVICE = new String[]{"ご要望", "苦情", "ご意見"};
    private String mName;

    private static final String sSignupUrl = "http://api-gocci.jp/login/";

    private static final String sSendUrl = "http://api-gocci.jp/feedback/";

    private AsyncHttpClient httpClient;
    private RequestParams loginParam;
    private RequestParams sendParams;

    private EditText adviceEdit;
    private Button adviceSendButton;
    private Spinner category;

    private String selectedCategory;
    private String mAdviceString;


    public AdviceSendForumView(Context context, String name) {
        super(context);

        adviceSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = String.valueOf(category.getSelectedItem());
                mAdviceString = adviceEdit.getText().toString();

                if (mAdviceString != null) {
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
        sendParams.put("select_support", selectedCategory);
        sendParams.put("content", mAdviceString);
        httpClient.post(context, sSendUrl, sendParams, new AsyncHttpResponseHandler() {

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

    public AdviceSendForumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdviceSendForumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

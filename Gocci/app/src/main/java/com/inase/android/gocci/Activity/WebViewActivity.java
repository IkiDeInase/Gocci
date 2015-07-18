package com.inase.android.gocci.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inase.android.gocci.R;

public class WebViewActivity extends AppCompatActivity {

    private int category;
    // policy 1 : license 2

    public static void startWebViewActivity(int category, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, FlexibleUserProfActivity.class);
        intent.putExtra("category", category);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        category = intent.getIntExtra("category", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView myWebView = (WebView) findViewById(R.id.webView);

        //リンクをタップしたときに標準ブラウザを起動させない
        myWebView.setWebViewClient(new WebViewClient());

        if (category == 1) {
            myWebView.loadUrl("file:///android_asset/policy.html");
            getSupportActionBar().setTitle("利用規約とポリシー");
        }
        if (category == 2) {
            myWebView.loadUrl("file:///android_asset/license.html");
            getSupportActionBar().setTitle("ライセンス情報");
        }

        //jacascriptを許可する
        myWebView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}

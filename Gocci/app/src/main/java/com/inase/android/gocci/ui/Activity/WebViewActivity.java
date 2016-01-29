package com.inase.android.gocci.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.web_view)
    WebView mWebView;

    private int category;
    // rule 0 : policy 1 : license 2

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private static final String URL_RULE = "http://inase-inc.jp/rules/";
    private static final String URL_POLICY = "http://inase-inc.jp/rules/privacy/";

    public static void startWebViewActivity(int category, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, WebViewActivity.class);
        intent.putExtra("category", category);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        Intent intent = getIntent();
        category = intent.getIntExtra("category", 0);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //リンクをタップしたときに標準ブラウザを起動させない
        mWebView.setWebViewClient(new WebViewClient());

        switch (category) {
            case 0:
                mWebView.loadUrl(URL_RULE);
                getSupportActionBar().setTitle(getString(R.string.rule));
                break;
            case 1:
                mWebView.loadUrl(URL_POLICY);
                getSupportActionBar().setTitle(getString(R.string.policy));
                break;
            case 2:
                mWebView.loadUrl("file:///android_asset/license.html");
                getSupportActionBar().setTitle(getString(R.string.source));
                break;
        }

        //jacascriptを許可する
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onPause() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        switch (category) {
            case 0:
                mTracker.setScreenName("rule");
                break;
            case 1:
                mTracker.setScreenName("policy");
                break;
            case 2:
                mTracker.setScreenName("license");
                break;
        }
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
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

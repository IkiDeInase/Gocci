package com.example.kinagafuji.gocci;


import android.app.Application;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.parse.Parse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class Application_Gocci extends Application {
    private final String TAG = "Gocci";

    @Override
    public void onCreate() {
        Log.v(TAG, "Gocci起動");

        Parse.initialize(this, getString(R.string.parse_app_id),
                getString(R.string.parse_client_key));

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

    }

    @Override
    public void onTerminate() {
        Log.v(TAG,"Gocci終了");
    }

}

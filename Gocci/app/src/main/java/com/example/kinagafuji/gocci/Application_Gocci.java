package com.example.kinagafuji.gocci;


import android.app.Application;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class Application_Gocci extends Application {
    private final String TAG = "Gocci";
    private HttpClient httpClient;

    @Override
    public void onCreate() {
        Log.v(TAG, "Gocci起動");
        httpClient = new DefaultHttpClient();
    }

    @Override
    public void onTerminate() {
        Log.v(TAG,"Gocci終了");
        httpClient.getConnectionManager().shutdown();
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }


}

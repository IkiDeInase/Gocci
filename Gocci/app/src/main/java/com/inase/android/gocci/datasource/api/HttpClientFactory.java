package com.inase.android.gocci.datasource.api;

import android.content.Context;

import com.inase.android.gocci.utils.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class HttpClientFactory {
    private static final SyncHttpClient sSsyncHttpClient = new SyncHttpClient();

    public static void get(Context context, String url, JsonHttpResponseHandler responseHandler) {
        sSsyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        sSsyncHttpClient.get(context, url, responseHandler);
    }
}

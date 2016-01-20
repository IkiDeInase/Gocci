package com.inase.android.gocci.utils.share;

import android.content.Context;
import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 16/01/15.
 */
public class FacebookUtil {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String SOURCE = "source";

    private static final String DESCRIPTION = "description";

    private static final String OCTET_STREAM = "application/octet-stream";

    private static final String FACEBOOK_SHARE_URL = "https://graph-video.facebook.com/me/videos";

    public static void performShare(final Context context, String token, final File movie, String description) {
        RequestParams param = new RequestParams();
        try {
            param.put(ACCESS_TOKEN, token);
            param.put(SOURCE, movie, OCTET_STREAM, "gocci.mp4");
            param.put(DESCRIPTION, description);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //190　400
        Application_Gocci.getClient().removeAllHeaders();
        Application_Gocci.getClient().setTimeout(50000);
        Application_Gocci.getClient().post(context, FACEBOOK_SHARE_URL, param, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), "Facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), "Facebookシェアが完了しました", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

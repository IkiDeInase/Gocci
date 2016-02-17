package com.inase.android.gocci.utils.share;

import android.content.Context;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.utils.SavedData;
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
        Application_Gocci.getClient().setTimeout(100000);
        Application_Gocci.getClient().post(context, FACEBOOK_SHARE_URL, param, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                BusHolder.get().post(new NotificationNumberEvent(SavedData.getNotification(context), "Facebookシェアに失敗しました"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                BusHolder.get().post(new NotificationNumberEvent(SavedData.getNotification(context), "Facebookシェアが完了しました"));
            }
        });
    }

    public static void performStoryPost(String title, String description, String videoUrl, String thumbUrl) {
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("fb:app_id", "977612005613710")
                .putString("og:type", "video.movie")
                .putString("og:title", "One Direction - Drag Me Down")
                .putString("og:description", "One Direction&#39;s new single Drag Me Down is out now! iTunes: http://smarturl.it/1DdmdIT Apple Music: http://smarturl.it/1DdmdAM Spotify: http://smarturl.it/1D...")
                .putString("og:url", "https://www.youtube.com/watch?v=Jwgf3wmiA04")
                .putString("og:image", "https://i.ytimg.com/vi/Jwgf3wmiA04/maxresdefault.jpg")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("video.watches")
                .putString("fb:explicitly_shared", "true")
                .putObject("movie", object)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("movie")
                .setAction(action)
                .build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e("ログ", result.toString());
            }

            @Override
            public void onCancel() {
                Log.e("ログ", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("ログ", error.toString());
            }
        });
    }
}

package com.inase.android.gocci.datasource.repository;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.inase.android.gocci.R;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/30.
 */
public class MyPageActionRepositoryImpl implements MyPageActionRepository {
    private static MyPageActionRepositoryImpl sMyPageActionRepository;

    public MyPageActionRepositoryImpl() {
    }

    public static MyPageActionRepositoryImpl getRepository() {
        if (sMyPageActionRepository == null) {
            sMyPageActionRepository = new MyPageActionRepositoryImpl();
        }
        return sMyPageActionRepository;
    }

    @Override
    public void changeProfile(String post_date, File file, final String url, final MyPageActionRepositoryCallback cb) {
        if (file != null || post_date != null) {
            TransferObserver transferObserver = Application_Gocci.getShareTransfer().upload(Const.POST_PHOTO_BUCKET_NAME, post_date + ".png", file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        postChangeProf(url, cb);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    cb.onError();
                }
            });
        } else {
            postChangeProf(url, cb);
        }
    }

    private void postChangeProf(String url, final MyPageActionRepositoryCallback cb) {
        Application_Gocci.getJsonHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                cb.onError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");
                    if (message.equals(Application_Gocci.getInstance().getString(R.string.change_profile_dialog_complete))) {
                        String name = response.getString("username");
                        String picture = response.getString("profile_img");
                        cb.onProfileChanged(name, picture);
                    } else {
                        cb.onProfileChangeFailed(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cb.onError();
                }
            }
        });
    }

    @Override
    public void deletePost(String post_id, final int position, final MyPageActionRepositoryCallback cb) {
        Application_Gocci.getJsonHttpClient(Const.getPostDeleteAPI(post_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");
                    if (message.equals(Application_Gocci.getInstance().getString(R.string.delete_post_complete_message))) {
                        cb.onPostDeleted(position);
                    } else {
                        cb.onPostDeleteFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cb.onError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                cb.onError();
            }
        });
    }
}

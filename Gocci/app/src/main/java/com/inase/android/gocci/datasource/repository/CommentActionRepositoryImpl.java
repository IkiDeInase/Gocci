package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentActionRepositoryImpl implements CommentActionRepository {
    private static CommentActionRepositoryImpl sCommentActionRepository;

    public CommentActionRepositoryImpl() {
    }

    public static CommentActionRepositoryImpl getRepository() {
        if (sCommentActionRepository == null) {
            sCommentActionRepository = new CommentActionRepositoryImpl();
        }
        return sCommentActionRepository;
    }

    @Override
    public void postComment(String postUrl, final String getUrl, final CommentActionRepositoryCallback cb) {
        Application_Gocci.getJsonSyncHttpClient(postUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");
                    if (message.equals(Application_Gocci.getInstance().getString(R.string.commented))) {
                        getCommentJson(getUrl, cb);
                    } else {
                        cb.onPostCommentFailed();
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

    private void getCommentJson(String getUrl, final CommentActionRepositoryCallback cb) {
        final ArrayList<HeaderData> mCommentData = new ArrayList<>();
        Application_Gocci.getTextSyncHttpClient(getUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                cb.onError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONArray array = new JSONArray(jsonObject.getString("comments"));
                    JSONObject obj = new JSONObject(jsonObject.getString("post"));

                    PostData mPostData = PostData.createPostData(obj);

                    if (array.length() != 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject comment = array.getJSONObject(i);
                            String judge = jsonObject.getString("comment");
                            if (!judge.equals("none")) {
                                mCommentData.add(HeaderData.createCommentHeaderData(comment));
                            }
                        }
                        cb.onPostCommented(mPostData, mCommentData);
                    } else {
                        cb.onPostCommentEmpty(mPostData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

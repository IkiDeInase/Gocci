package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
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
public class CommentDataRepositoryImpl implements CommentDataRepository {
    private static CommentDataRepositoryImpl sCommentDataRepository;

    public CommentDataRepositoryImpl() {
    }

    public static CommentDataRepositoryImpl getRepository() {
        if (sCommentDataRepository == null) {
            sCommentDataRepository = new CommentDataRepositoryImpl();
        }
        return sCommentDataRepository;
    }

    @Override
    public void getCommentDataList(final int api, String url, final CommentDataRepositoryCallback cb) {
        final ArrayList<HeaderData> mCommentData = new ArrayList<>();
        Application_Gocci.getTextHttpClient(url, new TextHttpResponseHandler() {
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
                        Collections.reverse(mCommentData);
                        cb.onCommentDataLoaded(api, mPostData, mCommentData);
                    } else {
                        cb.onCommentDataEmpty(api, mPostData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

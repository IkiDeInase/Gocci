package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class UserAndRestDataRepositoryImpl implements UserAndRestDataRepository {
    private static UserAndRestDataRepositoryImpl sUserDataRepository;

    public UserAndRestDataRepositoryImpl() {
    }

    public static UserAndRestDataRepositoryImpl getRepository() {
        if (sUserDataRepository == null) {
            sUserDataRepository = new UserAndRestDataRepositoryImpl();
        }
        return sUserDataRepository;
    }

    @Override
    public void getUserDataList(final int api, String url, final UserAndRestDataRepository.UserAndRestDataRepositoryCallback cb) {
        final ArrayList<PostData> mPostData = new ArrayList<>();
        final ArrayList<String> mPost_Ids = new ArrayList<>();

        Application_Gocci.getTextSyncHttpClient(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                cb.onError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    HeaderData mUserData = HeaderData.createUserHeaderData(headerObject);

                    if (postsObject.length() != 0) {
                        for (int i = 0; i < postsObject.length(); i++) {
                            JSONObject post = postsObject.getJSONObject(i);
                            mPostData.add(PostData.createPostData(post));
                            mPost_Ids.add(post.getString("post_id"));
                        }
                        cb.onUserAndRestDataLoaded(api, mUserData, mPostData, mPost_Ids);
                    } else {
                        cb.onUserAndRestDataEmpty(api, mUserData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getRestDataList(final int api, String url, final UserAndRestDataRepositoryCallback cb) {
        final ArrayList<PostData> mPostData = new ArrayList<>();
        final ArrayList<String> mPost_Ids = new ArrayList<>();

        Application_Gocci.getTextSyncHttpClient(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                cb.onError();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("restaurants");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    HeaderData mRestData = HeaderData.createTenpoHeaderData(headerObject);

                    if (postsObject.length() != 0) {
                        for (int i = 0; i < postsObject.length(); i++) {
                            JSONObject post = postsObject.getJSONObject(i);
                            mPostData.add(PostData.createPostData(post));
                            mPost_Ids.add(post.getString("post_id"));
                        }
                        cb.onUserAndRestDataLoaded(api, mRestData, mPostData, mPost_Ids);
                    } else {
                        cb.onUserAndRestDataEmpty(api, mRestData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

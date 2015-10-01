package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.application.Application_Gocci;
import com.inase.android.gocci.data.PostData;
import com.inase.android.gocci.datasource.api.ApiUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class PostDataRepositoryImpl implements PostDataRepository {
    private static PostDataRepositoryImpl sPostDataRepository;

    public PostDataRepositoryImpl() {
    }

    public static PostDataRepositoryImpl getRepository() {
        if (sPostDataRepository == null) {
            sPostDataRepository = new PostDataRepositoryImpl();
        }
        return sPostDataRepository;
    }

    @Override
    public void getPostDataList(final int api, String url, final PostDataRepositoryCallback cb) {
        final ArrayList<PostData> mPostData = new ArrayList<>();
        Application_Gocci.getJsonHttpClient(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() != 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            mPostData.add(PostData.createPostData(jsonObject));
                        }
                        cb.onPostDataLoaded(api, mPostData);
                    } else {
                        if (api == ApiUtil.TIMELINE_ADD) {
                            cb.onPostDataLoaded(api, mPostData);
                        } else {
                            cb.onPostDataEmpty();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                cb.onError();
            }
        });
    }
}

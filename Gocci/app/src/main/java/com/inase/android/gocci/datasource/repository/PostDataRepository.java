package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface PostDataRepository {
    void getPostDataList(int api, String url, PostDataRepositoryCallback cb);

    interface PostDataRepositoryCallback {
        void onPostDataLoaded(int api, ArrayList<PostData> postData);

        void onPostDataEmpty();

        void onError();
    }
}

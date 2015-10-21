package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface UserAndRestDataRepository {
    void getUserDataList(int api, String url, UserAndRestDataRepositoryCallback cb);

    void getRestDataList(int api, String url, UserAndRestDataRepositoryCallback cb);

    interface UserAndRestDataRepositoryCallback {
        void onUserAndRestDataLoaded(int api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids);

        void onUserAndRestDataEmpty(int api, HeaderData userData);

        void onError();
    }
}

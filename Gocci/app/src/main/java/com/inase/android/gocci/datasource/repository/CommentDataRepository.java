package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentDataRepository {

    void getCommentDataList(int api, String url, CommentDataRepositoryCallback cb);

    interface CommentDataRepositoryCallback {
        void onCommentDataLoaded(int api, PostData postData, ArrayList<HeaderData> commentData);

        void onCommentDataEmpty(int api, PostData postData);

        void onError();
    }
}

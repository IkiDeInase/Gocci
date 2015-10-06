package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentActionRepository {
    void postComment(String postUrl, String getUrl, CommentActionRepositoryCallback cb);

    interface CommentActionRepositoryCallback {
        void onPostCommented(PostData postData, ArrayList<HeaderData> commentData);

        void onPostCommentEmpty(PostData postData);

        void onPostCommentFailed();

        void onError();
    }
}

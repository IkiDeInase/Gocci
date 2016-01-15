package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentActionRepository {
    void postComment(Const.APICategory api, String postUrl, String getUrl, CommentActionRepositoryCallback cb);

    interface CommentActionRepositoryCallback {
        void onPostCommented(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids);

        void onPostEmpty(Const.APICategory api, HeaderData memoData);

        void onPostFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onPostFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

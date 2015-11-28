package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentPostUseCase {
    interface CommentPostUseCaseCallback {
        void onCommentPosted(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData);

        void onCommentPostEmpty(Const.APICategory api, HeaderData memoData);

        void onPostFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onPostFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String postUrl, String getUrl, CommentPostUseCaseCallback callback);

    void setCallback(CommentPostUseCaseCallback callback);

    void removeCallback();
}

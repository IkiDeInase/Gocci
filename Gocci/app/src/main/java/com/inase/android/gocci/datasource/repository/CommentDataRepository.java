package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentDataRepository {

    void getCommentDataList(Const.APICategory api, String url, CommentDataRepositoryCallback cb);

    interface CommentDataRepositoryCallback {
        void onCommentDataLoaded(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData);

        void onCommentDataEmpty(Const.APICategory api, HeaderData memoData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentPageUseCase {
    interface CommentPageUseCaseCallback {
        void onDataLoaded(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData);

        void onDataEmpty(Const.APICategory api, HeaderData memoData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, CommentPageUseCaseCallback callback);

    void setCallback(CommentPageUseCaseCallback callback);

    void removeCallback();
}

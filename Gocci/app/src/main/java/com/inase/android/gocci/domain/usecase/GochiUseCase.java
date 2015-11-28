package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

/**
 * Created by kinagafuji on 15/11/26.
 */
public interface GochiUseCase {
    interface GochiUseCaseCallback {
        void onGochiPosted(Const.APICategory api, String post_id);

        void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);

        void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);
    }

    void execute(Const.APICategory api, String url, String post_id, GochiUseCaseCallback callback);

    void setCallback(GochiUseCaseCallback callback);

    void removeCallback();
}

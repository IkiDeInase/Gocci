package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

/**
 * Created by kinagafuji on 15/11/26.
 */
public interface FollowUseCase {
    interface FollowUseCaseCallback {
        void onFollowPosted(Const.APICategory api, String user_id);

        void onFollowCausedByLocalError(Const.APICategory api, String errorMessage, String user_id);

        void onFollowCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id);
    }

    void execute(Const.APICategory api, String url, String user_id, FollowUseCaseCallback callback);

    void setCallback(FollowUseCaseCallback callback);

    void removeCallback();
}

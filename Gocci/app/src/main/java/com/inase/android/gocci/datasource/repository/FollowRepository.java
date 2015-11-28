package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

/**
 * Created by kinagafuji on 15/11/26.
 */
public interface FollowRepository {
    void postFollow(Const.APICategory api, String url, String user_id, FollowRepositoryCallback cb);

    interface FollowRepositoryCallback {
        void onSuccess(Const.APICategory api, String user_id);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id);
    }
}

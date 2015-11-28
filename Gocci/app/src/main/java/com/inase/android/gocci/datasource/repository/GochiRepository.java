package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

/**
 * Created by kinagafuji on 15/11/26.
 */
public interface GochiRepository {
    void postGochi(Const.APICategory api, String url, String post_id, GochiRepositoryCallback cb);

    interface GochiRepositoryCallback {
        void onSuccess(Const.APICategory api, String post_id);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);
    }
}

package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface LoginRepository {
    void userLogin(Const.APICategory api, String url, LoginRepositoryCallback cb);

    interface LoginRepositoryCallback {
        void onLogin(Const.APICategory api);

        void onNotLoginCausedByLocalError(Const.APICategory api, String errorMessage);

        void onNotLoginCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface UserLoginUseCase {

    interface UserLoginUseCaseCallback {
        void onUserLogin(Const.APICategory api);

        void onUserNotLoginCausedByLocalError(Const.APICategory api, String errorMessage);

        void onUserNotLoginCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, UserLoginUseCaseCallback callback);

    void setCallback(UserLoginUseCaseCallback callback);

    void removeCallback();
}

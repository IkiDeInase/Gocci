package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.pojo.User;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface UserLoginUseCase {

    interface UserLoginUseCaseCallback {
        void onUserLogin(int api, User user);

        void onUserNotLogin(int api);

        void onError();
    }

    void execute(int api, String url, UserLoginUseCaseCallback callback);

    void setCallback(UserLoginUseCaseCallback callback);

    void removeCallback();
}

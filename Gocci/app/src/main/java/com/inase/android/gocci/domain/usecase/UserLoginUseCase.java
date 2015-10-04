package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.User;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface UserLoginUseCase {

    interface UserLoginUseCaseCallback {
        void onUserLogin(User user);

        void onUserNotLogin();

        void onError();
    }

    void execute(String url, UserLoginUseCaseCallback callback);

    void setCallback(UserLoginUseCaseCallback callback);

    void removeCallback();
}

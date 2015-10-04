package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.domain.model.User;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface LoginRepository {
    void userLogin(String url, LoginRepositoryCallback cb);

    interface LoginRepositoryCallback {
        void onUserLogined(User user);

        void onUserNotLogined();

        void onError();
    }
}

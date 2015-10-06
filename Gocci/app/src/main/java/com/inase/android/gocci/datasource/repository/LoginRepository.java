package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.domain.model.pojo.User;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface LoginRepository {
    void userLogin(int api, String url, LoginRepositoryCallback cb);

    interface LoginRepositoryCallback {
        void onUserLogined(int api, User user);

        void onUserNotLogined(int api);

        void onError();
    }
}

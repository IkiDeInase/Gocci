package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.domain.model.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class LoginRepositoryImpl implements LoginRepository {
    private static LoginRepositoryImpl sLoginRepository;

    public LoginRepositoryImpl() {
    }

    public static LoginRepositoryImpl getRepository() {
        if (sLoginRepository == null) {
            sLoginRepository = new LoginRepositoryImpl();
        }
        return sLoginRepository;
    }

    @Override
    public void userLogin(final int api, String url, final LoginRepositoryCallback cb) {
        Application_Gocci.getJsonSyncHttpClient(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response.has("message")) {
                    User user = User.createUser(response);
                    cb.onUserLogined(api, user);
                } else {
                    cb.onUserNotLogined(api);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                cb.onError();
            }
        });
    }
}

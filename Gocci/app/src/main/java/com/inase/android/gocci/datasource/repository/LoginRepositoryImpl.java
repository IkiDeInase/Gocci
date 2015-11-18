package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class LoginRepositoryImpl implements LoginRepository {
    private static LoginRepositoryImpl sLoginRepository;
    private final API3 mAPI3;

    public LoginRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static LoginRepositoryImpl getRepository(API3 api3) {
        if (sLoginRepository == null) {
            sLoginRepository = new LoginRepositoryImpl(api3);
        }
        return sLoginRepository;
    }

    @Override
    public void userLogin(final Const.APICategory api, final String url, final LoginRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        switch (api) {
                            case AUTH_LOGIN:
                                mAPI3.auth_login_response(response, new API3.AuthResponseCallback() {
                                    @Override
                                    public void onSuccess() {
                                        cb.onLogin(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onNotLoginCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onNotLoginCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case AUTH_PASS_LOGIN:
                                mAPI3.auth_pass_login_response(response, new API3.AuthResponseCallback() {
                                    @Override
                                    public void onSuccess() {
                                        cb.onLogin(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onNotLoginCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onNotLoginCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case AUTH_FACEBOOK_LOGIN:
                            case AUTH_TWITTER_LOGIN:
                                mAPI3.auth_sns_login_response(response, new API3.AuthResponseCallback() {
                                    @Override
                                    public void onSuccess() {
                                        cb.onLogin(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onNotLoginCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onNotLoginCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case AUTH_SIGNUP:
                                mAPI3.auth_signup_response(response, new API3.AuthResponseCallback() {
                                    @Override
                                    public void onSuccess() {
                                        cb.onLogin(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        //グローバル　なんとかしないと！
                                        cb.onNotLoginCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onNotLoginCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onNotLoginCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onNotLoginCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            //グローバルエラー発生
            cb.onNotLoginCausedByGlobalError(api, globalCode);
        }
    }
}

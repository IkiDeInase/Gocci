package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

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
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    switch (api) {
                        case AUTH_LOGIN:
                        case AUTH_FACEBOOK_LOGIN:
                        case AUTH_TWITTER_LOGIN:
                            mAPI3.AuthLoginResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject payload) {
                                    try {
                                        String user_id = payload.getString("user_id");
                                        String username = payload.getString("username");
                                        String profile_img = payload.getString("profile_img");
                                        String badge_num = payload.getString("badge_num");
                                        String cognito_token = payload.getString("cognito_token");
                                        Application_Gocci.GuestInit(Application_Gocci.getInstance().getApplicationContext(), SavedData.getIdentityId(Application_Gocci.getInstance().getApplicationContext()), cognito_token, user_id);
                                        SavedData.setWelcome(Application_Gocci.getInstance().getApplicationContext(), username, profile_img, user_id, Integer.parseInt(badge_num));
                                        cb.onLogin(api);
                                    } catch (JSONException e) {
                                        cb.onNotLoginCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                    }
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
                            mAPI3.AuthPasswordResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject payload) {
                                    try {
                                        String identity_id = payload.getString("identity_id");
                                        SavedData.setIdentityId(Application_Gocci.getInstance().getApplicationContext(), identity_id);
                                        userLogin(Const.APICategory.AUTH_LOGIN, API3.Util.getAuthLoginAPI(identity_id), cb);
                                    } catch (JSONException e) {
                                        cb.onNotLoginCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                    }
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
                            mAPI3.AuthSignupResponse(response, new API3.PayloadResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject payload) {
                                    try {
                                        String identity_id = payload.getString("identity_id");
                                        SavedData.setIdentityId(Application_Gocci.getInstance().getApplicationContext(), identity_id);
                                        userLogin(Const.APICategory.AUTH_LOGIN, API3.Util.getAuthLoginAPI(identity_id), cb);
                                    } catch (JSONException e) {
                                        cb.onNotLoginCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                    }
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
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    cb.onNotLoginCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

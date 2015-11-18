package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class UserAndRestDataRepositoryImpl implements UserAndRestDataRepository {
    private static UserAndRestDataRepositoryImpl sUserDataRepository;
    private final API3 mAPI3;

    public UserAndRestDataRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static UserAndRestDataRepositoryImpl getRepository(API3 api3) {
        if (sUserDataRepository == null) {
            sUserDataRepository = new UserAndRestDataRepositoryImpl(api3);
        }
        return sUserDataRepository;
    }

    @Override
    public void getUserDataList(final Const.APICategory api, String url, final UserAndRestDataRepository.UserAndRestDataRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_user_response(response, new API3.GetUserAndRestResponseCallback() {
                            @Override
                            public void onSuccess(HeaderData headerData, ArrayList<PostData> postData, ArrayList<String> post_ids) {
                                cb.onUserAndRestDataLoaded(api, headerData, postData, post_ids);
                            }

                            @Override
                            public void onEmpty(HeaderData headerData) {
                                cb.onUserAndRestDataEmpty(api, headerData);
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                cb.onCausedByGlobalError(api, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                cb.onCausedByLocalError(api, errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            //グローバルエラー発生
            cb.onCausedByGlobalError(api, globalCode);
        }
    }

    @Override
    public void getRestDataList(final Const.APICategory api, String url, final UserAndRestDataRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_rest_response(response, new API3.GetUserAndRestResponseCallback() {
                            @Override
                            public void onSuccess(HeaderData headerData, ArrayList<PostData> postData, ArrayList<String> post_ids) {
                                cb.onUserAndRestDataLoaded(api, headerData, postData, post_ids);
                            }

                            @Override
                            public void onEmpty(HeaderData headerData) {
                                cb.onUserAndRestDataEmpty(api, headerData);
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                cb.onCausedByGlobalError(api, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                cb.onCausedByLocalError(api, errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            //グローバルエラー発生
            cb.onCausedByGlobalError(api, globalCode);
        }
    }
}

package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ListRepositoryImpl implements ListRepository {
    private static ListRepositoryImpl sListRepository;
    private final API3 mAPI3;

    public ListRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static ListRepositoryImpl getRepository(API3 api3) {
        if (sListRepository == null) {
            sListRepository = new ListRepositoryImpl(api3);
        }
        return sListRepository;
    }

    @Override
    public void getList(final Const.APICategory api, String url, final ListRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        switch (api) {
                            case GET_FOLLOW_FIRST:
                            case GET_FOLLOW_REFRESH:
                                mAPI3.get_follow_response(response, new API3.GetListResponseCallback() {

                                    @Override
                                    public void onSuccess(ArrayList<ListGetData> list) {
                                        cb.onSuccess(api, list);
                                    }

                                    @Override
                                    public void onEmpty() {
                                        cb.onEmpty(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onFailureCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onFailureCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case GET_FOLLOWER_FIRST:
                            case GET_FOLLOWER_REFRESH:
                                mAPI3.get_follower_response(response, new API3.GetListResponseCallback() {

                                    @Override
                                    public void onSuccess(ArrayList<ListGetData> list) {
                                        cb.onSuccess(api, list);
                                    }

                                    @Override
                                    public void onEmpty() {
                                        cb.onEmpty(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onFailureCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onFailureCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case GET_WANT_FIRST:
                            case GET_WANT_REFRESH:
                                mAPI3.get_want_response(response, new API3.GetListResponseCallback() {

                                    @Override
                                    public void onSuccess(ArrayList<ListGetData> list) {
                                        cb.onSuccess(api, list);
                                    }

                                    @Override
                                    public void onEmpty() {
                                        cb.onEmpty(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onFailureCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onFailureCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case GET_USER_CHEER_FIRST:
                            case GET_USER_CHEER_REFRESH:
                                mAPI3.get_user_cheer_response(response, new API3.GetListResponseCallback() {

                                    @Override
                                    public void onSuccess(ArrayList<ListGetData> list) {
                                        cb.onSuccess(api, list);
                                    }

                                    @Override
                                    public void onEmpty() {
                                        cb.onEmpty(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onFailureCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onFailureCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                            case GET_REST_CHEER_FIRST:
                            case GET_REST_CHEER_REFRESH:
                                mAPI3.get_rest_cheer_response(response, new API3.GetListResponseCallback() {

                                    @Override
                                    public void onSuccess(ArrayList<ListGetData> list) {
                                        cb.onSuccess(api, list);
                                    }

                                    @Override
                                    public void onEmpty() {
                                        cb.onEmpty(api);
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        cb.onFailureCausedByGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        cb.onFailureCausedByLocalError(api, errorMessage);
                                    }
                                });
                                break;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            //グローバルエラー発生
            cb.onFailureCausedByGlobalError(api, globalCode);
        }
    }
}

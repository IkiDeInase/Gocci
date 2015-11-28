package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/25.
 */
public class NearRepositoryImpl implements NearRepository {
    private static NearRepositoryImpl sNearRepository;
    private final API3 mAPI3;

    public NearRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static NearRepositoryImpl getRepository(API3 api3) {
        if (sNearRepository == null) {
            sNearRepository = new NearRepositoryImpl(api3);
        }
        return sNearRepository;
    }

    @Override
    public void getNear(final Const.APICategory api, final String url, final NearRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_near_response(response, new API3.GetNearResponseCallback() {

                            @Override
                            public void onSuccess(String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray) {
                                cb.onSuccess(api, restnames, restIdArray, restnameArray);
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
            cb.onFailureCausedByGlobalError(api, globalCode);
        }
    }
}

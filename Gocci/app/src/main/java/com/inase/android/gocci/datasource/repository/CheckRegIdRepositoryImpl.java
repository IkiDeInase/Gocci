package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.datasource.api.API3;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/10.
 */
public class CheckRegIdRepositoryImpl implements CheckRegIdRepository {
    private static CheckRegIdRepositoryImpl sCheckRegIdRepository;
    private final API3 mAPI3;

    public CheckRegIdRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static CheckRegIdRepositoryImpl getRepository(API3 api3) {
        if (sCheckRegIdRepository == null) {
            sCheckRegIdRepository = new CheckRegIdRepositoryImpl(api3);
        }
        return sCheckRegIdRepository;
    }

    @Override
    public void checkRegId(String url, final CheckRegIdRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.auth_check_response(response, new API3.CheckResponseCallback() {
                            @Override
                            public void onSuccess() {
                                cb.onSuccess();
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                cb.onFailureCausedByGlobalError(globalCode);
                            }

                            @Override
                            public void onLocalError(String id, String errorMessage) {
                                cb.onFailureCausedByLocalError(id, errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onFailureCausedByGlobalError(API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onFailureCausedByGlobalError(API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            cb.onFailureCausedByGlobalError(globalCode);
        }
    }
}

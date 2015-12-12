package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/26.
 */
public class GochiRepositoryImpl implements GochiRepository {
    private static GochiRepositoryImpl sGochiRepository;
    private final API3 mAPI3;

    public GochiRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static GochiRepositoryImpl getRepository(API3 api3) {
        if (sGochiRepository == null) {
            sGochiRepository = new GochiRepositoryImpl(api3);
        }
        return sGochiRepository;
    }

    @Override
    public void postGochi(final Const.APICategory api, String url, final String post_id, final GochiRepositoryCallback cb) {
        Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mAPI3.SetGochiResponse(response, new API3.PayloadResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject payload) {
                        cb.onSuccess(api, post_id);
                    }

                    @Override
                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                        cb.onFailureCausedByGlobalError(api, globalCode, post_id);
                    }

                    @Override
                    public void onLocalError(String errorMessage) {
                        cb.onFailureCausedByLocalError(api, errorMessage, post_id);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }
}

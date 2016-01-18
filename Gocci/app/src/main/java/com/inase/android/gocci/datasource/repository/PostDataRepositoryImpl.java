package com.inase.android.gocci.datasource.repository;

import android.util.Log;
import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 16/01/18.
 */
public class PostDataRepositoryImpl implements PostDataRepository {
    private static PostDataRepositoryImpl sPostDataRepository;
    private final API3 mAPI3;

    public PostDataRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static PostDataRepositoryImpl getRepository(API3 api3) {
        if (sPostDataRepository == null) {
            sPostDataRepository = new PostDataRepositoryImpl(api3);
        }
        return sPostDataRepository;
    }

    @Override
    public void getPostDataList(final Const.APICategory api, String url, final PostDataRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mAPI3.GetPostResponse(response, new API3.PayloadResponseCallback() {

                        @Override
                        public void onSuccess(JSONObject payload) {
                            PostData postData = PostData.createPostData(payload);
                            cb.onPostDataLoaded(api, postData);
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
                    cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

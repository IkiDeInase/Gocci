package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/25.
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
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_timeline_response(response, new API3.GetPostdataResponseCallback() {

                            @Override
                            public void onSuccess(ArrayList<TwoCellData> postData, ArrayList<String> post_ids) {
                                cb.onPostDataLoaded(api, postData, post_ids);
                            }

                            @Override
                            public void onEmpty() {
                                if (api == Const.APICategory.GET_TIMELINE_ADD) {
                                    cb.onPostDataLoaded(api, new ArrayList<TwoCellData>(), new ArrayList<String>());
                                } else {
                                    cb.onPostDataEmpty(api);
                                }
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

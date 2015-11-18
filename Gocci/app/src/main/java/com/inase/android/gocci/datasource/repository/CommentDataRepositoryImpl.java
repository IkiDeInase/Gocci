package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.CommentUserData;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentDataRepositoryImpl implements CommentDataRepository {
    private static CommentDataRepositoryImpl sCommentDataRepository;
    private final API3 mAPI3;

    public CommentDataRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static CommentDataRepositoryImpl getRepository(API3 api3) {
        if (sCommentDataRepository == null) {
            sCommentDataRepository = new CommentDataRepositoryImpl(api3);
        }
        return sCommentDataRepository;
    }

    @Override
    public void getCommentDataList(final Const.APICategory api, String url, final CommentDataRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_comment_response(response, new API3.GetCommentResponseCallback() {

                            @Override
                            public void onSuccess(HeaderData headerData, ArrayList<HeaderData> commentData) {
                                cb.onCommentDataLoaded(api, headerData, commentData);
                            }

                            @Override
                            public void onEmpty(HeaderData headerData) {
                                cb.onCommentDataEmpty(api, headerData);
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

package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
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
public class CommentActionRepositoryImpl implements CommentActionRepository {
    private static CommentActionRepositoryImpl sCommentActionRepository;
    private final API3 mAPI3;

    public CommentActionRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static CommentActionRepositoryImpl getRepository(API3 api3) {
        if (sCommentActionRepository == null) {
            sCommentActionRepository = new CommentActionRepositoryImpl(api3);
        }
        return sCommentActionRepository;
    }

    @Override
    public void postComment(final Const.APICategory api, String postUrl, final String getUrl, final CommentActionRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(postUrl, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.post_comment_response(response, new API3.PostResponseCallback() {
                            @Override
                            public void onSuccess() {
                                getCommentJson(api, getUrl, cb);
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                cb.onPostFailureCausedByGlobalError(api, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                cb.onPostFailureCausedByLocalError(api, errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            cb.onPostFailureCausedByGlobalError(api, globalCode);
        }
    }

    private void getCommentJson(final Const.APICategory api, String getUrl, final CommentActionRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(getUrl, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_comment_response(response, new API3.GetCommentResponseCallback() {

                            @Override
                            public void onSuccess(HeaderData headerData, ArrayList<HeaderData> commentData) {
                                cb.onPostCommented(api, headerData, commentData);
                            }

                            @Override
                            public void onEmpty(HeaderData headerData) {
                                cb.onPostEmpty(api, headerData);
                            }

                            @Override
                            public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                cb.onPostFailureCausedByGlobalError(api, globalCode);
                            }

                            @Override
                            public void onLocalError(String errorMessage) {
                                cb.onPostFailureCausedByLocalError(api, errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            //グローバルエラー発生
            cb.onPostFailureCausedByGlobalError(api, globalCode);
        }
    }
}

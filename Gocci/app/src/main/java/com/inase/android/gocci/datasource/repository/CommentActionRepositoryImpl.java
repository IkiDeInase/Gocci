package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.loopj.android.http.JsonHttpResponseHandler;

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
        API3.Util.GlobalCode globalCode = mAPI3.CheckGlobalCode();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(postUrl, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.SetCommentResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject jsonObject) {
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
        API3.Util.GlobalCode globalCode = mAPI3.CheckGlobalCode();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(getUrl, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.GetCommentResponse(response, new API3.PayloadResponseCallback() {

                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                try {
                                    JSONObject payload = jsonObject.getJSONObject("payload");
                                    JSONObject memo = payload.getJSONObject("memo");

                                    final ArrayList<HeaderData> mCommentData = new ArrayList<>();
                                    HeaderData headerData = HeaderData.createMemoData(memo);

                                    JSONArray comments = payload.getJSONArray("comments");
                                    if (comments.length() != 0) {
                                        for (int i = 0; i < comments.length(); i++) {
                                            JSONObject commentData = comments.getJSONObject(i);
                                            mCommentData.add(HeaderData.createCommentData(commentData));
                                        }
                                        cb.onPostCommented(api, headerData, mCommentData);
                                    } else {
                                        cb.onPostEmpty(api, headerData);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
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
                    cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    private void getCommentJson(final Const.APICategory api, String getUrl, final CommentActionRepositoryCallback cb) {
        Application_Gocci.getJsonSync(getUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mAPI3.GetCommentResponse(response, new API3.PayloadResponseCallback() {

                    @Override
                    public void onSuccess(JSONObject payload) {
                        try {
                            JSONObject memo = payload.getJSONObject("memo");
                            JSONArray comments = payload.getJSONArray("comments");

                            final ArrayList<HeaderData> mCommentData = new ArrayList<>();
                            HeaderData headerData = HeaderData.createMemoData(memo);

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
                            cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
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
                cb.onPostFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
            }
        });
    }
}

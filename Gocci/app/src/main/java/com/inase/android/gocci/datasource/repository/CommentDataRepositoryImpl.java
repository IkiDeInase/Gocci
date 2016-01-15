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
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mAPI3.GetCommentResponse(response, new API3.PayloadResponseCallback() {

                        @Override
                        public void onSuccess(JSONObject payload) {
                            try {
                                JSONObject memo = payload.getJSONObject("memo");
                                JSONArray comments = payload.getJSONArray("comments");

                                final ArrayList<HeaderData> mCommentData = new ArrayList<>();
                                final ArrayList<String> mComment_Ids = new ArrayList<>();
                                HeaderData headerData = HeaderData.createMemoData(memo);

                                if (comments.length() != 0) {
                                    for (int i = 0; i < comments.length(); i++) {
                                        JSONObject commentData = comments.getJSONObject(i);
                                        mCommentData.add(HeaderData.createCommentData(commentData));
                                        mComment_Ids.add(commentData.getString("comment_id"));
                                    }
                                    cb.onCommentDataLoaded(api, headerData, mCommentData, mComment_Ids);
                                } else {
                                    cb.onCommentDataEmpty(api, headerData);
                                }
                            } catch (JSONException e) {
                                cb.onGetByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                            }
                        }

                        @Override
                        public void onGlobalError(API3.Util.GlobalCode globalCode) {
                            cb.onGetByGlobalError(api, globalCode);
                        }

                        @Override
                        public void onLocalError(String errorMessage) {
                            cb.onGetCausedByLocalError(api, errorMessage);
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    cb.onGetByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

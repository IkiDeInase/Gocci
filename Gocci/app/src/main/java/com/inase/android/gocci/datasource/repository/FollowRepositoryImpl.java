package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/26.
 */
public class FollowRepositoryImpl implements FollowRepository {
    private static FollowRepositoryImpl sFollowRepository;
    private final API3 mAPI3;

    public FollowRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static FollowRepositoryImpl getRepository(API3 api3) {
        if (sFollowRepository == null) {
            sFollowRepository = new FollowRepositoryImpl(api3);
        }
        return sFollowRepository;
    }

    @Override
    public void postFollow(final Const.APICategory api, String url, final String user_id, final FollowRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (api == Const.APICategory.POST_FOLLOW) {
                            mAPI3.post_follow_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    cb.onSuccess(api, user_id);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    cb.onFailureCausedByGlobalError(api, globalCode, user_id);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    cb.onFailureCausedByLocalError(api, errorMessage, user_id);
                                }
                            });
                        } else if (api == Const.APICategory.POST_UNFOLLOW) {
                            mAPI3.post_unFollow_response(response, new API3.PostResponseCallback() {
                                @Override
                                public void onSuccess() {
                                    cb.onSuccess(api, user_id);
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    cb.onFailureCausedByGlobalError(api, globalCode, user_id);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    cb.onFailureCausedByLocalError(api, errorMessage, user_id);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED, user_id);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT, user_id);
            }
        } else {
            cb.onFailureCausedByGlobalError(api, globalCode, user_id);
        }
    }
}
package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/26.
 */
public class FollowRepositoryImpl implements FollowRepository {
    private static FollowRepositoryImpl sFollowRepository;
    private final API3 mAPI3;
    private long startTime;

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
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            startTime = System.currentTimeMillis();
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                    tracker.send(new HitBuilders.TimingBuilder()
                            .setCategory("System")
                            .setVariable(api.name())
                            .setLabel(SavedData.getServerUserId(Application_Gocci.getInstance()))
                            .setValue(System.currentTimeMillis() - startTime).build());
                    if (api == Const.APICategory.SET_FOLLOW) {
                        mAPI3.SetFollowResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
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
                    } else if (api == Const.APICategory.UNSET_FOLLOW) {
                        mAPI3.UnsetFollowResponse(response, new API3.PayloadResponseCallback() {
                            @Override
                            public void onSuccess(JSONObject payload) {
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
                    cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR, user_id);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

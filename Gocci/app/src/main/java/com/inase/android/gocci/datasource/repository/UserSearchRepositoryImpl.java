package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.SearchUserData;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 16/01/21.
 */
public class UserSearchRepositoryImpl implements UserSearchRepository {
    private static UserSearchRepositoryImpl sListRepository;
    private final API3 mAPI3;
    private long startTime;

    public UserSearchRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static UserSearchRepositoryImpl getRepository(API3 api3) {
        if (sListRepository == null) {
            sListRepository = new UserSearchRepositoryImpl(api3);
        }
        return sListRepository;
    }

    @Override
    public void getList(final Const.APICategory api, String url, final UserSearchRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            startTime = System.currentTimeMillis();
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                    Tracker tracker = Application_Gocci.getInstance().getDefaultTracker();
                    tracker.send(new HitBuilders.TimingBuilder()
                            .setCategory("System")
                            .setVariable(api.name())
                            .setLabel(SavedData.getServerUserId(Application_Gocci.getInstance()))
                            .setValue(System.currentTimeMillis() - startTime).build());
                    switch (api) {
                        case GET_USERNAME:
                            mAPI3.GetUsernameResponse(response, new API3.PayloadResponseCallback() {

                                @Override
                                public void onSuccess(JSONObject payload) {
                                    try {
                                        JSONArray users = payload.getJSONArray("users");

                                        final ArrayList<SearchUserData> mListData = new ArrayList<>();
                                        final ArrayList<String> mUser_ids = new ArrayList<>();

                                        for (int i = 0; i < users.length(); i++) {
                                            JSONObject listData = users.getJSONObject(i);
                                            mListData.add(SearchUserData.createSearchUserData(listData));
                                            mUser_ids.add(listData.getString("user_id"));
                                        }
                                        cb.onSuccess(api, mListData, mUser_ids);
                                    } catch (JSONException e) {
                                        cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                    }
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    cb.onFailureCausedByGlobalError(api, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    cb.onFailureCausedByLocalError(api, errorMessage);
                                }
                            });
                            break;
                        case GET_FOLLOWER_RANK_FIRST:
                        case GET_FOLLOWER_RANK_ADD:
                            mAPI3.GetUsernameResponse(response, new API3.PayloadResponseCallback() {

                                @Override
                                public void onSuccess(JSONObject payload) {
                                    try {
                                        JSONArray users = payload.getJSONArray("users");

                                        final ArrayList<SearchUserData> mListData = new ArrayList<>();
                                        final ArrayList<String> mUser_ids = new ArrayList<>();

                                        for (int i = 0; i < users.length(); i++) {
                                            JSONObject listData = users.getJSONObject(i);
                                            mListData.add(SearchUserData.createSearchUserData(listData));
                                            mUser_ids.add(listData.getString("user_id"));
                                        }
                                        cb.onSuccess(api, mListData, mUser_ids);
                                    } catch (JSONException e) {
                                        cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                                    }
                                }

                                @Override
                                public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                    cb.onFailureCausedByGlobalError(api, globalCode);
                                }

                                @Override
                                public void onLocalError(String errorMessage) {
                                    cb.onFailureCausedByLocalError(api, errorMessage);
                                }
                            });
                            break;
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

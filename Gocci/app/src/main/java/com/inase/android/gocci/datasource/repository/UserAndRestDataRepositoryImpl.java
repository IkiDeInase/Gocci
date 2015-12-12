package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class UserAndRestDataRepositoryImpl implements UserAndRestDataRepository {
    private static UserAndRestDataRepositoryImpl sUserDataRepository;
    private final API3 mAPI3;

    public UserAndRestDataRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static UserAndRestDataRepositoryImpl getRepository(API3 api3) {
        if (sUserDataRepository == null) {
            sUserDataRepository = new UserAndRestDataRepositoryImpl(api3);
        }
        return sUserDataRepository;
    }

    @Override
    public void getUserDataList(final Const.APICategory api, String url, final UserAndRestDataRepository.UserAndRestDataRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mAPI3.GetUserResponse(response, new API3.PayloadResponseCallback() {
                        @Override
                        public void onSuccess(JSONObject payload) {
                            try {
                                JSONObject user = payload.getJSONObject("user");
                                JSONArray posts = payload.getJSONArray("posts");

                                final ArrayList<PostData> mPostData = new ArrayList<>();
                                final ArrayList<String> mPost_Ids = new ArrayList<>();
                                HeaderData headerData = HeaderData.createUserHeaderData(user);

                                if (posts.length() != 0) {
                                    for (int i = 0; i < posts.length(); i++) {
                                        JSONObject postdata = posts.getJSONObject(i);
                                        mPostData.add(PostData.createUserPostData(postdata));
                                        mPost_Ids.add(postdata.getString("post_id"));
                                    }
                                    cb.onUserAndRestDataLoaded(api, headerData, mPostData, mPost_Ids);
                                } else {
                                    cb.onUserAndRestDataEmpty(api, headerData);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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

                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getRestDataList(final Const.APICategory api, String url, final UserAndRestDataRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mAPI3.GetRestResponse(response, new API3.PayloadResponseCallback() {
                        @Override
                        public void onSuccess(JSONObject payload) {
                            try {
                                JSONObject rest = payload.getJSONObject("rest");
                                JSONArray posts = payload.getJSONArray("posts");

                                final ArrayList<PostData> mPostData = new ArrayList<>();
                                final ArrayList<String> mPost_Ids = new ArrayList<>();
                                HeaderData headerData = HeaderData.createTenpoHeaderData(rest);

                                if (posts.length() != 0) {
                                    for (int i = 0; i < posts.length(); i++) {
                                        JSONObject postdata = posts.getJSONObject(i);
                                        mPostData.add(PostData.createRestPostData(postdata));
                                        mPost_Ids.add(postdata.getString("post_id"));
                                    }
                                    cb.onUserAndRestDataLoaded(api, headerData, mPostData, mPost_Ids);
                                } else {
                                    cb.onUserAndRestDataEmpty(api, headerData);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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

                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

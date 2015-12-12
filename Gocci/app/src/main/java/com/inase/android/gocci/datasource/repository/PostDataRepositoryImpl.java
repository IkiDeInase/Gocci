package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
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
        API3.Util.GlobalCode globalCode = mAPI3.CheckGlobalCode();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        switch (api) {
                            case GET_NEARLINE_FIRST:
                            case GET_NEARLINE_REFRESH:
                            case GET_NEARLINE_FILTER:
                            case GET_NEARLINE_ADD:
                                mAPI3.GetNearlineResponse(response, new API3.PayloadResponseCallback() {

                                    @Override
                                    public void onSuccess(JSONObject payload) {
                                        try {
                                            JSONArray posts = payload.getJSONArray("posts");

                                            final ArrayList<TwoCellData> mPostData = new ArrayList<>();
                                            final ArrayList<String> mPost_Ids = new ArrayList<>();

                                            if (posts.length() != 0) {
                                                for (int i = 0; i < posts.length(); i++) {
                                                    JSONObject postdata = posts.getJSONObject(i);
                                                    mPostData.add(TwoCellData.createPostData(postdata));
                                                    mPost_Ids.add(postdata.getString("post_id"));
                                                }
                                                cb.onPostDataLoaded(api, mPostData, mPost_Ids);
                                            } else {
                                                if (api == Const.APICategory.GET_NEARLINE_ADD) {
                                                    cb.onPostDataLoaded(api, new ArrayList<TwoCellData>(), new ArrayList<String>());
                                                } else {
                                                    cb.onPostDataEmpty(api);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
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
                                break;
                            case GET_FOLLOWLINE_FIRST:
                            case GET_FOLLOWLINE_REFRESH:
                            case GET_FOLLOWLINE_FILTER:
                            case GET_FOLLOWLINE_ADD:
                                mAPI3.GetFollowlineResponse(response, new API3.PayloadResponseCallback() {

                                    @Override
                                    public void onSuccess(JSONObject payload) {
                                        try {
                                            JSONArray posts = payload.getJSONArray("posts");

                                            final ArrayList<TwoCellData> mPostData = new ArrayList<>();
                                            final ArrayList<String> mPost_Ids = new ArrayList<>();

                                            if (posts.length() != 0) {
                                                for (int i = 0; i < posts.length(); i++) {
                                                    JSONObject postdata = posts.getJSONObject(i);
                                                    mPostData.add(TwoCellData.createPostData(postdata));
                                                    mPost_Ids.add(postdata.getString("post_id"));
                                                }
                                                cb.onPostDataLoaded(api, mPostData, mPost_Ids);
                                            } else {
                                                if (api == Const.APICategory.GET_FOLLOWLINE_ADD) {
                                                    cb.onPostDataLoaded(api, new ArrayList<TwoCellData>(), new ArrayList<String>());
                                                } else {
                                                    cb.onPostDataEmpty(api);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
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
                                break;
                            case GET_TIMELINE_FIRST:
                            case GET_TIMELINE_REFRESH:
                            case GET_TIMELINE_FILTER:
                            case GET_TIMELINE_ADD:
                                mAPI3.GetTimelineResponse(response, new API3.PayloadResponseCallback() {

                                    @Override
                                    public void onSuccess(JSONObject payload) {
                                        try {
                                            JSONArray posts = payload.getJSONArray("posts");

                                            final ArrayList<TwoCellData> mPostData = new ArrayList<>();
                                            final ArrayList<String> mPost_Ids = new ArrayList<>();

                                            if (posts.length() != 0) {
                                                for (int i = 0; i < posts.length(); i++) {
                                                    JSONObject postdata = posts.getJSONObject(i);
                                                    mPostData.add(TwoCellData.createPostData(postdata));
                                                    mPost_Ids.add(postdata.getString("post_id"));
                                                }
                                                cb.onPostDataLoaded(api, mPostData, mPost_Ids);
                                            } else {
                                                if (api == Const.APICategory.GET_TIMELINE_ADD) {
                                                    cb.onPostDataLoaded(api, new ArrayList<TwoCellData>(), new ArrayList<String>());
                                                } else {
                                                    cb.onPostDataEmpty(api);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
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
                                break;
                        }
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

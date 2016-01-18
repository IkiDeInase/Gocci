package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class PostsDataRepositoryImpl implements PostsDataRepository {
    private static PostsDataRepositoryImpl sPostDataRepository;
    private final API3 mAPI3;

    public PostsDataRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static PostsDataRepositoryImpl getRepository(API3 api3) {
        if (sPostDataRepository == null) {
            sPostDataRepository = new PostsDataRepositoryImpl(api3);
        }
        return sPostDataRepository;
    }

    @Override
    public void getPostDataList(final Const.APICategory api, String url, final PostDataRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
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
                                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
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
                                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
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
                                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
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
                        case GET_GOCHILINE_FIRST:
                        case GET_GOCHILINE_REFRESH:
                        case GET_GOCHILINE_ADD:
                        case GET_GOCHILINE_FILTER:
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
                                            if (api == Const.APICategory.GET_GOCHILINE_ADD) {
                                                cb.onPostDataLoaded(api, new ArrayList<TwoCellData>(), new ArrayList<String>());
                                            } else {
                                                cb.onPostDataEmpty(api);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
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
                        case GET_COMMENTLINE_FIRST:
                        case GET_COMMENTLINE_REFRESH:
                        case GET_COMMENTLINE_FILTER:
                        case GET_COMMENTLINE_ADD:
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
                                            if (api == Const.APICategory.GET_COMMENTLINE_ADD) {
                                                cb.onPostDataLoaded(api, new ArrayList<TwoCellData>(), new ArrayList<String>());
                                            } else {
                                                cb.onPostDataEmpty(api);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
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
                    cb.onCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_UNKNOWN_ERROR);
                }
            });
        } else {
            Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), Application_Gocci.getInstance().getApplicationContext().getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }
    }
}

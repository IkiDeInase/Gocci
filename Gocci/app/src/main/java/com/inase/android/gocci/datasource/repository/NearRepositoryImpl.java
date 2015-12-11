package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/25.
 */
public class NearRepositoryImpl implements NearRepository {
    private static NearRepositoryImpl sNearRepository;
    private final API3 mAPI3;

    public NearRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static NearRepositoryImpl getRepository(API3 api3) {
        if (sNearRepository == null) {
            sNearRepository = new NearRepositoryImpl(api3);
        }
        return sNearRepository;
    }

    @Override
    public void getNear(final Const.APICategory api, final String url, final NearRepositoryCallback cb) {
        API3.Util.GlobalCode globalCode = mAPI3.CheckGlobalCode();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.GetNearResponse(response, new API3.PayloadResponseCallback() {

                            @Override
                            public void onSuccess(JSONObject jsonObject) {
                                try {
                                    String[] restnames = new String[30];
                                    final ArrayList<String> restIdArray = new ArrayList<>();
                                    final ArrayList<String> restnameArray = new ArrayList<>();

                                    JSONArray payload = jsonObject.getJSONArray("payload");
                                    if (payload.length() != 0) {
                                        for (int i = 0; i < payload.length(); i++) {
                                            JSONObject listData = payload.getJSONObject(i);
                                            final String rest_name = listData.getString("restname");
                                            String rest_id = listData.getString("rest_id");

                                            restnames[i] = rest_name;
                                            restIdArray.add(rest_id);
                                            restnameArray.add(rest_name);
                                        }
                                        cb.onSuccess(api, restnames, restIdArray, restnameArray);
                                    } else {
                                        cb.onEmpty(api);
                                    }
                                } catch (JSONException e) {
                                    cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_BASEFRAME_JSON_MALFORMED);
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
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_NO_DATA_RECIEVED);
                    }
                });
            } catch (SocketTimeoutException e) {
                cb.onFailureCausedByGlobalError(api, API3.Util.GlobalCode.ERROR_CONNECTION_TIMEOUT);
            }
        } else {
            cb.onFailureCausedByGlobalError(api, globalCode);
        }
    }
}

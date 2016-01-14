package com.inase.android.gocci.datasource.repository;

import android.widget.Toast;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.utils.Util;
import com.inase.android.gocci.utils.map.HeatmapLog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kinagafuji on 15/11/05.
 */
public class HeatmapRepositoryImpl implements HeatmapRepository {
    private static HeatmapRepositoryImpl sHeatmapRepository;
    private final API3 mAPI3;

    public HeatmapRepositoryImpl(API3 api3) {
        mAPI3 = api3;
    }

    public static HeatmapRepositoryImpl getRepository(API3 api3) {
        if (sHeatmapRepository == null) {
            sHeatmapRepository = new HeatmapRepositoryImpl(api3);
        }
        return sHeatmapRepository;
    }

    @Override
    public void getHeatmap(final Const.APICategory api, final String url, final HeatmapRepositoryCallback cb) {
        if (Util.getConnectedState(Application_Gocci.getInstance().getApplicationContext()) != Util.NetworkStatus.OFF) {
            Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mAPI3.GetHeatmapResponse(response, new API3.PayloadResponseCallback() {

                        @Override
                        public void onSuccess(JSONObject payload) {
                            try {
                                JSONArray rests = payload.getJSONArray("rests");

                                final ArrayList<HeatmapLog> mListData = new ArrayList<>();

                                for (int i = 0; i < rests.length(); i++) {
                                    JSONObject listData = rests.getJSONObject(i);
                                    String rest_id = listData.getString("rest_id");
                                    String restname = listData.getString("restname");
                                    double lat = listData.getDouble("lat");
                                    double lon = listData.getDouble("lon");
                                    mListData.add(new HeatmapLog(rest_id, restname, lat, lon));
                                }
                                cb.onSuccess(api, mListData);
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

package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.utils.map.HeatmapLog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
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
        API3.Util.GlobalCode globalCode = mAPI3.check_global_error();
        if (globalCode == API3.Util.GlobalCode.SUCCESS) {
            try {
                Application_Gocci.getJsonSync(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mAPI3.get_heatmap_response(response, new API3.GetHeatmapResponseCallback() {

                            @Override
                            public void onSuccess(ArrayList<HeatmapLog> list) {
                                cb.onSuccess(api, list);
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

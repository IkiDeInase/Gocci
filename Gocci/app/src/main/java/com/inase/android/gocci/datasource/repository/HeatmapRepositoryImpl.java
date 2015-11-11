package com.inase.android.gocci.datasource.repository;

import com.google.android.gms.maps.model.LatLng;
import com.inase.android.gocci.Application_Gocci;
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

    private static final String TAG_LON = "lon";
    private static final String TAG_LAT = "lat";

    private static HeatmapRepositoryImpl sHeatmapRepository;

    public HeatmapRepositoryImpl() {
    }

    public static HeatmapRepositoryImpl getRepository() {
        if (sHeatmapRepository == null) {
            sHeatmapRepository = new HeatmapRepositoryImpl();
        }
        return sHeatmapRepository;
    }

    @Override
    public void getHeatmap(String url, final HeatmapRepositoryCallback cb) {
        final ArrayList<LatLng> mHeatData = new ArrayList<>();

        Application_Gocci.getJsonSyncHttpClient(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() != 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            //mPostData.add(PostData.createPostData(jsonObject));
                            mHeatData.add(new LatLng(jsonObject.getDouble(TAG_LAT), jsonObject.getDouble(TAG_LON)));
                        }
                        cb.onHeatmapLoaded(mHeatData);
                    } else {
                        cb.onError();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    cb.onError();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                cb.onError();
            }
        });
    }
}

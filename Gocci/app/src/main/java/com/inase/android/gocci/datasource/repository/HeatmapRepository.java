package com.inase.android.gocci.datasource.repository;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public interface HeatmapRepository {
    void getHeatmap(String url, HeatmapRepositoryCallback cb);

    interface HeatmapRepositoryCallback {
        void onHeatmapLoaded(ArrayList<LatLng> heatData);

        void onError();
    }
}

package com.inase.android.gocci.domain.usecase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public interface HeatmapUseCase {

    interface HeatmapUseCaseCallback {
        void onHeatmapLoaded(ArrayList<LatLng> heatData);

        void onError();
    }

    void execute(String url, HeatmapUseCaseCallback callback);

    void setCallback(HeatmapUseCaseCallback callback);

    void removeCallback();
}

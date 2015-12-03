package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.utils.map.HeatmapLog;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public interface HeatmapUseCase {

    interface HeatmapUseCaseCallback {
        void onHeatmapLoaded(Const.APICategory api, ArrayList<HeatmapLog> heatData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, HeatmapUseCaseCallback callback);

    void setCallback(HeatmapUseCaseCallback callback);

    void removeCallback();
}

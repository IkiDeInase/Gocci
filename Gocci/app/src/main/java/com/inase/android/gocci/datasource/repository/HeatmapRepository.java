package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.utils.map.HeatmapLog;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public interface HeatmapRepository {
    void getHeatmap(Const.APICategory api, String url, HeatmapRepositoryCallback cb);

    interface HeatmapRepositoryCallback {
        void onSuccess(Const.APICategory api, ArrayList<HeatmapLog> heatData);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

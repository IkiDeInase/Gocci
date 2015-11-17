package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.TwoCellData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface TimelineLatestUseCase {

    interface LatestTimelineUseCaseCallback {
        void onLatestTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids);

        void onLatestTimelineEmpty(Const.APICategory api);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, LatestTimelineUseCaseCallback callback);

    void setCallback(LatestTimelineUseCaseCallback callback);

    void removeCallback();
}

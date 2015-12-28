package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/12/28.
 */
public interface TimelineGochiUseCase {

    interface GochiTimelineUseCaseCallback {
        void onGochiTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids);

        void onGochiTimelineEmpty(Const.APICategory api);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, GochiTimelineUseCaseCallback callback);

    void setCallback(GochiTimelineUseCaseCallback callback);

    void removeCallback();
}

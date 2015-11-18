package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public interface NoticeDataUseCase {
    interface NoticeDataUseCaseCallback {
        void onSuccess(Const.APICategory api, ArrayList<HeaderData> list);

        void onEmpty(Const.APICategory api);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, NoticeDataUseCaseCallback callback);

    void setCallback(NoticeDataUseCaseCallback callback);

    void removeCallback();
}

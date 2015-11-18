package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public interface ListGetUseCase {
    interface ListGetUseCaseCallback {
        void onLoaded(Const.APICategory api, ArrayList<ListGetData> list);

        void onEmpty(Const.APICategory api);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, ListGetUseCaseCallback callback);

    void setCallback(ListGetUseCaseCallback callback);

    void removeCallback();
}

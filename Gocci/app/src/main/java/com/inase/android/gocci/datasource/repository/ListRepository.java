package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public interface ListRepository {
    void getList(Const.APICategory api, String url, ListRepositoryCallback cb);

    interface ListRepositoryCallback {
        void onSuccess(Const.APICategory api, ArrayList<ListGetData> list);

        void onEmpty(Const.APICategory api);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

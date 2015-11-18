package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public interface NoticeRepository {
    void getNotice(Const.APICategory api, String url, NoticeRepositoryCallback cb);

    interface NoticeRepositoryCallback {
        void onSuccess(Const.APICategory api, ArrayList<HeaderData> list);

        void onEmpty(Const.APICategory api);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

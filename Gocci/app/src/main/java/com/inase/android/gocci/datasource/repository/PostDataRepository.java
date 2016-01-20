package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.PostData;

/**
 * Created by kinagafuji on 16/01/18.
 */
public interface PostDataRepository {
    void getPostDataList(Const.APICategory api, String url, PostDataRepositoryCallback cb);

    interface PostDataRepositoryCallback {
        void onPostDataLoaded(Const.APICategory api, PostData postData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

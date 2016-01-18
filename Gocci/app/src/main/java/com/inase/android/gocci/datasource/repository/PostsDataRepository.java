package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface PostsDataRepository {
    void getPostDataList(Const.APICategory api, String url, PostDataRepositoryCallback cb);

    interface PostDataRepositoryCallback {
        void onPostDataLoaded(Const.APICategory api, ArrayList<TwoCellData> postData, ArrayList<String> post_ids);

        void onPostDataEmpty(Const.APICategory api);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

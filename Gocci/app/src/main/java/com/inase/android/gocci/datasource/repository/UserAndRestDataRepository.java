package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface UserAndRestDataRepository {
    void getUserDataList(Const.APICategory api, String url, UserAndRestDataRepositoryCallback cb);

    void getRestDataList(Const.APICategory api, String url, UserAndRestDataRepositoryCallback cb);

    interface UserAndRestDataRepositoryCallback {
        void onUserAndRestDataLoaded(Const.APICategory api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids);

        void onUserAndRestDataEmpty(Const.APICategory api, HeaderData userData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

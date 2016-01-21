package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.SearchUserData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 16/01/21.
 */
public interface UserSearchRepository {
    void getList(Const.APICategory api, String url, UserSearchRepositoryCallback cb);

    interface UserSearchRepositoryCallback {
        void onSuccess(Const.APICategory api, ArrayList<SearchUserData> list, ArrayList<String> user_ids);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

package com.inase.android.gocci.datasource.repository;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/25.
 */
public interface NearRepository {
    void getNear(Const.APICategory api, String url, NearRepositoryCallback cb);

    interface NearRepositoryCallback {
        void onSuccess(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray);

        void onEmpty(Const.APICategory api);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }
}

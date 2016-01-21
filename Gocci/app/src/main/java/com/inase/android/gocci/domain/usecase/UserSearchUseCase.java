package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.SearchUserData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 16/01/21.
 */
public interface UserSearchUseCase {
    interface UserSearchUseCaseCallback {
        void onUserSearchListed(Const.APICategory api, ArrayList<SearchUserData> data, ArrayList<String> user_ids);

        void onFailureCausedByLocalError(Const.APICategory api, String errorMessage);

        void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String getUrl, UserSearchUseCaseCallback callback);

    void setCallback(UserSearchUseCaseCallback callback);

    void removeCallback();
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface UserAndRestUseCase {

    interface UserAndRestUseCaseCallback {
        void onDataLoaded(Const.APICategory api, HeaderData userdata, ArrayList<PostData> postData, ArrayList<String> post_ids);

        void onDataEmpty(Const.APICategory api, HeaderData mUserData);

        void onCausedByLocalError(Const.APICategory api, String errorMessage);

        void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);
    }

    void execute(Const.APICategory api, String url, UserAndRestUseCaseCallback callback);

    void setCallback(UserAndRestUseCaseCallback callback);

    void removeCallback();
}

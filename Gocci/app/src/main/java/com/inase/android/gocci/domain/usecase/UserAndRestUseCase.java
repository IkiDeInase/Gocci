package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.pojo.HeaderData;
import com.inase.android.gocci.domain.model.pojo.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface UserAndRestUseCase {

    interface UserAndRestUseCaseCallback {
        void onDataLoaded(int api, HeaderData userdata, ArrayList<PostData> postData);

        void onDataEmpty(int api, HeaderData mUserData);

        void onError();
    }

    void execute(int api, String url, UserAndRestUseCaseCallback callback);

    void setCallback(UserAndRestUseCaseCallback callback);

    void removeCallback();
}
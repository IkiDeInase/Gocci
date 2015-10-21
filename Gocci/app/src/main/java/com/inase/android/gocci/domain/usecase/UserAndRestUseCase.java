package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface UserAndRestUseCase {

    interface UserAndRestUseCaseCallback {
        void onDataLoaded(int api, HeaderData userdata, ArrayList<PostData> postData, ArrayList<String> post_ids);

        void onDataEmpty(int api, HeaderData mUserData);

        void onError();
    }

    void execute(int api, String url, UserAndRestUseCaseCallback callback);

    void setCallback(UserAndRestUseCaseCallback callback);

    void removeCallback();
}

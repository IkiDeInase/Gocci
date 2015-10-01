package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface ProfUseCase {

    interface ProfUseCaseCallback {
        void onProfLoaded(int api, HeaderData mUserdata, ArrayList<PostData> mPostData);

        void onProfEmpty(int api, HeaderData mUserData);

        void onError();
    }

    void execute(int api, String url, ProfUseCaseCallback callback);

    void setCallback(ProfUseCaseCallback callback);

    void removeCallback();
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.data.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public interface FollowTimelineUseCase {

    interface FollowTimelineUseCaseCallback {
        void onFollowTimelineLoaded(int api, ArrayList<PostData> mPostData);
        void onFollowTimelineEmpty();
        void onError();
    }

    void execute(int api, String url, FollowTimelineUseCaseCallback callback);

    void setCallback(FollowTimelineUseCaseCallback callback);
    void removeCallback();
}

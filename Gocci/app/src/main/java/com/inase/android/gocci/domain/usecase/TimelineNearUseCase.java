package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/11.
 */
public interface TimelineNearUseCase {

    interface NearTimelineUseCaseCallback {
        void onNearTimelineLoaded(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void onNearTimelineEmpty(int api);

        void onError();
    }

    void execute(int api, String url, NearTimelineUseCaseCallback callback);

    void setCallback(NearTimelineUseCaseCallback callback);

    void removeCallback();
}

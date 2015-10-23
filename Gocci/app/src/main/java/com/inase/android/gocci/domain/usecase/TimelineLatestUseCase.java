package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface TimelineLatestUseCase {

    interface LatestTimelineUseCaseCallback {
        void onLatestTimelineLoaded(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void onLatestTimelineEmpty(int api);

        void onError();
    }

    void execute(int api, String url, LatestTimelineUseCaseCallback callback);

    void setCallback(LatestTimelineUseCaseCallback callback);

    void removeCallback();
}

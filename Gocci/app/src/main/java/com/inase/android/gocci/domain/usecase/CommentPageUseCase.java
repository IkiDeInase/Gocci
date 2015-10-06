package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentPageUseCase {
    interface CommentPageUseCaseCallback {
        void onDataLoaded(int api, PostData postData, ArrayList<HeaderData> commentData);

        void onDataEmpty(int api, PostData postData);

        void onError();
    }

    void execute(int api, String url, CommentPageUseCaseCallback callback);

    void setCallback(CommentPageUseCaseCallback callback);

    void removeCallback();
}

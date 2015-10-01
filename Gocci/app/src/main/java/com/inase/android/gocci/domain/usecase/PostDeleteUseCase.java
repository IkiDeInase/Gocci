package com.inase.android.gocci.domain.usecase;

import java.io.File;

/**
 * Created by kinagafuji on 15/10/01.
 */
public interface PostDeleteUseCase {

    interface PostDeleteUseCaseCallback {
        void onPostDeleted(int position);

        void onPostDeleteFailed();

        void onError();
    }

    void execute(String post_id, int position, PostDeleteUseCaseCallback callback);

    void setCallback(PostDeleteUseCaseCallback callback);

    void removeCallback();
}

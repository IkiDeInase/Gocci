package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public interface CommentPostUseCase {
    interface CommentPostUseCaseCallback {
        void onCommentPosted(PostData postData, ArrayList<HeaderData> commentData);

        void onCommentPostEmpty(PostData postData);

        void onCommentPostFailed();

        void onError();
    }

    void execute(String postUrl, String getUrl, CommentPostUseCaseCallback callback);

    void setCallback(CommentPostUseCaseCallback callback);

    void removeCallback();
}

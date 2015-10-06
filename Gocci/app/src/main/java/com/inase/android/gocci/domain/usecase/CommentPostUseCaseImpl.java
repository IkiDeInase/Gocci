package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.CommentActionRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentPostUseCaseImpl extends UseCase2<String, String> implements CommentPostUseCase, CommentActionRepository.CommentActionRepositoryCallback {
    private static CommentPostUseCaseImpl sUseCase;
    private final CommentActionRepository mCommentActionRepository;
    private PostExecutionThread mPostExecutionThread;
    private CommentPostUseCaseCallback mCallback;

    public static CommentPostUseCaseImpl getUseCase(CommentActionRepository commentActionRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new CommentPostUseCaseImpl(commentActionRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public CommentPostUseCaseImpl(CommentActionRepository commentActionRepository, PostExecutionThread postExecutionThread) {
        mCommentActionRepository = commentActionRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onPostCommented(final PostData postData, final ArrayList<HeaderData> commentData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentPosted(postData, commentData);
                }
            }
        });
    }

    @Override
    public void onPostCommentEmpty(final PostData postData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentPostEmpty(postData);
                }
            }
        });
    }

    @Override
    public void onPostCommentFailed() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentPostFailed();
                }
            }
        });
    }

    @Override
    public void onError() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onError();
                }
            }
        });
    }

    @Override
    public void execute(String postUrl, String getUrl, CommentPostUseCaseCallback callback) {
        mCallback = callback;
        this.start(postUrl, getUrl);
    }

    @Override
    public void setCallback(CommentPostUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(String param1, String param2) {
        mCommentActionRepository.postComment(param1, param2, this);
    }
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.CommentActionRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentPostUseCaseImpl extends UseCase3<Const.APICategory, String, String> implements CommentPostUseCase, CommentActionRepository.CommentActionRepositoryCallback {
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
    public void execute(Const.APICategory api, String postUrl, String getUrl, CommentPostUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, postUrl, getUrl);
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
    public void onPostCommented(final Const.APICategory api, final HeaderData memoData, final ArrayList<HeaderData> commentData, final ArrayList<String> comment_ids) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentPosted(api, memoData, commentData, comment_ids);
                }
            }
        });
    }

    @Override
    public void onPostEmpty(final Const.APICategory api, final HeaderData memoData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentPostEmpty(api, memoData);
                }
            }
        });
    }

    @Override
    public void onPostFailureCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onPostFailureCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onPostFailureCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onPostFailureCausedByGlobalError(api, globalCode);
                }
            }
        });
    }

    @Override
    protected void call(Const.APICategory param1, String param2, String param3) {
        mCommentActionRepository.postComment(param1, param2, param3, this);
    }
}

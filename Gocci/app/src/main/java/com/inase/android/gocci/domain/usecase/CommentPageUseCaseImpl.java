package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.CommentDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentPageUseCaseImpl extends UseCase2<Const.APICategory, String> implements CommentPageUseCase, CommentDataRepository.CommentDataRepositoryCallback {
    private static CommentPageUseCaseImpl sUseCase;
    private final CommentDataRepository mCommentDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private CommentPageUseCaseCallback mCallback;

    public static CommentPageUseCaseImpl getUseCase(CommentDataRepository commentDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new CommentPageUseCaseImpl(commentDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public CommentPageUseCaseImpl(CommentDataRepository commentDataRepository, PostExecutionThread postExecutionThread) {
        mCommentDataRepository = commentDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, CommentPageUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mCommentDataRepository.getCommentDataList(param1, param2, this);
    }

    @Override
    public void setCallback(CommentPageUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onCommentDataLoaded(final Const.APICategory api, final HeaderData memoData, final ArrayList<HeaderData> commentData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataLoaded(api, memoData, commentData);
                }
            }
        });
    }

    @Override
    public void onCommentDataEmpty(final Const.APICategory api, final HeaderData memoData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataEmpty(api, memoData);
                }
            }
        });
    }

    @Override
    public void onGetCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onGetCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onGetByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onGetCausedByGlobalError(api, globalCode);
                }
            }
        });
    }
}

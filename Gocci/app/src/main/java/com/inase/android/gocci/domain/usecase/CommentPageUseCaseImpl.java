package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.CommentDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class CommentPageUseCaseImpl extends UseCase2<Integer, String> implements CommentPageUseCase, CommentDataRepository.CommentDataRepositoryCallback {
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
    public void onCommentDataLoaded(final int api, final PostData postData, final ArrayList<HeaderData> commentData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataLoaded(api, postData, commentData);
                }
            }
        });
    }

    @Override
    public void onCommentDataEmpty(final int api, final PostData postData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataEmpty(api, postData);
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
    public void execute(int api, String url, CommentPageUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Integer param1, String param2) {
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

}

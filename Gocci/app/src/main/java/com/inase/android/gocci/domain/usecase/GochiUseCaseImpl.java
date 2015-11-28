package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.GochiRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

/**
 * Created by kinagafuji on 15/11/26.
 */
public class GochiUseCaseImpl extends UseCase3<Const.APICategory, String, String> implements GochiUseCase, GochiRepository.GochiRepositoryCallback {
    private static GochiUseCaseImpl sUseCase;
    private final GochiRepository mGochiRepository;
    private PostExecutionThread mPostExecutionThread;
    private GochiUseCaseCallback mCallback;

    public static GochiUseCaseImpl getUseCase(GochiRepository gochiRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new GochiUseCaseImpl(gochiRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public GochiUseCaseImpl(GochiRepository gochiRepository, PostExecutionThread postExecutionThread) {
        mGochiRepository = gochiRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onSuccess(final Const.APICategory api, final String post_id) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onGochiPosted(api, post_id);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByLocalError(final Const.APICategory api, final String errorMessage, final String post_id) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onGochiCausedByLocalError(api, errorMessage, post_id);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode, final String post_id) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onGochiCausedByGlobalError(api, globalCode, post_id);
                }
            }
        });
    }

    @Override
    public void execute(Const.APICategory api, String url, String post_id, GochiUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url, post_id);
    }

    @Override
    public void setCallback(GochiUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(Const.APICategory param1, String param2, String param3) {
        mGochiRepository.postGochi(param1, param2, param3, this);
    }
}

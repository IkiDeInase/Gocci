package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.datasource.repository.CheckRegIdRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

/**
 * Created by kinagafuji on 15/11/10.
 */
public class CheckRegIdUseCaseImpl extends UseCase<String> implements CheckRegIdUseCase, CheckRegIdRepository.CheckRegIdRepositoryCallback {

    private static CheckRegIdUseCaseImpl sUseCase;
    private final CheckRegIdRepository mCheckRegIdRepository;
    private PostExecutionThread mPostExecutionThread;
    private CheckRegIdUseCaseCallback mCallback;

    public static CheckRegIdUseCaseImpl getUseCase(CheckRegIdRepository checkRegIdRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new CheckRegIdUseCaseImpl(checkRegIdRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public CheckRegIdUseCaseImpl(CheckRegIdRepository checkRegIdRepository, PostExecutionThread postExecutionThread) {
        mCheckRegIdRepository = checkRegIdRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onSuccess() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onSuccess();
                }
            }
        });
    }

    @Override
    public void onFailureCausedByLocalError(final String id, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFailureCausedByLocalError(id, errorMessage);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByGlobalError(final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFailureCausedByGlobalError(globalCode);
                }
            }
        });
    }

    @Override
    public void execute(String url, CheckRegIdUseCaseCallback callback) {
        mCallback = callback;
        this.start(url);
    }

    @Override
    public void setCallback(CheckRegIdUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(String params) {
        mCheckRegIdRepository.checkRegId(params, this);
    }
}

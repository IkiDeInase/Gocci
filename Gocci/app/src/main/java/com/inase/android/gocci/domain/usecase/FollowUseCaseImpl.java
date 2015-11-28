package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.FollowRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

/**
 * Created by kinagafuji on 15/11/26.
 */
public class FollowUseCaseImpl extends UseCase3<Const.APICategory, String, String> implements FollowUseCase, FollowRepository.FollowRepositoryCallback {
    private static FollowUseCaseImpl sUseCase;
    private final FollowRepository mFollowRepository;
    private PostExecutionThread mPostExecutionThread;
    private FollowUseCaseCallback mCallback;

    public static FollowUseCaseImpl getUseCase(FollowRepository followRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new FollowUseCaseImpl(followRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public FollowUseCaseImpl(FollowRepository followRepository, PostExecutionThread postExecutionThread) {
        mFollowRepository = followRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onSuccess(final Const.APICategory api, final String user_id) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFollowPosted(api, user_id);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByLocalError(final Const.APICategory api, final String errorMessage, final String user_id) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFollowCausedByLocalError(api, errorMessage, user_id);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode, final String user_id) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFollowCausedByGlobalError(api, globalCode, user_id);
                }
            }
        });
    }

    @Override
    public void execute(Const.APICategory api, String url, String user_id, FollowUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url, user_id);
    }

    @Override
    public void setCallback(FollowUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(Const.APICategory param1, String param2, String param3) {
        mFollowRepository.postFollow(param1, param2, param3, this);
    }
}

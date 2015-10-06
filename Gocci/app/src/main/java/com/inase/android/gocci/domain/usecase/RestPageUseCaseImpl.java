package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.domain.model.pojo.HeaderData;
import com.inase.android.gocci.domain.model.pojo.PostData;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/04.
 */
public class RestPageUseCaseImpl extends UseCase2<Integer, String> implements UserAndRestUseCase, UserAndRestDataRepository.UserAndRestDataRepositoryCallback {
    private static RestPageUseCaseImpl sUseCase;
    private final UserAndRestDataRepository mUserAndRestDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private UserAndRestUseCaseCallback mCallback;

    public static RestPageUseCaseImpl getUseCase(UserAndRestDataRepository userAndRestDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new RestPageUseCaseImpl(userAndRestDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public RestPageUseCaseImpl(UserAndRestDataRepository userAndRestDataRepository, PostExecutionThread postExecutionThread) {
        mUserAndRestDataRepository = userAndRestDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(int api, String url, UserAndRestUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Integer param1, String param2) {
        mUserAndRestDataRepository.getRestDataList(param1, param2, this);
    }

    @Override
    public void setCallback(UserAndRestUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onUserAndRestDataLoaded(final int api, final HeaderData userData, final ArrayList<PostData> postData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataLoaded(api, userData, postData);
                }
            }
        });
    }

    @Override
    public void onUserAndRestDataEmpty(final int api, final HeaderData userData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataEmpty(api, userData);
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
}
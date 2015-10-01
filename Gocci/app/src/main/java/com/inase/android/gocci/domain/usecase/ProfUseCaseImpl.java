package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.inase.android.gocci.datasource.repository.UserDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ProfUseCaseImpl extends UseCase2<Integer, String> implements ProfUseCase, UserDataRepository.UserDataRepositoryCallback {
    private static ProfUseCaseImpl sUseCase;
    private final UserDataRepository mUserDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private ProfUseCaseCallback mCallback;

    public static ProfUseCaseImpl getUseCase(UserDataRepository userDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new ProfUseCaseImpl(userDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public ProfUseCaseImpl(UserDataRepository userDataRepository, PostExecutionThread postExecutionThread) {
        mUserDataRepository = userDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(int api, String url, ProfUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Integer param1, String param2) {
        mUserDataRepository.getUserDataList(param1, param2, this);
    }

    @Override
    public void setCallback(ProfUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onUserDataLoaded(final int api, final HeaderData userData, final ArrayList<PostData> postData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onProfLoaded(api, userData, postData);
                }
            }
        });
    }

    @Override
    public void onUserDataEmpty(final int api, final HeaderData userData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onProfEmpty(api, userData);
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

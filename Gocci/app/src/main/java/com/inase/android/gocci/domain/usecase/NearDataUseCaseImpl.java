package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.NearRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/25.
 */
public class NearDataUseCaseImpl extends UseCase2<Const.APICategory, String> implements NearDataUseCase, NearRepository.NearRepositoryCallback {
    private static NearDataUseCaseImpl sUseCase;
    private final NearRepository mNearRepository;
    private PostExecutionThread mPostExecutionThread;
    private NearDataUseCaseCallback mCallback;

    public static NearDataUseCaseImpl getUseCase(NearRepository nearRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new NearDataUseCaseImpl(nearRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public NearDataUseCaseImpl(NearRepository nearRepository, PostExecutionThread postExecutionThread) {
        mNearRepository = nearRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, NearDataUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    public void setCallback(NearDataUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onSuccess(final Const.APICategory api, final String[] restnames, final ArrayList<String> restIdArray, final ArrayList<String> restnameArray) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onLoaded(api, restnames, restIdArray, restnameArray);
                }
            }
        });
    }

    @Override
    public void onEmpty(final Const.APICategory api) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onEmpty(api);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCausedByGlobalError(api, globalCode);
                }
            }
        });
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mNearRepository.getNear(param1, param2, this);
    }
}

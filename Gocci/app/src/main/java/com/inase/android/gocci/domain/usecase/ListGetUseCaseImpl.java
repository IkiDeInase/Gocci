package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.ListRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.ListGetData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ListGetUseCaseImpl extends UseCase2<Const.APICategory, String> implements ListGetUseCase, ListRepository.ListRepositoryCallback {
    private static ListGetUseCaseImpl sUseCase;
    private final ListRepository mListRepository;
    private PostExecutionThread mPostExecutionThread;
    private ListGetUseCaseCallback mCallback;

    public static ListGetUseCaseImpl getUseCase(ListRepository listRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new ListGetUseCaseImpl(listRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public ListGetUseCaseImpl(ListRepository listRepository, PostExecutionThread postExecutionThread) {
        mListRepository = listRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, ListGetUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mListRepository.getList(param1, param2, this);
    }

    @Override
    public void setCallback(ListGetUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onSuccess(final Const.APICategory api, final ArrayList<ListGetData> list) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onLoaded(api, list);
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
}

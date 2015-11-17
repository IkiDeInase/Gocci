package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ProfPageUseCaseImpl extends UseCase2<Const.APICategory, String> implements UserAndRestUseCase, UserAndRestDataRepository.UserAndRestDataRepositoryCallback {
    private static ProfPageUseCaseImpl sUseCase;
    private final UserAndRestDataRepository mUserAndRestDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private UserAndRestUseCaseCallback mCallback;

    public static ProfPageUseCaseImpl getUseCase(UserAndRestDataRepository userAndRestDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new ProfPageUseCaseImpl(userAndRestDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public ProfPageUseCaseImpl(UserAndRestDataRepository userAndRestDataRepository, PostExecutionThread postExecutionThread) {
        mUserAndRestDataRepository = userAndRestDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, UserAndRestUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mUserAndRestDataRepository.getUserDataList(param1, param2, this);
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
    public void onUserAndRestDataLoaded(final Const.APICategory api, final HeaderData userData, final ArrayList<PostData> postData, final ArrayList<String> post_ids) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onDataLoaded(api, userData, postData, post_ids);
                }
            }
        });
    }

    @Override
    public void onUserAndRestDataEmpty(final Const.APICategory api, final HeaderData userData) {
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
    public void onCausedByLocalError(final Const.APICategory api, final String errorMessage) {
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
    public void onCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
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

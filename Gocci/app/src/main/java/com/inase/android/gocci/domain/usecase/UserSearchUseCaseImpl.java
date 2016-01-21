package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.UserSearchRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.SearchUserData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 16/01/21.
 */
public class UserSearchUseCaseImpl extends UseCase2<Const.APICategory, String> implements UserSearchUseCase, UserSearchRepository.UserSearchRepositoryCallback {
    private static UserSearchUseCaseImpl sUseCase;
    private final UserSearchRepository mUserSearchRepository;
    private PostExecutionThread mPostExecutionThread;
    private UserSearchUseCaseCallback mCallback;

    public static UserSearchUseCaseImpl getUseCase(UserSearchRepository userSearchRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new UserSearchUseCaseImpl(userSearchRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public UserSearchUseCaseImpl(UserSearchRepository userSearchRepository, PostExecutionThread postExecutionThread) {
        mUserSearchRepository = userSearchRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String getUrl, UserSearchUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, getUrl);
    }

    @Override
    public void setCallback(UserSearchUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onSuccess(final Const.APICategory api, final ArrayList<SearchUserData> list, final ArrayList<String> user_ids) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onUserSearchListed(api, list, user_ids);
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
                    mCallback.onFailureCausedByLocalError(api, errorMessage);
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
                    mCallback.onFailureCausedByGlobalError(api, globalCode);
                }
            }
        });
    }

    @Override
    protected void call(Const.APICategory param, String param1) {
        mUserSearchRepository.getList(param, param1, this);
    }
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class UserLoginUseCaseImpl extends UseCase2<Const.APICategory, String> implements UserLoginUseCase, LoginRepository.LoginRepositoryCallback {

    private static UserLoginUseCaseImpl sUseCase;
    private final LoginRepository mLoginRepository;
    private PostExecutionThread mPostExecutionThread;
    private UserLoginUseCaseCallback mCallback;

    public static UserLoginUseCaseImpl getUseCase(LoginRepository loginRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new UserLoginUseCaseImpl(loginRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public UserLoginUseCaseImpl(LoginRepository loginRepository, PostExecutionThread postExecutionThread) {
        mLoginRepository = loginRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onLogin(final Const.APICategory api) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onUserLogin(api);
                }
            }
        });
    }

    @Override
    public void onNotLoginCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onUserNotLoginCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onNotLoginCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onUserNotLoginCausedByGlobalError(api, globalCode);
                }
            }
        });
    }

    @Override
    public void execute(Const.APICategory api, String url, UserLoginUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    public void setCallback(UserLoginUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mLoginRepository.userLogin(param1, param2, this);
    }
}

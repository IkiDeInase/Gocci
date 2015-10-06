package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.pojo.User;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class UserLoginUseCaseImpl extends UseCase2<Integer, String> implements UserLoginUseCase, LoginRepository.LoginRepositoryCallback {

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
    public void onUserLogined(final int api, final User user) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onUserLogin(api, user);
                }
            }
        });
    }

    @Override
    public void onUserNotLogined(final int api) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onUserNotLogin(api);
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

    @Override
    public void execute(int api, String url, UserLoginUseCaseCallback callback) {
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
    protected void call(Integer param1, String param2) {
        mLoginRepository.userLogin(param1, param2, this);
    }
}

package com.inase.android.gocci.presenter;

import com.inase.android.gocci.domain.model.User;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class ShowUserLoginPresenter extends Presenter implements UserLoginUseCase.UserLoginUseCaseCallback {

    private UserLoginUseCase mUserLoginUseCase;
    private ShowUserLogin mShowUserLogin;

    public ShowUserLoginPresenter(UserLoginUseCase userLoginUseCase) {
        mUserLoginUseCase = userLoginUseCase;
    }

    public void setShowUserLoginView(ShowUserLogin view) {
        mShowUserLogin = view;
    }

    public void loginUser(String url) {
        mShowUserLogin.showLoading();
        mUserLoginUseCase.execute(url, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserLoginUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserLoginUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onUserLogin(User user) {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showResult(user);
    }

    @Override
    public void onUserNotLogin() {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showNoResult();
    }

    @Override
    public void onError() {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showError();
    }

    public interface ShowUserLogin {
        void showLoading();

        void hideLoading();

        void showResult(User user);

        void showNoResult();

        void showError();
    }
}

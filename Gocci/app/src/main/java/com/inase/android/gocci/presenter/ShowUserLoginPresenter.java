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

    public void loginUser(int api, String url) {
        mShowUserLogin.showLoading();
        mUserLoginUseCase.execute(api, url, this);
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
    public void onUserLogin(int api, User user) {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showResult(api, user);
    }

    @Override
    public void onUserNotLogin(int api) {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showNoResult(api);
    }

    @Override
    public void onError() {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showError();
    }

    public interface ShowUserLogin {
        void showLoading();

        void hideLoading();

        void showResult(int api, User user);

        void showNoResult(int api);

        void showError();
    }
}

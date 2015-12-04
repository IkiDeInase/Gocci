package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
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

    public void loginUser(Const.APICategory api, String url) {
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
    public void onUserLogin(Const.APICategory api) {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showResult(api);
    }

    @Override
    public void onUserNotLoginCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onUserNotLoginCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowUserLogin.hideLoading();
        mShowUserLogin.showNoResultCausedByGlobalError(api, globalCode);
    }

    public interface ShowUserLogin {
        void showLoading();

        void hideLoading();

        void showResult(Const.APICategory api);

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);
    }
}

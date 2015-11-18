package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.usecase.CheckRegIdUseCase;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class ShowUserLoginPresenter extends Presenter implements UserLoginUseCase.UserLoginUseCaseCallback, CheckRegIdUseCase.CheckRegIdUseCaseCallback {

    private UserLoginUseCase mUserLoginUseCase;
    private CheckRegIdUseCase mCheckRegIdUseCase;
    private ShowUserLogin mShowUserLogin;

    public ShowUserLoginPresenter(UserLoginUseCase userLoginUseCase, CheckRegIdUseCase checkRegIdUseCase) {
        mCheckRegIdUseCase = checkRegIdUseCase;
        mUserLoginUseCase = userLoginUseCase;
    }

    public void setShowUserLoginView(ShowUserLogin view) {
        mShowUserLogin = view;
    }

    public void loginUser(Const.APICategory api, String url) {
        mShowUserLogin.showLoading();
        mUserLoginUseCase.execute(api, url, this);
    }

    public void checkRegId(String url) {
        mCheckRegIdUseCase.execute(url, this);
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

    @Override
    public void onSuccess() {
        mShowUserLogin.onCheckSuccess();
    }

    @Override
    public void onFailureCausedByLocalError(String id, String errorMessage) {
        mShowUserLogin.onCheckFailureCausedByLocalError(id, errorMessage);
    }

    @Override
    public void onFailureCausedByGlobalError(API3.Util.GlobalCode globalCode) {
        mShowUserLogin.onCheckFailureCausedByGlobalError(globalCode);
    }

    public interface ShowUserLogin {
        void showLoading();

        void hideLoading();

        void onCheckSuccess();

        void onCheckFailureCausedByLocalError(String id, String errorMessage);

        void onCheckFailureCausedByGlobalError(API3.Util.GlobalCode globalCode);

        void showResult(Const.APICategory api);

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);
    }
}

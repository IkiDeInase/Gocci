package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ShowMyProfPresenter extends Presenter implements UserAndRestUseCase.UserAndRestUseCaseCallback, GochiUseCase.GochiUseCaseCallback {

    private UserAndRestUseCase mUserAndRestUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowProfView mShowProfView;

    public ShowMyProfPresenter(UserAndRestUseCase userAndRestUseCase, GochiUseCase gochiUseCase) {
        mUserAndRestUseCase = userAndRestUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setProfView(ShowProfView view) {
        mShowProfView = view;
    }

    public void getProfData(Const.APICategory api, String url) {
        mShowProfView.showLoading();
        mUserAndRestUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData mUserdata, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowProfView.hideLoading();
        mShowProfView.hideEmpty();
        mShowProfView.showResult(api, mUserdata, mPostData, post_ids);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData mUserData) {
        mShowProfView.hideLoading();
        mShowProfView.showEmpty(api, mUserData);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowProfView.hideLoading();
        mShowProfView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowProfView.hideLoading();
        mShowProfView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserAndRestUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserAndRestUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowProfView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowProfView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowProfView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowProfView {
        void showLoading();

        void hideLoading();

        void showEmpty(Const.APICategory api, HeaderData mUserData);

        void hideEmpty();

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, HeaderData mUserData, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void gochiSuccess(Const.APICategory api, String post_id);

        void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);

        void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);
    }
}

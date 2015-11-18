package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/04.
 */
public class ShowUserProfPresenter extends Presenter implements UserAndRestUseCase.UserAndRestUseCaseCallback {

    private UserAndRestUseCase mUserAndRestUseCase;
    private ShowUserProfView mShowUserProfView;

    public ShowUserProfPresenter(UserAndRestUseCase userAndRestUseCase) {
        mUserAndRestUseCase = userAndRestUseCase;
    }

    public void setProfView(ShowUserProfView view) {
        mShowUserProfView = view;
    }

    public void getProfData(Const.APICategory api, String url) {
        mShowUserProfView.showLoading();
        mUserAndRestUseCase.execute(api, url, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserAndRestUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserAndRestUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData mUserdata, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.hideNoResultCase();
        mShowUserProfView.showResult(api, mUserdata, mPostData, post_ids);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData mUserData) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.showNoResultCase(api, mUserData);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.showNoResultCausedByGlobalError(api, globalCode);
    }

    public interface ShowUserProfView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api, HeaderData userData);

        void hideNoResultCase();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids);
    }
}

package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.FollowUseCase;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/04.
 */
public class ShowUserProfPresenter extends Presenter implements UserAndRestUseCase.UserAndRestUseCaseCallback, FollowUseCase.FollowUseCaseCallback,
        GochiUseCase.GochiUseCaseCallback {

    private UserAndRestUseCase mUserAndRestUseCase;
    private FollowUseCase mFollowUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowUserProfView mShowUserProfView;

    public ShowUserProfPresenter(UserAndRestUseCase userAndRestUseCase, FollowUseCase followUseCase, GochiUseCase gochiUseCase) {
        mUserAndRestUseCase = userAndRestUseCase;
        mFollowUseCase = followUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setProfView(ShowUserProfView view) {
        mShowUserProfView = view;
    }

    public void getProfData(Const.APICategory api, String url) {
        mShowUserProfView.showLoading();
        mUserAndRestUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    public void postFollow(Const.APICategory api, String url, String user_id) {
        mFollowUseCase.execute(api, url, user_id, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserAndRestUseCase.setCallback(this);
        mFollowUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserAndRestUseCase.removeCallback();
        mFollowUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData mUserdata, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.hideEmpty();
        mShowUserProfView.showResult(api, mUserdata, mPostData, post_ids);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData mUserData) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.showEmpty(api, mUserData);
    }

    @Override
    public void onFollowPosted(Const.APICategory api, String user_id) {
        mShowUserProfView.followSuccess(api, user_id);
    }

    @Override
    public void onFollowCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        mShowUserProfView.followFailureCausedByLocalError(api, errorMessage, user_id);
    }

    @Override
    public void onFollowCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        mShowUserProfView.followFailureCausedByGlobalError(api, globalCode, user_id);
    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowUserProfView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        mShowUserProfView.gochiFailureCausedByLocalError(api, errorMessage, user_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        mShowUserProfView.gochiFailureCausedByGlobalError(api, globalCode, user_id);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.causedByGlobalError(api, globalCode);
    }

    public interface ShowUserProfView {
        void showLoading();

        void hideLoading();

        void showEmpty(Const.APICategory api, HeaderData userData);

        void hideEmpty();

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void followSuccess(Const.APICategory api, String user_id);

        void followFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id);

        void followFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id);

        void showResult(Const.APICategory api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids);

        void gochiSuccess(Const.APICategory api, String post_id);

        void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);

        void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);
    }
}

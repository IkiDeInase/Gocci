package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.SearchUserData;
import com.inase.android.gocci.domain.usecase.FollowUseCase;
import com.inase.android.gocci.domain.usecase.UserSearchUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 16/01/21.
 */
public class ShowUserSearchPresenter extends Presenter implements UserSearchUseCase.UserSearchUseCaseCallback, FollowUseCase.FollowUseCaseCallback {
    private UserSearchUseCase mUserSearchUseCase;
    private FollowUseCase mFollowUseCase;
    private ShowUserSearchView mShowUserSearchView;

    public ShowUserSearchPresenter(UserSearchUseCase userSearchUseCase, FollowUseCase followUseCase) {
        mUserSearchUseCase = userSearchUseCase;
        mFollowUseCase = followUseCase;
    }

    public void setUserSearchView(ShowUserSearchView view) {
        mShowUserSearchView = view;
    }

    public void getListData(Const.APICategory api, String url) {
        mShowUserSearchView.showLoading();
        mUserSearchUseCase.execute(api, url, this);
    }

    public void postFollow(Const.APICategory api, String url, String user_id) {
        mFollowUseCase.execute(api, url, user_id, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserSearchUseCase.setCallback(this);
        mFollowUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserSearchUseCase.removeCallback();
        mFollowUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onFollowPosted(Const.APICategory api, String user_id) {
        mShowUserSearchView.followSuccess(api, user_id);
    }

    @Override
    public void onFollowCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        mShowUserSearchView.followFailureCausedByLocalError(api, errorMessage, user_id);
    }

    @Override
    public void onFollowCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        mShowUserSearchView.followFailureCausedByGlobalError(api, globalCode, user_id);
    }

    @Override
    public void onUserSearchListed(Const.APICategory api, ArrayList<SearchUserData> data, ArrayList<String> user_ids) {
        mShowUserSearchView.hideLoading();
        mShowUserSearchView.showResult(api, data, user_ids);
    }

    @Override
    public void onFailureCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowUserSearchView.hideLoading();
        mShowUserSearchView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowUserSearchView.hideLoading();
        mShowUserSearchView.causedByGlobalError(api, globalCode);
    }

    public interface ShowUserSearchView {
        void showLoading();

        void hideLoading();

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void followSuccess(Const.APICategory api, String user_id);

        void followFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id);

        void followFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id);

        void showResult(Const.APICategory api, ArrayList<SearchUserData> list, ArrayList<String> user_ids);
    }
}

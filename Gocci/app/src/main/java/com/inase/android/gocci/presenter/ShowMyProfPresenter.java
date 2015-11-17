package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.PostDeleteUseCase;
import com.inase.android.gocci.domain.usecase.ProfChangeUseCase;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ShowMyProfPresenter extends Presenter implements UserAndRestUseCase.UserAndRestUseCaseCallback,
        ProfChangeUseCase.ProfChangeUseCaseCallback, PostDeleteUseCase.PostDeleteUseCaseCallback {

    private UserAndRestUseCase mUserAndRestUseCase;
    private ProfChangeUseCase mProfChangeUseCase;
    private PostDeleteUseCase mPostDeleteUseCase;
    private ShowProfView mShowProfView;

    public ShowMyProfPresenter(UserAndRestUseCase userAndRestUseCase, ProfChangeUseCase profChangeUseCase, PostDeleteUseCase postDeleteUseCase) {
        mUserAndRestUseCase = userAndRestUseCase;
        mProfChangeUseCase = profChangeUseCase;
        mPostDeleteUseCase = postDeleteUseCase;
    }

    public void setProfView(ShowProfView view) {
        mShowProfView = view;
    }

    public void getProfData(Const.APICategory api, String url) {
        mShowProfView.showLoading();
        mUserAndRestUseCase.execute(api, url, this);
    }

    public void profChange(String post_date, File file, String url) {
        mShowProfView.showLoading();
        mProfChangeUseCase.execute(post_date, file, url, this);
    }

    public void postDelete(String post_id, int position) {
        mPostDeleteUseCase.execute(post_id, position, this);
    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData mUserdata, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowProfView.hideLoading();
        mShowProfView.hideNoResultCase();
        mShowProfView.showResult(api, mUserdata, mPostData, post_ids);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData mUserData) {
        mShowProfView.hideLoading();
        mShowProfView.showNoResultCase(api, mUserData);
    }

    @Override
    public void onPostDeleted(int position) {
        mShowProfView.postDeleted(position);
    }

    @Override
    public void onPostDeleteFailed() {
        mShowProfView.postDeleteFailed();
    }

    @Override
    public void onProfChanged(String userName, String profile_img) {
        mShowProfView.hideLoading();
        mShowProfView.profChanged(userName, profile_img);
    }

    @Override
    public void onProfChangeFailed(String message) {
        mShowProfView.hideLoading();
        mShowProfView.profChangeFailed(message);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowProfView.hideLoading();
        mShowProfView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowProfView.hideLoading();
        mShowProfView.showNoResultCausedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserAndRestUseCase.setCallback(this);
        mProfChangeUseCase.setCallback(this);
        mPostDeleteUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserAndRestUseCase.removeCallback();
        mProfChangeUseCase.removeCallback();
        mPostDeleteUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowProfView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api, HeaderData mUserData);

        void hideNoResultCase();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, HeaderData mUserData, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void profChanged(String userName, String profile_img);

        void profChangeFailed(String message);

        void postDeleted(int position);

        void postDeleteFailed();
    }
}

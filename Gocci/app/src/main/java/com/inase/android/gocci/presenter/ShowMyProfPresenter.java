package com.inase.android.gocci.presenter;

import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.inase.android.gocci.domain.usecase.PostDeleteUseCase;
import com.inase.android.gocci.domain.usecase.ProfChangeUseCase;
import com.inase.android.gocci.domain.usecase.ProfUseCase;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ShowMyProfPresenter extends Presenter implements ProfUseCase.ProfUseCaseCallback,
        ProfChangeUseCase.ProfChangeUseCaseCallback, PostDeleteUseCase.PostDeleteUseCaseCallback {

    private ProfUseCase mProfUseCase;
    private ProfChangeUseCase mProfChangeUseCase;
    private PostDeleteUseCase mPostDeleteUseCase;
    private ShowProfView mShowProfView;

    public ShowMyProfPresenter(ProfUseCase profUseCase, ProfChangeUseCase profChangeUseCase, PostDeleteUseCase postDeleteUseCase) {
        mProfUseCase = profUseCase;
        mProfChangeUseCase = profChangeUseCase;
        mPostDeleteUseCase = postDeleteUseCase;
    }

    public void setProfView(ShowProfView view) {
        mShowProfView = view;
    }

    public void getProfData(int api, String url) {
        mShowProfView.showLoading();
        mProfUseCase.execute(api, url, this);
    }

    public void profChange(String post_date, File file, String url) {
        mShowProfView.showLoading();
        mProfChangeUseCase.execute(post_date, file, url, this);
    }

    public void postDelete(String post_id, int position) {
        mPostDeleteUseCase.execute(post_id, position, this);
    }

    @Override
    public void onProfLoaded(int api, HeaderData mUserdata, ArrayList<PostData> mPostData) {
        mShowProfView.hideLoading();
        mShowProfView.hideNoResultCase();
        mShowProfView.hideError();
        mShowProfView.showResult(api, mUserdata, mPostData);
    }

    @Override
    public void onProfEmpty(int api, HeaderData mUserData) {
        mShowProfView.hideLoading();
        mShowProfView.hideError();
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
        mShowProfView.hideLoading();
        mShowProfView.hideNoResultCase();
        mShowProfView.showError();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mProfUseCase.setCallback(this);
        mProfChangeUseCase.setCallback(this);
        mPostDeleteUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mProfUseCase.removeCallback();
        mProfChangeUseCase.removeCallback();
        mPostDeleteUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowProfView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(int api, HeaderData mUserData);

        void hideNoResultCase();

        void showError();

        void hideError();

        void showResult(int api, HeaderData mUserData, ArrayList<PostData> mPostData);

        void profChanged(String userName, String profile_img);

        void profChangeFailed(String message);

        void postDeleted(int position);

        void postDeleteFailed();
    }
}

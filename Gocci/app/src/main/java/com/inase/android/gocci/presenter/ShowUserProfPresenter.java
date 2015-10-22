package com.inase.android.gocci.presenter;

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

    public void getProfData(int api, String url) {
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
    public void onDataLoaded(int api, HeaderData mUserdata, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.hideNoResultCase();
        mShowUserProfView.showResult(api, mUserdata, mPostData, post_ids);
    }

    @Override
    public void onDataEmpty(int api, HeaderData mUserData) {
        mShowUserProfView.hideLoading();
        mShowUserProfView.showNoResultCase(api, mUserData);
    }

    @Override
    public void onError() {
        mShowUserProfView.hideLoading();
        mShowUserProfView.hideNoResultCase();
        mShowUserProfView.showError();
    }

    public interface ShowUserProfView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(int api, HeaderData userData);

        void hideNoResultCase();

        void showError();

        void showResult(int api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids);
    }
}

package com.inase.android.gocci.presenter;

import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/04.
 */
public class ShowRestPagePresenter extends Presenter implements UserAndRestUseCase.UserAndRestUseCaseCallback {
    private UserAndRestUseCase mUserAndRestUseCase;
    private ShowRestView mShowRestView;

    public ShowRestPagePresenter(UserAndRestUseCase userAndRestUseCase) {
        mUserAndRestUseCase = userAndRestUseCase;
    }

    public void setRestView(ShowRestView view) {
        mShowRestView = view;
    }

    public void getRestData(int api, String url) {
        mShowRestView.showLoading();
        mUserAndRestUseCase.execute(api, url, this);
    }

    @Override
    public void onDataLoaded(int api, HeaderData mUserdata, ArrayList<PostData> mPostData) {
        mShowRestView.hideLoading();
        mShowRestView.hideNoResultCase();
        mShowRestView.hideError();
        mShowRestView.showResult(api, mUserdata, mPostData);
    }

    @Override
    public void onDataEmpty(int api, HeaderData mUserData) {
        mShowRestView.hideLoading();
        mShowRestView.hideError();
        mShowRestView.showNoResultCase(api, mUserData);
    }

    @Override
    public void onError() {
        mShowRestView.hideLoading();
        mShowRestView.hideNoResultCase();
        mShowRestView.showError();
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

    public interface ShowRestView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(int api, HeaderData mRestData);

        void hideNoResultCase();

        void showError();

        void hideError();

        void showResult(int api, HeaderData mRestData, ArrayList<PostData> mPostData);
    }
}

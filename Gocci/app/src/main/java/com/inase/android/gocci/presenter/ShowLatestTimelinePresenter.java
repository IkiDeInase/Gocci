package com.inase.android.gocci.presenter;

import com.inase.android.gocci.domain.model.pojo.PostData;
import com.inase.android.gocci.domain.usecase.LatestTimelineUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class ShowLatestTimelinePresenter extends Presenter implements LatestTimelineUseCase.LatestTimelineUseCaseCallback {

    private LatestTimelineUseCase mLatestTimelineUseCase;
    private ShowLatestTimelineView mShowLatestTimelineView;

    public ShowLatestTimelinePresenter(LatestTimelineUseCase latestTimelineUseCase) {
        mLatestTimelineUseCase = latestTimelineUseCase;
    }

    public void setLatestTimelineView(ShowLatestTimelineView view) {
        mShowLatestTimelineView = view;
    }

    public void getLatestTimelinePostData(int api, String url) {
        mShowLatestTimelineView.showLoading();
        mLatestTimelineUseCase.execute(api, url, this);
    }

    @Override
    public void onLatestTimelineLoaded(int api, ArrayList<PostData> mPostData) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideNoResultCase();
        mShowLatestTimelineView.hideError();
        mShowLatestTimelineView.showResult(api, mPostData);
    }

    @Override
    public void onLatestTimelineEmpty() {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideError();
        mShowLatestTimelineView.showNoResultCase();
    }

    @Override
    public void onError() {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideNoResultCase();
        mShowLatestTimelineView.showError();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mLatestTimelineUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mLatestTimelineUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowLatestTimelineView {
        void showLoading();

        void hideLoading();

        void showNoResultCase();

        void hideNoResultCase();

        void showError();

        void hideError();

        void showResult(int api, ArrayList<PostData> mPostData);

        void successGochi(int position);
    }
}

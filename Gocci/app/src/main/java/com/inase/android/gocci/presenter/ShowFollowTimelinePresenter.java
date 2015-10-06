package com.inase.android.gocci.presenter;

import com.inase.android.gocci.domain.model.pojo.PostData;
import com.inase.android.gocci.domain.usecase.FollowTimelineUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ShowFollowTimelinePresenter extends Presenter implements FollowTimelineUseCase.FollowTimelineUseCaseCallback {
    private FollowTimelineUseCase mFollowTimelineUseCase;
    private ShowFollowTimelineView mShowFollowTimelineView;

    public ShowFollowTimelinePresenter(FollowTimelineUseCase followTimelineUseCase) {
        mFollowTimelineUseCase = followTimelineUseCase;
    }

    public void setFollowTimelineView(ShowFollowTimelineView view) {
        mShowFollowTimelineView = view;
    }

    public void getFollowTimelinePostData(int api, String url) {
        mShowFollowTimelineView.showLoading();
        mFollowTimelineUseCase.execute(api, url, this);
    }

    @Override
    public void onFollowTimelineLoaded(int api, ArrayList<PostData> mPostData) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.hideNoResultCase();
        mShowFollowTimelineView.hideError();
        mShowFollowTimelineView.showResult(api, mPostData);
    }

    @Override
    public void onFollowTimelineEmpty() {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.hideError();
        mShowFollowTimelineView.showNoResultCase();
    }

    @Override
    public void onError() {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.hideNoResultCase();
        mShowFollowTimelineView.showError();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mFollowTimelineUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mFollowTimelineUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowFollowTimelineView {
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

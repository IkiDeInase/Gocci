package com.inase.android.gocci.presenter;

import com.inase.android.gocci.domain.model.PostData;
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
    public void onFollowTimelineLoaded(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.hideNoResultCase();
        mShowFollowTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onFollowTimelineEmpty(int api) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.showNoResultCase(api);
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

        void showNoResultCase(int api);

        void hideNoResultCase();

        void showError();

        void showResult(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void successGochi(int position);
    }
}

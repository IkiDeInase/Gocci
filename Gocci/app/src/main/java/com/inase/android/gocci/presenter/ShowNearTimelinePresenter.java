package com.inase.android.gocci.presenter;

import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.TimelineNearUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/11.
 */
public class ShowNearTimelinePresenter extends Presenter implements TimelineNearUseCase.NearTimelineUseCaseCallback {

    private TimelineNearUseCase mTimelineNearUseCase;
    private ShowNearTimelineView mShowLatestTimelineView;

    public ShowNearTimelinePresenter(TimelineNearUseCase timelineNearUseCase) {
        mTimelineNearUseCase = timelineNearUseCase;
    }

    public void setNearTimelineView(ShowNearTimelineView view) {
        mShowLatestTimelineView = view;
    }

    public void getNearTimelinePostData(int api, String url) {
        mShowLatestTimelineView.showLoading();
        mTimelineNearUseCase.execute(api, url, this);
    }

    @Override
    public void onNearTimelineLoaded(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideNoResultCase();
        mShowLatestTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onNearTimelineEmpty(int api) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.showNoResultCase(api);
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
        mTimelineNearUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineNearUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowNearTimelineView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(int api);

        void hideNoResultCase();

        void showError();

        void showResult(int api, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void successGochi(int position);
    }
}

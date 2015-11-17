package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
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

    public void getNearTimelinePostData(Const.APICategory api, String url) {
        mShowLatestTimelineView.showLoading();
        mTimelineNearUseCase.execute(api, url, this);
    }

    @Override
    public void onNearTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideNoResultCase();
        mShowLatestTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onNearTimelineEmpty(Const.APICategory api) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.showNoResultCase(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.showNoResultCausedByGlobalError(api, globalCode);
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

        void showNoResultCase(Const.APICategory api);

        void hideNoResultCase();

        void showResult(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids);

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);
    }
}

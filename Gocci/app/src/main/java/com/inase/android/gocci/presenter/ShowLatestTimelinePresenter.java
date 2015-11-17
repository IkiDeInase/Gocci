package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.TimelineLatestUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class ShowLatestTimelinePresenter extends Presenter implements TimelineLatestUseCase.LatestTimelineUseCaseCallback {

    private TimelineLatestUseCase mTimelineLatestUseCase;
    private ShowLatestTimelineView mShowLatestTimelineView;

    public ShowLatestTimelinePresenter(TimelineLatestUseCase timelineLatestUseCase) {
        mTimelineLatestUseCase = timelineLatestUseCase;
    }

    public void setLatestTimelineView(ShowLatestTimelineView view) {
        mShowLatestTimelineView = view;
    }

    public void getLatestTimelinePostData(Const.APICategory api, String url) {
        mShowLatestTimelineView.showLoading();
        mTimelineLatestUseCase.execute(api, url, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mTimelineLatestUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineLatestUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onLatestTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideNoResultCase();
        mShowLatestTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onLatestTimelineEmpty(Const.APICategory api) {
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

    public interface ShowLatestTimelineView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api);

        void hideNoResultCase();

        void showResult(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids);

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);
    }
}

package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.TimelineFollowUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ShowFollowTimelinePresenter extends Presenter implements TimelineFollowUseCase.FollowTimelineUseCaseCallback {
    private TimelineFollowUseCase mTimelineFollowUseCase;
    private ShowFollowTimelineView mShowFollowTimelineView;

    public ShowFollowTimelinePresenter(TimelineFollowUseCase timelineFollowUseCase) {
        mTimelineFollowUseCase = timelineFollowUseCase;
    }

    public void setFollowTimelineView(ShowFollowTimelineView view) {
        mShowFollowTimelineView = view;
    }

    public void getFollowTimelinePostData(Const.APICategory api, String url) {
        mShowFollowTimelineView.showLoading();
        mTimelineFollowUseCase.execute(api, url, this);
    }

    @Override
    public void onFollowTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.hideNoResultCase();
        mShowFollowTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onFollowTimelineEmpty(Const.APICategory api) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.showNoResultCase(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.showNoResultCausedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mTimelineFollowUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineFollowUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowFollowTimelineView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api);

        void hideNoResultCase();

        void showResult(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids);

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);
    }
}

package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.TimelineNearUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/11.
 */
public class ShowNearTimelinePresenter extends Presenter implements TimelineNearUseCase.NearTimelineUseCaseCallback, GochiUseCase.GochiUseCaseCallback {

    private TimelineNearUseCase mTimelineNearUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowNearTimelineView mShowLatestTimelineView;

    public ShowNearTimelinePresenter(TimelineNearUseCase timelineNearUseCase, GochiUseCase gochiUseCase) {
        mTimelineNearUseCase = timelineNearUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setNearTimelineView(ShowNearTimelineView view) {
        mShowLatestTimelineView = view;
    }

    public void getNearTimelinePostData(Const.APICategory api, String url) {
        mShowLatestTimelineView.showLoading();
        mTimelineNearUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onNearTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.hideEmpty();
        mShowLatestTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onNearTimelineEmpty(Const.APICategory api) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.showEmpty(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowLatestTimelineView.hideLoading();
        mShowLatestTimelineView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mTimelineNearUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineNearUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowLatestTimelineView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowLatestTimelineView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowLatestTimelineView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowNearTimelineView {
        void showLoading();

        void hideLoading();

        void showEmpty(Const.APICategory api);

        void hideEmpty();

        void showResult(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids);

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void gochiSuccess(Const.APICategory api, String post_id);

        void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);

        void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);
    }
}

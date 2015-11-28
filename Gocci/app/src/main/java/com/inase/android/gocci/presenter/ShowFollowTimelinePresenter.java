package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.TimelineFollowUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class ShowFollowTimelinePresenter extends Presenter implements TimelineFollowUseCase.FollowTimelineUseCaseCallback, GochiUseCase.GochiUseCaseCallback {
    private TimelineFollowUseCase mTimelineFollowUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowFollowTimelineView mShowFollowTimelineView;

    public ShowFollowTimelinePresenter(TimelineFollowUseCase timelineFollowUseCase, GochiUseCase gochiUseCase) {
        mTimelineFollowUseCase = timelineFollowUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setFollowTimelineView(ShowFollowTimelineView view) {
        mShowFollowTimelineView = view;
    }

    public void getFollowTimelinePostData(Const.APICategory api, String url) {
        mShowFollowTimelineView.showLoading();
        mTimelineFollowUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onFollowTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.hideEmpty();
        mShowFollowTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onFollowTimelineEmpty(Const.APICategory api) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.showEmpty(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowFollowTimelineView.hideLoading();
        mShowFollowTimelineView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mTimelineFollowUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineFollowUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowFollowTimelineView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowFollowTimelineView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowFollowTimelineView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowFollowTimelineView {
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

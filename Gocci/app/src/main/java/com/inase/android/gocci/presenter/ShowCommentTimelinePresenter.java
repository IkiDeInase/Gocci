package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.TimelineCommentUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/12/28.
 */
public class ShowCommentTimelinePresenter extends Presenter implements TimelineCommentUseCase.CommentTimelineUseCaseCallback, GochiUseCase.GochiUseCaseCallback {
    private TimelineCommentUseCase mTimelineCommentUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowCommentTimelineView mShowCommentTimelineView;

    public ShowCommentTimelinePresenter(TimelineCommentUseCase timelineCommentUseCase, GochiUseCase gochiUseCase) {
        mTimelineCommentUseCase = timelineCommentUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setCommentTimelineView(ShowCommentTimelineView view) {
        mShowCommentTimelineView = view;
    }

    public void getCommentTimelinePostData(Const.APICategory api, String url) {
        mShowCommentTimelineView.showLoading();
        mTimelineCommentUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onCommentTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowCommentTimelineView.hideLoading();
        mShowCommentTimelineView.hideEmpty();
        mShowCommentTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onCommentTimelineEmpty(Const.APICategory api) {
        mShowCommentTimelineView.hideLoading();
        mShowCommentTimelineView.showEmpty(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowCommentTimelineView.hideLoading();
        mShowCommentTimelineView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowCommentTimelineView.hideLoading();
        mShowCommentTimelineView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mTimelineCommentUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineCommentUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowCommentTimelineView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowCommentTimelineView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowCommentTimelineView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowCommentTimelineView {
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

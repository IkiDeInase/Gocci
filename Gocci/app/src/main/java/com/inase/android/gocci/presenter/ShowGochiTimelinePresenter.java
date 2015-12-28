package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.TwoCellData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.TimelineGochiUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/12/28.
 */
public class ShowGochiTimelinePresenter extends Presenter implements TimelineGochiUseCase.GochiTimelineUseCaseCallback, GochiUseCase.GochiUseCaseCallback {
    private TimelineGochiUseCase mTimelineGochiUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowGochiTimelineView mShowGochiTimelineView;

    public ShowGochiTimelinePresenter(TimelineGochiUseCase timelineGochiUseCase, GochiUseCase gochiUseCase) {
        mTimelineGochiUseCase = timelineGochiUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setGochiTimelineView(ShowGochiTimelineView view) {
        mShowGochiTimelineView = view;
    }

    public void getGochiTimelinePostData(Const.APICategory api, String url) {
        mShowGochiTimelineView.showLoading();
        mTimelineGochiUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onGochiTimelineLoaded(Const.APICategory api, ArrayList<TwoCellData> mPostData, ArrayList<String> post_ids) {
        mShowGochiTimelineView.hideLoading();
        mShowGochiTimelineView.hideEmpty();
        mShowGochiTimelineView.showResult(api, mPostData, post_ids);
    }

    @Override
    public void onGochiTimelineEmpty(Const.APICategory api) {
        mShowGochiTimelineView.hideLoading();
        mShowGochiTimelineView.showEmpty(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowGochiTimelineView.hideLoading();
        mShowGochiTimelineView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowGochiTimelineView.hideLoading();
        mShowGochiTimelineView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mTimelineGochiUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mTimelineGochiUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowGochiTimelineView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowGochiTimelineView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowGochiTimelineView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowGochiTimelineView {
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

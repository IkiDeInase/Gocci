package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.usecase.NoticeDataUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ShowNoticePresenter extends Presenter implements NoticeDataUseCase.NoticeDataUseCaseCallback {

    private NoticeDataUseCase mNoticeDataUseCase;
    private ShowNoticeView mShowNoticeView;

    public ShowNoticePresenter(NoticeDataUseCase noticeUseCase) {
        mNoticeDataUseCase = noticeUseCase;
    }

    public void setNoticeView(ShowNoticeView view) {
        mShowNoticeView = view;
    }

    public void getNoticeData(Const.APICategory api, String url) {
        mShowNoticeView.showLoading();
        mNoticeDataUseCase.execute(api, url, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mNoticeDataUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mNoticeDataUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onSuccess(Const.APICategory api, ArrayList<HeaderData> list) {
        mShowNoticeView.hideLoading();
        mShowNoticeView.hideNoResultCase();
        mShowNoticeView.showResult(api, list);
    }

    @Override
    public void onEmpty(Const.APICategory api) {
        mShowNoticeView.hideLoading();
        mShowNoticeView.showNoResultCase(api);
    }

    @Override
    public void onFailureCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowNoticeView.hideLoading();
        mShowNoticeView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowNoticeView.hideLoading();
        mShowNoticeView.showNoResultCausedByGlobalError(api, globalCode);
    }

    public interface ShowNoticeView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api);

        void hideNoResultCase();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, ArrayList<HeaderData> list);
    }
}

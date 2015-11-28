package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.domain.usecase.FollowUseCase;
import com.inase.android.gocci.domain.usecase.ListGetUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ShowListPresenter extends Presenter implements ListGetUseCase.ListGetUseCaseCallback, FollowUseCase.FollowUseCaseCallback {

    private ListGetUseCase mListGetUseCase;
    private FollowUseCase mFollowUseCase;
    private ShowListGetView mShowListGetView;

    public ShowListPresenter(ListGetUseCase listGetUseCase, FollowUseCase followUseCase) {
        mListGetUseCase = listGetUseCase;
        mFollowUseCase = followUseCase;
    }

    public void setListView(ShowListGetView view) {
        mShowListGetView = view;
    }

    public void getListData(Const.APICategory api, String url) {
        mShowListGetView.showLoading();
        mListGetUseCase.execute(api, url, this);
    }

    public void postFollow(Const.APICategory api, String url, String user_id) {
        mFollowUseCase.execute(api, url, user_id, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mListGetUseCase.setCallback(this);
        mFollowUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mListGetUseCase.removeCallback();
        mFollowUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onLoaded(Const.APICategory api, ArrayList<ListGetData> list) {
        mShowListGetView.hideLoading();
        mShowListGetView.hideEmpty();
        mShowListGetView.showResult(api, list);
    }

    @Override
    public void onEmpty(Const.APICategory api) {
        mShowListGetView.hideLoading();
        mShowListGetView.showEmpty(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowListGetView.hideLoading();
        mShowListGetView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowListGetView.hideLoading();
        mShowListGetView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void onFollowPosted(Const.APICategory api, String user_id) {
        mShowListGetView.followSuccess(api, user_id);
    }

    @Override
    public void onFollowCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        mShowListGetView.followFailureCausedByLocalError(api, errorMessage, user_id);
    }

    @Override
    public void onFollowCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        mShowListGetView.followFailureCausedByGlobalError(api, globalCode, user_id);
    }

    public interface ShowListGetView {
        void showLoading();

        void hideLoading();

        void showEmpty(Const.APICategory api);

        void hideEmpty();

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void followSuccess(Const.APICategory api, String user_id);

        void followFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id);

        void followFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id);

        void showResult(Const.APICategory api, ArrayList<ListGetData> list);
    }
}

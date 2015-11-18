package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.domain.usecase.ListGetUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class ShowListPresenter extends Presenter implements ListGetUseCase.ListGetUseCaseCallback {

    private ListGetUseCase mListGetUseCase;
    private ShowListGetView mShowListGetView;

    public ShowListPresenter(ListGetUseCase listGetUseCase) {
        mListGetUseCase = listGetUseCase;
    }

    public void setListView(ShowListGetView view) {
        mShowListGetView = view;
    }

    public void getListData(Const.APICategory api, String url) {
        mShowListGetView.showLoading();
        mListGetUseCase.execute(api, url, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mListGetUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mListGetUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onLoaded(Const.APICategory api, ArrayList<ListGetData> list) {
        mShowListGetView.hideLoading();
        mShowListGetView.hideNoResultCase();
        mShowListGetView.showResult(api, list);
    }

    @Override
    public void onEmpty(Const.APICategory api) {
        mShowListGetView.hideLoading();
        mShowListGetView.showNoResultCase(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowListGetView.hideLoading();
        mShowListGetView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowListGetView.hideLoading();
        mShowListGetView.showNoResultCausedByGlobalError(api, globalCode);
    }

    public interface ShowListGetView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api);

        void hideNoResultCase();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, ArrayList<ListGetData> list);
    }
}

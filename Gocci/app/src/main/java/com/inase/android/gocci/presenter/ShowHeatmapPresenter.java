package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.usecase.HeatmapUseCase;
import com.inase.android.gocci.utils.map.HeatmapLog;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/05.
 */
public class ShowHeatmapPresenter extends Presenter implements HeatmapUseCase.HeatmapUseCaseCallback {

    private HeatmapUseCase mHeatmapUseCase;
    private ShowHeatmapView mShowHeatmapView;

    public ShowHeatmapPresenter(HeatmapUseCase heatmapUseCase) {
        mHeatmapUseCase = heatmapUseCase;
    }

    public void setHeatmapView(ShowHeatmapView view) {
        mShowHeatmapView = view;
    }

    public void getHeatmapData(Const.APICategory api, String url) {
        mShowHeatmapView.showLoading();
        mHeatmapUseCase.execute(api, url, this);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mHeatmapUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mHeatmapUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onHeatmapLoaded(Const.APICategory api, ArrayList<HeatmapLog> data) {
        mShowHeatmapView.hideLoading();
        mShowHeatmapView.showResult(api, data);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowHeatmapView.hideLoading();
        mShowHeatmapView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowHeatmapView.hideLoading();
        mShowHeatmapView.showNoResultCausedByGlobalError(api, globalCode);
    }

    public interface ShowHeatmapView {
        void showLoading();

        void hideLoading();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, ArrayList<HeatmapLog> data);
    }
}

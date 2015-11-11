package com.inase.android.gocci.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.inase.android.gocci.domain.usecase.HeatmapUseCase;

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

    public void getHeatmapData(String url) {
        mShowHeatmapView.showLoading();
        mHeatmapUseCase.execute(url, this);
    }

    @Override
    public void onHeatmapLoaded(ArrayList<LatLng> heatData) {
        mShowHeatmapView.hideLoading();
        mShowHeatmapView.showResult(heatData);
    }

    @Override
    public void onError() {
        mShowHeatmapView.hideLoading();
        mShowHeatmapView.showError();
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

    public interface ShowHeatmapView {
        void showLoading();

        void hideLoading();

        void showError();

        void showResult(ArrayList<LatLng> heatData);
    }
}

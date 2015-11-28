package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.usecase.NearDataUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/25.
 */
public class ShowCameraPresenter extends Presenter implements NearDataUseCase.NearDataUseCaseCallback {

    private NearDataUseCase mNearDataUseCase;
    private ShowCameraView mShowCameraView;

    public ShowCameraPresenter(NearDataUseCase nearUseCase) {
        mNearDataUseCase = nearUseCase;
    }

    public void setCameraView(ShowCameraView view) {
        mShowCameraView = view;
    }

    public void getNearData(Const.APICategory api, String url) {
        mNearDataUseCase.execute(api, url, this);
    }

    @Override
    public void onLoaded(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray) {
        mShowCameraView.hideNoResultCase();
        mShowCameraView.showResult(api, restnames, restIdArray, restnameArray);
    }

    @Override
    public void onEmpty(Const.APICategory api) {
        mShowCameraView.showNoResultCase(api);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowCameraView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowCameraView.showNoResultCausedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mNearDataUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mNearDataUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowCameraView {
        void showNoResultCase(Const.APICategory api);

        void hideNoResultCase();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, String[] restnames, ArrayList<String> restIdArray, ArrayList<String> restnameArray);
    }
}

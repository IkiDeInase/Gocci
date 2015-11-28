package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/04.
 */
public class ShowRestPagePresenter extends Presenter implements UserAndRestUseCase.UserAndRestUseCaseCallback, GochiUseCase.GochiUseCaseCallback {
    private UserAndRestUseCase mUserAndRestUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowRestView mShowRestView;

    public ShowRestPagePresenter(UserAndRestUseCase userAndRestUseCase, GochiUseCase gochiUseCase) {
        mUserAndRestUseCase = userAndRestUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setRestView(ShowRestView view) {
        mShowRestView = view;
    }

    public void getRestData(Const.APICategory api, String url) {
        mShowRestView.showLoading();
        mUserAndRestUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData mUserdata, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        mShowRestView.hideLoading();
        mShowRestView.hideEmpty();
        mShowRestView.showResult(api, mUserdata, mPostData, post_ids);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData mUserData) {
        mShowRestView.hideLoading();
        mShowRestView.showEmpty(api, mUserData);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowRestView.hideLoading();
        mShowRestView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowRestView.hideLoading();
        mShowRestView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mUserAndRestUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mUserAndRestUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowRestView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowRestView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowRestView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowRestView {
        void showLoading();

        void hideLoading();

        void showEmpty(Const.APICategory api, HeaderData mRestData);

        void hideEmpty();

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, HeaderData mRestData, ArrayList<PostData> mPostData, ArrayList<String> post_ids);

        void gochiSuccess(Const.APICategory api, String post_id);

        void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);

        void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);
    }
}

package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.PostPageUseCase;

/**
 * Created by kinagafuji on 16/01/18.
 */
public class ShowPostPagePresenter extends Presenter implements PostPageUseCase.PostPageUseCaseCallback, GochiUseCase.GochiUseCaseCallback {
    private PostPageUseCase mPostPageUseCase;
    private GochiUseCase mGochiUseCase;
    private ShowPostView mShowPostView;

    public ShowPostPagePresenter(PostPageUseCase postPageUseCase, GochiUseCase gochiUseCase) {
        mPostPageUseCase = postPageUseCase;
        mGochiUseCase = gochiUseCase;
    }

    public void setPostView(ShowPostView view) {
        mShowPostView = view;
    }

    public void getPostData(Const.APICategory api, String url) {
        mShowPostView.showLoading();
        mPostPageUseCase.execute(api, url, this);
    }

    public void postGochi(Const.APICategory api, String url, String post_id) {
        mGochiUseCase.execute(api, url, post_id, this);
    }

    @Override
    public void onPostLoaded(Const.APICategory api, PostData mPostData) {
        mShowPostView.hideLoading();
        mShowPostView.showResult(api, mPostData);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowPostView.hideLoading();
        mShowPostView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowPostView.hideLoading();
        mShowPostView.causedByGlobalError(api, globalCode);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mPostPageUseCase.setCallback(this);
        mGochiUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mPostPageUseCase.removeCallback();
        mGochiUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onGochiPosted(Const.APICategory api, String post_id) {
        mShowPostView.gochiSuccess(api, post_id);
    }

    @Override
    public void onGochiCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        mShowPostView.gochiFailureCausedByLocalError(api, errorMessage, post_id);
    }

    @Override
    public void onGochiCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        mShowPostView.gochiFailureCausedByGlobalError(api, globalCode, post_id);
    }

    public interface ShowPostView {
        void showLoading();

        void hideLoading();

        void showResult(Const.APICategory api, PostData mPostData);

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void gochiSuccess(Const.APICategory api, String post_id);

        void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id);

        void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id);
    }
}

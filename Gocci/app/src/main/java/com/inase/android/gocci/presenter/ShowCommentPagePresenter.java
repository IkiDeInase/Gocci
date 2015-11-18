package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.CommentPageUseCase;
import com.inase.android.gocci.domain.usecase.CommentPostUseCase;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/10/06.
 */
public class ShowCommentPagePresenter extends Presenter implements CommentPageUseCase.CommentPageUseCaseCallback,
        CommentPostUseCase.CommentPostUseCaseCallback {
    private CommentPageUseCase mCommentPageUseCase;
    private CommentPostUseCase mCommentPostUseCase;
    private ShowCommentView mShowCommentView;

    public ShowCommentPagePresenter(CommentPageUseCase commentPageUseCase, CommentPostUseCase commentPostUseCase) {
        mCommentPageUseCase = commentPageUseCase;
        mCommentPostUseCase = commentPostUseCase;
    }

    public void setCommentView(ShowCommentView view) {
        mShowCommentView = view;
    }

    public void getCommentData(Const.APICategory api, String url) {
        mShowCommentView.showLoading();
        mCommentPageUseCase.execute(api, url, this);
    }

    public void postComment(String postUrl, String getUrl) {
        mShowCommentView.showLoading();
        mCommentPostUseCase.execute(postUrl, getUrl, this);
    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData) {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.showResult(api, memoData, commentData);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData memoData) {
        mShowCommentView.hideLoading();
        mShowCommentView.showNoResultCase(api, memoData);
    }

    @Override
    public void onCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowCommentView.hideLoading();
        mShowCommentView.showNoResultCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowCommentView.hideLoading();
        mShowCommentView.showNoResultCausedByGlobalError(api, globalCode);
    }

    @Override
    public void onCommentPosted(PostData postData, ArrayList<HeaderData> commentData) {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.postCommented(null, commentData);
    }

    @Override
    public void onCommentPostEmpty(PostData postData) {
        mShowCommentView.hideLoading();
        mShowCommentView.postCommentEmpty(null);
    }

    @Override
    public void onCommentPostFailed() {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.postCommentFailed();
    }

    @Override
    public void onError() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void resume() {
        mCommentPageUseCase.setCallback(this);
        mCommentPostUseCase.setCallback(this);
    }

    @Override
    public void pause() {
        mCommentPageUseCase.removeCallback();
        mCommentPostUseCase.removeCallback();
    }

    @Override
    public void destroy() {

    }

    public interface ShowCommentView {
        void showLoading();

        void hideLoading();

        void showNoResultCase(Const.APICategory api, HeaderData postData);

        void hideNoResultCase();

        void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, HeaderData postData, ArrayList<HeaderData> commentData);

        void postCommented(HeaderData postData, ArrayList<HeaderData> commentData);

        void postCommentEmpty(HeaderData postData);

        void postCommentFailed();
    }
}

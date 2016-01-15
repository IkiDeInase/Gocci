package com.inase.android.gocci.presenter;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.domain.model.HeaderData;
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

    public void postComment(Const.APICategory api, String postUrl, String getUrl) {
        mShowCommentView.showLoading();
        mCommentPostUseCase.execute(api, postUrl, getUrl, this);
    }

    @Override
    public void onDataLoaded(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids) {
        mShowCommentView.hideLoading();
        mShowCommentView.hideEmpty();
        mShowCommentView.showResult(api, memoData, commentData, comment_ids);
    }

    @Override
    public void onDataEmpty(Const.APICategory api, HeaderData memoData) {
        mShowCommentView.hideLoading();
        mShowCommentView.showEmpty(api, memoData);
    }

    @Override
    public void onGetCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowCommentView.hideLoading();
        mShowCommentView.causedByLocalError(api, errorMessage);
    }

    @Override
    public void onGetCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowCommentView.hideLoading();
        mShowCommentView.causedByGlobalError(api, globalCode);
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

    @Override
    public void onCommentPosted(Const.APICategory api, HeaderData memoData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids) {
        mShowCommentView.hideLoading();
        mShowCommentView.hideEmpty();
        mShowCommentView.postCommented(api, memoData, commentData, comment_ids);
    }

    @Override
    public void onCommentPostEmpty(Const.APICategory api, HeaderData memoData) {
        mShowCommentView.hideLoading();
        mShowCommentView.postCommentEmpty(api, memoData);
    }

    @Override
    public void onPostFailureCausedByLocalError(Const.APICategory api, String errorMessage) {
        mShowCommentView.hideLoading();
        mShowCommentView.postFailureCausedByLocalError(api, errorMessage);
    }

    @Override
    public void onPostFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mShowCommentView.hideLoading();
        mShowCommentView.postFailureCausedByGlobalError(api, globalCode);
    }

    public interface ShowCommentView {
        void showLoading();

        void hideLoading();

        void showEmpty(Const.APICategory api, HeaderData postData);

        void hideEmpty();

        void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void causedByLocalError(Const.APICategory api, String errorMessage);

        void showResult(Const.APICategory api, HeaderData postData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids);

        void postCommented(Const.APICategory api, HeaderData postData, ArrayList<HeaderData> commentData, ArrayList<String> comment_ids);

        void postCommentEmpty(Const.APICategory api, HeaderData postData);

        void postFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode);

        void postFailureCausedByLocalError(Const.APICategory api, String errorMessage);
    }
}

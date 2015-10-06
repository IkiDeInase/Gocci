package com.inase.android.gocci.presenter;

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

    public void getCommentData(int api, String url) {
        mShowCommentView.showLoading();
        mCommentPageUseCase.execute(api, url, this);
    }

    public void postComment(String postUrl, String getUrl) {
        mShowCommentView.showLoading();
        mCommentPostUseCase.execute(postUrl, getUrl, this);
    }

    @Override
    public void onDataLoaded(int api, PostData postData, ArrayList<HeaderData> commentData) {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.showResult(api, postData, commentData);
    }

    @Override
    public void onDataEmpty(int api, PostData postData) {
        mShowCommentView.hideLoading();
        mShowCommentView.showNoResultCase(api, postData);
    }

    @Override
    public void onCommentPosted(PostData postData, ArrayList<HeaderData> commentData) {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.postCommented(postData, commentData);
    }

    @Override
    public void onCommentPostEmpty(PostData postData) {
        mShowCommentView.hideLoading();
        mShowCommentView.postCommentEmpty(postData);
    }

    @Override
    public void onCommentPostFailed() {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.postCommentFailed();
    }

    @Override
    public void onError() {
        mShowCommentView.hideLoading();
        mShowCommentView.hideNoResultCase();
        mShowCommentView.showError();
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

        void showNoResultCase(int api, PostData postData);

        void hideNoResultCase();

        void showError();

        void showResult(int api, PostData postData, ArrayList<HeaderData> comentData);

        void postCommented(PostData postData, ArrayList<HeaderData> comentData);

        void postCommentEmpty(PostData postData);

        void postCommentFailed();
    }
}

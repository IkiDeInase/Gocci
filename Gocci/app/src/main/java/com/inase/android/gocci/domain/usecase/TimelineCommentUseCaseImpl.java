package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.PostsDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.TwoCellData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/12/28.
 */
public class TimelineCommentUseCaseImpl extends UseCase2<Const.APICategory, String> implements TimelineCommentUseCase, PostsDataRepository.PostDataRepositoryCallback {
    private static TimelineCommentUseCaseImpl sUseCase;
    private final PostsDataRepository mPostsDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private CommentTimelineUseCaseCallback mCallback;

    public static TimelineCommentUseCaseImpl getUseCase(PostsDataRepository postsDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new TimelineCommentUseCaseImpl(postsDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public TimelineCommentUseCaseImpl(PostsDataRepository postsDataRepository, PostExecutionThread postExecutionThread) {
        mPostsDataRepository = postsDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, CommentTimelineUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mPostsDataRepository.getPostDataList(param1, param2, this);
    }

    @Override
    public void setCallback(CommentTimelineUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onPostDataLoaded(final Const.APICategory api, final ArrayList<TwoCellData> postData, final ArrayList<String> post_ids) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentTimelineLoaded(api, postData, post_ids);
                }
            }
        });
    }

    @Override
    public void onPostDataEmpty(final Const.APICategory api) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCommentTimelineEmpty(api);
                }
            }
        });
    }

    @Override
    public void onCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCausedByGlobalError(api, globalCode);
                }
            }
        });
    }
}

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.PostData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/29.
 */
public class TimelineFollowUseCaseImpl extends UseCase2<Integer, String> implements TimelineFollowUseCase, PostDataRepository.PostDataRepositoryCallback {
    private static TimelineFollowUseCaseImpl sUseCase;
    private final PostDataRepository mPostDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private TimelineFollowUseCase.FollowTimelineUseCaseCallback mCallback;

    public static TimelineFollowUseCaseImpl getUseCase(PostDataRepository postDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new TimelineFollowUseCaseImpl(postDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public TimelineFollowUseCaseImpl(PostDataRepository postDataRepository, PostExecutionThread postExecutionThread) {
        mPostDataRepository = postDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(int api, String url, TimelineFollowUseCase.FollowTimelineUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Integer param1, String param2) {
        mPostDataRepository.getPostDataList(param1, param2, this);
    }

    @Override
    public void setCallback(TimelineFollowUseCase.FollowTimelineUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onPostDataLoaded(final int api, final ArrayList<PostData> postData, final ArrayList<String> post_ids) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFollowTimelineLoaded(api, postData, post_ids);
                }
            }
        });
    }

    @Override
    public void onPostDataEmpty(final int api) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFollowTimelineEmpty(api);
                }
            }
        });
    }

    @Override
    public void onError() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onError();
                }
            }
        });
    }
}
package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.data.PostData;
import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/09/25.
 */
public class LatestTimelineUseCaseImpl extends UseCase2<Integer, String> implements LatestTimelineUseCase, PostDataRepository.PostDataRepositoryCallback {
    private static LatestTimelineUseCaseImpl sUseCase;
    private final PostDataRepository mPostDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private LatestTimelineUseCaseCallback mCallback;

    public static LatestTimelineUseCaseImpl getUseCase(PostDataRepository postDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new LatestTimelineUseCaseImpl(postDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public LatestTimelineUseCaseImpl(PostDataRepository postDataRepository, PostExecutionThread postExecutionThread) {
        mPostDataRepository = postDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(int api, String url, LatestTimelineUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    protected void call(Integer param1, String param2) {
        mPostDataRepository.getPostDataList(param1, param2, this);
    }

    @Override
    public void setCallback(LatestTimelineUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onPostDataLoaded(final int api, final ArrayList<PostData> postData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onLatestTimelineLoaded(api, postData);
                }
            }
        });
    }

    @Override
    public void onPostDataEmpty() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onLatestTimelineEmpty();
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

package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.MyPageActionRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

/**
 * Created by kinagafuji on 15/10/01.
 */
public class PostDeleteUseCaseImpl extends UseCase2<String, Integer> implements PostDeleteUseCase, MyPageActionRepository.MyPageActionRepositoryCallback {
    private static PostDeleteUseCaseImpl sUseCase;
    private final MyPageActionRepository mMyPageActionRepository;
    private PostExecutionThread mPostExecutionThread;
    private PostDeleteUseCaseCallback mCallback;

    public static PostDeleteUseCaseImpl getUseCase(MyPageActionRepository myPageActionRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new PostDeleteUseCaseImpl(myPageActionRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public PostDeleteUseCaseImpl(MyPageActionRepository myPageActionRepository, PostExecutionThread postExecutionThread) {
        mMyPageActionRepository = myPageActionRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onProfileChanged(String userName, String profile_img) {

    }

    @Override
    public void onProfileChangeFailed(String message) {

    }

    @Override
    public void onPostDeleted(final int position) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onPostDeleted(position);
                }
            }
        });
    }

    @Override
    public void onPostDeleteFailed() {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onPostDeleteFailed();
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

    @Override
    public void execute(String post_id, int position, PostDeleteUseCaseCallback callback) {
        mCallback = callback;
        this.start(post_id, position);
    }

    @Override
    public void setCallback(PostDeleteUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(String param1, Integer param2) {
        mMyPageActionRepository.deletePost(param1, param2, this);
    }
}

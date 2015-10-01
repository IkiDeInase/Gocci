package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.datasource.repository.MyPageActionRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;

import java.io.File;

/**
 * Created by kinagafuji on 15/10/01.
 */
public class ProfChangeUseCaseImpl extends UseCase3<String, File, String> implements ProfChangeUseCase, MyPageActionRepository.MyPageActionRepositoryCallback {
    private static ProfChangeUseCaseImpl sUseCase;
    private final MyPageActionRepository mMyPageActionRepository;
    private PostExecutionThread mPostExecutionThread;
    private ProfChangeUseCaseCallback mCallback;

    public static ProfChangeUseCaseImpl getUseCase(MyPageActionRepository myPageActionRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new ProfChangeUseCaseImpl(myPageActionRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public ProfChangeUseCaseImpl(MyPageActionRepository myPageActionRepository, PostExecutionThread postExecutionThread) {
        mMyPageActionRepository = myPageActionRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onProfileChanged(final String userName, final String profile_img) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onProfChanged(userName, profile_img);
                }
            }
        });
    }

    @Override
    public void onProfileChangeFailed(final String message) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onProfChangeFailed(message);
                }
            }
        });
    }

    @Override
    public void onPostDeleted(int position) {

    }

    @Override
    public void onPostDeleteFailed() {

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
    public void execute(String post_date, File file, String url, ProfChangeUseCaseCallback callback) {
        mCallback = callback;
        this.start(post_date, file, url);
    }

    @Override
    public void setCallback(ProfChangeUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(String param1, File param2, String param3) {
        mMyPageActionRepository.changeProfile(param1, param2, param3, this);
    }
}

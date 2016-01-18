package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.PostDataRepository;
import com.inase.android.gocci.datasource.repository.PostsDataRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.PostData;

/**
 * Created by kinagafuji on 16/01/18.
 */
public class PostPageUseCaseImpl extends UseCase2<Const.APICategory, String> implements PostPageUseCase, PostDataRepository.PostDataRepositoryCallback {
    private static PostPageUseCaseImpl sUseCase;
    private final PostDataRepository mPostDataRepository;
    private PostExecutionThread mPostExecutionThread;
    private PostPageUseCaseCallback mCallback;

    public static PostPageUseCaseImpl getUseCase(PostDataRepository postDataRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new PostPageUseCaseImpl(postDataRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public PostPageUseCaseImpl(PostDataRepository postDataRepository, PostExecutionThread postExecutionThread) {
        mPostDataRepository = postDataRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void onPostDataLoaded(final Const.APICategory api, final PostData postData) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onPostLoaded(api, postData);
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

    @Override
    public void execute(Const.APICategory api, String url, PostPageUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    public void setCallback(PostPageUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mPostDataRepository.getPostDataList(param1, param2, this);
    }
}

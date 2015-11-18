package com.inase.android.gocci.domain.usecase;

import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.datasource.repository.NoticeRepository;
import com.inase.android.gocci.domain.executor.PostExecutionThread;
import com.inase.android.gocci.domain.model.HeaderData;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/11/18.
 */
public class NoticeDataUseCaseImpl extends UseCase2<Const.APICategory, String> implements NoticeDataUseCase, NoticeRepository.NoticeRepositoryCallback {
    private static NoticeDataUseCaseImpl sUseCase;
    private final NoticeRepository mNoticeRepository;
    private PostExecutionThread mPostExecutionThread;
    private NoticeDataUseCaseCallback mCallback;

    public static NoticeDataUseCaseImpl getUseCase(NoticeRepository noticeRepository, PostExecutionThread postExecutionThread) {
        if (sUseCase == null) {
            sUseCase = new NoticeDataUseCaseImpl(noticeRepository, postExecutionThread);
        }
        return sUseCase;
    }

    public NoticeDataUseCaseImpl(NoticeRepository noticeRepository, PostExecutionThread postExecutionThread) {
        mNoticeRepository = noticeRepository;
        mPostExecutionThread = postExecutionThread;
    }

    @Override
    public void execute(Const.APICategory api, String url, NoticeDataUseCaseCallback callback) {
        mCallback = callback;
        this.start(api, url);
    }

    @Override
    public void setCallback(NoticeDataUseCaseCallback callback) {
        mCallback = callback;
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void onSuccess(final Const.APICategory api, final ArrayList<HeaderData> list) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onSuccess(api, list);
                }
            }
        });
    }

    @Override
    public void onEmpty(final Const.APICategory api) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onEmpty(api);
                }
            }
        });

    }

    @Override
    public void onFailureCausedByLocalError(final Const.APICategory api, final String errorMessage) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFailureCausedByLocalError(api, errorMessage);
                }
            }
        });
    }

    @Override
    public void onFailureCausedByGlobalError(final Const.APICategory api, final API3.Util.GlobalCode globalCode) {
        mPostExecutionThread.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFailureCausedByGlobalError(api, globalCode);
                }
            }
        });
    }

    @Override
    protected void call(Const.APICategory param1, String param2) {
        mNoticeRepository.getNotice(param1, param2, this);
    }
}

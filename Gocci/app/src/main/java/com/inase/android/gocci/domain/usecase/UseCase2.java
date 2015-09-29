package com.inase.android.gocci.domain.usecase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kinagafuji on 15/09/25.
 */
public abstract class UseCase2<T> {
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public void start(final T params) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                call(params);
            }
        });
    }

    abstract protected void call(T params);
}

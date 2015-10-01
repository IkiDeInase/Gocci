package com.inase.android.gocci.domain.usecase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kinagafuji on 15/09/25.
 */
public abstract class UseCase2<T, S> {
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public void start(final T param1, final S param2) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                call(param1, param2);
            }
        });
    }

    abstract protected void call(T param1, S param2);
}

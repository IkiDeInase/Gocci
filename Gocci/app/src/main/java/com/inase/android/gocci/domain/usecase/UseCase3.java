package com.inase.android.gocci.domain.usecase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kinagafuji on 15/10/01.
 */
public abstract class UseCase3<T, S, W> {
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public void start(final T param1, final S param2, final W param3) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                call(param1, param2, param3);
            }
        });
    }

    abstract protected void call(T param1, S param2, W param3);
}

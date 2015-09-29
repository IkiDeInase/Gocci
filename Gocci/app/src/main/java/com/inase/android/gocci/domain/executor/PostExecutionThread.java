package com.inase.android.gocci.domain.executor;

/**
 * Created by kinagafuji on 15/09/25.
 */
public interface PostExecutionThread {
    void post(Runnable runnable);
}

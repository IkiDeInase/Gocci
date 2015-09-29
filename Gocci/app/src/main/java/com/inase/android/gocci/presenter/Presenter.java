package com.inase.android.gocci.presenter;

/**
 * Created by kinagafuji on 15/09/25.
 */
public abstract class Presenter {
    public abstract void initialize();

    public abstract void resume();

    public abstract void pause();

    public abstract void destroy();
}

package com.inase.android.gocci.event;

import com.inase.android.gocci.consts.Const;

/**
 * Created by kinagafuji on 15/11/13.
 */
public class RetryApiEvent {
    public Const.APICategory api;

    public RetryApiEvent(Const.APICategory api) {
        super();
        this.api = api;
    }
}

package com.inase.android.gocci.event;

import com.inase.android.gocci.consts.Const;

/**
 * Created by kinagafuji on 15/11/27.
 */
public class PostCallbackEvent {
    public Const.PostCallback callback;
    public Const.ActivityCategory activityCategory;
    public Const.APICategory apiCategory;
    public String id;

    public PostCallbackEvent(Const.PostCallback callback, Const.ActivityCategory activityCategory, Const.APICategory apiCategory, String id) {
        super();
        this.callback = callback;
        this.activityCategory = activityCategory;
        this.apiCategory = apiCategory;
        this.id = id;
    }
}

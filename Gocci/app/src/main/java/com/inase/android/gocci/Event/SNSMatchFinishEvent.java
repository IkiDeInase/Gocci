package com.inase.android.gocci.Event;

/**
 * Created by kinagafuji on 15/08/04.
 */
public class SNSMatchFinishEvent {

    public String message;
    public String profile_img;

    public SNSMatchFinishEvent(String message, String profile_img) {
        super();
        this.message = message;
        this.profile_img = profile_img;
    }
}

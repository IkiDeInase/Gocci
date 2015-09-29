package com.inase.android.gocci.event;

/**
 * Created by kinagafuji on 15/05/15.
 */
public class NotificationNumberEvent {

    public int mNotificationNumber;
    public String mMessage;

    public NotificationNumberEvent(int number, String message) {
        super();
        this.mNotificationNumber = number;
        this.mMessage = message;
    }
}

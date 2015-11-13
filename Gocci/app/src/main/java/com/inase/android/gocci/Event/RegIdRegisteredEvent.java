package com.inase.android.gocci.event;

/**
 * Created by kinagafuji on 15/07/06.
 */
public class RegIdRegisteredEvent {

    //public String providerName; // facebook twitter username
    public String register_id;

    public RegIdRegisteredEvent(String register_id) {
        super();
        //this.providerName = providerName;
        this.register_id = register_id;
    }
}

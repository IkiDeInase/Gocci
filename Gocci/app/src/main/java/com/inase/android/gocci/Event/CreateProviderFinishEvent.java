package com.inase.android.gocci.Event;

/**
 * Created by kinagafuji on 15/07/06.
 */
public class CreateProviderFinishEvent {

    //public String providerName; // facebook twitter username
    public String identityId;

    public CreateProviderFinishEvent(/*String providerName, */String identityId) {
        super();
        //this.providerName = providerName;
        this.identityId = identityId;
    }
}

package com.inase.android.gocci.event;


public class BusHolder {

    private static MainThreadBus sBus = new MainThreadBus();

    public static MainThreadBus get() {
        return sBus;
    }
}

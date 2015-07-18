package com.inase.android.gocci.Event;


public class BusHolder {

    private static MainThreadBus sBus = new MainThreadBus();

    public static MainThreadBus get() {
        return sBus;
    }
}

package com.example.kinagafuji.gocci.Base;

import com.example.kinagafuji.gocci.data.UserData;

import java.util.ArrayList;

public class ArrayListGetEvent {
    public ArrayList<UserData> users;

    public ArrayListGetEvent(final ArrayList<UserData> users) {
        super();
        this.users = users;
    }
}

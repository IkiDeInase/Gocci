package com.inase.android.gocci.Event;

import com.inase.android.gocci.data.UserData;

import java.util.ArrayList;

public class ArrayListGetEvent {
    //検索画面の近くの３０件検索結果をタイムライン画面の赤丸画面に送るようにしている。
    //Otto使用
    public ArrayList<UserData> users = new ArrayList<>();

    public ArrayListGetEvent(final ArrayList<UserData> users) {
        super();
        this.users = users;
    }
}

package com.example.kinagafuji.gocci.Activity;

import android.util.Log;

import com.example.kinagafuji.gocci.Base.BusHolder;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginDispatchActivity;
import com.squareup.otto.Subscribe;

public class GocciDispatchActivity extends ParseLoginDispatchActivity {

    @Override
    protected Class<?> getTargetClass() {
        //ここの返り値クラスを自分のActivityに変更する
        return SlidingTabActivity.class;
    }

}

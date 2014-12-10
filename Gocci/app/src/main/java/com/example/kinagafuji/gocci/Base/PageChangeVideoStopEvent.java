package com.example.kinagafuji.gocci.Base;

import android.util.Log;

public class PageChangeVideoStopEvent {
    public int position;

    public PageChangeVideoStopEvent(int position) {
        super();
        //positionの位置によって場合分けをする。
        //positionは０からか１からか
        this.position = position;
        Log.e("Otto発動！", "position: " + position);
    }
}

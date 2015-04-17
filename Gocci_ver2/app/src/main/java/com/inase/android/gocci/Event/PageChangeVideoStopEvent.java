package com.inase.android.gocci.Event;


public class PageChangeVideoStopEvent {

    //タイムライン画面の動画の再生を止めるために、ページが切り替わったタイミングを送るようにしている。
    public int position;

    public PageChangeVideoStopEvent(int position) {
        super();
        //positionの位置によって場合分けをする。
        //positionは０からか１からか
        this.position = position;
    }
}

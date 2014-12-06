package com.example.kinagafuji.gocci.Base;


import android.support.v4.view.ViewPager;
import android.util.Log;

public class MyOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

    private static final String TAG = "OnPageChangeListener";
    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                //ページ移動完了
                Log.e(TAG, "onPageScrollStateChanged" +
                        " state: SCROLL_STATE_IDLE");
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                //ドラッグ終了
                Log.e(TAG, "onPageScrollStateChanged" +
                        " state: SCROLL_STATE_SETTLING");
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                //ドラッグ開始
                Log.e(TAG, "onPageScrollStateChanged" +
                        " state: SCROLL_STATE_DRAGGING");
                break;
            default:
                Log.e(TAG, "onPageScrollStateChanged" +
                        " state: default");
                break;
        }
    }
    @Override
    public void onPageScrolled(int position,
                               float positionOffset, int positionOffsetPixels) {
        //ドラッグ中
        Log.e(TAG, "onPageScrolled" +
                " position: " + position +
                ", positionOffset: " + positionOffset +
                ", positionOffsetPixels: " + positionOffsetPixels);
    }
    @Override
    public void onPageSelected(int position) {
        //ドラッグ終了　ページ選択時
        Log.e(TAG, "onPageSelected" +
                " position: " + position);
    }
}

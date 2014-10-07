package com.example.kinagafuji.gocci;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {


    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //Fragement for Android Tab
                return new TimelineFragment();
            case 1:
                //Fragment for Ios Tab
                return new Search_mapFragment();
            case 2:
                //Fragment for Windows Tab
                return new LifelogFragment();
            case 3:

                return new ProfileFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 4;
    }



}

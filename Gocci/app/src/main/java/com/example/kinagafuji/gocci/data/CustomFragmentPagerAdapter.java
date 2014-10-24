package com.example.kinagafuji.gocci.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kinagafuji.gocci.Fragment.LifelogFragment;
import com.example.kinagafuji.gocci.Fragment.ProfileFragment;
import com.example.kinagafuji.gocci.Fragment.Search_mapFragment;
import com.example.kinagafuji.gocci.Fragment.TimelineFragment;

public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TimelineFragment();
            case 1:
                return new Search_mapFragment();
            case 2:
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

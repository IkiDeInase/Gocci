package com.example.kinagafuji.gocci.Base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kinagafuji.gocci.Fragment.LifelogFragment;
import com.example.kinagafuji.gocci.Fragment.ProfileFragment;
import com.example.kinagafuji.gocci.Fragment.Search_mapFragment;
import com.example.kinagafuji.gocci.Fragment.TimelineFragment;
import com.example.kinagafuji.gocci.R;
import com.parse.ParseUser;

public class SlidingTabsBasicFragment extends BaseFragment {

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    private String pictureImageUrl;
    private String name;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.fragment_sample, container, false);

        ParseUser user = ParseUser.getCurrentUser();
        id = user.getString("id");
        name = user.getString("name");

        pictureImageUrl = "https://graph.facebook.com/" + id + "/picture";

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("name", name);
        editor.putString("pictureImageUrl", pictureImageUrl);

        editor.apply();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //mViewPager.setBackgroundColor(R.color.main_color);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new SamplePagerAdapter(getFragmentManager()));

        //mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setCustomTabView();
        mSlidingTabLayout.setDividerColors(android.R.color.transparent);
        //mSlidingTabLayout.setCustomTabColorizer();
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)

    }

    class SamplePagerAdapter extends FragmentPagerAdapter {


        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TimelineFragment().newIntent(name, pictureImageUrl);

                case 1:
                    return new Search_mapFragment().newIntent(name, pictureImageUrl);

                case 2:
                    return new LifelogFragment().newIntent(name, pictureImageUrl);

                case 3:
                    return new ProfileFragment().newIntent(name, pictureImageUrl);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

}

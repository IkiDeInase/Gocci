package com.example.kinagafuji.gocci.Activity;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.CustomFragmentPagerAdapter;

public class PagerTabStripActivity extends FragmentActivity {

    ViewPager mViewwPager;
    CustomFragmentPagerAdapter mCustomFragmentPagerAdapter;
    ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_tab_strip);

        mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayUseLogoEnabled(false);
        //mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        //mActionBar.setCustomView(R.layout.custom_actionbar);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        // ViewPager の Adapter
        mCustomFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());

        mViewwPager = (ViewPager) findViewById(R.id.pager);
        mViewwPager.setOffscreenPageLimit(1);
        mViewwPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mActionBar = getActionBar();

                mActionBar.setSelectedNavigationItem(position);
                Log.d("アクションバー", "通っております！");
            }
        });

        mViewwPager.setAdapter(mCustomFragmentPagerAdapter);
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewwPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub
            }
        };
        //Add New Tab
        /*mActionBar.addTab(mActionBar.newTab().setIcon(R.drawable.abc_ic_menu_share_holo_dark).setTabListener(tabListener));
        mActionBar.addTab(mActionBar.newTab().setIcon(R.drawable.abc_ic_search).setTabListener(tabListener));
        mActionBar.addTab(mActionBar.newTab().setIcon(R.drawable.abc_ic_clear).setTabListener(tabListener));
        mActionBar.addTab(mActionBar.newTab().setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark).setTabListener(tabListener));*/
    }



}

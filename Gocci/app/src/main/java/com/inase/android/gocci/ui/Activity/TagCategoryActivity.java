package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.andexert.library.RippleView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.ui.fragment.TagCategoryFragment;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.konifar.fab_transformation.FabTransformation;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class TagCategoryActivity extends AppCompatActivity {

    @Bind(R.id.tool_bar)
    Toolbar toolBar;
    @Bind(R.id.smart_tab)
    SmartTabLayout smartTab;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.overlay)
    View overlay;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.category_spinner)
    MaterialBetterSpinner categorySpinner;
    @Bind(R.id.value_spinner)
    MaterialBetterSpinner valueSpinner;
    @Bind(R.id.sort_spinner)
    MaterialBetterSpinner sortSpinner;
    @Bind(R.id.filter_ripple)
    RippleView filterRipple;
    @Bind(R.id.sheet)
    CardView sheet;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @OnClick(R.id.fab)
    public void click() {
        if (fab.getVisibility() == View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
        SmartLocation.with(TagCategoryActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                nowLocation = location;
            }
        });
    }

    @OnClick(R.id.overlay)
    public void clickOverlay() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
    }

    public static void startTagCategoryActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, TagCategoryActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static int mShowPosition = 0;
    public static int mWaSort_id = 0;
    public static int mYouSort_id = 0;
    public static int mTyuuSort_id = 0;
    public static int mCareSort_id = 0;
    public static int mRamenSort_id = 0;
    public static int mTakokuSort_id = 0;
    public static int mCafeSort_id = 0;
    public static int mSakeSort_id = 0;
    public static int mSonotaSort_id = 0;
    public static Location nowLocation;

    private String[] SORT;

    private Drawer result;

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GocciTimelineActivity activity
                    = (GocciTimelineActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_MYPAGE:
                    GocciMyprofActivity.startMyProfActivity(activity);
                    break;
                case Const.INTENT_TO_ADVICE:
                    Util.setAdviceDialog(activity);
                    break;
                case Const.INTENT_TO_SETTING:
                    SettingActivity.startSettingActivity(activity);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("気分から見つける");

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolBar)
                .withHeader(new DrawerProfHeader(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.timeline)).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(getString(R.string.mypage)).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.send_advice)).withIcon(GoogleMaterial.Icon.gmd_send).withSelectable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName(SavedData.getSettingMute(this) == 0 ? getString(R.string.setting_support_mute) : getString(R.string.setting_support_unmute)).withIcon(GoogleMaterial.Icon.gmd_volume_mute).withSelectable(false).withIdentifier(5),
                        new PrimaryDrawerItem().withName(getString(R.string.settings)).withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, TagCategoryActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, TagCategoryActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, TagCategoryActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(TagCategoryActivity.this)) {
                                    case 0:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(-1));
                                        SavedData.setSettingMute(TagCategoryActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(0));
                                        SavedData.setSettingMute(TagCategoryActivity.this, 0);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_mute)));
                                        break;
                                }
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(-1)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        finish();
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        return true;
                    }
                })
                .build();

        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.wa, TagCategoryFragment.class, new Bundler().putInt("key", 0).get())
                .add(R.string.you, TagCategoryFragment.class, new Bundler().putInt("key", 1).get())
                .add(R.string.tyuu, TagCategoryFragment.class, new Bundler().putInt("key", 2).get())
                .add(R.string.care, TagCategoryFragment.class, new Bundler().putInt("key", 3).get())
                .add(R.string.ramen, TagCategoryFragment.class, new Bundler().putInt("key", 4).get())
                .add(R.string.takoku, TagCategoryFragment.class, new Bundler().putInt("key", 5).get())
                .add(R.string.cafe, TagCategoryFragment.class, new Bundler().putInt("key", 6).get())
                .add(R.string.sake, TagCategoryFragment.class, new Bundler().putInt("key", 7).get())
                .add(R.string.sonota, TagCategoryFragment.class, new Bundler().putInt("key", 8).get())
                .create());

        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(5);

        smartTab.setViewPager(viewpager);
        smartTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BusHolder.get().post(new PageChangeVideoStopEvent(position));
                mShowPosition = position;
                switch (mShowPosition) {
                    case 0:
                        sortSpinner.setText(SORT[mWaSort_id]);
                        break;
                    case 1:
                        sortSpinner.setText(SORT[mYouSort_id]);
                        break;
                    case 2:
                        sortSpinner.setText(SORT[mTyuuSort_id]);
                        break;
                    case 3:
                        sortSpinner.setText(SORT[mCareSort_id]);
                        break;
                    case 4:
                        sortSpinner.setText(SORT[mRamenSort_id]);
                        break;
                    case 5:
                        sortSpinner.setText(SORT[mTakokuSort_id]);
                        break;
                    case 6:
                        sortSpinner.setText(SORT[mCafeSort_id]);
                        break;
                    case 7:
                        sortSpinner.setText(SORT[mSakeSort_id]);
                        break;
                    case 8:
                        sortSpinner.setText(SORT[mSonotaSort_id]);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) fab.hide();
                if (state == 0) fab.show();
            }
        });

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        String[] VALUE = getResources().getStringArray(R.array.list_value);
        SORT = getResources().getStringArray(R.array.list_sort);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        categorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //category_id = position + 1;
            }
        });
        ArrayAdapter<String> valueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, VALUE);
        valueSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //value_id = position + 1;
            }
        });
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, SORT);
        sortSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mShowPosition) {
                    case 0:
                        mWaSort_id = position;
                        break;
                    case 1:
                        mYouSort_id = position;
                        break;
                    case 2:
                        mTyuuSort_id = position;
                        break;
                    case 3:
                        mCareSort_id = position;
                        break;
                    case 4:
                        mRamenSort_id = position;
                        break;
                    case 5:
                        mTakokuSort_id = position;
                        break;
                    case 6:
                        mCafeSort_id = position;
                        break;
                    case 7:
                        mSakeSort_id = position;
                        break;
                    case 8:
                        mSonotaSort_id = position;
                        break;
                }
            }
        });
        categorySpinner.setAdapter(categoryAdapter);
        valueSpinner.setAdapter(valueAdapter);
        sortSpinner.setAdapter(sortAdapter);

        filterRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
                //Otto currentpageと絞り込みurl
                BusHolder.get().post(new FilterTimelineEvent(mShowPosition, Const.getCustomTagCategoryAPI(mShowPosition + 2, getPositionSortId(),
                        nowLocation.getLongitude(), nowLocation.getLatitude(), 0)));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    public void onUserClicked(int user_id, String username) {
        FlexibleUserProfActivity.startUserProfActivity(user_id, username, TagCategoryActivity.this);
    }

    public void onTenpoClicked(int rest_id, String restname) {
        FlexibleTenpoActivity.startTenpoActivity(rest_id, restname, TagCategoryActivity.this);
    }

    public void onCommentClicked(int post_id) {
        CommentActivity.startCommentActivity(post_id, TagCategoryActivity.this);
    }

    private int getPositionSortId() {
        int sort_id = 0;
        switch (mShowPosition) {
            case 0:
                sort_id = mWaSort_id;
                break;
            case 1:
                sort_id = mYouSort_id;
                break;
            case 2:
                sort_id = mTyuuSort_id;
                break;
            case 3:
                sort_id = mCareSort_id;
                break;
            case 4:
                sort_id = mRamenSort_id;
                break;
            case 5:
                sort_id = mTakokuSort_id;
                break;
            case 6:
                sort_id = mCafeSort_id;
                break;
            case 7:
                sort_id = mSakeSort_id;
                break;
            case 8:
                sort_id = mSonotaSort_id;
                break;
        }
        return sort_id;
    }
}

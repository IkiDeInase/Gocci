package com.inase.android.gocci.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.inase.android.gocci.Base.ToukouPopup;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.ui.fragment.FollowTimelineFragment;
import com.inase.android.gocci.ui.fragment.LatestTimelineFragment;
import com.inase.android.gocci.R;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.NotificationListView;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.konifar.fab_transformation.FabTransformation;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.otto.Subscribe;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class GocciTimelineActivity extends AppCompatActivity {

    private final GocciTimelineActivity self = this;
    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.smart_tab)
    SmartTabLayout mSmartTab;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.overlay)
    View mOverlay;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.category_spinner)
    MaterialBetterSpinner mCategorySpinner;
    @Bind(R.id.value_spinner)
    MaterialBetterSpinner mValueSpinner;
    @Bind(R.id.sort_spinner)
    MaterialBetterSpinner mSortSpinner;
    @Bind(R.id.filter_ripple)
    RippleView mFilterRipple;
    @Bind(R.id.sheet)
    CardView mSheet;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @OnClick(R.id.fab)
    public void click() {
        if (mFab.getVisibility() == View.VISIBLE) {
            FabTransformation.with(mFab).setOverlay(mOverlay).transformTo(mSheet);
        }
        SmartLocation.with(GocciTimelineActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                nowLocation = location;
            }
        });
    }

    @OnClick(R.id.overlay)
    public void clickOverlay() {
        if (mFab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(mFab).setOverlay(mOverlay).transformFrom(mSheet);
        }
    }

    private TextView mNotificationNumber;

    public static int mShowPosition = 0;
    public static int mLatestSort_id = 0;
    public static int mFollowSort_id = 0;
    public static Location nowLocation;

    private String[] SORT;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

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
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    Const.ANALYTICS_ID, //Amazon Mobile Analytics App ID
                    Const.IDENTITY_POOL_ID //Amazon Cognito Identity Pool ID
            );
        } catch (InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }

        setContentView(R.layout.activity_gocci_timeline);
        ButterKnife.bind(this);

        mToolBar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolBar)
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
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, GocciTimelineActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, GocciTimelineActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, GocciTimelineActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(GocciTimelineActivity.this)) {
                                    case 0:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(-1));
                                        SavedData.setSettingMute(GocciTimelineActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(0));
                                        SavedData.setSettingMute(GocciTimelineActivity.this, 0);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_mute)));
                                        break;
                                }
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result.setSelection(1);

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tab_latest, LatestTimelineFragment.class)
                .add(R.string.tab_follow, FollowTimelineFragment.class)
                .create());

        mViewpager.setAdapter(adapter);

        mSmartTab.setViewPager(mViewpager);
        mSmartTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BusHolder.get().post(new PageChangeVideoStopEvent(position));
                mShowPosition = position;
                mSortSpinner.setText(mShowPosition == 0 ? SORT[mLatestSort_id] : SORT[mFollowSort_id]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) mFab.hide();
                if (state == 0) mFab.show();
            }
        });

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        String[] VALUE = getResources().getStringArray(R.array.list_value);
        SORT = getResources().getStringArray(R.array.list_sort);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        mCategorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //category_id = position + 1;
            }
        });
        ArrayAdapter<String> valueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, VALUE);
        mValueSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //value_id = position + 1;
            }
        });
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, SORT);
        mSortSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mShowPosition == 0) {
                    mLatestSort_id = position;
                } else {
                    mFollowSort_id = position;
                }
            }
        });
        mCategorySpinner.setAdapter(categoryAdapter);
        mValueSpinner.setAdapter(valueAdapter);
        mSortSpinner.setAdapter(sortAdapter);

        mFilterRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FabTransformation.with(mFab).setOverlay(mOverlay).transformFrom(mSheet);
                //Otto currentpageと絞り込みurl
                BusHolder.get().post(new FilterTimelineEvent(mShowPosition, Const.getCustomTimelineAPI(mShowPosition, mLatestSort_id, mFollowSort_id,
                        nowLocation.getLongitude(), nowLocation.getLatitude(), 0)));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        BusHolder.get().unregister(self);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
        //２1文字で改行っぽい
        if (!event.mMessage.equals(getString(R.string.videoposting_complete))) {
            mNotificationNumber.setVisibility(View.VISIBLE);
            mNotificationNumber.setText(String.valueOf(event.mNotificationNumber));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bell_notification, menu);
        // お知らせ未読件数バッジ表示
        MenuItem item = menu.findItem(R.id.badge);
        MenuItem cameraitem = menu.findItem(R.id.camera);
        MenuItemCompat.setActionView(item, R.layout.toolbar_notification_icon);
        View view = MenuItemCompat.getActionView(item);
        mNotificationNumber = (TextView) view.findViewById(R.id.notification_number);
        int notifications = SavedData.getNotification(this);

        // バッジの数字を更新。0の場合はバッジを表示させない

        if (notifications == 0) {
            mNotificationNumber.setVisibility(View.INVISIBLE);
        } else {
            mNotificationNumber.setText(String.valueOf(notifications));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationNumber.setVisibility(View.INVISIBLE);
                SavedData.setNotification(GocciTimelineActivity.this, 0);
                View notification = new NotificationListView(GocciTimelineActivity.this);

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(GocciTimelineActivity.this);

                View header = notification.findViewById(R.id.header_view);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (window.isShowing()) {
                            window.dismiss();
                        }
                    }
                });
                window.setContentView(notification);
                //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                ToukouPopup.showLikeQuickAction(window, notification, v, GocciTimelineActivity.this.getWindowManager(), 0, 0);
            }
        });

        cameraitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (SavedData.getVideoUrl(GocciTimelineActivity.this).equals("") || SavedData.getLat(GocciTimelineActivity.this) == 0.0) {
                    startActivity(new Intent(GocciTimelineActivity.this, GocciCameraActivity.class));
                } else {
                    new MaterialDialog.Builder(GocciTimelineActivity.this)
                            .title(getString(R.string.already_exist_video))
                            .titleColorRes(R.color.namegrey)
                            .content(getString(R.string.already_exist_video_message))
                            .contentColorRes(R.color.namegrey)
                            .positiveText(getString(R.string.already_exist_video_yeah))
                            .positiveColorRes(R.color.gocci_header)
                            .negativeText(getString(R.string.already_exist_video_no))
                            .negativeColorRes(R.color.gocci_header)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    Intent intent = new Intent(GocciTimelineActivity.this, AlreadyExistCameraPreview.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    SharedPreferences prefs = getSharedPreferences("movie", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(GocciTimelineActivity.this, GocciCameraActivity.class));
                                }
                            }).show();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else if (mFab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(mFab).setOverlay(mOverlay).transformFrom(mSheet);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}

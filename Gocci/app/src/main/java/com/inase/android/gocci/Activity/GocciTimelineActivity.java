package com.inase.android.gocci.Activity;

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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.inase.android.gocci.Base.ToukouPopup;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.FilterTimelineEvent;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.Event.PageChangeVideoStopEvent;
import com.inase.android.gocci.Fragment.FollowTimelineFragment;
import com.inase.android.gocci.Fragment.LatestTimelineFragment;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.View.NotificationListView;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.konifar.fab_transformation.FabTransformation;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.otto.Subscribe;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class GocciTimelineActivity extends AppCompatActivity {

    private final GocciTimelineActivity self = this;

    private TextView notificationNumber;

    private FloatingActionButton fab;

    public static int mShowPosition = 0;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

    private CoordinatorLayout coordinatorLayout;

    private CardView sheet;
    private View overlay;

    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner value_spinner;
    private MaterialBetterSpinner sort_spinner;
    private RippleView filter_ripple;

    private int category_id = 0;
    private int value_id = 0;
    private int sort_id = 0;

    private Location nowLocation;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(new DrawerProfHeader(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.timeline)).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(getString(R.string.mypage)).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(getString(R.string.send_advice)).withIcon(GoogleMaterial.Icon.gmd_send).withSelectable(false).withIdentifier(3),
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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        final SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BusHolder.get().post(new PageChangeVideoStopEvent(position));
                mShowPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) fab.hide();
                if (state == 0) fab.show();
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                switch (viewPager.getCurrentItem()) {
//                    case 0:
//                        LatestTimelineFragment latestTimelineFragment = (LatestTimelineFragment) adapter.getPage(0);
//                        Toast.makeText(GocciTimelineActivity.this ,"LATEST", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        TrendTimelineFragment trendTimelineFragment = (TrendTimelineFragment) adapter.getPage(1);
//                        Toast.makeText(GocciTimelineActivity.this ,"TREND", Toast.LENGTH_SHORT).show();
//                        break;
//                }
                if (fab.getVisibility() == View.VISIBLE) {
                    FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
                }
                //startActivity(new Intent(GocciTimelineActivity.this, GocciCameraActivity.class));
                SmartLocation.with(GocciTimelineActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        nowLocation = location;
                    }
                });
            }
        });

        sheet = (CardView) findViewById(R.id.sheet);
        overlay = (View) findViewById(R.id.overlay);

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab.getVisibility() != View.VISIBLE) {
                    FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
                }
            }
        });

        category_spinner = (MaterialBetterSpinner) findViewById(R.id.category_spinner);
        value_spinner = (MaterialBetterSpinner) findViewById(R.id.value_spinner);
        sort_spinner = (MaterialBetterSpinner) findViewById(R.id.sort_spinner);
        filter_ripple = (RippleView) findViewById(R.id.filter_Ripple);

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        String[] VALUE = getResources().getStringArray(R.array.list_value);
        String[] SORT = getResources().getStringArray(R.array.list_sort);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        category_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category_id = position + 1;
            }
        });
        ArrayAdapter<String> valueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, VALUE);
        value_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                value_id = position + 1;
            }
        });
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, SORT);
        sort_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sort_id = position;
            }
        });
        category_spinner.setAdapter(categoryAdapter);
        value_spinner.setAdapter(valueAdapter);
        sort_spinner.setAdapter(sortAdapter);

        filter_ripple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
                //Otto currentpageと絞り込みurl
                BusHolder.get().post(new FilterTimelineEvent(viewPager.getCurrentItem(), Const.getTimelineCustomApi(0, 0, sort_id, nowLocation.getLongitude(), nowLocation.getLatitude())));
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
        Snackbar.make(coordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
        //２1文字で改行っぽい
        if (!event.mMessage.equals(getString(R.string.videoposting_complete))) {
            notificationNumber.setVisibility(View.VISIBLE);
            notificationNumber.setText(String.valueOf(event.mNotificationNumber));
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
        notificationNumber = (TextView) view.findViewById(R.id.notification_number);
        int notifications = SavedData.getNotification(this);

        // バッジの数字を更新。0の場合はバッジを表示させない

        if (notifications == 0) {
            notificationNumber.setVisibility(View.INVISIBLE);
        } else {
            notificationNumber.setText(String.valueOf(notifications));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationNumber.setVisibility(View.INVISIBLE);
                SavedData.setNotification(GocciTimelineActivity.this, 0);
                View notification = new NotificationListView(GocciTimelineActivity.this);

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(GocciTimelineActivity.this);

                View header = notification.findViewById(R.id.headerView);
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
        }
        else if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    public void onUserClicked(int user_id, String username) {
        FlexibleUserProfActivity.startUserProfActivity(user_id, username, GocciTimelineActivity.this);
    }

    public void onTenpoClicked(int rest_id, String restname) {
        FlexibleTenpoActivity.startTenpoActivity(rest_id, restname, GocciTimelineActivity.this);
    }

    public void onCommentClicked(int post_id) {
        CommentActivity.startCommentActivity(post_id, GocciTimelineActivity.this);
    }
}

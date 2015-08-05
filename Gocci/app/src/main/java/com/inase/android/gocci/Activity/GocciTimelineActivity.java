package com.inase.android.gocci.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.inase.android.gocci.Base.ToukouPopup;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.Event.PageChangeVideoStopEvent;
import com.inase.android.gocci.Fragment.LatestTimelineFragment;
import com.inase.android.gocci.Fragment.TrendTimelineFragment;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.View.NotificationListView;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.melnykov.fab.FloatingActionButton;
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

public class GocciTimelineActivity extends AppCompatActivity {

    private final GocciTimelineActivity self = this;

    private TextView notificationNumber;

    private FloatingActionButton fab;

    public static int mShowPosition = 0;

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
                case Const.INTENT_TO_USERPAGE:
                    FlexibleUserProfActivity.startUserProfActivity(msg.arg1, activity);
                    break;
                case Const.INTENT_TO_RESTPAGE:
                    FlexibleTenpoActivity.startTenpoActivity(msg.arg1, activity);
                    break;
                case Const.INTENT_TO_COMMENT:
                    CommentActivity.startCommentActivity(msg.arg1, activity);
                    break;
                case Const.INTENT_TO_CAMERA:
                    activity.startActivity(new Intent(activity, GocciCameraActivity.class));
                    break;
                case Const.INTENT_TO_POLICY:
                    WebViewActivity.startWebViewActivity(1, activity);
                    break;
                case Const.INTENT_TO_LICENSE:
                    WebViewActivity.startWebViewActivity(2, activity);
                    break;
                case Const.INTENT_TO_ADVICE:
                    Util.setAdviceDialog(activity);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイページ").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withCheckable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("要望を送る").withIcon(GoogleMaterial.Icon.gmd_send).withCheckable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName("利用規約とポリシー").withIcon(GoogleMaterial.Icon.gmd_visibility).withCheckable(false).withIdentifier(4),
                        new PrimaryDrawerItem().withName("ライセンス情報").withIcon(GoogleMaterial.Icon.gmd_build).withCheckable(false).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
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
                                        sHandler.obtainMessage(Const.INTENT_TO_POLICY, 0, 0, GocciTimelineActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_LICENSE, 0, 0, GocciTimelineActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            }
                        }
                        return false;
                    }
                })
                .withSelectedItem(0)
                .withSavedInstance(savedInstanceState)
                .build();

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tab_near, LatestTimelineFragment.class)
                .add(R.string.tab_follow_cheer, TrendTimelineFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
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

        fab = (FloatingActionButton) findViewById(R.id.toukouButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg =
                        sHandler.obtainMessage(Const.INTENT_TO_CAMERA, 0, 0, GocciTimelineActivity.this);
                sHandler.sendMessageDelayed(msg, 50);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(self);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        Snackbar.make(fab, event.mMessage, Snackbar.LENGTH_SHORT).show();
        fab.hide();
        //２1文字で改行っぽい
        notificationNumber.setVisibility(View.VISIBLE);
        notificationNumber.setText(String.valueOf(event.mNotificationNumber));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bell_notification, menu);
        // お知らせ未読件数バッジ表示
        MenuItem item = menu.findItem(R.id.badge);
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
                Log.e("ログ", "通知クリック");
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    public void onUserClicked(int user_id) {
        Message msg =
                sHandler.obtainMessage(Const.INTENT_TO_USERPAGE, user_id, user_id, GocciTimelineActivity.this);
        sHandler.sendMessageDelayed(msg, 750);
    }

    public void onTenpoClicked(int rest_id) {
        Message msg =
                sHandler.obtainMessage(Const.INTENT_TO_RESTPAGE, rest_id, rest_id, GocciTimelineActivity.this);
        sHandler.sendMessageDelayed(msg, 750);
    }

    public void onCommentClicked(int post_id) {
        Message msg =
                sHandler.obtainMessage(Const.INTENT_TO_COMMENT, post_id, post_id, GocciTimelineActivity.this);
        sHandler.sendMessageDelayed(msg, 750);
    }
}

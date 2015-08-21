package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;

public class SettingActivity extends AppCompatActivity {

    private final SettingActivity self = this;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

    private Toolbar tool_bar;
    private CoordinatorLayout coordinatorLayout;

    public static void startSettingActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, SettingActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingActivity activity
                    = (SettingActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_TIMELINE:
                    activity.startActivity(new Intent(activity, GocciTimelineActivity.class));
                    activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    break;
                case Const.INTENT_TO_MYPAGE:
                    GocciMyprofActivity.startMyProfActivity(activity);
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

        setContentView(R.layout.activity_settings);

        tool_bar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("設定");

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(tool_bar)
                .withHeader(new DrawerProfHeader(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイページ").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(2).withCheckable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("要望を送る").withIcon(GoogleMaterial.Icon.gmd_send).withCheckable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName("利用規約とポリシー").withIcon(GoogleMaterial.Icon.gmd_visibility).withCheckable(false).withIdentifier(4),
                        new PrimaryDrawerItem().withName("ライセンス情報").withIcon(GoogleMaterial.Icon.gmd_build).withCheckable(false).withIdentifier(5),
                        new PrimaryDrawerItem().withName("設定").withIcon(GoogleMaterial.Icon.gmd_settings).withCheckable(false).withIdentifier(6)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, SettingActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, SettingActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, SettingActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_POLICY, 0, 0, SettingActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_LICENSE, 0, 0, SettingActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 6) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, SettingActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
    }
}

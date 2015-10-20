package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.ProfPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.presenter.ShowUserProfPresenter;
import com.inase.android.gocci.ui.adapter.UserProfAdapter;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FlexibleUserProfActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        ShowUserProfPresenter.ShowUserProfView, UserProfAdapter.UserProfCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.list)
    RecyclerView mUserProfRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.empty_text)
    TextView mEmptyText;
    @Bind(R.id.empty_image)
    ImageView mEmptyImage;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private UserProfAdapter mUserProfAdapter;
    private ArrayList<PostData> mUserProfusers = new ArrayList<PostData>();
    private StaggeredGridLayoutManager mLayoutManager;

    private HeaderData headerUserData;

    private int mUser_id;

    private final FlexibleUserProfActivity self = this;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

    private ShowUserProfPresenter mPresenter;

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FlexibleUserProfActivity activity
                    = (FlexibleUserProfActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_TIMELINE:
                    activity.startActivity(new Intent(activity, GocciTimelineActivity.class));
                    activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    break;
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

    public static void startUserProfActivity(int user_id, String username, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, FlexibleUserProfActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("user_name", username);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

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

        UserAndRestDataRepository userAndRestDataRepositoryImpl = UserAndRestDataRepositoryImpl.getRepository();
        UserAndRestUseCase userAndRestUseCaseImpl = ProfPageUseCaseImpl.getUseCase(userAndRestDataRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowUserProfPresenter(userAndRestUseCaseImpl);
        mPresenter.setProfView(this);

        setContentView(R.layout.activity_flexible_user_prof);
        ButterKnife.bind(this);

        Intent userintent = getIntent();
        mUser_id = userintent.getIntExtra("user_id", 0);

        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        mToolBar.setTitle(userintent.getStringExtra("user_name"));
        setSupportActionBar(mToolBar);

        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mUserProfRecyclerView.setLayoutManager(mLayoutManager);
        mUserProfRecyclerView.setHasFixedSize(true);
        mUserProfRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mPresenter.getProfData(Const.USERPAGE_FIRST, Const.getUserpageAPI(mUser_id));

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
                            if (drawerItem.getIdentifier() == 1) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, FlexibleUserProfActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(FlexibleUserProfActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(FlexibleUserProfActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(FlexibleUserProfActivity.this, 0);
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

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(FlexibleUserProfActivity.this) != Util.NetworkStatus.OFF) {
                    mPresenter.getProfData(Const.USERPAGE_REFRESH, Const.getUserpageAPI(mUser_id));
                } else {
                    Toast.makeText(FlexibleUserProfActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        BusHolder.get().unregister(self);
        mAppBar.removeOnOffsetChangedListener(this);
        mPresenter.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);
        mAppBar.addOnOffsetChangedListener(this);
        mPresenter.resume();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeContainer.setEnabled(i == 0);
    }

    @Override
    public void showLoading() {
        mSwipeContainer.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void showNoResultCase(int api, HeaderData mUserData) {
        headerUserData = mUserData;
        if (api == Const.USERPAGE_FIRST) {
            mUserProfAdapter = new UserProfAdapter(this, headerUserData, mUserProfusers);
            mUserProfAdapter.setUserProfCallback(this);
            mUserProfRecyclerView.setAdapter(mUserProfAdapter);
        } else if (api == Const.USERPAGE_REFRESH) {
            mUserProfusers.clear();
            mUserProfAdapter.setData(headerUserData);
        }
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoResultCase() {
        mEmptyImage.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideError() {

    }

    @Override
    public void showResult(int api, HeaderData mUserData, ArrayList<PostData> mPostData) {
        headerUserData = mUserData;
        mUserProfusers.clear();
        mUserProfusers.addAll(mPostData);
        switch (api) {
            case Const.USERPAGE_FIRST:
                mUserProfAdapter = new UserProfAdapter(this, headerUserData, mUserProfusers);
                mUserProfAdapter.setUserProfCallback(this);
                mUserProfRecyclerView.setAdapter(mUserProfAdapter);
                break;
            case Const.USERPAGE_REFRESH:
                mUserProfAdapter.setData(headerUserData);
                break;
        }
    }

    @Override
    public void onFollowListClick(int user_id) {
        ListActivity.startListActivity(user_id, 0, Const.CATEGORY_FOLLOW, FlexibleUserProfActivity.this);
    }

    @Override
    public void onFollowerListClick(int user_id) {
        ListActivity.startListActivity(user_id, 0, Const.CATEGORY_FOLLOWER, FlexibleUserProfActivity.this);
    }

    @Override
    public void onUserCheerClick(int user_id) {
        ListActivity.startListActivity(user_id, 0, Const.CATEGORY_USER_CHEER, FlexibleUserProfActivity.this);
    }

    @Override
    public void onImageClick(int post_id) {
        //CommentActivity.startCommentActivity(post_id, FlexibleUserProfActivity.this);
    }

    @Override
    public void onLocationClick(ArrayList<PostData> postData) {
        ProfMapActivity.startProfMapActivity(postData, FlexibleUserProfActivity.this);
    }
}

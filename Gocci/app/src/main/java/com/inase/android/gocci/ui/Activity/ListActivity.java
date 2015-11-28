package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.FollowRepository;
import com.inase.android.gocci.datasource.repository.FollowRepositoryImpl;
import com.inase.android.gocci.datasource.repository.ListRepository;
import com.inase.android.gocci.datasource.repository.ListRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.ListGetData;
import com.inase.android.gocci.domain.usecase.FollowUseCase;
import com.inase.android.gocci.domain.usecase.FollowUseCaseImpl;
import com.inase.android.gocci.domain.usecase.ListGetUseCase;
import com.inase.android.gocci.domain.usecase.ListGetUseCaseImpl;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.presenter.ShowListPresenter;
import com.inase.android.gocci.ui.adapter.ListGetAdapter;
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
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        ShowListPresenter.ShowListGetView, ListGetAdapter.ListGetCallback {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.empty_text)
    TextView mEmptyText;
    @Bind(R.id.empty_image)
    ImageView mEmptyImage;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgress;

    private Const.ListCategory mCategory;
    private String mId;
    private boolean isMypage;

    private LinearLayoutManager mLayoutManager;
    private ArrayList<ListGetData> mList = new ArrayList<>();
    private ArrayList<String> mUser_idList = new ArrayList<>();
    private ListGetAdapter mListGetAdapter;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

    private ShowListPresenter mPresenter;

    public static void startListActivity(String id, boolean isMypage, Const.ListCategory category, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, ListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("check", isMypage);
        intent.putExtra("category", category);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private static Handler sHandler = new Handler();

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

        final API3 api3Impl = API3.Impl.getRepository();
        ListRepository listRepositoryImpl = ListRepositoryImpl.getRepository(api3Impl);
        ListGetUseCase listGetUseCaseImpl = ListGetUseCaseImpl.getUseCase(listRepositoryImpl, UIThread.getInstance());
        FollowRepository followRepository = FollowRepositoryImpl.getRepository(api3Impl);
        FollowUseCase followUseCase = FollowUseCaseImpl.getUseCase(followRepository, UIThread.getInstance());
        mPresenter = new ShowListPresenter(listGetUseCaseImpl, followUseCase);
        mPresenter.setListView(this);

        setContentView(R.layout.activity_list_follow_follower_cheer);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCategory = (Const.ListCategory) intent.getSerializableExtra("category");
        mId = intent.getStringExtra("id");
        isMypage = intent.getBooleanExtra("check", false);

        setSupportActionBar(mToolBar);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefresh.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(true);
                if (Util.getConnectedState(ListActivity.this) != Util.NetworkStatus.OFF) {
                    switch (mCategory) {
                        case FOLLOW:
                            API3.Util.GetFollowLocalCode getFollowLocalCode = api3Impl.get_follow_parameter_regex(mId);
                            if (getFollowLocalCode == null) {
                                mPresenter.getListData(Const.APICategory.GET_FOLLOW_REFRESH, API3.Util.getGetFollowAPI(mId));
                            } else {
                                Toast.makeText(ListActivity.this, API3.Util.getFollowLocalErrorMessageTable(getFollowLocalCode), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case FOLLOWER:
                            API3.Util.GetFollowerLocalCode getFollowerLocalCode = api3Impl.get_follower_parameter_regex(mId);
                            if (getFollowerLocalCode == null) {
                                mPresenter.getListData(Const.APICategory.GET_FOLLOWER_REFRESH, API3.Util.getGetFollowerAPI(mId));
                            } else {
                                Toast.makeText(ListActivity.this, API3.Util.getFollowerLocalErrorMessageTable(getFollowerLocalCode), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case USER_CHEER:
                            API3.Util.GetUserCheerLocalCode getUserCheerLocalCode = api3Impl.get_user_cheer_parameter_regex(mId);
                            if (getUserCheerLocalCode == null) {
                                mPresenter.getListData(Const.APICategory.GET_USER_CHEER_REFRESH, API3.Util.getGetUserCheerAPI(mId));
                            } else {
                                Toast.makeText(ListActivity.this, API3.Util.getUserCheerLocalErrorMessageTable(getUserCheerLocalCode), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case WANT:
                            API3.Util.GetWantLocalCode getWantLocalCode = api3Impl.get_want_parameter_regex(mId);
                            if (getWantLocalCode == null) {
                                mPresenter.getListData(Const.APICategory.GET_WANT_REFRESH, API3.Util.getGetWantAPI(mId));
                            } else {
                                Toast.makeText(ListActivity.this, API3.Util.getWantLocalErrorMessageTable(getWantLocalCode), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case REST_CHEER:
                            API3.Util.GetRestCheerLocalCode getRestCheerLocalCode = api3Impl.get_rest_cheer_parameter_regex(mId);
                            if (getRestCheerLocalCode == null) {
                                mPresenter.getListData(Const.APICategory.GET_REST_CHEER_REFRESH, API3.Util.getGetRestCheerAPI(mId));
                            } else {
                                Toast.makeText(ListActivity.this, API3.Util.getRestCheerLocalErrorMessageTable(getRestCheerLocalCode), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                } else {
                    Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });

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
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(ListActivity.this, TimelineActivity.class));
                                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyprofActivity.startMyProfActivity(ListActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.setFeedbackDialog(ListActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingActivity.startSettingActivity(ListActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(ListActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(ListActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(ListActivity.this, 0);
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

        switch (mCategory) {
            case FOLLOW:
                getSupportActionBar().setTitle(getString(R.string.follow_list));
                mEmptyText.setText(getString(R.string.follow_empty_text));
                API3.Util.GetFollowLocalCode getFollowLocalCode = api3Impl.get_follow_parameter_regex(mId);
                if (getFollowLocalCode == null) {
                    mPresenter.getListData(Const.APICategory.GET_FOLLOW_FIRST, API3.Util.getGetFollowAPI(mId));
                } else {
                    Toast.makeText(ListActivity.this, API3.Util.getFollowLocalErrorMessageTable(getFollowLocalCode), Toast.LENGTH_SHORT).show();
                }
                break;
            case FOLLOWER:
                getSupportActionBar().setTitle(getString(R.string.follower_list));
                mEmptyText.setText(getString(R.string.follower_empty_text));
                API3.Util.GetFollowerLocalCode getFollowerLocalCode = api3Impl.get_follower_parameter_regex(mId);
                if (getFollowerLocalCode == null) {
                    mPresenter.getListData(Const.APICategory.GET_FOLLOWER_FIRST, API3.Util.getGetFollowerAPI(mId));
                } else {
                    Toast.makeText(ListActivity.this, API3.Util.getFollowerLocalErrorMessageTable(getFollowerLocalCode), Toast.LENGTH_SHORT).show();
                }
                break;
            case USER_CHEER:
                getSupportActionBar().setTitle(getString(R.string.cheer_list));
                mEmptyText.setText(getString(R.string.cheer_empty_text));
                API3.Util.GetUserCheerLocalCode getUserCheerLocalCode = api3Impl.get_user_cheer_parameter_regex(mId);
                if (getUserCheerLocalCode == null) {
                    mPresenter.getListData(Const.APICategory.GET_USER_CHEER_FIRST, API3.Util.getGetUserCheerAPI(mId));
                } else {
                    Toast.makeText(ListActivity.this, API3.Util.getUserCheerLocalErrorMessageTable(getUserCheerLocalCode), Toast.LENGTH_SHORT).show();
                }
                break;
            case WANT:
                getSupportActionBar().setTitle(getString(R.string.want_list));
                mEmptyText.setText(getString(R.string.want_empty_text));
                API3.Util.GetWantLocalCode getWantLocalCode = api3Impl.get_want_parameter_regex(mId);
                if (getWantLocalCode == null) {
                    mPresenter.getListData(Const.APICategory.GET_WANT_FIRST, API3.Util.getGetWantAPI(mId));
                } else {
                    Toast.makeText(ListActivity.this, API3.Util.getWantLocalErrorMessageTable(getWantLocalCode), Toast.LENGTH_SHORT).show();
                }
                break;
            case REST_CHEER:
                getSupportActionBar().setTitle(getString(R.string.cheer_user_list));
                mEmptyText.setText(getString(R.string.usercheer_empty_text));
                API3.Util.GetRestCheerLocalCode getRestCheerLocalCode = api3Impl.get_rest_cheer_parameter_regex(mId);
                if (getRestCheerLocalCode == null) {
                    mPresenter.getListData(Const.APICategory.GET_REST_CHEER_FIRST, API3.Util.getGetRestCheerAPI(mId));
                } else {
                    Toast.makeText(ListActivity.this, API3.Util.getRestCheerLocalErrorMessageTable(getRestCheerLocalCode), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        mPresenter.pause();
        mAppBar.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(this);
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        mPresenter.resume();
        mAppBar.addOnOffsetChangedListener(this);
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

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeRefresh.setEnabled(i == 0);
    }

    @Override
    public void onUserClick(String user_id, String username) {
        UserProfActivity.startUserProfActivity(user_id, username, this);
    }

    @Override
    public void onRestClick(String rest_id, String restname) {
        TenpoActivity.startTenpoActivity(rest_id, restname, this);
    }

    @Override
    public void onFollowClick(Const.APICategory api, String user_id) {
        if (api == Const.APICategory.POST_FOLLOW) {
            API3.Util.PostFollowLocalCode postFollowLocalCode = API3.Impl.getRepository().post_follow_parameter_regex(user_id);
            if (postFollowLocalCode == null) {
                mPresenter.postFollow(api, API3.Util.getPostFollowAPI(user_id), user_id);
            } else {
                Toast.makeText(ListActivity.this, API3.Util.postFollowLocalErrorMessageTable(postFollowLocalCode), Toast.LENGTH_SHORT).show();
            }
        } else if (api == Const.APICategory.POST_UNFOLLOW) {
            API3.Util.PostUnfollowLocalCode postUnfollowLocalCode = API3.Impl.getRepository().post_unFollow_parameter_regex(user_id);
            if (postUnfollowLocalCode == null) {
                mPresenter.postFollow(api, API3.Util.getPostUnfollowAPI(user_id), user_id);
            } else {
                Toast.makeText(ListActivity.this, API3.Util.postUnfollowLocalErrorMessageTable(postUnfollowLocalCode), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmpty(Const.APICategory api) {
        switch (api) {
            case GET_FOLLOW_FIRST:
            case GET_FOLLOWER_FIRST:
            case GET_WANT_FIRST:
            case GET_USER_CHEER_FIRST:
            case GET_REST_CHEER_FIRST:
                mProgress.setVisibility(View.INVISIBLE);
                mListGetAdapter = new ListGetAdapter(this, isMypage, mCategory, mList);
                mListGetAdapter.setListGetCallback(this);
                mRecyclerView.setAdapter(mListGetAdapter);
                break;
            case GET_FOLLOW_REFRESH:
            case GET_FOLLOWER_REFRESH:
            case GET_WANT_REFRESH:
            case GET_USER_CHEER_REFRESH:
            case GET_REST_CHEER_REFRESH:
                mList.clear();
                mListGetAdapter.setData();
                mUser_idList.clear();
                break;
        }
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        mEmptyImage.setVisibility(View.INVISIBLE);
        mEmptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void causedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
        mProgress.setVisibility(View.INVISIBLE);
        switch (api) {
            case GET_FOLLOW_FIRST:
            case GET_FOLLOWER_FIRST:
            case GET_WANT_FIRST:
            case GET_USER_CHEER_FIRST:
            case GET_REST_CHEER_FIRST:
                mListGetAdapter = new ListGetAdapter(this, isMypage, mCategory, mList);
                mListGetAdapter.setListGetCallback(this);
                mRecyclerView.setAdapter(mListGetAdapter);
                break;
        }
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mProgress.setVisibility(View.INVISIBLE);
        switch (api) {
            case GET_FOLLOW_FIRST:
            case GET_FOLLOWER_FIRST:
            case GET_WANT_FIRST:
            case GET_USER_CHEER_FIRST:
            case GET_REST_CHEER_FIRST:
                mListGetAdapter = new ListGetAdapter(this, isMypage, mCategory, mList);
                mListGetAdapter.setListGetCallback(this);
                mRecyclerView.setAdapter(mListGetAdapter);
                break;
        }
    }

    @Override
    public void followSuccess(Const.APICategory api, String user_id) {

    }

    @Override
    public void followFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
        if (api == Const.APICategory.POST_FOLLOW) {
            mList.get(mUser_idList.indexOf(user_id)).setFollow_flag(0);
        } else if (api == Const.APICategory.POST_UNFOLLOW) {
            mList.get(mUser_idList.indexOf(user_id)).setFollow_flag(1);
        }
        mListGetAdapter.notifyItemChanged(mUser_idList.indexOf(user_id));
    }

    @Override
    public void followFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        if (api == Const.APICategory.POST_FOLLOW) {
            mList.get(mUser_idList.indexOf(user_id)).setFollow_flag(0);
        } else if (api == Const.APICategory.POST_UNFOLLOW) {
            mList.get(mUser_idList.indexOf(user_id)).setFollow_flag(1);
        }
        mListGetAdapter.notifyItemChanged(mUser_idList.indexOf(user_id));
    }

    @Override
    public void showResult(Const.APICategory api, ArrayList<ListGetData> list) {
        switch (api) {
            case GET_FOLLOW_FIRST:
            case GET_FOLLOWER_FIRST:
            case GET_WANT_FIRST:
            case GET_USER_CHEER_FIRST:
            case GET_REST_CHEER_FIRST:
                mProgress.setVisibility(View.INVISIBLE);
                mList.addAll(list);
                mListGetAdapter = new ListGetAdapter(this, isMypage, mCategory, mList);
                mListGetAdapter.setListGetCallback(this);
                mRecyclerView.setAdapter(mListGetAdapter);

                if (api == Const.APICategory.GET_FOLLOW_FIRST || api == Const.APICategory.GET_FOLLOWER_FIRST) {
                    for (int i = 0; i < mList.size(); i++) {
                        mUser_idList.add(i, mList.get(i).getUser_id());
                    }
                }
                break;
            case GET_FOLLOW_REFRESH:
            case GET_FOLLOWER_REFRESH:
            case GET_WANT_REFRESH:
            case GET_USER_CHEER_REFRESH:
            case GET_REST_CHEER_REFRESH:
                mList.clear();
                mList.addAll(list);
                mListGetAdapter.setData();

                if (api == Const.APICategory.GET_FOLLOW_REFRESH || api == Const.APICategory.GET_FOLLOWER_REFRESH) {
                    mUser_idList.clear();
                    for (int i = 0; i < mList.size(); i++) {
                        mUser_idList.add(i, mList.get(i).getUser_id());
                    }
                }
                break;
        }
    }
}

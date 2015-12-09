package com.inase.android.gocci.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.FollowRepository;
import com.inase.android.gocci.datasource.repository.FollowRepositoryImpl;
import com.inase.android.gocci.datasource.repository.GochiRepository;
import com.inase.android.gocci.datasource.repository.GochiRepositoryImpl;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.FollowUseCase;
import com.inase.android.gocci.domain.usecase.FollowUseCaseImpl;
import com.inase.android.gocci.domain.usecase.GochiUseCase;
import com.inase.android.gocci.domain.usecase.GochiUseCaseImpl;
import com.inase.android.gocci.domain.usecase.ProfPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.event.ProfJsonEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.event.TimelineMuteChangeEvent;
import com.inase.android.gocci.presenter.ShowUserProfPresenter;
import com.inase.android.gocci.ui.fragment.GridUserProfFragment;
import com.inase.android.gocci.ui.fragment.StreamUserProfFragment;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.GochiLayout;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class UserProfActivity extends AppCompatActivity implements ShowUserProfPresenter.ShowUserProfView {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.username_tool_bar)
    Toolbar mUserToolBar;
    @Bind(R.id.empty_text)
    TextView mEmptyText;
    @Bind(R.id.empty_image)
    ImageView mEmptyImage;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapse;
    @Bind(R.id.userprof_picture)
    ImageView mUserPicture;
    @Bind(R.id.follow_num)
    TextView mFollowNum;
    @Bind(R.id.follower_num)
    TextView mFollowerNum;
    @Bind(R.id.usercheer_num)
    TextView mUsercheerNum;
    @Bind(R.id.follow_text)
    TextView mFollowText;
    @Bind(R.id.gochi_layout)
    GochiLayout mGochi;

    @Bind(R.id.follow_ripple)
    RippleView mFollowRipple;
    @Bind(R.id.follower_ripple)
    RippleView mFollowerRipple;
    @Bind(R.id.usercheer_ripple)
    RippleView mUsercheerRipple;
    @Bind(R.id.userprof_follow)
    RippleView mUserProfFollow;

    @OnClick(R.id.stream)
    public void onStream() {
        if (mShowPosition == 1) {
            mShowPosition = 0;
            mViewpager.setCurrentItem(mShowPosition);
        }
    }

    @OnClick(R.id.grid)
    public void onGrid() {
        if (mShowPosition == 0) {
            mShowPosition = 1;
            mViewpager.setCurrentItem(mShowPosition);
        }
    }

    @OnClick(R.id.location)
    public void onLocation() {
        MapProfActivity.startProfMapActivity(TimelineActivity.mLongitude, TimelineActivity.mLatitude, mUsers, UserProfActivity.this);
    }

    private HeaderData headerUserData;
    private ArrayList<PostData> mUsers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();

    private float pointX;
    private float pointY;

    public static int mShowPosition = 0;

    private String mUser_id;

    private final UserProfActivity self = this;

    private Drawer result;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private static MobileAnalyticsManager analytics;

    private ShowUserProfPresenter mPresenter;

    private FragmentPagerItemAdapter adapter;

    private String mShareShare;
    private String mShareRestname;

    private static Handler sHandler = new Handler();

    public static void startUserProfActivity(String user_id, String username, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfActivity.class);
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

        final API3 api3Impl = API3.Impl.getRepository();
        UserAndRestDataRepository userAndRestDataRepositoryImpl = UserAndRestDataRepositoryImpl.getRepository(api3Impl);
        FollowRepository followRepository = FollowRepositoryImpl.getRepository(api3Impl);
        GochiRepository gochiRepository = GochiRepositoryImpl.getRepository(api3Impl);
        UserAndRestUseCase userAndRestUseCaseImpl = ProfPageUseCaseImpl.getUseCase(userAndRestDataRepositoryImpl, UIThread.getInstance());
        FollowUseCase followUseCase = FollowUseCaseImpl.getUseCase(followRepository, UIThread.getInstance());
        GochiUseCase gochiUseCase = GochiUseCaseImpl.getUseCase(gochiRepository, UIThread.getInstance());
        mPresenter = new ShowUserProfPresenter(userAndRestUseCaseImpl, followUseCase, gochiUseCase);
        mPresenter.setProfView(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(UserProfActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(UserProfActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        setContentView(R.layout.activity_userprof);
        ButterKnife.bind(this);

        Intent userintent = getIntent();
        mUser_id = userintent.getStringExtra("user_id");

        //toolbar.inflateMenu(R.menu.toolbar_menu);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        mUserToolBar.setTitle(userintent.getStringExtra("user_name"));
        mCollapse.setExpandedTitleTextAppearance(R.style.TitleText);
        mCollapse.setTitle(userintent.getStringExtra("user_name"));

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tab_near, StreamUserProfFragment.class)
                .add(R.string.tab_follow, GridUserProfFragment.class)
                .create());

        mViewpager.setAdapter(adapter);
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            }
        });

        API3.Util.GetUserLocalCode localCode = api3Impl.get_user_parameter_regex(mUser_id);
        if (localCode == null) {
            mPresenter.getProfData(Const.APICategory.GET_USER_FIRST, API3.Util.getGetUserAPI(mUser_id));
        } else {
            Toast.makeText(UserProfActivity.this, API3.Util.getUserLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }

        mFollowRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(mUser_id, false, Const.ListCategory.FOLLOW, UserProfActivity.this);
            }
        });

        mFollowerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(mUser_id, false, Const.ListCategory.FOLLOWER, UserProfActivity.this);
            }
        });

        mUsercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(mUser_id, false, Const.ListCategory.USER_CHEER, UserProfActivity.this);
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
                                        startActivity(new Intent(UserProfActivity.this, TimelineActivity.class));
                                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyprofActivity.startMyProfActivity(UserProfActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.setFeedbackDialog(UserProfActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingActivity.startSettingActivity(UserProfActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(UserProfActivity.this)) {
                                    case 0:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(-1));
                                        SavedData.setSettingMute(UserProfActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(0));
                                        SavedData.setSettingMute(UserProfActivity.this, 0);
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

        mGochi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                //final float y = Util.getScreenHeightInPx(TimelineActivity.this) - event.getRawY();
                pointX = event.getX();
                pointY = event.getY();
                return false;
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
        mPresenter.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);
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
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showEmpty(Const.APICategory api, HeaderData mUserData) {
        headerUserData = mUserData;

        Picasso.with(this)
                .load(headerUserData.getProfile_img())
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(mUserPicture);
        mFollowNum.setText(String.valueOf(headerUserData.getFollow_num()));
        mFollowerNum.setText(String.valueOf(headerUserData.getFollower_num()));
        mUsercheerNum.setText(String.valueOf(headerUserData.getCheer_num()));

        if (headerUserData.getFollow_flag() == 0) {
            mFollowText.setText(getString(R.string.do_follow));
        } else {
            mFollowText.setText(getString(R.string.do_unfollow));
        }

        if (mUserData.getUsername().equals(SavedData.getServerName(this))) {
            mFollowText.setText(getString(R.string.do_yours));
        }

        mUserProfFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mFollowText.getText().toString()) {
                    case "フォローする":
                        API3.Util.PostFollowLocalCode postFollowLocalCode = API3.Impl.getRepository().post_follow_parameter_regex(mUser_id);
                        if (postFollowLocalCode == null) {
                            mPresenter.postFollow(Const.APICategory.POST_FOLLOW, API3.Util.getPostFollowAPI(mUser_id), mUser_id);
                            mFollowText.setText(getString(R.string.do_unfollow));
                            headerUserData.setFollow_flag(1);
                        } else {
                            Toast.makeText(UserProfActivity.this, API3.Util.postFollowLocalErrorMessageTable(postFollowLocalCode), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "フォロー解除する":
                        API3.Util.PostUnfollowLocalCode postUnfollowLocalCode = API3.Impl.getRepository().post_unFollow_parameter_regex(mUser_id);
                        if (postUnfollowLocalCode == null) {
                            mPresenter.postFollow(Const.APICategory.POST_UNFOLLOW, API3.Util.getPostUnfollowAPI(mUser_id), mUser_id);
                            mFollowText.setText(getString(R.string.do_follow));
                            headerUserData.setFollow_flag(0);
                        } else {
                            Toast.makeText(UserProfActivity.this, API3.Util.postUnfollowLocalErrorMessageTable(postUnfollowLocalCode), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "これはあなたです":
                        break;
                }
            }
        });

        BusHolder.get().post(new ProfJsonEvent(api, mUsers, mPost_ids));

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
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void followSuccess(Const.APICategory api, String user_id) {

    }

    @Override
    public void followFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String user_id) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
        if (api == Const.APICategory.POST_FOLLOW) {
            mFollowText.setText(getString(R.string.do_follow));
            headerUserData.setFollow_flag(0);
        } else if (api == Const.APICategory.POST_UNFOLLOW) {
            mFollowText.setText(getString(R.string.do_unfollow));
            headerUserData.setFollow_flag(1);
        }
    }

    @Override
    public void followFailureCausedByLocalError(Const.APICategory api, String errorMessage, String user_id) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        if (api == Const.APICategory.POST_FOLLOW) {
            mFollowText.setText(getString(R.string.do_follow));
            headerUserData.setFollow_flag(0);
        } else if (api == Const.APICategory.POST_UNFOLLOW) {
            mFollowText.setText(getString(R.string.do_unfollow));
            headerUserData.setFollow_flag(1);
        }
    }

    @Override
    public void showResult(Const.APICategory api, HeaderData mUserData, ArrayList<PostData> mPostData, ArrayList<String> post_ids) {
        headerUserData = mUserData;

        Picasso.with(this)
                .load(headerUserData.getProfile_img())
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(mUserPicture);
        mFollowNum.setText(String.valueOf(headerUserData.getFollow_num()));
        mFollowerNum.setText(String.valueOf(headerUserData.getFollower_num()));
        mUsercheerNum.setText(String.valueOf(headerUserData.getCheer_num()));

        if (headerUserData.getFollow_flag() == 0) {
            mFollowText.setText(getString(R.string.do_follow));
        } else {
            mFollowText.setText(getString(R.string.do_unfollow));
        }

        if (mUserData.getUsername().equals(SavedData.getServerName(this))) {
            mFollowText.setText(getString(R.string.do_yours));
        }

        mUserProfFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mFollowText.getText().toString()) {
                    case "フォローする":
                        API3.Util.PostFollowLocalCode postFollowLocalCode = API3.Impl.getRepository().post_follow_parameter_regex(mUser_id);
                        if (postFollowLocalCode == null) {
                            mPresenter.postFollow(Const.APICategory.POST_FOLLOW, API3.Util.getPostFollowAPI(mUser_id), mUser_id);
                            mFollowText.setText(getString(R.string.do_unfollow));
                            headerUserData.setFollow_flag(1);
                        } else {
                            Toast.makeText(UserProfActivity.this, API3.Util.postFollowLocalErrorMessageTable(postFollowLocalCode), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "フォロー解除する":
                        API3.Util.PostUnfollowLocalCode postUnfollowLocalCode = API3.Impl.getRepository().post_unFollow_parameter_regex(mUser_id);
                        if (postUnfollowLocalCode == null) {
                            mPresenter.postFollow(Const.APICategory.POST_UNFOLLOW, API3.Util.getPostUnfollowAPI(mUser_id), mUser_id);
                            mFollowText.setText(getString(R.string.do_follow));
                            headerUserData.setFollow_flag(0);
                        } else {
                            Toast.makeText(UserProfActivity.this, API3.Util.postUnfollowLocalErrorMessageTable(postUnfollowLocalCode), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "これはあなたです":
                        break;
                }
            }
        });

        mUsers.clear();
        mUsers.addAll(mPostData);
        mPost_ids.clear();
        mPost_ids.addAll(post_ids);
        BusHolder.get().post(new ProfJsonEvent(api, mUsers, mPost_ids));

    }

    @Override
    public void gochiSuccess(Const.APICategory api, String post_id) {
        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.SUCCESS, Const.ActivityCategory.USER_PAGE, api, post_id));
    }

    @Override
    public void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        PostData data = mUsers.get(mPost_ids.indexOf(post_id));
        if (api == Const.APICategory.POST_GOCHI) {
            data.setGochi_flag(0);
            data.setGochi_num(data.getGochi_num() - 1);
        }
        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, Const.ActivityCategory.USER_PAGE, api, post_id));
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
    }

    @Override
    public void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        PostData data = mUsers.get(mPost_ids.indexOf(post_id));
        if (api == Const.APICategory.POST_GOCHI) {
            data.setGochi_flag(0);
            data.setGochi_num(data.getGochi_num() - 1);
        }
        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, Const.ActivityCategory.USER_PAGE, api, post_id));
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void refreshJson() {
        API3.Util.GetUserLocalCode localCode = API3.Impl.getRepository().get_user_parameter_regex(mUser_id);
        if (localCode == null) {
            mPresenter.getProfData(Const.APICategory.GET_USER_REFRESH, API3.Util.getGetUserAPI(mUser_id));
        } else {
            Toast.makeText(UserProfActivity.this, API3.Util.getUserLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public void setGochiLayout() {
        final float y = Util.getScreenHeightInPx(this) - pointY;
        mGochi.post(new Runnable() {
            @Override
            public void run() {
                mGochi.addGochi(R.drawable.ic_icon_beef_orange, pointX, y);
            }
        });
    }

    public void postGochi(String post_id) {
        API3.Util.PostGochiLocalCode postGochiLocalCode = API3.Impl.getRepository().post_gochi_parameter_regex(post_id);
        if (postGochiLocalCode == null) {
            mPresenter.postGochi(Const.APICategory.POST_GOCHI, API3.Util.getPostGochiAPI(post_id), post_id);
        } else {
            Toast.makeText(this, API3.Util.postGochiLocalErrorMessageTable(postGochiLocalCode), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareVideoPost(final int requastCode, String share, String restname) {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mShareShare = share;
            mShareRestname = restname;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(this)
                        .content("シェアをするにはストレージにアクセスする必要があります。権限を許可しますか？")
                        .contentColorRes(R.color.nameblack)
                        .positiveText("許可する")
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText("いいえ")
                        .negativeColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                ActivityCompat.requestPermissions(UserProfActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requastCode);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requastCode);
            }
        } else {
            switch (requastCode) {
                case 25:
                    Util.facebookVideoShare(this, shareDialog, share);
                    break;
                case 26:
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    if (session != null) {
                        TwitterAuthToken authToken = session.getAuthToken();
                        Util.twitterShare(this, "#" + restname.replaceAll("\\s+", "") + " #Gocci", share, authToken);
                    } else {
                        Toast.makeText(this, "設定ページでTwitter連携を行ってください", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 27:
                    Util.instaVideoShare(this, share);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 25:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Util.facebookVideoShare(this, shareDialog, mShareShare);
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            new MaterialDialog.Builder(this)
                                    .title("権限許可のお願い")
                                    .titleColorRes(R.color.namegrey)
                                    .content("シェアするには権限を許可する必要があるため、設定を変更する必要があります")
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText("変更する")
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText("いいえ")
                                    .negativeColorRes(R.color.gocci_header)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null); //Fragmentの場合はgetContext().getPackageName()
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    } else {
                        Util.facebookVideoShare(this, shareDialog, mShareShare);
                    }
                }
                break;
            case 26:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        TwitterSession session = Twitter.getSessionManager().getActiveSession();
                        if (session != null) {
                            TwitterAuthToken authToken = session.getAuthToken();
                            Util.twitterShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, authToken);
                        } else {
                            Toast.makeText(this, "設定ページでTwitter連携を行ってください", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            new MaterialDialog.Builder(this)
                                    .title("権限許可のお願い")
                                    .titleColorRes(R.color.namegrey)
                                    .content("シェアするには権限を許可する必要があるため、設定を変更する必要があります")
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText("変更する")
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText("いいえ")
                                    .negativeColorRes(R.color.gocci_header)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null); //Fragmentの場合はgetContext().getPackageName()
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    } else {
                        TwitterSession session = Twitter.getSessionManager().getActiveSession();
                        if (session != null) {
                            TwitterAuthToken authToken = session.getAuthToken();
                            Util.twitterShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, authToken);
                        } else {
                            Toast.makeText(this, "設定ページでTwitter連携を行ってください", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case 27:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Util.instaVideoShare(this, mShareShare);
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            new MaterialDialog.Builder(this)
                                    .title("権限許可のお願い")
                                    .titleColorRes(R.color.namegrey)
                                    .content("シェアするには権限を許可する必要があるため、設定を変更する必要があります")
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText("変更する")
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText("いいえ")
                                    .negativeColorRes(R.color.gocci_header)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null); //Fragmentの場合はgetContext().getPackageName()
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(UserProfActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    } else {
                        Util.instaVideoShare(this, mShareShare);
                    }
                }
                break;
        }
        // other 'case' lines to check for other
        // permissions this app might request
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_USER_FIRST:
            case GET_USER_REFRESH:
                mPresenter.getProfData(event.api, API3.Util.getGetUserAPI(mUser_id));
                break;
            default:
                break;
        }
    }
}

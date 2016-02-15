package com.inase.android.gocci.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.datasource.repository.GochiRepository;
import com.inase.android.gocci.datasource.repository.GochiRepositoryImpl;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
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
import com.inase.android.gocci.presenter.ShowMyProfPresenter;
import com.inase.android.gocci.ui.fragment.CalendarMyProfFragment;
import com.inase.android.gocci.ui.fragment.GridMyProfFragment;
import com.inase.android.gocci.ui.fragment.StreamMyProfFragment;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.GochiLayout;
import com.inase.android.gocci.ui.view.NotificationListView;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.ui.view.ToukouPopup;
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
import com.pnikosis.materialishprogress.ProgressWheel;
import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MyprofActivity extends AppCompatActivity implements ShowMyProfPresenter.ShowProfView {

    private final MyprofActivity self = this;
    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;
    @Bind(R.id.empty_text)
    TextView mEmptyText;
    @Bind(R.id.empty_image)
    ImageView mEmptyImage;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.myprof_username)
    TextView mUsername;
    @Bind(R.id.myprof_picture)
    ImageView mMyPicture;
    @Bind(R.id.follow_num)
    TextView mFollowNum;
    @Bind(R.id.follower_num)
    TextView mFollowerNum;
    @Bind(R.id.usercheer_num)
    TextView mUsercheerNum;
    @Bind(R.id.post_count_num)
    TextView mPostCountNum;
    @Bind(R.id.gochi_count_text)
    TextView mGochiCountNum;

    @Bind(R.id.follow_ripple)
    RippleView mFollowRipple;
    @Bind(R.id.follower_ripple)
    RippleView mFollowerRipple;
    @Bind(R.id.usercheer_ripple)
    RippleView mUsercheerRipple;
    @Bind(R.id.post_count_ripple)
    RippleView mPostCountRipple;
    @Bind(R.id.edit_profile)
    RippleView mEditProfile;
    @Bind(R.id.gochi_layout)
    GochiLayout mGochi;

    @OnClick(R.id.calendar)
    public void onCalendar() {
        if (mShowPosition != 2) {
            mShowPosition = 2;
            mViewpager.setCurrentItem(mShowPosition);
        }
    }

    @OnClick(R.id.stream)
    public void onStream() {
        if (mShowPosition != 0) {
            mShowPosition = 0;
            mViewpager.setCurrentItem(mShowPosition);
        }
    }

    @OnClick(R.id.grid)
    public void onGrid() {
        if (mShowPosition != 1) {
            mShowPosition = 1;
            mViewpager.setCurrentItem(mShowPosition);
        }
    }

    @OnClick(R.id.location)
    public void onLocation() {
        MapProfActivity.startProfMapActivity(Double.parseDouble(TimelineActivity.mLongitude), Double.parseDouble(TimelineActivity.mLatitude), mUsers, MyprofActivity.this);
    }

    private Drawer result;

    private TextView mNotificationNumber;
    private ImageView mEditBackground;
    private ImageView mEditPicture;
    private TextView mEditUsername;
    private EditText mEditUsernameEdit;

    private boolean isPicture = false;
    private boolean isName = false;

    private float pointX;
    private float pointY;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private ShowMyProfPresenter mPresenter;

    private HeaderData mHeaderUserData;
    private ArrayList<PostData> mUsers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();

    public static int mShowPosition = 0;

    private FragmentPagerItemAdapter adapter;

    private String mShareShare;
    private String mShareRestname;

    private static Handler sHandler = new Handler();

    public static void startMyProfActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MyprofActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final API3 api3Impl = API3.Impl.getRepository();
        UserAndRestDataRepository userAndRestDataRepositoryImpl = UserAndRestDataRepositoryImpl.getRepository(api3Impl);
        GochiRepository gochiRepositoryImpl = GochiRepositoryImpl.getRepository(api3Impl);
        UserAndRestUseCase userAndRestUseCaseImpl = ProfPageUseCaseImpl.getUseCase(userAndRestDataRepositoryImpl, UIThread.getInstance());
        GochiUseCase gochiUseCaseImpl = GochiUseCaseImpl.getUseCase(gochiRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowMyProfPresenter(userAndRestUseCaseImpl, gochiUseCaseImpl);
        mPresenter.setProfView(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(MyprofActivity.this, getString(R.string.complete_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MyprofActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Util.facebookVideoShare(MyprofActivity.this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, loginResult.getAccessToken().getToken());
                API3PostUtil.setSnsLinkAsync(MyprofActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.MY_PAGE, Const.APICategory.SET_FACEBOOK_LINK);
            }

            @Override
            public void onCancel() {
                Toast.makeText(MyprofActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MyprofActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        setContentView(R.layout.activity_myprof);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tab_near, StreamMyProfFragment.class)
                .add(R.string.tab_follow, GridMyProfFragment.class)
                .add(R.string.tab_gochi, CalendarMyProfFragment.class)
                .create());

        mViewpager.setOffscreenPageLimit(3);
        mViewpager.setAdapter(adapter);
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BusHolder.get().post(new PageChangeVideoStopEvent(position));
                mShowPosition = position;
                switch (mShowPosition) {
                    case 0:
                        mTracker = applicationGocci.getDefaultTracker();
                        mTracker.setScreenName("MyProfStream");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 1:
                        mTracker = applicationGocci.getDefaultTracker();
                        mTracker.setScreenName("MyProfGrid");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                    case 2:
                        mTracker = applicationGocci.getDefaultTracker();
                        mTracker.setScreenName("MyProfCalendar");
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        API3.Util.GetUserLocalCode localCode = api3Impl.GetUserParameterRegex(SavedData.getServerUserId(this));
        if (localCode == null) {
            mPresenter.getProfData(Const.APICategory.GET_USER_FIRST, API3.Util.getGetUserAPI(SavedData.getServerUserId(this)));
        } else {
            Toast.makeText(this, API3.Util.GetUserLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }

        mToolBar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        mFollowRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(SavedData.getServerUserId(MyprofActivity.this), Const.ListCategory.FOLLOW, MyprofActivity.this);
            }
        });

        mFollowerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(SavedData.getServerUserId(MyprofActivity.this), Const.ListCategory.FOLLOWER, MyprofActivity.this);
            }
        });

        mUsercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(SavedData.getServerUserId(MyprofActivity.this), Const.ListCategory.USER_CHEER, MyprofActivity.this);
            }
        });

        mEditProfile.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                final MaterialDialog dialog = new MaterialDialog.Builder(MyprofActivity.this)
                        .title(getString(R.string.change_profile_dialog_title))
                        .customView(R.layout.view_header_myprof_edit, false)
                        .positiveText(getString(R.string.change_profile_dialog_yeah))
                        .positiveColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                                if (isName && isPicture) {
                                    //どっちも変更した
                                    API3PostUtil.setUsernameAsync(MyprofActivity.this, mEditUsername.getText().toString(), Const.ActivityCategory.MY_PAGE);
                                    isName = false;
                                } else if (isPicture) {
                                    //写真だけ変更
                                    String post_date = SavedData.getServerUserId(MyprofActivity.this) + "_" + Util.getDateTimeString();
                                    File update_file = Util.getLocalBitmapFile(mEditPicture, post_date);
                                    API3PostUtil.setProfileImgAsync(MyprofActivity.this, post_date, update_file, Const.ActivityCategory.MY_PAGE);
                                    isPicture = false;
                                } else if (isName) {
                                    //名前だけ
                                    API3PostUtil.setUsernameAsync(MyprofActivity.this, mEditUsername.getText().toString(), Const.ActivityCategory.MY_PAGE);
                                    isName = false;
                                }
                            }
                        })
                        .build();

                mEditPicture = (ImageView) dialog.getCustomView().findViewById(R.id.myprof_picture);
                mEditUsername = (TextView) dialog.getCustomView().findViewById(R.id.myprof_username);
                mEditUsernameEdit = (EditText) dialog.getCustomView().findViewById(R.id.myprof_username_edit);

                Picasso.with(MyprofActivity.this)
                        .load(SavedData.getServerPicture(MyprofActivity.this))
                        .fit()
                        .placeholder(R.drawable.ic_userpicture)
                        .transform(new RoundedTransformation())
                        .into(mEditPicture);

                mEditUsername.setText(SavedData.getServerName(MyprofActivity.this));
                mEditUsernameEdit.setHint(SavedData.getServerName(MyprofActivity.this));

                mEditPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Crop.pickImage(MyprofActivity.this);
                    }
                });
                mEditUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditUsername.setVisibility(View.GONE);
                        mEditUsernameEdit.setVisibility(View.VISIBLE);
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        mEditUsernameEdit.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                    if (mEditUsernameEdit.getText().toString().isEmpty()) {
                                        Toast.makeText(MyprofActivity.this, getString(R.string.cheat_input_username), Toast.LENGTH_SHORT).show();
                                        return false;
                                    } else {
                                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(mEditUsernameEdit.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                        mEditUsername.setText(mEditUsernameEdit.getText().toString());
                                        mEditUsername.setVisibility(View.VISIBLE);
                                        mEditUsernameEdit.setVisibility(View.GONE);
                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                        isName = true;
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });
                    }
                });
                dialog.show();
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
                                        startActivity(new Intent(MyprofActivity.this, TimelineActivity.class));
                                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.setFeedbackDialog(MyprofActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SettingActivity.startSettingActivity(MyprofActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(MyprofActivity.this)) {
                                    case 0:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(-1));
                                        SavedData.setSettingMute(MyprofActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        BusHolder.get().post(new TimelineMuteChangeEvent(0));
                                        SavedData.setSettingMute(MyprofActivity.this, 0);
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

        result.setSelection(2);

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
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("MyProfStream");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(self);
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        BusHolder.get().unregister(self);
        mPresenter.pause();
        super.onPause();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
        if (event.mMessage.equals(getString(R.string.videoposting_complete))) {
            API3.Util.GetUserLocalCode localCode = API3.Impl.getRepository().GetUserParameterRegex(SavedData.getServerUserId(this));
            if (localCode == null) {
                mPresenter.getProfData(Const.APICategory.GET_USER_REFRESH, API3.Util.getGetUserAPI(SavedData.getServerUserId(this)));
            } else {
                Toast.makeText(this, API3.Util.GetUserLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
            }
        } else {
            mNotificationNumber.setText(String.valueOf(event.mNotificationNumber));
            if (event.mNotificationNumber != 0) {
                mNotificationNumber.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
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

        if (notifications == 0) {
            mNotificationNumber.setVisibility(View.INVISIBLE);
        } else {
            mNotificationNumber.setText(String.valueOf(notifications));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationNumber.setVisibility(View.INVISIBLE);
                SavedData.setNotification(MyprofActivity.this, 0);
                final NotificationListView notification = new NotificationListView(MyprofActivity.this);

                notification.resume();

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(MyprofActivity.this);

                View header = notification.findViewById(R.id.header_view);
                header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (window.isShowing()) {
                            window.dismiss();
                            notification.pause();
                        }
                    }
                });

                window.setContentView(notification);
                //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                ToukouPopup.showLikeQuickAction(window, notification, v, MyprofActivity.this.getWindowManager(), 0, 0);
            }
        });

        cameraitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                enableCamera();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void enableCamera() {
        int requestcode = 40;
        List<String> permissionArray = new ArrayList<>();
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionArray.add(Manifest.permission.CAMERA);
            requestcode = requestcode + 1;
        }
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionArray.add(Manifest.permission.RECORD_AUDIO);
            requestcode = requestcode + 1;
        }
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionArray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requestcode = requestcode + 1;
        }
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionArray.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestcode = requestcode + 1;
        }
        if (requestcode != 40) {
            String[] permissions = new String[permissionArray.size()];
            permissions = permissionArray.toArray(permissions);
            rationaleDialog(permissions, requestcode);
        } else {
            goCamera();
        }
    }

    private void rationaleDialog(final String[] permissions, final int requestCode) {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.permission_camera_title))
                .titleColorRes(R.color.namegrey)
                .content(getString(R.string.permission_camera_content))
                .contentColorRes(R.color.nameblack)
                .positiveText(getString(R.string.permission_camera_positive)).positiveColorRes(R.color.gocci_header)
                .negativeText(getString(R.string.permission_camera_negative)).negativeColorRes(R.color.gocci_header)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        ActivityCompat.requestPermissions(MyprofActivity.this, permissions, requestCode);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        Toast.makeText(MyprofActivity.this, getString(R.string.permission_camera_cancel), Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 25:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken != null) {
                            if (accessToken.getPermissions().contains("publish_actions")) {
                                Util.facebookVideoShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, accessToken.getToken());
                            } else {
                                ArrayList<String> permission = new ArrayList<>();
                                permission.add("publish_actions");
                                LoginManager.getInstance().logInWithPublishPermissions(this, permission);
                            }
                        } else {
                            ArrayList<String> permission = new ArrayList<>();
                            permission.add("publish_actions");
                            LoginManager.getInstance().logInWithPublishPermissions(this, permission);
                        }
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            new MaterialDialog.Builder(this)
                                    .title(getString(R.string.permission_storage_title))
                                    .titleColorRes(R.color.namegrey)
                                    .content(getString(R.string.permission_storage_content))
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText(getString(R.string.permission_storage_positive))
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText(getString(R.string.permission_storage_negative))
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
                                            Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    } else {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken != null) {
                            if (accessToken.getPermissions().contains("publish_actions")) {
                                Util.facebookVideoShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, accessToken.getToken());
                            } else {
                                ArrayList<String> permission = new ArrayList<>();
                                permission.add("publish_actions");
                                LoginManager.getInstance().logInWithPublishPermissions(this, permission);
                            }
                        } else {
                            ArrayList<String> permission = new ArrayList<>();
                            permission.add("publish_actions");
                            LoginManager.getInstance().logInWithPublishPermissions(this, permission);
                        }
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
                            Util.twitterVideoShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, authToken);
                        } else {
                            Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            new MaterialDialog.Builder(this)
                                    .title(getString(R.string.permission_storage_title))
                                    .titleColorRes(R.color.namegrey)
                                    .content(getString(R.string.permission_storage_content))
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText(getString(R.string.permission_storage_positive))
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText(getString(R.string.permission_storage_negative))
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
                                            Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    } else {
                        TwitterSession session = Twitter.getSessionManager().getActiveSession();
                        if (session != null) {
                            TwitterAuthToken authToken = session.getAuthToken();
                            Util.twitterVideoShare(this, "#" + mShareRestname.replaceAll("\\s+", "") + " #Gocci", mShareShare, authToken);
                        } else {
                            Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
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
                                    .title(getString(R.string.permission_storage_title))
                                    .titleColorRes(R.color.namegrey)
                                    .content(getString(R.string.permission_storage_content))
                                    .contentColorRes(R.color.nameblack)
                                    .positiveText(getString(R.string.permission_storage_positive))
                                    .positiveColorRes(R.color.gocci_header)
                                    .negativeText(R.string.permission_storage_negative)
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
                                            Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                    } else {
                        Util.instaVideoShare(this, mShareShare);
                    }
                }
                break;
            case 44:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                        goCamera();
                    } else {
                        rationaleSettingDialog();
                    }
                } else {
                    checkCamera();
                }
                break;
            case 43:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        goCamera();
                    } else {
                        rationaleSettingDialog();
                    }
                } else {
                    checkCamera();
                }
                break;
            case 42:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        goCamera();
                    } else {
                        rationaleSettingDialog();
                    }
                } else {
                    checkCamera();
                }
                break;
            case 41:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults.length > 0 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        goCamera();
                    } else {
                        rationaleSettingDialog();
                    }
                } else {
                    checkCamera();
                }
                break;
        }
    }

    private void rationaleSettingDialog() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.permission_camera_rationale_title))
                    .titleColorRes(R.color.namegrey)
                    .content(getString(R.string.permission_camera_rationale_title))
                    .contentColorRes(R.color.nameblack)
                    .positiveText(getString(R.string.permission_camera_rationale_positive))
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(getString(R.string.permission_camera_rationale_negative))
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
                            Toast.makeText(MyprofActivity.this, getString(R.string.permission_camera_cancel), Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        } else {
            Toast.makeText(MyprofActivity.this, getString(R.string.permission_camera_cancel), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCamera() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            goCamera();
        } else {
            Toast.makeText(MyprofActivity.this, getString(R.string.permission_camera_cancel), Toast.LENGTH_SHORT).show();
        }
    }

    private void goCamera() {
        if (SavedData.getVideoUrl(MyprofActivity.this).isEmpty()) {
            if (SavedData.getTotalTime(MyprofActivity.this) != 0) {
                new MaterialDialog.Builder(MyprofActivity.this)
                        .title(getString(R.string.already_exist_pre_video))
                        .titleColorRes(R.color.namegrey)
                        .content(getString(R.string.already_exist_pre_video_message))
                        .contentColorRes(R.color.namegrey)
                        .positiveText(getString(R.string.already_exist_pre_video_yeah))
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText(getString(R.string.already_exist_pre_video_no))
                        .negativeColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                Intent intent = new Intent(MyprofActivity.this, CameraActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                SharedPreferences prefs = getSharedPreferences("movie", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(MyprofActivity.this, CameraActivity.class));
                            }
                        }).show();
            } else {
                startActivity(new Intent(MyprofActivity.this, CameraActivity.class));
            }
        } else {
            new MaterialDialog.Builder(MyprofActivity.this)
                    .title(getString(R.string.already_exist_video))
                    .titleColorRes(R.color.namegrey)
                    .content(getString(R.string.already_exist_video_message))
                    .contentColorRes(R.color.namegrey)
                    .positiveText(getString(R.string.already_exist_video_yeah))
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(getString(R.string.already_exist_video_no))
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            Intent intent = new Intent(MyprofActivity.this, CameraPreviewAlreadyExistActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            SharedPreferences prefs = getSharedPreferences("movie", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.clear();
                            editor.apply();
                            startActivity(new Intent(MyprofActivity.this, CameraActivity.class));
                        }
                    }).show();
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == -1) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == -1) {
            Picasso.with(this)
                    .load(Crop.getOutput(result))
                    .fit()
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(mEditPicture);
            isPicture = true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshJson() {
        API3.Util.GetUserLocalCode localCode = API3.Impl.getRepository().GetUserParameterRegex(SavedData.getServerUserId(this));
        if (localCode == null) {
            mPresenter.getProfData(Const.APICategory.GET_USER_REFRESH, API3.Util.getGetUserAPI(SavedData.getServerUserId(this)));
        } else {
            Toast.makeText(this, API3.Util.GetUserLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
        }
    }

    public void setDeleteDialog(final String post_id) {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.check_delete_post))
                .positiveText(getString(R.string.check_delete_yeah))
                .positiveColorRes(R.color.gocci_header)
                .negativeText(getString(R.string.check_delete_no))
                .negativeColorRes(R.color.gocci_header)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        API3PostUtil.unsetPostAsync(MyprofActivity.this, post_id, Const.ActivityCategory.MY_PAGE);
                    }
                }).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        mProgressWheel.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty(Const.APICategory api, HeaderData userData) {
        mHeaderUserData = userData;

        mUsername.setText(mHeaderUserData.getUsername());
        Picasso.with(this)
                .load(mHeaderUserData.getProfile_img())
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(mMyPicture);
        mFollowNum.setText(String.valueOf(mHeaderUserData.getFollow_num()));
        mFollowerNum.setText(String.valueOf(mHeaderUserData.getFollower_num()));
        mUsercheerNum.setText(String.valueOf(mHeaderUserData.getCheer_num()));
        mPostCountNum.setText(String.valueOf(mHeaderUserData.getPost_num()));
        mGochiCountNum.setText(String.valueOf(mHeaderUserData.getGochi_num()));

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
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void causedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    @Override
    public void showResult(Const.APICategory api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids) {
        mHeaderUserData = userData;

        mUsername.setText(mHeaderUserData.getUsername());
        Picasso.with(this)
                .load(mHeaderUserData.getProfile_img())
                .fit()
                .placeholder(R.drawable.ic_userpicture)
                .transform(new RoundedTransformation())
                .into(mMyPicture);
        mFollowNum.setText(String.valueOf(mHeaderUserData.getFollow_num()));
        mFollowerNum.setText(String.valueOf(mHeaderUserData.getFollower_num()));
        mUsercheerNum.setText(String.valueOf(mHeaderUserData.getCheer_num()));
        mPostCountNum.setText(String.valueOf(mHeaderUserData.getPost_num()));
        mGochiCountNum.setText(String.valueOf(mHeaderUserData.getGochi_num()));

        mUsers.clear();
        mUsers.addAll(postData);
        mPost_ids.clear();
        mPost_ids.addAll(post_ids);
        BusHolder.get().post(new ProfJsonEvent(api, mUsers, mPost_ids));

    }

    @Override
    public void gochiSuccess(Const.APICategory api, String post_id) {

    }

    @Override
    public void gochiFailureCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode, String post_id) {
        PostData data = mUsers.get(mPost_ids.indexOf(post_id));
        if (api == Const.APICategory.SET_GOCHI) {
            data.setGochi_flag(false);
            data.setGochi_num(data.getGochi_num() - 1);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            data.setGochi_flag(true);
            data.setGochi_num(data.getGochi_num() + 1);
        }
        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.GLOBALERROR, Const.ActivityCategory.MY_PAGE, api, post_id));
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(API3.Util.GlobalCodeMessageTable(globalCode)).build());
    }

    @Override
    public void gochiFailureCausedByLocalError(Const.APICategory api, String errorMessage, String post_id) {
        PostData data = mUsers.get(mPost_ids.indexOf(post_id));
        if (api == Const.APICategory.SET_GOCHI) {
            data.setGochi_flag(false);
            data.setGochi_num(data.getGochi_num() - 1);
        } else if (api == Const.APICategory.UNSET_GOCHI) {
            data.setGochi_flag(true);
            data.setGochi_num(data.getGochi_num() + 1);
        }
        BusHolder.get().post(new PostCallbackEvent(Const.PostCallback.LOCALERROR, Const.ActivityCategory.MY_PAGE, api, post_id));
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder().setCategory("ApiBug").setAction(api.name()).setLabel(errorMessage).build());
    }

    public void setGochiLayout() {
        final float y = Util.getScreenHeightInPx(this) - pointY;
        mGochi.post(new Runnable() {
            @Override
            public void run() {
                mGochi.addGochi(pointX, y);
            }
        });
    }

    public void postGochi(String post_id, Const.APICategory apiCategory) {
        if (apiCategory == Const.APICategory.SET_GOCHI) {
            API3.Util.SetGochiLocalCode postGochiLocalCode = API3.Impl.getRepository().SetGochiParameterRegex(post_id);
            if (postGochiLocalCode == null) {
                mPresenter.postGochi(Const.APICategory.SET_GOCHI, API3.Util.getSetGochiAPI(post_id), post_id);
            } else {
                Toast.makeText(this, API3.Util.SetGochiLocalCodeMessageTable(postGochiLocalCode), Toast.LENGTH_SHORT).show();
            }
        } else if (apiCategory == Const.APICategory.UNSET_GOCHI) {
            API3.Util.UnsetGochiLocalCode unpostGochiLocalCode = API3.Impl.getRepository().UnsetGochiParameterRegex(post_id);
            if (unpostGochiLocalCode == null) {
                mPresenter.postGochi(Const.APICategory.UNSET_GOCHI, API3.Util.getUnsetGochiAPI(post_id), post_id);
            } else {
                Toast.makeText(this, API3.Util.UnsetGochiLocalCodeMessageTable(unpostGochiLocalCode), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void shareVideoPost(final int requastCode, String share, String restname) {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mShareShare = share;
            mShareRestname = restname;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(this)
                        .title(getString(R.string.permission_storage_title))
                        .titleColorRes(R.color.namegrey)
                        .content(getString(R.string.permission_storage_content))
                        .contentColorRes(R.color.nameblack)
                        .positiveText(getString(R.string.permission_storage_positive))
                        .positiveColorRes(R.color.gocci_header)
                        .negativeText(getString(R.string.permission_storage_negative))
                        .negativeColorRes(R.color.gocci_header)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                ActivityCompat.requestPermissions(MyprofActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requastCode);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                Toast.makeText(MyprofActivity.this, getString(R.string.error_share), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requastCode);
            }
        } else {
            switch (requastCode) {
                case 25:
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    if (accessToken != null) {
                        if (accessToken.getPermissions().contains("publish_actions")) {
                            Util.facebookVideoShare(this, "#" + restname.replaceAll("\\s+", "") + " #Gocci", share, accessToken.getToken());
                        } else {
                            ArrayList<String> permission = new ArrayList<>();
                            permission.add("publish_actions");
                            LoginManager.getInstance().logInWithPublishPermissions(this, permission);
                        }
                    } else {
                        ArrayList<String> permission = new ArrayList<>();
                        permission.add("publish_actions");
                        LoginManager.getInstance().logInWithPublishPermissions(this, permission);
                    }
                    break;
                case 26:
                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    if (session != null) {
                        TwitterAuthToken authToken = session.getAuthToken();
                        Util.twitterVideoShare(this, "#" + restname.replaceAll("\\s+", "") + " #Gocci", share, authToken);
                    } else {
                        Toast.makeText(this, getString(R.string.alert_twitter_sharing), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 27:
                    Util.instaVideoShare(this, share);
                    break;
            }
        }
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_USER_FIRST:
            case GET_USER_REFRESH:
                mPresenter.getProfData(event.api, API3.Util.getGetUserAPI(SavedData.getServerUserId(this)));
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.MY_PAGE) {
            switch (event.apiCategory) {
                case UNSET_POST:
                    int position = mPost_ids.indexOf(event.id);
                    mUsers.remove(position);
                    mPost_ids.remove(position);

                    BusHolder.get().post(new ProfJsonEvent(Const.APICategory.GET_USER_REFRESH, mUsers, mPost_ids));

                    if (mUsers.isEmpty()) {
                        mEmptyImage.setVisibility(View.VISIBLE);
                        mEmptyText.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyImage.setVisibility(View.GONE);
                        mEmptyText.setVisibility(View.GONE);
                    }
                    break;
                case SET_USERNAME:
                    if (isPicture) {
                        String post_date = SavedData.getServerUserId(MyprofActivity.this) + "_" + Util.getDateTimeString();
                        File update_file = Util.getLocalBitmapFile(mEditPicture, post_date);
                        API3PostUtil.setProfileImgAsync(MyprofActivity.this, post_date, update_file, Const.ActivityCategory.MY_PAGE);
                        isPicture = false;
                    } else {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                    break;
                case SET_PROFILEIMG:
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    break;
            }
        }
    }
}

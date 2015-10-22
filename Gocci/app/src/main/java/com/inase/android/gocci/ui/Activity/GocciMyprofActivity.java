package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.MyPageActionRepository;
import com.inase.android.gocci.datasource.repository.MyPageActionRepositoryImpl;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepository;
import com.inase.android.gocci.datasource.repository.UserAndRestDataRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.domain.usecase.PostDeleteUseCase;
import com.inase.android.gocci.domain.usecase.PostDeleteUseCaseImpl;
import com.inase.android.gocci.domain.usecase.ProfChangeUseCase;
import com.inase.android.gocci.domain.usecase.ProfChangeUseCaseImpl;
import com.inase.android.gocci.domain.usecase.ProfPageUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserAndRestUseCase;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.ProfJsonEvent;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PageChangeVideoStopEvent;
import com.inase.android.gocci.presenter.ShowMyProfPresenter;
import com.inase.android.gocci.ui.fragment.GridMyProfFragment;
import com.inase.android.gocci.ui.fragment.StreamMyProfFragment;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
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

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GocciMyprofActivity extends AppCompatActivity implements ShowMyProfPresenter.ShowProfView {

    private final GocciMyprofActivity self = this;
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
    @Bind(R.id.want_num)
    TextView mWantNum;

    @Bind(R.id.follow_ripple)
    RippleView mFollowRipple;
    @Bind(R.id.follower_ripple)
    RippleView mFollowerRipple;
    @Bind(R.id.usercheer_ripple)
    RippleView mUsercheerRipple;
    @Bind(R.id.want_ripple)
    RippleView mWantRipple;
    @Bind(R.id.edit_profile)
    RippleView mEditProfile;

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
        ProfMapActivity.startProfMapActivity(mUsers, GocciMyprofActivity.this);
    }

    private Drawer result;

    private TextView mNotificationNumber;
    private ImageView mEditBackground;
    private ImageView mEditPicture;
    private TextView mEditUsername;
    private EditText mEditUsernameEdit;

    private boolean isPicture = false;
    private boolean isName = false;

    private static MobileAnalyticsManager analytics;

    private ShowMyProfPresenter mPresenter;

    private HeaderData mHeaderUserData;
    private ArrayList<PostData> mUsers = new ArrayList<>();
    private ArrayList<String> mPost_ids = new ArrayList<>();

    public static int mShowPosition = 0;

    private FragmentPagerItemAdapter adapter;

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GocciMyprofActivity activity
                    = (GocciMyprofActivity) msg.obj;
            switch (msg.what) {
                case Const.INTENT_TO_TIMELINE:
                    activity.startActivity(new Intent(activity, GocciTimelineActivity.class));
                    activity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
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

    public static void startMyProfActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, GocciMyprofActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
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
        MyPageActionRepository myPageActionRepositoryImpl = MyPageActionRepositoryImpl.getRepository();
        UserAndRestUseCase userAndRestUseCaseImpl = ProfPageUseCaseImpl.getUseCase(userAndRestDataRepositoryImpl, UIThread.getInstance());
        ProfChangeUseCase profChangeUseCaseImpl = ProfChangeUseCaseImpl.getUseCase(myPageActionRepositoryImpl, UIThread.getInstance());
        PostDeleteUseCase postDeleteUseCaseImpl = PostDeleteUseCaseImpl.getUseCase(myPageActionRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowMyProfPresenter(userAndRestUseCaseImpl, profChangeUseCaseImpl, postDeleteUseCaseImpl);
        mPresenter.setProfView(this);

        setContentView(R.layout.activity_gocci_myprof);
        ButterKnife.bind(this);

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tab_near, StreamMyProfFragment.class)
                .add(R.string.tab_follow, GridMyProfFragment.class)
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

        mPresenter.getProfData(Const.USERPAGE_FIRST, Const.getUserpageAPI(Integer.parseInt(SavedData.getServerUserId(this))));

        mToolBar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        mFollowRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(Integer.parseInt(SavedData.getServerUserId(GocciMyprofActivity.this)), 1, Const.CATEGORY_FOLLOW, GocciMyprofActivity.this);
            }
        });

        mFollowerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(Integer.parseInt(SavedData.getServerUserId(GocciMyprofActivity.this)), 1, Const.CATEGORY_FOLLOWER, GocciMyprofActivity.this);
            }
        });

        mUsercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(Integer.parseInt(SavedData.getServerUserId(GocciMyprofActivity.this)), 1, Const.CATEGORY_USER_CHEER, GocciMyprofActivity.this);
            }
        });

        mWantRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ListActivity.startListActivity(Integer.parseInt(SavedData.getServerUserId(GocciMyprofActivity.this)), 1, Const.CATEGORY_WANT, GocciMyprofActivity.this);
            }
        });

        mEditProfile.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                final MaterialDialog dialog = new MaterialDialog.Builder(GocciMyprofActivity.this)
                        .title(getString(R.string.change_profile_dialog_title))
                        .customView(R.layout.view_header_myprof_edit, false)
                        .positiveText(getString(R.string.change_profile_dialog_yeah))
                        .positiveColorRes(R.color.gocci_header)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                mProgressWheel.setVisibility(View.VISIBLE);

                                String updateUrl = null;
                                String post_date = null;
                                File update_file = null;

                                if (isName && isPicture) {
                                    //どっちも変更した
                                    post_date = SavedData.getServerUserId(GocciMyprofActivity.this) + "_" + Util.getDateTimeString();
                                    update_file = Util.getLocalBitmapFile(mEditPicture, post_date);
                                    updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_BOTH,
                                            mEditUsername.getText().toString(),
                                            post_date);
                                    isName = false;
                                    isPicture = false;
                                } else if (isPicture) {
                                    //写真だけ変更
                                    post_date = SavedData.getServerUserId(GocciMyprofActivity.this) + "_" + Util.getDateTimeString();
                                    update_file = Util.getLocalBitmapFile(mEditPicture, post_date);
                                    updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_PICTURE,
                                            null, post_date);
                                    isPicture = false;
                                } else if (isName) {
                                    //名前だけ
                                    updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_NAME,
                                            mEditUsername.getText().toString(), null);
                                    isName = false;
                                }

                                if (updateUrl != null) {
                                    mPresenter.profChange(post_date, update_file, updateUrl);
                                }
                            }
                        })
                        .build();

                mEditPicture = (ImageView) dialog.getCustomView().findViewById(R.id.myprof_picture);
                mEditUsername = (TextView) dialog.getCustomView().findViewById(R.id.myprof_username);
                mEditUsernameEdit = (EditText) dialog.getCustomView().findViewById(R.id.myprof_username_edit);

                Picasso.with(GocciMyprofActivity.this)
                        .load(SavedData.getServerPicture(GocciMyprofActivity.this))
                        .fit()
                        .placeholder(R.drawable.ic_userpicture)
                        .transform(new RoundedTransformation())
                        .into(mEditPicture);

                mEditUsername.setText(SavedData.getServerName(GocciMyprofActivity.this));
                mEditUsernameEdit.setHint(SavedData.getServerName(GocciMyprofActivity.this));

                mEditPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Crop.pickImage(GocciMyprofActivity.this);
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
                                        Toast.makeText(GocciMyprofActivity.this, getString(R.string.cheat_input_username), Toast.LENGTH_SHORT).show();
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
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, GocciMyprofActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, GocciMyprofActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, GocciMyprofActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(GocciMyprofActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(GocciMyprofActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(GocciMyprofActivity.this, 0);
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
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
        BusHolder.get().unregister(self);
        mPresenter.pause();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
        if (event.mMessage.equals(getString(R.string.videoposting_complete))) {
            mPresenter.getProfData(Const.USERPAGE_REFRESH, Const.getUserpageAPI(Integer.parseInt(SavedData.getServerUserId(this))));
        } else {
            mNotificationNumber.setVisibility(View.VISIBLE);
            mNotificationNumber.setText(String.valueOf(event.mNotificationNumber));
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
                SavedData.setNotification(GocciMyprofActivity.this, 0);
                View notification = new NotificationListView(GocciMyprofActivity.this);

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(GocciMyprofActivity.this);

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
                ToukouPopup.showLikeQuickAction(window, notification, v, GocciMyprofActivity.this.getWindowManager(), 0, 0);
            }
        });

        cameraitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (SavedData.getVideoUrl(GocciMyprofActivity.this).equals("") || SavedData.getLat(GocciMyprofActivity.this) == 0.0) {
                    startActivity(new Intent(GocciMyprofActivity.this, GocciCameraActivity.class));
                } else {
                    new MaterialDialog.Builder(GocciMyprofActivity.this)
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
                                    Intent intent = new Intent(GocciMyprofActivity.this, AlreadyExistCameraPreview.class);
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
                                    startActivity(new Intent(GocciMyprofActivity.this, GocciCameraActivity.class));
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
        mPresenter.getProfData(Const.USERPAGE_REFRESH, Const.getUserpageAPI(Integer.parseInt(SavedData.getServerUserId(GocciMyprofActivity.this))));
    }

    public void setDeleteDialog(final String post_id, final int position) {
        new MaterialDialog.Builder(this)
                .content(getString(R.string.check_delete_post))
                .positiveText(getString(R.string.check_delete_yeah))
                .positiveColorRes(R.color.gocci_header)
                .negativeText(getString(R.string.check_delete_no))
                .negativeColorRes(R.color.gocci_header)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mPresenter.postDelete(post_id, position);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }

    //setDeleteDialog(post_id, position);

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showNoResultCase(int api, HeaderData userData) {
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
        mWantNum.setText(String.valueOf(mHeaderUserData.getWant_num()));

        BusHolder.get().post(new ProfJsonEvent(api, mUsers, mPost_ids));

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
    public void showResult(int api, HeaderData userData, ArrayList<PostData> postData, ArrayList<String> post_ids) {
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
        mWantNum.setText(String.valueOf(mHeaderUserData.getWant_num()));

        mUsers.clear();
        mUsers.addAll(postData);
        mPost_ids.clear();
        mPost_ids.addAll(post_ids);
        BusHolder.get().post(new ProfJsonEvent(api, mUsers, mPost_ids));

    }

    @Override
    public void profChanged(String userName, String profile_img) {
        mProgressWheel.setVisibility(View.GONE);

        if (userName.equals(getString(R.string.change_profile_dialog_error_username))) {
            SavedData.changeProfile(this, SavedData.getServerName(this), profile_img);
            Toast.makeText(this, getString(R.string.change_profile_dialog_error_username_message), Toast.LENGTH_SHORT).show();
        } else {
            SavedData.changeProfile(this, userName, profile_img);
        }

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void profChangeFailed(String message) {
        mProgressWheel.setVisibility(View.GONE);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postDeleted(int position) {
        mUsers.remove(position);
        mPost_ids.remove(position);

        BusHolder.get().post(new ProfJsonEvent(Const.USERPAGE_REFRESH, mUsers, mPost_ids));

        if (mUsers.isEmpty()) {
            mEmptyImage.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mEmptyImage.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void postDeleteFailed() {
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }
}

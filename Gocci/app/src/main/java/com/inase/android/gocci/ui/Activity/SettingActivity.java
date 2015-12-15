package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.BuildConfig;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3PostUtil;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.event.PostCallbackEvent;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.GocciTwitterLoginButton;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    private final SettingActivity self = this;
    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.locale)
    TextView mLocale;
    @Bind(R.id.support_mute)
    TextView mSupportMute;
    @Bind(R.id.version_number)
    TextView mVersionNumber;
    @Bind(R.id.logout_ripple)
    RippleView mLogoutRipple;
    @Bind(R.id.twitter_setting)
    TextView mTwitterSetting;
    @Bind(R.id.facebook_setting)
    TextView mFacebookSetting;
    @Bind(R.id.login_button)
    LoginButton mFacebookLoginButton;
    @Bind(R.id.twitter_login_button)
    GocciTwitterLoginButton mTwitterLoginButton;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @OnClick(R.id.account_locale)
    public void locale() {
        int selected = SavedData.getSettingRegions(SettingActivity.this);
//                new MaterialDialog.Builder(SettingActivity.this)
//                        .items(R.array.locale)
//                        .itemsCallbackSingleChoice(selected, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                SavedData.setSettingRegions(SettingActivity.this, which);
//                                return true;
//                            }
//                        })
//                        .widgetColorRes(R.color.gocci_header)
//                        .positiveText(getString(R.string.check_change_yeah))
//                        .positiveColorRes(R.color.gocci_header)
//                        .show();
        new MaterialDialog.Builder(SettingActivity.this)
                .content(getString(R.string.check_not_implemented_message))
                .positiveText(getString(R.string.check_not_implemented_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    @OnClick(R.id.account_password)
    public void password() {
        new MaterialDialog.Builder(SettingActivity.this)
                .content(getString(R.string.password_message))
                .contentColorRes(R.color.namegrey)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        String password = charSequence.toString();
                        if (!password.isEmpty()) {
                            API3PostUtil.postPasswordAsync(SettingActivity.this, password, Const.ActivityCategory.SETTING, Const.APICategory.SET_PASSWORD);
                        } else {
                            Toast.makeText(SettingActivity.this, getString(R.string.cheat_input_password), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(getString(R.string.password_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    @OnClick(R.id.account_notification)
    public void notification() {
        Integer[] selected = SavedData.getSettingNotifications(SettingActivity.this);

        new MaterialDialog.Builder(SettingActivity.this)
                .items(R.array.notice)
                .itemsCallbackMultiChoice(selected, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        SavedData.setSettingNotifications(SettingActivity.this, which);
                        return true; // allow selection
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(getString(R.string.check_change_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    @OnClick(R.id.friend_search)
    public void search() {
        new MaterialDialog.Builder(SettingActivity.this)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.search_friend_hint), null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        //Util.searchUserPost(SettingActivity.this, SettingActivity.this, input.toString());
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(getString(R.string.search_friend_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    @OnClick(R.id.friend_invite)
    public void invite() {
        new MaterialDialog.Builder(SettingActivity.this)
                .content(getString(R.string.check_not_implemented_message))
                .positiveText(getString(R.string.check_not_implemented_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    @OnClick(R.id.socialnetwork_twitter)
    public void twitter() {
        if (isTwitterSetting) {
            //ログアウトダイアログ
            new MaterialDialog.Builder(SettingActivity.this)
                    .content(getString(R.string.remove_auth_twitter_message))
                    .positiveText(R.string.remove_auth_yeah)
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(R.string.remove_auth_no)
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            TwitterSession session =
                                    Twitter.getSessionManager().getActiveSession();
                            TwitterAuthToken authToken = session.getAuthToken();
                            API3PostUtil.postSnsUnlinkAsync(SettingActivity.this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.SETTING, Const.APICategory.UNSET_TWITTER_LINK);
                        }
                    }).show();
        } else {
            //ログイン
            mTwitterLoginButton.performClick();
        }
    }

    @OnClick(R.id.socialnetwork_facebook)
    public void facebook() {
        if (isFacebookSetting) {
            //ログアウトダイアログ
            new MaterialDialog.Builder(SettingActivity.this)
                    .content(getString(R.string.remove_auth_facebook_message))
                    .positiveText(R.string.remove_auth_yeah)
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText(R.string.remove_auth_no)
                    .negativeColorRes(R.color.gocci_header)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            API3PostUtil.postSnsUnlinkAsync(SettingActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.SETTING, Const.APICategory.UNSET_FACEBOOK_LINK);
                        }
                    }).show();
        } else {
            mFacebookLoginButton.performClick();
        }
    }

    @OnClick(R.id.support_autoplay)
    public void autoplay() {
        int selected = SavedData.getSettingAutoPlay(SettingActivity.this);
        new MaterialDialog.Builder(SettingActivity.this)
                .items(R.array.autoplay)
                .itemsCallbackSingleChoice(selected, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        SavedData.setSettingAutoPlay(SettingActivity.this, which);
                        return true;
                    }
                })
                .widgetColorRes(R.color.gocci_header)
                .positiveText(getString(R.string.check_change_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    @OnClick(R.id.support_mute)
    public void mute() {
        //-1　mute / 0 unmute
        if (mSupportMute.getText().equals(getString(R.string.setting_support_mute))) {
            mSupportMute.setText(getString(R.string.setting_support_unmute));
            SavedData.setSettingMute(SettingActivity.this, -1);
        } else {
            mSupportMute.setText(getString(R.string.setting_support_mute));
            SavedData.setSettingMute(SettingActivity.this, 0);
        }
    }

    @OnClick(R.id.support_advice)
    public void advice() {
        Util.setFeedbackDialog(SettingActivity.this);
    }

    @OnClick(R.id.support_rule)
    public void rule() {
        WebViewActivity.startWebViewActivity(0, SettingActivity.this);
    }

    @OnClick(R.id.support_policy)
    public void policy() {
        WebViewActivity.startWebViewActivity(1, SettingActivity.this);
    }

    @OnClick(R.id.support_source)
    public void source() {
        WebViewActivity.startWebViewActivity(2, SettingActivity.this);
    }

    @OnClick(R.id.support_delete)
    public void delete() {
        new MaterialDialog.Builder(SettingActivity.this)
                .content(getString(R.string.check_not_implemented_message))
                .positiveText(getString(R.string.check_not_implemented_yeah))
                .positiveColorRes(R.color.gocci_header)
                .show();
    }

    private CallbackManager callbackManager;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

    private boolean isTwitterSetting;
    private boolean isFacebookSetting;

    public static void startSettingActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, SettingActivity.class);
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

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(getString(R.string.settings));

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolBar)
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
                            if (drawerItem.getIdentifier() == 1) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SettingActivity.this, TimelineActivity.class));
                                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyprofActivity.startMyProfActivity(SettingActivity.this);
                                    }
                                }, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.setFeedbackDialog(SettingActivity.this);
                                    }
                                }, 500);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
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
        result.setSelection(4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFacebookLoginButton.setReadPermissions("public_profile");
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                API3PostUtil.postSnsLinkAsync(SettingActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.SETTING, Const.APICategory.SET_FACEBOOK_LINK);
                Profile profile = Profile.getCurrentProfile();
                String profile_img = "https://graph.facebook.com/" + profile.getId() + "/picture";
                String post_date = SavedData.getServerUserId(SettingActivity.this) + "_" + Util.getDateTimeString();
                API3PostUtil.postProfileImgAsync(SettingActivity.this, post_date, profile_img, Const.ActivityCategory.SETTING);
            }

            @Override
            public void onCancel() {
                Toast.makeText(SettingActivity.this, getString(R.string.cancel_login), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(SettingActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterAuthToken authToken = result.data.getAuthToken();
                API3PostUtil.postSnsLinkAsync(SettingActivity.this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.SETTING, Const.APICategory.SET_TWITTER_LINK);
                String username = result.data.getUserName();
                String profile_img = "http://www.paper-glasses.com/api/twipi/" + username;
                String post_date = SavedData.getServerUserId(SettingActivity.this) + "_" + Util.getDateTimeString();
                API3PostUtil.postProfileImgAsync(SettingActivity.this, post_date, profile_img, Const.ActivityCategory.SETTING);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(SettingActivity.this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        TwitterSession session =
                Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            TwitterAuthToken authToken = session.getAuthToken();
            mTwitterSetting.setText(session.getUserName());
            isTwitterSetting = true;
            if (Application_Gocci.getShareTransfer() != null) {
                API3PostUtil.postSnsLinkAsync(SettingActivity.this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.SETTING, Const.APICategory.SET_TWITTER_LINK);
            }
        } else {
            isTwitterSetting = false;
        }

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            mFacebookSetting.setText(profile.getName());
            isFacebookSetting = true;
            if (Application_Gocci.getShareTransfer() != null) {
                API3PostUtil.postSnsLinkAsync(SettingActivity.this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.SETTING, Const.APICategory.SET_FACEBOOK_LINK);
            }
        } else {
            isFacebookSetting = false;
        }

        mVersionNumber.setText(BuildConfig.VERSION_NAME);
        mSupportMute.setText(SavedData.getSettingMute(SettingActivity.this) == 0 ? getString(R.string.setting_support_mute) : getString(R.string.setting_support_unmute));
        mLocale.setText(Locale.getDefault() == Locale.JAPAN ? getString(R.string.japanese) : getString(R.string.english));

        mLogoutRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                new MaterialDialog.Builder(SettingActivity.this)
                        .content(getString(R.string.check_not_implemented_message))
                        .positiveText(getString(R.string.check_not_implemented_yeah))
                        .positiveColorRes(R.color.gocci_header)
                        .show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
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

    private void logoutFacebook() {
        LoginManager.getInstance().logOut();
    }

    private void logoutTwitter() {
        CookieSyncManager.createInstance(SettingActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
    }

    @Subscribe
    public void subscribe(PostCallbackEvent event) {
        if (event.activityCategory == Const.ActivityCategory.SETTING) {
            switch (event.apiCategory) {
                case SET_FACEBOOK_LINK:
                    mFacebookSetting.setText(Profile.getCurrentProfile().getName());
                    isFacebookSetting = true;
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Application_Gocci.getProvider(SettingActivity.this).refresh();
                            return null;
                        }
                    }.execute();
                    break;
                case UNSET_FACEBOOK_LINK:
                    mFacebookSetting.setText(getString(R.string.no_auth_message));
                    isFacebookSetting = false;
                    logoutFacebook();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Application_Gocci.getProvider(SettingActivity.this).refresh();
                            return null;
                        }
                    }.execute();
                    break;
                case SET_TWITTER_LINK:
                    mTwitterSetting.setText(Twitter.getSessionManager().getActiveSession().getUserName());
                    isTwitterSetting = true;
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Application_Gocci.getProvider(SettingActivity.this).refresh();
                            return null;
                        }
                    }.execute();
                    break;
                case UNSET_TWITTER_LINK:
                    mTwitterSetting.setText(getString(R.string.no_auth_message));
                    isTwitterSetting = false;
                    logoutTwitter();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Application_Gocci.getProvider(SettingActivity.this).refresh();
                            return null;
                        }
                    }.execute();
                    break;
            }
        }
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case SET_FACEBOOK_LINK:
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    API3PostUtil.postSnsLinkAsync(this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.SETTING, Const.APICategory.SET_FACEBOOK_LINK);
                }
                break;
            case UNSET_FACEBOOK_LINK:
                API3PostUtil.postSnsUnlinkAsync(this, Const.ENDPOINT_FACEBOOK, AccessToken.getCurrentAccessToken().getToken(), Const.ActivityCategory.SETTING, Const.APICategory.UNSET_FACEBOOK_LINK);
                break;
            case SET_TWITTER_LINK:
                TwitterSession session =
                        Twitter.getSessionManager().getActiveSession();
                if (session != null) {
                    TwitterAuthToken authToken = session.getAuthToken();

                    API3PostUtil.postSnsLinkAsync(this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.SETTING, Const.APICategory.SET_TWITTER_LINK);
                }
                break;
            case UNSET_TWITTER_LINK:
                TwitterSession unlinksession =
                        Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = unlinksession.getAuthToken();
                API3PostUtil.postSnsUnlinkAsync(this, Const.ENDPOINT_TWITTER, authToken.token + ";" + authToken.secret, Const.ActivityCategory.SETTING, Const.APICategory.UNSET_TWITTER_LINK);
                break;
            default:
                break;
        }
    }
}

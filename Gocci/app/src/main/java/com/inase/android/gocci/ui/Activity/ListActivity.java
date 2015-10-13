package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.HeaderData;
import com.inase.android.gocci.event.NotificationNumberEvent;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.inase.android.gocci.ui.view.RoundedTransformation;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ListActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

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

    private int mCategory;
    private int mId;
    private String mUrl;
    private int isMypage; // 0　マイページでない　１　マイページ

    private ArrayList<HeaderData> users = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;

    private FollowFollowerAdapter followefollowerAdapter;
    private UserCheerAdapter usercheerAdapter;
    private WantAdapter wantAdapter;
    private RestCheerAdapter restcheerAdapter;

    private Drawer result;

    private static MobileAnalyticsManager analytics;

    public static void startListActivity(int id, int isMypage, int category, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, ListActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("check", isMypage);
        intent.putExtra("category", category);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListActivity activity
                    = (ListActivity) msg.obj;
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

        setContentView(R.layout.activity_follower_followee_cheer_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCategory = intent.getIntExtra("category", 0);
        mId = intent.getIntExtra("id", 0);
        isMypage = intent.getIntExtra("check", 0);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefresh.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(true);
                if (Util.getConnectedState(ListActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshJSON(mUrl, mCategory, ListActivity.this);
                } else {
                    Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });

        setSupportActionBar(mToolBar);

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
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, ListActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_MYPAGE, 0, 0, ListActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, ListActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, ListActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
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
            case Const.CATEGORY_FOLLOW:
                getSupportActionBar().setTitle(getString(R.string.follow_list));
                mEmptyText.setText(getString(R.string.follow_empty_text));
                followefollowerAdapter = new FollowFollowerAdapter(this);
                mUrl = Const.getFollowAPI(mId);
                break;
            case Const.CATEGORY_FOLLOWER:
                getSupportActionBar().setTitle(getString(R.string.follower_list));
                mEmptyText.setText(getString(R.string.follower_empty_text));
                followefollowerAdapter = new FollowFollowerAdapter(this);
                mUrl = Const.getFollowerAPI(mId);
                break;
            case Const.CATEGORY_USER_CHEER:
                getSupportActionBar().setTitle(getString(R.string.cheer_list));
                mEmptyText.setText(getString(R.string.cheer_empty_text));
                usercheerAdapter = new UserCheerAdapter(this);
                mUrl = Const.getUserCheerAPI(mId);
                break;
            case Const.CATEGORY_WANT:
                getSupportActionBar().setTitle(getString(R.string.want_list));
                mEmptyText.setText(getString(R.string.want_empty_text));
                wantAdapter = new WantAdapter(this);
                mUrl = Const.getWantAPI(mId);
                break;
            case Const.CATEGORY_REST_CHEER:
                getSupportActionBar().setTitle(getString(R.string.cheer_user_list));
                mEmptyText.setText(getString(R.string.usercheer_empty_text));
                restcheerAdapter = new RestCheerAdapter(this);
                mUrl = Const.getRestCheerAPI(mId);
                break;
        }

        getJSON(mUrl, mCategory, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }

        mAppBar.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }

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

    private void getJSON(String url, int category, Context context) {
        switch (category) {
            case Const.CATEGORY_FOLLOW:
                getFollowJSON(url);
                break;
            case Const.CATEGORY_FOLLOWER:
                getFollowerJSON(url);
                break;
            case Const.CATEGORY_USER_CHEER:
                getUserCheerJSON(url);
                break;
            case Const.CATEGORY_WANT:
                getWantJSON(url);
                break;
            case Const.CATEGORY_REST_CHEER:
                getRestCheerJSON(url);
                break;
        }
    }

    private void getRefreshJSON(String url, int category, Context context) {
        switch (category) {
            case Const.CATEGORY_FOLLOW:
                getRefreshFollowJSON(url);
                break;
            case Const.CATEGORY_FOLLOWER:
                getRefreshFollowerJSON(url);
                break;
            case Const.CATEGORY_USER_CHEER:
                getRefreshUserCheerJSON(url);
                break;
            case Const.CATEGORY_WANT:
                getRefreshWantJSON(url);
                break;
            case Const.CATEGORY_REST_CHEER:
                getRefreshRestCheerJSON(url);
                break;
        }
    }

    private void getFollowJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int user_id = jsonObject.getInt("user_id");
                        String username = jsonObject.getString("username");
                        String profile_img = jsonObject.getString("profile_img");
                        int follow_flag = jsonObject.getInt("follow_flag");

                        HeaderData user = new HeaderData();
                        user.setUser_id(user_id);
                        user.setUsername(username);
                        user.setProfile_img(profile_img);
                        user.setFollow_flag(follow_flag);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRecyclerView.setAdapter(followefollowerAdapter);

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getFollowerJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int user_id = jsonObject.getInt("user_id");
                        String username = jsonObject.getString("username");
                        String profile_img = jsonObject.getString("profile_img");
                        int follow_flag = jsonObject.getInt("follow_flag");

                        HeaderData user = new HeaderData();
                        user.setUser_id(user_id);
                        user.setUsername(username);
                        user.setProfile_img(profile_img);
                        user.setFollow_flag(follow_flag);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRecyclerView.setAdapter(followefollowerAdapter);

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getUserCheerJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int rest_id = jsonObject.getInt("rest_id");
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");

                        HeaderData user = new HeaderData();
                        user.setRest_id(rest_id);
                        user.setRestname(restname);
                        user.setLocality(locality);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRecyclerView.setAdapter(usercheerAdapter);

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getWantJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int rest_id = jsonObject.getInt("rest_id");
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");

                        HeaderData user = new HeaderData();
                        user.setRest_id(rest_id);
                        user.setRestname(restname);
                        user.setLocality(locality);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRecyclerView.setAdapter(wantAdapter);

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getRestCheerJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int user_id = jsonObject.getInt("user_id");
                        String username = jsonObject.getString("username");
                        String profile_img = jsonObject.getString("profile_img");
                        int follow_flag = jsonObject.getInt("follow_flag");

                        HeaderData user = new HeaderData();
                        user.setUser_id(user_id);
                        user.setUsername(username);
                        user.setProfile_img(profile_img);
                        user.setFollow_flag(follow_flag);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRecyclerView.setAdapter(restcheerAdapter);

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getRefreshFollowJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int user_id = jsonObject.getInt("user_id");
                        String username = jsonObject.getString("username");
                        String profile_img = jsonObject.getString("profile_img");
                        int follow_flag = jsonObject.getInt("follow_flag");

                        HeaderData user = new HeaderData();
                        user.setUser_id(user_id);
                        user.setUsername(username);
                        user.setProfile_img(profile_img);
                        user.setFollow_flag(follow_flag);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                followefollowerAdapter.notifyDataSetChanged();

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mSwipeRefresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshFollowerJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int user_id = jsonObject.getInt("user_id");
                        String username = jsonObject.getString("username");
                        String profile_img = jsonObject.getString("profile_img");
                        int follow_flag = jsonObject.getInt("follow_flag");

                        HeaderData user = new HeaderData();
                        user.setUser_id(user_id);
                        user.setUsername(username);
                        user.setProfile_img(profile_img);
                        user.setFollow_flag(follow_flag);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                followefollowerAdapter.notifyDataSetChanged();

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mSwipeRefresh.setRefreshing(false);
            }
        });

    }

    private void getRefreshUserCheerJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int rest_id = jsonObject.getInt("rest_id");
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");

                        HeaderData user = new HeaderData();
                        user.setRest_id(rest_id);
                        user.setRestname(restname);
                        user.setLocality(locality);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                usercheerAdapter.notifyDataSetChanged();

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mSwipeRefresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshWantJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int rest_id = jsonObject.getInt("rest_id");
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");

                        HeaderData user = new HeaderData();
                        user.setRest_id(rest_id);
                        user.setRestname(restname);
                        user.setLocality(locality);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                wantAdapter.notifyDataSetChanged();

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mSwipeRefresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshRestCheerJSON(String url) {
        Application_Gocci.getJsonAsyncHttpClient(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int user_id = jsonObject.getInt("user_id");
                        String username = jsonObject.getString("username");
                        String profile_img = jsonObject.getString("profile_img");
                        int follow_flag = jsonObject.getInt("follow_flag");

                        HeaderData user = new HeaderData();
                        user.setUser_id(user_id);
                        user.setUsername(username);
                        user.setProfile_img(profile_img);
                        user.setFollow_flag(follow_flag);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                restcheerAdapter.notifyDataSetChanged();

                if (users.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeRefresh.setEnabled(i == 0);
    }

    public static class FollowFollowerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.follow_follower_picture)
        ImageView mFollowFollowerPicture;
        @Bind(R.id.user_name)
        TextView mUserName;
        @Bind(R.id.add_follow_button)
        ImageView mAddFollowButton;
        @Bind(R.id.delete_follow_button)
        ImageView mDeleteFollowButton;
        @Bind(R.id.account_button)
        RippleView mAccountRipple;

        public FollowFollowerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class UserCheerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cheer_picture)
        ImageView mCheerPicture;
        @Bind(R.id.rest_name)
        TextView mRestName;
        @Bind(R.id.locality)
        TextView mLocality;

        public UserCheerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class WantViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.want_picture)
        ImageView mWantPicture;
        @Bind(R.id.rest_name)
        TextView mRestName;
        @Bind(R.id.locality)
        TextView mLocality;
        @Bind(R.id.delete_want_button)
        ImageView mDeleteWantButton;
        @Bind(R.id.add_want_button)
        ImageView mAddWantButton;
        @Bind(R.id.want_button)
        RippleView mWantRipple;

        public WantViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class RestCheerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tenpo_cheer_picture)
        ImageView mTenpoCheerPicture;
        @Bind(R.id.user_name)
        TextView mUserName;
        @Bind(R.id.add_follow_button)
        ImageView mAddFollowButton;
        @Bind(R.id.delete_follow_button)
        ImageView mDeleteFollowButton;
        @Bind(R.id.account_button)
        RippleView mAccountRipple;

        public RestCheerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class FollowFollowerAdapter extends RecyclerView.Adapter<FollowFollowerViewHolder> {

        private Context mContext;

        public FollowFollowerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public FollowFollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.cell_follow_follower, parent, false);
            return new FollowFollowerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(FollowFollowerViewHolder viewHolder, final int position) {
            final HeaderData user = users.get(position);

            viewHolder.mUserName.setText(user.getUsername());

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.mFollowFollowerPicture);

            viewHolder.mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });

            viewHolder.mFollowFollowerPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });


            switch (mCategory) {
                case Const.CATEGORY_FOLLOW:
                    if (isMypage == 1) {
                        viewHolder.mDeleteFollowButton.setVisibility(View.VISIBLE);
                        final FollowFollowerViewHolder finalViewHolder = viewHolder;
                        viewHolder.mAccountRipple.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (finalViewHolder.mDeleteFollowButton.isShown()) {
                                    finalViewHolder.mDeleteFollowButton.setVisibility(View.INVISIBLE);
                                    finalViewHolder.mAddFollowButton.setVisibility(View.VISIBLE);
                                    Util.unfollowAsync(ListActivity.this, user);
                                } else {
                                    finalViewHolder.mDeleteFollowButton.setVisibility(View.VISIBLE);
                                    finalViewHolder.mAddFollowButton.setVisibility(View.INVISIBLE);
                                    Util.followAsync(ListActivity.this, user);
                                }
                            }
                        });
                    }
                    break;
                case Const.CATEGORY_FOLLOWER:
                    if (isMypage == 1) {
                        if (user.getFollow_flag() == 0) {
                            viewHolder.mAddFollowButton.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.mDeleteFollowButton.setVisibility(View.VISIBLE);
                        }
                        final FollowFollowerViewHolder finalViewHolder1 = viewHolder;
                        viewHolder.mAccountRipple.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (finalViewHolder1.mAddFollowButton.isShown()) {
                                    finalViewHolder1.mAddFollowButton.setVisibility(View.INVISIBLE);
                                    finalViewHolder1.mDeleteFollowButton.setVisibility(View.VISIBLE);
                                    Util.followAsync(ListActivity.this, user);
                                } else {
                                    finalViewHolder1.mAddFollowButton.setVisibility(View.VISIBLE);
                                    finalViewHolder1.mDeleteFollowButton.setVisibility(View.INVISIBLE);
                                    Util.unfollowAsync(ListActivity.this, user);
                                }
                            }
                        });
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    public class UserCheerAdapter extends RecyclerView.Adapter<UserCheerViewHolder> {

        private Context mContext;

        public UserCheerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public UserCheerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.cell_cheer, parent, false);
            return new UserCheerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(UserCheerViewHolder viewHolder, final int position) {
            final HeaderData user = users.get(position);

            viewHolder.mRestName.setText(user.getRestname());
            viewHolder.mLocality.setText(user.getLocality());

            /*
            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);
                    */

            viewHolder.mRestName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });

            viewHolder.mCheerPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    public class WantAdapter extends RecyclerView.Adapter<WantViewHolder> {
        private Context mContext;

        public WantAdapter(Context context) {
            mContext = context;
        }

        @Override
        public WantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.cell_want, parent, false);
            return new WantViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WantViewHolder viewHolder, final int position) {
            final HeaderData user = users.get(position);

            viewHolder.mRestName.setText(user.getRestname());
            viewHolder.mLocality.setText(user.getLocality());

            /*
            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);
                    */

            viewHolder.mRestName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });

            viewHolder.mWantPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });

            viewHolder.mDeleteWantButton.setVisibility(View.VISIBLE);
            final WantViewHolder finalViewHolder = viewHolder;
            viewHolder.mWantRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder.mDeleteWantButton.isShown()) {
                        finalViewHolder.mDeleteWantButton.setVisibility(View.INVISIBLE);
                        finalViewHolder.mAddWantButton.setVisibility(View.VISIBLE);
                        Util.unwantAsync(ListActivity.this, user);
                    } else {
                        finalViewHolder.mDeleteWantButton.setVisibility(View.VISIBLE);
                        finalViewHolder.mAddWantButton.setVisibility(View.INVISIBLE);
                        Util.wantAsync(ListActivity.this, user);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    public class RestCheerAdapter extends RecyclerView.Adapter<RestCheerViewHolder> {
        private Context mContext;

        public RestCheerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public RestCheerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext)
                    .inflate(R.layout.cell_tenpo_cheer, parent, false);
            return new RestCheerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RestCheerViewHolder viewHolder, final int position) {
            final HeaderData user = users.get(position);

            viewHolder.mUserName.setText(user.getUsername());

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.mTenpoCheerPicture);

            viewHolder.mUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });

            viewHolder.mTenpoCheerPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });

            if (user.getFollow_flag() == 0) {
                viewHolder.mAddFollowButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mDeleteFollowButton.setVisibility(View.VISIBLE);
            }
            final RestCheerViewHolder finalViewHolder1 = viewHolder;
            viewHolder.mAccountRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder1.mAddFollowButton.isShown()) {
                        finalViewHolder1.mAddFollowButton.setVisibility(View.INVISIBLE);
                        finalViewHolder1.mDeleteFollowButton.setVisibility(View.VISIBLE);
                        Util.followAsync(ListActivity.this, user);
                    } else {
                        finalViewHolder1.mAddFollowButton.setVisibility(View.VISIBLE);
                        finalViewHolder1.mDeleteFollowButton.setVisibility(View.INVISIBLE);
                        Util.unfollowAsync(ListActivity.this, user);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}

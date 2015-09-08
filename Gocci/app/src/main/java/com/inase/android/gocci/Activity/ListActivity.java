package com.inase.android.gocci.Activity;

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
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.HeaderData;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private int mCategory;
    private int mId;
    private String mUrl;
    private int isMypage; // 0　マイページでない　１　マイページ

    private ArrayList<HeaderData> users = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout refresh;

    private FollowFollowerAdapter followefollowerAdapter;
    private UserCheerAdapter usercheerAdapter;
    private WantAdapter wantAdapter;
    private RestCheerAdapter restcheerAdapter;

    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

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

        Intent intent = getIntent();
        mCategory = intent.getIntExtra("category", 0);
        mId = intent.getIntExtra("id", 0);
        isMypage = intent.getIntExtra("check", 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refresh.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                if (Util.getConnectedState(ListActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshJSON(mUrl, mCategory, ListActivity.this);
                } else {
                    Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    refresh.setRefreshing(false);
                }
            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
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
                followefollowerAdapter = new FollowFollowerAdapter(this);
                mUrl = Const.getFollowAPI(mId);
                break;
            case Const.CATEGORY_FOLLOWER:
                getSupportActionBar().setTitle(getString(R.string.follower_list));
                followefollowerAdapter = new FollowFollowerAdapter(this);
                mUrl = Const.getFollowerAPI(mId);
                break;
            case Const.CATEGORY_USER_CHEER:
                getSupportActionBar().setTitle(getString(R.string.cheer_list));
                usercheerAdapter = new UserCheerAdapter(this);
                mUrl = Const.getUserCheerAPI(mId);
                break;
            case Const.CATEGORY_WANT:
                getSupportActionBar().setTitle(getString(R.string.want_list));
                wantAdapter = new WantAdapter(this);
                mUrl = Const.getWantAPI(mId);
                break;
            case Const.CATEGORY_REST_CHEER:
                getSupportActionBar().setTitle(getString(R.string.cheer_user_list));
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

        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }

        appBarLayout.addOnOffsetChangedListener(this);
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
        Snackbar.make(coordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
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
                getFollowJSON(url, context);
                break;
            case Const.CATEGORY_FOLLOWER:
                getFollowerJSON(url, context);
                break;
            case Const.CATEGORY_USER_CHEER:
                getUserCheerJSON(url, context);
                break;
            case Const.CATEGORY_WANT:
                getWantJSON(url, context);
                break;
            case Const.CATEGORY_REST_CHEER:
                getRestCheerJSON(url, context);
                break;
        }
    }

    private void getRefreshJSON(String url, int category, Context context) {
        switch (category) {
            case Const.CATEGORY_FOLLOW:
                getRefreshFollowJSON(url, context);
                break;
            case Const.CATEGORY_FOLLOWER:
                getRefreshFollowerJSON(url, context);
                break;
            case Const.CATEGORY_USER_CHEER:
                getRefreshUserCheerJSON(url, context);
                break;
            case Const.CATEGORY_WANT:
                getRefreshWantJSON(url, context);
                break;
            case Const.CATEGORY_REST_CHEER:
                getRefreshRestCheerJSON(url, context);
                break;
        }
    }

    private void getFollowJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getFollowerJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getUserCheerJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getWantJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getRestCheerJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getRefreshFollowJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshFollowerJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }
        });

    }

    private void getRefreshUserCheerJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshWantJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshRestCheerJSON(String url, Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(ListActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        refresh.setEnabled(i == 0);
    }

    public static class FollowFollowerViewHolder extends RecyclerView.ViewHolder {
        ImageView userpicture;
        TextView username;
        ImageView addfollowButton;
        ImageView deletefollowButton;
        RippleView accountRipple;

        public FollowFollowerViewHolder(View view) {
            super(view);
            this.userpicture = (ImageView) view.findViewById(R.id.follower_followee_picture);
            this.username = (TextView) view.findViewById(R.id.username);
            this.addfollowButton = (ImageView) view.findViewById(R.id.addfollowButton);
            this.deletefollowButton = (ImageView) view.findViewById(R.id.deletefollowButton);
            this.accountRipple = (RippleView) view.findViewById(R.id.accountButton);
        }
    }

    public static class UserCheerViewHolder extends RecyclerView.ViewHolder {
        ImageView restpicture;
        TextView restname;
        TextView locality;
        //ImageView deletecheerButton;
        //RippleView cheerRipple;

        public UserCheerViewHolder(View view) {
            super(view);
            this.restpicture = (ImageView) view.findViewById(R.id.cheer_picture);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.locality = (TextView) view.findViewById(R.id.locality);
            //this.deletecheerButton = (ImageView) view.findViewById(R.id.deleteCheerButton);
            //this.cheerRipple = (RippleView) view.findViewById(R.id.cheerButton);
        }
    }

    public static class WantViewHolder extends RecyclerView.ViewHolder {
        ImageView restpicture;
        TextView restname;
        TextView locality;
        ImageView deletewantButton;
        ImageView addwantButton;
        RippleView wantRipple;

        public WantViewHolder(View view) {
            super(view);
            this.restpicture = (ImageView) view.findViewById(R.id.want_picture);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.deletewantButton = (ImageView) view.findViewById(R.id.deletewantButton);
            this.addwantButton = (ImageView) view.findViewById(R.id.addwantButton);
            this.wantRipple = (RippleView) view.findViewById(R.id.wantButton);
        }
    }

    public static class RestCheerViewHolder extends RecyclerView.ViewHolder {
        ImageView userpicture;
        TextView username;
        ImageView addfollowButton;
        ImageView deletefollowButton;
        RippleView accountRipple;

        public RestCheerViewHolder(View view) {
            super(view);
            this.userpicture = (ImageView) view.findViewById(R.id.tenpo_cheer_picture);
            this.username = (TextView) view.findViewById(R.id.username);
            this.addfollowButton = (ImageView) view.findViewById(R.id.addfollowButton);
            this.deletefollowButton = (ImageView) view.findViewById(R.id.deletefollowButton);
            this.accountRipple = (RippleView) view.findViewById(R.id.accountButton);
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

            viewHolder.username.setText(user.getUsername());

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.userpicture);

            viewHolder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });

            viewHolder.userpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });


            switch (mCategory) {
                case Const.CATEGORY_FOLLOW:
                    if (isMypage == 1) {
                        viewHolder.deletefollowButton.setVisibility(View.VISIBLE);
                        final FollowFollowerViewHolder finalViewHolder = viewHolder;
                        viewHolder.accountRipple.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (finalViewHolder.deletefollowButton.isShown()) {
                                    finalViewHolder.deletefollowButton.setVisibility(View.INVISIBLE);
                                    finalViewHolder.addfollowButton.setVisibility(View.VISIBLE);
                                    Util.unfollowAsync(ListActivity.this, user);
                                } else {
                                    finalViewHolder.deletefollowButton.setVisibility(View.VISIBLE);
                                    finalViewHolder.addfollowButton.setVisibility(View.INVISIBLE);
                                    Util.followAsync(ListActivity.this, user);
                                }
                            }
                        });
                    }
                    break;
                case Const.CATEGORY_FOLLOWER:
                    if (isMypage == 1) {
                        if (user.getFollow_flag() == 0) {
                            viewHolder.addfollowButton.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.deletefollowButton.setVisibility(View.VISIBLE);
                        }
                        final FollowFollowerViewHolder finalViewHolder1 = viewHolder;
                        viewHolder.accountRipple.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (finalViewHolder1.addfollowButton.isShown()) {
                                    finalViewHolder1.addfollowButton.setVisibility(View.INVISIBLE);
                                    finalViewHolder1.deletefollowButton.setVisibility(View.VISIBLE);
                                    Util.followAsync(ListActivity.this, user);
                                } else {
                                    finalViewHolder1.addfollowButton.setVisibility(View.VISIBLE);
                                    finalViewHolder1.deletefollowButton.setVisibility(View.INVISIBLE);
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

            viewHolder.restname.setText(user.getRestname());
            viewHolder.locality.setText(user.getLocality());

            /*
            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);
                    */

            viewHolder.restname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });

            viewHolder.restpicture.setOnClickListener(new View.OnClickListener() {
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

            viewHolder.restname.setText(user.getRestname());
            viewHolder.locality.setText(user.getLocality());

            /*
            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);
                    */

            viewHolder.restname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });

            viewHolder.restpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleTenpoActivity.startTenpoActivity(user.getRest_id(), user.getRestname(), ListActivity.this);
                }
            });

            viewHolder.deletewantButton.setVisibility(View.VISIBLE);
            final WantViewHolder finalViewHolder = viewHolder;
            viewHolder.wantRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder.deletewantButton.isShown()) {
                        finalViewHolder.deletewantButton.setVisibility(View.INVISIBLE);
                        finalViewHolder.addwantButton.setVisibility(View.VISIBLE);
                        Util.unwantAsync(ListActivity.this, user);
                    } else {
                        finalViewHolder.deletewantButton.setVisibility(View.VISIBLE);
                        finalViewHolder.addwantButton.setVisibility(View.INVISIBLE);
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

            viewHolder.username.setText(user.getUsername());

            Picasso.with(mContext)
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.userpicture);

            viewHolder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });

            viewHolder.userpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlexibleUserProfActivity.startUserProfActivity(user.getUser_id(), user.getUsername(), ListActivity.this);
                }
            });

            if (user.getFollow_flag() == 0) {
                viewHolder.addfollowButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.deletefollowButton.setVisibility(View.VISIBLE);
            }
            final RestCheerViewHolder finalViewHolder1 = viewHolder;
            viewHolder.accountRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder1.addfollowButton.isShown()) {
                        finalViewHolder1.addfollowButton.setVisibility(View.INVISIBLE);
                        finalViewHolder1.deletefollowButton.setVisibility(View.VISIBLE);
                        Util.followAsync(ListActivity.this, user);
                    } else {
                        finalViewHolder1.addfollowButton.setVisibility(View.VISIBLE);
                        finalViewHolder1.deletefollowButton.setVisibility(View.INVISIBLE);
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

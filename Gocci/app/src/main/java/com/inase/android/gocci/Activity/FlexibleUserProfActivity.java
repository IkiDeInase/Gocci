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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.inase.android.gocci.Base.SquareImageView;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FlexibleUserProfActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private String mProfUrl;

    private RecyclerView mUserProfRecyclerView;
    private UserProfAdapter mUserProfAdapter;
    private ArrayList<PostData> mUserProfusers = new ArrayList<PostData>();
    private StaggeredGridLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

    private HeaderData headerUserData;

    private int mUser_id;

    private final FlexibleUserProfActivity self = this;

    private Drawer result;

    private Toolbar toolbar;

    private static MobileAnalyticsManager analytics;

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

        setContentView(R.layout.activity_flexible_user_prof);

        Intent userintent = getIntent();
        mUser_id = userintent.getIntExtra("user_id", 0);

        mProfUrl = Const.getUserpageAPI(mUser_id);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        toolbar.setTitle(userintent.getStringExtra("user_name"));
        setSupportActionBar(toolbar);

        mUserProfRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mUserProfAdapter = new UserProfAdapter(FlexibleUserProfActivity.this);
        mUserProfRecyclerView.setLayoutManager(mLayoutManager);
        mUserProfRecyclerView.setHasFixedSize(true);
        mUserProfRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        getSignupAsync(FlexibleUserProfActivity.this);

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Util.getConnectedState(FlexibleUserProfActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(FlexibleUserProfActivity.this);
                } else {
                    Toast.makeText(FlexibleUserProfActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
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

        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
        BusHolder.get().register(self);

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
        Snackbar.make(coordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mProfUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleUserProfActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerUserData = HeaderData.createUserHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mUserProfusers.add(PostData.createPostData(post));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUserProfRecyclerView.setAdapter(mUserProfAdapter);
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mProfUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(FlexibleUserProfActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mUserProfusers.clear();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerUserData = HeaderData.createUserHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mUserProfusers.add(PostData.createPostData(post));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUserProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    static class UserProfHeaderViewHolder extends RecyclerView.ViewHolder {
        private ImageView userprof_background;
        private ImageView userprof_picture;
        private ImageView locationButton;
        private TextView userprof_username;
        private RippleView userprof_follow;
        private TextView follow_num;
        private TextView follower_num;
        private TextView usercheer_num;
        private TextView follow_text;
        private RippleView followRipple;
        private RippleView followerRipple;
        private RippleView usercheerRipple;

        public UserProfHeaderViewHolder(View view) {
            super(view);
            userprof_background = (ImageView) view.findViewById(R.id.userprof_background);
            userprof_picture = (ImageView) view.findViewById(R.id.userprof_picture);
            locationButton = (ImageView) view.findViewById(R.id.location);
            userprof_username = (TextView) view.findViewById(R.id.userprof_username);
            userprof_follow = (RippleView) view.findViewById(R.id.userprof_follow);
            follow_num = (TextView) view.findViewById(R.id.follow_num);
            follower_num = (TextView) view.findViewById(R.id.follower_num);
            usercheer_num = (TextView) view.findViewById(R.id.usercheer_num);
            follow_text = (TextView) view.findViewById(R.id.followText);
            followRipple = (RippleView) view.findViewById(R.id.followRipple);
            followerRipple = (RippleView) view.findViewById(R.id.followerRipple);
            usercheerRipple = (RippleView) view.findViewById(R.id.usercheerRipple);
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        private SquareImageView squareImage;

        public GridViewHolder(View view) {
            super(view);
            squareImage = (SquareImageView) view.findViewById(R.id.squareImage);
        }
    }

    public class UserProfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private int cellSize;

        private boolean lockedAnimations = false;
        private long profileHeaderAnimationStartTime = 0;
        private int lastAnimatedItem = 0;

        public static final int TYPE_PROFILE_HEADER = 0;
        public static final int TYPE_POST = 1;

        public UserProfAdapter(Context context) {
            mContext = context;
            this.cellSize = Util.getScreenWidth(context) / 3;
        }

        public PostData getItem(int position) {
            return mUserProfusers.get(position);
        }

        public boolean isEmpty() {
            return mUserProfusers.isEmpty();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PROFILE_HEADER;
            } else {
                return TYPE_POST;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (TYPE_PROFILE_HEADER == viewType) {
                final View view = LayoutInflater.from(mContext).inflate(R.layout.view_header_userprof, parent, false);
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return new UserProfHeaderViewHolder(view);
            } else {
                final View view = LayoutInflater.from(mContext).inflate(R.layout.cell_grid, parent, false);
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                layoutParams.height = cellSize;
                layoutParams.width = cellSize;
                layoutParams.setFullSpan(false);
                view.setLayoutParams(layoutParams);
                return new GridViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (TYPE_PROFILE_HEADER == viewType) {
                bindHeader((UserProfHeaderViewHolder) holder);
            } else {
                PostData users = mUserProfusers.get(position - 1);
                bindPost((GridViewHolder) holder, position, users);
            }
        }

        private void bindHeader(final UserProfHeaderViewHolder holder) {
            holder.userprof_username.setText(headerUserData.getUsername());

            holder.follow_num.setText(String.valueOf(headerUserData.getFollow_num()));
            holder.follower_num.setText(String.valueOf(headerUserData.getFollower_num()));
            holder.usercheer_num.setText(String.valueOf(headerUserData.getCheer_num()));

            holder.followRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 0, Const.CATEGORY_FOLLOW, FlexibleUserProfActivity.this);
                }
            });

            holder.followerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 0, Const.CATEGORY_FOLLOWER, FlexibleUserProfActivity.this);
                }
            });

            holder.usercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 0, Const.CATEGORY_USER_CHEER, FlexibleUserProfActivity.this);
                }
            });

            Picasso.with(FlexibleUserProfActivity.this)
                    .load(headerUserData.getProfile_img())
                    .fit()
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.userprof_picture);

            if (headerUserData.getFollow_flag() == 0) {
                holder.follow_text.setText(getString(R.string.do_follow));
            } else {
                holder.follow_text.setText(getString(R.string.do_unfollow));
            }

            if (headerUserData.getUsername().equals(SavedData.getServerName(mContext))) {
                holder.follow_text.setText(getString(R.string.do_yours));
            }

            holder.userprof_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //お気に入りするときの処理
                    switch (holder.follow_text.getText().toString()) {
                        case "フォローする":
                            Util.followAsync(FlexibleUserProfActivity.this, headerUserData);
                            holder.follow_text.setText(getString(R.string.do_unfollow));
                            break;
                        case "フォロー解除する":
                            Util.unfollowAsync(FlexibleUserProfActivity.this, headerUserData);
                            holder.follow_text.setText(getString(R.string.do_follow));
                            break;
                        case "これはあなたです":
                            break;
                    }
                }
            });

            holder.locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfMapActivity.startProfMapActivity(mUserProfusers, FlexibleUserProfActivity.this);
                }
            });
        }

        private void bindPost(final GridViewHolder holder, final int position, final PostData user) {
            Picasso.with(mContext)
                    .load(user.getThumbnail())
                    .resize(cellSize, cellSize)
                    .centerCrop()
                    .into(holder.squareImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //animatePhoto(holder);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            holder.squareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentActivity.startCommentActivity(Integer.parseInt(user.getPost_id()), FlexibleUserProfActivity.this);
                }
            });

            if (lastAnimatedItem < position) lastAnimatedItem = position;
        }

        @Override
        public int getItemCount() {
            return mUserProfusers.size() + 1;
        }

    }
}

package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.andexert.library.RippleView;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareImageView;
import com.inase.android.gocci.Base.ToukouPopup;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.View.NotificationListView;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.HeaderData;
import com.inase.android.gocci.data.PostData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GocciMyprofActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private final GocciMyprofActivity self = this;
    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.list)
    RecyclerView mProfRecyclerView;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;
    @Bind(R.id.empty_text)
    TextView mEmptyText;
    @Bind(R.id.empty_image)
    ImageView mEmptyImage;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private Drawer result;

    private String mProfUrl;

    private StaggeredGridLayoutManager mLayoutManager;
    public static ArrayList<PostData> mProfusers = new ArrayList<>();
    private HeaderData headerUserData;
    private MyProfileAdapter mMyProfAdapter;

    private TextView mNotificationNumber;
    private ImageView mEditBackground;
    private ImageView mEditPicture;
    private TextView mEditUsername;
    private EditText mEditUsernameEdit;

    private boolean isPicture = false;
    private boolean isName = false;

    private static MobileAnalyticsManager analytics;

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

        setContentView(R.layout.activity_gocci_myprof);
        ButterKnife.bind(this);

        mToolBar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");

        mProfUrl = Const.getUserpageAPI(Integer.parseInt(SavedData.getServerUserId(this)));

        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mProfRecyclerView.setLayoutManager(mLayoutManager);
        mProfRecyclerView.setHasFixedSize(true);
        mProfRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        getSignupAsync(this);//サインアップとJSON

        mSwipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeContainer.setRefreshing(true);
                if (Util.getConnectedState(GocciMyprofActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(GocciMyprofActivity.this);
                } else {
                    Toast.makeText(GocciMyprofActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeContainer.setRefreshing(false);
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

        mAppBar.addOnOffsetChangedListener(this);
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
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(mCoordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
        if (event.mMessage.equals(getString(R.string.videoposting_complete))) {
            getRefreshAsync(GocciMyprofActivity.this);
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

        // バッジの数字を更新。0の場合はバッジを表示させない
        // _unreadHogeCountはAPIなどで通信した結果を格納する想定です

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

    private void getSignupAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mProfUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mProfusers.clear();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerUserData = HeaderData.createUserHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mProfusers.add(PostData.createPostData(post));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mMyProfAdapter = new MyProfileAdapter(context);
                mProfRecyclerView.setAdapter(mMyProfAdapter);

                if (mProfusers.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, mProfUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mProfusers.clear();
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject headerObject = jsonObject.getJSONObject("header");
                    JSONArray postsObject = jsonObject.getJSONArray("posts");

                    headerUserData = HeaderData.createUserHeaderData(headerObject);

                    for (int i = 0; i < postsObject.length(); i++) {
                        JSONObject post = postsObject.getJSONObject(i);
                        mProfusers.add(PostData.createPostData(post));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mMyProfAdapter.notifyDataSetChanged();

                if (mProfusers.isEmpty()) {
                    mEmptyImage.setVisibility(View.VISIBLE);
                    mEmptyText.setVisibility(View.VISIBLE);
                } else {
                    mEmptyImage.setVisibility(View.GONE);
                    mEmptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFinish() {
                mSwipeContainer.setRefreshing(false);
            }
        });
    }

    private void setDeleteDialog(final String post_id, final int position) {
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
                        deleteSignupAsync(GocciMyprofActivity.this, post_id, position);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }

    private void deleteSignupAsync(final Context context, final String post_id, final int position) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getPostDeleteAPI(post_id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");

                    if (message.equals(getString(R.string.delete_post_complete_message))) {
                        mProfusers.remove(position);
                        mMyProfAdapter.notifyDataSetChanged();

                        if (mProfusers.isEmpty()) {
                            mEmptyImage.setVisibility(View.VISIBLE);
                            mEmptyText.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyImage.setVisibility(View.GONE);
                            mEmptyText.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //mMaterialDialog.dismiss();
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeContainer.setEnabled(i == 0);
    }

    static class MyProfHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.myprof_background)
        ImageView mMyprofBackground;
        @Bind(R.id.myprof_picture)
        ImageView mMyprofPicture;
        @Bind(R.id.location)
        ImageView mLocationButton;
        @Bind(R.id.myprof_username)
        TextView mMyprofUsername;
        @Bind(R.id.edit_profile)
        RippleView mEditProfile;
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

        public MyProfHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.square_image)
        SquareImageView mSquareImage;

        public GridViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class MyProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int TYPE_PROFILE_HEADER = 0;
        public static final int TYPE_PHOTO = 1;

        private Context context;
        private int cellSize;

        private boolean lockedAnimations = false;
        private long profileHeaderAnimationStartTime = 0;
        private int lastAnimatedItem = 0;

        public MyProfileAdapter(Context context) {
            this.context = context;
            this.cellSize = Util.getScreenWidth(context) / 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PROFILE_HEADER;
            } else {
                return TYPE_PHOTO;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (TYPE_PROFILE_HEADER == viewType) {
                final View view = LayoutInflater.from(context).inflate(R.layout.view_header_myprof, parent, false);
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                layoutParams.setFullSpan(true);
                view.setLayoutParams(layoutParams);
                return new MyProfHeaderViewHolder(view);
            } else {
                final View view = LayoutInflater.from(context).inflate(R.layout.cell_grid, parent, false);
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
                bindHeader((MyProfHeaderViewHolder) holder);
            } else {
                PostData users = mProfusers.get(position - 1);
                bindPhoto((GridViewHolder) holder, position, users);
            }
        }

        private void bindHeader(final MyProfHeaderViewHolder holder) {
            final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            holder.mMyprofUsername.setText(headerUserData.getUsername());
            Picasso.with(context)
                    .load(headerUserData.getProfile_img())
                    .fit()
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.mMyprofPicture);

            holder.mFollowNum.setText(String.valueOf(headerUserData.getFollow_num()));
            holder.mFollowerNum.setText(String.valueOf(headerUserData.getFollower_num()));
            holder.mUsercheerNum.setText(String.valueOf(headerUserData.getCheer_num()));
            holder.mWantNum.setText(String.valueOf(headerUserData.getWant_num()));

            holder.mFollowRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_FOLLOW, GocciMyprofActivity.this);
                }
            });

            holder.mFollowerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_FOLLOWER, GocciMyprofActivity.this);
                }
            });

            holder.mUsercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_USER_CHEER, GocciMyprofActivity.this);
                }
            });

            holder.mWantRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_WANT, GocciMyprofActivity.this);
                }
            });

            holder.mEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(context)
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
                                        post_date = SavedData.getServerUserId(context) + "_" + Util.getDateTimeString();
                                        update_file = Util.getLocalBitmapFile(mEditPicture, post_date);
                                        updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_BOTH,
                                                mEditUsername.getText().toString(),
                                                post_date);
                                        isName = false;
                                        isPicture = false;
                                    } else if (isPicture) {
                                        //写真だけ変更
                                        post_date = SavedData.getServerUserId(context) + "_" + Util.getDateTimeString();
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
                                        postChangeProfileAsync(context, post_date, update_file, updateUrl);
                                    }
                                }
                            })
                            .build();

                    mEditPicture = (ImageView) dialog.getCustomView().findViewById(R.id.myprof_picture);
                    mEditUsername = (TextView) dialog.getCustomView().findViewById(R.id.myprof_username);
                    mEditUsernameEdit = (EditText) dialog.getCustomView().findViewById(R.id.myprof_username_edit);

                    Picasso.with(context)
                            .load(SavedData.getServerPicture(context))
                            .fit()
                            .placeholder(R.drawable.ic_userpicture)
                            .transform(new RoundedTransformation())
                            .into(mEditPicture);

                    mEditUsername.setText(SavedData.getServerName(context));
                    mEditUsernameEdit.setHint(SavedData.getServerName(context));

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
                                            Toast.makeText(context, getString(R.string.cheat_input_username), Toast.LENGTH_SHORT).show();
                                            return false;
                                        } else {
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

            holder.mLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfMapActivity.startProfMapActivity(mProfusers, GocciMyprofActivity.this);
                }
            });
        }

        private void bindPhoto(final GridViewHolder holder, final int position, final PostData users) {
            Picasso.with(context)
                    .load(users.getThumbnail())
                    .resize(cellSize, cellSize)
                    .centerCrop()
                    .into(holder.mSquareImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //animatePhoto(holder);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            holder.mSquareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentActivity.startCommentActivity(Integer.parseInt(users.getPost_id()), GocciMyprofActivity.this);
                }
            });

            holder.mSquareImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setDeleteDialog(users.getPost_id(), position - 1);
                    return false;
                }
            });
            if (lastAnimatedItem < position) lastAnimatedItem = position;
        }

        @Override
        public int getItemCount() {
            return mProfusers.size() + 1;
        }

    }

    private void postChangeProfileAsync(final Context context, final String post_date, final File file, final String url) {
        if (file != null || post_date != null) {
            TransferObserver transferObserver = Application_Gocci.getTransfer(context).upload(Const.POST_PHOTO_BUCKET_NAME, post_date + ".png", file);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        postChangeProf(context, url);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        } else {
            postChangeProf(context, url);
        }
    }

    private void postChangeProf(final Context context, String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");

                    if (message.equals(getString(R.string.change_profile_dialog_complete))) {
                        String name = response.getString("username");
                        String picture = response.getString("profile_img");

                        if (name.equals(getString(R.string.change_profile_dialog_error_username))) {
                            SavedData.changeProfile(context, SavedData.getServerName(context), picture);
                            Toast.makeText(context, getString(R.string.change_profile_dialog_error_username_message), Toast.LENGTH_SHORT).show();
                        } else {
                            SavedData.changeProfile(context, name, picture);
                        }

                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();

                        overridePendingTransition(0, 0);
                        startActivity(intent);

                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                mProgressWheel.setVisibility(View.GONE);
            }
        });
    }
}

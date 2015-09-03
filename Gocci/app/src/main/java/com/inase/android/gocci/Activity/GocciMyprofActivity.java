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

public class GocciMyprofActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private final GocciMyprofActivity self = this;

    private TextView notificationNumber;

    private Drawer result;

    private String mProfUrl;

    private RecyclerView mProfRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    public static ArrayList<PostData> mProfusers = new ArrayList<>();
    private HeaderData headerUserData;
    private MyProfileAdapter mMyProfAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressWheel progress;

    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

    private ImageView edit_background;
    private ImageView edit_picture;
    private TextView edit_username;
    private EditText edit_username_edit;

    private boolean isPicture = false;
    private boolean isName = false;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int pastVisibleItems, visibleItemCount, totalItemCount;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mProfUrl = Const.getUserpageAPI(Integer.parseInt(SavedData.getServerUserId(this)));

        mProfRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mProfRecyclerView.setLayoutManager(mLayoutManager);
        mProfRecyclerView.setHasFixedSize(true);
        mProfRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        mProfRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                visibleItemCount = mProfRecyclerView.getChildCount();
//                totalItemCount = mLayoutManager.getItemCount();
//                int[] firstVisibleItems = null;
//                firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
//                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
//                    pastVisibleItems = firstVisibleItems[0];
//                }
//
//                if (loading) {
//                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
//                        loading = false;
//                        //Toast.makeText(getActivity(), "LoadMoreなタイミング", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        getSignupAsync(this);//サインアップとJSON

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (Util.getConnectedState(GocciMyprofActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(GocciMyprofActivity.this);
                } else {
                    Toast.makeText(GocciMyprofActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
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

        appBarLayout.addOnOffsetChangedListener(this);
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

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        Snackbar.make(coordinatorLayout, event.mMessage, Snackbar.LENGTH_SHORT).show();
        if (event.mMessage.equals(getString(R.string.videoposting_complete))) {
            getRefreshAsync(GocciMyprofActivity.this);
        } else {
            notificationNumber.setVisibility(View.VISIBLE);
            notificationNumber.setText(String.valueOf(event.mNotificationNumber));
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
        notificationNumber = (TextView) view.findViewById(R.id.notification_number);
        int notifications = SavedData.getNotification(this);

        // バッジの数字を更新。0の場合はバッジを表示させない
        // _unreadHogeCountはAPIなどで通信した結果を格納する想定です

        if (notifications == 0) {
            notificationNumber.setVisibility(View.INVISIBLE);
        } else {

            notificationNumber.setText(String.valueOf(notifications));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationNumber.setVisibility(View.INVISIBLE);
                SavedData.setNotification(GocciMyprofActivity.this, 0);
                View notification = new NotificationListView(GocciMyprofActivity.this);

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(GocciMyprofActivity.this);

                View header = notification.findViewById(R.id.headerView);
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
                    .into(edit_picture);
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
            }

            @Override
            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
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
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    static class MyProfHeaderViewHolder extends RecyclerView.ViewHolder {
        private ImageView myprof_background;
        private ImageView myprof_picture;
        private ImageView locationButton;
        private TextView myprof_username;
        private RippleView editProfile;
        private TextView follow_num;
        private TextView follower_num;
        private TextView usercheer_num;
        private TextView want_num;
        private RippleView followRipple;
        private RippleView followerRipple;
        private RippleView usercheerRipple;
        private RippleView wantRipple;

        public MyProfHeaderViewHolder(View view) {
            super(view);
            myprof_background = (ImageView) view.findViewById(R.id.myprof_background);
            myprof_picture = (ImageView) view.findViewById(R.id.myprof_picture);
            locationButton = (ImageView) view.findViewById(R.id.location);
            myprof_username = (TextView) view.findViewById(R.id.myprof_username);
            editProfile = (RippleView) view.findViewById(R.id.editProfile);
            follow_num = (TextView) view.findViewById(R.id.follow_num);
            follower_num = (TextView) view.findViewById(R.id.follower_num);
            usercheer_num = (TextView) view.findViewById(R.id.usercheer_num);
            want_num = (TextView) view.findViewById(R.id.want_num);
            followRipple = (RippleView) view.findViewById(R.id.followRipple);
            followerRipple = (RippleView) view.findViewById(R.id.followerRipple);
            usercheerRipple = (RippleView) view.findViewById(R.id.usercheerRipple);
            wantRipple = (RippleView) view.findViewById(R.id.wantRipple);
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        private SquareImageView squareImage;

        public GridViewHolder(View view) {
            super(view);
            squareImage = (SquareImageView) view.findViewById(R.id.squareImage);
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
            holder.myprof_username.setText(headerUserData.getUsername());
            Picasso.with(context)
                    .load(headerUserData.getProfile_img())
                    .fit()
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(holder.myprof_picture);

            holder.follow_num.setText(String.valueOf(headerUserData.getFollow_num()));
            holder.follower_num.setText(String.valueOf(headerUserData.getFollower_num()));
            holder.usercheer_num.setText(String.valueOf(headerUserData.getCheer_num()));
            holder.want_num.setText(String.valueOf(headerUserData.getWant_num()));

            holder.followRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_FOLLOW, GocciMyprofActivity.this);
                }
            });

            holder.followerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_FOLLOWER, GocciMyprofActivity.this);
                }
            });

            holder.usercheerRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_USER_CHEER, GocciMyprofActivity.this);
                }
            });

            holder.wantRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    ListActivity.startListActivity(headerUserData.getUser_id(), 1, Const.CATEGORY_WANT, GocciMyprofActivity.this);
                }
            });

            holder.editProfile.setOnClickListener(new View.OnClickListener() {
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
                                    progress.setVisibility(View.VISIBLE);

                                    String updateUrl = null;
                                    String post_date = null;
                                    File update_file = null;

                                    if (isName && isPicture) {
                                        //どっちも変更した
                                        post_date = SavedData.getServerUserId(context) + "_" + Util.getDateTimeString();
                                        update_file = Util.getLocalBitmapFile(edit_picture, post_date);
                                        updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_BOTH,
                                                edit_username.getText().toString(),
                                                post_date);
                                        isName = false;
                                        isPicture = false;
                                    } else if (isPicture) {
                                        //写真だけ変更
                                        post_date = SavedData.getServerUserId(context) + "_" + Util.getDateTimeString();
                                        update_file = Util.getLocalBitmapFile(edit_picture, post_date);
                                        updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_PICTURE,
                                                null, post_date);
                                        isPicture = false;
                                    } else if (isName) {
                                        //名前だけ
                                        updateUrl = Const.getPostUpdateProfileAPI(Const.FLAG_CHANGE_NAME,
                                                edit_username.getText().toString(), null);
                                        isName = false;
                                    }

                                    if (updateUrl != null) {
                                        postChangeProfileAsync(context, post_date, update_file, updateUrl);
                                    }
                                }
                            })
                            .build();

                    edit_picture = (ImageView) dialog.getCustomView().findViewById(R.id.myprof_picture);
                    edit_username = (TextView) dialog.getCustomView().findViewById(R.id.myprof_username);
                    edit_username_edit = (EditText) dialog.getCustomView().findViewById(R.id.myprof_username_edit);

                    Picasso.with(context)
                            .load(SavedData.getServerPicture(context))
                            .fit()
                            .placeholder(R.drawable.ic_userpicture)
                            .transform(new RoundedTransformation())
                            .into(edit_picture);

                    edit_username.setText(SavedData.getServerName(context));
                    edit_username_edit.setHint(SavedData.getServerName(context));

                    edit_picture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Crop.pickImage(GocciMyprofActivity.this);
                        }
                    });
                    edit_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            edit_username.setVisibility(View.GONE);
                            edit_username_edit.setVisibility(View.VISIBLE);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            edit_username_edit.setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode, KeyEvent event) {
                                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                        if (edit_username_edit.getText().toString().isEmpty()) {
                                            Toast.makeText(context, getString(R.string.cheat_input_username), Toast.LENGTH_SHORT).show();
                                            return false;
                                        } else {
                                            inputMethodManager.hideSoftInputFromWindow(edit_username_edit.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                            edit_username.setText(edit_username_edit.getText().toString());
                                            edit_username.setVisibility(View.VISIBLE);
                                            edit_username_edit.setVisibility(View.GONE);
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

            holder.locationButton.setOnClickListener(new View.OnClickListener() {
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
                    CommentActivity.startCommentActivity(Integer.parseInt(users.getPost_id()), GocciMyprofActivity.this);
                }
            });

            holder.squareImage.setOnLongClickListener(new View.OnLongClickListener() {
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
                progress.setVisibility(View.GONE);
            }
        });
    }
}

package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.inase.android.gocci.Base.SquareImageView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.HlsRendererBuilder;
import com.inase.android.gocci.VideoPlayer.SquareExoVideoView;
import com.inase.android.gocci.VideoPlayer.VideoPlayer;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.inase.android.gocci.data.PostData;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.FilterTimelineEvent;
import com.inase.android.gocci.ui.view.DrawerProfHeader;
import com.konifar.fab_transformation.FabTransformation;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class GridSearchActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, AudioCapabilitiesReceiver.Listener {

    @Bind(R.id.tool_bar)
    Toolbar toolBar;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.list)
    ObservableRecyclerView list;
    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.empty_text)
    TextView emptyText;
    @Bind(R.id.empty_image)
    ImageView emptyImage;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.place)
    TextView place;
    @Bind(R.id.overlay)
    View overlay;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.category_spinner)
    MaterialBetterSpinner categorySpinner;
    @Bind(R.id.value_spinner)
    MaterialBetterSpinner valueSpinner;
    @Bind(R.id.sort_spinner)
    MaterialBetterSpinner sortSpinner;
    @Bind(R.id.filter_ripple)
    RippleView filterRipple;
    @Bind(R.id.sheet)
    CardView sheet;

    @OnClick(R.id.fab)
    public void click() {
        if (fab.getVisibility() == View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
    }

    @OnClick(R.id.overlay)
    public void clickOverlay() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        }
    }

    private Drawer result;
    private double mLongitude;
    private double mLatitude;

    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<SearchGridViewHolder, String> mViewHolderHash;  // Value: PosterId

    private VideoPlayer player;
    private boolean playerNeedsPrepare;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    private StaggeredGridLayoutManager mLayoutManager;
    private ArrayList<PostData> mProfusers = new ArrayList<>();
    private MyProfileAdapter mMyProfAdapter;

    private int previousTotal = 0;
    private int visibleThreshold = 5;
    private int mNextCount = 1;
    private boolean isEndScrioll = false;
    private boolean loading = true;
    private int pastVisibleItems, firstVisivleItems, visibleItemCount, totalItemCount;

    private int category_id = 0;

    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GridSearchActivity activity
                    = (GridSearchActivity) msg.obj;
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

    public static void startGridSearchActivity(Activity startingActivity) {
        Intent intent = new Intent(startingActivity, GridSearchActivity.class);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPlayBlockFlag = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("場所から見つける");

        place.setText("現在地");

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getApplicationContext(), this);
        audioCapabilitiesReceiver.register();

        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        list.setLayoutManager(mLayoutManager);
        list.setHasFixedSize(true);
        list.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mMyProfAdapter = new MyProfileAdapter(this);

        getSignupAsync(this);//サインアップとJSON

        swipeContainer.setColorSchemeResources(R.color.gocci_1, R.color.gocci_2, R.color.gocci_3, R.color.gocci_4);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                if (Util.getConnectedState(GridSearchActivity.this) != Util.NetworkStatus.OFF) {
                    getRefreshAsync(GridSearchActivity.this);
                } else {
                    Toast.makeText(GridSearchActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                }
            }
        });

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolBar)
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
                                        sHandler.obtainMessage(Const.INTENT_TO_TIMELINE, 0, 0, GridSearchActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_ADVICE, 0, 0, GridSearchActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Message msg =
                                        sHandler.obtainMessage(Const.INTENT_TO_SETTING, 0, 0, GridSearchActivity.this);
                                sHandler.sendMessageDelayed(msg, 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                switch (SavedData.getSettingMute(GridSearchActivity.this)) {
                                    case 0:
                                        SavedData.setSettingMute(GridSearchActivity.this, -1);
                                        result.updateName(5, new StringHolder(getString(R.string.setting_support_unmute)));
                                        break;
                                    case -1:
                                        SavedData.setSettingMute(GridSearchActivity.this, 0);
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

        categorySpinner.setVisibility(View.VISIBLE);
        sortSpinner.setVisibility(View.GONE);
        String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        categorySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //category_id = position + 1;
                category_id = position + 2;
            }
        });

        categorySpinner.setAdapter(categoryAdapter);
        filterRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
                //Otto currentpageと絞り込みurl
                getFilterJsonAsync(Const.getCustomGridSearchAPI(mLongitude, mLatitude, 0, category_id));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusHolder.get().register(this);

        if (player == null) {
            if (mPlayingPostId != null) {
                if (Util.isMovieAutoPlay(this)) {

                }
            }
        } else {
            player.setBackgrounded(false);
        }

        appBar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);

        if (player != null) {
            player.blockingClearSurface();
        }
        releasePlayer();

        appBar.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_grid, menu);
        // お知らせ未読件数バッジ表示
        MenuItem pin = menu.findItem(R.id.map_search);
        pin.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MapSearchActivity.startMapSearchActivity(123, GridSearchActivity.this);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String address = bundle.getString("place");
                    mLatitude = bundle.getDouble("lat");
                    mLongitude = bundle.getDouble("lon");

                    place.setText(address);
                    getSearchMapAsync(this, Const.getCustomGridSearchAPI(mLongitude, mLatitude, 0, category_id));
                } else if (resultCode == RESULT_CANCELED) {

                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        if (mPlayingPostId != null) {
            releasePlayer();
            if (Util.isMovieAutoPlay(this)) {

            }
        }
        player.setBackgrounded(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    private void getSignupAsync(final Context context) {
        swipeContainer.setRefreshing(true);
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
                Const.asyncHttpClient.get(context, Const.getCustomGridSearchAPI(mLongitude, mLatitude, 0, category_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject post = response.getJSONObject(i);
                                mProfusers.add(PostData.createDistPostData(post));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        list.setAdapter(mMyProfAdapter);

                        if (mProfusers.isEmpty()) {
                            emptyImage.setVisibility(View.VISIBLE);
                            emptyText.setVisibility(View.VISIBLE);
                        } else {
                            emptyImage.setVisibility(View.GONE);
                            emptyText.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFinish() {
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                category_id = 0;
                categorySpinner.setText("");
                place.setText("現在地");
                Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
                Const.asyncHttpClient.get(context, Const.getCustomGridSearchAPI(mLongitude, mLatitude, 0, category_id), new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        mProfusers.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject post = response.getJSONObject(i);
                                mProfusers.add(PostData.createDistPostData(post));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mPlayingPostId = null;
                        mViewHolderHash.clear();
                        mMyProfAdapter.notifyDataSetChanged();

                        if (mProfusers.isEmpty()) {
                            emptyImage.setVisibility(View.VISIBLE);
                            emptyText.setVisibility(View.VISIBLE);
                        } else {
                            emptyImage.setVisibility(View.GONE);
                            emptyText.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFinish() {
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void getAddJsonAsync(final Context context, final int call) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getCustomGridSearchAPI(mLongitude, mLatitude, call, category_id), new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                //mTimelineSwipe.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() != 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            mProfusers.add(PostData.createDistPostData(jsonObject));
                        }

                        mPlayingPostId = null;
                        mMyProfAdapter.notifyDataSetChanged();

                        mNextCount++;
                    } else {
                        isEndScrioll = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                //mTimelineSwipe.setRefreshing(false);
            }
        });
    }

    private void getSearchMapAsync(final Context context, String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                swipeContainer.setRefreshing(true);
                list.scrollVerticallyToPosition(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                //mTimelineSwipe.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mProfusers.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject post = response.getJSONObject(i);
                        mProfusers.add(PostData.createDistPostData(post));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPlayingPostId = null;
                mViewHolderHash.clear();
                mMyProfAdapter.notifyDataSetChanged();

                if (mProfusers.isEmpty()) {
                    emptyImage.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyImage.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFinish() {
                //mTimelineSwipe.setRefreshing(false);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void getFilterJsonAsync(String url) {
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(this));
        Const.asyncHttpClient.get(this, url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                swipeContainer.setRefreshing(true);
                list.scrollVerticallyToPosition(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(GridSearchActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                //mTimelineSwipe.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mProfusers.clear();
                isEndScrioll = false;
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        mProfusers.add(PostData.createDistPostData(jsonObject));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mMyProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void preparePlayer(final SearchGridViewHolder viewHolder, String path) {
        if (player == null) {
            player = new VideoPlayer(new HlsRendererBuilder(this, com.google.android.exoplayer.util.Util.getUserAgent(this, "Gocci"), path));
            player.addListener(new VideoPlayer.Listener() {
                @Override
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                        case VideoPlayer.STATE_BUFFERING:
                            break;
                        case VideoPlayer.STATE_ENDED:
                            player.seekTo(0);
                            break;
                        case VideoPlayer.STATE_IDLE:
                            break;
                        case VideoPlayer.STATE_PREPARING:
                            break;
                        case VideoPlayer.STATE_READY:
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (e instanceof UnsupportedDrmException) {
                        // Special case DRM failures.
                        UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
                        int stringId = com.google.android.exoplayer.util.Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                                : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                                ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
                        Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
                    }
                    playerNeedsPrepare = true;
                }

                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
                    viewHolder.mSquareImage.setVisibility(View.GONE);
                    viewHolder.mAspectFrame.setAspectRatio(
                            height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
                }
            });
            //player.seekTo(playerPosition);
            playerNeedsPrepare = true;
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(viewHolder.mSquareExoVideo.getHolder().getSurface());
        player.setPlayWhenReady(true);

        if (SavedData.getSettingMute(this) == -1) {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, -1);
        } else {
            player.setSelectedTrack(VideoPlayer.TYPE_AUDIO, 0);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            //playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    private void changeMovie(PostData postData) {
        // TODO:実装
        if (mPlayingPostId != null) {
            // 前回の動画再生停止処理
            final SearchGridViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                oldViewHolder.mSquareImage.setVisibility(View.VISIBLE);
            }

            if (mPlayingPostId.equals(postData.getPost_id())) {
                return;
            }
        }
            mPlayingPostId = postData.getPost_id();
            final SearchGridViewHolder currentViewHolder = getPlayingViewHolder();
            if (mPlayBlockFlag) {
                return;
            }

            final String path = postData.getMovie();
            releasePlayer();
            if (Util.isMovieAutoPlay(GridSearchActivity.this)) {
                preparePlayer(currentViewHolder, path);
            }
    }

    private SearchGridViewHolder getPlayingViewHolder() {
        SearchGridViewHolder viewHolder = null;
        if (mPlayingPostId != null) {
            for (Map.Entry<SearchGridViewHolder, String> entry : mViewHolderHash.entrySet()) {
                if (entry.getValue().equals(mPlayingPostId)) {
                    viewHolder = entry.getKey();
                    break;
                }
            }
        }
        return viewHolder;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        swipeContainer.setEnabled(i == 0);
    }

    static class SearchGridViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.video_thumbnail)
        SquareImageView mSquareImage;
        @Bind(R.id.square_video_exo)
        SquareExoVideoView mSquareExoVideo;
        @Bind(R.id.video_frame)
        AspectRatioFrameLayout mAspectFrame;
        @Bind(R.id.restname)
        TextView mRestname;
        @Bind(R.id.distance)
        TextView mDistance;

        public SearchGridViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class MyProfileAdapter extends RecyclerView.Adapter<SearchGridViewHolder> {

        private Context context;
        private int cellSize;

        private boolean lockedAnimations = false;
        private long profileHeaderAnimationStartTime = 0;
        private int lastAnimatedItem = 0;

        public MyProfileAdapter(Context context) {
            this.context = context;
            this.cellSize = Util.getScreenWidth(context) / 2;
        }

        @Override
        public SearchGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.cell_search_grid, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            layoutParams.setFullSpan(false);
            view.setLayoutParams(layoutParams);
            return new SearchGridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SearchGridViewHolder holder, int position) {
            final PostData user = mProfusers.get(position);
            Picasso.with(context)
                    .load(user.getThumbnail())
                    .resize(cellSize, cellSize)
                    .centerCrop()
                    .into(holder.mSquareImage);

            holder.mSquareImage.setVisibility(View.VISIBLE);

            holder.mRestname.setText(user.getRestname());
            holder.mDistance.setText(getDist(user.getDistance()));

            holder.mAspectFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (player != null && mPlayingPostId.equals(user.getPost_id())) {
                            if (player.getPlayerControl().isPlaying()) {
                                player.getPlayerControl().pause();
                            } else {
                                player.getPlayerControl().start();
                            }
                        } else {
                            changeMovie(user);
                        }
                }
            });

            holder.mAspectFrame.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CommentActivity.startCommentActivity(Integer.parseInt(user.getPost_id()), GridSearchActivity.this);
                    return false;
                }
            });

            if (lastAnimatedItem < position) lastAnimatedItem = position;
            mViewHolderHash.put(holder, user.getPost_id());
        }

        @Override
        public int getItemCount() {
            return mProfusers.size();
        }

        private String getDist(int distance) {
            String dist = null;
            if (distance > 1000) {
                dist = distance/1000 + "km";
            } else {
                dist = distance + "m";
            }
            return dist;
        }
    }
}

package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.facebook.login.LoginManager;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.NotificationNumberEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CommentView;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlexibleTenpoActivity extends AppCompatActivity implements ObservableScrollViewCallbacks, AbsListView.OnScrollListener, CacheManager.ICacheManagerListener {

    private String mTenpoUrl;
    private String mPost_restname;
    private String mPost_locality;
    private String mPhoneNumber;
    private String mHomepage;
    private String mCategory;
    private String mEncoderestname;
    private String clickedUsername;
    private String clickedUserpicture;
    private String clickedUserbackground;

    private double mLat;
    private double mLon;

    private int mWant_flag;
    private int mTotal_cheer_num;

    private ProgressWheel tenpoprogress;
    private ArrayList<UserData> mTenpousers = new ArrayList<UserData>();
    private TenpoAdapter mTenpoAdapter;
    private SwipyRefreshLayout mTenpoSwipe;
    private ObservableListView mTenpoListView;

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private AsyncHttpClient httpClient3;

    private MapView mMapView;
    private GoogleMap mMap;

    private AttributeSet mVideoAttr;
    private Point mDisplaySize;
    private CacheManager mCacheManager;
    private String mPlayingPostId;
    private boolean mPlayBlockFlag;
    private ConcurrentHashMap<ViewHolder, String> mViewHolderHash;  // Value: PosterId

    private boolean isExist = false;
    private boolean isSee = false;

    private final FlexibleTenpoActivity self = this;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.e("DEBUG", "onGlobalLayout called: " + mPlayingPostId);
            if (isSee) {
            changeMovie();
            }
            Log.e("DEBUG", "onGlobalLayout  changeMovie called: " + mPlayingPostId);
            if (mPlayingPostId != null || !isExist) {
                mTenpoListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    };

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCacheManager = CacheManager.getInstance(getApplicationContext());
        // 画面回転に対応するならonResumeが安全かも
        mDisplaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(mDisplaySize);

        mPlayBlockFlag = false;

        // 初期化処理
        mPlayingPostId = null;
        mViewHolderHash = new ConcurrentHashMap<>();

        setContentView(R.layout.activity_flexible_tenpo);

        Intent intent = getIntent();
        mPost_restname = intent.getStringExtra("restname");
        mPost_locality = intent.getStringExtra("locality");
        mLat = intent.getDoubleExtra("lat", 35.71012566481748);
        mLon = intent.getDoubleExtra("lon", 139.81149673461914);
        mPhoneNumber = intent.getStringExtra("phone");
        mHomepage = intent.getStringExtra("homepage");
        mCategory = intent.getStringExtra("category");
        mWant_flag = intent.getIntExtra("want_flag", 0);
        mTotal_cheer_num = intent.getIntExtra("total_cheer_num", 0);

        try {
            mEncoderestname = URLEncoder.encode(mPost_restname, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        EventDateRecorder profilerecorder = EventDateRecorder.load(FlexibleTenpoActivity.this, "use_first_tenpo");
        if (!profilerecorder.didRecorded()) {
            // 機能が１度も利用されてない時のみ実行したい処理を書く
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FlexibleTenpoActivity.this);
            Effectstype effect = Effectstype.SlideBottom;
            dialogBuilder
                    .withTitle("店舗画面")
                    .withMessage("店舗に投稿されている動画を見ることができます")
                    .withDuration(500)                                          //def
                    .withEffect(effect)
                    .isCancelableOnTouchOutside(true)
                    .show();

            profilerecorder.record();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        View header = new DrawerProfHeader(this);

        Drawer.Result result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("ライフログ").withIcon(GoogleMaterial.Icon.gmd_event).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName("お店を検索する").withIcon(GoogleMaterial.Icon.gmd_explore).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイプロフィール").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(4).withCheckable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("アプリに関する要望を送る").withCheckable(false).withIdentifier(5),
                        new SecondaryDrawerItem().withName("利用規約とポリシー").withCheckable(false).withIdentifier(6),
                        new SecondaryDrawerItem().withName("ログアウト").withCheckable(false).withIdentifier(7)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Handler handler = new Handler();
                                handler.postDelayed(new timelineClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Handler handler = new Handler();
                                handler.postDelayed(new lifelogClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Handler handler = new Handler();
                                handler.postDelayed(new searchClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Handler handler = new Handler();
                                handler.postDelayed(new myprofClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                Handler handler = new Handler();
                                handler.postDelayed(new adviceClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 6) {
                                Handler handler = new Handler();
                                handler.postDelayed(new policyClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 7) {
                                Handler handler = new Handler();
                                handler.postDelayed(new logoutClickHandler(), 500);
                            }
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(-1)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        finish();
                        return true;
                    }
                })
                .build();

        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTenpoUrl = "http://api-gocci.jp/restpage/?restname=" + mEncoderestname;

        final RequestParams param = new RequestParams("restname", mPost_restname);

        tenpoprogress = (ProgressWheel) findViewById(R.id.tenpoprogress_wheel);
        mTenpoListView = (ObservableListView) findViewById(R.id.list);
        mTenpoSwipe = (SwipyRefreshLayout) findViewById(R.id.swipe_container);

        mTenpoListView.setOnScrollListener(this);
        mTenpoListView.setScrollViewCallbacks(this);
        mTenpoListView.setDivider(null);
        mTenpoListView.setVerticalScrollBarEnabled(false);
        mTenpoListView.setSelector(android.R.color.transparent);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTenpoListView.addHeaderView(inflater.inflate(R.layout.view_header_tenpo, null));

        TextView tenpo_name = (TextView) findViewById(R.id.tenpo_name);
        TextView tenpo_category = (TextView) findViewById(R.id.category);
        TextView tenpo_number = (TextView) findViewById(R.id.cheer_number);
        ImageView tenpo_flag = (ImageView) findViewById(R.id.cheer_flag);
        RippleView checkRipple = (RippleView) findViewById(R.id.checkRipple);
        final ImageView check_Image = (ImageView) findViewById(R.id.check_image);
        final TextView check_text = (TextView) findViewById(R.id.check_text);
        RippleView callRipple = (RippleView) findViewById(R.id.callRipple);
        RippleView gohereRipple = (RippleView) findViewById(R.id.gohereRipple);
        RippleView etcRipple = (RippleView) findViewById(R.id.etcRipple);

        if (mWant_flag == 0) {
            check_Image.setImageResource(R.drawable.ic_like_white);
            check_text.setText("行きたい店に認定");
        } else {
            check_Image.setImageResource(R.drawable.ic_favorite_orange);
            check_text.setText("行きたい店を取消");
        }
        tenpo_number.setText(String.valueOf(mTotal_cheer_num));

        tenpo_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        checkRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_text.getText().toString().equals("行きたい店に認定")) {
                    check_Image.setImageResource(R.drawable.ic_favorite_orange);
                    check_text.setText("行きたい店を取消");
                    final AsyncHttpClient client = new AsyncHttpClient();
                    client.setCookieStore(SavedData.getCookieStore(FlexibleTenpoActivity.this));
                    client.post(FlexibleTenpoActivity.this, Const.URL_RESTRAUNT_FOLLOW, param, new JsonHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                    check_Image.setImageResource(R.drawable.ic_like_white);
                                    check_text.setText("行きたい店に認定");
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        String message = response.getString("message");

                                        if (!message.equals("行きたいリストに登録しました")) {
                                            check_Image.setImageResource(R.drawable.ic_like_white);
                                            check_text.setText("行きたい店に認定");
                                            Toast.makeText(FlexibleTenpoActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                } else {
                    check_Image.setImageResource(R.drawable.ic_like_white);
                    check_text.setText("行きたい店に認定");
                    final AsyncHttpClient client = new AsyncHttpClient();
                    client.setCookieStore(SavedData.getCookieStore(FlexibleTenpoActivity.this));
                    client.post(FlexibleTenpoActivity.this, Const.URL_RESTRAUNT_UNFOLLOW, param, new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwableString, JSONObject response) {
                            check_Image.setImageResource(R.drawable.ic_favorite_orange);
                            check_text.setText("行きたい店を取消");
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String message = response.getString("message");

                                if (!message.equals("行きたいリストから解除しました")) {
                                    check_Image.setImageResource(R.drawable.ic_favorite_orange);
                                    check_text.setText("行きたい店を取消");
                                    Toast.makeText(FlexibleTenpoActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
        callRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new callClickHandler(), 750);
            }
        });
        gohereRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new gohereClickHandler(), 750);
            }
        });
        etcRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHomepage.equals("none")) {
                    new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleTenpoActivity.this)
                            .title("その他メニュー")
                            .items(R.array.list_tenpo_menu)
                            .itemsCallback(new com.afollestad.materialdialogs.MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(com.afollestad.materialdialogs.MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    if (charSequence.toString().equals("ホームページを見る")) {
                                        Uri uri = Uri.parse(mHomepage);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(FlexibleTenpoActivity.this, "その他メニューはありません", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        tenpo_name.setText(mPost_restname);
        tenpo_category.setText(mCategory);

        /*
        TextView tenpo_name = (TextView) findViewById(R.id.tenpo_name);
        TextView tenpo_category = (TextView) findViewById(R.id.tenpo_category);
        TextView tenpo_locality = (TextView) findViewById(R.id.tenpo_locality);
        RippleView tenpo_homepage = (RippleView) findViewById(R.id.tenpo_homepage);
        RippleView tenpo_phone = (RippleView) findViewById(R.id.tenpo_call);
        TextView tenpo_phonenumber = (TextView) findViewById(R.id.tenpo_phonenumber);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        tenpo_name.setText(mPost_restname);
        tenpo_category.setText(mCategory);
        tenpo_locality.setText(mPost_locality);
        tenpo_phonenumber.setText(mPhoneNumber);

        if (!mHomepage.equals("none")) {
            tenpo_homepage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ホームページ押されたときの処理
                    Uri uri = Uri.parse(mHomepage);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        } else {
            tenpo_homepage.setVisibility(View.GONE);
        }

        tenpo_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //電話するときの処理
                Handler handler = new Handler();
                handler.postDelayed(new callClickHandler(), 750);
            }
        });
        */

        setUpMapIfNeeded();

        getSignupAsync(FlexibleTenpoActivity.this);

        mTenpoAdapter = new TenpoAdapter(this, 0, mTenpousers);

        mTenpoSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mTenpoSwipe.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
                    mTenpoSwipe.setRefreshing(true);
                    getRefreshAsync(FlexibleTenpoActivity.this);
                } else {

                }
            }
        });
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = mMapView.getMap();
            if (mMap != null) {
                MapsInitializer.initialize(this);

                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng lng = new LatLng(mLat, mLon);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.addMarker(new MarkerOptions().position(lng).title(mPost_restname));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lng)
                .zoom(15)
                .tilt(50)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //ヘッダー通り過ぎた
        isSee = scrollY > 550;
    }

    @Override
    public final void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public final void onPause() {
        mMapView.onPause();
        super.onPause();
        BusHolder.get().unregister(self);

        ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder != null) {
            stopMovie(viewHolder);
        }
    }

    @Override
    public final void onResume() {
        super.onResume();
        BusHolder.get().register(self);
        mMapView.onResume();

        startMovie();
    }

    @Subscribe
    public void subscribe(NotificationNumberEvent event) {
        SnackbarManager.show(
                Snackbar.with(this)
                        .type(SnackbarType.MULTI_LINE)
                        .position(Snackbar.SnackbarPosition.BOTTOM)
                        .margin(16, 16, 16, 20)
                        .backgroundDrawable(R.color.material_drawer_background)
                        .text(event.mMessage)
        );
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {

            // スクロールしていない
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                //mBusy = false;
                Log.d("DEBUG", "SCROLL_STATE_IDLE");
                if (isSee) {
                changeMovie();
                }

                break;
            // スクロール中
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                //mBusy = true;
                Log.d("DEBUG", "SCROLL_STATE_TOUCH_SCROLL");

                break;
            // はじいたとき
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                //mBusy = true;
                Log.d("DEBUG", "SCROLL_STATE_FLING");

                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (totalItemCount != 1) {
            //投稿はある
            isExist = true;
        } else {
            //投稿がない
            isExist = false;
        }

    }

    private void getSignupAsync(final Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.get(context, mTenpoUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTenpousers.add(UserData.createTenpoUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mTenpoListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoListView.setAdapter(mTenpoAdapter);

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(FlexibleTenpoActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss getTimeline finish");
                tenpoprogress.setVisibility(View.GONE);
            }
        });
    }

    private void postSignupAsync(final Context context, final String post_id, final int position) {
        httpClient2 = new AsyncHttpClient();
        httpClient2.setCookieStore(SavedData.getCookieStore(context));
        RequestParams goodParam = new RequestParams("post_id", post_id);
        httpClient2.post(context, Const.URL_GOOD_API, goodParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                final UserData user = mTenpousers.get(position);
                user.setPushed_at(0);
                user.setgoodnum(user.getgoodnum() - 1);
                Toast.makeText(FlexibleTenpoActivity.this, "いいね送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        httpClient3 = new AsyncHttpClient();
        httpClient3.setCookieStore(SavedData.getCookieStore(context));
        httpClient3.get(context, mTenpoUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                mTenpousers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mTenpousers.add(UserData.createTenpoUserData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPlayingPostId = null;
                mViewHolderHash.clear();
                mTenpoListView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
                mTenpoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(FlexibleTenpoActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mTenpoSwipe.setRefreshing(false);
            }
        });
    }

    private void violateSignupAsync(final Context context, final String post_id) {
        final AsyncHttpClient httpClient5 = new AsyncHttpClient();
        httpClient5.setCookieStore(SavedData.getCookieStore(context));
        RequestParams violateParam = new RequestParams("post_id", post_id);
        httpClient5.post(context, Const.URL_VIOLATE_API, violateParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, "違反報告が完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //mMaterialDialog.dismiss();
                Toast.makeText(context, "違反報告に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void movieCacheCreated(boolean success, String postId) {
        if (success && mPlayingPostId.equals(postId) && getApplicationContext() != null) {
            Log.d("DEBUG", "MOVIE::movieCacheCreated 動画再生処理開始 postId:" + mPlayingPostId);
            startMovie();
        }
    }

    private void changeMovie() {
        Log.d("DEBUG", "changeMovie called");
        // TODO:実装
        final int position = mTenpoListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        if (mTenpoAdapter.isEmpty()) {
            return;
        }
        final UserData userData = mTenpoAdapter.getItem(position - 1);
        if (!userData.getPost_id().equals(mPlayingPostId)) {
            Log.d("DEBUG", "postId change");

            // 前回の動画再生停止処理
            final ViewHolder oldViewHolder = getPlayingViewHolder();
            if (oldViewHolder != null) {
                Log.d("DEBUG", "MOVIE::changeMovie 再生停止 postId:" + mPlayingPostId);
                Log.e("DEBUG", "changeMovie 動画再生停止");
                stopMovie(oldViewHolder);

                oldViewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            }

            mPlayingPostId = userData.getPost_id();
            final ViewHolder currentViewHolder = getPlayingViewHolder();

            if (!currentViewHolder.movie.isShown()) {
                Log.e("DEBUG", "バグだよ");
            }

            final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
            if (path != null) {
                // 動画再生開始
                Log.d("DEBUG", "MOVIE::changeMovie 動画再生処理開始 postId:" + mPlayingPostId);
                startMovie();
            } else {
                // 動画DL開始
                Log.d("DEBUG", "MOVIE::changeMovie  [ProgressBar VISIBLE] 動画DL処理開始 postId:" + mPlayingPostId);
                currentViewHolder.movieProgress.setVisibility(View.VISIBLE);
                currentViewHolder.videoFrame.setClickable(false);
                mCacheManager.requestMovieCacheCreate(FlexibleTenpoActivity.this, userData.getMovie(), userData.getPost_id(), FlexibleTenpoActivity.this, currentViewHolder.movieProgress);

            }
        }
    }

    private void startMovie() {
        if (mPlayBlockFlag) {
            Log.d("DEBUG", "startMovie play block status");
            return;
        }
        final ViewHolder viewHolder = getPlayingViewHolder();
        if (viewHolder == null) {
            Log.d("DEBUG", "startMovie viewHolder is null");
            return;
        }
        final int position = mTenpoListView.pointToPosition(mDisplaySize.x / 2, mDisplaySize.y / 2);
        final UserData userData = mTenpoAdapter.getItem(position - 1);

        final String postId = userData.getPost_id();

        // 安定感が増すおまじないを試してみる
        refreshVideoView(viewHolder);

        final String path = mCacheManager.getCachePath(userData.getPost_id(), userData.getMovie());
        Log.e("DEBUG", "[ProgressBar GONE] cache Path: " + path);
        if (path == null) {
            Log.d("DEBUG", "startMovie path is null");
            return;
        }
        viewHolder.movieProgress.setVisibility(View.INVISIBLE);
        viewHolder.movie.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                Log.e("DEBUG", "VideoView::Error what:" + what + " extra:" + extra);
                return true;
            }
        });
        viewHolder.movie.setVideoPath(path);
        viewHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("DEBUG", "MOVIE::onPrepared postId: " + postId);
                if (mPlayingPostId.equals(postId) && !mPlayBlockFlag) {
                    Log.d("DEBUG", "MOVIE::onPrepared 再生開始");
                    //viewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    //viewHolder.movie.start();
                    viewHolder.videoFrame.setClickable(true);
                    Log.e("DEBUG", "onPrepared 動画再生開始: " + userData.getMovie());

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.e("DEBUG", "onPrepared onCompletion 動画再生開始");
                            mp.seekTo(0);
                            mp.start();
                        }
                    });
                    mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                            Log.e("DEBUG", "動画再生OnError: what:" + what + " extra:" + extra);
                            if (mPlayingPostId.equals(postId) && !mPlayBlockFlag && isSee) {
                                Log.d("DEBUG", "MOVIE::onErrorListener 再生開始");
                                mPlayingPostId = null;
                                changeMovie();
                            }
                            return true;
                        }
                    });
                } else {
                    Log.e("DEBUG", "onPrepared 動画再生停止");
                    viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
                    stopMovie(viewHolder);

                }
            }
        });
    }

    public void stopMovie(ViewHolder viewHolder) {
        if (viewHolder == null) {
            viewHolder = getPlayingViewHolder();
        }
        viewHolder.movie.pause();
        viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
    }

    private void setViolateDialog(final Context context, final String post_id) {
        new MaterialDialog.Builder(context)
                .title("投稿の違反報告")
                .content("本当にこの投稿を違反報告しますか？")
                .positiveText("する")
                .negativeText("いいえ")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        violateSignupAsync(context, post_id);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }


    /**
     * 現在再生中のViewHolderを取得
     *
     * @return
     */
    private ViewHolder getPlayingViewHolder() {
        ViewHolder viewHolder = null;
        Log.d("DEBUG", "getPlayingViewHolder :" + mPlayingPostId);
        if (mPlayingPostId != null) {
            for (Map.Entry<ViewHolder, String> entry : mViewHolderHash.entrySet()) {
                if (entry.getValue().equals(mPlayingPostId)) {
                    viewHolder = entry.getKey();
                    break;
                }
            }
        }
        return viewHolder;
    }

    private void refreshVideoView(ViewHolder viewHolder) {
        viewHolder.movie.setOnPreparedListener(null);
        viewHolder.movie.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(final MediaPlayer mp, final int what, final int extra) {
                return true;
            }
        });

        viewHolder.movie.stopPlayback();
        ViewGroup viewgroup = (ViewGroup) viewHolder.movie.getParent();
        if (mVideoAttr == null) {
            mVideoAttr = viewHolder.movie.getAttributes();
        }
        viewHolder.movie.suspend();
        try {
            viewgroup.removeView(viewHolder.movie);
        } catch (RuntimeException runtimeexception) {
            try {
                viewgroup.removeView(viewHolder.movie);
            } catch (Exception exception) {
                Log.e("ERROR", "Weird things are happening.");
            }
        }
        viewHolder.movie = new SquareVideoView(FlexibleTenpoActivity.this, mVideoAttr);
        viewHolder.movie.setId(R.id.videoView);
        viewgroup.addView(viewHolder.movie, 0);

    }

    public static class ViewHolder {
        public ImageView circleImage;
        public TextView user_name;
        public TextView datetime;
        public TextView comment;
        public RippleView menuRipple;
        public SquareVideoView movie;
        public RoundCornerProgressBar movieProgress;
        public ImageView mVideoThumbnail;
        //public ImageView restaurantImage;
        //public TextView locality;
        public TextView rest_name;
        public TextView category;
        public TextView value;
        public TextView atmosphere;
        public RippleView tenpoRipple;
        public TextView likes;
        public ImageView likes_Image;
        public TextView comments;
        public RippleView likes_ripple;
        public RippleView comments_ripple;
        public FrameLayout videoFrame;
    }

    public class TenpoAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater mLayoutInflater;

        public TenpoAdapter(Context context, int viewResourceId, ArrayList<UserData> tenpousers) {
            super(context, viewResourceId, tenpousers);
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final UserData user = this.getItem(position);
            Log.e("ポジション", String.valueOf(position));

            // ViewHolder 取得・作成処理
            ViewHolder viewHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mLayoutInflater.inflate(R.layout.cell_timeline2, null);

                viewHolder = new ViewHolder();
                viewHolder.circleImage = (ImageView) convertView.findViewById(R.id.circleImage);
                viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.datetime = (TextView) convertView.findViewById(R.id.time_text);
                viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
                viewHolder.menuRipple = (RippleView) convertView.findViewById(R.id.menuRipple);
                viewHolder.movie = (SquareVideoView) convertView.findViewById(R.id.videoView);
                viewHolder.movieProgress = (RoundCornerProgressBar) convertView.findViewById(R.id.video_progress);
                viewHolder.mVideoThumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                //viewHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
                viewHolder.rest_name = (TextView) convertView.findViewById(R.id.rest_name);
                //viewHolder.locality = (TextView) convertView.findViewById(R.id.locality);
                viewHolder.category = (TextView) convertView.findViewById(R.id.category);
                viewHolder.value = (TextView) convertView.findViewById(R.id.value);
                viewHolder.atmosphere = (TextView) convertView.findViewById(R.id.mood);
                viewHolder.tenpoRipple = (RippleView) convertView.findViewById(R.id.tenpoRipple);
                viewHolder.likes = (TextView) convertView.findViewById(R.id.likes_Number);
                viewHolder.likes_Image = (ImageView) convertView.findViewById(R.id.likes_Image);
                viewHolder.comments = (TextView) convertView.findViewById(R.id.comments_Number);
                viewHolder.likes_ripple = (RippleView) convertView.findViewById(R.id.likes_ripple);
                viewHolder.comments_ripple = (RippleView) convertView.findViewById(R.id.comments_ripple);
                viewHolder.videoFrame = (FrameLayout) convertView.findViewById(R.id.videoFrame);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.user_name.setText(user.getUser_name());
            if (viewHolder.circleImage == null) {
                Log.d("DEBUG", "viewHolder.circleImage is null");
            }

            viewHolder.datetime.setText(user.getDatetime());

            viewHolder.comment.setText(user.getComment());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.circleImage);

            viewHolder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedUsername = user.getUser_name();
                    clickedUserpicture = user.getPicture();
                    clickedUserbackground = user.getBackground();
                    Handler handler = new Handler();
                    handler.postDelayed(new nameClickHandler(), 750);

                }
            });

            viewHolder.circleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedUsername = user.getUser_name();
                    clickedUserpicture = user.getPicture();
                    clickedUserbackground = user.getBackground();
                    Handler handler = new Handler();
                    handler.postDelayed(new nameClickHandler(), 750);

                }
            });

            viewHolder.menuRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(FlexibleTenpoActivity.this, R.style.BottomSheet_StyleDialog).sheet(R.menu.popup_ubnormal).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.violation:
                                    setViolateDialog(FlexibleTenpoActivity.this, user.getPost_id());
                                    break;
                                case R.id.close:
                                    dialog.dismiss();
                            }
                        }
                    }).show();
                }
            });

            Picasso.with(getContext())
                    .load(user.getThumbnail())
                    .placeholder(R.color.videobackground)
                    .into(viewHolder.mVideoThumbnail);
            viewHolder.mVideoThumbnail.setVisibility(View.VISIBLE);
            viewHolder.movieProgress.setVisibility(View.INVISIBLE);
            viewHolder.movieProgress.setAlpha(0.5f);
            viewHolder.movieProgress.setProgress(0);
            if (viewHolder.movie.isPlaying()) {
                Log.e("DEBUG", "getView 動画再生停止");
                stopMovie(viewHolder);
            }

            final ViewHolder videoClickViewHolder = viewHolder;
            viewHolder.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoClickViewHolder.movie.isPlaying()) {
                        videoClickViewHolder.movie.pause();
                    } else {
                        videoClickViewHolder.movie.start();
                        videoClickViewHolder.mVideoThumbnail.setVisibility(View.INVISIBLE);
                    }
                }
            });

            viewHolder.rest_name.setText(user.getRest_name());
            //viewHolder.locality.setText(user.getLocality());

            if (!user.getTagCategory().equals("none")) {
                viewHolder.category.setText(user.getTagCategory());
            } else {
                viewHolder.category.setText("タグなし");
            }
            if (!user.getAtmosphere().equals("none")) {
                viewHolder.atmosphere.setText(user.getAtmosphere());
            } else {
                viewHolder.atmosphere.setText("タグなし");
            }
            if (!user.getValue().equals("0")) {
                viewHolder.value.setText(user.getValue());
            } else {
                viewHolder.value.setText("タグなし");
            }

            final int currentgoodnum = user.getgoodnum();
            final int currentcommentnum = user.getComment_num();

            viewHolder.likes.setText(String.valueOf(currentgoodnum));
            viewHolder.comments.setText(String.valueOf(currentcommentnum));

            if (user.getPushed_at() == 0) {
                viewHolder.likes_ripple.setClickable(true);
                viewHolder.likes_Image.setImageResource(R.drawable.ic_like_white);

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.likes_ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("いいねをクリック", user.getPost_id());

                        final UserData user = mTenpousers.get(position);
                        user.setPushed_at(1);
                        user.setgoodnum(currentgoodnum + 1);
                        finalViewHolder.likes.setText(String.valueOf((currentgoodnum + 1)));
                        finalViewHolder.likes_Image.setImageResource(R.drawable.ic_like_red);
                        finalViewHolder.likes_ripple.setClickable(false);

                        postSignupAsync(FlexibleTenpoActivity.this, user.getPost_id(), position);
                    }
                });
            } else {
                viewHolder.likes_Image.setImageResource(R.drawable.ic_like_red);
                viewHolder.likes_ripple.setClickable(false);
            }

            viewHolder.comments_ripple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                    //投稿に対するコメントが見れるダイアログを表示
                    View commentView = new CommentView(FlexibleTenpoActivity.this, user.getPost_id());

                    new MaterialDialog.Builder(FlexibleTenpoActivity.this)
                            .customView(commentView, false)
                            .show();
                }
            });

            mViewHolderHash.put(viewHolder, user.getPost_id());
            Log.e("入れたポスト", user.getPost_id());
            return convertView;
        }
    }

    class nameClickHandler implements Runnable {
        public void run() {
            Intent userintent = new Intent(FlexibleTenpoActivity.this, FlexibleUserProfActivity.class);
            userintent.putExtra("username", clickedUsername);
            userintent.putExtra("picture", clickedUserpicture);
            userintent.putExtra("background", clickedUserbackground);
            startActivity(userintent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class timelineClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleTenpoActivity.this, GocciTimelineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class lifelogClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleTenpoActivity.this, GocciLifelogActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class searchClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleTenpoActivity.this, GocciSearchTenpoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class myprofClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(FlexibleTenpoActivity.this, GocciMyprofActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class checkClickHandler implements Runnable {
        public void run() {

        }
    }

    class callClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + mPhoneNumber));
            startActivity(intent);
        }
    }

    class gohereClickHandler implements Runnable {
        public void run() {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mLat + "," + mLon + "&mode=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    class etcClickHandler implements Runnable {
        public void run() {

        }
    }

    class adviceClickHandler implements Runnable {
        public void run() {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleTenpoActivity.this)
                    .title("アドバイスを送る")
                    .content("以下から当てはまるもの１つを選択してください。")
                    .items(R.array.single_choice_array)
                    .itemsCallbackSingleChoice(-1, new com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(com.afollestad.materialdialogs.MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            return true;
                        }
                    })
                    .positiveText("次へ進む")
                    .positiveColorRes(R.color.gocci_header)
                    .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);

                            if (dialog.getSelectedIndex() != -1) {
                                switch (dialog.getSelectedIndex()) {
                                    case 0:
                                        dialog.cancel();
                                        setNextDialog("ご要望");
                                        break;
                                    case 1:
                                        dialog.cancel();
                                        setNextDialog("苦情");
                                        break;
                                    case 2:
                                        dialog.cancel();
                                        setNextDialog("ご意見");
                                        break;
                                }
                            } else {
                                dialog.show();
                                Toast.makeText(FlexibleTenpoActivity.this, "一つを選択してください", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .show();
        }

        private void setNextDialog(final String string) {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleTenpoActivity.this)
                    .title("アドバイスを送る")
                    .content("コメントを入力してください")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(string, null, new com.afollestad.materialdialogs.MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(com.afollestad.materialdialogs.MaterialDialog materialDialog, CharSequence charSequence) {
                        }
                    })
                    .positiveText("送信する")
                    .positiveColorRes(R.color.gocci_header)
                    .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String message = dialog.getInputEditText().getText().toString();

                            if (!message.isEmpty()) {
                                postSignupAsync(FlexibleTenpoActivity.this, string, message);
                            } else {
                                Toast.makeText(FlexibleTenpoActivity.this, "文字を入力してください", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).show();
        }

        private void postSignupAsync(final Context context, final String category, final String message) {
            RequestParams sendParams = new RequestParams();
            sendParams.put("select_support", category);
            sendParams.put("content", message);
            final AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.setCookieStore(SavedData.getCookieStore(context));
            httpClient.post(context, Const.URL_ADVICE_API, sendParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "送信に失敗しました", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class policyClickHandler implements Runnable {
        public void run() {
            Uri uri = Uri.parse("http://inase-inc.jp/rules/");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class logoutClickHandler implements Runnable {
        public void run() {
            new com.afollestad.materialdialogs.MaterialDialog.Builder(FlexibleTenpoActivity.this)
                    .title("確認")
                    .content("本当にログアウトしますか？")
                    .positiveText("はい")
                    .negativeText("いいえ")
                    .callback(new com.afollestad.materialdialogs.MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(com.afollestad.materialdialogs.MaterialDialog dialog) {
                            super.onPositive(dialog);

                            SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                            String judge = pref.getString("judge", "no judge");

                            switch (judge) {
                                case "facebook":
                                    LoginManager.getInstance().logOut();
                                    break;
                                case "twitter":
                                    Twitter.logOut();
                                    break;
                                case "auth":
                                    break;
                                default:
                                    break;
                            }

                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.apply();

                            EventDateRecorder recorder = EventDateRecorder.load(FlexibleTenpoActivity.this, "use_first_gocci_android");
                            recorder.clear();

                            Intent intent = new Intent(FlexibleTenpoActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    }).show();
        }
    }
}

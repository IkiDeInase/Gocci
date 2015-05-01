package com.inase.android.gocci.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inase.android.gocci.Activity.FlexibleTenpoActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.BaseFragment;
import com.inase.android.gocci.Event.ArrayListGetEvent;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.SearchKeywordPostEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class Search_mapFragment extends BaseFragment
        implements OnMapReadyCallback, ObservableScrollViewCallbacks {

    private static final String KEY_IMAGE_URL = "image_url";

    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_TELL = "tell";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";
    private static final String TAG_HOMEPAGE = "homepage";

    private static final String FUNCTION_FIRST = "first";
    private static final String FUNCTION_REFRESH = "refresh";

    private SupportMapFragment fm;

    private ObservableListView mSearch_mapListView;
    private ArrayList<UserData> mSearch_mapusers = new ArrayList<>();
    private ArrayList<String> tenpo_string_users = new ArrayList<>();
    private Search_mapAdapter mSearch_mapAdapter;
    private SwipeRefreshLayout mSearchmapSwipe;
    private FloatingActionButton fab;

    private ProgressWheel mapprogress;

    private final Search_mapFragment search_mapself = this;

    private String mSearch_keywordUrl;
    private String mSearchword;
    private String mEncode_searchword;
    private String mSearch_mapUrl;

    private String clickedRestname;
    private String clickedLocality;
    private double clickedLat;
    private double clickedLon;
    private String clickedPhoneNumber;
    private String clickedHomepage;
    private String clickedCategory;

    private double mLatitude;
    private double mLongitude;

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private AsyncHttpClient httpClient3;

    private GoogleMap mMap;

    private LocationManager mLocationManager;

    private boolean isCapturingLocation = false;

    private Location firstLocation = null;

    public Search_mapFragment newIntent(String name, String imageUrl) {
        Search_mapFragment fragment = new Search_mapFragment();
        Bundle args = new Bundle();
        args.putString(TAG_USER_NAME, name);
        if (imageUrl != null) {
            args.putString(KEY_IMAGE_URL, imageUrl);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        View view1 = getActivity().getLayoutInflater().inflate(R.layout.fragment_search_map,
                container, false);

        if (Application_Gocci.getFirstLocation() != null) {
            firstLocation = Application_Gocci.getFirstLocation();
            Log.e("DEBUG", "アプリから位置とったよ");
        }

        mapprogress = (ProgressWheel) view1.findViewById(R.id.mapprogress_wheel);

        mSearch_mapListView = (ObservableListView) view1.findViewById(R.id.list);
        mSearch_mapListView.setDivider(null);
        // スクロールバーを表示しない
        mSearch_mapListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mSearch_mapListView.setSelector(android.R.color.transparent);
        mSearch_mapListView.setScrollViewCallbacks(this);

        mSearchmapSwipe = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_searchmap);
        mSearchmapSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mSearchmapSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//Handle the refresh then call
                if (isCapturingLocation) {
                    Log.d("DEBUG", "ProgressDialog show [mRefreshDialog]");
                    mSearchmapSwipe.setRefreshing(true);
                    refreshLocation();
                }

            }
        });

        fab = (FloatingActionButton) view1.findViewById(R.id.siboriButton);
        fab.attachToListView(mSearch_mapListView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        if (location != null) {
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                        } else {
                            Log.e("からでしたー", "locationupdated");
                        }
                    }
                });
                new MaterialDialog.Builder(getActivity())
                        .title("表示件数を変更する")
                        .items(R.array.single_choice_limit2)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                return true;
                            }
                        })
                        .positiveText("OK")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                int number = 30;
                                switch (dialog.getSelectedIndex()) {
                                    case 0:
                                        number = 30;
                                        break;
                                    case 1:
                                        number = 50;
                                        break;
                                    case 2:
                                        number = 100;
                                        break;
                                }
                                mMap.clear();
                                mapprogress.setVisibility(View.VISIBLE);
                                setUpMap(FUNCTION_FIRST, mLatitude, mLongitude, number);
                            }
                        })
                        .show();
            }
        });

        //googleserviceが使えるか判断して処理を分ける
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (status != ConnectionResult.SUCCESS) {
            // Google Play Services が使えない場合
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();
        } else {
            // Google Play Services が使える場合
            //activity_main.xmlのSupportMapFragmentへの参照を取得
            fm = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(this);
        }
        //システムサービスのLOCATION_SERVICEからLocationManager objectを取得
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return view1;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        //Bundle args = getArguments();
        //mName = args.getString(TAG_USER_NAME);
        //mPictureImageUrl = args.getString(KEY_IMAGE_URL);
    }

    private void setUpMap(String function, Double latitude, Double longitude, int number) {
        //最終的な経度緯度情報から近くのお店や検索した店舗を表示させる。
        LatLng latLng = new LatLng(latitude, longitude);
        mSearch_mapUrl = "http://api-gocci.jp/dist/?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&limit=" + number;
        Log.e("経度緯度", mSearch_mapUrl);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        switch (function) {
            case FUNCTION_FIRST:
                getSearchMapJson(getActivity(), mSearch_mapUrl);
                break;
            case FUNCTION_REFRESH:
                getRefreshMapJson(getActivity(), mSearch_mapUrl);
        }

    }

    private boolean setSearchWord(String searchWord, double lat, double lon) {
        if (searchWord != null && !searchWord.equals("")) {
            // searchWordがあることを確認
            mSearchword = searchWord;
            try {
                mEncode_searchword = URLEncoder.encode(mSearchword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mSearch_keywordUrl = "http://api-gocci.jp/search/?lat=" + lat + "&limit=30&lon=" + lon + "&restname=" + mEncode_searchword;

            mMap.clear();

            getKeywordSearchJson(getActivity());

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "文字を入力して下さい。", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(search_mapself);

        //GPSかネットワーク経由から位置情報を取ってくるかの条件分岐
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialDialog.Builder(getActivity())
                    .title("位置情報取得について")
                    .content("位置情報を使いたいのですが、GPSが無効になっています。" + "設定を変更してください")
                    .positiveText("はい")
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText("いいえ")
                    .negativeColorRes(R.color.material_drawer_primary_light)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingIntent);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            Toast.makeText(getActivity(), "近くの店舗表示ができなくなります", Toast.LENGTH_LONG).show();
                        }
                    }).show();

        } else {
            if (!isCapturingLocation) {
                startLocation();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(search_mapself);
        if (isCapturingLocation) {
            stopLocation();
        }
    }

    private void startLocation() {
        isCapturingLocation = true;
        SmartLocation.with(getActivity()).location().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    Application_Gocci.setFirstLocation(location);
                } else {
                    Log.e("からでしたー", "locationupdated");
                }
            }
        });
    }

    private void firstLocation() {
        SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    setUpMap(FUNCTION_FIRST, mLatitude, mLongitude, 30);
                    Application_Gocci.setFirstLocation(location);
                } else {
                    Log.e("からでしたー", "locationupdated");
                }
            }
        });
    }

    private void refreshLocation() {
        SmartLocation.with(getActivity()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    Application_Gocci.setFirstLocation(location);
                    setUpMap(FUNCTION_REFRESH, mLatitude, mLongitude, 30);
                } else {
                    Log.e("からでしたー", "locationupdated");
                }
            }
        });
    }

    private void stopLocation() {
        isCapturingLocation = false;
        SmartLocation.with(getActivity()).location().stop();
    }

    @Subscribe
    public void subscribe(SearchKeywordPostEvent event) {
        setSearchWord(event.searchWord, event.mLat, event.mLon);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //fragmentからGoogleMap objectを取得
        mMap = fm.getMap();

        //Google MapのMyLocationレイヤーを使用可能にする
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mSearch_mapListView.setSelection(tenpo_string_users.indexOf(marker.getTitle()));
                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                setUpMap(FUNCTION_REFRESH, mLatitude, mLongitude, 30);
            }
        });

        Log.d("DEBUG", "ProgressDialog show [mSearchmapDialog]");
        if (firstLocation == null) {
            firstLocation();
        } else {
            setUpMap(FUNCTION_FIRST, firstLocation.getLatitude(), firstLocation.getLongitude(), 30);
        }
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    public static class Search_mapHolder {
        public RippleView searchRipple;
        public TextView restname;
        public TextView distance;
        public TextView category;
        public TextView locality;
    }

    private void getSearchMapJson(Context context, String url) {
        //mSearchmapDialog = new CustomProgressDialog(getActivity());
        //mSearchmapDialog.setCancelable(false);
        //mSearchmapDialog.show();

        httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mSearch_mapusers.clear();
                tenpo_string_users.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String rest_name = jsonObject.getString(TAG_RESTNAME);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        final Double lat = jsonObject.getDouble(TAG_LAT);
                        final Double lon = jsonObject.getDouble(TAG_LON);
                        final String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);
                        String homepage = jsonObject.getString(TAG_HOMEPAGE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(rest_name);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);
                        user.setHomepage(homepage);

                        mSearch_mapusers.add(user);
                        tenpo_string_users.add(rest_name);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .snippet(locality)
                                        .title(rest_name));

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSearch_mapAdapter = new Search_mapAdapter(getActivity(), 0, mSearch_mapusers);
                mSearch_mapListView.setAdapter(mSearch_mapAdapter);
                BusHolder.get().post(new ArrayListGetEvent(mSearch_mapusers));

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss [mSearchmapDialog]");
                //mSearchmapDialog.dismiss();
                mapprogress.setVisibility(View.GONE);
            }
        });
    }

    private void getRefreshMapJson(Context context, String url) {
        httpClient3 = new AsyncHttpClient();
        httpClient3.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mSearch_mapusers.clear();
                tenpo_string_users.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String rest_name = jsonObject.getString(TAG_RESTNAME);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        final Double lat = jsonObject.getDouble(TAG_LAT);
                        final Double lon = jsonObject.getDouble(TAG_LON);
                        final String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);
                        String homepage = jsonObject.getString(TAG_HOMEPAGE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(rest_name);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);
                        user.setHomepage(homepage);

                        mSearch_mapusers.add(user);
                        tenpo_string_users.add(rest_name);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .snippet(locality)
                                        .title(rest_name));

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSearch_mapAdapter.notifyDataSetChanged();
                BusHolder.get().post(new ArrayListGetEvent(mSearch_mapusers));

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss [mRefreshDialog]");
                mSearchmapSwipe.setRefreshing(false);
            }
        });
    }

    private void getKeywordSearchJson(Context context) {
        httpClient2 = new AsyncHttpClient();
        httpClient2.get(context, mSearch_keywordUrl, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mapprogress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mSearch_mapusers.clear();
                tenpo_string_users.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String rest_name = jsonObject.getString(TAG_RESTNAME);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        final Double lat = jsonObject.getDouble(TAG_LAT);
                        final Double lon = jsonObject.getDouble(TAG_LON);
                        final String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);
                        String homepage = jsonObject.getString(TAG_HOMEPAGE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(rest_name);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);
                        user.setHomepage(homepage);

                        mSearch_mapusers.add(user);
                        tenpo_string_users.add(rest_name);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .snippet(locality)
                                        .title(rest_name));
                                //snippetで詳細も表示できる
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSearch_mapAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Log.d("DEBUG", "ProgressDialog dismiss [mRefreshDialog]");
                mapprogress.setVisibility(View.GONE);
            }

        });
    }

    //現在の位置情報から近くの３０件を表示させるAdapter
    public class Search_mapAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public Search_mapAdapter(Context context, int viewResourceId, ArrayList<UserData> search_mapusers) {
            super(context, viewResourceId, search_mapusers);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Search_mapHolder mapHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.cell_search, null);
                mapHolder = new Search_mapHolder();
                mapHolder.searchRipple = (RippleView) convertView.findViewById(R.id.searchRipple);
                mapHolder.restname = (TextView) convertView.findViewById(R.id.restname);
                mapHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                mapHolder.category = (TextView) convertView.findViewById(R.id.category);
                mapHolder.locality = (TextView) convertView.findViewById(R.id.locality);
                convertView.setTag(mapHolder);
            } else {
                mapHolder = (Search_mapHolder) convertView.getTag();
            }
            final UserData user = this.getItem(position);

            mapHolder.restname.setText(user.getRest_name());
            mapHolder.distance.setText(user.getDistance());
            mapHolder.category.setText(user.getCategory());
            mapHolder.locality.setText(user.getLocality());

            mapHolder.searchRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedRestname = user.getRest_name();
                    clickedLocality = user.getLocality();
                    clickedLat = user.getLat();
                    clickedLon = user.getLon();
                    clickedCategory = user.getCategory();
                    clickedHomepage = user.getHomepage();
                    clickedPhoneNumber = user.getTell();

                    //リップルエフェクトを見せるためにすこし遅らせてIntentを飛ばす
                    Handler handler = new Handler();
                    handler.postDelayed(new searchClickHandler(), 750);
                }
            });

            //getViewごとに店舗があるところが中心になるように地図を動かしている。
            LatLng markerlatLng = new LatLng(user.getLat(), user.getLon());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(markerlatLng));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerlatLng)
                    .zoom(18)
                    .tilt(50)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            return convertView;
        }
    }

    class searchClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getActivity(), FlexibleTenpoActivity.class);
            intent.putExtra("restname", clickedRestname);
            intent.putExtra("locality", clickedLocality);
            intent.putExtra("lat", clickedLat);
            intent.putExtra("lon", clickedLon);
            intent.putExtra("phone", clickedPhoneNumber);
            intent.putExtra("homepage", clickedHomepage);
            intent.putExtra("category", clickedCategory);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}

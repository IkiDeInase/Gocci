package com.example.kinagafuji.gocci.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Adapter.Search_keywordAdapter;
import com.example.kinagafuji.gocci.Adapter.Search_mapAdapter;
import com.example.kinagafuji.gocci.AsyncTask.KeywordSearchAsyncTask;
import com.example.kinagafuji.gocci.AsyncTask.Search_mapAsyncTask;
import com.example.kinagafuji.gocci.Base.AddMarkerEvent;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.BusHolder;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Search_mapFragment extends BaseFragment implements GooglePlayServicesClient.OnConnectionFailedListener, GooglePlayServicesClient.ConnectionCallbacks {

    public CustomProgressDialog mSearchmapDialog;
    public ListView mSearch_mapListView;
    public ArrayList<UserData> mSearch_mapusers = new ArrayList<UserData>();
    public Search_mapAdapter mSearch_mapAdapter;

    private SwipeRefreshLayout mSearchmapSwipe;

    public String mSearch_keywordUrl;
    public CustomProgressDialog mKeywordDialog;
    public ArrayList<UserData> mKeywordusers = new ArrayList<UserData>();
    public Search_keywordAdapter mSearch_keywordAdapter;

    public double mLatitude;
    public double mLongitude;

    public GoogleMap mMap;

    public SearchView mSearchView;

    public String mSearchword;
    private String mEncode_searchword;

    public String mName;
    public String mPictureImageUrl;

    public LocationManager mLocationManager;
    private LocationClient mLocationClient = null;
    private Location currentLocation;

    private static final String KEY_IMAGE_URL = "image_url";

    private static final String TAG_USER_NAME = "user_name";

    private static final String TAG = "Search_mapFragment";
    private final Search_mapFragment self = this;

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

        mSearch_mapAdapter = new Search_mapAdapter(getActivity(), 0, mSearch_mapusers);

        mSearch_mapListView = (ListView) view1.findViewById(R.id.mylistView1);
        mSearch_mapListView.setDivider(null);
        // スクロールバーを表示しない
        mSearch_mapListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mSearch_mapListView.setSelector(android.R.color.transparent);

        mSearch_mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                UserData country = mSearch_mapusers.get(pos);

                Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                intent.putExtra("restname", country.getRest_name());
                intent.putExtra("locality", country.getLocality());
                intent.putExtra("name", mName);
                intent.putExtra("pictureImageUrl", mPictureImageUrl);
                startActivity(intent);
            }
        });

        mSearch_mapListView.setAdapter(mSearch_mapAdapter);

        mSearchView = (SearchView) view1.findViewById(R.id.searchbar);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(true);

        mSearchView.setOnQueryTextListener(onQueryTextListener);

        mSearchmapSwipe = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_searchmap);
        mSearchmapSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mSearchmapSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//Handle the refresh then call
                setUpMap();
                mSearchmapSwipe.setRefreshing(false);
            }
        });

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (status != ConnectionResult.SUCCESS) {
            // Google Play Services が使えない場合
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        } else {
            // Google Play Services が使える場合
            //activity_main.xmlのSupportMapFragmentへの参照を取得
            SupportMapFragment fm = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);

            //fragmentからGoogleMap objectを取得
            mMap = fm.getMap();

            //Google MapのMyLocationレイヤーを使用可能にする
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //システムサービスのLOCATION_SERVICEからLocationManager objectを取得
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            mLocationClient = new LocationClient(getActivity(), this, this); // ConnectionCallbacks, OnConnectionFailedListener
            if (mLocationClient != null) {
                // Google Play Servicesに接続
                mLocationClient.connect();
                Log.e("TAG", "グーグルサービスにコネクトされました");
            }
        }

        return view1;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        mPictureImageUrl = args.getString(KEY_IMAGE_URL);

    }


    private void setUpMap() {

        Log.e("TAG", "結局のセットアップ時経度" + mLatitude + "/" + "緯度" + mLongitude);

        LatLng latLng = new LatLng(mLatitude, mLongitude);

        SharedPreferences latlon = getActivity().getSharedPreferences("latlon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = latlon.edit();
        editor.putString("latitude", String.valueOf(mLatitude));
        editor.putString("longitude", String.valueOf(mLongitude));
        editor.apply();

        String mSearch_mapUrl = "http://api-gocci.jp/dist/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        new Search_mapAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_mapUrl);
        mSearchmapDialog = new CustomProgressDialog(getActivity());
        mSearchmapDialog.setCancelable(false);
        mSearchmapDialog.show();
    }


    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchWord) {
            // SubmitボタンorEnterKeyを押されたら呼び出されるメソッド
            return setSearchWord(searchWord);
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // 入力される度に呼び出される
            return false;
        }
    };

    private boolean setSearchWord(String searchWord) {
        if (searchWord != null && !searchWord.equals("")) {
            // searchWordがあることを確認
            mSearchword = searchWord;

            try {
                mEncode_searchword = URLEncoder.encode(mSearchword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mSearch_keywordUrl = "http://api-gocci.jp/search/?restname=" + mEncode_searchword;
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "文字を入力して下さい。", Toast.LENGTH_SHORT).show();
        }
        // 虫眼鏡アイコンを隠す
        mSearchView.setIconified(false);
        // SearchViewを隠す
        mSearchView.onActionViewCollapsed();
        // Focusを外す
        mSearchView.clearFocus();

        mMap.clear();

        new KeywordSearchAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_keywordUrl);
        mKeywordDialog = new CustomProgressDialog(getActivity());
        mKeywordDialog.setCancelable(false);
        mKeywordDialog.show();

        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {

        currentLocation = mLocationClient.getLastLocation();

        mLatitude = currentLocation.getLatitude();
        mLongitude = currentLocation.getLongitude();

        setUpMap();

    }

    @Override
    public void onDisconnected() {
        Log.e("TAG", "グーグルサービスにディスコネクトされました");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "グーグルサービスにコネクト失敗しました");
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(self);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Location Manager");
            builder.setMessage("位置情報を使いたいのですが、GPSが無効になっています。/n" + "設定を変更しますか？");
            builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settingIntent);
                }
            });
            builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }

        updateDisplay();
        int minTime = 5000;
        float minDistance = 0;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);

        Log.e("TAG", "経度緯度をResumeで読み込みました");
    }

    @Subscribe
    public void subscribe(final AddMarkerEvent event) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(event.lat, event.lon))
                        .title(event.restname));

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(self);

        if (mLocationClient != null) {
            // Google Play Servicesに接続
            mLocationClient.disconnect();
            Log.e("TAG", "ロケーションクライアントがディスコネクトされました");
        }
        mLocationManager.removeUpdates(listener);
        Log.e("TAG", "ロケーションマネージャーがディスコネクトされました");
    }

    private void updateDisplay() {
        if (currentLocation == null) {
            Log.e("TAG", "位置が測定できないので東京スカイツリーに移動します。");
            mLatitude = 35.710057714926265;
            mLongitude = 139.81071829999996;
            //非同期を開始
            //setUpMap();

        } else {
            Log.e("アップデートディスプレイ", currentLocation.getLatitude() + "/" + currentLocation.getLongitude());
            //非同期を開始
        }
    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            mLatitude = currentLocation.getLatitude();
            mLongitude = currentLocation.getLongitude();

            Log.e("TAG", "位置変更時経度" + mLatitude + "/" + "緯度" + mLongitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}

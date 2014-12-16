package com.example.kinagafuji.gocci.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class Search_mapFragment extends BaseFragment
        implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks, OnMapReadyCallback {

    private static final String KEY_IMAGE_URL = "image_url";

    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_TELL = "tell";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";

    private static final int STATUS_ONE = 1;
    private static final int STATUS_TWO = 2;

    private SupportMapFragment fm;

    private ListView mSearch_mapListView;
    private ArrayList<UserData> mSearch_mapusers = new ArrayList<UserData>();
    private Search_mapAdapter mSearch_mapAdapter;
    private CustomProgressDialog mSearchmapDialog;
    private SwipeRefreshLayout mSearchmapSwipe;

    private Search_keywordAdapter mSearch_keywordAdapter;
    private CustomProgressDialog mKeywordDialog;

    private SearchView mSearchView;

    private String mSearch_keywordUrl;
    private String mSearchword;
    private String mName;
    private String mPictureImageUrl;
    private String mEncode_searchword;
    private String mSearch_mapUrl;

    private double mLatitude;
    private double mLongitude;

    private MaterialDialog mMaterialDialog;
    private MaterialDialog mSearchDialog;

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location currentLocation = null;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            mLatitude = currentLocation.getLatitude();
            mLongitude = currentLocation.getLongitude();
            Log.e("TAG", "位置変更時経度" + mLatitude + "/" + "緯度" + mLongitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchWord) {
            // SubmitボタンorEnterKeyを押されたら呼び出されるメソッド
            return setSearchWord(searchWord);
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

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

        httpClient = new AsyncHttpClient();
        httpClient2 = new AsyncHttpClient();

        mSearch_mapListView = (ListView) view1.findViewById(R.id.mylistView1);
        mSearch_mapListView.setDivider(null);
        // スクロールバーを表示しない
        mSearch_mapListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mSearch_mapListView.setSelector(android.R.color.transparent);

        mSearch_mapAdapter = new Search_mapAdapter(getActivity(), 0, mSearch_mapusers);

        mSearch_mapListView.addHeaderView(makeHeaderView());

        mSearch_mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                UserData country = mSearch_mapusers.get(pos);

                Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                intent.putExtra("restname", country.getRest_name());
                intent.putExtra("locality", country.getLocality());
                intent.putExtra("name", mName);
                startActivity(intent);
            }
        });

        mSearchmapSwipe = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_searchmap);
        mSearchmapSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mSearchmapSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//Handle the refresh then call
                updateDisplay(STATUS_TWO);
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
            fm = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(this);
            //システムサービスのLOCATION_SERVICEからLocationManager objectを取得
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        return view1;
    }

    public View makeHeaderView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View countView = inflater.inflate(R.layout.search_header, null);
        ImageButton searchButton = (ImageButton) countView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView = new SearchView(getActivity());
                mSearchView.setOnQueryTextListener(onQueryTextListener);
                mSearchView.setQueryHint("検索");
                mSearchDialog = new MaterialDialog(getActivity()).setContentView(mSearchView)
                        .setCanceledOnTouchOutside(true);
                mSearchDialog.show();
            }
        });

        return countView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        mPictureImageUrl = args.getString(KEY_IMAGE_URL);
    }

    private void setUpMap(int status) {
        Log.e("セットアップ", "経度" + mLatitude + "/" + "緯度" + mLongitude);
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mSearch_mapUrl = "http://api-gocci.jp/dist/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        getSearchMapJson(getActivity(), status);

    }

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

            mSearchDialog.dismiss();
            mMap.clear();

            getKeywordSearchJson(getActivity());

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "文字を入力して下さい。", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onDisconnected() {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onResume() {
        super.onResume();
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mMaterialDialog = new MaterialDialog(getActivity())
                    .setTitle("位置情報取得について")
                    .setMessage("位置情報を使いたいのですが、GPSが無効になっています。" + "設定を変更しますか？")
                    .setPositiveButton("はい", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                            Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingIntent);
                        }
                    })
                    .setNegativeButton("いいえ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                            Toast.makeText(getActivity(), "ネット回線で位置情報取得を行います。", Toast.LENGTH_SHORT).show();
                            int minTime = 5000;
                            float minDistance = 0;
                            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener);
                            //updateDisplay();
                        }
                    });
            mMaterialDialog.show();
        } else {
            int minTime = 5000;
            float minDistance = 0;
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(listener);
        Log.e("TAG", "ロケーションマネージャーがディスコネクトされました");
    }

    private void updateDisplay(int status) {
        if (currentLocation == null) {
            Log.e("位置情報が空だよ", "スカイツリーの位置情報を入れておく");
            mLatitude = 35.710057714926265;
            mLongitude = 139.81071829999996;

            LatLng firstLocation = new LatLng(mLatitude, mLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
            Log.e("パート６", "位置なかったよ");
        } else {
            Log.e("パート６", "位置あったから読み込むよ");
            if (mLatitude == 35.710057714926265 && mLongitude == 139.81071829999996) {
                Toast.makeText(getActivity(), "まだ位置情報が無いので、一旦東京スカイツリーに移動します。", Toast.LENGTH_SHORT).show();
                Log.e("パート７", "位置あったけど仕方なくスカイツリー");
                setUpMap(status);
            }
            mLatitude = currentLocation.getLatitude();
            mLongitude = currentLocation.getLongitude();

            setUpMap(status);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("パート3", "地図の用意できたのでいろいろカスタマイズ");
        mMap = googleMap;

        //fragmentからGoogleMap objectを取得
        mMap = fm.getMap();

        //Google MapのMyLocationレイヤーを使用可能にする
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (currentLocation == null) {
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        updateDisplay(STATUS_ONE);
    }

    public static class Search_mapHolder {
        public ImageView search1;
        public ImageView search2;
        public ImageView search3;
        public TextView restname;
        public TextView category;
        public TextView locality;
        public TextView distance;

        public Search_mapHolder(View view) {
            this.search1 = (ImageView) view.findViewById(R.id.search1);
            this.search2 = (ImageView) view.findViewById(R.id.search2);
            this.search3 = (ImageView) view.findViewById(R.id.search3);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.category = (TextView) view.findViewById(R.id.category);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.distance = (TextView) view.findViewById(R.id.distance);
        }
    }

    private void getSearchMapJson(Context context, final int status) {
        httpClient.get(context, mSearch_mapUrl, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mSearchmapDialog = new CustomProgressDialog(getActivity());
                mSearchmapDialog.setCancelable(false);
                mSearchmapDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String rest_name = jsonObject.getString(TAG_RESTNAME);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        final Double lat = jsonObject.getDouble(TAG_LAT);
                        final Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(rest_name);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        mSearch_mapusers.add(user);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .title(rest_name));
                                //snippetで詳細も表示できる
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status == STATUS_ONE) {
                    mSearch_mapListView.setAdapter(mSearch_mapAdapter);

                } else if (status == STATUS_TWO){
                    mSearch_mapAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "何が怒っているのだ....", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mSearchmapDialog.dismiss();
            }
        });
    }

    private void getKeywordSearchJson(Context context) {
        httpClient2.get(context, mSearch_keywordUrl, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mKeywordDialog = new CustomProgressDialog(getActivity());
                mKeywordDialog.setCancelable(false);
                mKeywordDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String rest_name = jsonObject.getString(TAG_RESTNAME);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        final Double lat = jsonObject.getDouble(TAG_LAT);
                        final Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(rest_name);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        mSearch_mapusers.add(user);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .title(rest_name));
                                //snippetで詳細も表示できる
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSearch_keywordAdapter = new Search_keywordAdapter(getActivity(), 0, mSearch_mapusers);
                mSearch_mapListView.setAdapter(mSearch_keywordAdapter);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mKeywordDialog.dismiss();
            }

        });
    }

    public class Search_mapAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private Search_mapHolder search_mapHolder;

        public Search_mapAdapter(Context context, int viewResourceId, ArrayList<UserData> search_mapusers) {
            super(context, viewResourceId, search_mapusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                search_mapHolder = new Search_mapHolder(convertView);
                convertView.setTag(search_mapHolder);
            } else {
                search_mapHolder = (Search_mapHolder) convertView.getTag();
            }
            final UserData user = this.getItem(position);
            search_mapHolder.restname.setText(user.getRest_name());
            search_mapHolder.category.setText(user.getCategory());
            search_mapHolder.distance.setText(user.getDistance());

            LatLng markerlatLng = new LatLng(user.getLat(), user.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerlatLng));

            return convertView;
        }
    }

    public class Search_keywordAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private Search_mapHolder search_mapHolder;

        public Search_keywordAdapter(Context context, int viewResourceId, ArrayList<UserData> search_keywordusers) {
            super(context, viewResourceId, search_keywordusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                search_mapHolder = new Search_mapHolder(convertView);
                convertView.setTag(search_mapHolder);
            } else {
                search_mapHolder = (Search_mapHolder) convertView.getTag();
            }
            final UserData user = this.getItem(position);

            search_mapHolder.restname.setText(user.getRest_name());
            search_mapHolder.category.setText(user.getCategory());
            search_mapHolder.distance.setText(user.getDistance());

            LatLng markerlatLng = new LatLng(user.getLat(), user.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerlatLng));

            return convertView;
        }
    }
}

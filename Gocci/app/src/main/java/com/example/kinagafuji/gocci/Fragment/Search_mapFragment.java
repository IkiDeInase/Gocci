package com.example.kinagafuji.gocci.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Base.ArrayListGetEvent;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.BusHolder;
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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class Search_mapFragment extends BaseFragment
        implements GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks, OnMapReadyCallback {

    private static final String KEY_IMAGE_URL = "image_url";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG = "Search_mapFragment";
    private final Search_mapFragment self = this;
    public CustomProgressDialog mSearchmapDialog;
    public ListView mSearch_mapListView;
    public ArrayList<UserData> mSearch_mapusers = new ArrayList<UserData>();
    public Search_mapAdapter mSearch_mapAdapter;
    public String mSearch_keywordUrl;
    public CustomProgressDialog mKeywordDialog;
    public Search_keywordAdapter mSearch_keywordAdapter;
    public double mLatitude;
    public double mLongitude;
    public GoogleMap mMap;
    public SearchView mSearchView;
    public String mSearchword;
    public String mName;
    public String mPictureImageUrl;
    public LocationManager mLocationManager;
    private SwipeRefreshLayout mSearchmapSwipe;
    private String mEncode_searchword;
    private Location currentLocation = null;
    private MaterialDialog mMaterialDialog;
    private MaterialDialog mSearchDialog;
    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            mLatitude = currentLocation.getLatitude();
            mLongitude = currentLocation.getLongitude();

            Log.e("パート１０","経度緯度変更されたお");
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
    private SupportMapFragment fm;
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

        mSearch_mapAdapter = new Search_mapAdapter(getActivity(), 0, mSearch_mapusers);

        mSearch_mapListView = (ListView) view1.findViewById(R.id.mylistView1);
        mSearch_mapListView.setDivider(null);
        // スクロールバーを表示しない
        mSearch_mapListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mSearch_mapListView.setSelector(android.R.color.transparent);


        mSearch_mapListView.addHeaderView(makeHeaderView());

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

        mSearchmapSwipe = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_searchmap);
        mSearchmapSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mSearchmapSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//Handle the refresh then call
                updateDisplay();
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

            Log.e("パート１","googleserviceにつなぎました");


        }

        return view1;
    }

    public View makeHeaderView() {
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View countView = inflater.inflate(R.layout.search_header, null);
        ImageButton searchButton = (ImageButton)countView.findViewById(R.id.searchButton);
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

    private void setUpMap() {

        Log.e("パート８", "結局のセットアップ時経度" + mLatitude + "/" + "緯度" + mLongitude);

        LatLng latLng = new LatLng(mLatitude, mLongitude);

        String mSearch_mapUrl = "http://api-gocci.jp/dist/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        new Search_mapAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_mapUrl);
        mSearchmapDialog = new CustomProgressDialog(getActivity());
        mSearchmapDialog.setCancelable(false);
        mSearchmapDialog.show();
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

            new KeywordSearchAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_keywordUrl);
            mKeywordDialog = new CustomProgressDialog(getActivity());
            mKeywordDialog.setCancelable(false);
            mKeywordDialog.show();

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "文字を入力して下さい。", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.e("TAG","グーグルサービスにコネクトされました");
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
        Log.e("パート2","resumeを読み込むよ");

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

            //updateDisplay();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(listener);
        Log.e("TAG", "ロケーションマネージャーがディスコネクトされました");
    }

    private void updateDisplay() {
        if (currentLocation == null) {
            Log.e("位置情報が空だよ", "スカイツリーの位置情報を入れておく");
            mLatitude = 35.710057714926265;
            mLongitude = 139.81071829999996;

            LatLng firstLocation = new LatLng(mLatitude,mLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation,15));
            Log.e("パート６","位置なかったよ");
            //非同期を開始
            //setUpMap();

        } else {
            Log.e("パート６","位置あったから読み込むよ");

            if (mLatitude == 35.710057714926265 && mLongitude == 139.81071829999996) {
                Toast.makeText(getActivity(), "まだ位置情報が無いので、一旦東京スカイツリーに移動します。", Toast.LENGTH_SHORT).show();
                Log.e("パート７","位置あったけど仕方なくスカイツリー");
                setUpMap();
            }

            mLatitude = currentLocation.getLatitude();
            mLongitude = currentLocation.getLongitude();

            setUpMap();

            //非同期を開始
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("パート3","地図の用意できたのでいろいろカスタマイズ");
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

        updateDisplay();

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

    public class Search_mapAsyncTask extends AsyncTask<String, String, Integer> {

        private static final String TAG_TELL = "tell";
        private static final String TAG_RESTNAME = "restname";
        private static final String TAG_CATEGORY = "category";
        private static final String TAG_LAT = "lat";
        private static final String TAG_LON = "lon";
        private static final String TAG_LOCALITY = "locality";
        private static final String TAG_DISTANCE = "distance";


        @Override
        protected Integer doInBackground(String... params) {
            String param = params[0];

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(param);
            HttpResponse httpResponse = null;

            try {
                httpResponse = httpClient.execute(request);
            } catch (Exception e) {
                Log.d("JSONSampleActivity", "Error Execute");
            }

            int status = httpResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {
                String mSearch_mapData = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    mSearch_mapData = outputStream.toString(); // JSONデータ
                    Log.d("data", mSearch_mapData);
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }
                mSearch_mapusers.clear();

                try {

                    JSONArray searchmapArray = new JSONArray(mSearch_mapData);

                    for (int i = 0; i < searchmapArray.length(); i++) {
                        JSONObject jsonObject = searchmapArray.getJSONObject(i);

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
                        //BusHolder.get().post(new AddMarkerEvent(lat,lon,rest_name));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("えらー", String.valueOf(e));
                }

            } else {
                Log.d("JSONSampleActivity", "Status" + status);
            }

            return status;

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result != null && result == HttpStatus.SC_OK) {
                //ListViewの最読み込み
                mSearch_mapAdapter.notifyDataSetChanged();
                mSearch_mapListView.invalidateViews();
                BusHolder.get().post(new ArrayListGetEvent(mSearch_mapusers));

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            mSearchmapDialog.dismiss();

        }
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

    public class KeywordSearchAsyncTask extends AsyncTask<String, String, Integer> {

        private static final String TAG_TELL = "tell";
        private static final String TAG_RESTNAME = "restname";
        private static final String TAG_CATEGORY = "category";
        private static final String TAG_LAT = "lat";
        private static final String TAG_LON = "lon";
        private static final String TAG_LOCALITY = "locality";
        private static final String TAG_DISTANCE = "distance";


        @Override
        protected Integer doInBackground(String... params) {
            String param = params[0];

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(param);
            HttpResponse httpResponse = null;

            try {
                httpResponse = httpClient.execute(request);
            } catch (Exception e) {
                Log.d("JSONSampleActivity", "Error Execute");
            }

            int status = httpResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {
                String mKeywordData = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    mKeywordData = outputStream.toString(); // JSONデータ
                    Log.d("data", mKeywordData);

                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                mSearch_mapusers.clear();
                try {

                    JSONArray jsonArray = new JSONArray(mKeywordData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String restname = jsonObject.getString(TAG_RESTNAME);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        final Double lat = jsonObject.getDouble(TAG_LAT);
                        final Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(restname);
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
                                        .title(restname));

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("えらー", String.valueOf(e));
                }

            } else {
                Log.d("JSONSampleActivity", "Status" + status);

            }

            return status;
        }

        // このメソッドは非同期処理の終わった後に呼び出されます
        @Override
        protected void onPostExecute(Integer result) {

            if (result != null && result == HttpStatus.SC_OK) {
                //ListViewの最読み込み
                mSearch_keywordAdapter = new Search_keywordAdapter(getActivity(), 0, mSearch_mapusers);
                mSearch_mapListView.setAdapter(mSearch_keywordAdapter);

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            mKeywordDialog.dismiss();
        }
    }

}

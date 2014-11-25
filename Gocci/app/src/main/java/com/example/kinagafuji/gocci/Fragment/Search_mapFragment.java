package com.example.kinagafuji.gocci.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class Search_mapFragment extends BaseFragment implements LocationListener,GpsStatus.Listener{

    public String mSearch_mapUrl;
    private String mSearch_mapData;
    private CustomProgressDialog mSearchmapDialog;
    private ListView mSearch_mapListView;
    private ArrayList<UserData> mSearch_mapusers = new ArrayList<UserData>();
    private Search_mapAdapter mSearch_mapAdapter;
    public SwipeRefreshLayout mSearchmapSwipe;

    private String mKeywordData;
    public String mSearch_keywordUrl;
    private CustomProgressDialog mKeywordDialog;
    public ArrayList<UserData> mKeywordusers = new ArrayList<UserData>();
    public Search_keywordAdapter mSearch_keywordAdapter;

    public double mLatitude;
    public double mLongitude;

    private GoogleMap mMap = null;
    public Marker mMarker = null;

    public SearchView mSearchView;

    public String mSearchword;
    private String mEncode_searchword;

    public String mName;
    public String mPictureImageUrl;

    private LocationManager mLocationManager;

    private static final String KEY_IMAGE_URL = "image_url";

    private static final String TAG_POST_ID = "post_id";
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_GOODNUM = "goodnum";
    private static final String TAG_COMMENT_NUM = "comment_num";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_STAR_EVALUATION = "star_evaluation";
    private static final String TAG_TELL = "tell";
    private static final String TAG_RESTNAME1 = "restname";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";

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
        // Add the newly created View to the ViewPager

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

        mSearchView = (SearchView)view1.findViewById(R.id.searchbar);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(true);

        mSearchView.setOnQueryTextListener(onQueryTextListener);

        mSearchmapSwipe = (SwipeRefreshLayout) view1.findViewById(R.id.swipe_searchmap);
        mSearchmapSwipe.setColorSchemeColors(R.color.main_color_light,R.color.gocci,R.color.main_color_dark,R.color.window_bg);
        mSearchmapSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//Handle the refresh then call
                setUpMap();
                mSearchmapSwipe.setRefreshing(false);
            }
        });




        return view1;
    }

    @Override
    public void onStart() {
        super.onStart();

        // This verification should be done during onStart() because the system calls
        // this method when the user returns to the activity, which ensures the desired
        // location provider is enabled each time the activity resumes from the stopped state.

        final boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            enableLocationSettings();
            Log.d("GPS設定","設定して下しあ");
        }
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLocationManager!=null) {
            mLocationManager.removeUpdates(Search_mapFragment.this);
            mLocationManager.removeGpsStatusListener(Search_mapFragment.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mLocationManager!=null) {
            mLocationManager.removeUpdates(Search_mapFragment.this);
            mLocationManager.removeGpsStatusListener(Search_mapFragment.this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        mPictureImageUrl = args.getString(KEY_IMAGE_URL);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addGpsStatusListener(this);

        if (mLocationManager != null) {

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setSpeedRequired(false);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            String provider = mLocationManager.getBestProvider(criteria, true);
            mLocationManager.requestLocationUpdates(provider, 0, 0, Search_mapFragment.this);

            SupportMapFragment fm = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);

            mMap = fm.getMap();

            mMap.setMyLocationEnabled(true);

            LocationProvider mProvider =
                    mLocationManager.getProvider(LocationManager.GPS_PROVIDER);

            Log.d("経度・緯度", mLatitude + "/" + mLongitude);
            mSearch_mapUrl = "http://api-gocci.jp/api/public/dist/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";

            setUpMap();
        }


    }

    private void setUpMap() {
        UserData userData = new UserData();

        /*if (mLatitude == 0.0) {
            mLatitude = userData.getmLatitude();
            mLongitude = userData.getmLongitude();
            Log.d("経度・緯度", mLatitude + "/" + mLongitude);
            mSearch_mapUrl = "http://api-gocci.jp/api/public/dist/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";
        }*/


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // MyLocationButtonを有効に
        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);

        new SearchMapAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_mapUrl);
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
            mSearch_keywordUrl = "http://api-gocci.jp/api/public/search/?restname=" + mEncode_searchword;
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

        new KeywordSearchAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_keywordUrl);
        mKeywordDialog = new CustomProgressDialog(getActivity());
        mKeywordDialog.setCancelable(false);
        mKeywordDialog.show();

        return false;
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        UserData userData = new UserData();
        userData.setmLatitude(mLatitude);
        userData.setmLongitude(mLongitude);

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


    public class SearchMapAsyncTask extends AsyncTask<String, String, Integer> {

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
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    mSearch_mapData = outputStream.toString(); // JSONデータ
                    Log.d("data", mSearch_mapData);
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray searchmapArray = new JSONArray(mSearch_mapData);

                    for (int i = 0; i < searchmapArray.length(); i++) {
                        JSONObject jsonObject = searchmapArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String rest_name = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        final LatLng mapLng = new LatLng(lat,lon);

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
                        handler.post(new Runnable(){

                            @Override
                            public void run() {
                                mMarker = mMap.addMarker(new MarkerOptions().position(mapLng).title(rest_name));

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

        @Override
        protected void onPostExecute(Integer result) {

            if (result != null && result == HttpStatus.SC_OK) {
                //ListViewの最読み込み
                mSearch_mapAdapter.notifyDataSetChanged();
                mSearch_mapListView.invalidateViews();

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            mSearchmapDialog.dismiss();

        }
    }

    public class KeywordSearchAsync extends AsyncTask<String, String, Integer> {

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
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    mKeywordData = outputStream.toString(); // JSONデータ
                    Log.d("data", mKeywordData);

                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(mKeywordData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        final String restname = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        final LatLng keywordLng = new LatLng(lat,lon);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRest_name(restname);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        mKeywordusers.add(user);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable(){

                            @Override
                            public void run() {
                                mMarker = mMap.addMarker(new MarkerOptions().position(keywordLng).title(restname));

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
                mSearch_keywordAdapter = new Search_keywordAdapter(getActivity(), 0, mKeywordusers);
                mSearch_mapListView.setAdapter(mSearch_keywordAdapter);


            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            mKeywordDialog.dismiss();
        }
    }

    public static class SearchMapHolder {
        ImageView search1;
        ImageView search2;
        ImageView search3;
        TextView restname;
        TextView category;
        TextView locality;
        TextView distance;

        public SearchMapHolder(View view) {
            this.search1 = (ImageView) view.findViewById(R.id.search1);
            this.search2 = (ImageView) view.findViewById(R.id.search2);
            this.search3 = (ImageView) view.findViewById(R.id.search3);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.category = (TextView) view.findViewById(R.id.category);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.distance = (TextView) view.findViewById(R.id.distance);
        }
    }

    public class Search_mapAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private SearchMapHolder searchMapHolder;

        public Search_mapAdapter(Context context, int viewResourceId, ArrayList<UserData> search_mapusers) {
            super(context, viewResourceId, search_mapusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                searchMapHolder = new SearchMapHolder(convertView);
                convertView.setTag(searchMapHolder);
            } else {
                searchMapHolder = (SearchMapHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            searchMapHolder.restname.setText(user.getRest_name());
            searchMapHolder.category.setText(user.getCategory());
            searchMapHolder.distance.setText(user.getDistance());

            LatLng markerlatLng = new LatLng(user.getLat(), user.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerlatLng));

            return convertView;
        }
    }

    public class Search_keywordAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private SearchMapHolder searchMapHolder;

        public Search_keywordAdapter(Context context, int viewResourceId, ArrayList<UserData> search_keywordusers) {
            super(context, viewResourceId, search_keywordusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                searchMapHolder = new SearchMapHolder(convertView);
                convertView.setTag(searchMapHolder);
            } else {
                searchMapHolder = (SearchMapHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            searchMapHolder.restname.setText(user.getRest_name());
            searchMapHolder.category.setText(user.getCategory());
            searchMapHolder.distance.setText(user.getDistance());

            LatLng markerlatLng = new LatLng(user.getLat(), user.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerlatLng));

            return convertView;
        }
    }
}

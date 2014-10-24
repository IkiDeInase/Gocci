package com.example.kinagafuji.gocci.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.data.UserData;
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

public class Search_mapFragment extends BaseFragment {

    private GoogleMap mMap = null;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public double Latitude1;
    public double Longitude1;

    public String MapURL;
    public String SearchURL;

    private String encodesearchword;

    private String data;

    public UserAdapter userAdapter;
    public UserAdapter2 userAdapter2;

    private static final String TAG_TELL = "tell";
    private static final String TAG_RESTNAME1 = "restname";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";

    private CustomProgressDialog dialog;

    private ArrayList<UserData> users = new ArrayList<UserData>();
    private ArrayList<UserData> users2 = new ArrayList<UserData>();

    private SearchView searchView;
    private String searchword;

    private ListView mlistView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        View rootView = inflater.inflate(R.layout.fragment_search_map, container, false);

        setUpMapIfNeeded();

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.CYAN, Color.MAGENTA, Color.BLACK);

        userAdapter = new UserAdapter(getActivity(), 0, users);
        userAdapter2 = new UserAdapter2(getActivity(), 0, users2);

        mlistView = (ListView) rootView.findViewById(R.id.mylistView1);
        mlistView.setDivider(null);
        // スクロールバーを表示しない
        mlistView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mlistView.setSelector(android.R.color.transparent);
        mlistView.setAdapter(userAdapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                UserData country = users.get(pos);

                Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                intent.putExtra("restname", country.getRestname());
                intent.putExtra("locality", country.getLocality());
                startActivity(intent);
            }
        });

        searchView = (SearchView)rootView.findViewById(R.id.searchbar);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(onQueryTextListener);

        new MyMapAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,MapURL);
        dialog = new CustomProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.show();

        return rootView;
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
            searchword = searchWord;

            try {
                encodesearchword = URLEncoder.encode(searchword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            SearchURL = "https://codelecture.com/gocci/search.php?restname=" + encodesearchword;
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "文字を入力して下さい。", Toast.LENGTH_SHORT).show();
        }
        // 虫眼鏡アイコンを隠す
        this.searchView.setIconified(false);
        // SearchViewを隠す
        this.searchView.onActionViewCollapsed();
        // Focusを外す
        this.searchView.clearFocus();

        new MySearchAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SearchURL);
        dialog = new CustomProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.show();
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        Location myLocation = locationManager.getLastKnownLocation(provider);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (myLocation != null) {
            Latitude1 = myLocation.getLatitude();

            Longitude1 = myLocation.getLongitude();
        }

        Log.d("経度・緯度", Latitude1 + "/" + Longitude1);

        MapURL = "https://codelecture.com/gocci/?lat=" + String.valueOf(Latitude1) + "&lon=" + String.valueOf(Longitude1) + "&limit=30";

        LatLng latLng = new LatLng(Latitude1, Longitude1);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // MyLocationButtonを有効に
        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
    }

    public class MySearchAsync extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(SearchURL);
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
                    data = outputStream.toString(); // JSONデータ
                    Log.d("data", data);

                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(data);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        String restname = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRestname(restname);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        users.add(user);

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
                mlistView.invalidateViews();
                userAdapter2.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    public class MyMapAsync extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(MapURL);
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
                    data = outputStream.toString(); // JSONデータ
                    Log.d("data", data);

                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(data);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        String restname = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        UserData user = new UserData();

                        user.setTell(tell);
                        user.setRestname(restname);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        users.add(user);

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
                mlistView.invalidateViews();
                userAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // 3秒待機
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    };

    public static class ViewHolder {
        TextView tell;
        TextView restname;
        TextView category;
        TextView locality;
        TextView distance;

        public ViewHolder(View view) {
            this.tell = (TextView) view.findViewById(R.id.tell);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.category = (TextView) view.findViewById(R.id.category);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.distance = (TextView) view.findViewById(R.id.distance);
        }
    }

    public class UserAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public UserAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            LatLng latLng1 = new LatLng(user.getLat(), user.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng1);
            options.title(user.getRestname());
            options.draggable(false);
            mMap.addMarker(options);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });

            viewHolder.tell.setText(user.getTell());
            viewHolder.restname.setText(user.getRestname());
            viewHolder.category.setText(user.getCategory());
            viewHolder.locality.setText(user.getLocality());
            viewHolder.distance.setText(user.getDistance());

            return convertView;
        }
    }
    public class UserAdapter2 extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public UserAdapter2(Context context, int viewResourceId, ArrayList<UserData> users2) {
            super(context, viewResourceId, users2);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            LatLng latLng2 = new LatLng(user.getLat(), user.getLon());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng2));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng2);
            options.title(user.getRestname());
            options.draggable(false);
            mMap.addMarker(options);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });

            viewHolder.tell.setText(user.getTell());
            viewHolder.restname.setText(user.getRestname());
            viewHolder.category.setText(user.getCategory());
            viewHolder.locality.setText(user.getLocality());
            viewHolder.distance.setText(user.getDistance());

            return convertView;
        }
    }
}

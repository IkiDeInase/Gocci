

package com.example.kinagafuji.gocci.Base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.PopupHelper;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.ToukouPopup;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SlidingTabsBasicFragment extends BaseFragment {

    public SlidingTabLayout mSlidingTabLayout;

    public ViewPager mViewPager;

    public int width;
    public int height;

    public String timelineUrl = "https://codelecture.com/gocci/timeline.php";
    public String search_mapUrl;
    public String search_keywordUrl;
    public String profUrl = "https://codelecture.com/gocci/mypage.php";
    public String signupUrl = "https://codelecture.com/gocci/signup.php";

    private String timelineData;
    private String search_mapData;
    private String keywordData;
    private String search_tenpoData;
    private String profData;

    private CustomProgressDialog timelineDialog;
    private CustomProgressDialog searchmapDialog;
    private CustomProgressDialog searchtenpoDialog;
    private CustomProgressDialog keywordDialog;
    private CustomProgressDialog profDialog;

    public ListView timelineListView;
    private ListView timeline_search_mapListView;
    private ListView search_mapListView;
    private ListView profListView;

    private ArrayList<UserData> timelineusers = new ArrayList<UserData>();
    private ArrayList<UserData> search_mapusers = new ArrayList<UserData>();
    public ArrayList<UserData> keywordusers = new ArrayList<UserData>();
    public ArrayList<UserData> search_tenpousers = new ArrayList<UserData>();
    private ArrayList<UserData> profusers = new ArrayList<UserData>();

    private TimelineAdapter timelineAdapter;
    private Search_mapAdapter search_mapAdapter;
    private Search_tenpoAdapter search_tenpoAdapter;
    private ProfAdapter profAdapter;

    public double mLatitude;
    public double mLongitude;

    private static boolean isMov = false;

    public int mCardLayoutIndex = 0;

    private GoogleMap mMap = null;

    public TimelineHolder timelineHolder;
    public SearchMapHolder searchMapHolder;
    public SearchTenpoHolder searchtenpoHolder;
    public ProfHolder profHolder;

    public SearchView mSearchView;

    public String searchword;
    private String encode_searchword;

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

    public String name;
    public String location;
    public String pictureImageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_sample, container, false);

        Point size = new Point();
        width = size.x;
        height = size.y;

        new TimelineTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, timelineUrl);
        timelineDialog = new CustomProgressDialog(getActivity());
        timelineDialog.setCancelable(false);
        timelineDialog.show();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //mViewPager.setBackgroundColor(R.color.main_color);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setCustomTabView();
        mSlidingTabLayout.setDividerColors(android.R.color.transparent);
        mSlidingTabLayout.setSelectedIndicatorColors(R.color.main_color_light);
        //mSlidingTabLayout.setCustomTabColorizer();
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

            if (position == 1) {
                Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }

        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            switch (position) {
                case 0:

                    // Inflate a new layout from our resources
                    final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline,
                            container, false);

                    // Add the newly created View to the ViewPager
                    container.addView(view);

                    timelineListView = (ListView) view.findViewById(R.id.mylistView2);

                    ImageButton toukouButton = (ImageButton) view.findViewById(R.id.toukouButton);
                    toukouButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PopupWindow window = ToukouPopup.newBasicPopupWindow(getActivity());

                            View inflateView = getActivity().getLayoutInflater().inflate(R.layout.searchlist, container, false);

                            setUpTenpo();

                            timeline_search_mapListView = (ListView) inflateView.findViewById(R.id.searchListView);
                            timeline_search_mapListView.setDivider(null);
                            // スクロールバーを表示しない
                            timeline_search_mapListView.setVerticalScrollBarEnabled(false);
                            // カード部分をselectorにするので、リストのselectorは透明にする
                            timeline_search_mapListView.setSelector(android.R.color.transparent);

                            if (mCardLayoutIndex > 0) {
                                timeline_search_mapListView.addFooterView(LayoutInflater.from(getActivity()).inflate(
                                        R.layout.card_footer, timeline_search_mapListView, false));
                            }

                            search_tenpoAdapter = new Search_tenpoAdapter(getActivity(), 0, search_tenpousers);

                            timeline_search_mapListView.setAdapter(search_tenpoAdapter);

                            window.setContentView(inflateView);
                            //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);

                            ToukouPopup.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);
                        }
                    });

                    timelineAdapter = new TimelineAdapter(getActivity(), 0, timelineusers);
                    timelineListView.setDivider(null);
                    // スクロールバーを表示しない
                    timelineListView.setVerticalScrollBarEnabled(false);
                    // カード部分をselectorにするので、リストのselectorは透明にする
                    timelineListView.setSelector(android.R.color.transparent);

                    // 最後の余白分のビューを追加
                    if (mCardLayoutIndex > 0) {
                        timelineListView.addFooterView(LayoutInflater.from(getActivity()).inflate(
                                R.layout.card_footer, timelineListView, false));
                    }
                    timelineListView.setAdapter(timelineAdapter);

                    // Return the View
                    return view;


                case 1:
                    // Inflate a new layout from our resources
                    View view1 = getActivity().getLayoutInflater().inflate(R.layout.fragment_search_map,
                            container, false);
                    // Add the newly created View to the ViewPager
                    container.addView(view1);

                    setUpMapIfNeeded();

                    search_mapAdapter = new Search_mapAdapter(getActivity(), 0, search_mapusers);

                    search_mapListView = (ListView) view1.findViewById(R.id.mylistView1);
                    search_mapListView.setDivider(null);
                    // スクロールバーを表示しない
                    search_mapListView.setVerticalScrollBarEnabled(false);
                    // カード部分をselectorにするので、リストのselectorは透明にする
                    search_mapListView.setSelector(android.R.color.transparent);
                    search_mapListView.setAdapter(search_mapAdapter);

                    search_mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                            UserData country = search_mapusers.get(pos);

                            Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                            intent.putExtra("restname", country.getRest_name());
                            intent.putExtra("locality", country.getLocality());
                            startActivity(intent);
                        }
                    });

                    mSearchView = (SearchView)view1.findViewById(R.id.searchbar);
                    mSearchView.setIconifiedByDefault(true);
                    mSearchView.setSubmitButtonEnabled(true);

                    mSearchView.setOnQueryTextListener(onQueryTextListener);

                    // Return the View
                    return view1;
                case 2:
                    // Inflate a new layout from our resources
                    View view2 = getActivity().getLayoutInflater().inflate(R.layout.fragment_lifelog,
                            container, false);
                    // Add the newly created View to the ViewPager
                    container.addView(view2);

                    // Return the View
                    return view2;
                case 3:
                    // Inflate a new layout from our resources
                    View view3 = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile,
                            container, false);
                    // Add the newly created View to the ViewPager
                    container.addView(view3);

                    TextView post_name = (TextView) view3.findViewById(R.id.post_name);
                    ImageView post_Imageurl = (ImageView) view3.findViewById(R.id.post_Imageurl);
                    TextView post_location = (TextView) view3.findViewById(R.id.post_location);

                    SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

                    name = pref.getString("name", "");
                    pictureImageUrl = pref.getString("pictureUrl", "");
                    location = pref.getString("location", "");

                    post_name.setText(name);
                    post_location.setText(location);

                    Picasso.with(getActivity())
                            .load(pictureImageUrl)
                            .resize(120, 120)
                            .placeholder(R.drawable.ic_userpicture)
                            .centerCrop()
                            .transform(new RoundedTransformation())
                            .into(post_Imageurl);

                    profListView = (ListView) view3.findViewById(R.id.proflist);

                    new ProfTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    profDialog = new CustomProgressDialog(getActivity());
                    profDialog.setCancelable(false);
                    profDialog.show();

                    profAdapter = new ProfAdapter(getActivity(), 0, profusers);

                    profListView.setDivider(null);
                    // スクロールバーを表示しない
                    profListView.setVerticalScrollBarEnabled(false);
                    // カード部分をselectorにするので、リストのselectorは透明にする
                    profListView.setSelector(android.R.color.transparent);

                    // 最後の余白分のビューを追加
                    if (mCardLayoutIndex > 0) {
                        profListView.addFooterView(LayoutInflater.from(getActivity()).inflate(
                                R.layout.card_footer, profListView, false));
                    }
                    profListView.setAdapter(profAdapter);

                    // Return the View
                    return view3;

            }
            return null;
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

            mLatitude = myLocation.getLatitude();

            mLongitude = myLocation.getLongitude();

            Log.d("経度・緯度", mLatitude + "/" + mLongitude);

            search_mapUrl = "https://codelecture.com/gocci/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";

            new SearchMapAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search_mapUrl);
            searchmapDialog = new CustomProgressDialog(getActivity());
            searchmapDialog.setCancelable(false);
            searchmapDialog.show();


            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng latLng = new LatLng(mLatitude, mLongitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            // MyLocationButtonを有効に
            UiSettings settings = mMap.getUiSettings();
            settings.setMyLocationButtonEnabled(true);
        }

        private void setUpTenpo() {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);
            Location myLocation = locationManager.getLastKnownLocation(provider);

            mLatitude = myLocation.getLatitude();

            mLongitude = myLocation.getLongitude();

            Log.d("経度・緯度", mLatitude + "/" + mLongitude);

            search_mapUrl = "https://codelecture.com/gocci/?lat=" + String.valueOf(mLatitude) + "&lon=" + String.valueOf(mLongitude) + "&limit=30";

            new SearchTenpoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search_mapUrl);
            searchtenpoDialog = new CustomProgressDialog(getActivity());
            searchtenpoDialog.setCancelable(false);
            searchtenpoDialog.show();

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
                    encode_searchword = URLEncoder.encode(searchword, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                search_keywordUrl = "https://codelecture.com/gocci/search.php?restname=" + encode_searchword;
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "文字を入力して下さい。", Toast.LENGTH_SHORT).show();
            }
            // 虫眼鏡アイコンを隠す
            mSearchView.setIconified(false);
            // SearchViewを隠す
            mSearchView.onActionViewCollapsed();
            // Focusを外す
            mSearchView.clearFocus();

            new KeywordSearchAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search_keywordUrl);
            keywordDialog = new CustomProgressDialog(getActivity());
            keywordDialog.setCancelable(false);
            keywordDialog.show();

            return false;
        }



    }

    public class TimelineTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String string = strings[0];

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(string);
            HttpResponse httpResponse = null;

            try {
                httpResponse = httpClient.execute(request);
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }

            int status = httpResponse.getStatusLine().getStatusCode();


            if (HttpStatus.SC_OK == status) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    timelineData = outputStream.toString(); // JSONデータ
                    Log.d("data", timelineData);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                try {
                    JSONArray jsonArray = new JSONArray(timelineData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        int line = (i/4)*4;
                        int position = i - line;

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        /*switch(position){
                            case 0:
                                //０の操作
                                break;
                            case 1:
                                //１の操作
                                break;
                            case 2:
                                //２の操作
                                break;
                            case 3:
                                //３の操作
                                break;

                        }*/

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        String user_id = jsonObject.getString(TAG_USER_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String rest_name = jsonObject.getString(TAG_RESTNAME);
                        String goodnum = jsonObject.getString(TAG_GOODNUM);
                        String comment_num = jsonObject.getString(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        String star_evaluation = jsonObject.getString(TAG_STAR_EVALUATION);

                        UserData user = new UserData();

                        user.setPost_id(post_id);
                        user.setMovie(movie);
                        user.setPicture(picture);
                        user.setUser_id(user_id);
                        user.setUser_name(user_name);
                        user.setRest_name(rest_name);
                        user.setgoodnum(goodnum);
                        user.setComment_num(comment_num);
                        user.setThumbnail(thumbnail);
                        user.setStar_evaluation(star_evaluation);

                        timelineusers.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("error", String.valueOf(e));
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
                timelineListView.invalidateViews();
                timelineAdapter.notifyDataSetChanged();

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            timelineDialog.dismiss();
        }
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
                    search_mapData = outputStream.toString(); // JSONデータ
                    Log.d("data", search_mapData);
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(search_mapData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        String rest_name = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
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

                        search_mapusers.add(user);

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
                search_mapAdapter.notifyDataSetChanged();
                search_mapListView.invalidateViews();

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            searchmapDialog.dismiss();
            Log.d("ダイアログ","searchmap");

        }
    }

    public class SearchTenpoAsyncTask extends AsyncTask<String, String, Integer> {

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
                    search_tenpoData = outputStream.toString(); // JSONデータ
                    Log.d("data", search_tenpoData);
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(search_tenpoData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        String rest_name = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
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

                        search_tenpousers.add(user);

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
                search_tenpoAdapter.notifyDataSetChanged();
                timeline_search_mapListView.invalidateViews();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            searchtenpoDialog.dismiss();
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
                    keywordData = outputStream.toString(); // JSONデータ
                    Log.d("data", keywordData);

                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(keywordData);

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
                        user.setRest_name(restname);
                        user.setCategory(category);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        keywordusers.add(user);

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
                search_mapListView.invalidateViews();
                search_mapAdapter.notifyDataSetChanged();

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            keywordDialog.dismiss();
            Log.d("ダイアログ","keywordsearch");
        }
    }

    public class ProfTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            HttpClient client1 = new DefaultHttpClient();

            HttpPost method = new HttpPost(signupUrl);

            ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
            contents.add(new BasicNameValuePair("user_name", name));
            contents.add(new BasicNameValuePair("picture", pictureImageUrl));
            Log.d("読み取り", name + "と" + pictureImageUrl);

            String body = null;
            try {
                method.setEntity(new UrlEncodedFormEntity(contents, "utf-8"));
                HttpResponse res = client1.execute(method);
                Log.d("TAGだよ", "反応");
                HttpEntity entity = res.getEntity();
                body = EntityUtils.toString(entity, "UTF-8");
                Log.d("bodyの中身だよ", body);
            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpGet request = new HttpGet(profUrl);
            HttpResponse httpResponse = null;

            try {
                httpResponse = client1.execute(request);
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }

            int status = httpResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    profData = outputStream.toString(); // JSONデータ
                    Log.d("data", profData);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                try {
                    JSONArray jsonArray = new JSONArray(profData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        String user_id = jsonObject.getString(TAG_USER_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String restname = jsonObject.getString(TAG_RESTNAME);
                        String goodnum = jsonObject.getString(TAG_GOODNUM);
                        String comment_num = jsonObject.getString(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        String star_evaluation = jsonObject.getString(TAG_STAR_EVALUATION);

                        UserData user = new UserData();

                        user.setPost_id(post_id);
                        user.setMovie(movie);
                        user.setPicture(picture);
                        user.setUser_id(user_id);
                        user.setUser_name(user_name);
                        user.setRest_name(restname);
                        user.setgoodnum(goodnum);
                        user.setComment_num(comment_num);
                        user.setThumbnail(thumbnail);
                        user.setStar_evaluation(star_evaluation);

                        profusers.add(user);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("error", String.valueOf(e));
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
                profListView.invalidateViews();
                profAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            profDialog.dismiss();
        }
    }

    private static class TimelineHolder {
        ImageView circleImage;
        TextView time;
        TextView user_name;
        VideoView movie;
        TextView comment;
        ImageView restaurantImage;
        TextView rest_name;
        TextView category;

        public TimelineHolder(View view) {
            this.circleImage = (ImageView) view.findViewById(R.id.circleImage);
            this.time = (TextView) view.findViewById(R.id.time);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.movie = (VideoView) view.findViewById(R.id.videoView);
            this.comment = (TextView) view.findViewById(R.id.comment);
            this.restaurantImage = (ImageView) view.findViewById(R.id.restaurantImage);
            this.rest_name = (TextView) view.findViewById(R.id.rest_name);
            this.category = (TextView) view.findViewById(R.id.category);

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

    public static class SearchTenpoHolder {
        ImageView search1;
        ImageView search2;
        ImageView search3;
        TextView restname;
        TextView category;
        TextView locality;
        TextView distance;

        public SearchTenpoHolder(View view) {
            this.search1 = (ImageView) view.findViewById(R.id.search1);
            this.search2 = (ImageView) view.findViewById(R.id.search2);
            this.search3 = (ImageView) view.findViewById(R.id.search3);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.category = (TextView) view.findViewById(R.id.category);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.distance = (TextView) view.findViewById(R.id.distance);
        }
    }

    private static class ProfHolder {
        ImageView circleImage;
        TextView time;
        TextView user_name;
        VideoView movie;
        TextView comment;
        ImageView restaurantImage;
        TextView rest_name;
        TextView category;

        public ProfHolder(View view) {
            this.circleImage = (ImageView) view.findViewById(R.id.circleImage);
            this.time = (TextView) view.findViewById(R.id.time);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.movie = (VideoView) view.findViewById(R.id.videoView);
            this.comment = (TextView) view.findViewById(R.id.comment);
            this.restaurantImage = (ImageView) view.findViewById(R.id.restaurantImage);
            this.rest_name = (TextView) view.findViewById(R.id.rest_name);
            this.category = (TextView) view.findViewById(R.id.category);

        }
    }


    public class TimelineAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public TimelineAdapter(Context context, int viewResourceId, ArrayList<UserData> timelineusers) {
            super(context, viewResourceId, timelineusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int line = (position/4)*4;
            int pos = position - line;

            UserData user = this.getItem(position);

            //元あった感じのswitch文にも戻してみる
            if (convertView == null) {


                switch(pos){
                            case 0:
                                //０の操作
                                convertView = layoutInflater.inflate(R.layout.name_picture_bar, null);

                                timelineHolder = new TimelineHolder(convertView);
                                convertView.setTag(timelineHolder);

                                timelineHolder.user_name.setText(user.getUser_name());
                                Picasso.with(getContext())
                                        .load(user.getPicture())
                                        .resize(50, 50)
                                        .placeholder(R.drawable.ic_userpicture)
                                        .centerCrop()
                                        .transform(new RoundedTransformation())
                                        .into(timelineHolder.circleImage);
                                break;
                            case 1:
                                //１の操作
                                convertView = layoutInflater.inflate(R.layout.video_bar, null);

                                timelineHolder = new TimelineHolder(convertView);
                                convertView.setTag(timelineHolder);
                                
                               /* Uri video = Uri.parse(user.getMovie());

                        viewHolder.movie.setVideoURI(video);
                        viewHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                viewHolder.movie.start();
                                mp.setLooping(true);
                            }
                        });

                        viewHolder.movie.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                isMov = false;
                                viewHolder.movie.setVideoURI(null);
                                //動画終了
                            }
                        });

                        if (!isMov) {
                            isMov = true;
                            viewHolder.movie.setVideoURI(video);
                            //viewHolder.movie.start();
                        }*/
                                break;
                            case 2:
                                //２の操作
                                convertView = layoutInflater.inflate(R.layout.comment_bar, null);

                                timelineHolder = new TimelineHolder(convertView);
                                convertView.setTag(timelineHolder);
                                break;
                            case 3:
                                //３の操作
                                convertView = layoutInflater.inflate(R.layout.restaurant_bar, null);

                                timelineHolder = new TimelineHolder(convertView);
                                convertView.setTag(timelineHolder);

                                timelineHolder.rest_name.setText(user.getRest_name());
                                timelineHolder.category.setText(user.getLocality());
                                break;

                        }


            } else {
                switch (pos) {
                    case 0:
                        timelineHolder = (TimelineHolder) convertView.getTag();
                        break;

                    case 1:
                        timelineHolder = (TimelineHolder) convertView.getTag();
                        break;

                    case 2:
                        timelineHolder = (TimelineHolder) convertView.getTag();
                        break;

                    case 3:
                        timelineHolder = (TimelineHolder) convertView.getTag();
                        break;
                }

            }


            /*viewHolder.restnamebutton.setText(user.getRestname());
            viewHolder.restnamebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                    UserData country = users.get(position);
                    intent.putExtra("restname", country.getRestname());
                    intent.putExtra("locality", country.getLocality());

                    startActivity(intent);
                }
            });*/
            return convertView;
        }
    }

    public class Search_mapAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

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



                LatLng latLng1 = new LatLng(user.getLat(), user.getLon());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                MarkerOptions options = new MarkerOptions();
                options.position(latLng1);
                options.title(user.getLocality());
                options.draggable(false);
                mMap.addMarker(options);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });



            return convertView;
        }
    }

    public class Search_tenpoAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public Search_tenpoAdapter(Context context, int viewResourceId, ArrayList<UserData> search_tenpousers) {
            super(context, viewResourceId, search_tenpousers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                searchtenpoHolder = new SearchTenpoHolder(convertView);
                convertView.setTag(searchtenpoHolder);
            } else {
                searchtenpoHolder = (SearchTenpoHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);


            searchtenpoHolder.restname.setText(user.getRest_name());
            searchtenpoHolder.category.setText(user.getCategory());
            searchtenpoHolder.distance.setText(user.getDistance());


            return convertView;
        }
    }

    public class ProfAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public ProfAdapter(Context context, int viewResourceId, ArrayList<UserData> profusers) {
            super(context, viewResourceId, profusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int line = (position/4)*4;
            int pos = position - line;

            UserData user = this.getItem(position);

            //元あった感じのswitch文にも戻してみる
            if (convertView == null) {

                switch(pos){
                    case 0:
                        //０の操作
                        convertView = layoutInflater.inflate(R.layout.name_picture_bar, null);

                        profHolder = new ProfHolder(convertView);
                        convertView.setTag(profHolder);

                        profHolder.user_name.setText(user.getUser_name());
                        Picasso.with(getContext())
                                .load(user.getPicture())
                                .resize(50, 50)
                                .placeholder(R.drawable.ic_userpicture)
                                .centerCrop()
                                .transform(new RoundedTransformation())
                                .into(profHolder.circleImage);
                        break;

                    case 1:
                        //１の操作
                        convertView = layoutInflater.inflate(R.layout.video_bar, null);

                        profHolder = new ProfHolder(convertView);
                        convertView.setTag(profHolder);

                               /* Uri video = Uri.parse(user.getMovie());

                        viewHolder.movie.setVideoURI(video);
                        viewHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                viewHolder.movie.start();
                                mp.setLooping(true);
                            }
                        });

                        viewHolder.movie.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                isMov = false;
                                viewHolder.movie.setVideoURI(null);
                                //動画終了
                            }
                        });

                        if (!isMov) {
                            isMov = true;
                            viewHolder.movie.setVideoURI(video);
                            //viewHolder.movie.start();
                        }*/
                        break;

                    case 2:
                        //２の操作
                        convertView = layoutInflater.inflate(R.layout.comment_bar, null);

                        profHolder = new ProfHolder(convertView);
                        convertView.setTag(profHolder);
                        break;

                    case 3:
                        //３の操作
                        convertView = layoutInflater.inflate(R.layout.restaurant_bar, null);

                        profHolder = new ProfHolder(convertView);
                        convertView.setTag(profHolder);

                        profHolder.rest_name.setText(user.getRest_name());
                        profHolder.category.setText(user.getLocality());
                        break;
                }

            } else {
                switch (pos) {
                    case 0:
                        profHolder = (ProfHolder) convertView.getTag();
                        break;

                    case 1:
                        profHolder = (ProfHolder) convertView.getTag();
                        break;

                    case 2:
                        profHolder = (ProfHolder) convertView.getTag();
                        break;

                    case 3:
                        profHolder = (ProfHolder) convertView.getTag();
                        break;
                }

            }

            /*viewHolder.restnamebutton.setText(user.getRest_name());
            viewHolder.restnamebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                    UserData country = profuser.get(position);
                    intent.putExtra("restname", country.getRest_name());
                    intent.putExtra("locality", country.getLocality());

                    startActivity(intent);
                }
            });*/
            return convertView;
        }
    }

}

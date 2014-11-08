

package com.example.kinagafuji.gocci.Base;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.PopupHelper;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.UserData;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SlidingTabsBasicFragment extends BaseFragment {

    public SlidingTabLayout mSlidingTabLayout;

    public String url = "https://codelecture.com/gocci/timeline.php";
    public String SearchUrl;

    private ViewHolder viewHolder;

    private String data;
    private String searchdata;

    private CustomProgressDialog dialog;
    public ListView mListView;
    private ListView searchListView;

    private ArrayList<UserData> users = new ArrayList<UserData>();
    private ArrayList<UserData> searchusers = new ArrayList<UserData>();

    private UserAdapter userAdapter;
    private SearchAdapter searchAdapter;

    public double Latitude;
    public double Longitude;

    private static boolean isMov = false;

    public int mCardLayoutIndex = 0;

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

    /**
     * A {@link android.support.v4.view.ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    public ViewPager mViewPager;

    /**
     * Inflates the {@link android.view.View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_sample, container, false);

        new UserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        dialog = new CustomProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.show();
        return v;
    }
    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //mViewPager.setBackgroundColor(R.color.main_color);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setBackgroundColor(R.color.main_color);
        //mSlidingTabLayout.setCustomTabView();
        mSlidingTabLayout.setDividerColors(android.R.color.transparent);
        mSlidingTabLayout.setSelectedIndicatorColors(R.color.main_color_light);
        //mSlidingTabLayout.setCustomTabColorizer();
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 4;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(final ViewGroup container, int position) {

            switch (position) {
                case 0:

                    // Inflate a new layout from our resources
                    View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline,
                            container, false);


                    // Add the newly created View to the ViewPager
                    container.addView(view);

                    Point size = new Point();
                    final int width = size.x;
                    final int height = size.y;

                    mListView = (ListView) view.findViewById(R.id.mylistView2);

                    ImageButton floatImageButton = (ImageButton) view.findViewById(R.id.floatImageButton);
                    floatImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupWindow window = PopupHelper.newBasicPopupWindow(getActivity(), width, height);

                            View inflateView = getActivity().getLayoutInflater().inflate(R.layout.searchlist, container, false);
                            searchListView = (ListView) inflateView.findViewById(R.id.searchListView);

                            setUpLocation();

                            new SearchCameraAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SearchUrl);
                            dialog = new CustomProgressDialog(getActivity());
                            dialog.setCancelable(false);
                            dialog.show();

                            searchAdapter = new SearchAdapter(getActivity(), 0, searchusers);
                            searchListView.setAdapter(searchAdapter);

                            window.setContentView(inflateView);
                            //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);

                            PopupHelper.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);

                        }
                    });



                    userAdapter = new UserAdapter(getActivity(), 0, users);
                    mListView.setDivider(null);
                    // スクロールバーを表示しない
                    mListView.setVerticalScrollBarEnabled(false);
                    // カード部分をselectorにするので、リストのselectorは透明にする
                    mListView.setSelector(android.R.color.transparent);

                    // 最後の余白分のビューを追加
                    if (mCardLayoutIndex > 0) {
                        mListView.addFooterView(LayoutInflater.from(getActivity()).inflate(
                                R.layout.card_footer, mListView, false));
                    }
                    mListView.setAdapter(userAdapter);


                    // Return the View
                    return view;


                case 1:
                    // Inflate a new layout from our resources
                    View view1 = getActivity().getLayoutInflater().inflate(R.layout.fragment_lifelog,
                            container, false);
                    // Add the newly created View to the ViewPager
                    container.addView(view1);

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

                    // Return the View
                    return view3;

            }
            return null;
        }

        private void setUpLocation() {

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);
            Location myLocation = locationManager.getLastKnownLocation(provider);

            Latitude = myLocation.getLatitude();

            Longitude = myLocation.getLongitude();

            Log.d("経度・緯度", Latitude + "/" + Longitude);

            SearchUrl = "https://codelecture.com/gocci/?lat=" + String.valueOf(Latitude) + "&lon=" + String.valueOf(Longitude) + "&limit=30";

        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    public class UserTask extends AsyncTask<String, String, Integer> {

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

            int status = 0;
            if (httpResponse != null) {
                status = httpResponse.getStatusLine().getStatusCode();
            }

            if (HttpStatus.SC_OK == status) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    data = outputStream.toString(); // JSONデータ
                    Log.d("data", data);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                try {
                    JSONArray jsonArray = new JSONArray(data);

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

                        users.add(user);
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
                mListView.invalidateViews();
                userAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    public class SearchCameraAsyncTask extends AsyncTask<String, String, Integer> {

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

            int status = 0;
            if (httpResponse != null) {
                status = httpResponse.getStatusLine().getStatusCode();
            }

            if (HttpStatus.SC_OK == status) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    searchdata = outputStream.toString(); // JSONデータ
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(searchdata);

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

                        searchusers.add(user);

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
                searchListView.invalidateViews();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    private static class ViewHolder {
        VideoView movie;
        ImageView circleImage;
        ImageView restaurantImage;
        TextView comment;
        TextView time;
        TextView user_name;
        TextView rest_name;
        TextView category;

        public ViewHolder(View view) {
            this.movie = (VideoView) view.findViewById(R.id.videoView);
            this.circleImage = (ImageView) view.findViewById(R.id.circleImage);
            this.restaurantImage = (ImageView) view.findViewById(R.id.restaurantImage);
            this.comment = (TextView) view.findViewById(R.id.comment);
            this.time = (TextView) view.findViewById(R.id.time);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.rest_name = (TextView) view.findViewById(R.id.rest_name);
            this.category = (TextView) view.findViewById(R.id.category);
        }
    }

    public static class ViewHolder2 {
        TextView tell;
        TextView restname;
        TextView category;
        TextView locality;
        TextView distance;

        public ViewHolder2(View view) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            //元あった感じのswitch文にも戻してみる
            if (convertView == null) {
                int line = (position/4)*4;
                int pos = position - line;

                UserData user = this.getItem(position);

                switch(pos){
                            case 0:
                                //０の操作
                                convertView = layoutInflater.inflate(R.layout.name_picture_bar, null);

                                viewHolder = new ViewHolder(convertView);
                                convertView.setTag(viewHolder);

                                viewHolder.user_name.setText(user.getUser_name());
                                Picasso.with(getContext())
                                        .load(user.getPicture())
                                        .resize(50, 50)
                                        .placeholder(R.drawable.ic_gocci)
                                        .centerCrop()
                                        .transform(new RoundedTransformation())
                                        .into(viewHolder.circleImage);
                                break;
                            case 1:
                                //１の操作
                                convertView = layoutInflater.inflate(R.layout.video_bar, null);

                                viewHolder = new ViewHolder(convertView);
                                convertView.setTag(viewHolder);

                                convertView.
                                Uri video = Uri.parse(user.getMovie());

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
                        }
                                break;
                            case 2:
                                //２の操作
                                convertView = layoutInflater.inflate(R.layout.comment_bar, null);

                                viewHolder = new ViewHolder(convertView);
                                convertView.setTag(viewHolder);
                                break;
                            case 3:
                                //３の操作
                                convertView = layoutInflater.inflate(R.layout.restaurant_bar, null);

                                viewHolder = new ViewHolder(convertView);
                                convertView.setTag(viewHolder);

                                viewHolder.rest_name.setText(user.getRest_name());
                                viewHolder.category.setText(user.getLocality());
                                break;

                        }


            } else {
                viewHolder = (ViewHolder) convertView.getTag();
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

    public class SearchAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public SearchAdapter(Context context, int viewResourceId, ArrayList<UserData> searchusers) {
            super(context, viewResourceId, searchusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder2 viewHolder2;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                viewHolder2 = new ViewHolder2(convertView);
                convertView.setTag(viewHolder2);
            } else {
                viewHolder2 = (ViewHolder2) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            viewHolder2.tell.setText(user.getTell());
            viewHolder2.restname.setText(user.getRest_name());
            viewHolder2.category.setText(user.getCategory());
            viewHolder2.locality.setText(user.getLocality());
            viewHolder2.distance.setText(user.getDistance());

            return convertView;
        }
    }
}

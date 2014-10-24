package com.example.kinagafuji.gocci.Fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.BaseVideoView;
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


public class TimelineFragment extends BaseFragment {

    private CustomProgressDialog dialog;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static String url = "https://codelecture.com/gocci/timeline.php";
    private String SearchUrl;

    private ViewHolder viewHolder;

    private String data;
    private String searchdata;

    private ArrayList<UserData> users = new ArrayList<UserData>();
    private ArrayList<UserData> searchusers = new ArrayList<UserData>();

    private ListView mListView;
    private ListView searchListView;

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

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        final View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.CYAN, Color.MAGENTA, Color.BLACK);

        mListView = (ListView) rootView.findViewById(R.id.mylistView2);

        ImageButton floatImageButton = (ImageButton) rootView.findViewById(R.id.floatImageButton);
        floatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow window = PopupHelper.newBasicPopupWindow(getActivity());

                View inflateView = inflater.inflate(R.layout.searchlist, container, false);
                searchListView = (ListView) inflateView.findViewById(R.id.searchListView);

                setUpLocation();

                new SearchCameraAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SearchUrl);
                dialog = new CustomProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.show();

                searchAdapter = new SearchAdapter(getActivity(), 0, searchusers);
                searchListView.setAdapter(searchAdapter);

                searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                        //UserData country = searchusers.get(pos);

                        //カメラは後々Intentで設定する。
                        Intent intent = new Intent(getActivity().getApplicationContext(), com.javacv.recorder.FFmpegRecorderActivity.class);
                        startActivity(intent);

                    }

                });

                window.setContentView(inflateView);
                //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                v.getLocationOnScreen(location);

                PopupHelper.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);

            }
        });

        new UserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        dialog = new CustomProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.show();

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

        return rootView;
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

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setEnabled(false);
            new UserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            // 3秒待機
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }, 3000);
        }
    };

    public class UserTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(url);
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
                    data = outputStream.toString(); // JSONデータ
                    Log.d("data", data);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                try {
                    JSONArray jsonArray = new JSONArray(data);

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
                        user.setRestname(restname);
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

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(SearchUrl);
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
                    searchdata = outputStream.toString(); // JSONデータ
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(searchdata);

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
        BaseVideoView movie;
        ImageView picture;
        TextView post_id;
        TextView user_id;
        TextView user_name;
        Button restnamebutton;

        public ViewHolder(View view) {
            this.movie = (BaseVideoView) view.findViewById(R.id.movieview);
            this.picture = (ImageView) view.findViewById(R.id.pictureView);
            this.post_id = (TextView) view.findViewById(R.id.post_id);
            this.user_id = (TextView) view.findViewById(R.id.user_id);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.restnamebutton = (Button) view.findViewById(R.id.restnamebutton);
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
        int mAnimatedPosition = ListView.INVALID_POSITION;

        public UserAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.timeline, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UserData user = this.getItem(position);

            viewHolder.post_id.setText(user.getPost_id());
            viewHolder.user_id.setText(user.getUser_id());
            viewHolder.user_name.setText(user.getUser_name());
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

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .resize(50, 50)
                    .placeholder(R.drawable.ic_gocci)
                    .centerCrop()
                    .transform(new RoundedTransformation())
                    .into(viewHolder.picture);
            //ここをImageではなく、ImageButtonに変更は可能？

            // まだ表示していない位置ならアニメーションする
            if (mAnimatedPosition < position) {
                // XMLからアニメーターを作成
                Animator animator = AnimatorInflater.loadAnimator(getContext(),
                        R.animator.card_slide_in);
                // アニメーションさせるビューをセット
                animator.setTarget(convertView);
                // アニメーションを開始
                animator.start();
                mAnimatedPosition = position;
            }

            viewHolder.restnamebutton.setText(user.getRestname());
            viewHolder.restnamebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                    UserData country = users.get(position);
                    intent.putExtra("restname", country.getRestname());
                    intent.putExtra("locality", country.getLocality());

                    startActivity(intent);
                }
            });
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
            viewHolder2.restname.setText(user.getRestname());
            viewHolder2.category.setText(user.getCategory());
            viewHolder2.locality.setText(user.getLocality());
            viewHolder2.distance.setText(user.getDistance());

            return convertView;
        }
    }
}

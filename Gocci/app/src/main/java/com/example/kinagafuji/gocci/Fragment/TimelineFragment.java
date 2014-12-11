package com.example.kinagafuji.gocci.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Activity.UserProfActivity;
import com.example.kinagafuji.gocci.Base.ArrayListGetEvent;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.BusHolder;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.Base.PageChangeVideoStopEvent;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.View.CommentView;
import com.example.kinagafuji.gocci.View.ToukouView;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.ToukouPopup;
import com.example.kinagafuji.gocci.data.UserData;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;
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
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class TimelineFragment extends BaseFragment implements ListView.OnScrollListener {

    private static final String sTimelineUrl = "http://api-gocci.jp/timeline/";

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
    private static final String TAG_LOCALITY = "locality";

    private static final String TAG = "TimelineFragment";
    private final TimelineFragment self = this;

    public CustomProgressDialog mTimelineDialog;
    public ListView mTimelineListView;
    public ArrayList<UserData> mTimelineusers = new ArrayList<UserData>();
    public ArrayList<UserData> mTenpousers;
    public TimelineAdapter mTimelineAdapter;
    public CommentHolder commentHolder;
    public LikeCommentHolder likeCommentHolder;
    private SwipeRefreshLayout mTimelineSwipe;
    private VideoHolder videoHolder;
    private VideoView nextVideo;
    public String mName;
    public String mPictureImageUrl;

    public boolean mBusy = false;

    public int mGoodCommePosition;
    public int mGoodNumberPosition;

    public String currentgoodnum;
    public String currentcommentnum;


    public TimelineFragment newIntent(String name, String imageUrl) {
        TimelineFragment fragment = new TimelineFragment();
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
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline,
                container, false);

        new TimelineAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sTimelineUrl);
        mTimelineDialog = new CustomProgressDialog(getActivity());
        mTimelineDialog.setCancelable(false);
        mTimelineDialog.show();

        mTimelineListView = (ListView) view.findViewById(R.id.mylistView2);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.toukouButton);
        fab.attachToListView(mTimelineListView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation animation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_repeat);
                animation.setInterpolator(new LinearInterpolator());
                fab.startAnimation(animation);

                View inflateView = new ToukouView(getActivity(), mName, mPictureImageUrl, mTenpousers);

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(getActivity());
                window.setContentView(inflateView);
                //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                ToukouPopup.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);

            }
        });

        mTimelineAdapter = new TimelineAdapter(getActivity(), 0, mTimelineusers);
        mTimelineListView.setDivider(null);
        // スクロールバーを表示しない
        mTimelineListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mTimelineListView.setSelector(android.R.color.transparent);

        mTimelineListView.setAdapter(mTimelineAdapter);

        mTimelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int line = (position / 5) * 5;
                int pos = position - line;
                UserData country = mTimelineusers.get(position);

                switch (pos) {
                    case 0:
                        //名前部分のview　プロフィール画面へ
                        //Signupを読み込みそう後回し
                        Intent userintent = new Intent(getActivity(), UserProfActivity.class);
                        userintent.putExtra("username", country.getUser_name());
                        userintent.putExtra("name", mName);
                        userintent.putExtra("pictureImageUrl", mPictureImageUrl);
                        startActivity(userintent);
                        break;

                    case 1:
                        //動画のview
                        //クリックしたら止まるくらい
                        break;

                    case 2:
                        //コメントのview
                        //とくになんもしない
                        break;

                    case 3:
                        //レストランのview
                        //レストラン画面に飛ぼうか
                        Intent intent = new Intent(getActivity(), TenpoActivity.class);
                        intent.putExtra("restname", country.getRest_name());
                        intent.putExtra("name", mName);
                        intent.putExtra("pictureImageUrl", mPictureImageUrl);
                        intent.putExtra("locality", country.getLocality());
                        startActivity(intent);
                        break;

                    case 4:
                        //いいね　コメント　シェア
                        break;

                }
            }
        });

        mTimelineSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_timeline);
        mTimelineSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mTimelineSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new TimelineAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sTimelineUrl);
                mTimelineDialog = new CustomProgressDialog(getActivity());
                mTimelineDialog.setCancelable(false);
                mTimelineDialog.show();
                mTimelineSwipe.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        mPictureImageUrl = args.getString(KEY_IMAGE_URL);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // スクロールしていない
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;
                break;

            // スクロール中
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mBusy = true;
                break;

            // はじいたとき
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                mBusy = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (videoHolder.movie != null) {
                if (videoHolder.movie.isPlaying()) {
                    videoHolder.movie.pause();
                }
            }

            if (nextVideo != null) {
                if (nextVideo.isPlaying()) {
                    nextVideo.pause();
                }
            }
        }catch (NullPointerException e) {
            Log.e("ぬるぽだよ〜","ぬるぽちゃん");
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "Fragment-onResume");
        BusHolder.get().register(self);

        try {
            if (videoHolder.movie != null) {
                if (!videoHolder.movie.isPlaying()) {
                    videoHolder.movie.start();
                }
            }

            if (nextVideo != null) {
                if (!nextVideo.isPlaying()) {
                    nextVideo.start();
                }
            }
        }catch (NullPointerException e) {
            Log.e("ぬるぽだよ〜","ぬるぽちゃん");
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "Fragment-onPause");
        BusHolder.get().unregister(self);

        try {
            if (videoHolder.movie != null) {
                if (videoHolder.movie.isPlaying()) {
                    videoHolder.movie.pause();
                }
            }

            if (nextVideo != null) {
                if (nextVideo.isPlaying()) {
                    nextVideo.pause();
                }
            }
        }catch (NullPointerException e) {
            Log.e("ぬるぽだよ〜","ぬるぽちゃん");
            e.printStackTrace();
        }
    }

    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        if (event.position == 0) {
            //タイムラインが呼ばれた時の処理
            if (!videoHolder.movie.isPlaying()) {
                videoHolder.movie.start();
            }
            if (nextVideo != null && !nextVideo.isPlaying()) {
                nextVideo.start();
            }

            Log.e("Otto発動", "動画再生復帰");
        } else {
            //タイムライン以外のfragmentが可視化している場合
            if (videoHolder.movie.isPlaying()) {
                videoHolder.movie.pause();
            }
            if (nextVideo != null && nextVideo.isPlaying()) {
                nextVideo.pause();
            }

            Log.e("Otto発動", "動画再生停止");
        }
    }

    @Subscribe
    public void subscribe(ArrayListGetEvent event) {
        mTenpousers = event.users;
    }

    public static class NameHolder {
        public ImageView circleImage;
        public TextView user_name;
    }

    public static class VideoHolder {
        public VideoView movie;
        public ImageView mVideoThumbnail;
    }

    public static class CommentHolder {
        public RatingBar star_evaluation;
        public TextView likesnumber;
        public TextView commentsnumber;
        public TextView sharenumber;
    }

    public static class RestHolder {
        public ImageView restaurantImage;
        public TextView locality;
        public TextView rest_name;
    }

    public static class LikeCommentHolder {
        public ImageView likes;
        public ImageView comments;
        public ImageView share;
    }

    public class TimelineGoodAsyncTask extends AsyncTask<String, String, Integer> {

        private static final String sGoodUrl = "http://api-gocci.jp/goodinsert/";
        private static final String sDataurl = "http://api-gocci.jp/login/";

        private int mStatus;
        private int mStatus2;
        private int mStatus3;


        @Override
        protected Integer doInBackground(String... params) {
            String param = params[0];

            HttpClient client = new DefaultHttpClient();

            HttpPost method = new HttpPost(sDataurl);

            ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
            contents.add(new BasicNameValuePair("user_name", mName));
            contents.add(new BasicNameValuePair("picture", mPictureImageUrl));
            Log.d("読み取り", mName + "と" + mPictureImageUrl);

            String body = null;
            try {
                method.setEntity(new UrlEncodedFormEntity(contents, "utf-8"));
                HttpResponse res = client.execute(method);
                mStatus = res.getStatusLine().getStatusCode();
                Log.d("TAGだよ", "反応");
                HttpEntity entity = res.getEntity();
                body = EntityUtils.toString(entity, "UTF-8");
                Log.d("bodyの中身だよ", body);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (HttpStatus.SC_OK == mStatus) {

                HttpPost goodnummethod = new HttpPost(sGoodUrl);

                ArrayList<NameValuePair> goodnumcontents = new ArrayList<NameValuePair>();
                goodnumcontents.add(new BasicNameValuePair("post_id", param));
                Log.d("読み取り", param);

                String goodnumbody = null;
                try {
                    goodnummethod.setEntity(new UrlEncodedFormEntity(goodnumcontents, "utf-8"));
                    HttpResponse goodnumres = client.execute(goodnummethod);
                    mStatus2 = goodnumres.getStatusLine().getStatusCode();
                    Log.d("TAGだよ", "反応");
                    HttpEntity goodnumentity = goodnumres.getEntity();
                    goodnumbody = EntityUtils.toString(goodnumentity, "UTF-8");
                    Log.d("bodyの中身だよ", goodnumbody);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (HttpStatus.SC_OK == mStatus2) {
                //TimelineJSONの読み込み
                HttpGet request = new HttpGet(sTimelineUrl);
                HttpResponse httpResponse = null;

                try {
                    httpResponse = client.execute(request);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                mStatus3 = httpResponse.getStatusLine().getStatusCode();

                if (HttpStatus.SC_OK == mStatus3) {
                    String timelineData = null;
                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(outputStream);
                        timelineData = outputStream.toString(); // JSONデータ
                        Log.d("data", timelineData);
                    } catch (Exception e) {
                        Log.d("error", String.valueOf(e));
                    }

                    mTimelineusers.clear();

                    try {
                        JSONArray jsonArray = new JSONArray(timelineData);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String post_id = jsonObject.getString(TAG_POST_ID);
                            Integer user_id = jsonObject.getInt(TAG_USER_ID);
                            String user_name = jsonObject.getString(TAG_USER_NAME);
                            String picture = jsonObject.getString(TAG_PICTURE);
                            String movie = jsonObject.getString(TAG_MOVIE);
                            String rest_name = jsonObject.getString(TAG_RESTNAME);
                            Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                            Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                            String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                            Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
                            String locality = jsonObject.getString(TAG_LOCALITY);

                            UserData user1 = new UserData();
                            user1.setUser_name(user_name);
                            user1.setPicture(picture);
                            mTimelineusers.add(user1);

                            UserData user2 = new UserData();
                            user2.setMovie(movie);
                            user2.setThumbnail(thumbnail);
                            mTimelineusers.add(user2);

                            UserData user3 = new UserData();
                            user3.setComment_num(comment_num);
                            user3.setgoodnum(goodnum);
                            user3.setStar_evaluation(star_evaluation);
                            mTimelineusers.add(user3);

                            UserData user4 = new UserData();
                            user4.setRest_name(rest_name);
                            user4.setLocality(locality);
                            mTimelineusers.add(user4);

                            UserData user5 = new UserData();
                            user5.setPost_id(post_id);
                            user5.setUser_id(user_id);
                            mTimelineusers.add(user5);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error", String.valueOf(e));
                    }finally {
                        // shutdownすると通信できなくなる
                        client.getConnectionManager().shutdown();
                    }
                } else {
                    Log.d("JSONSampleActivity", "Status" + mStatus3);
                }
            }

            return mStatus3;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null && result == HttpStatus.SC_OK) {
                //いいねが送れた処理　項目itemの更新
                /*
                mTimelineAdapter.notifyDataSetChanged();
                mTimelineListView.invalidateViews();
                */

                View targetView = mTimelineListView.getChildAt(mGoodNumberPosition);
                mTimelineListView.getAdapter().getView(mGoodNumberPosition, targetView, mTimelineListView);
                Log.e("いいね追加成功", "成功しました"+"/"+"位置" + mGoodNumberPosition);


            } else {
                //失敗のため、いいね取り消し
                ImageView likesView = (ImageView)mTimelineListView.findViewWithTag(mGoodCommePosition);
                TextView likesnumberView = (TextView)mTimelineListView.findViewWithTag(mGoodNumberPosition);
                likesnumberView.setText(currentgoodnum);
                likesView.setClickable(true);
                likesView.setBackgroundResource(R.drawable.ic_like);
                Toast.makeText(getActivity(), "いいね追加に失敗しました。", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class TimelineAsyncTask extends AsyncTask<String, String, Integer> {


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
                String timelineData = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    timelineData = outputStream.toString(); // JSONデータ
                    Log.d("data", timelineData);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                mTimelineusers.clear();


                try {
                    JSONArray jsonArray = new JSONArray(timelineData);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        Integer user_id = jsonObject.getInt(TAG_USER_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String rest_name = jsonObject.getString(TAG_RESTNAME);
                        Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                        Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
                        String locality = jsonObject.getString(TAG_LOCALITY);

                        UserData user1 = new UserData();
                        user1.setUser_name(user_name);
                        user1.setPicture(picture);
                        mTimelineusers.add(user1);

                        UserData user2 = new UserData();
                        user2.setMovie(movie);
                        user2.setThumbnail(thumbnail);
                        mTimelineusers.add(user2);

                        UserData user3 = new UserData();
                        user3.setComment_num(comment_num);
                        user3.setgoodnum(goodnum);
                        user3.setStar_evaluation(star_evaluation);
                        mTimelineusers.add(user3);

                        UserData user4 = new UserData();
                        user4.setRest_name(rest_name);
                        user4.setLocality(locality);
                        mTimelineusers.add(user4);

                        UserData user5 = new UserData();
                        user5.setPost_id(post_id);
                        user5.setUser_id(user_id);
                        mTimelineusers.add(user5);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("error", String.valueOf(e));
                }finally {
                    // shutdownすると通信できなくなる
                    httpClient.getConnectionManager().shutdown();
                }
            } else {
                Log.d("JSONSampleActivity", "Status" + status);
            }

            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (!isDetached() && isAdded()) {
                if (result != null && result == HttpStatus.SC_OK) {
                    //ListViewの最読み込み
                    mTimelineListView.invalidateViews();
                    mTimelineAdapter.notifyDataSetChanged();

                } else {
                    //通信失敗した際のエラー処理
                    Toast.makeText(getActivity(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
                }

                mTimelineDialog.dismiss();
            }
        }
    }

    public class TimelineAdapter extends ArrayAdapter<UserData> {
        public String mNextCommentnum;
        private int mShowPosition;
        private String mNextGoodnum;

        private NameHolder nameHolder;
        private RestHolder restHolder;

        public TimelineAdapter(Context context, int viewResourceId, ArrayList<UserData> timelineusers) {
            super(context, viewResourceId, timelineusers);
        }

        @Override
        public int getItemViewType(int position) {
            int line = (position / 5) * 5;
            int pos = position - line;

            switch (pos) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                default:
                    return 4;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final UserData user = getItem(position);

            switch (getItemViewType(position)) {

                case 0:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.name_picture_bar, null);

                        nameHolder = new NameHolder();
                        nameHolder.circleImage = (ImageView) convertView.findViewById(R.id.circleImage);
                        nameHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);

                        convertView.setTag(nameHolder);
                    } else {
                        nameHolder = (NameHolder) convertView.getTag();
                    }


                    nameHolder.user_name.setText(user.getUser_name());

                    Picasso.with(getContext())
                            .load(user.getPicture())
                            .resize(50, 50)
                            .placeholder(R.drawable.ic_userpicture)
                            .centerCrop()
                            .transform(new RoundedTransformation())
                            .into(nameHolder.circleImage);

                    break;

                case 1:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.video_bar, null);

                        videoHolder = new VideoHolder();
                        videoHolder.movie = (VideoView) convertView.findViewById(R.id.videoView);
                        videoHolder.mVideoThumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);

                        convertView.setTag(videoHolder);
                    } else {
                        videoHolder = (VideoHolder) convertView.getTag();
                    }

                    Picasso.with(getContext())
                            .load(user.getThumbnail())
                            .placeholder(R.color.videobackground)
                            .into(videoHolder.mVideoThumbnail);
                    videoHolder.mVideoThumbnail.setVisibility(View.VISIBLE);

                    if (!mBusy) {

                        videoHolder.movie.setVideoURI(Uri.parse(user.getMovie()));
                        Log.e("読み込みました", user.getMovie());
                        videoHolder.movie.requestFocus();
                        videoHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                nextVideo = (VideoView) mTimelineListView.findViewWithTag(mShowPosition);

                                if (nextVideo != null) {
                                    nextVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.stop();
                                        }
                                    });
                                    Log.e("TAG", "pause : " + mShowPosition);
                                }

                                videoHolder.mVideoThumbnail.setVisibility(View.GONE);
                                videoHolder.movie.start();

                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.start();
                                        mp.setLooping(true);
                                    }
                                });

                                Log.e("TAG", "start : " + position);
                                mShowPosition = position;
                            }
                        });
                        videoHolder.movie.setTag(mShowPosition);
                    }

                    break;

                case 2:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.comment_bar, null);

                        commentHolder = new CommentHolder();
                        commentHolder.star_evaluation = (RatingBar) convertView.findViewById(R.id.star_evaluation);
                        commentHolder.likesnumber = (TextView) convertView.findViewById(R.id.likesnumber);
                        commentHolder.commentsnumber = (TextView) convertView.findViewById(R.id.commentsnumber);
                        commentHolder.sharenumber = (TextView) convertView.findViewById(R.id.sharenumber);

                        convertView.setTag(commentHolder);
                    } else {
                        commentHolder = (CommentHolder) convertView.getTag();
                    }

                    currentgoodnum = String.valueOf((user.getgoodnum()));
                    currentcommentnum = String.valueOf(user.getComment_num());
                    mNextGoodnum = String.valueOf(user.getgoodnum()+1);
                    mNextCommentnum = String.valueOf((user.getComment_num()+1));

                    commentHolder.likesnumber.setText(currentgoodnum);
                    commentHolder.commentsnumber.setText(currentcommentnum);

                    commentHolder.star_evaluation.setIsIndicator(true);
                    commentHolder.star_evaluation.setRating(user.getStar_evaluation());
                    Log.e("星を読み込んだよ",String.valueOf(user.getStar_evaluation()));

                    break;

                case 3:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.restaurant_bar, null);

                        restHolder = new RestHolder();
                        restHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
                        restHolder.rest_name = (TextView) convertView.findViewById(R.id.rest_name);
                        restHolder.locality = (TextView) convertView.findViewById(R.id.locality);

                        convertView.setTag(restHolder);
                    } else {
                        restHolder = (RestHolder) convertView.getTag();
                    }

                    restHolder.rest_name.setText(user.getRest_name());
                    restHolder.locality.setText(user.getLocality());

                    break;

                default:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.likes_comments_bar, null);

                        likeCommentHolder = new LikeCommentHolder();
                        likeCommentHolder.likes = (ImageView) convertView.findViewById(R.id.likes);
                        likeCommentHolder.comments = (ImageView) convertView.findViewById(R.id.comments);
                        likeCommentHolder.share = (ImageView) convertView.findViewById(R.id.share);

                        convertView.setTag(likeCommentHolder);
                    } else {
                        likeCommentHolder = (LikeCommentHolder) convertView.getTag();
                    }

                    //クリックされた時の処理
                    if (position == mGoodCommePosition) {
                        Log.e("いいね入れ替え部分", "通ったよ"+ "/" + position + "/" + mGoodCommePosition);
                        likeCommentHolder.likes.setClickable(false);
                        likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);
                    } else {
                        likeCommentHolder.likes.setClickable(true);
                        likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like);

                    }

                    likeCommentHolder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("いいねをクリック", user.getPost_id() + mNextGoodnum);
                            mGoodCommePosition = position;
                            mGoodNumberPosition = (position-2);

                            likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);
                            likeCommentHolder.likes.setClickable(false);
                            likeCommentHolder.likes.setTag(mGoodCommePosition);
                            commentHolder.likesnumber.setText(mNextGoodnum);
                            commentHolder.likesnumber.setTag(mGoodNumberPosition);

                            new TimelineGoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user.getPost_id());
                        }
                    });

                    likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                            View commentView = new CommentView(getActivity(), mName, mPictureImageUrl, user.getPost_id());

                            MaterialDialog mMaterialDialog = new MaterialDialog(getActivity())
                            .setContentView(commentView)
                                    .setCanceledOnTouchOutside(true);
                            mMaterialDialog.show();
                        }
                    });

                    break;
            }

            return convertView;

        }
    }

}

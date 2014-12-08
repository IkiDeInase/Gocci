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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Activity.UserProfActivity;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.BusHolder;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.Base.PageChangeVideoStopEvent;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.View.CommentView;
import com.example.kinagafuji.gocci.View.ToukouView;
import com.example.kinagafuji.gocci.data.LayoutHolder;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.ToukouPopup;
import com.example.kinagafuji.gocci.data.UserData;
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

public class TimelineFragment extends BaseFragment implements ListView.OnScrollListener {

    private static final String sTimelineUrl = "http://api-gocci.jp/timeline/";

    public CustomProgressDialog mTimelineDialog;
    public ListView mTimelineListView;
    public ArrayList<UserData> mTimelineusers = new ArrayList<UserData>();
    public TimelineAdapter mTimelineAdapter;

    private SwipeRefreshLayout mTimelineSwipe;

    public String mName;
    public String mPictureImageUrl;

    public boolean mBusy = false;

    private VideoHolder videoHolder;
    public CommentHolder commentHolder;
    public LikeCommentHolder likeCommentHolder;

    public String currentgoodnum;

    private static final String KEY_IMAGE_URL = "image_url";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG = "TimelineFragment";

    private final TimelineFragment self = this;

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

        final ImageButton toukouButton = (ImageButton) view.findViewById(R.id.toukouButton);
        toukouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation animation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_repeat);
                animation.setInterpolator(new LinearInterpolator());
                toukouButton.startAnimation(animation);

                SharedPreferences pref = getActivity().getSharedPreferences("latlon", Context.MODE_PRIVATE);
                String latitude = pref.getString("latitude", null);
                String longitude = pref.getString("longitude", null);

                double mLatitude = Double.parseDouble(latitude);
                double mLongitude = Double.parseDouble(longitude);

                View inflateView = new ToukouView(getActivity(), mName, mPictureImageUrl, mLatitude, mLongitude);
                Log.d("経度・緯度", mLatitude + "/" + mLongitude);

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
    public void onResume() {
        super.onResume();
        Log.e(TAG, "Fragment-onResume");
        BusHolder.get().register(self);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "Fragment-onPause");
        BusHolder.get().unregister(self);
    }

    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        if (event.position == 0) {
            //タイムラインが呼ばれた時の処理
            videoHolder.movie.start();
            Log.e("Otto発動","動画再生復帰");
        } else {
            //タイムライン以外のfragmentが可視化している場合
            videoHolder.movie.pause();
            Log.e("Otto発動","動画再生停止");
        }



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "Fragment-onDestroyView");
    }

    public class TimelineGoodAsyncTask extends AsyncTask<String, String, Integer> {

        private static final String sGoodUrl = "http://api-gocci.jp/goodinsert/";
        private static final String sDataurl = "http://api-gocci.jp/login/";

        private int mStatus;
        private int mStatus2;


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

            return mStatus2;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null && result == HttpStatus.SC_OK) {
                //いいねが送れた処理　項目itemの更新
                //View numberview = mTimelineListView.getChildAt(mTagPosition);
                //mTimelineListView.getAdapter().getView(mTagPosition,numberview,mTimelineListView);
                mTimelineAdapter.notifyDataSetChanged();
            } else {
                //失敗のため、いいね取り消し
                commentHolder.likesnumber.setText(currentgoodnum);
                likeCommentHolder.likes.setClickable(true);
                likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like);
                Toast.makeText(getActivity(), "いいね追加に失敗しました。", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class TimelineAsyncTask extends AsyncTask<String, String, Integer> {

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
        private LayoutInflater layoutInflater;
        private int mShowPosition;
        private String mNextGoodnum;

        public TimelineAdapter(Context context, int viewResourceId, ArrayList<UserData> timelineusers) {
            super(context, viewResourceId, timelineusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int line = (position / 5) * 5;
            int pos = position - line;

            final UserData user = getItem(position);

            switch (pos) {

                case 0:
                    convertView = layoutInflater.inflate(R.layout.name_picture_bar, null);
                    break;

                case 1:
                    convertView = layoutInflater.inflate(R.layout.video_bar, null);
                    break;

                case 2:
                    convertView = layoutInflater.inflate(R.layout.comment_bar, null);
                    break;

                case 3:
                    convertView = layoutInflater.inflate(R.layout.restaurant_bar, null);
                    break;

                case 4:
                    convertView = layoutInflater.inflate(R.layout.likes_comments_bar, null);
                    break;

            }

            switch (pos) {

                case 0:
                    NameHolder nameHolder = new NameHolder(convertView);

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
                    videoHolder = new VideoHolder(convertView);

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
                                final VideoView nextVideo = (VideoView) mTimelineListView.findViewWithTag(mShowPosition);

                                if (nextVideo != null) {
                                    nextVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.stop();
                                            Log.e("TAG", "pause : " + mShowPosition);
                                            //nextVideo.stopPlayback();
                                            //nextVideo.pause();
                                        }
                                    });
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

                        videoHolder.movie.setTag(position);
                    }
                    break;

                case 2:
                    commentHolder = new CommentHolder(convertView);
                    commentHolder.likesnumber.setText(String.valueOf(user.getgoodnum()));
                    commentHolder.commentsnumber.setText(String.valueOf(user.getComment_num()));

                    mNextGoodnum = String.valueOf(user.getgoodnum() + 1);
                    currentgoodnum = String.valueOf((user.getgoodnum()));

                    break;

                case 3:
                    RestHolder restHolder = new RestHolder(convertView);

                    restHolder.rest_name.setText(user.getRest_name());
                    restHolder.locality.setText(user.getLocality());
                    break;

                case 4:
                    likeCommentHolder = new LikeCommentHolder(convertView);
                    //クリックされた時の処理
                    likeCommentHolder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("いいねをクリック", user.getPost_id() + mNextGoodnum);

                            likeCommentHolder.likes.setClickable(false);
                            commentHolder.likesnumber.setText(mNextGoodnum);
                            //画像差し込み
                            likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);

                            new TimelineGoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user.getPost_id());
                        }
                    });

                    likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                            //引数に入れたい値を入れていく
                            View commentView = new CommentView(getActivity(), mName, mPictureImageUrl, user.getPost_id());

                            final PopupWindow window = ToukouPopup.newBasicPopupWindow(getActivity());
                            window.setContentView(commentView);
                            //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);
                            ToukouPopup.showLikeQuickAction(window, commentView, v, getActivity().getWindowManager(), 0, 0);
                        }
                    });
                    break;

            }

            return convertView;

        }
    }

    public static class NameHolder {
        public ImageView circleImage;
        public TextView user_name;
        public TextView time;

        public NameHolder(View view) {
            this.circleImage = (ImageView) view.findViewById(R.id.circleImage);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.time = (TextView) view.findViewById(R.id.time);
        }
    }

    public static class VideoHolder {
        public VideoView movie;
        public ImageView mVideoThumbnail;

        public VideoHolder(View view) {
            this.movie = (VideoView) view.findViewById(R.id.videoView);
            this.mVideoThumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
        }
    }

    public static class CommentHolder {
        public TextView likesnumber;
        public TextView likes;
        public TextView commentsnumber;
        public TextView comments;
        public TextView sharenumber;
        public TextView share;

        public CommentHolder(View view) {
            this.likesnumber = (TextView) view.findViewById(R.id.likesnumber);
            this.likes = (TextView) view.findViewById(R.id.likes);
            this.commentsnumber = (TextView) view.findViewById(R.id.commentsnumber);
            this.comments = (TextView) view.findViewById(R.id.comments);
            this.sharenumber = (TextView) view.findViewById(R.id.sharenumber);
            this.share = (TextView) view.findViewById(R.id.share);
        }
    }

    public static class RestHolder {
        public ImageView restaurantImage;
        public TextView locality;
        public TextView rest_name;

        public RestHolder(View view) {
            this.restaurantImage = (ImageView) view.findViewById(R.id.restaurantImage);
            this.rest_name = (TextView) view.findViewById(R.id.rest_name);
            this.locality = (TextView) view.findViewById(R.id.locality);
        }
    }

    public static class LikeCommentHolder {
        public ImageView likes;
        public ImageView comments;
        public ImageView share;

        public LikeCommentHolder(View view) {
            this.likes = (ImageView) view.findViewById(R.id.likes);
            this.comments = (ImageView) view.findViewById(R.id.comments);
            this.share = (ImageView) view.findViewById(R.id.share);

        }
    }

}

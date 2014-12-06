package com.example.kinagafuji.gocci.Activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Base.BaseActivity;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TenpoActivity extends BaseActivity implements ListView.OnScrollListener {

    private static final String TAG_POST_ID = "post_id";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_REVIEW = "review";
    private static final String TAG_GOODNUM = "goodnum";
    private static final String TAG_COMMENT_NUM = "comment_num";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_STAR_EVALUATION = "star_evaluation";

    private static final String sGoodUrl = "http://api-gocci.jp/goodinsert/";

    private CustomProgressDialog mTenpoDialog;
    private ArrayList<UserData> mTenpousers = new ArrayList<UserData>();
    private ListView mTenpoListView;
    private TenpoAdapter mTenpoAdapter;
    private SwipeRefreshLayout mTenpoSwipe;

    private String mPost_restname;
    private String mName;
    private String mPictureImageUrl;

    private String mEncoderestname;

    private VideoHolder videoHolder;

    private boolean mBusy = false;

    private int mShowPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenpo);

        Intent intent = getIntent();
        mPost_restname = intent.getStringExtra("restname");
        String mPost_locality = intent.getStringExtra("locality");
        mName = intent.getStringExtra("name");
        mPictureImageUrl = intent.getStringExtra("pictureImageUrl");

        try {
            mEncoderestname = URLEncoder.encode(mPost_restname, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String mTenpoUrl = "http://api-gocci.jp/restpage/?restname=" + mEncoderestname;

        /*
        TextView restname = (TextView) findViewById(R.id.restname);
        restname.setText(mPost_restname);
        TextView locality = (TextView) findViewById(R.id.locality);
        locality.setText(mPost_locality);

        ImageView imageurl = (ImageView)findViewById(R.id.post_Imageurl);
        */
        /*Picasso.with(getApplicationContext())
                .load()
                .resize(80, 80)
                .placeholder(R.drawable.ic_userpicture)
                .centerCrop()
                .transform(new RoundedTransformation())
                .into(imageurl);*/

        final ImageButton toukoubutton = (ImageButton) findViewById(R.id.toukouButton);
        toukoubutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            RotateAnimation animation = (RotateAnimation) AnimationUtils.loadAnimation(TenpoActivity.this, R.anim.rotate_repeat);
                            animation.setInterpolator(new LinearInterpolator());
                            toukoubutton.startAnimation(animation);
                Intent intent = new Intent(TenpoActivity.this, IntentVineCamera.class);
                intent.putExtra("restname", mPost_restname);
                intent.putExtra("name", mName);
                intent.putExtra("pictureImageUrl", mPictureImageUrl);
                startActivity(intent);
            }
        });


        new TenpoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTenpoUrl);
        mTenpoDialog = new CustomProgressDialog(this);
        mTenpoDialog.setCancelable(false);
        mTenpoDialog.show();

        mTenpoAdapter = new TenpoAdapter(this, 0, mTenpousers);

        mTenpoListView = (ListView) findViewById(R.id.tenpolist);
        mTenpoListView.setDivider(null);
        // スクロールバーを表示しない
        mTenpoListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mTenpoListView.setSelector(android.R.color.transparent);

        mTenpoListView.setAdapter(mTenpoAdapter);

        mTenpoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserData country = mTenpousers.get(position);
                int line = (position / 5) * 5;
                int pos = position - line;

                switch(pos) {
                    case 0:
                        //名前部分のview　プロフィール画面へ
                        //Signupを読み込みそう後回し
                        Intent userintent = new Intent(TenpoActivity.this, UserProfActivity.class);
                        userintent.putExtra("user_name", country.getUser_name());
                        userintent.putExtra("mName",mName);
                        userintent.putExtra("mPictureImageUrl",mPictureImageUrl);
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

                        break;
                    case 4:
                        //いいね　コメント　シェア
                        break;
                }

            }
        });


        mTenpoSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mTenpoSwipe.setColorSchemeColors(R.color.main_color_light,R.color.gocci,R.color.main_color_dark,R.color.window_bg);
        mTenpoSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//Handle the refresh then call
                new TenpoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTenpoUrl);
                mTenpoDialog = new CustomProgressDialog(getApplication());
                mTenpoDialog.setCancelable(false);
                mTenpoDialog.show();
                mTenpoSwipe.setRefreshing(false);

            }
        });

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

    public class TenpoAsyncTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String param = params[0];
            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(param);
            HttpResponse httpResponse = null;

            try {
                httpResponse = httpClient.execute(request);
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }

            int status = httpResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {
                String mTenpoData = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    mTenpoData = outputStream.toString(); // JSONデータ
                    Log.d("data", mTenpoData);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                try {
                    JSONArray jsonArray = new JSONArray(mTenpoData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String restname = jsonObject.getString(TAG_RESTNAME);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String review = jsonObject.getString(TAG_REVIEW);
                        Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                        Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);

                        UserData user1 = new UserData();
                        user1.setUser_name(user_name);
                        user1.setPicture(picture);
                        mTenpousers.add(user1);

                        UserData user2 = new UserData();
                        user2.setMovie(movie);
                        user2.setThumbnail(thumbnail);
                        mTenpousers.add(user2);

                        UserData user3 = new UserData();
                        user3.setReview(review);
                        user3.setComment_num(comment_num);
                        user3.setgoodnum(goodnum);
                        user3.setStar_evaluation(star_evaluation);
                        mTenpousers.add(user3);

                        UserData user4 = new UserData();
                        user4.setRest_name(restname);
                        //user4.setLocality(locality);
                        mTenpousers.add(user4);

                        UserData user5 = new UserData();
                        user5.setPost_id(post_id);
                        //user5.setUser_id(user_id);
                        mTenpousers.add(user5);

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
                mTenpoListView.invalidateViews();
                mTenpoAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(TenpoActivity.this, "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            mTenpoDialog.dismiss();
        }
    }

    private static class NameHolder {
        ImageView circleImage;
        TextView user_name;
        TextView time;

        public NameHolder(View view) {
            this.circleImage = (ImageView) view.findViewById(R.id.circleImage);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.time = (TextView) view.findViewById(R.id.time);
        }
    }

    private static class VideoHolder {
        VideoView movie;
        ImageView mVideoThumbnail;

        public VideoHolder(View view) {
            this.movie = (VideoView) view.findViewById(R.id.videoView);
            this.mVideoThumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
        }
    }

    private static class CommentHolder {
        TextView likesnumber;
        TextView likes;
        TextView commentsnumber;
        TextView comments;
        TextView sharenumber;
        TextView share;

        public CommentHolder(View view) {
            this.likesnumber = (TextView) view.findViewById(R.id.likesnumber);
            this.likes = (TextView) view.findViewById(R.id.likes);
            this.commentsnumber = (TextView) view.findViewById(R.id.commentsnumber);
            this.comments = (TextView) view.findViewById(R.id.comments);
            this.sharenumber = (TextView) view.findViewById(R.id.sharenumber);
            this.share = (TextView) view.findViewById(R.id.share);
        }
    }

    private static class RestHolder {
        ImageView restaurantImage;
        TextView locality;
        TextView rest_name;

        public RestHolder(View view) {
            this.restaurantImage = (ImageView) view.findViewById(R.id.restaurantImage);
            this.rest_name = (TextView) view.findViewById(R.id.rest_name);
            this.locality = (TextView) view.findViewById(R.id.locality);
        }
    }

    private static class LikeCommentHolder {
        ImageView likes;
        ImageView comments;
        ImageView share;

        public LikeCommentHolder(View view) {
            this.likes = (ImageView)view.findViewById(R.id.likes);
            this.comments = (ImageView)view.findViewById(R.id.comments);
            this.share = (ImageView)view.findViewById(R.id.share);

        }
    }

    public class TenpoAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private CommentHolder commentHolder;
        public String mNextGoodnum;

        public TenpoAdapter(Context context, int viewResourceId, ArrayList<UserData> tenpousers) {
            super(context, viewResourceId, tenpousers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int line = (position/5)*5;
            int pos = position - line;

            final UserData user = this.getItem(position);

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

                                VideoView nextVideo = (VideoView) mTenpoListView.findViewWithTag(mShowPosition);

                                if (nextVideo != null) {
                                    Log.e("TAG", "pause : " + mShowPosition);
                                    nextVideo.stopPlayback();
                                }

                                videoHolder.mVideoThumbnail.setVisibility(View.GONE);
                                videoHolder.movie.start();
                                mp.setLooping(true);
                                Log.e("TAG", "start : " + position);
                                mShowPosition = position;
                            }
                        });
                        videoHolder.movie.setTag(position);

                        /*
                        videoHolder.movie.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                videoHolder.movie.seekTo(0);
                                videoHolder.movie.start();
                            }
                        });
                        */
                    }

                    break;

                case 2:
                    commentHolder = new CommentHolder(convertView);
                    commentHolder.likesnumber.setText(String.valueOf(user.getgoodnum()));
                    commentHolder.commentsnumber.setText(String.valueOf(user.getComment_num()));

                    mNextGoodnum = String.valueOf(user.getgoodnum() + 1);
                    break;

                case 3:
                    RestHolder restHolder = new RestHolder(convertView);
                    restHolder.rest_name.setText(user.getRest_name());
                    //restHolder.locality.setText(user.getLocality());
                    break;
                case 4:
                    final LikeCommentHolder likeCommentHolder = new LikeCommentHolder(convertView);
                    likeCommentHolder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("いいねをクリック", user.getPost_id() + sGoodUrl + mNextGoodnum);

                            likeCommentHolder.likes.setClickable(false);
                            commentHolder.likesnumber.setText(mNextGoodnum);
                            //画像差し込み
                        }
                    });

                    likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("コメントをクリック", "コメント！" + user.getPost_id());
                        }
                    });
                    //クリックされた時の処理
                    break;

            }

            return convertView;
        }
    }
}

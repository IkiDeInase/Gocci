package com.example.kinagafuji.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Base.BaseActivity;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.View.CommentView;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class UserProfActivity extends BaseActivity implements ListView.OnScrollListener {

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

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sGoodUrl = "http://api-gocci.jp/goodinsert/";

    private int mGoodCommePosition;
    private int mGoodNumberPosition;
    private int mShowPosition;

    private boolean mBusy = false;

    private String mNextGoodnum;
    private String currentgoodnum;
    private String currentcommentnum;
    private String mNextCommentnum;
    private String mUser_name;
    private String mName;
    private String mPictureImageUrl;
    private String mProfUrl;
    private String mEncodeUser_name;

    private ListView mUserProfListView;
    private UserProfAdapter mUserProfAdapter;
    private SwipeRefreshLayout mUserProfSwipe;
    private ArrayList<UserData> mUserProfusers = new ArrayList<UserData>();

    private NameHolder nameHolder;
    private RestHolder restHolder;
    private VideoHolder videoHolder;
    private VideoView nextVideo;
    private CommentHolder commentHolder;
    private LikeCommentHolder likeCommentHolder;

    private CustomProgressDialog mUserProfDialog;
    private CustomProgressDialog mRefreshUserProfDialog;

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private AsyncHttpClient httpClient3;
    private RequestParams loginParam;
    private RequestParams goodParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prof);

        Intent userintent = getIntent();
        mUser_name = userintent.getStringExtra("username");
        mName = userintent.getStringExtra("name");

        try {
            mEncodeUser_name = URLEncoder.encode(mUser_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mProfUrl = "http://api-gocci.jp/mypage/?user_name=" + mEncodeUser_name;

        loginParam = new RequestParams("user_name", mName);

        httpClient = new AsyncHttpClient();
        httpClient2 = new AsyncHttpClient();
        httpClient3 = new AsyncHttpClient();

        mUserProfListView = (ListView) findViewById(R.id.userProfListView);
        mUserProfAdapter = new UserProfAdapter(this, 0, mUserProfusers);
        mUserProfListView.setDivider(null);
        // スクロールバーを表示しない
        mUserProfListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mUserProfListView.setSelector(android.R.color.transparent);

        getSignupAsync(UserProfActivity.this);

        mUserProfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserData country = mUserProfusers.get(position);
                int line = (position / 5) * 5;
                int pos = position - line;
                switch (pos) {
                    case 0:
                        //名前部分のview　プロフィール画面へ
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
                        Intent intent = new Intent(UserProfActivity.this, TenpoActivity.class);
                        intent.putExtra("restname", country.getRest_name());
                        intent.putExtra("name", mName);
                        intent.putExtra("locality", country.getLocality());
                        startActivity(intent);
                        break;
                    case 4:
                        //いいね　コメント　シェア
                        break;
                }
            }
        });

        mUserProfSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_user_prof);
        mUserProfSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mUserProfSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getRefreshAsync(UserProfActivity.this);
                mUserProfSwipe.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        } catch (NullPointerException e) {
            Log.e("ぬるぽだよ〜", "ぬるぽちゃん");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        } catch (NullPointerException e) {
            Log.e("ぬるぽだよ〜", "ぬるぽちゃん");
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        } catch (NullPointerException e) {
            Log.e("ぬるぽだよ〜", "ぬるぽちゃん");
            e.printStackTrace();
        }
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

    private void getSignupAsync(final Context context) {
        httpClient.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getUserProfJson(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(UserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfJson(Context context) {
        httpClient.get(context, mProfUrl, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mUserProfDialog = new CustomProgressDialog(UserProfActivity.this);
                mUserProfDialog.setCancelable(false);
                mUserProfDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        Integer user_id = jsonObject.getInt(TAG_USER_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String restname = jsonObject.getString(TAG_RESTNAME);
                        Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                        Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
                        //String locality = jsonObject.getString(TAG_LOCALITY);

                        UserData user1 = new UserData();
                        user1.setUser_name(user_name);
                        user1.setPicture(picture);
                        mUserProfusers.add(user1);

                        UserData user2 = new UserData();
                        user2.setMovie(movie);
                        user2.setThumbnail(thumbnail);
                        mUserProfusers.add(user2);

                        UserData user3 = new UserData();
                        user3.setComment_num(comment_num);
                        user3.setgoodnum(goodnum);
                        user3.setStar_evaluation(star_evaluation);
                        mUserProfusers.add(user3);

                        UserData user4 = new UserData();
                        user4.setRest_name(restname);
                        //user4.setLocality(locality);
                        mUserProfusers.add(user4);

                        UserData user5 = new UserData();
                        user5.setPost_id(post_id);
                        user5.setUser_id(user_id);
                        mUserProfusers.add(user5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUserProfListView.setAdapter(mUserProfAdapter);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {

                Toast.makeText(UserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mUserProfDialog.dismiss();
            }
        });
    }

    private void postSignupAsync(final Context context, final String post_id) {
        httpClient2.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postGoodAsync(context, post_id);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(UserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postGoodAsync(final Context context, String post_id) {
        goodParam = new RequestParams("post_id", post_id);
        httpClient2.post(context, sGoodUrl, goodParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getUserProfGoodJson(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(UserProfActivity.this, "いいね送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfGoodJson(Context context) {
        httpClient2.get(context, mProfUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        Integer user_id = jsonObject.getInt(TAG_USER_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String restname = jsonObject.getString(TAG_RESTNAME);
                        Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                        Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
                        //String locality = jsonObject.getString(TAG_LOCALITY);

                        UserData user1 = new UserData();
                        user1.setUser_name(user_name);
                        user1.setPicture(picture);
                        mUserProfusers.add(user1);

                        UserData user2 = new UserData();
                        user2.setMovie(movie);
                        user2.setThumbnail(thumbnail);
                        mUserProfusers.add(user2);

                        UserData user3 = new UserData();
                        user3.setComment_num(comment_num);
                        user3.setgoodnum(goodnum);
                        user3.setStar_evaluation(star_evaluation);
                        mUserProfusers.add(user3);

                        UserData user4 = new UserData();
                        user4.setRest_name(restname);
                        //user4.setLocality(locality);
                        mUserProfusers.add(user4);

                        UserData user5 = new UserData();
                        user5.setPost_id(post_id);
                        user5.setUser_id(user_id);
                        mUserProfusers.add(user5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                View targetView = mUserProfListView.getChildAt(mGoodNumberPosition);
                mUserProfListView.getAdapter().getView(mGoodNumberPosition, targetView, mUserProfListView);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                ImageView likesView = (ImageView) mUserProfListView.findViewWithTag(mGoodCommePosition);
                TextView likesnumberView = (TextView) mUserProfListView.findViewWithTag(mGoodNumberPosition);
                likesnumberView.setText(currentgoodnum);
                likesView.setClickable(true);
                likesView.setBackgroundResource(R.drawable.ic_like);
                Toast.makeText(UserProfActivity.this, "更新に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshAsync(final Context context) {
        httpClient3.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mRefreshUserProfDialog = new CustomProgressDialog(UserProfActivity.this);
                mRefreshUserProfDialog.setCancelable(false);
                mRefreshUserProfDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getRefreshUserProfJson(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshUserProfDialog.dismiss();
                Toast.makeText(UserProfActivity.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshUserProfJson(Context context) {
        httpClient3.get(context, mProfUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String post_id = jsonObject.getString(TAG_POST_ID);
                        Integer user_id = jsonObject.getInt(TAG_USER_ID);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String restname = jsonObject.getString(TAG_RESTNAME);
                        Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                        Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
                        //String locality = jsonObject.getString(TAG_LOCALITY);

                        UserData user1 = new UserData();
                        user1.setUser_name(user_name);
                        user1.setPicture(picture);
                        mUserProfusers.add(user1);

                        UserData user2 = new UserData();
                        user2.setMovie(movie);
                        user2.setThumbnail(thumbnail);
                        mUserProfusers.add(user2);

                        UserData user3 = new UserData();
                        user3.setComment_num(comment_num);
                        user3.setgoodnum(goodnum);
                        user3.setStar_evaluation(star_evaluation);
                        mUserProfusers.add(user3);

                        UserData user4 = new UserData();
                        user4.setRest_name(restname);
                        //user4.setLocality(locality);
                        mUserProfusers.add(user4);

                        UserData user5 = new UserData();
                        user5.setPost_id(post_id);
                        user5.setUser_id(user_id);
                        mUserProfusers.add(user5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mUserProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(UserProfActivity.this, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mRefreshUserProfDialog.dismiss();
            }
        });
    }

    private static class NameHolder {
        ImageView circleImage;
        TextView user_name;
    }

    private static class VideoHolder {
        VideoView movie;
        ImageView mVideoThumbnail;
    }

    private static class CommentHolder {
        RatingBar star_evaluation;
        TextView likesnumber;
        TextView commentsnumber;
        TextView sharenumber;
    }

    private static class RestHolder {
        ImageView restaurantImage;
        TextView locality;
        TextView rest_name;
    }

    private static class LikeCommentHolder {
        ImageView likes;
        ImageView comments;
        ImageView share;
    }

    public class UserProfAdapter extends ArrayAdapter<UserData> {

        public UserProfAdapter(Context context, int viewResourceId, ArrayList<UserData> userprofusers) {
            super(context, viewResourceId, userprofusers);
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
            final UserData user = this.getItem(position);

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
                                nextVideo = (VideoView) mUserProfListView.findViewWithTag(mShowPosition);

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
                    mNextGoodnum = String.valueOf(user.getgoodnum() + 1);
                    mNextCommentnum = String.valueOf((user.getComment_num() + 1));

                    commentHolder.likesnumber.setText(currentgoodnum);
                    commentHolder.commentsnumber.setText(currentcommentnum);

                    commentHolder.star_evaluation.setIsIndicator(true);
                    commentHolder.star_evaluation.setRating(user.getStar_evaluation());
                    Log.e("星を読み込んだよ", String.valueOf(user.getStar_evaluation()));
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
                        Log.e("いいね入れ替え部分", "通ったよ" + "/" + position + "/" + mGoodCommePosition);
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
                            mGoodNumberPosition = (position - 2);

                            likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);
                            likeCommentHolder.likes.setClickable(false);
                            likeCommentHolder.likes.setTag(mGoodCommePosition);
                            commentHolder.likesnumber.setText(mNextGoodnum);
                            commentHolder.likesnumber.setTag(mGoodNumberPosition);

                            postSignupAsync(UserProfActivity.this, user.getPost_id());
                        }
                    });

                    likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("コメントをクリック", "コメント！" + user.getPost_id());
                            commentHolder.commentsnumber.setText(mNextCommentnum);

                            //引数に入れたい値を入れていく
                            View commentView = new CommentView(UserProfActivity.this, mName, user.getPost_id());

                            MaterialDialog mMaterialDialog = new MaterialDialog(UserProfActivity.this)
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

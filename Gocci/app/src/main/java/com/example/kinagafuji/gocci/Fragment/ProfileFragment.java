package com.example.kinagafuji.gocci.Fragment;


import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class ProfileFragment extends BaseFragment implements ListView.OnScrollListener {

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sGoodUrl = "http://api-gocci.jp/goodinsert/";
    private static final String sDataurl = "http://api-gocci.jp/login/";
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
    private final ProfileFragment self = this;
    public int mGoodCommePosition;
    public String mNextGoodnum;
    public String currentgoodnum;
    public String mNextCommentnum;
    private String mProfUrl;
    private CustomProgressDialog mProfDialog;
    private ListView mProfListView;
    private ArrayList<UserData> mProfusers = new ArrayList<UserData>();
    public ArrayList<UserData> mTenpousers;
    private ProfAdapter mProfAdapter;
    private SwipeRefreshLayout mProfSwipe;
    private String mEncode_user_name;
    private String mName;
    private String mPictureImageUrl;
    private VideoView nextVideo;
    private NameHolder nameHolder;
    private RestHolder restHolder;
    private VideoHolder videoHolder;
    private CommentHolder commentHolder;
    private LikeCommentHolder likeCommentHolder;
    private int mShowPosition;
    private boolean mBusy = false;

    public ProfileFragment newIntent(String name, String imageUrl) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view3 = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile,
                container, false);

        /*mPost_name = (TextView) view3.findViewById(R.id.post_name);
        mPost_Imageurl = (ImageView) view3.findViewById(R.id.post_Imageurl);
        */

        mProfListView = (ListView) view3.findViewById(R.id.proflist);

        mProfAdapter = new ProfAdapter(getActivity(), 0, mProfusers);

        mProfListView.setDivider(null);
        // スクロールバーを表示しない
        mProfListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mProfListView.setSelector(android.R.color.transparent);

        mProfListView.setAdapter(mProfAdapter);

        mProfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserData country = mProfusers.get(position);
                int line = (position / 5) * 5;
                int pos = position - line;

                switch (pos) {
                    case 0:
                        //名前部分のview　プロフィール画面へ
                        //Signupを読み込みそう後回し
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
                        intent.putExtra("locality", country.getLocality());
                        startActivity(intent);
                        break;
                    case 4:
                        //いいね　コメント　シェア
                        break;
                }
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) view3.findViewById(R.id.toukouButton);
        fab.attachToListView(mProfListView);

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

        mProfSwipe = (SwipeRefreshLayout) view3.findViewById(R.id.swipe_prof);
        mProfSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mProfSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
//Handle the refresh then call
                new ProfTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mProfDialog = new CustomProgressDialog(getActivity());
                mProfDialog.setCancelable(false);
                mProfDialog.show();
                mProfSwipe.setRefreshing(false);

            }
        });

        return view3;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        mPictureImageUrl = args.getString(KEY_IMAGE_URL);

        new ProfTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mProfDialog = new CustomProgressDialog(getActivity());
        mProfDialog.setCancelable(false);
        mProfDialog.show();


        /*mPost_name.setText(mName);

        Picasso.with(getActivity())
                .load(mPictureImageUrl)
                .resize(80, 80)
                .placeholder(R.drawable.ic_userpicture)
                .centerCrop()
                .transform(new RoundedTransformation())
                .into(mPost_Imageurl);
                */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    @Override
    public void onResume() {

        super.onResume();
        BusHolder.get().register(self);

    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(self);

    }

    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        if (event.position == 3) {
            //タイムラインが呼ばれた時の処理
            videoHolder.movie.start();

            if (nextVideo != null) {
                nextVideo.start();
            }
            Log.e("Otto発動", "動画再生復帰");
        } else {
            //タイムライン以外のfragmentが可視化している場合
            videoHolder.movie.pause();

            if (nextVideo != null) {
                nextVideo.pause();
            }
            Log.e("Otto発動", "動画再生停止");
        }
    }

    @Subscribe
    public void subscribe(ArrayListGetEvent event) {
        mTenpousers = event.users;
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

    public class ProfTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            HttpClient client1 = new DefaultHttpClient();

            HttpPost method = new HttpPost(sSignupUrl);

            ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
            contents.add(new BasicNameValuePair("user_name", mName));
            contents.add(new BasicNameValuePair("picture", mPictureImageUrl));
            Log.d("読み取り", mName + "と" + mPictureImageUrl);

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


            try {
                mEncode_user_name = URLEncoder.encode(mName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            mProfUrl = "http://api-gocci.jp/mypage/?user_name=" + mEncode_user_name;
            HttpGet request = new HttpGet(mProfUrl);
            HttpResponse httpResponse = null;

            try {
                httpResponse = client1.execute(request);
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }

            int status = httpResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {
                String mProfData = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    mProfData = outputStream.toString(); // JSONデータ
                    Log.d("data", mProfData);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                mProfusers.clear();

                try {
                    JSONArray jsonArray = new JSONArray(mProfData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

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
                        mProfusers.add(user1);

                        UserData user2 = new UserData();
                        user2.setMovie(movie);
                        user2.setThumbnail(thumbnail);
                        mProfusers.add(user2);

                        UserData user3 = new UserData();
                        user3.setComment_num(comment_num);
                        user3.setgoodnum(goodnum);
                        user3.setStar_evaluation(star_evaluation);
                        mProfusers.add(user3);

                        UserData user4 = new UserData();
                        user4.setRest_name(restname);
                        //user4.setLocality(locality);
                        mProfusers.add(user4);

                        UserData user5 = new UserData();
                        user5.setPost_id(post_id);
                        user5.setUser_id(user_id);
                        mProfusers.add(user5);

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
                mProfListView.invalidateViews();
                mProfAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            mProfDialog.dismiss();
        }
    }

    public class ProfAdapter extends ArrayAdapter<UserData> {

        public ProfAdapter(Context context, int viewResourceId, ArrayList<UserData> profusers) {
            super(context, viewResourceId, profusers);
        }

        @Override
        public int getItemViewType(int position) {
            int line = (position / 5) * 5;
            int pos = position - line;
            Log.e("どんなposition/どのタイミングで帰ってくるのか？", String.valueOf(position));

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
                                nextVideo = (VideoView) mProfListView.findViewWithTag(mShowPosition);

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

                    commentHolder.likesnumber.setText(String.valueOf(user.getgoodnum()));
                    commentHolder.commentsnumber.setText(String.valueOf(user.getComment_num()));

                    commentHolder.star_evaluation.setIsIndicator(true);
                    commentHolder.star_evaluation.setRating((float) user.getStar_evaluation());

                    mNextGoodnum = String.valueOf(user.getgoodnum() + 1);
                    currentgoodnum = String.valueOf((user.getgoodnum()));
                    mNextCommentnum = String.valueOf((user.getComment_num() + 1));

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
                    if (mGoodCommePosition == position) {
                        likeCommentHolder.likes.setClickable(false);
                        likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);
                    }

                    likeCommentHolder.likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("いいねをクリック", user.getPost_id() + mNextGoodnum);
                            mGoodCommePosition = position;

                            likeCommentHolder.likes.setClickable(false);
                            commentHolder.likesnumber.setText(mNextGoodnum);
                            //画像差し込み
                            likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);

                            new ProfGoodnumTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user.getPost_id());
                        }
                    });

                    likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("コメントをクリック", "コメント！" + user.getPost_id());
                            commentHolder.commentsnumber.setText(mNextCommentnum);

                            //引数に入れたい値を入れていく
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

    public class ProfGoodnumTask extends AsyncTask<String, String, Integer> {
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

                HttpGet request = new HttpGet(mProfUrl);
                HttpResponse httpResponse = null;

                try {
                    httpResponse = client.execute(request);
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }

                mStatus3 = httpResponse.getStatusLine().getStatusCode();

                if (HttpStatus.SC_OK == mStatus3) {
                    String mProfData = null;
                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(outputStream);
                        mProfData = outputStream.toString(); // JSONデータ
                        Log.d("data", mProfData);
                    } catch (Exception e) {
                        Log.d("error", String.valueOf(e));
                    }

                    mProfusers.clear();

                    try {
                        JSONArray jsonArray = new JSONArray(mProfData);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

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
                            mProfusers.add(user1);

                            UserData user2 = new UserData();
                            user2.setMovie(movie);
                            user2.setThumbnail(thumbnail);
                            mProfusers.add(user2);

                            UserData user3 = new UserData();
                            user3.setComment_num(comment_num);
                            user3.setgoodnum(goodnum);
                            user3.setStar_evaluation(star_evaluation);
                            mProfusers.add(user3);

                            UserData user4 = new UserData();
                            user4.setRest_name(restname);
                            //user4.setLocality(locality);
                            mProfusers.add(user4);

                            UserData user5 = new UserData();
                            user5.setPost_id(post_id);
                            user5.setUser_id(user_id);
                            mProfusers.add(user5);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("error", String.valueOf(e));
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
                View targetView = mProfListView.getChildAt((mGoodCommePosition - 2));
                mProfListView.getAdapter().getView((mGoodCommePosition - 2), targetView, mProfListView);
                Log.e("いいね追加成功", "成功しました");
            } else {
                //失敗のため、いいね取り消し
                commentHolder.likesnumber.setText(currentgoodnum);
                likeCommentHolder.likes.setClickable(true);
                likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like);
                Toast.makeText(getActivity().getApplicationContext(), "いいね追加に失敗しました。", Toast.LENGTH_SHORT).show();
            }

        }

    }
}


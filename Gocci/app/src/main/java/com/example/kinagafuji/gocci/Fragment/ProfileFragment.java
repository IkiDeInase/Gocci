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
import com.example.kinagafuji.gocci.Application_Gocci;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseUser;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
    private static final String sPostDeleteUrl = "http://api-gocci.jp/delete/";

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

    private final ProfileFragment profileself = this;

    private int mGoodCommePosition;
    private int mGoodNumberPosition;
    private int mShowPosition;

    private String mNextGoodnum;
    private String currentgoodnum;
    private String currentcommentnum;
    private String mNextCommentnum;
    private String mProfUrl;
    private String mEncode_user_name;
    private String mName;
    private String mPictureImageUrl;

    private CustomProgressDialog mProfDialog;
    private CustomProgressDialog mPostDeleteDialog;
    private CustomProgressDialog mRefreshProfDialog;
    private ListView mProfListView;
    private ArrayList<UserData> mProfusers = new ArrayList<UserData>();
    private ArrayList<UserData> mTenpousers;
    private ProfAdapter mProfAdapter;
    private SwipeRefreshLayout mProfSwipe;

    private NameHolder nameHolder;
    private RestHolder restHolder;
    private VideoView nextVideo;
    private VideoHolder videoHolder;
    private CommentHolder commentHolder;
    private LikeCommentHolder likeCommentHolder;

    private boolean mBusy = false;

    private MaterialDialog mMaterialDialog;

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private AsyncHttpClient httpClient3;
    private AsyncHttpClient httpClient4;
    private RequestParams loginParam;
    private RequestParams goodParam;
    private RequestParams deleteParam;

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
        View view3 = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile,container, false);

        httpClient = new AsyncHttpClient();
        httpClient2 = new AsyncHttpClient();
        httpClient3 = new AsyncHttpClient();
        httpClient4 = new AsyncHttpClient();

        ParseUser user = ParseUser.getCurrentUser();
        mName = user.getString("name");

        loginParam = new RequestParams("user_name", mName);

        try {
            mEncode_user_name = URLEncoder.encode(mName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mProfUrl = "http://api-gocci.jp/mypage/?user_name=" + mEncode_user_name;

        mProfListView = (ListView) view3.findViewById(R.id.proflist);
        mProfListView.setDivider(null);
        // スクロールバーを表示しない
        mProfListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mProfListView.setSelector(android.R.color.transparent);

        mProfAdapter = new ProfAdapter(getActivity(), 0, mProfusers);

        getSignupAsync(getActivity());//サインアップとJSON

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

        mProfListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int line = (position / 5) * 5;
                int pos = position - line;
                switch (pos) {
                    case 0:
                        UserData delete0 = mProfusers.get(position + 4);
                        setDeleteDialog(delete0.getPost_id());
                        break;
                    case 1:
                        UserData delete1 = mProfusers.get(position + 3);
                        setDeleteDialog(delete1.getPost_id());
                        break;
                    case 2:
                        UserData delete2 = mProfusers.get(position + 2);
                        setDeleteDialog(delete2.getPost_id());
                        break;
                    case 3:
                        UserData delete3 = mProfusers.get(position + 1);
                        setDeleteDialog(delete3.getPost_id());
                        break;
                    case 4:
                        UserData delete4 = mProfusers.get(position);
                        setDeleteDialog(delete4.getPost_id());
                        break;
                }
                return true;
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

                View inflateView = new ToukouView(getActivity(), mName, mTenpousers);

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
                getRefreshAsync(getActivity());
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
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(profileself);

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
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(profileself);

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

    @Subscribe
    public void subscribe(PageChangeVideoStopEvent event) {
        if (event.position == 3) {
            //タイムラインが呼ばれた時の処理
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
                Log.e("再生ぬるぽだよ〜", "ぬるぽちゃん");
                e.printStackTrace();
            }
            Log.e("Otto発動", "動画再生復帰");
        } else {
            //タイムライン以外のfragmentが可視化している場合
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
                Log.e("中止ぬるぽだよ〜", "ぬるぽちゃん");
                e.printStackTrace();
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
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

    private void setDeleteDialog(final String post_id) {
        mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle("投稿の削除")
                .setMessage("この投稿を削除しますか？")
                .setPositiveButton("はい", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mMaterialDialog.dismiss();
                        deleteSignupAsync(getActivity(), post_id);
                    }
                })
                .setNegativeButton("いいえ", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
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

    private void getSignupAsync(final Context context) {
        httpClient.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getProfileJson(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfileJson(Context context) {
        httpClient.get(context, mProfUrl, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                mProfDialog = new CustomProgressDialog(getActivity());
                mProfDialog.setCancelable(false);
                mProfDialog.show();
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
                }

                mProfListView.setAdapter(mProfAdapter);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mProfDialog.dismiss();
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
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postGoodAsync(final Context context, String post_id) {
        goodParam = new RequestParams("post_id", post_id);
        httpClient2.post(context, sGoodUrl, goodParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getProfileGoodJson(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "いいね送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfileGoodJson(Context context) {
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
                }

                View targetView = mProfListView.getChildAt(mGoodNumberPosition);
                mProfListView.getAdapter().getView(mGoodNumberPosition, targetView, mProfListView);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                ImageView likesView = (ImageView) mProfListView.findViewWithTag(mGoodCommePosition);
                TextView likesnumberView = (TextView) mProfListView.findViewWithTag(mGoodNumberPosition);
                likesnumberView.setText(currentgoodnum);
                likesView.setClickable(true);
                likesView.setBackgroundResource(R.drawable.ic_like);
                Toast.makeText(getActivity(), "更新に失敗しました", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getRefreshAsync(final Context context) {
        httpClient3.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mRefreshProfDialog = new CustomProgressDialog(getActivity());
                mRefreshProfDialog.setCancelable(false);
                mRefreshProfDialog.show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getRefreshProfileJson(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshProfDialog.dismiss();
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRefreshProfileJson(Context context) {
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
                }

                mProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mRefreshProfDialog.dismiss();
            }
        });
    }

    private void deleteSignupAsync(final Context context, final String post_id) {
        httpClient4.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mPostDeleteDialog = new CustomProgressDialog(getActivity());
                mPostDeleteDialog.setCancelable(false);
                mPostDeleteDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postDeleteAsync(context, post_id);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mMaterialDialog.dismiss();
                mPostDeleteDialog.dismiss();
                Toast.makeText(getActivity(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postDeleteAsync(final Context context, String post_id) {
        deleteParam = new RequestParams("post_id", post_id);
        httpClient4.post(context, sPostDeleteUrl, deleteParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getDeleteProfileJson(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mMaterialDialog.dismiss();
                mPostDeleteDialog.dismiss();
                Toast.makeText(getActivity(), "いいね送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeleteProfileJson(Context context) {
        httpClient4.get(context, mProfUrl, new JsonHttpResponseHandler() {
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
                }

                mProfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getActivity(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
            mMaterialDialog.dismiss();
            mRefreshProfDialog.dismiss();
            }
        });
    }

    public class ProfAdapter extends ArrayAdapter<UserData> {

        public ProfAdapter(Context context, int viewResourceId, ArrayList<UserData> profusers) {
            super(context, viewResourceId, profusers);
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

                            postSignupAsync(getActivity(), user.getPost_id());
                        }
                    });

                    likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("コメントをクリック", "コメント！" + user.getPost_id());
                            commentHolder.commentsnumber.setText(mNextCommentnum);

                            //引数に入れたい値を入れていく
                            View commentView = new CommentView(getActivity(), mName, user.getPost_id());

                            MaterialDialog mMaterialDialog = new MaterialDialog(getActivity())
                                    .setContentView(commentView)
                                    .setCanceledOnTouchOutside(true);
                            mMaterialDialog.show();
                        }
                    });
                    videoHolder.movie.setTag(mShowPosition);
                    break;
            }
            return convertView;
        }
    }
}


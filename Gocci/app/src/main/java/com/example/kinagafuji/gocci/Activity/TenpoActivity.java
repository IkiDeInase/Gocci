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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class TenpoActivity extends BaseActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

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

    private CustomProgressDialog dialog;

    private String data;

    private ArrayList<UserData> users = new ArrayList<UserData>();

    private String restname1;
    private String locality1;
    private String tenpoUrl;
    private String encoderestname;


    private ListView tenpoListView;

    public TenpoAdapter tenpoAdapter;

    private int mCardLayoutIndex = 0;

    private static boolean isMov = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenpo);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(Color.TRANSPARENT, Color.GRAY, Color.TRANSPARENT, Color.TRANSPARENT);

        Intent intent = getIntent();
        restname1 = intent.getStringExtra("restname");
        locality1 = intent.getStringExtra("locality");

        try {
            encoderestname = URLEncoder.encode(restname1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        tenpoUrl = "https://codelecture.com/gocci/submit/restpage.php?restname=" + encoderestname;

        TextView TenpoView = (TextView) findViewById(R.id.TenpoView);
        TenpoView.setText(restname1);
        TextView TenpoView2 = (TextView) findViewById(R.id.TenpoView2);
        TenpoView2.setText(locality1);

        Button button = (Button) findViewById(R.id.toukoubutton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TenpoActivity.this, com.javacv.recorder.FFmpegRecorderActivity.class);
                intent.putExtra("restname", restname1);
                startActivity(intent);
            }
        });


        new MyTenpoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tenpoUrl);
        dialog = new CustomProgressDialog(this);
        dialog.setCancelable(false);
        dialog.show();

        tenpoAdapter = new TenpoAdapter(this, 0, users);

        tenpoListView = (ListView) findViewById(R.id.mylistView3);
        tenpoListView.setDivider(null);
        // スクロールバーを表示しない
        tenpoListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        tenpoListView.setSelector(android.R.color.transparent);

        // 最後の余白分のビューを追加
        if (mCardLayoutIndex > 0) {
            tenpoListView.addFooterView(LayoutInflater.from(this).inflate(
                    R.layout.card_footer, tenpoListView, false));
        }
        tenpoListView.setAdapter(tenpoAdapter);

        tenpoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                UserData country = users.get(pos);
            }
        });
    }

    public class MyTenpoAsync extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();

            HttpGet request = new HttpGet(tenpoUrl);
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
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture = jsonObject.getString(TAG_PICTURE);
                        String movie = jsonObject.getString(TAG_MOVIE);
                        String restname = jsonObject.getString(TAG_RESTNAME);
                        String locality = jsonObject.getString(TAG_LOCALITY);
                        String review = jsonObject.getString(TAG_REVIEW);
                        String goodnum = jsonObject.getString(TAG_GOODNUM);
                        String comment_num = jsonObject.getString(TAG_COMMENT_NUM);
                        String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                        String star_evaluation = jsonObject.getString(TAG_STAR_EVALUATION);

                        UserData user = new UserData();

                        user.setPost_id(post_id);
                        user.setMovie(movie);
                        user.setPicture(picture);
                        user.setUser_name(user_name);
                        user.setRest_name(restname);
                        user.setLocality(locality);
                        user.setReview(review);
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
                tenpoListView.invalidateViews();
                tenpoAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(TenpoActivity.this, "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
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

    private static class ViewHolder {
        VideoView movie;
        ImageView picture;
        TextView user_name;
        TextView restname;
        TextView post_id;
        TextView locality;
        TextView review;
        TextView goodnum;
        TextView comment_num;
        TextView thumbnail;
        TextView star_evaluation;

        public ViewHolder(View view) {
            this.movie = (VideoView) view.findViewById(R.id.movie);
            this.picture = (ImageView) view.findViewById(R.id.picture);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.post_id = (TextView) view.findViewById(R.id.post_id);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.review = (TextView) view.findViewById(R.id.review);
            this.goodnum = (TextView) view.findViewById(R.id.goodnum);
            this.comment_num = (TextView) view.findViewById(R.id.comment_num);
            this.thumbnail = (TextView) view.findViewById(R.id.thumbnail);
            this.star_evaluation = (TextView) view.findViewById(R.id.star_evaluation);
        }
    }

    public class TenpoAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        int mAnimatedPosition = ListView.INVALID_POSITION;

        public TenpoAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.tenpolist, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            UserData user = this.getItem(position);

            viewHolder.post_id.setText(user.getPost_id());
            viewHolder.user_name.setText(user.getUser_name());
            viewHolder.restname.setText(user.getRest_name());
            viewHolder.locality.setText(user.getLocality());
            viewHolder.review.setText(user.getReview());
            viewHolder.goodnum.setText(user.getgoodnum());
            viewHolder.comment_num.setText(user.getComment_num());
            viewHolder.thumbnail.setText(user.getThumbnail());
            viewHolder.star_evaluation.setText(user.getStar_evaluation());
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

            return convertView;
        }
    }
}

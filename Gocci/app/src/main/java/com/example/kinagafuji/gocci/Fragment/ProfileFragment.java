package com.example.kinagafuji.gocci.Fragment;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.UserData;
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

public class ProfileFragment extends BaseFragment {

    private ListView proflist;

    private ViewHolder viewHolder;

    private static boolean isMov = false;

    private static String profUrl = "https://codelecture.com/gocci/mypage.php";
    private static String Dataurl = "https://codelecture.com/gocci/signup.php";

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

    private String data;

    private CustomProgressDialog dialog;

    private ArrayList<UserData> profuser = new ArrayList<UserData>();

    private int mCardLayoutIndex = 0;

    private ProfAdapter profAdapter;

    private String name;
    private String location;
    private String pictureUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView post_name = (TextView) rootView.findViewById(R.id.post_name);
        ImageView post_Imageurl = (ImageView) rootView.findViewById(R.id.post_Imageurl);
        TextView post_location = (TextView) rootView.findViewById(R.id.post_location);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        name = pref.getString("name", "");
        pictureUrl = pref.getString("pictureUrl", "");
        location = pref.getString("location", "");

        post_name.setText(name);
        post_location.setText(location);

        Picasso.with(getActivity())
                .load(pictureUrl)
                .resize(50, 50)
                .placeholder(R.drawable.ic_userpicture)
                .centerCrop()
                .transform(new RoundedTransformation())
                .into(post_Imageurl);

        proflist = (ListView) rootView.findViewById(R.id.proflist);

        new ProfTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        dialog = new CustomProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.show();

        profAdapter = new ProfAdapter(getActivity(), 0, profuser);

        proflist.setDivider(null);
        // スクロールバーを表示しない
        proflist.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        proflist.setSelector(android.R.color.transparent);

        // 最後の余白分のビューを追加
        if (mCardLayoutIndex > 0) {
            proflist.addFooterView(LayoutInflater.from(getActivity()).inflate(
                    R.layout.card_footer, proflist, false));
        }
        proflist.setAdapter(profAdapter);

        return rootView;
    }

    public class ProfTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            HttpClient client1 = new DefaultHttpClient();

            HttpPost method = new HttpPost(Dataurl);

            ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
            contents.add(new BasicNameValuePair("user_name", name));
            contents.add(new BasicNameValuePair("picture", pictureUrl));
            Log.d("読み取り", name + "と" + pictureUrl);

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
                        user.setRest_name(restname);
                        user.setgoodnum(goodnum);
                        user.setComment_num(comment_num);
                        user.setThumbnail(thumbnail);
                        user.setStar_evaluation(star_evaluation);

                        profuser.add(user);

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
                proflist.invalidateViews();
                profAdapter.notifyDataSetChanged();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    private static class ViewHolder {
        VideoView movie;
        ImageView picture;
        TextView post_id;
        TextView user_id;
        TextView user_name;
        Button restnamebutton;

        public ViewHolder(View view) {
            this.movie = (VideoView) view.findViewById(R.id.movieview);
            this.picture = (ImageView) view.findViewById(R.id.pictureView);
            this.post_id = (TextView) view.findViewById(R.id.post_id);
            this.user_id = (TextView) view.findViewById(R.id.user_id);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.restnamebutton = (Button) view.findViewById(R.id.restnamebutton);
        }
    }

    public class ProfAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        int mAnimatedPosition = ListView.INVALID_POSITION;

        public ProfAdapter(Context context, int viewResourceId, ArrayList<UserData> profuser) {
            super(context, viewResourceId, profuser);
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

            viewHolder.restnamebutton.setText(user.getRest_name());
            viewHolder.restnamebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), TenpoActivity.class);
                    UserData country = profuser.get(position);
                    intent.putExtra("restname", country.getRest_name());
                    intent.putExtra("locality", country.getLocality());

                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}

package com.example.kinagafuji.gocci.View;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.IntentVineCamera;
import com.example.kinagafuji.gocci.Activity.UserProfActivity;
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
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CommentView extends LinearLayout {

    private ListView mCommentList;
    private EditText mCommenttext;
    private ImageButton mCommentButton;

    private CustomProgressDialog mCommentProgress;

    private CommentAdapter mCommentAdapter;
    private ArrayList<UserData> mCommentusers = new ArrayList<UserData>();

    private String mEncodePost_id;

    private static final String sPostCommentUrl = "http://api-gocci.jp/comment/";
    private static final String sDataurl = "http://api-gocci.jp/login/";

    private String mCommentJsonUrl;

    private String mCommentString;

    private static final String TAG_COMMENT = "comment";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE_URL = "picture";

    private String mName;
    private String mPictureImageUrl;
    private String mPost_id;

    public CommentView(final Context context, final String name, final String pictureImageUrl, String post_id) {
        super(context);
        mName = name;
        mPictureImageUrl = pictureImageUrl;
        mPost_id = post_id;

        try {
            mEncodePost_id = URLEncoder.encode(mPost_id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mCommentJsonUrl = "http://api-gocci.jp/comment_json/?post_id=" + mEncodePost_id;

        new CommentAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCommentJsonUrl);
        mCommentProgress = new CustomProgressDialog(getContext());
        mCommentProgress.setCancelable(false);
        mCommentProgress.show();

        View inflateView = LayoutInflater.from(context).inflate(R.layout.comment_layout, this);

        mCommentList = (ListView) inflateView.findViewById(R.id.commentList);
        mCommenttext = (EditText) inflateView.findViewById(R.id.commentText);
        final InputMethodManager inputMethodManager =  (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mCommentButton = (ImageButton) inflateView.findViewById(R.id.commentButton);

        // スクロールバーを表示しない
        mCommentList.setVerticalScrollBarEnabled(false);

        mCommentAdapter = new CommentAdapter(context, 0, mCommentusers);

        mCommentList.setAdapter(mCommentAdapter);

        mCommenttext.setOnKeyListener(new View.OnKeyListener() {
            //コールバックとしてonKey()メソッドを定義
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    inputMethodManager.hideSoftInputFromWindow(mCommenttext.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    mCommentString = String.valueOf(mCommenttext.getText());
                    Log.e("コメントの中身", mCommentString);
                    return true;
                }
                return false;
            }
        });

        mCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentString != null) {
                    new CommentPostTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCommentString);
                    mCommenttext.setText(null);
                } else {
                    Toast.makeText(context,"コメントを入力してください",Toast.LENGTH_SHORT).show();
                }

            }
        });

        mCommentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserData country = mCommentusers.get(position);

                Intent intent = new Intent(context.getApplicationContext(), UserProfActivity.class);
                intent.putExtra("username", country.getUser_name());
                intent.putExtra("name", mName);
                intent.putExtra("pictureImageUrl", mPictureImageUrl);
                context.startActivity(intent);
            }
        });


    }

    //xmlからの生成用
    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public class CommentAsyncTask extends AsyncTask<String, String, Integer> {

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

            int status = httpResponse.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {
                String commentdata = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    commentdata = outputStream.toString(); // JSONデータ
                    Log.d("data", commentdata);
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(commentdata);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String comment = jsonObject.getString(TAG_COMMENT);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture_url = jsonObject.getString(TAG_PICTURE_URL);

                        UserData user = new UserData();

                        user.setComment(comment);
                        user.setUser_name(user_name);
                        user.setPicture(picture_url);

                        mCommentusers.add(user);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("えらー", String.valueOf(e));
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

            if (result != null && result == HttpStatus.SC_OK) {
                //ListViewの最読み込み
                mCommentAdapter.notifyDataSetChanged();
                mCommentList.invalidateViews();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getContext().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            mCommentProgress.dismiss();
        }
    }

    public static class CommentHolder {
        TextView comment;
        TextView user_name;
        ImageView pisture_url;

        public CommentHolder(View view) {
            this.comment = (TextView)view.findViewById(R.id.usercomment);
            this.user_name = (TextView)view.findViewById(R.id.user_name);
            this.pisture_url = (ImageView)view.findViewById(R.id.commentUserImage);

        }
    }

    public class CommentAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private CommentHolder mCommentHolder;

        public CommentAdapter(Context context, int viewResourceId, ArrayList<UserData> mCommentusers) {
            super(context, viewResourceId, mCommentusers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.comment_cell, null);
                mCommentHolder = new CommentHolder(convertView);
                convertView.setTag(mCommentHolder);
            } else {
                mCommentHolder = (CommentHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            mCommentHolder.comment.setText(user.getComment());
            mCommentHolder.user_name.setText(user.getUser_name());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .resize(50, 50)
                    .placeholder(R.drawable.ic_userpicture)
                    .centerCrop()
                    .transform(new RoundedTransformation())
                    .into(mCommentHolder.pisture_url);

            return convertView;
        }
    }

    public class CommentPostTask extends AsyncTask<String, String, Integer> {
        int status;
        int status2;

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
                status = res.getStatusLine().getStatusCode();
                Log.d("TAGだよ", "反応");
                HttpEntity entity = res.getEntity();
                body = EntityUtils.toString(entity, "UTF-8");
                Log.d("bodyの中身だよ", body);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (HttpStatus.SC_OK == status) {

                HttpPost commentmethod = new HttpPost(sPostCommentUrl);

                ArrayList<NameValuePair> commentcontents = new ArrayList<NameValuePair>();
                commentcontents.add(new BasicNameValuePair("post_id", mPost_id));
                commentcontents.add(new BasicNameValuePair("comment", param));

                Log.d("読み取り", mPost_id + "/" + param);

                String commentbody = null;
                try {
                    commentmethod.setEntity(new UrlEncodedFormEntity(commentcontents, "utf-8"));
                    HttpResponse commentres = client.execute(commentmethod);
                    status2 = commentres.getStatusLine().getStatusCode();
                    Log.d("TAGだよ", "反応");
                    HttpEntity commententity = commentres.getEntity();
                    commentbody = EntityUtils.toString(commententity, "UTF-8");
                    Log.d("bodyの中身だよ", commentbody);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return status2;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null && result == HttpStatus.SC_OK) {
                new CommentAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCommentJsonUrl);
                mCommentProgress = new CustomProgressDialog(getContext());
                mCommentProgress.setCancelable(false);
                mCommentProgress.show();
            } else {
                Toast.makeText(getContext(),"コメントを送信できませんでした。",Toast.LENGTH_SHORT).show();

            }

        }

    }

}

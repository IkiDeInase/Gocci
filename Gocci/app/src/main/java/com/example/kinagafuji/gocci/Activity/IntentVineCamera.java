package com.example.kinagafuji.gocci.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.DataAsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class IntentVineCamera extends Activity {

    private static final int ACTION_TAKE_VIDEO = 1;
    private Uri mImageUri;

    private CustomProgressDialog mPostProgress;

    private static final String sSignupUrl = "http://api-gocci.jp/api/public/login/";
    private static final String sMovieurl = "http://api-gocci.jp/api/public/movie/";
    private static final String sPostUrl = "http://api-gocci.jp/api/public/post_restname/";
    private static final String sReviewUrl = "http://api-gocci.jp/api/public/submit/";

    private String mRestname;

    private String mName;
    private String mPictureImageUrl;

    private String mReviewText;

    private int status;
    private int status1;
    private int status2;
    private int status3;

    private String cursorUrl;

    private ArrayList<NameValuePair> logininfo;
    private ArrayList<NameValuePair> restinfo;
    private MultipartEntity fileEntity;
    private ArrayList<NameValuePair> reviewinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_vine_camera);

        String fileName = String.valueOf(System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        ContentResolver contentResolver = getContentResolver();
        mImageUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = getIntent();
        mRestname = intent.getStringExtra("restname");
        mName = intent.getStringExtra("name");
        mPictureImageUrl = intent.getStringExtra("pictureImageUrl");

        Log.d("ゲットしたデータ", mRestname + "/" + mName + "/" + mPictureImageUrl);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 6);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {

                    final EditText commentText = (EditText)findViewById(R.id.commentText);
                    final InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                    //EditTextにリスナーをセット
                    commentText.setOnKeyListener(new View.OnKeyListener() {
                        //コールバックとしてonKey()メソッドを定義
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                                inputMethodManager.hideSoftInputFromWindow(commentText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                SpannableStringBuilder sb = (SpannableStringBuilder)commentText.getText();
                                mReviewText = sb.toString();
                                Log.e("レビューの中身", mReviewText);
                                return true;
                            }
                            return false;
                        }
                    });


                    final VideoView video = (VideoView) findViewById(R.id.video);
                    video.setVideoURI(mImageUri);
                    Log.e("URL", String.valueOf(mImageUri));
                    video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            video.start();
                            mp.setLooping(true);
                        }
                    });

                    getRealPathFromURI(IntentVineCamera.this, mImageUri);

                    ImageButton facebookshare = (ImageButton) findViewById(R.id.facebookShare);
                    ImageButton twitershare = (ImageButton) findViewById(R.id.twitterShare);
                    ImageButton toukoushare = (ImageButton) findViewById(R.id.toukoushare);
                    toukoushare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UploadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            mPostProgress = new CustomProgressDialog(IntentVineCamera.this);
                            mPostProgress.setCancelable(false);
                            mPostProgress.show();

                        }
                    });

                    //getContentResolver().delete(mImageUri, null, null);
                    //mImageUri = null;
                }
                break;
            }
        }
    }

    public void getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            cursorUrl = cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        logininfo = new ArrayList<NameValuePair>();
        logininfo.add(new BasicNameValuePair("user_name", mName));
        logininfo.add(new BasicNameValuePair("picture", mPictureImageUrl));

        restinfo = new ArrayList<NameValuePair>();
        restinfo.add(new BasicNameValuePair("restname", mRestname));

        File file = new File(cursorUrl);
        FileBody fileBody = new FileBody(file);
        fileEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        fileEntity.addPart("movie", fileBody);

        reviewinfo = new ArrayList<NameValuePair>();
        reviewinfo.add(new BasicNameValuePair("val", mReviewText));
    }

    public class UploadAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost loginpost = new HttpPost(sSignupUrl);
            HttpPost restpost = new HttpPost(sPostUrl);
            HttpPost moviepost = new HttpPost(sMovieurl);
            HttpPost reviewpost = new HttpPost(sReviewUrl);


            //ログイン処理
            HttpResponse loginres = null;
            try {
                loginpost.setEntity(new UrlEncodedFormEntity(logininfo, "utf-8"));
                loginres = httpClient.execute(loginpost);
                status = loginres.getStatusLine().getStatusCode();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("失敗です","サインアップでエラー");
            }

            //店舗名ポスト処理
            if (HttpStatus.SC_OK == status) {

                HttpResponse restres = null;
                try {
                    restpost.setEntity(new UrlEncodedFormEntity(restinfo, "utf-8"));
                    restres = httpClient.execute(restpost);
                    status1 = restres.getStatusLine().getStatusCode();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("失敗です","店舗名ポストでエラー");
                }
            } else {
                Log.e("失敗です","サインアップでエラー");
            }

            if (HttpStatus.SC_OK == status1) {

                HttpResponse movieres = null;
                try {
                    moviepost.setEntity(fileEntity);
                    movieres = httpClient.execute(moviepost);
                    status2 = movieres.getStatusLine().getStatusCode();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("失敗です","動画ポストでエラー");
                }
            } else {
                Log.e("失敗です","店舗名ポストでエラー");
            }

            if (HttpStatus.SC_OK == status2) {

                HttpResponse reviewres = null;
                try {
                    reviewpost.setEntity(new UrlEncodedFormEntity(reviewinfo, "utf-8"));
                    reviewres = httpClient.execute(restpost);
                    status3 = reviewres.getStatusLine().getStatusCode();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("失敗です","レビューポストでエラー");
                }
            } else {
                Log.e("失敗です","動画ポストでエラー");
            }

            return status3;

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result != null && result == 200) {
                Toast.makeText(getApplicationContext(), "完了しました。", Toast.LENGTH_LONG).show();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(IntentVineCamera.this.getApplicationContext(), "失敗しました。", Toast.LENGTH_SHORT).show();
            }
            mPostProgress.dismiss();

            Intent intent = new Intent(IntentVineCamera.this, SlidingTabActivity.class);
            startActivity(intent);

        }

    }

}

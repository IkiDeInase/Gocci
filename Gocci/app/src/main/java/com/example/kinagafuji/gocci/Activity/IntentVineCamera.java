package com.example.kinagafuji.gocci.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.R;

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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class IntentVineCamera extends Activity {

    private static final int ACTION_TAKE_VIDEO = 1;
    private Uri mImageUri;

    private static final String sSignupUrl = "http://api-gocci.jp/api/public/login/";
    private static final String sMovieurl = "http://api-gocci.jp/api/public/movie/";
    private static final String sPostUrl = "http://api-gocci.jp/api/public/post_restname/";

    private String mRestname;

    private String mName;
    private String mPictureImageUrl;

    private String mPath;

    private int status;
    private int status1;
    private int status2;

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
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,6);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    final VideoView video = (VideoView)findViewById(R.id.video);
                    video.setVideoURI(mImageUri);
                    Log.e("URL", String.valueOf(mImageUri));
                    Log.e("Intent data", String.valueOf(data.getData()));
                    video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            video.start();
                            mp.setLooping(true);
                        }
                    });

                    ImageButton facebookshare = (ImageButton)findViewById(R.id.facebookShare);
                    ImageButton twitershare = (ImageButton)findViewById(R.id.twitterShare);
                    ImageButton toukoushare = (ImageButton)findViewById(R.id.toukoushare);
                    toukoushare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UploadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(mImageUri));
                        }
                    });

                    //getContentResolver().delete(mImageUri, null, null);
                    //mImageUri = null;
                }
                break;
            }
        }
    }

    public class UploadAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String path = params[0];

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost loginpost = new HttpPost(sSignupUrl);

            ArrayList<NameValuePair> logininfo = new ArrayList<NameValuePair>();
            logininfo.add(new BasicNameValuePair("user_name", mName));
            logininfo.add(new BasicNameValuePair("picture", mPictureImageUrl));
            Log.d("読み取り", mName + "と" + mPictureImageUrl);

            HttpResponse loginres = null;
            String body = null;
            try {
                loginpost.setEntity(new UrlEncodedFormEntity(logininfo, "utf-8"));
                loginres = httpClient.execute(loginpost);
                HttpEntity loginentity = loginres.getEntity();
                body = EntityUtils.toString(loginentity, "UTF-8");
                Log.d("bodyの中身だよ", body);
            } catch (Exception e) {
                e.printStackTrace();
            }

            status = loginres.getStatusLine().getStatusCode();

            if (HttpStatus.SC_OK == status) {

                HttpPost restpost = new HttpPost(sPostUrl);
                List<NameValuePair> restinfo = new ArrayList<NameValuePair>();
                restinfo.add(new BasicNameValuePair("restname", mRestname));

                HttpResponse restres = null;
                String restbody = null;
                try {
                    restpost.setEntity(new UrlEncodedFormEntity(restinfo, "utf-8"));
                    restres = httpClient.execute(restpost);
                    HttpEntity restEntity = restres.getEntity();
                    restbody = EntityUtils.toString(restEntity, "UTF-8");
                    Log.d("restbodyの中身だよ", restbody);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                status1 = restres.getStatusLine().getStatusCode();
            } else {
                Log.e("失敗しました", "第二関門");
            }

            if (HttpStatus.SC_OK == status1) {

                HttpResponse movieres = null;
                String moviebody = null;
                try {
                    File file = new File(path);
                    FileBody fileBody = new FileBody(file);

                    HttpPost moviepost = new HttpPost(sMovieurl);

                    MultipartEntity fileEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    fileEntity.addPart("movie", fileBody);
                    moviepost.setEntity(fileEntity);

                    movieres = httpClient.execute(moviepost);
                    HttpEntity movieEntity = movieres.getEntity();
                    moviebody = EntityUtils.toString(movieEntity, "UTF-8");
                    Log.d("moviebodyの中身だよ", moviebody);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                status2 = movieres.getStatusLine().getStatusCode();
            } else {
                Log.e("失敗しました", "第三関門");
            }

            return status2;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result != null && result == HttpStatus.SC_OK) {
                Toast.makeText(getApplicationContext(), "完了しました。", Toast.LENGTH_LONG).show();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(IntentVineCamera.this.getApplicationContext(), "失敗しました。", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(IntentVineCamera.this, SlidingTabActivity.class);
            startActivity(intent);

        }

    }

}

package com.example.kinagafuji.gocci.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Application_Gocci;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;

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

import java.io.File;
import java.util.ArrayList;

public class IntentVineCamera extends Activity {

    private Application_Gocci application_gocci;

    private static final int ACTION_TAKE_VIDEO = 1;
    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sMovieurl = "http://api-gocci.jp/movie/";
    private static final String sPostUrl = "http://api-gocci.jp/post_restname/";
    private static final String sRatingUrl = "http://api-gocci.jp/submit/";
    private Uri mImageUri;
    private CustomProgressDialog mPostProgress;
    private String mRestname;

    private String mName;
    private String mPictureImageUrl;

    private String mRatingNumber;

    private int status;
    private int status1;
    private int status2;
    private int status3;

    private String cursorUrl;

    private ArrayList<NameValuePair> logininfo;
    private ArrayList<NameValuePair> restinfo;
    private MultipartEntity fileEntity;
    private ArrayList<NameValuePair> rateinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_vine_camera);
        application_gocci = (Application_Gocci) this.getApplication();

        Intent intent = getIntent();
        mRestname = intent.getStringExtra("restname");
        mName = intent.getStringExtra("name");
        mPictureImageUrl = intent.getStringExtra("pictureImageUrl");

        Log.d("ゲットしたデータ", mRestname + "/" + mName + "/" + mPictureImageUrl);

        String fileName = String.valueOf(System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        ContentResolver contentResolver = getContentResolver();
        mImageUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 6);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {

                    getRealPathFromURI(IntentVineCamera.this, mImageUri);

                    final RatingBar videoRating = (RatingBar) findViewById(R.id.videorating);
                    videoRating.setStepSize(1);

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


                    ImageButton facebookshare = (ImageButton) findViewById(R.id.facebookShare);
                    ImageButton twitershare = (ImageButton) findViewById(R.id.twitterShare);
                    ImageButton toukoushare = (ImageButton) findViewById(R.id.toukoushare);

                    toukoushare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRatingNumber = String.valueOf((int) videoRating.getRating());
                            Log.e("星数", mRatingNumber);
                            new UploadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            Log.e("星送信","開始");
                            mPostProgress = new CustomProgressDialog(IntentVineCamera.this);
                            mPostProgress.setCancelable(false);
                            mPostProgress.show();

                        }
                    });

                    //getContentResolver().delete(mImageUri, null, null);
                    //mImageUri = null;
                } else {
                    Toast.makeText(IntentVineCamera.this,"撮影失敗です",Toast.LENGTH_SHORT).show();
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

        new PostMovieAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,cursorUrl);
        Log.e("動画送信","開始");
    }

    public class PostMovieAsyncTask extends AsyncTask<String,String,Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            HttpClient client = application_gocci.getHttpClient();

            HttpPost restpost = new HttpPost(sPostUrl);

            restinfo = new ArrayList<NameValuePair>();
            restinfo.add(new BasicNameValuePair("restname", mRestname));

            //店舗名ポスト処理
            HttpResponse restres = null;
            try {
                restpost.setEntity(new UrlEncodedFormEntity(restinfo, "utf-8"));
                restres = client.execute(restpost);
                status1 = restres.getStatusLine().getStatusCode();
                restres.getEntity().consumeContent();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("失敗です", "店舗名ポストでエラー");
            }

            if (HttpStatus.SC_OK == status1) {

                File file = new File(url);
                FileBody fileBody = new FileBody(file);
                fileEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                fileEntity.addPart("movie", fileBody);

                HttpPost moviepost = new HttpPost(sMovieurl);

                HttpResponse movieres = null;
                try {
                    moviepost.setEntity(fileEntity);
                    movieres = client.execute(moviepost);
                    status2 = movieres.getStatusLine().getStatusCode();
                    movieres.getEntity().consumeContent();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("失敗です", "動画ポストでエラー");
                }
            }

            Log.e("動画送信","終了");
            return status2;
        }
    }

    public class UploadAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            HttpClient client = application_gocci.getHttpClient();

            HttpPost ratingpost = new HttpPost(sRatingUrl);

                HttpResponse ratingres = null;
                try {
                    ratingpost.setEntity(new UrlEncodedFormEntity(rateinfo, "utf-8"));
                    ratingres = client.execute(ratingpost);
                    status3 = ratingres.getStatusLine().getStatusCode();
                    ratingres.getEntity().consumeContent();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("失敗です", "星ポストでエラー");
                }

            return status3;

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result != null && result == HttpStatus.SC_OK) {
                Toast.makeText(IntentVineCamera.this, "完了しました。", Toast.LENGTH_LONG).show();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(IntentVineCamera.this, "失敗しました。", Toast.LENGTH_SHORT).show();
            }
            mPostProgress.dismiss();

            Intent intent = new Intent(IntentVineCamera.this, SlidingTabActivity.class);
            startActivity(intent);

        }

    }

}

package com.example.kinagafuji.gocci.Activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.Base.BaseActivity;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

public class IntentVineCamera extends BaseActivity {

    private static final int ACTION_TAKE_VIDEO = 1;

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sMovieurl = "http://api-gocci.jp/movie/";
    private static final String sPostUrl = "http://api-gocci.jp/post_restname/";
    private static final String sRatingUrl = "http://api-gocci.jp/submit/";

    private Uri mImageUri;

    private CustomProgressDialog mPostProgress;

    private String mRestname;
    private String mName;
    private String mRatingNumber;
    private String cursorUrl;

    private AsyncHttpClient httpClient;
    private RequestParams loginParam;
    private RequestParams restParam;
    private RequestParams rateParam;

    private ImageButton facebookshare;
    private ImageButton twittershare;
    private ImageButton toukoushare;
    private RatingBar videoRating;
    private VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_vine_camera);

        Intent intent = getIntent();
        mRestname = intent.getStringExtra("restname");
        mName = intent.getStringExtra("name");

        httpClient = new AsyncHttpClient();

        loginParam = new RequestParams("user_name", mName);
        restParam = new RequestParams("restname", mRestname);

        facebookshare = (ImageButton) findViewById(R.id.facebookShare);
        twittershare = (ImageButton) findViewById(R.id.twitterShare);
        toukoushare = (ImageButton) findViewById(R.id.toukoushare);
        videoRating = (RatingBar) findViewById(R.id.videorating);
        videoRating.setStepSize(1);
        video = (VideoView) findViewById(R.id.video);

        String fileName = String.valueOf(System.currentTimeMillis());

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
        ContentResolver contentResolver = getContentResolver();
        mImageUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        getRealPathFromURI(IntentVineCamera.this, mImageUri);

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

                    video.setVideoPath(cursorUrl);
                    video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            video.start();
                            mp.setLooping(true);
                        }
                    });

                    toukoushare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRatingNumber = String.valueOf((int) videoRating.getRating());
                            rateParam = new RequestParams("star_evaluation", mRatingNumber);
                            postSignupAsync(IntentVineCamera.this);
                        }
                    });

                    //getContentResolver().delete(mImageUri, null, null);
                    //mImageUri = null;
                } else {
                    Toast.makeText(IntentVineCamera.this, "撮影失敗です", Toast.LENGTH_SHORT).show();
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
    }

    private void postSignupAsync(final Context context) {
        httpClient.post(context, sSignupUrl, loginParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mPostProgress = new CustomProgressDialog(IntentVineCamera.this);
                mPostProgress.setCancelable(false);
                mPostProgress.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                postRestAsync(context);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mPostProgress.dismiss();
                Toast.makeText(IntentVineCamera.this, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postRestAsync(final Context context) {
        httpClient.post(context, sPostUrl, restParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                postMovieAsync(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mPostProgress.dismiss();
                Toast.makeText(IntentVineCamera.this, "店名送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postMovieAsync(final Context context) {
        File myFile = new File(cursorUrl);
        RequestParams movieParam = new RequestParams();
        try {
            movieParam.put("movie", myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        httpClient.post(context, sMovieurl, movieParam, new FileAsyncHttpResponseHandler(IntentVineCamera.this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                mPostProgress.dismiss();
                Toast.makeText(IntentVineCamera.this, "動画送信に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                postStarAsync(context);
            }
        });
    }

    private void postStarAsync(final Context context) {
        httpClient.post(context, sRatingUrl, rateParam, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(IntentVineCamera.this, "投稿が完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(IntentVineCamera.this, "星送信に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
        public void onFinish() {
                mPostProgress.dismiss();
                Intent intent = new Intent(IntentVineCamera.this, SlidingTabActivity.class);
                startActivity(intent);
            }
        });
    }
}

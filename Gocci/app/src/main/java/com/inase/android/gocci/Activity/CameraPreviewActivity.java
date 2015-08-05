package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.andexert.library.RippleView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.common.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class CameraPreviewActivity extends AppCompatActivity {

    private int mRest_id;
    private int mCategory_id;
    private int mTag_id;
    private int mCheer_flag = 0;
    private String mRestname;
    private String mVideoUrl;
    private String mAwsPostName;
    private String mValue;
    private String mMemo;
    private boolean mIsnewRestname;
    private double mLatitude;
    private double mLongitude;

    private File mVideoFile;

    private ProgressWheel mPostProgress;

    private SquareVideoView videoView;

    private MaterialBetterSpinner restname_spinner;
    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner mood_spinner;
    private MaterialEditText edit_value;
    private MaterialEditText edit_comment;

    private CheckBox cheerCheck;
    private ImageButton twitterButton;
    private ImageButton facebookButton;
    private ImageButton instagramButton;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private boolean isUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        isUpload = false;

        Intent intent = getIntent();
        mRest_id = intent.getIntExtra("rest_id", 1);
        mVideoUrl = intent.getStringExtra("video_url");
        mAwsPostName = intent.getStringExtra("aws") + "_" + SavedData.getServerUserId(this);
        mCategory_id = intent.getIntExtra("category_id", 1);
        mTag_id = intent.getIntExtra("tag_id", 1);
        mMemo = intent.getStringExtra("memo");
        mValue = intent.getStringExtra("value");
        mIsnewRestname = intent.getBooleanExtra("isNewRestname", false);
        mLatitude = intent.getDoubleExtra("lat", 0.0);
        mLongitude = intent.getDoubleExtra("lon", 0.0);

        mVideoFile = new File(mVideoUrl);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(CameraPreviewActivity.this, "シェアが完了しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(CameraPreviewActivity.this, "キャンセルしました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(CameraPreviewActivity.this, "シェアに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });

        Fabric.with(this, new TweetComposer());

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ImageButton addrestButton = (ImageButton) findViewById(R.id.restaddButton);
        addrestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTenpo();
            }
        });

        RelativeLayout addrestView = (RelativeLayout) findViewById(R.id.addRestView);
        if (mIsnewRestname) {
            addrestView.setVisibility(View.GONE);
        }

        videoView = (SquareVideoView) findViewById(R.id.previewVideo);
        mPostProgress = (ProgressWheel) findViewById(R.id.cameraprogress_wheel);

        restname_spinner = (MaterialBetterSpinner) findViewById(R.id.restname_spinner);
        category_spinner = (MaterialBetterSpinner) findViewById(R.id.category_spinner);
        mood_spinner = (MaterialBetterSpinner) findViewById(R.id.mood_spinner);
        edit_value = (MaterialEditText) findViewById(R.id.edit_value);
        edit_comment = (MaterialEditText) findViewById(R.id.edit_comment);
        cheerCheck = (CheckBox) findViewById(R.id.cheerCheck);
        twitterButton = (ImageButton) findViewById(R.id.twitterButton);
        facebookButton = (ImageButton) findViewById(R.id.facebookButton);
        instagramButton = (ImageButton) findViewById(R.id.instagramButton);
        RippleView toukou_ripple = (RippleView) findViewById(R.id.toukou_button_Ripple);

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        String[] MOOD = getResources().getStringArray(R.array.list_mood);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        category_spinner.setAdapter(categoryAdapter);
        category_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategory_id = position + 2;
            }
        });

        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, MOOD);
        mood_spinner.setAdapter(moodAdapter);
        mood_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTag_id = position + 2;
            }
        });

        ArrayAdapter<String> restAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, GocciCameraActivity.restname);
        restname_spinner.setAdapter(restAdapter);
        restname_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRest_id = GocciCameraActivity.rest_id.get(position);
            }
        });

        restname_spinner.setText(mRest_id == 1 ? "" : GocciCameraActivity.restname.get(GocciCameraActivity.rest_id.indexOf(mRest_id)));
        category_spinner.setText(mCategory_id == 1 ? "" : CATEGORY[mCategory_id - 2]);
        mood_spinner.setText(mTag_id == 1 ? "" : MOOD[mTag_id - 2]);
        edit_value.setText(mValue);
        edit_comment.setText(mMemo);

        videoView.setVideoPath(mVideoUrl);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri bmpUri = Util.getUri(mVideoUrl);
                if (bmpUri != null) {
                    if (mRestname.equals("")) {
                        Toast.makeText(CameraPreviewActivity.this, "店名を入力してください", Toast.LENGTH_SHORT).show();
                    } else {
                        TweetComposer.Builder builder = new TweetComposer.Builder(CameraPreviewActivity.this)
                                .text("#" + mRestname.replaceAll("\\s+", "") + " #Gocci #FoodPorn")
                                .image(bmpUri);

                        builder.show();
                    }
                } else {
                    // ...sharing failed, handle error
                    Toast.makeText(CameraPreviewActivity.this, "twitterシェアに失敗しました", Toast.LENGTH_SHORT).show();
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.fromFile(mVideoFile);
                if (ShareDialog.canShow(ShareVideoContent.class)) {
                    ShareVideo video = new ShareVideo.Builder()
                            .setLocalUrl(uri)
                            .build();
                    ShareVideoContent content = new ShareVideoContent.Builder()
                            .setVideo(video)
                            .build();
                    shareDialog.show(content);
                } else {
                    // ...sharing failed, handle error
                    Toast.makeText(CameraPreviewActivity.this, "facebookシェアに失敗しました", Toast.LENGTH_SHORT).show();
                }
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRestname.equals("")) {
                    Uri uri = Uri.fromFile(mVideoFile);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    // Set the MIME type
                    share.setType("video/*");
                    // Add the URI and the caption to the Intent.
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setPackage("com.instagram.android");
                    share.putExtra(Intent.EXTRA_TEXT, "#" + mRestname.replaceAll("\\s+", "") + " #Gocci #FoodPorn");
                    // Broadcast the Intent.
                    startActivity(Intent.createChooser(share, "Share to"));
                } else {
                    Toast.makeText(CameraPreviewActivity.this, "店名を入力してください", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toukou_ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRest_id != 1) {
                    if (edit_value.getText().length() != 0) {
                        mValue = edit_value.getText().toString();
                    } else {
                        mValue = "0";
                    }
                    if (edit_comment.getText().length() != 0) {
                        mMemo = edit_comment.getText().toString();
                    } else {
                        mMemo = "none";
                    }
                    if (cheerCheck.isChecked()) {
                        mCheer_flag = 1;
                    }
                    postSignupAsync(CameraPreviewActivity.this);
                } else {
                    Toast.makeText(CameraPreviewActivity.this, "店名は入力してください", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title("確認")
                .content("ここで戻ると、この動画は初期化されますがよろしいですか？")
                .positiveText("戻る")
                .negativeText("いいえ")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        CameraPreviewActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void createTenpo() {
        new MaterialDialog.Builder(CameraPreviewActivity.this)
                .title("店舗追加")
                .content("あなたのいるお店の名前を入力してください。※位置情報は現在の位置を使います。")
                .input("店舗名", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText("送信する")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mRestname = dialog.getInputEditText().getText().toString();

                        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(CameraPreviewActivity.this));
                        Const.asyncHttpClient.get(CameraPreviewActivity.this, Const.getPostRestAddAPI(mRestname, mLatitude, mLongitude), new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                mPostProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(CameraPreviewActivity.this, "通信に失敗しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.e("ジェイソン成功", String.valueOf(response));

                                try {
                                    String message = response.getString("message");

                                    if (message.equals("店舗を追加しました")) {
                                        Toast.makeText(CameraPreviewActivity.this, message, Toast.LENGTH_SHORT).show();
                                        //店名をセット
                                        mIsnewRestname = true;
                                        restname_spinner.setText(mRestname);
                                        mRest_id = response.getInt("rest_id");
                                        restname_spinner.setClickable(false);
                                    } else {
                                        Toast.makeText(CameraPreviewActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFinish() {
                                mPostProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                })
                .show();
    }

    private void postSignupAsync(final Context context) {
        if (!isUpload) {
            mPostProgress.setVisibility(View.VISIBLE);
            TransferObserver transferObserver = Application_Gocci.transferUtility.upload(Const.POST_MOVIE_BUCKET_NAME, mAwsPostName + ".mp4", mVideoFile);
            transferObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        postMovieAsync(context);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }

    }

    private void postMovieAsync(final Context context) {
        isUpload = true;
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.setConnectTimeout(10 * 1000);
        Const.asyncHttpClient.setResponseTimeout(60 * 1000);
        Const.asyncHttpClient.get(context, Const.getPostMovieAPI(mRest_id, mAwsPostName, mCategory_id, mTag_id, mValue, mMemo, mCheer_flag), new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "投稿に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");
                    int code = response.getInt("code");

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    if (code == 200 && message.equals("投稿しました")) {
                        Intent intent = new Intent(context, GocciTimelineActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(context, "投稿に失敗しました", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                mPostProgress.setVisibility(View.GONE);
            }
        });
    }
}

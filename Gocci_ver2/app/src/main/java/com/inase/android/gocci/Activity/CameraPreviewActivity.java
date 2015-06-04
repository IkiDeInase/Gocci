package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.Camera.up18CameraFragment;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class CameraPreviewActivity extends AppCompatActivity {

    private String mRestname;
    private String mVideoUrl;
    private String mValue;
    private String mCategory;
    private String mAtmosphere;
    private String mComment;
    private boolean mIsnewRestname;
    private int mStar_evaluation = 0;
    private double mLatitude;
    private double mLongitude;

    private int mYen;

    private File mVideoFile;

    private AsyncHttpClient httpClient;
    private RequestParams toukouParam;

    private static final String sSignupUrl = "http://api-gocci.jp/login/";
    private static final String sMovieurl = "http://api-gocci.jp/android_movie/";
    private ProgressWheel mPostProgress;

    private SquareVideoView videoView;

    private MaterialBetterSpinner restname_spinner;
    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner mood_spinner;
    private MaterialEditText edit_value;
    private MaterialEditText edit_comment;

    private CheckBox cheerCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        Intent intent = getIntent();
        mRestname = intent.getStringExtra("restname");
        mVideoUrl = intent.getStringExtra("video_url");
        mCategory = intent.getStringExtra("category");
        mAtmosphere = intent.getStringExtra("mood");
        mComment = intent.getStringExtra("comment");
        mValue = intent.getStringExtra("value");
        mIsnewRestname = intent.getBooleanExtra("isNewRestname", false);
        mLatitude = intent.getDoubleExtra("lat", 0.0);
        mLongitude = intent.getDoubleExtra("lon", 0.0);

        mVideoFile = new File(mVideoUrl);

        EventDateRecorder recorder = EventDateRecorder.load(CameraPreviewActivity.this, "use_camera_preview");
        if (!recorder.didRecorded()) {
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(CameraPreviewActivity.this);
            Effectstype effect = Effectstype.SlideBottom;
            dialogBuilder
                    .withTitle("確認画面")
                    .withMessage("タグの確認をして投稿しましょう！※投稿できなくなりますので、この画面から戻らないようにしてください!")
                    .withDuration(500)                                          //def
                    .withEffect(effect)
                    .isCancelableOnTouchOutside(true)
                    .show();
            recorder.record();
        }

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

        toukouParam = new RequestParams();

        try {
            toukouParam.put("movie", mVideoFile);
            Log.e("入れたファイル", String.valueOf(mVideoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("失敗ファイル", String.valueOf(mVideoFile));
        }

        videoView = (SquareVideoView) findViewById(R.id.previewVideo);
        mPostProgress = (ProgressWheel) findViewById(R.id.cameraprogress_wheel);

        restname_spinner = (MaterialBetterSpinner) findViewById(R.id.restname_spinner);
        category_spinner = (MaterialBetterSpinner) findViewById(R.id.category_spinner);
        mood_spinner = (MaterialBetterSpinner) findViewById(R.id.mood_spinner);
        edit_value = (MaterialEditText) findViewById(R.id.edit_value);
        edit_comment = (MaterialEditText) findViewById(R.id.edit_comment);
        cheerCheck = (CheckBox) findViewById(R.id.cheerCheck);
        RippleView toukou_ripple = (RippleView) findViewById(R.id.toukou_button_Ripple);

        String[] CATEGORY = getResources().getStringArray(R.array.list_category);
        String[] MOOD = getResources().getStringArray(R.array.list_mood);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY);
        category_spinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, MOOD);
        mood_spinner.setAdapter(moodAdapter);

        ArrayAdapter<String> restAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, GocciCameraActivity.restname);
        restname_spinner.setAdapter(restAdapter);

        restname_spinner.setText(mRestname);
        category_spinner.setText(mCategory);
        mood_spinner.setText(mAtmosphere);
        edit_value.setText(mValue);
        edit_comment.setText(mComment);

        videoView.setVideoPath(mVideoUrl);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });

        toukou_ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRestname = restname_spinner.getText().toString();

                if (mRestname.length() != 0) {
                    if (category_spinner.getText().length() == 0) {
                        mCategory = "none";
                    } else {
                        mCategory = category_spinner.getText().toString();
                    }
                    if (mood_spinner.getText().length() == 0) {
                        mAtmosphere = "none";
                    } else {
                        mAtmosphere = mood_spinner.getText().toString();
                    }
                    if (edit_value.getText().length() == 0) {
                        mYen = 0;
                    } else {
                        mYen = Integer.parseInt(edit_value.getText().toString());
                    }
                    if (edit_comment.getText().length() == 0) {
                        mComment = "none";
                    } else {
                        mComment = edit_comment.getText().toString();
                    }
                    if (cheerCheck.isChecked()) {
                        mStar_evaluation = 1;
                    }

                    toukouParam.put("restname", mRestname);
                    toukouParam.put("star_evaluation", mStar_evaluation);
                    toukouParam.put("category", mCategory);
                    toukouParam.put("atmosphere", mAtmosphere);
                    toukouParam.put("value", mYen);
                    toukouParam.put("comment", mComment);

                    postSignupAsync(CameraPreviewActivity.this);
                } else {
                    Toast.makeText(CameraPreviewActivity.this, "店名だけは入力してください", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Disable Back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }

        return super.onKeyDown(keyCode, event);
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

                        RequestParams params = new RequestParams();
                        params.put("restname", mRestname);
                        params.put("lat", mLatitude);
                        params.put("lon", mLongitude);

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(CameraPreviewActivity.this, Const.URL_INSERT_REST, params, new JsonHttpResponseHandler() {
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

                                    if (message.equals("店舗追加完了しました")) {
                                        Toast.makeText(CameraPreviewActivity.this, message, Toast.LENGTH_SHORT).show();
                                        //店名をセット
                                        mIsnewRestname = true;
                                        restname_spinner.setText(mRestname);
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
        Log.e("中身は！？！？", String.valueOf(toukouParam));
        httpClient = new AsyncHttpClient();
        httpClient.setConnectTimeout(10 * 1000);
        httpClient.setResponseTimeout(60 * 1000);
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.post(context, sMovieurl, toukouParam, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                mPostProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "投稿に失敗しました", Toast.LENGTH_SHORT).show();
                Log.e("失敗ログ", responseString);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(context, "投稿が完了しました", Toast.LENGTH_SHORT).show();
                Log.e("成功ログ", responseString);

            }

            @Override
            public void onFinish() {
                mPostProgress.setVisibility(View.GONE);
                //成功しても失敗してもホーム画面に戻る。
                Intent intent = new Intent(context, LoginPreferenceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}

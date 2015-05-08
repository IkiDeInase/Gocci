package com.inase.android.gocci.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.coremedia.iso.boxes.Container;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Base.CameraManager;
import com.inase.android.gocci.Base.CircleProgressBar;
import com.inase.android.gocci.Base.RecorderManager;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.MySurfaceView;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class CameraActivity extends Activity implements ViewPager.OnPageChangeListener {

    private RecorderManager recorderManager = null;
    private CameraManager cameraManager;

    private Runnable progressRunnable = null;
    private Handler handler = null;
    private ProgressWheel cameraProgress;

    private CircleProgressBar progress;

    private String mRest_name;
    private String mFinalVideoUrl;
    private String mValue;
    private String mCategory;
    private String mAtmosphere;
    private String mComment;
    private boolean mIsnewRestname = false;

    private boolean onScroll = false;

    private String timeStamp;

    private boolean isPlaying = false;
    private boolean isFirst = true;

    private ViewPagerItemAdapter adapter;

    public static String[] restname = new String[30];
    private ImageButton toukouButton;

    private double latitude;
    private double longitude;
    private String mSearch_mapUrl;

    private static final String TAG_REST_NAME = "restname";

    private MaterialBetterSpinner restname_spinner;
    private MaterialBetterSpinner category_spinner;
    private MaterialBetterSpinner mood_spinner;
    private MaterialEditText edit_value;
    private MaterialEditText edit_comment;
    private ImageButton restaddButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        isFirst = true;

        getLocation(this);

        for (int i = 0; i < 30; i++) {
            restname[i] = "";
        }

        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(CameraActivity.this);
        Effectstype effect = Effectstype.SlideBottom;
        dialogBuilder
                .withTitle("Gocciカメラ")
                .withMessage("このカメラはボタンをタップしている間だけ再生されます。技術的な問題でうまく動作しない場合があります。")
                .withDuration(500)                                          //def
                .withEffect(effect)
                .isCancelableOnTouchOutside(true)
                .show();

        cameraProgress = (ProgressWheel) findViewById(R.id.cameraprogress_wheel);
        MySurfaceView videoSurface = (MySurfaceView) findViewById(R.id.cameraView);
        cameraManager = getCameraManager();
        recorderManager = new RecorderManager(getCameraManager(), videoSurface, this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        adapter = new ViewPagerItemAdapter(ViewPagerItems.with(this)
                .add(R.string.lat, R.layout.view_camera_1)
                .add(R.string.lon, R.layout.view_camera_2)
                .create());

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1 < 7000) {
                    // System.out.println("Clickable");
                    // finishButton.setClickable(true);
                    // finishButton
                    // .setBackgroundResource(R.drawable.btn_capture_arrow);
                } else {
                    cameraProgress.setVisibility(View.VISIBLE);

                    // System.out.println("UnClickable");
                    // finishButton.setClickable(false);
                    onFinishPressed();
                    // finishButton
                    // .setBackgroundResource(R.drawable.btn_capture_arrow_pressed);
                }
                int circle = (int) (msg.arg1 * 1.0 / 70);

                if (circle > 50) {
                    progress.setProgress(circle + 1);
                } else {
                    progress.setProgress(circle);
                }
                super.handleMessage(msg);
                // //
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);
    }

    private void getLocation(final Context context) {
        SmartLocation.with(context).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mSearch_mapUrl = "http://api-gocci.jp/dist/?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&limit=30";
                    getTenpoJson(context, mSearch_mapUrl);
                } else {
                    Log.e("からでしたー", "locationupdated");
                }
            }
        });
    }

    private void getTenpoJson(final Context context, String url) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(context, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        final String rest_name = jsonObject.getString(TAG_REST_NAME);

                        restname[i] = rest_name;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(context, "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void muteAll(boolean isMute) {
        // ((AudioManager) this.getSystemService(Context.AUDIO_SERVICE))
        // .setStreamSolo(AudioManager.STREAM_SYSTEM, isMute);
        // ((AudioManager) this.getSystemService(Context.AUDIO_SERVICE))
        // .setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
        List<Integer> streams = new ArrayList<Integer>();
        Field[] fields = AudioManager.class.getFields();
        for (Field field : fields) {
            if (field.getName().startsWith("STREAM_")
                    && Modifier.isStatic(field.getModifiers())
                    && field.getType() == int.class) {
                try {
                    Integer stream = (Integer) field.get(null);
                    streams.add(stream);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // do nothing
                }
            }
        }
    }

    public CameraManager getCameraManager() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
        return cameraManager;
    }

    public void onBackPressed(View view) {
        recorderManager.reset();
        isPlaying = false;
        if (progress.getProgress() == 0) {
            CameraActivity.this.finish();
        }
    }

    public void onFinishPressed() {
        if (!isPlaying && recorderManager.getVideoTempFiles().size() != 0) {
            Toast.makeText(CameraActivity.this, "確認画面に進みます", Toast.LENGTH_SHORT).show();
            combineFiles();
            isPlaying = true;
        } else {
            recorderManager.reset();
            isPlaying = false;
        }
    }

    public void startPlay() {
        recorderManager.reset();

        //ここで記入済みの値を持って行こう
        if (onScroll) {
            if (restname_spinner.getText().length() == 0) {
                mRest_name = "";
            } else {
                mRest_name = restname_spinner.getText().toString();
            }
            if (category_spinner.getText().length() == 0) {
                mCategory = "";
            } else {
                mCategory = category_spinner.getText().toString();
            }
            if (mood_spinner.getText().length() == 0) {
                mAtmosphere = "";
            } else {
                mAtmosphere = mood_spinner.getText().toString();
            }
            if (edit_value.getText().length() == 0) {
                mValue = "";
            } else {
                mValue = edit_value.getText().toString();
            }
            if (edit_comment.getText().length() == 0) {
                mComment = "";
            } else {
                mComment = edit_comment.getText().toString();
            }
        } else {
            mRest_name = "";
            mCategory = "";
            mAtmosphere = "";
            mValue = "";
            mComment = "";
        }

        Intent intent = new Intent(CameraActivity.this, CameraPreviewActivity.class);
        intent.putExtra("restname", mRest_name);
        intent.putExtra("video_url", mFinalVideoUrl);
        intent.putExtra("category", mCategory);
        intent.putExtra("mood", mAtmosphere);
        intent.putExtra("comment", mComment);
        intent.putExtra("value", mValue);
        intent.putExtra("isNewRestname", mIsnewRestname);
        intent.putExtra("lat", latitude);
        intent.putExtra("lon", longitude);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

        cameraProgress.setVisibility(View.GONE);
    }

    private void combineFiles() {
        mFinalVideoUrl = getFinalVideoFileName();

        try {
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            for (String fileName : recorderManager.getVideoTempFiles()) {
                try {
                    Movie movie = MovieCreator.build(fileName);
                    for (Track t : movie.getTracks()) {
                        if (t.getHandler().equals("soun")) {
                            audioTracks.add(t);
                        }
                        if (t.getHandler().equals("vide")) {
                            videoTracks.add(t);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            Container out = new DefaultMp4Builder().build(result);

            ContentResolver contentResolver = getContentResolver();
            ContentValues values = new ContentValues(3);
            values.put(MediaStore.Video.Media.TITLE, timeStamp);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.DATA, mFinalVideoUrl);
            contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

            FileOutputStream fos = new FileOutputStream(new File(mFinalVideoUrl));
            out.writeContainer(fos.getChannel());
            fos.close();

            startPlay();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getFinalVideoFileName() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return recorderManager.getVideoParentpath() + "/" + timeStamp + ".mp4";
    }

    @Override
    protected void onDestroy() {
        muteAll(false);
        super.onDestroy();
        recorderManager.reset();
        handler.removeCallbacks(progressRunnable);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isFirst && position == 0) {
            View page = adapter.getPage(position);
            toukouButton = (ImageButton) page.findViewById(R.id.toukouButton);
            progress = (CircleProgressBar) findViewById(R.id.circleProgress);

            toukouButton.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        try {
                            // sign.setPressed(true);
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    cameraManager.getCamera().autoFocus(null);
                                }
                            });
                            recorderManager.startRecord(true);

                        } finally {
                            muteAll(true);
                        }
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            // sign.setPressed(false);
                            recorderManager.stopRecord();
                        } finally {
                            muteAll(false);
                            //
                        }
                    }
                    return true;
                }
            });

            isFirst = false;
        }
    }

    @Override
    public void onPageSelected(int position) {
        View page = adapter.getPage(position);

        switch (position) {
            case 0:

                break;
            case 1:
                onScroll = true;
                EventDateRecorder recorder = EventDateRecorder.load(CameraActivity.this, "use_camera_tab");
                if (!recorder.didRecorded()) {
                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(CameraActivity.this);
                    Effectstype effect = Effectstype.SlideBottom;
                    dialogBuilder
                            .withTitle("タグ画面")
                            .withMessage("店名以外にもタグを付けて投稿してみませんか？")
                            .withDuration(500)                                          //def
                            .withEffect(effect)
                            .isCancelableOnTouchOutside(true)
                            .show();
                    recorder.record();
                }

                restname_spinner = (MaterialBetterSpinner) page.findViewById(R.id.restname_spinner);
                category_spinner = (MaterialBetterSpinner) page.findViewById(R.id.category_spinner);
                mood_spinner = (MaterialBetterSpinner) page.findViewById(R.id.mood_spinner);
                edit_value = (MaterialEditText) page.findViewById(R.id.edit_value);
                edit_comment = (MaterialEditText) page.findViewById(R.id.edit_comment);

                restaddButton = (ImageButton) page.findViewById(R.id.restaddButton);

                restname_spinner.setIconRight(R.drawable.ic_arrow_drop_down_white_24dp);
                category_spinner.setIconRight(R.drawable.ic_arrow_drop_down_white_24dp);
                mood_spinner.setIconRight(R.drawable.ic_arrow_drop_down_white_24dp);

                String[] CATEGORY = getResources().getStringArray(R.array.list_category);
                String[] MOOD = getResources().getStringArray(R.array.list_mood);

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(CameraActivity.this,
                        android.R.layout.simple_dropdown_item_1line, CATEGORY);
                category_spinner.setAdapter(categoryAdapter);

                ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(CameraActivity.this,
                        android.R.layout.simple_dropdown_item_1line, MOOD);
                mood_spinner.setAdapter(moodAdapter);

                ArrayAdapter<String> restAdapter = new ArrayAdapter<>(CameraActivity.this,
                        android.R.layout.simple_dropdown_item_1line, restname);
                restname_spinner.setAdapter(restAdapter);

                restaddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //店舗追加のダイアログ
                        createTenpo();
                    }
                });

                break;
        }

    }

    private void createTenpo() {
        new MaterialDialog.Builder(CameraActivity.this)
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
                        mRest_name = dialog.getInputEditText().getText().toString();

                        RequestParams params = new RequestParams();
                        params.put("restname", mRest_name);
                        params.put("lat", latitude);
                        params.put("lon", longitude);

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(CameraActivity.this, Const.URL_INSERT_REST, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                cameraProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(CameraActivity.this, "通信に失敗しました", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.e("ジェイソン成功", String.valueOf(response));

                                try {
                                    String message = response.getString("message");

                                    if (message.equals("店舗追加完了しました")) {
                                        Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
                                        //店名をセット
                                        mIsnewRestname = true;
                                        restname_spinner.setText(mRest_name);
                                    } else {
                                        Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFinish() {
                                cameraProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (ViewPager.SCROLL_STATE_DRAGGING == state) {
            //ボタンクリックできないように
            toukouButton.setClickable(false);
        } else {
            toukouButton.setClickable(true);
        }

    }

    private class ProgressRunnable implements Runnable {

        @Override
        public void run() {
            int time = 0;
            time = recorderManager.checkIfMax(new Date().getTime());
            Message message = new Message();
            message.arg1 = time;
            handler.sendMessage(message);
            // System.out.println(time);
            handler.postDelayed(this, 10);

        }
    }
}

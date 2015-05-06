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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.andexert.library.RippleView;
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
import com.inase.android.gocci.Base.RecorderManager;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.CameraPreviewView;
import com.inase.android.gocci.View.MySurfaceView;
import com.inase.android.gocci.View.ProgressView;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

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

public class CameraActivity extends Activity {

    private RecorderManager recorderManager = null;
    private CameraManager cameraManager;
    private ProgressView progressView = null;

    private Runnable progressRunnable = null;
    private Handler handler = null;
    private ProgressWheel cameraProgress;

    private String mRest_name;
    private String mFinalVideoUrl;
    private String timeStamp;

    private boolean isPlaying = false;

    private ImageView shopImage;

    private double latitude;
    private double longitude;
    public static ArrayList<UserData> users = new ArrayList<>();
    private String mSearch_mapUrl;

    private static final String TAG_REST_NAME = "restname";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getLocation(this);

        EventDateRecorder maprecorder = EventDateRecorder.load(CameraActivity.this, "use_first_camera");
        if (!maprecorder.didRecorded()) {
            // 機能が１度も利用されてない時のみ実行したい処理を書く
            //初めての起動のみの処理
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(CameraActivity.this);
            Effectstype effect = Effectstype.SlideBottom;
            dialogBuilder
                    .withTitle("Gocciカメラ")
                    .withMessage("このカメラはボタンをタップしている間だけ再生されます。自分の撮りたい場所だけを、７秒で記録しましょう！")
                    .withDuration(500)                                          //def
                    .withEffect(effect)
                    .isCancelableOnTouchOutside(true)
                    .show();

            maprecorder.record();
        }

        Toast.makeText(this, "技術的な問題でうまく動作しない場合があります", Toast.LENGTH_SHORT).show();

        cameraProgress = (ProgressWheel) findViewById(R.id.cameraprogress_wheel);
        MySurfaceView videoSurface = (MySurfaceView) findViewById(R.id.cameraView);
        progressView = (ProgressView) findViewById(R.id.progress);
        shopImage = (ImageView) findViewById(R.id.shopImage);
        ImageView infoImage = (ImageView) findViewById(R.id.infoImage);
        cameraManager = getCameraManager();
        recorderManager = new RecorderManager(getCameraManager(), videoSurface, this);
        RippleView selectShopButton = (RippleView) findViewById(R.id.selectShopButton);
        RippleView infoButton = (RippleView) findViewById(R.id.infoButton);

        ImageButton toukouButton = (ImageButton) findViewById(R.id.toukouButton);

        selectShopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //店舗選択Activity
                if (!users.isEmpty()) {
                    int requestCode = 123;
                    Intent intent = new Intent(CameraActivity.this, SelectShopActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivityForResult(intent, requestCode);
                } else {
                    Toast.makeText(CameraActivity.this, "店舗情報が取れるまで、少々お待ち下さい", Toast.LENGTH_SHORT).show();
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                new MaterialDialog.Builder(CameraActivity.this)
                        .title("追加情報")
                        .content("追加したい情報を入力してください")
                        .positiveText("完了")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                infoImage.setBackgroundResource(R.drawable.ic_done_white_24dp);
                            }
                        }).show();
                        */
            }
        });

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
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int total = ((ViewGroup) progressView.getParent())
                        .getMeasuredWidth();
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
                double length = msg.arg1 * 1.0 / 7000 * total;
                progressView.setWidth((int) length);
                progressView.invalidate();
                super.handleMessage(msg);
                // //
            }
        };

        progressRunnable = new ProgressRunnable();
        handler.post(progressRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mRest_name = bundle.getString("restname");
                    Log.e("インプット店舗", mRest_name);
                    shopImage.setBackgroundResource(R.drawable.ic_done_white_24dp);
                } else if (resultCode == RESULT_CANCELED) {

                }
                break;
            case 125:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mRest_name = bundle.getString("restname");
                    Log.e("インプット店舗", mRest_name);
                    shopImage.setBackgroundResource(R.drawable.ic_done_white_24dp);
                    startPlay();
                } else if (resultCode == RESULT_CANCELED) {

                }
            default:
                break;
        }
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
                users.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        final String rest_name = jsonObject.getString(TAG_REST_NAME);
                        final String locality = jsonObject.getString(TAG_LOCALITY);
                        String distance = jsonObject.getString(TAG_DISTANCE);

                        UserData user = new UserData();

                        user.setRest_name(rest_name);
                        user.setLocality(locality);
                        user.setDistance(distance);

                        users.add(user);

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
        progressView.setBackgroundResource(R.color.bg_capture_progress);
        isPlaying = false;
        if (progressView.getCurrentWidth() == 0) {
            CameraActivity.this.finish();
        }
    }

    public void onFinishPressed() {
        if (!isPlaying && recorderManager.getVideoTempFiles().size() != 0) {
            progressView.setBackgroundResource(R.color.gocci_progress);
            Toast.makeText(CameraActivity.this, "確認画面に進みます", Toast.LENGTH_SHORT).show();
            combineFiles();
            isPlaying = true;
        } else {
            recorderManager.reset();
            isPlaying = false;
        }
    }

    public void startPlay() {
        if (mRest_name != null) {
            recorderManager.reset();

            CameraPreviewView fragment = CameraPreviewView.newInstance(
                    2,
                    4.0f,
                    true,
                    false,
                    false,
                    mRest_name,
                    mFinalVideoUrl
            );
            fragment.setCancelable(false);
            fragment.show(getFragmentManager(), "blur_sample");
            cameraProgress.setVisibility(View.GONE);
        } else {
            Toast.makeText(CameraActivity.this, "撮影するお店を選択してください", Toast.LENGTH_SHORT).show();
            if (!users.isEmpty()) {
                int requestCode = 125;
                Intent intent = new Intent(CameraActivity.this, SelectShopActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, requestCode);
            }
        }
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

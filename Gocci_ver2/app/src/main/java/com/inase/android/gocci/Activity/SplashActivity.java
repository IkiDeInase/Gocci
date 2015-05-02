package com.inase.android.gocci.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class SplashActivity extends Activity {

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new MaterialDialog.Builder(this)
                    .title("位置情報取得について")
                    .content("位置情報を使いたいのですが、GPSが無効になっています。" + "設定を変更しますか？")
                    .positiveText("はい")
                    .positiveColorRes(R.color.gocci_header)
                    .negativeText("いいえ")
                    .negativeColorRes(R.color.material_drawer_primary_light)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingIntent);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            Toast.makeText(SplashActivity.this, "近くの店舗表示ができなくなります", Toast.LENGTH_LONG).show();
                        }
                    }).show();

        } else {
            firstLocation();
        }
    }

    private void firstLocation() {
        SmartLocation.with(getApplicationContext()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    Application_Gocci.setFirstLocation(location);
                    Log.e("とったどー", "いえい！");

                    EventDateRecorder recorder = EventDateRecorder.load(SplashActivity.this, "use_first_gocci_android");
                    if (!recorder.didRecorded()) {
                        // 機能が１度も利用されてない時のみ実行したい処理を書く
                        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        SplashActivity.this.finish();
                        recorder.record();
                    } else {
                        Intent mainIntent2 = new Intent(SplashActivity.this, LoginPreferenceActivity.class);
                        SplashActivity.this.startActivity(mainIntent2);
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        SplashActivity.this.finish();
                    }
                } else {
                    Toast.makeText(SplashActivity.this, "位置情報が読み取れないため、Gocciを起動できませんでした", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}





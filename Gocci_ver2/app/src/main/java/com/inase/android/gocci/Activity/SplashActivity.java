package com.inase.android.gocci.Activity;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class SplashActivity extends Activity {

    private Application_Gocci gocci;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        gocci = (Application_Gocci) getApplication();
        firstLocation();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);
    }

    private void firstLocation() {
        SmartLocation.with(getApplicationContext()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    gocci.setFirstLocation(location);
                    Log.e("とったどー", "いえい！");
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                    SplashActivity.this.finish();
                } else {
                    Toast.makeText(SplashActivity.this, "位置情報が読み取れないため、Gocciを起動できませんでした", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}





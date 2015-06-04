package com.inase.android.gocci.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.inase.android.gocci.Camera.up18CameraFragment;
import com.inase.android.gocci.R;

public class GocciCameraActivity extends AppCompatActivity {

    public static String[] restname = new String[30];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gocci_camera);

        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new up18CameraFragment()).commit();
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            } else {
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        }

    }

}


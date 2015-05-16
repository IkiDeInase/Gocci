package com.inase.android.gocci.Activity;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class SplashActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult> {

    private Application_Gocci gocci;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected LocationSettingsRequest mLocationSettingsRequest;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        gocci = (Application_Gocci) getApplication();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        checkLocationSettings();
    }

    private void firstLocation() {
        SmartLocation.with(getApplicationContext()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (location != null) {
                    gocci.setFirstLocation(location);
                    Log.e("とったどー", "いえい！");

                    EventDateRecorder recorder = EventDateRecorder.load(SplashActivity.this, "use_first_gocci_android");
                    if (!recorder.didRecorded()) {
                        // 機能が１度も利用されてない時のみ実行したい処理を書く

                        /*
                        //初回起動時
                        //一度アンインストールして再びインストールした
                        //ログアウトして再びログインしたい
                         */
                        Log.e("DEBUG", "LoginActivitへ");
                        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        SplashActivity.this.finish();
                        recorder.record();
                    } else {

                        /*
                        //２回目以降の起動
                         */
                        Log.e("DEBUG", "LoginPreferenceActivitへ");
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

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.e("DEBUG", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.e("ログ", "All location settings are satisfied.");
                firstLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.e("ログ", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("ログ", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.e("ログ", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                Toast.makeText(SplashActivity.this, "アプリを終了します", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("ログ", "User agreed to make required location settings changes.");
                        firstLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("ログ", "User chose not to make required location settings changes.");
                        Toast.makeText(SplashActivity.this, "アプリを終了します", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                break;
        }
    }
}





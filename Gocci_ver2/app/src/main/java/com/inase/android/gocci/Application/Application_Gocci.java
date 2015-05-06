package com.inase.android.gocci.Application;


import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.inase.android.gocci.common.CacheManager;
import com.mopub.common.MoPub;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import io.nlopez.smartlocation.SmartLocation;

public class Application_Gocci extends Application {

    private final String TAG = "Gocci";

    //経度緯度情報
    private Location mLocation;

    public void setFirstLocation(Location location) {
        mLocation = location;
    }

    public Location getFirstLocation() {
        return mLocation;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Gocci起動");
        CacheManager.getInstance(getApplicationContext()).clearCache();
        //Example: single kit
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("kurJalaArRFtwhnZCoMxB2kKU",
                        "oOCDmf29DyJyfxOPAaj8tSASzSPAHNepvbxcfVLkA9dJw7inYa");

        // Example: multiple kits
        Fabric.with(this, new Twitter(authConfig),
                new Crashlytics(),
                new MoPub());

    }

    @Override
    public void onTerminate() {
        Log.v(TAG, "Gocci終了");
        SmartLocation.with(getApplicationContext()).location().stop();
    }
}

package com.inase.android.gocci.Application;


import android.app.Application;
import android.location.Location;
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
    private Location mLocation = null;

    private String mName;
    private String mPicture;
    private int mFollower;
    private int mFollowee;
    private int mCheer;

    @Override
    public void onCreate() {
        Log.v(TAG, "Gocci起動");
        CacheManager.getInstance(getApplicationContext()).clearCache();
        //Example: single kit
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("kurJalaArRFtwhnZCoMxB2kKU",
                        "oOCDmf29DyJyfxOPAaj8tSASzSPAHNepvbxcfVLkA9dJw7inYa");

        //Fabric.with(this, new Twitter(authConfig));

        // Example: multiple kits
        Fabric.with(this, new Twitter(authConfig),
                new Crashlytics(),
                new MoPub());

    }

    public void setFirstLocation(Location location) {
        mLocation = location;
    }

    public Location getFirstLocation() {
        return mLocation;
    }

    public void setAccount(String name, String picture, Integer follower, Integer followee, Integer cheer) {
        mName = name;
        mPicture = picture;
        mFollower = follower;
        mFollowee = followee;
        mCheer = cheer;
    }

    public String getName() {
        return mName;
    }

    public String getPicture() {
        return mPicture;
    }

    public int getFollower() {
        return mFollower;
    }

    public int getFollowee() {
        return mFollowee;
    }

    public int getCheer() {
        return mCheer;
    }

    public void addFollower() {
        mFollower++;
    }

    public void addFollowee() {
        mFollowee++;
    }

    public void addCheer() {
        mCheer++;
    }

    public void downFollower() {
        mFollower--;
    }

    public void downFollowee() {
        mFollowee--;
    }

    public void downCheer() {
        mCheer--;
    }

    @Override
    public void onTerminate() {
        Log.v(TAG, "Gocci終了");
        SmartLocation.with(getApplicationContext()).location().stop();
    }

}

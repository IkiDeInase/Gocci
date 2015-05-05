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

    //端末内で使うやつ
    private String mName;
    private String mPicture;
    private String mBackground;
    private int mFollower;
    private int mFollowee;
    private int mCheer;

    //ログイン時に投げるやつ
    private String mLoginName;
    private String mLoginPicture;

    //経度緯度情報
    private Location mLocation;

    public void setAccount(String name, String picture, String background, int followee, int follower, int cheer) {
        mName = name;
        mPicture = picture;
        mBackground = background;
        mFollowee = followee;
        mFollower = follower;
        mCheer = cheer;
    }

    public void setLoginParam(String name, String picture) {
        mLoginName = name;
        mLoginPicture = picture;
    }

    public void changeProfile(String name, String picture, String background) {
        mName = name;
        mPicture = picture;
        mBackground = background;
    }

    public String getMyName() {
        return mName;
    }

    public String getMypicture() {
        return mPicture;
    }

    public String getMyBackground() {
        return mBackground;
    }

    public int getMyFollower() {
        return mFollower;
    }

    public int getMyFollowee() {
        return mFollowee;
    }

    public int getMyCheer() {
        return mCheer;
    }

    public String getLoginName() {
        return mLoginName;
    }

    public String getLoginPicture() {
        return mLoginPicture;
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

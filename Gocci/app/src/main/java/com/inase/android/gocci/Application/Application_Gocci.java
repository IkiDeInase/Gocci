package com.inase.android.gocci.Application;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.CreateProviderFinishEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.aws.CustomProvider;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class Application_Gocci extends Application {

    private final String TAG = "Gocci";

    public static CognitoSyncManager syncClient;
    public static CognitoCachingCredentialsProvider credentialsProvider = null;
    public static CustomProvider customProvider;

    //経度緯度情報
    private double mLatitude;
    private double mLongitude;

    public void setFirstLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getFirstLatitude() {
        return mLatitude;
    }

    public double getFirstLongitude() {
        return mLongitude;
    }

    private static final String PROPERTY_ID = "UA-63362687-1";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public Application_Gocci() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public static void sendGAScreen(Context context, String screenName) {
        Tracker t = ((Application_Gocci) context.getApplicationContext()).getTracker(Application_Gocci.TrackerName.APP_TRACKER);
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public static void sendGAEvent(Context context, String category, String action, String label) {
        if (label.length() == 0) {
            label = "-";
        }
        Tracker t = ((Application_Gocci) context.getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(0)
                .build());
    }

    public static void SNSInit(final Context context, final String providerName, final String token) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                credentialsProvider = new CognitoCachingCredentialsProvider(context, Const.IDENTITY_POOL_ID, Const.REGION);

                Map<String, String> logins = credentialsProvider.getLogins();
                if (logins == null) {
                    logins = new HashMap<String, String>();
                }
                logins.put(providerName, token);
                credentialsProvider.setLogins(logins);

                syncClient = new CognitoSyncManager(context, Const.REGION, credentialsProvider);

                Log.e("SNSInit", credentialsProvider.getIdentityId());
                return credentialsProvider.getIdentityId();
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new CreateProviderFinishEvent(result));
            }
        }.execute();
    }

    public static void GuestInit(final Context context, final String identity_id, final String token, final String user_id) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                customProvider = new CustomProvider(identity_id, token);
                credentialsProvider = new CognitoCachingCredentialsProvider(context, customProvider, Const.REGION);

                Map<String, String> logins = credentialsProvider.getLogins();
                if (logins == null) {
                    logins = new HashMap<String, String>();
                }
                logins.put(customProvider.getProviderName(), user_id);
                credentialsProvider.setLogins(logins);
                credentialsProvider.refresh();

                syncClient = new CognitoSyncManager(context, Const.REGION, credentialsProvider);

                Log.e("GuestInit", credentialsProvider.getIdentityId());
                return credentialsProvider.getIdentityId();
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new CreateProviderFinishEvent(result));
            }
        }.execute();
    }

    public static CognitoSyncManager getInstance() {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }
        return syncClient;
    }

    public static void addLogins(final String providerName, final String token) {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Map<String, String> logins = credentialsProvider.getLogins();
                if (logins == null) {
                    logins = new HashMap<String, String>();
                }
                logins.put(providerName, token);
                credentialsProvider.setLogins(logins);
                credentialsProvider.refresh();
                return null;
            }
        }.execute();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Gocci起動");
        CacheManager.getInstance(getApplicationContext()).clearCache();
        //Example: single kit
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("kurJalaArRFtwhnZCoMxB2kKU",
                        "oOCDmf29DyJyfxOPAaj8tSASzSPAHNepvbxcfVLkA9dJw7inYa");

        // Example: multiple kits
        Fabric.with(this, new Twitter(authConfig),
                new Crashlytics());

        FacebookSdk.sdkInitialize(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.v(TAG, "Gocci終了");
    }
}

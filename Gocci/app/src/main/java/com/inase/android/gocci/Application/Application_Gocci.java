package com.inase.android.gocci.Application;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.CreateProviderFinishEvent;
import com.inase.android.gocci.Event.SNSMatchFinishEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.aws.CustomProvider;
import com.inase.android.gocci.common.CacheManager;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class Application_Gocci extends Application {

    private final String TAG = "Gocci";

    public static CognitoCachingCredentialsProvider credentialsProvider = null;
    public static CustomProvider customProvider = null;
    public static AmazonS3 s3 = null;
    public static TransferUtility transferUtility = null;

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

                s3 = new AmazonS3Client(credentialsProvider);
                s3.setRegion(Region.getRegion(Const.REGION));
                transferUtility = new TransferUtility(s3, context);

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

                s3 = new AmazonS3Client(credentialsProvider);
                s3.setRegion(Region.getRegion(Const.REGION));
                transferUtility = new TransferUtility(s3, context);

                return "identityId";
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new CreateProviderFinishEvent(result));
            }
        }.execute();
    }

    public static void addLogins(final Context context, String providerName, String token, String profile_img) {
        /*
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Map<String, String> logins = credentialsProvider.getLogins();
                if (logins == null) {
                    logins = new HashMap<String, String>();
                }
                logins.put(providerName, token);
                credentialsProvider.setLogins(logins);
                credentialsProvider.refresh();
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new CreateProviderFinishEvent(result));
            }
        }.execute();
        */
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getAuthSNSMatchAPI(providerName, token, profile_img), new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "通信に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int code = response.getInt("code");
                    String message = response.getString("message");
                    String profile_img = response.getString("profile_img");

                    if (code == 200) {
                        BusHolder.get().post(new SNSMatchFinishEvent(message, profile_img));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

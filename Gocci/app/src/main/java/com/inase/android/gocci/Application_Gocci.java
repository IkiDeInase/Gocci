package com.inase.android.gocci;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.memory.CacheManager;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.CreateProviderFinishEvent;
import com.inase.android.gocci.event.SNSMatchFinishEvent;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.aws.CustomProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

public class Application_Gocci extends Application {

    private final String TAG = "Gocci";

    private static Application_Gocci sInstance;

    private static CognitoCachingCredentialsProvider credentialsProvider = null;
    private static CustomProvider customProvider = null;
    private static AmazonS3 s3 = null;
    private static TransferUtility transferUtility = null;

    private static final SyncHttpClient sSsyncHttpClient = new SyncHttpClient();
    private static final AsyncHttpClient sAsyncHttpClient = new AsyncHttpClient();

    //経度緯度情報
    private double mLatitude;
    private double mLongitude;

    public void setFirstLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public static void getJsonSyncHttpClient(String url, JsonHttpResponseHandler responseHandler) {
        sSsyncHttpClient.get(url, responseHandler);
    }

    public static void getTextSyncHttpClient(String url, TextHttpResponseHandler responseHandler) {
        sSsyncHttpClient.get(url, responseHandler);
    }

    public static void getJsonAsyncHttpClient(String url, JsonHttpResponseHandler responseHandler) {
        sAsyncHttpClient.get(url, responseHandler);
    }

    public static void getTextAsyncHttpClient(String url, TextHttpResponseHandler responseHandler) {
        sAsyncHttpClient.get(url, responseHandler);
    }

    public static void getAsyncHttpClient(String url, AsyncHttpResponseHandler responseHandler) {
        sAsyncHttpClient.get(url, responseHandler);
    }

    public static CognitoCachingCredentialsProvider getLoginProvider() {
        return credentialsProvider;
    }

    public static CognitoCachingCredentialsProvider getProvider(Context context) {
        if (credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(context, Const.IDENTITY_POOL_ID, Const.REGION);
        }
        return credentialsProvider;
    }

    private static AmazonS3 getS3(Context context) {
        if (s3 == null) {
            s3 = new AmazonS3Client(getProvider(context));
        }
        return s3;
    }

    public static TransferUtility getTransfer(Context context) {
        if (transferUtility == null) {
            transferUtility = new TransferUtility(getS3(context), context);
        }
        return transferUtility;
    }

    public static TransferUtility getShareTransfer() {
        return transferUtility;
    }

    public double getFirstLatitude() {
        return mLatitude;
    }

    public double getFirstLongitude() {
        return mLongitude;
    }

    private static final String PROPERTY_ID = "UA-63362687-1";

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public Application_Gocci() {
        super();
    }

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
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
                credentialsProvider.refresh();

                return credentialsProvider.getIdentityId();
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new CreateProviderFinishEvent(result));
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        s3 = new AmazonS3Client(credentialsProvider);
                        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
                        transferUtility = new TransferUtility(s3, context);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (SavedData.getPostingId(context) != 0) {
                            TransferObserver observer = transferUtility.resume(SavedData.getPostingId(context));
                            observer.setTransferListener(new TransferListener() {
                                @Override
                                public void onStateChanged(int id, TransferState state) {
                                    if (state == TransferState.COMPLETED) {
                                        SavedData.setPostingId(context, 0);
                                    }
                                }

                                @Override
                                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                                }

                                @Override
                                public void onError(int id, Exception ex) {

                                }
                            });
                        }
                    }
                }.execute();
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

                return "identityId";
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new CreateProviderFinishEvent(result));
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        s3 = new AmazonS3Client(credentialsProvider);
                        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
                        transferUtility = new TransferUtility(s3, context);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (SavedData.getPostingId(context) != 0) {
                            TransferObserver observer = transferUtility.resume(SavedData.getPostingId(context));
                            if (observer != null) {
                                observer.setTransferListener(new TransferListener() {
                                    @Override
                                    public void onStateChanged(int id, TransferState state) {
                                        if (state == TransferState.COMPLETED) {
                                            SavedData.setPostingId(context, 0);
                                        }
                                    }

                                    @Override
                                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                                    }

                                    @Override
                                    public void onError(int id, Exception ex) {

                                    }
                                });
                            } else {
                                SavedData.setPostingId(context, 0);
                            }
                        }
                    }
                }.execute();
            }
        }.execute();
    }

    public static void addLogins(final Context context, String providerName, String token, String profile_img) {
        getJsonAsyncHttpClient(Const.getAuthSNSMatchAPI(providerName, token, profile_img), new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, context.getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int code = response.getInt("code");
                    String message = response.getString("message");
                    String profile_img = response.getString("profile_img");

                    if (code == 200) {
                        SavedData.setServerPicture(context, profile_img);
                        BusHolder.get().post(new SNSMatchFinishEvent(message, profile_img));

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                credentialsProvider.refresh();
                                return null;
                            }
                        }.execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void postingVideoToS3(final Context context, String mAwsPostName, File mVideoFile) {
        final TransferObserver transferObserver = getTransfer(context).upload(Const.POST_MOVIE_BUCKET_NAME, mAwsPostName + ".mp4", mVideoFile);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.IN_PROGRESS) {
                    SavedData.setPostingId(context, id);
                } else if (state == TransferState.COMPLETED) {
                    SavedData.setPostingId(context, 0);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, context.getString(R.string.bad_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized Application_Gocci getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Gocci起動");
        sInstance = this;
        CacheManager.getInstance(getApplicationContext()).clearCache();
        //Example: single kit
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("kurJalaArRFtwhnZCoMxB2kKU",
                        "oOCDmf29DyJyfxOPAaj8tSASzSPAHNepvbxcfVLkA9dJw7inYa");

        // Example: multiple kits
        Fabric.with(this, new Twitter(authConfig),
                new Crashlytics(), new TweetComposer());

        FacebookSdk.sdkInitialize(this);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker(PROPERTY_ID); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        sSsyncHttpClient.setCookieStore(SavedData.getCookieStore(this));
        sAsyncHttpClient.setCookieStore(SavedData.getCookieStore(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.v(TAG, "Gocci終了");
    }
}

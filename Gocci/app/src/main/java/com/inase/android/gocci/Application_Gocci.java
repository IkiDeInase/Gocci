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
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.utils.SavedData;
import com.inase.android.gocci.utils.aws.CustomProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;
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

    private static final SyncHttpClient sSyncHttpClient = new SyncHttpClient();
    private static final AsyncHttpClient sAsyncHttpClient = new AsyncHttpClient();

    public static void getJsonSync(String url, JsonHttpResponseHandler responseHandler) {
        sSyncHttpClient.setCookieStore(SavedData.getCookieStore(getInstance().getApplicationContext()));
        sSyncHttpClient.get(url, responseHandler);
    }

    public static void getJsonAsync(String url, JsonHttpResponseHandler responseHandler) {
        sAsyncHttpClient.setCookieStore(SavedData.getCookieStore(getInstance().getApplicationContext()));
        sAsyncHttpClient.get(url, responseHandler);
    }

    public static AsyncHttpClient getClient() {
        return sAsyncHttpClient;
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

    public static class SNSAsync extends AsyncTask<Void, Void, String> {

        public interface SNSAsyncCallback {
            void preExecute();

            void onPostExecute(String identity_id);
        }

        private SNSAsyncCallback callback = null;
        private Context context;
        private String provider_name = null;
        private String token = null;

        public SNSAsync(Context context, String provider_name, String token, SNSAsyncCallback asyncCallback) {
            this.context = context;
            this.provider_name = provider_name;
            this.token = token;
            this.callback = asyncCallback;
        }

        @Override
        protected String doInBackground(Void... params) {
            credentialsProvider = new CognitoCachingCredentialsProvider(context, Const.IDENTITY_POOL_ID, Const.REGION);

            Map<String, String> logins = credentialsProvider.getLogins();
            if (logins == null) {
                logins = new HashMap<String, String>();
            }
            logins.put(provider_name, token);
            credentialsProvider.setLogins(logins);
            credentialsProvider.refresh();

            return credentialsProvider.getIdentityId();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.preExecute();
        }

        @Override
        protected void onPostExecute(String identity_id) {
            super.onPostExecute(identity_id);
            this.callback.onPostExecute(identity_id);
            createS3(context);
        }
    }

    public static void SNSInit(Context context, final String providerName, final String token, SNSAsync.SNSAsyncCallback cb) {
        new Application_Gocci.SNSAsync(context, providerName, token, cb).execute();
    }

    public static class GuestAsync extends AsyncTask<Void, Void, String> {

        private Context context;
        private String identity_id = null;
        private String cognito_token = null;
        private String user_id = null;

        public GuestAsync(Context context, String identity_id, String cognito_token, String user_id) {
            this.context = context;
            this.identity_id = identity_id;
            this.cognito_token = cognito_token;
            this.user_id = user_id;
        }

        @Override
        protected String doInBackground(Void... params) {
            customProvider = new CustomProvider(identity_id, cognito_token);
            credentialsProvider = new CognitoCachingCredentialsProvider(context, customProvider, Const.REGION);

            Map<String, String> logins = credentialsProvider.getLogins();
            if (logins == null) {
                logins = new HashMap<String, String>();
            }
            logins.put(customProvider.getProviderName(), user_id);
            credentialsProvider.setLogins(logins);
            credentialsProvider.refresh();

            return identity_id;
        }

        @Override
        protected void onPostExecute(String identity_id) {
            super.onPostExecute(identity_id);
            createS3(context);
        }
    }

    public static void GuestInit(final Context context, final String identity_id, final String token, final String user_id) {
        new Application_Gocci.GuestAsync(context, identity_id, token, user_id).execute();
    }

    private static void createS3(final Context context) {
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
                                    SavedData.setPostingId(context, id);
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
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void resolveOrHandleGlobalError(final Const.APICategory api, API3.Util.GlobalCode globalCode) {
        switch (globalCode) {
            case ERROR_SESSION_EXPIRED:
                //ログインとコグニートリフレッシュ　→　リトライ
                        getJsonAsync(API3.Util.getAuthLoginAPI(SavedData.getIdentityId(getInstance().getApplicationContext())), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                API3.Impl.getRepository().AuthLoginResponse(response, new API3.PayloadResponseCallback() {
                                    @Override
                                    public void onSuccess(JSONObject jsonObject) {
                                        //リフレッシュ&リトライ
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... params) {
                                                credentialsProvider.refresh();
                                                return null;
                                            }
                                        }.execute();
                                        createS3(getInstance().getApplicationContext());
                                        BusHolder.get().post(new RetryApiEvent(api));
                                    }

                                    @Override
                                    public void onGlobalError(API3.Util.GlobalCode globalCode) {
                                        resolveOrHandleGlobalError(api, globalCode);
                                    }

                                    @Override
                                    public void onLocalError(String errorMessage) {
                                        Toast.makeText(Application_Gocci.getInstance().getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                break;
            case ERROR_CLIENT_OUTDATED:
                //アップデートダイアログ
                break;
        }
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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.v(TAG, "Gocci終了");
    }
}

package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Fragment.TutorialFragment;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TutorialGuideActivity extends AppCompatActivity {

    static final int NUM_PAGES = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    TextView login_session_text;
    boolean isOpaque = true;

    private ProgressWheel progress;

    private boolean isOK = false;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String SENDER_ID = "913263613395";

    static final String TAG = "GCMDemo";

    private GoogleCloudMessaging gcm;
    private String regid;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_tutorial);

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Log.e("msg", regid);
                SavedData.setRegId(TutorialGuideActivity.this, regid);
            }
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);

        login_session_text = TextView.class.cast(findViewById(R.id.have_text));
        login_session_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSessionActivity.startLoginSessionActivity(TutorialGuideActivity.this);
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(5);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == NUM_PAGES - 1 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == 3 && !isOK) {
                    Handler handler = new Handler();
                    handler.postDelayed(new UsernameClickHandler(), 250);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will then pass the result to the login
        // button.
        Fragment fragment = (Fragment) pagerAdapter.instantiateItem(pager, pager.getCurrentItem());
        if (fragment != null && pager.getCurrentItem() == 4) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < NUM_PAGES) {
            for (int i = 0; i < NUM_PAGES; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.text_selected));
                } else {
                    circle.setColorFilter(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TutorialFragment tp = null;
            switch (position) {
                case 0:
                    tp = TutorialFragment.newInstance(R.layout.view_tutorial1);
                    break;
                case 1:
                    tp = TutorialFragment.newInstance(R.layout.view_tutorial2);
                    break;
                case 2:
                    tp = TutorialFragment.newInstance(R.layout.view_tutorial3);
                    break;
                case 3:
                    tp = TutorialFragment.newInstance(R.layout.view_tutorial4);
                    break;
                case 4:
                    tp = TutorialFragment.newInstance(R.layout.view_tutorial5);
                    break;
            }

            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head = page.findViewById(R.id.text_1);
            View text_content = page.findViewById(R.id.text_2);

            View object1 = page.findViewById(R.id.image_1);//Gocciくん
            View object2 = page.findViewById(R.id.image_2);//カメラ
            View object3 = page.findViewById(R.id.image_3);//Gocciくん２
            View object4 = page.findViewById(R.id.image_4);//Gocciうすうす
            View object5 = page.findViewById(R.id.image_5);//Gocciうす
            View object6 = page.findViewById(R.id.image_6);//Gocciなかうす
            View object7 = page.findViewById(R.id.image_7);//Gocciふつう
            View object8 = page.findViewById(R.id.image_8);//Gocciくん
            View object9 = page.findViewById(R.id.image_9);//Gocciくん

            RippleView twitter_ripple = (RippleView) page.findViewById(R.id.twitter_Ripple);
            RippleView facebook_ripple = (RippleView) page.findViewById(R.id.facebook_Ripple);
            RippleView skip_ripple = (RippleView) page.findViewById(R.id.skip_Ripple);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {
            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }

                if (object1 != null) {
                    ViewHelper.setTranslationX(object1, pageWidth / 2 * position);
                }

                // parallax effect
                if (object2 != null) {
                    ViewHelper.setTranslationX(object2, pageWidth * position);
                }

                if (object3 != null) {
                    ViewHelper.setTranslationX(object3, pageWidth / 2 * position);
                }

                if (object4 != null) {
                    ViewHelper.setTranslationX(object4, (float) (pageWidth / 1.5 * position));
                }
                if (object5 != null) {
                    ViewHelper.setTranslationX(object5, (float) (pageWidth / 1.1 * position));
                }
                if (object6 != null) {
                    ViewHelper.setTranslationX(object6, (float) (pageWidth / 1.6 * position));
                }
                if (object7 != null) {
                    ViewHelper.setTranslationX(object7, (float) (pageWidth / 1.8 * position));
                }

                if (object8 != null) {
                    //ViewHelper.setTranslationX(object8,(float)(pageWidth/1.5 * position));
                }

                if (object9 != null) {
                    ViewHelper.setTranslationX(object9, pageWidth / 2 * position);
                }

                if (twitter_ripple != null) {
                    ViewHelper.setTranslationX(twitter_ripple, pageWidth * position);
                    ViewHelper.setAlpha(twitter_ripple, 1.0f - Math.abs(position));
                }

                if (facebook_ripple != null) {
                    ViewHelper.setTranslationX(facebook_ripple, pageWidth * position);
                    ViewHelper.setAlpha(facebook_ripple, 1.0f - Math.abs(position));
                }

                if (skip_ripple != null) {
                    ViewHelper.setTranslationX(skip_ripple, pageWidth * position);
                    ViewHelper.setAlpha(skip_ripple, 1.0f - Math.abs(position));
                }
            }
        }
    }

    class UsernameClickHandler implements Runnable {
        public void run() {
            //ダイアログ
            setLoginDialog(TutorialGuideActivity.this);
        }

        private void setLoginDialog(final Context context) {
            new MaterialDialog.Builder(context)
                    .content("好きなユーザー名を入力しましょう")
                    .input("ユーザー名", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                            materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(charSequence.length() > 0);
                        }
                    })
                    .cancelable(false)
                    .alwaysCallInputCallback()
                    .positiveText("完了")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {
                            super.onPositive(dialog);
                            isOK = true;
                            progress.setVisibility(View.VISIBLE);
                            username = dialog.getInputEditText().getText().toString();

                            String url = Const.getAuthSignupAPI(username, Build.VERSION.RELEASE, Build.MODEL, regid);
                            Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
                            Const.asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.has("message")) {
                                            int code = response.getInt("code");
                                            String user_id = response.getString("user_id");
                                            String username = response.getString("username");
                                            String profile_img = response.getString("profile_img");
                                            String identity_id = response.getString("identity_id");
                                            int badge_num = response.getInt("badge_num");
                                            String message = response.getString("message");
                                            String token = response.getString("token");

                                            if (code == 200) {
                                                SavedData.setWelcome(context, username, profile_img, user_id, identity_id, badge_num);
                                                Application_Gocci.GuestInit(context, identity_id, token, user_id);
                                                progress.setVisibility(View.INVISIBLE);
                                                SavedData.setFlag(context, 0);
                                                isOK = true;
                                            } else {
                                                progress.setVisibility(View.INVISIBLE);
                                                isOK = false;
                                                pager.setCurrentItem(3);
                                            }
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            progress.setVisibility(View.INVISIBLE);
                                            isOK = false;
                                            pager.setCurrentItem(3);
                                            Toast.makeText(TutorialGuideActivity.this, "このユーザー名はすでに登録されています", Toast.LENGTH_SHORT).show();
                                            //setLoginDialog(context);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                    isOK = false;
                                    pager.setCurrentItem(3);
                                    Toast.makeText(context, "ログインに失敗しました", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .show();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.e(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.e(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences("pref",
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(TutorialGuideActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(TutorialGuideActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("メッセージログ", msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        SavedData.setRegId(TutorialGuideActivity.this, regid);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.e(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }
}

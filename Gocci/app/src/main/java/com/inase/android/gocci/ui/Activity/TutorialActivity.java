package com.inase.android.gocci.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.andexert.library.RippleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.service.RegistrationIntentService;
import com.inase.android.gocci.ui.fragment.LoginCreateUserNameFragment;
import com.inase.android.gocci.ui.fragment.LoginSocialAuthenticationFragment;
import com.nineoldandroids.view.ViewHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialActivity extends AppCompatActivity {

    static final int NUM_PAGES = 5;

    PagerAdapter pagerAdapter;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    static final String TAG = "GCMDemo";

    private static MobileAnalyticsManager analytics;

    @Bind(R.id.pager)
    public ViewPager mPager;
    @Bind(R.id.circles)
    LinearLayout mCircles;

    @OnClick(R.id.have_text)
    public void session() {
        LoginSessionActivity.startLoginSessionActivity(TutorialActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    Const.ANALYTICS_ID, //Amazon Mobile Analytics App ID
                    Const.IDENTITY_POOL_ID //Amazon Cognito Identity Pool ID
            );
        } catch (InitializationException ex) {
            Log.e(this.getClass().getName(), "Failed to initialize Amazon Mobile Analytics", ex);
        }

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Log.e(TAG, "No valid Google Play Services APK found.");
        }

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setOffscreenPageLimit(4);
        //pager.setPageTransformer(true, new CrossfadePageTransformer());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
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
        Fragment fragment = (Fragment) pagerAdapter.instantiateItem(mPager, mPager.getCurrentItem());
        if (fragment != null && mPager.getCurrentItem() == 4) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 45: {
                mPager.setCurrentItem(4, true);
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPager != null) {
            mPager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < NUM_PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            mCircles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < NUM_PAGES - 1) {
            for (int i = 0; i < NUM_PAGES - 1; i++) {
                ImageView circle = (ImageView) mCircles.getChildAt(i);
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
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tp = null;
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
                    tp = LoginCreateUserNameFragment.newInstance();
                    break;
                case 4:
                    tp = LoginSocialAuthenticationFragment.newInstance();
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

            RippleView twitter_ripple = (RippleView) page.findViewById(R.id.twitter_ripple);
            RippleView facebook_ripple = (RippleView) page.findViewById(R.id.facebook_ripple);
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

    public static class TutorialFragment extends Fragment {
        final static String LAYOUT_ID = "layoutid";

        public static TutorialFragment newInstance(int layoutId) {
            TutorialFragment pane = new TutorialFragment();
            Bundle args = new Bundle();
            args.putInt(LAYOUT_ID, layoutId);
            pane.setArguments(args);
            return pane;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int layoutId = getArguments().getInt(LAYOUT_ID, -1);
            ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);
            return rootView;
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
}

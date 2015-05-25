package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.andexert.library.RippleView;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.Tutorial.TutorialView1;
import com.inase.android.gocci.Tutorial.TutorialView2;
import com.inase.android.gocci.Tutorial.TutorialView3;
import com.inase.android.gocci.Tutorial.TutorialView4;
import com.inase.android.gocci.common.SavedData;
import com.viewpagerindicator.CirclePageIndicator;

public class TutorialGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_guide);

        Intent intent = getIntent();
        String judge = intent.getStringExtra("judge");
        String name = intent.getStringExtra("name");
        String picture = intent.getStringExtra("picture");

        EventDateRecorder recorder = EventDateRecorder.load(this, "use_first_gocci_tutorial");
        if (!recorder.didRecorded()) {
            // 機能が１度も利用されてない時のみ実行したい処理を書く
            recorder.record();
            Log.e("DEBUG", "TutorialGuideActivit　チュートあり");

            //Gocciへようこそ　このアプリは・・・・的な感じで考えている。

            SavedData.setLoginParam(this, name, picture, judge);

            ViewPager tutorialViewpager = (ViewPager) findViewById(R.id.viewpager_tutorial);
            tutorialViewpager.setAdapter(new TutorialPagerAdapter(getSupportFragmentManager()));
            tutorialViewpager.setOffscreenPageLimit(4);
            CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circles);
            circlePageIndicator.setViewPager(tutorialViewpager);


            //チュートリアルを実施
            RippleView button = (RippleView) findViewById(R.id.tutorial_finish);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler handler = new Handler();
                    handler.postDelayed(new finishClickHandler(), 750);
                }
            });

        } else {
            Log.e("DEBUG", "TutorialGuideActivit　チュートなし");

            Intent goIntent = new Intent(TutorialGuideActivity.this, GocciTimelineActivity.class);
            startActivity(goIntent);
            TutorialGuideActivity.this.finish();
        }
    }

    class TutorialPagerAdapter extends FragmentPagerAdapter {

        public TutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TutorialView1();
                case 1:
                    return new TutorialView2();
                case 2:
                    return new TutorialView3();
                default:
                    return new TutorialView4();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    class finishClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(TutorialGuideActivity.this, GocciTimelineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            TutorialGuideActivity.this.finish();
        }
    }
}

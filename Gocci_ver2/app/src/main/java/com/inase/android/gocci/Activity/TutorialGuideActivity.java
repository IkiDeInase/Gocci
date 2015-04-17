package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.R;
import com.inase.android.gocci.Tutorial.TutorialView1;
import com.inase.android.gocci.Tutorial.TutorialView2;
import com.inase.android.gocci.Tutorial.TutorialView3;
import com.inase.android.gocci.Tutorial.TutorialView4;
import com.viewpagerindicator.CirclePageIndicator;

public class TutorialGuideActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_guide);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String picture = intent.getStringExtra("picture");
        final String judge = intent.getStringExtra("judge");

        EventDateRecorder recorder = EventDateRecorder.load(this, "use_first_gocci");
        if (!recorder.didRecorded()) {
            // 機能が１度も利用されてない時のみ実行したい処理を書く
            recorder.record();

            //Gocciへようこそ　このアプリは・・・・的な感じで考えている。

            SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString("name", name);
            editor.putString("pictureImageUrl", picture);
            editor.putString("judge", judge);
            editor.apply();

            ViewPager tutorialViewpager = (ViewPager) findViewById(R.id.viewpager_tutorial);
            tutorialViewpager.setAdapter(new TutorialPagerAdapter(getSupportFragmentManager()));
            CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circles);
            circlePageIndicator.setViewPager(tutorialViewpager);


            //チュートリアルを実施
            Button button = (Button) findViewById(R.id.tutorial_finish);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TutorialGuideActivity.this, GocciTimelineActivity.class);
                    startActivity(intent);
                    TutorialGuideActivity.this.finish();
                }
            });

        } else {
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


}

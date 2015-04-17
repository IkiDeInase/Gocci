package com.inase.android.gocci.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class GocciMyprofActivity extends ActionBarActivity {

    private String mName;
    private String mPicureImageurl;
    private Integer mFolloweeNumber; //フォローされている
    private Integer mFollowerNumber; //フォローしている
    private Integer mCheerNumber;

    private Application_Gocci gocci;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gocci_myprof);

        gocci = (Application_Gocci) getApplication();
        mName = gocci.getName();
        mPicureImageurl = gocci.getPicture();
        mFollowerNumber = gocci.getFollower();
        mFolloweeNumber = gocci.getFollowee();
        mCheerNumber = gocci.getCheer();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        View header = new DrawerProfHeader(this, mName, mPicureImageurl, mFollowerNumber, mFolloweeNumber, mCheerNumber);

        final Drawer.Result result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("ライフログ").withIcon(GoogleMaterial.Icon.gmd_event).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName("お店を検索する").withIcon(GoogleMaterial.Icon.gmd_explore).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイプロフィール").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(4).withCheckable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("アプリに関する要望を送る").withCheckable(false).withIdentifier(5),
                        new SecondaryDrawerItem().withName("お店を追加する").withCheckable(false).withIdentifier(6),
                        new SecondaryDrawerItem().withName("利用規約").withCheckable(false).withIdentifier(7),
                        new SecondaryDrawerItem().withName("プライバシーポリシー").withCheckable(false).withIdentifier(8)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Handler handler = new Handler();
                                handler.postDelayed(new timelineClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Handler handler = new Handler();
                                handler.postDelayed(new lifelogClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 3) {
                                Handler handler = new Handler();
                                handler.postDelayed(new searchClickHandler(), 500);
                            }
                        }

                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result.setSelectionByIdentifier(4);
    }

    class timelineClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciMyprofActivity.this, GocciTimelineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class lifelogClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciMyprofActivity.this, GocciLifelogActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class searchClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciMyprofActivity.this, GocciSearchTenpoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // いつものUPナビゲーションの処理
        switch (id) {
            case R.id.videocam:
                Log.e("通知押したよ", "クリック");
                return true;
            case R.id.videocam2:
                Intent intent = new Intent(GocciMyprofActivity.this, CameraActivity.class);
                intent.putExtra("name", mName);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

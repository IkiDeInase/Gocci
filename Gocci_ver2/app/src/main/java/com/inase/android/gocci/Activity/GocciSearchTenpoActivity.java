package com.inase.android.gocci.Activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.SearchKeywordPostEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class GocciSearchTenpoActivity extends ActionBarActivity {

    private String mName;
    private String mPicureImageurl;
    private Integer mFolloweeNumber; //フォローされている
    private Integer mFollowerNumber; //フォローしている
    private Integer mCheerNumber;

    private Application_Gocci gocci;

    private Toolbar toolbar;
    private CardView search_card;

    private SearchView mSearchView;

    private double mLat = 0.0;
    private double mLon = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gocci_search_tenpo);

        gocci = (Application_Gocci) getApplication();
        mName = gocci.getName();
        mPicureImageurl = gocci.getPicture();
        mFollowerNumber = gocci.getFollower();
        mFolloweeNumber = gocci.getFollowee();
        mCheerNumber = gocci.getCheer();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.search);

        mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint("お店を検索する");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchWord) {
                if (mLat != 0.0) {
                    BusHolder.get().post(new SearchKeywordPostEvent(searchWord, mLat, mLon));
                    mSearchView.clearFocus();
                } else {
                    Toast.makeText(GocciSearchTenpoActivity.this, "現在地が読み込めず、検索に失敗しました。", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartLocation.with(GocciSearchTenpoActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        if (location != null) {
                            mLat = location.getLatitude();
                            mLon = location.getLongitude();
                            Log.e("とったどー", "検索に使うよ");
                        } else {
                            Log.e("からでしたー", "locationupdated");
                        }
                    }
                });
            }
        });

        search_card = (CardView) findViewById(R.id.search_card);
        search_card.setCardElevation(4);
        search_card.setRadius(4);

        View header = new DrawerProfHeader(this, mName, mPicureImageurl, mFollowerNumber, mFolloweeNumber, mCheerNumber);

        Drawer.Result result = new Drawer()
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
                            } else if (drawerItem.getIdentifier() == 4) {
                                Handler handler = new Handler();
                                handler.postDelayed(new myprofClickHandler(), 500);
                            }
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result.setSelectionByIdentifier(3);
    }

    class timelineClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciSearchTenpoActivity.this, GocciTimelineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class lifelogClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciSearchTenpoActivity.this, GocciLifelogActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class myprofClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciSearchTenpoActivity.this, GocciMyprofActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }
}

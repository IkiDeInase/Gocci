package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inase.android.gocci.R;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapSearchActivity extends AppCompatActivity {

    @Bind(R.id.tool_bar)
    Toolbar toolBar;

    private SupportMapFragment fm;

    private GoogleMap mMap;

    private Marker mMarker = null;

    private String mPlace;
    private double mLat;
    private double mLon;

    private MenuItem pin;

    private NiftyDialogBuilder niftyDialogBuilder;

    public static void startMapSearchActivity(int requestCode, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MapSearchActivity.class);
        startingActivity.startActivityForResult(intent, requestCode);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        ButterKnife.bind(this);

        niftyDialogBuilder = NiftyDialogBuilder.getInstance(this);
        niftyDialogBuilder
                .withTitle("注意事項")
                .withMessage("見つけたい場所をクリックして右上のチェックマークを押そう！")
                .withDuration(500)                                          //def
                .withEffect(Effectstype.SlideBottom)
                .show();

        //googleserviceが使えるか判断して処理を分ける
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapSearchActivity.this);
        if (status != ConnectionResult.SUCCESS) {
            // Google Play Services が使えない場合
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, MapSearchActivity.this, requestCode);
            dialog.show();
        } else {
            // Google Play Services が使える場合
            //activity_main.xmlのSupportMapFragmentへの参照を取得
            fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(readyCallback);
        }

        toolBar.setTitle("場所を選択する");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    OnMapReadyCallback readyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            //fragmentからGoogleMap objectを取得
            mMap = fm.getMap();

            //Google MapのMyLocationレイヤーを使用可能にする
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.681382, 139.766084), 4));

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    pin.setVisible(true);
                    pin.setCheckable(true);
                    mLat = latLng.latitude;
                    mLon = latLng.longitude;

                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    Geocoder geo = new Geocoder(MapSearchActivity.this);
                    try {
                        List<Address> list = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        String address = list.get(0).getAddressLine(1);
                        int index = address.indexOf(" ");
                        mPlace = address.substring(index + 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_map, menu);
        // お知らせ未読件数バッジ表示
        pin = menu.findItem(R.id.map_check);
        pin.setVisible(false);
        pin.setCheckable(false);
        pin.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("place", mPlace);
                bundle.putDouble("lat", mLat);
                bundle.putDouble("lon", mLon);
                data.putExtras(bundle);
                setResult(RESULT_OK, data);
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}

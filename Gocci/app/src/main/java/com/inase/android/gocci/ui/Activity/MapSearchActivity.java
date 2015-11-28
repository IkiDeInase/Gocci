package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.api.API3;
import com.inase.android.gocci.datasource.repository.HeatmapRepository;
import com.inase.android.gocci.datasource.repository.HeatmapRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.usecase.HeatmapUseCase;
import com.inase.android.gocci.domain.usecase.HeatmapUseCaseImpl;
import com.inase.android.gocci.presenter.ShowHeatmapPresenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapSearchActivity extends AppCompatActivity implements ShowHeatmapPresenter.ShowHeatmapView {

    @Bind(R.id.tool_bar)
    Toolbar toolBar;
    @Bind(R.id.map_layout)
    RelativeLayout mapLayout;

    private Snackbar mSnack;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private SupportMapFragment fm;

    private GoogleMap mMap;

    private Marker mMarker = null;

    private String mPlace;
    private double mLat;
    private double mLon;

    private MenuItem pin;

    private ShowHeatmapPresenter mPresenter;

    public static void startMapSearchActivity(int requestCode, double longitude, double latitude, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MapSearchActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startingActivity.startActivityForResult(intent, requestCode);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        ButterKnife.bind(this);

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

        final API3 api3Impl = API3.Impl.getRepository();
        HeatmapRepository heatmapRepositoryImpl = HeatmapRepositoryImpl.getRepository(api3Impl);
        HeatmapUseCase heatmapUseCaseImpl = HeatmapUseCaseImpl.getUseCase(heatmapRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowHeatmapPresenter(heatmapUseCaseImpl);
        mPresenter.setHeatmapView(this);

        toolBar.setTitle("場所を選択する");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    OnMapReadyCallback readyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            API3.Util.GetHeatmapLocalCode localCode = API3.Impl.getRepository().get_heatmap_parameter_regex();
            if (localCode == null) {
                mPresenter.getHeatmapData(Const.APICategory.GET_HEATMAP_FIRST, API3.Util.getGetHeatmapAPI());
            } else {
                Toast.makeText(MapSearchActivity.this, API3.Util.getHeatmapLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
            }

            mMap = googleMap;

            //fragmentからGoogleMap objectを取得
            mMap = fm.getMap();

            //Google MapのMyLocationレイヤーを使用可能にする
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getDoubleExtra("latitude", 35.681382), getIntent().getDoubleExtra("longitude", 139.766084)), 10));

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
                    if (mSnack.isShown()) {
                        mSnack.dismiss();
                        Toast.makeText(MapSearchActivity.this, "位置を特定しています", Toast.LENGTH_SHORT).show();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    Geocoder geo = new Geocoder(MapSearchActivity.this);
                    try {
                        List<Address> list = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        String address = list.get(0).getAddressLine(1);
                        int index = address.indexOf(" ");
                        mPlace = address.substring(index + 1);
                        toolBar.setTitle(mPlace);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSnack = Snackbar.make(mapLayout, "検索したい位置をタップしてピンを立てよう", Snackbar.LENGTH_INDEFINITE);
            mSnack.show();
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

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(Const.APICategory api, ArrayList<LatLng> data) {
        if (mProvider == null) {
            mProvider = new HeatmapTileProvider.Builder().data(
                    data).build();
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            // Render links
        } else {
            mProvider.setData(data);
            mOverlay.clearTileCache();
        }
    }
}

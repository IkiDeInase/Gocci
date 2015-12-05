package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
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
import com.inase.android.gocci.utils.map.HeatmapLog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MapSearchActivity extends AppCompatActivity implements ShowHeatmapPresenter.ShowHeatmapView, ClusterManager.OnClusterClickListener<HeatmapLog>,
        ClusterManager.OnClusterInfoWindowClickListener<HeatmapLog>, ClusterManager.OnClusterItemClickListener<HeatmapLog>, ClusterManager.OnClusterItemInfoWindowClickListener<HeatmapLog> {

    @Bind(R.id.tool_bar)
    Toolbar toolBar;
    @Bind(R.id.map_layout)
    RelativeLayout mapLayout;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;

    private Snackbar mSnack;

    private SupportMapFragment fm;

    private GoogleMap mMap;

    private ClusterManager<HeatmapLog> mClusterManager;

    private String mPlace;
    private double mLat;
    private double mLon;

    private LatLng clickedClusterPosition = null;
    private LatLng clickedItemPosition = null;

    private ShowHeatmapPresenter mPresenter;

    public static void startMapSearchActivity(int requestCode, double longitude, double latitude, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MapSearchActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startingActivity.startActivityForResult(intent, requestCode);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    public boolean onClusterClick(Cluster<HeatmapLog> cluster) {
        if (cluster.getPosition() != clickedClusterPosition) {
            clickedClusterPosition = cluster.getPosition();
            mLat = cluster.getPosition().latitude;
            mLon = cluster.getPosition().longitude;

            if (mSnack.isShown()) {
                mSnack.dismiss();
            }
            Toast.makeText(MapSearchActivity.this, "位置を特定しています", Toast.LENGTH_SHORT).show();
            getRevGeo(mLat, mLon);
        } else {
            backNearline();
        }
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<HeatmapLog> cluster) {
        backNearline();
    }

    private void backNearline() {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("place", mPlace);
        bundle.putDouble("lat", mLat);
        bundle.putDouble("lon", mLon);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onClusterItemClick(HeatmapLog heatmapLog) {
        if (heatmapLog.getPosition() != clickedItemPosition) {
            clickedItemPosition = heatmapLog.getPosition();
        } else {
            TenpoActivity.startTenpoActivity(heatmapLog.mRest_id, heatmapLog.mRestname, MapSearchActivity.this);
        }
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(HeatmapLog heatmapLog) {
        TenpoActivity.startTenpoActivity(heatmapLog.mRest_id, heatmapLog.mRestname, MapSearchActivity.this);
    }

    private class HeatmapLogRenderer extends DefaultClusterRenderer<HeatmapLog> {

        public HeatmapLogRenderer(Context context, GoogleMap map, ClusterManager<HeatmapLog> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(HeatmapLog heatmapLog, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(heatmapLog, markerOptions);
            // Draw a single person.
            // Set the info window to show their name.
            markerOptions.title(heatmapLog.mRestname);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<HeatmapLog> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            super.onBeforeClusterRendered(cluster, markerOptions);
            markerOptions.title("この周辺の近い店を見る");
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
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
            mMap = googleMap;

            //fragmentからGoogleMap objectを取得
            mMap = fm.getMap();

            mClusterManager = new ClusterManager<HeatmapLog>(MapSearchActivity.this, mMap);
            mClusterManager.setRenderer(new HeatmapLogRenderer(getApplicationContext(), mMap, mClusterManager));
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(MapSearchActivity.this);
            mClusterManager.setOnClusterInfoWindowClickListener(MapSearchActivity.this);
            mClusterManager.setOnClusterItemClickListener(MapSearchActivity.this);
            mClusterManager.setOnClusterItemInfoWindowClickListener(MapSearchActivity.this);

            API3.Util.GetHeatmapLocalCode localCode = API3.Impl.getRepository().get_heatmap_parameter_regex();
            if (localCode == null) {
                mPresenter.getHeatmapData(Const.APICategory.GET_HEATMAP_FIRST, API3.Util.getGetHeatmapAPI());
            } else {
                Toast.makeText(MapSearchActivity.this, API3.Util.getHeatmapLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
            }

            //Google MapのMyLocationレイヤーを使用可能にする
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mSnack = Snackbar.make(mapLayout, "検索したい位置のクラスターをタップしてみよう", Snackbar.LENGTH_INDEFINITE);
            mSnack.show();
        }
    };

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
        mProgressWheel.setVisibility(View.GONE);
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
    public void showResult(Const.APICategory api, ArrayList<HeatmapLog> data) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getDoubleExtra("latitude", 35.681382), getIntent().getDoubleExtra("longitude", 139.766084)), 8));
        mClusterManager.addItems(data);
        mClusterManager.cluster();
    }

    private void getRevGeo(double lat, double lng) {
        try {
            Application_Gocci.getJsonAsync("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true&language=ja", new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray resultsArray = response.getJSONArray("results");
                        // 配列を用意
                        String locality = "";
                        for (int i = 0; i < resultsArray.length(); i++) {
                            if (!locality.isEmpty()) break;
                            JSONObject jsonObject = resultsArray.getJSONObject(i);
                            JSONArray typesArray = jsonObject.getJSONArray("types");
                            for (int j = 0; j < typesArray.length(); j++) {
                                if (!locality.isEmpty()) break;
                                String elem = typesArray.getString(j);
                                if (elem.equals("locality") && locality.isEmpty()) {
                                    locality = jsonObject.getString("formatted_address");
                                    break;
                                }
                            }
                        }

                        if (!locality.isEmpty()) {
                            // 区市町村を返す
                            int index = locality.indexOf(" ");
                            mPlace = locality.substring(index + 1);
                            toolBar.setTitle(mPlace.isEmpty() ? "場所不明" : mPlace);
                        } else {
                            toolBar.setTitle("場所不明");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                }
            });
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
    }
}

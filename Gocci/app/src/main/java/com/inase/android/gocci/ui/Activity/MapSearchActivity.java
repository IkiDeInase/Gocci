package com.inase.android.gocci.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.inase.android.gocci.event.AddressNameEvent;
import com.inase.android.gocci.event.BusHolder;
import com.inase.android.gocci.event.RetryApiEvent;
import com.inase.android.gocci.presenter.ShowHeatmapPresenter;
import com.inase.android.gocci.utils.map.HeatmapLog;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

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

    private String mPlace = "";
    private double mLat;
    private double mLon;

    private LatLng clickedClusterPosition = null;
    private LatLng clickedItemPosition = null;

    private ShowHeatmapPresenter mPresenter;

    private Geocoder geo;

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    public static void startMapSearchActivity(int requestCode, double longitude, double latitude, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MapSearchActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startingActivity.startActivityForResult(intent, requestCode);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    public boolean onClusterClick(Cluster<HeatmapLog> cluster) {
        clickedItemPosition = null;
        if (cluster.getPosition() != clickedClusterPosition) {
            clickedClusterPosition = cluster.getPosition();
            mLat = cluster.getPosition().latitude;
            mLon = cluster.getPosition().longitude;

            if (mSnack.isShown()) {
                mSnack.dismiss();
            }
            Toast.makeText(MapSearchActivity.this, getString(R.string.place_searching_alert), Toast.LENGTH_SHORT).show();
            mPlace = getString(R.string.place_searching);
            toolBar.setTitle(mPlace);
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
        clickedClusterPosition = null;
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
            markerOptions.title(getString(R.string.see_near_place));
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

        applicationGocci = (Application_Gocci) getApplication();

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

        toolBar.setTitle(getString(R.string.select_place));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        geo = new Geocoder(MapSearchActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("MapSearch");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        BusHolder.get().register(this);
        mPresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        BusHolder.get().unregister(this);
        mPresenter.pause();
    }

    @Subscribe
    public void subscribe(RetryApiEvent event) {
        switch (event.api) {
            case GET_HEATMAP_FIRST:
                mPresenter.getHeatmapData(Const.APICategory.GET_HEATMAP_FIRST, API3.Util.getGetHeatmapAPI());
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void subscribe(AddressNameEvent event) {
        toolBar.setTitle(event.mPlace.isEmpty() ? getString(R.string.undefined_place) : event.mPlace);
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

            API3.Util.GetHeatmapLocalCode localCode = API3.Impl.getRepository().GetHeatmapParameterRegex();
            if (localCode == null) {
                mPresenter.getHeatmapData(Const.APICategory.GET_HEATMAP_FIRST, API3.Util.getGetHeatmapAPI());
            } else {
                Toast.makeText(MapSearchActivity.this, API3.Util.GetHeatmapLocalCodeMessageTable(localCode), Toast.LENGTH_SHORT).show();
            }

            //Google MapのMyLocationレイヤーを使用可能にする
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mSnack = Snackbar.make(mapLayout, getString(R.string.please_click_cluster), Snackbar.LENGTH_INDEFINITE);
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
        Application_Gocci.resolveOrHandleGlobalError(this, api, globalCode);
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

    private void getRevGeo(final double lat, final double lng) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    List<Address> list = geo.getFromLocation(lat, lng, 1);
                    String address = list.get(0).getAddressLine(1);
                    int index = address.indexOf(" ");
                    mPlace = address.substring(index + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mPlace;
            }

            @Override
            protected void onPostExecute(String result) {
                BusHolder.get().post(new AddressNameEvent(mPlace));
            }
        }.execute();
    }
}

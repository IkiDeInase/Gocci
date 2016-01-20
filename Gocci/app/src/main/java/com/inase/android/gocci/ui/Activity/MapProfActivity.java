package com.inase.android.gocci.ui.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.domain.model.PostData;
import com.inase.android.gocci.utils.map.MultiDrawable;
import com.inase.android.gocci.utils.map.PhotoLog;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapProfActivity extends AppCompatActivity implements ClusterManager.OnClusterClickListener<PhotoLog>, ClusterManager.OnClusterInfoWindowClickListener<PhotoLog>, ClusterManager.OnClusterItemClickListener<PhotoLog>, ClusterManager.OnClusterItemInfoWindowClickListener<PhotoLog> {

    @Bind(R.id.tool_bar)
    Toolbar mToolBar;
    @Bind(R.id.progress_wheel)
    ProgressWheel mProgressWheel;

    private SupportMapFragment fm;

    private GoogleMap mMap;

    private ClusterManager<PhotoLog> mClusterManager;

    private MapDrawableAsync mAsync;

    private static ArrayList<PhotoLog> list = new ArrayList<>();

    private Tracker mTracker;
    private Application_Gocci applicationGocci;

    private static ArrayList<PostData> mList = new ArrayList<>();

    private LatLng clickedItemPosition = null;

    public static void startProfMapActivity(double longitude, double latitude, ArrayList<PostData> data, Activity startingActivity) {
        mList = data;
        Intent intent = new Intent(startingActivity, MapProfActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startingActivity.startActivity(intent);
        startingActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private class PhotoLogRenderer extends DefaultClusterRenderer<PhotoLog> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PhotoLogRenderer(Context context, GoogleMap map, ClusterManager<PhotoLog> clusterManager) {
            super(context, map, clusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.cell_photolog, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.photolog_size);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.photolog_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(PhotoLog photolog, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageDrawable(photolog.mDrawable);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(photolog.mRestname + "(" + photolog.mDatetime + ")");
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<PhotoLog> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (PhotoLog p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = p.mDrawable;
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<PhotoLog> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String restname = cluster.getItems().iterator().next().mRestname;
        boolean isSame = true;
        for (PhotoLog photoLog : cluster.getItems()) {
            if (!restname.equals(photoLog.mRestname)) {
                isSame = false;
                break;
            }
        }

        if (isSame) {
            if (cluster.getPosition() != clickedItemPosition) {
                clickedItemPosition = cluster.getPosition();
                return false;
            } else {
                TenpoActivity.startTenpoActivity(cluster.getItems().iterator().next().userdata.getPost_rest_id(), cluster.getItems().iterator().next().userdata.getRestname(), MapProfActivity.this);
                return true;
            }
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), mMap.getCameraPosition().zoom + 3));
            return true;
        }
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PhotoLog> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(PhotoLog item) {
        // Does nothing, but you could go into the user's profile page, for example.
        if (item.getPosition() != clickedItemPosition) {
            clickedItemPosition = item.getPosition();
        } else {
            TenpoActivity.startTenpoActivity(item.userdata.getPost_rest_id(), item.userdata.getRestname(), MapProfActivity.this);
        }
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PhotoLog item) {
        // Does nothing, but you could go into the user's profile page, for example.
        TenpoActivity.startTenpoActivity(item.userdata.getPost_rest_id(), item.userdata.getRestname(), MapProfActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FragmentのViewを返却
        setContentView(R.layout.activity_map_prof);
        ButterKnife.bind(this);

        applicationGocci = (Application_Gocci) getApplication();

        mAsync = new MapDrawableAsync(new MapDrawableAsync.Callback() {
            @Override
            public void onFinish() {
                //googleserviceが使えるか判断して処理を分ける
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapProfActivity.this);
                if (status != ConnectionResult.SUCCESS) {
                    // Google Play Services が使えない場合
                    int requestCode = 10;
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, MapProfActivity.this, requestCode);
                    dialog.show();
                } else {
                    // Google Play Services が使える場合
                    //activity_main.xmlのSupportMapFragmentへの参照を取得
                    fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    fm.getMapAsync(readyCallback);
                }
            }
        }, this);
        mAsync.execute();

        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        mToolBar.setTitle(getString(R.string.maplog));
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker = applicationGocci.getDefaultTracker();
        mTracker.setScreenName("MapProf");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onDestroy() {
        mAsync.cancel(true);
        super.onDestroy();
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

            mClusterManager = new ClusterManager<PhotoLog>(MapProfActivity.this, mMap);
            mClusterManager.setRenderer(new PhotoLogRenderer(getApplicationContext(), mMap, mClusterManager));
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(MapProfActivity.this);
            mClusterManager.setOnClusterInfoWindowClickListener(MapProfActivity.this);
            mClusterManager.setOnClusterItemClickListener(MapProfActivity.this);
            mClusterManager.setOnClusterItemInfoWindowClickListener(MapProfActivity.this);
            mClusterManager.addItems(list);
            mClusterManager.cluster();

            mProgressWheel.setVisibility(View.GONE);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getDoubleExtra("latitude", 35.681382), getIntent().getDoubleExtra("longitude", 139.766084)), 12));
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

    private static class MapDrawableAsync extends AsyncTask<Void, Void, Void> {

        private Callback callbackRef;
        private Context context;

        private MapDrawableAsync(Callback callback, Context context) {
            callbackRef = callback;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            list.clear();
            for (PostData data : mList) {
                try {
                    list.add(new PhotoLog(data, new BitmapDrawable(Picasso.with(context).load(data.getThumbnail()).get())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            callbackRef.onFinish();
        }

        private interface Callback {
            void onFinish();
        }
    }
}

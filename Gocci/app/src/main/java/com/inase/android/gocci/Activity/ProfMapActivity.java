package com.inase.android.gocci.Activity;


import android.app.Dialog;
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
import com.inase.android.gocci.R;
import com.inase.android.gocci.data.MultiDrawable;
import com.inase.android.gocci.data.PhotoLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ProfMapActivity extends AppCompatActivity implements ClusterManager.OnClusterClickListener<PhotoLog>, ClusterManager.OnClusterInfoWindowClickListener<PhotoLog>, ClusterManager.OnClusterItemClickListener<PhotoLog>, ClusterManager.OnClusterItemInfoWindowClickListener<PhotoLog> {

    private SupportMapFragment fm;

    private GoogleMap mMap;

    private ClusterManager<PhotoLog> mClusterManager;

    private MapDrawableAsync mAsync;

    private class PhotoLogRenderer extends DefaultClusterRenderer<PhotoLog> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PhotoLogRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);

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
            Log.e("ログ", "onBeforeClusterItemRendered");
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
            Log.e("ログ", "onBeforeClusterRendered");
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
        Log.e("ログ", "onClusterClick");
        String restname = cluster.getItems().iterator().next().mRestname;
        boolean isSame = true;
        for (PhotoLog photoLog : cluster.getItems()) {
            if (!restname.equals(photoLog.mRestname)) {
                isSame = false;
                break;
            }
        }

        if (isSame) {
            CommentActivity.startCommentActivity(Integer.parseInt(cluster.getItems().iterator().next().userdata.getPost_id()), ProfMapActivity.this);
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), mMap.getCameraPosition().zoom + 3));
        }
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PhotoLog> cluster) {
        // Does nothing, but you could go to a list of the users.
        Log.e("ログ", "onClusterInfoWindowClick");
    }

    @Override
    public boolean onClusterItemClick(PhotoLog item) {
        // Does nothing, but you could go into the user's profile page, for example.
        Log.e("ログ", "onClusterItemClick");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PhotoLog item) {
        // Does nothing, but you could go into the user's profile page, for example.
        Log.e("ログ", "onClusterItemInfoWindowClick");
        CommentActivity.startCommentActivity(Integer.parseInt(item.userdata.getPost_id()), ProfMapActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FragmentのViewを返却
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        setContentView(R.layout.activity_prof_map);

        mAsync = new MapDrawableAsync(new MapDrawableAsync.Callback() {
            @Override
            public void onFinish() {
                //googleserviceが使えるか判断して処理を分ける
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ProfMapActivity.this);
                if (status != ConnectionResult.SUCCESS) {
                    // Google Play Services が使えない場合
                    int requestCode = 10;
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, ProfMapActivity.this, requestCode);
                    dialog.show();
                } else {
                    // Google Play Services が使える場合
                    //activity_main.xmlのSupportMapFragmentへの参照を取得
                    fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    fm.getMapAsync(readyCallback);
                }
            }
        });
        mAsync.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.inflateMenu(R.menu.toolbar_menu);
        //toolbar.setLogo(R.drawable.ic_gocci_moji_white45);
        toolbar.setTitle("マップログ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

            mClusterManager = new ClusterManager<PhotoLog>(ProfMapActivity.this, mMap);
            mClusterManager.setRenderer(new PhotoLogRenderer());
            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(ProfMapActivity.this);
            mClusterManager.setOnClusterInfoWindowClickListener(ProfMapActivity.this);
            mClusterManager.setOnClusterItemClickListener(ProfMapActivity.this);
            mClusterManager.setOnClusterItemInfoWindowClickListener(ProfMapActivity.this);

            for (int i = 0; i < GocciMyprofActivity.mProfusers.size(); i++) {
                mClusterManager.addItem(new PhotoLog(GocciMyprofActivity.mProfusers.get(i), GocciMyprofActivity.mProfDrawables.get(i)));
            }
            mClusterManager.cluster();
            Log.e("ログ", "onMapReady");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.681382, 139.766084), 4));
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

        private MapDrawableAsync(Callback callback) {
            callbackRef = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            GocciMyprofActivity.mProfDrawables.clear();
            for (int i = 0; i < GocciMyprofActivity.mProfusers.size(); i++) {
                Bitmap bmp = ImageLoader.getInstance().loadImageSync(GocciMyprofActivity.mProfusers.get(i).getThumbnail());
                GocciMyprofActivity.mProfDrawables.add(new BitmapDrawable(bmp));
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

package com.example.kinagafuji.gocci.Base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.kinagafuji.gocci.Activity.IntentVineCamera;
import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Fragment.LifelogFragment;
import com.example.kinagafuji.gocci.Fragment.ProfileFragment;
import com.example.kinagafuji.gocci.Fragment.Search_mapFragment;
import com.example.kinagafuji.gocci.Fragment.TimelineFragment;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.data.PopupHelper;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.ToukouPopup;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SlidingTabsBasicFragment extends BaseFragment {

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    private String name;
    private String pictureImageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.fragment_sample, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        name = pref.getString("name", "no_name");
        pictureImageUrl = pref.getString("pictureImageUrl", null);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //mViewPager.setBackgroundColor(R.color.main_color);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new SamplePagerAdapter(getFragmentManager()));

        //mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setCustomTabView();
        mSlidingTabLayout.setDividerColors(android.R.color.transparent);
        //mSlidingTabLayout.setCustomTabColorizer();
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)

    }

    class SamplePagerAdapter extends FragmentPagerAdapter {


        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TimelineFragment().newIntent(name, pictureImageUrl);

                case 1:
                    return new Search_mapFragment().newIntent(name, pictureImageUrl);

                case 2:
                    return new LifelogFragment().newIntent(name, pictureImageUrl);

                case 3:
                    return new ProfileFragment().newIntent(name, pictureImageUrl);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }



    }
}

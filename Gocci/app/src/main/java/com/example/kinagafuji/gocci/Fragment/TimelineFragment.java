package com.example.kinagafuji.gocci.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.kinagafuji.gocci.Activity.TenpoActivity;
import com.example.kinagafuji.gocci.Activity.UserProfActivity;
import com.example.kinagafuji.gocci.Adapter.TimelineAdapter;
import com.example.kinagafuji.gocci.AsyncTask.TimelineAsyncTask;
import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.View.ToukouView;
import com.example.kinagafuji.gocci.data.LayoutHolder;
import com.example.kinagafuji.gocci.data.ToukouPopup;
import com.example.kinagafuji.gocci.data.UserData;

import java.util.ArrayList;

public class TimelineFragment extends BaseFragment implements ListView.OnScrollListener {

    private static final String sTimelineUrl = "http://api-gocci.jp/timeline/";

    public CustomProgressDialog mTimelineDialog;
    public ListView mTimelineListView;
    public ArrayList<UserData> mTimelineusers = new ArrayList<UserData>();
    public TimelineAdapter mTimelineAdapter;

    private SwipeRefreshLayout mTimelineSwipe;

    public String mName;
    public String mPictureImageUrl;

    public boolean mBusy = false;

    public LayoutHolder.CommentHolder commentHolder;
    public LayoutHolder.LikeCommentHolder likeCommentHolder;

    public String currentgoodnum;

    private static final String KEY_IMAGE_URL = "image_url";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG = "TimelineFragment";

    public TimelineFragment newIntent(String name, String imageUrl) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(TAG_USER_NAME, name);
        if (imageUrl != null) {
            args.putString(KEY_IMAGE_URL, imageUrl);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_timeline,
                container, false);

        new TimelineAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sTimelineUrl);
        mTimelineDialog = new CustomProgressDialog(getActivity());
        mTimelineDialog.setCancelable(false);
        mTimelineDialog.show();

        mTimelineListView = (ListView) view.findViewById(R.id.mylistView2);

        final ImageButton toukouButton = (ImageButton) view.findViewById(R.id.toukouButton);
        toukouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation animation = (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_repeat);
                animation.setInterpolator(new LinearInterpolator());
                toukouButton.startAnimation(animation);

                SharedPreferences pref = getActivity().getSharedPreferences("latlon", Context.MODE_PRIVATE);
                String latitude = pref.getString("latitude", null);
                String longitude = pref.getString("longitude", null);

                double mLatitude = Double.parseDouble(latitude);
                double mLongitude = Double.parseDouble(longitude);

                View inflateView = new ToukouView(getActivity(), mName, mPictureImageUrl, mLatitude, mLongitude);
                Log.d("経度・緯度", mLatitude + "/" + mLongitude);

                final PopupWindow window = ToukouPopup.newBasicPopupWindow(getActivity());
                window.setContentView(inflateView);
                //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                ToukouPopup.showLikeQuickAction(window, inflateView, v, getActivity().getWindowManager(), 0, 0);

            }
        });

        mTimelineAdapter = new TimelineAdapter(getActivity(), 0, mTimelineusers);
        mTimelineListView.setDivider(null);
        // スクロールバーを表示しない
        mTimelineListView.setVerticalScrollBarEnabled(false);
        // カード部分をselectorにするので、リストのselectorは透明にする
        mTimelineListView.setSelector(android.R.color.transparent);

        mTimelineListView.setAdapter(mTimelineAdapter);

        mTimelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int line = (position / 5) * 5;
                int pos = position - line;
                UserData country = mTimelineusers.get(position);

                switch (pos) {
                    case 0:
                        //名前部分のview　プロフィール画面へ
                        //Signupを読み込みそう後回し
                        Intent userintent = new Intent(getActivity(), UserProfActivity.class);
                        userintent.putExtra("username", country.getUser_name());
                        userintent.putExtra("name", mName);
                        userintent.putExtra("pictureImageUrl", mPictureImageUrl);
                        startActivity(userintent);
                        break;

                    case 1:
                        //動画のview
                        //クリックしたら止まるくらい
                        break;

                    case 2:
                        //コメントのview
                        //とくになんもしない
                        break;

                    case 3:
                        //レストランのview
                        //レストラン画面に飛ぼうか
                        Intent intent = new Intent(getActivity(), TenpoActivity.class);
                        intent.putExtra("restname", country.getRest_name());
                        intent.putExtra("name", mName);
                        intent.putExtra("pictureImageUrl", mPictureImageUrl);
                        intent.putExtra("locality", country.getLocality());
                        startActivity(intent);
                        break;

                    case 4:
                        //いいね　コメント　シェア
                        break;

                }
            }
        });

        mTimelineSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_timeline);
        mTimelineSwipe.setColorSchemeColors(R.color.main_color_light, R.color.gocci, R.color.main_color_dark, R.color.window_bg);
        mTimelineSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new TimelineAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sTimelineUrl);
                mTimelineDialog = new CustomProgressDialog(getActivity());
                mTimelineDialog.setCancelable(false);
                mTimelineDialog.show();
                mTimelineSwipe.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        mPictureImageUrl = args.getString(KEY_IMAGE_URL);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // スクロールしていない
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;
                break;

            // スクロール中
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mBusy = true;
                break;

            // はじいたとき
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                mBusy = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "Fragment-onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "Fragment-onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "Fragment-onDestroyView");
    }

}

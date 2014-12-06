package com.example.kinagafuji.gocci.Fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.R;

public class LifelogFragment extends BaseFragment {

    private static final String KEY_IMAGE_URL = "image_url";
    private static final String TAG_USER_NAME = "user_name";

    private static final String TAG = "LifelogFragment";

    public String mName;
    public String pictureImageUrl;

    public LifelogFragment newIntent(String name, String imageUrl) {
        LifelogFragment fragment = new LifelogFragment();
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
        View view2 = getActivity().getLayoutInflater().inflate(R.layout.fragment_lifelog,
                container, false);
        // Add the newly created View to the ViewPager

        return view2;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 引数を取得
        Bundle args = getArguments();
        mName = args.getString(TAG_USER_NAME);
        pictureImageUrl = args.getString(KEY_IMAGE_URL);
    }

    @Override
    public void onAttach(Activity act) {
        super.onAttach(act);
        Log.e(TAG, "Fragment-onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "Fragment-onCreate");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "Fragment-onStart");
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
    public void onStop() {
        super.onStop();
        Log.e(TAG, "Fragment-onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "Fragment-onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Fragment-onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "Fragment-onDetach");
    }
}

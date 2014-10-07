package com.example.kinagafuji.gocci.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kinagafuji.gocci.Base.BaseFragment;
import com.example.kinagafuji.gocci.R;

public class LifelogFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        View rootView = inflater.inflate(R.layout.fragment_lifelog, container, false);

        return rootView;
    }
}

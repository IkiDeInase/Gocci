package com.inase.android.gocci.Tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inase.android.gocci.R;

/**
 * Created by kinagafuji on 15/02/15.
 */
public class TutorialView4 extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // FragmentのViewを返却
        View view = inflater.inflate(R.layout.view_tutorial4, container, false);
        return view;
    }
}

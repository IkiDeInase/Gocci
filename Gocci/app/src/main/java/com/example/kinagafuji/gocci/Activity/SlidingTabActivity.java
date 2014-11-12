package com.example.kinagafuji.gocci.Activity;

import android.os.Bundle;

import com.example.kinagafuji.gocci.Base.BaseActivity;
import com.example.kinagafuji.gocci.Base.SlidingTabsBasicFragment;
import com.example.kinagafuji.gocci.R;

public class SlidingTabActivity extends BaseActivity {

    public SlidingTabsBasicFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab);

        if (savedInstanceState == null) {
            fragment = new SlidingTabsBasicFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.sample_content_fragment, fragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            fragment = (SlidingTabsBasicFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.sample_content_fragment);
        }
    }

}

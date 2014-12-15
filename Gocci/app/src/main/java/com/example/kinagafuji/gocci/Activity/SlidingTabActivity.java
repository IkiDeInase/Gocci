package com.example.kinagafuji.gocci.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.kinagafuji.gocci.Application_Gocci;
import com.example.kinagafuji.gocci.Base.BaseActivity;
import com.example.kinagafuji.gocci.Base.SlidingTabsBasicFragment;
import com.example.kinagafuji.gocci.R;
import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

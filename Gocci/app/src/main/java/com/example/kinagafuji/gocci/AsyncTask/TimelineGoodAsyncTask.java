package com.example.kinagafuji.gocci.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Fragment.TimelineFragment;
import com.example.kinagafuji.gocci.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;


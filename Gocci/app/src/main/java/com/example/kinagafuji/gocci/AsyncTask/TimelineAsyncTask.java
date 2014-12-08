package com.example.kinagafuji.gocci.AsyncTask;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Fragment.TimelineFragment;
import com.example.kinagafuji.gocci.data.UserData;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;



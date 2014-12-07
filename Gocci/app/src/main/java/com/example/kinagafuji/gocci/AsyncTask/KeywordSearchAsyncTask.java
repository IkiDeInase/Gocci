package com.example.kinagafuji.gocci.AsyncTask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Adapter.Search_keywordAdapter;
import com.example.kinagafuji.gocci.Fragment.Search_mapFragment;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class KeywordSearchAsyncTask extends AsyncTask<String, String, Integer> {

    private static final String TAG_TELL = "tell";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";

    Search_mapFragment fragment = new Search_mapFragment();

    @Override
    protected Integer doInBackground(String... params) {
        String param = params[0];

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet(param);
        HttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(request);
        } catch (Exception e) {
            Log.d("JSONSampleActivity", "Error Execute");
        }

        int status = httpResponse.getStatusLine().getStatusCode();

        if (HttpStatus.SC_OK == status) {
            String mKeywordData = null;
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                mKeywordData = outputStream.toString(); // JSONデータ
                Log.d("data", mKeywordData);

            } catch (Exception e) {
                Log.d("JSONSampleActivity", "Error");
            }

            try {

                JSONArray jsonArray = new JSONArray(mKeywordData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String tell = jsonObject.getString(TAG_TELL);
                    final String restname = jsonObject.getString(TAG_RESTNAME);
                    String category = jsonObject.getString(TAG_CATEGORY);
                    final Double lat = jsonObject.getDouble(TAG_LAT);
                    final Double lon = jsonObject.getDouble(TAG_LON);
                    String locality = jsonObject.getString(TAG_LOCALITY);
                    String distance = jsonObject.getString(TAG_DISTANCE);

                    UserData user = new UserData();

                    user.setTell(tell);
                    user.setRest_name(restname);
                    user.setCategory(category);
                    user.setLat(lat);
                    user.setLon(lon);
                    user.setLocality(locality);
                    user.setDistance(distance);

                    fragment.mKeywordusers.add(user);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            fragment.mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title(restname));

                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("えらー", String.valueOf(e));
            }

        } else {
            Log.d("JSONSampleActivity", "Status" + status);

        }

        return status;
    }

    // このメソッドは非同期処理の終わった後に呼び出されます
    @Override
    protected void onPostExecute(Integer result) {

        if (result != null && result == HttpStatus.SC_OK) {
            //ListViewの最読み込み
            fragment.mSearch_keywordAdapter = new Search_keywordAdapter(fragment.getActivity(), 0, fragment.mKeywordusers);
            fragment.mSearch_mapListView.setAdapter(fragment.mSearch_keywordAdapter);


        } else {
            //通信失敗した際のエラー処理
            Toast.makeText(fragment.getActivity(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
        }

        fragment.mKeywordDialog.dismiss();
    }
}


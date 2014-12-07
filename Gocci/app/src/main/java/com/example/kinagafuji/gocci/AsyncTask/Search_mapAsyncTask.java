package com.example.kinagafuji.gocci.AsyncTask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Base.AddMarkerEvent;
import com.example.kinagafuji.gocci.Base.BusHolder;
import com.example.kinagafuji.gocci.Fragment.Search_mapFragment;
import com.example.kinagafuji.gocci.data.UserData;
import com.google.android.gms.maps.model.LatLng;
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

public class Search_mapAsyncTask extends AsyncTask<String, String, Integer> {

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
            String mSearch_mapData = null;
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                mSearch_mapData = outputStream.toString(); // JSONデータ
                Log.d("data", mSearch_mapData);
            } catch (Exception e) {
                Log.d("JSONSampleActivity", "Error");
            }

            try {

                JSONArray searchmapArray = new JSONArray(mSearch_mapData);

                for (int i = 0; i < searchmapArray.length(); i++) {
                    JSONObject jsonObject = searchmapArray.getJSONObject(i);

                    String tell = jsonObject.getString(TAG_TELL);
                    final String rest_name = jsonObject.getString(TAG_RESTNAME);
                    String category = jsonObject.getString(TAG_CATEGORY);
                    final Double lat = jsonObject.getDouble(TAG_LAT);
                    final Double lon = jsonObject.getDouble(TAG_LON);
                    String locality = jsonObject.getString(TAG_LOCALITY);
                    String distance = jsonObject.getString(TAG_DISTANCE);

                    UserData user = new UserData();

                    user.setTell(tell);
                    user.setRest_name(rest_name);
                    user.setCategory(category);
                    user.setLat(lat);
                    user.setLon(lon);
                    user.setLocality(locality);
                    user.setDistance(distance);

                    fragment.mSearch_mapusers.add(user);

                    BusHolder.get().post(new AddMarkerEvent(lat,lon,rest_name));

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

    @Override
    protected void onPostExecute(Integer result) {

        if (result != null && result == HttpStatus.SC_OK) {
            //ListViewの最読み込み
            fragment.mSearch_mapAdapter.notifyDataSetChanged();
            fragment.mSearch_mapListView.invalidateViews();

        } else {
            //通信失敗した際のエラー処理
            Toast.makeText(fragment.getActivity().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
        }

        fragment.mSearchmapDialog.dismiss();

    }
}

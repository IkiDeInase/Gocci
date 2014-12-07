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

public class TimelineAsyncTask extends AsyncTask<String, String, Integer> {

    private static final String TAG_POST_ID = "post_id";
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_MOVIE = "movie";
    private static final String TAG_RESTNAME = "restname";
    private static final String TAG_GOODNUM = "goodnum";
    private static final String TAG_COMMENT_NUM = "comment_num";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_STAR_EVALUATION = "star_evaluation";
    private static final String TAG_LOCALITY = "locality";

    TimelineFragment fragment = new TimelineFragment();

    @Override
    protected Integer doInBackground(String... strings) {
        String string = strings[0];

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet(string);
        HttpResponse httpResponse = null;

        try {
            httpResponse = httpClient.execute(request);
        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
        }

        int status = httpResponse.getStatusLine().getStatusCode();

        if (HttpStatus.SC_OK == status) {
            String timelineData = null;
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                timelineData = outputStream.toString(); // JSONデータ
                Log.d("data", timelineData);
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }

            try {
                JSONArray jsonArray = new JSONArray(timelineData);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String post_id = jsonObject.getString(TAG_POST_ID);
                    Integer user_id = jsonObject.getInt(TAG_USER_ID);
                    String user_name = jsonObject.getString(TAG_USER_NAME);
                    String picture = jsonObject.getString(TAG_PICTURE);
                    String movie = jsonObject.getString(TAG_MOVIE);
                    String rest_name = jsonObject.getString(TAG_RESTNAME);
                    Integer goodnum = jsonObject.getInt(TAG_GOODNUM);
                    Integer comment_num = jsonObject.getInt(TAG_COMMENT_NUM);
                    String thumbnail = jsonObject.getString(TAG_THUMBNAIL);
                    Integer star_evaluation = jsonObject.getInt(TAG_STAR_EVALUATION);
                    String locality = jsonObject.getString(TAG_LOCALITY);

                    UserData user1 = new UserData();
                    user1.setUser_name(user_name);
                    user1.setPicture(picture);
                    fragment.mTimelineusers.add(user1);

                    UserData user2 = new UserData();
                    user2.setMovie(movie);
                    user2.setThumbnail(thumbnail);
                    fragment.mTimelineusers.add(user2);

                    UserData user3 = new UserData();
                    user3.setComment_num(comment_num);
                    user3.setgoodnum(goodnum);
                    user3.setStar_evaluation(star_evaluation);
                    fragment.mTimelineusers.add(user3);

                    UserData user4 = new UserData();
                    user4.setRest_name(rest_name);
                    user4.setLocality(locality);
                    fragment.mTimelineusers.add(user4);

                    UserData user5 = new UserData();
                    user5.setPost_id(post_id);
                    user5.setUser_id(user_id);
                    fragment.mTimelineusers.add(user5);


                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("error", String.valueOf(e));
            }
        } else {
            Log.d("JSONSampleActivity", "Status" + status);
        }

        return status;
    }

    @Override
    protected void onPostExecute(Integer result) {

        if (!fragment.isDetached() && fragment.isAdded()) {
            if (result != null && result == HttpStatus.SC_OK) {
                //ListViewの最読み込み
                fragment.mTimelineListView.invalidateViews();
                fragment.mTimelineAdapter.notifyDataSetChanged();

            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(fragment.getActivity(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            fragment.mTimelineDialog.dismiss();
        }
    }
}

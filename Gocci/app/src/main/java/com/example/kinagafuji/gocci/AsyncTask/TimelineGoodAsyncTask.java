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

public class TimelineGoodAsyncTask extends AsyncTask<String, String, Integer> {

    private static final String sGoodUrl = "http://api-gocci.jp/goodinsert/";
    private static final String sDataurl = "http://api-gocci.jp/login/";

    private int mStatus;
    private int mStatus2;

    TimelineFragment fragment = new TimelineFragment();

    @Override
    protected Integer doInBackground(String... params) {
        String param = params[0];

        HttpClient client = new DefaultHttpClient();

        HttpPost method = new HttpPost(sDataurl);

        ArrayList<NameValuePair> contents = new ArrayList<NameValuePair>();
        contents.add(new BasicNameValuePair("user_name", fragment.mName));
        contents.add(new BasicNameValuePair("picture", fragment.mPictureImageUrl));
        Log.d("読み取り", fragment.mName + "と" + fragment.mPictureImageUrl);

        String body = null;
        try {
            method.setEntity(new UrlEncodedFormEntity(contents, "utf-8"));
            HttpResponse res = client.execute(method);
            mStatus = res.getStatusLine().getStatusCode();
            Log.d("TAGだよ", "反応");
            HttpEntity entity = res.getEntity();
            body = EntityUtils.toString(entity, "UTF-8");
            Log.d("bodyの中身だよ", body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (HttpStatus.SC_OK == mStatus) {

            HttpPost goodnummethod = new HttpPost(sGoodUrl);

            ArrayList<NameValuePair> goodnumcontents = new ArrayList<NameValuePair>();
            goodnumcontents.add(new BasicNameValuePair("post_id", param));
            Log.d("読み取り", param);

            String goodnumbody = null;
            try {
                goodnummethod.setEntity(new UrlEncodedFormEntity(goodnumcontents, "utf-8"));
                HttpResponse goodnumres = client.execute(goodnummethod);
                mStatus2 = goodnumres.getStatusLine().getStatusCode();
                Log.d("TAGだよ", "反応");
                HttpEntity goodnumentity = goodnumres.getEntity();
                goodnumbody = EntityUtils.toString(goodnumentity, "UTF-8");
                Log.d("bodyの中身だよ", goodnumbody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mStatus2;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result != null && result == HttpStatus.SC_OK) {
            //いいねが送れた処理　項目itemの更新
            //View numberview = mTimelineListView.getChildAt(mTagPosition);
            //mTimelineListView.getAdapter().getView(mTagPosition,numberview,mTimelineListView);
            fragment.mTimelineAdapter.notifyDataSetChanged();
        } else {
            //失敗のため、いいね取り消し
            fragment.commentHolder.likesnumber.setText(fragment.currentgoodnum);
            fragment.likeCommentHolder.likes.setClickable(true);
            fragment.likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like);
            Toast.makeText(fragment.getActivity(), "いいね追加に失敗しました。", Toast.LENGTH_SHORT).show();
        }

    }

}

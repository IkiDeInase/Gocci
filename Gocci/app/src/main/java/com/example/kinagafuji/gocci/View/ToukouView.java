package com.example.kinagafuji.gocci.View;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinagafuji.gocci.Activity.IntentVineCamera;
import com.example.kinagafuji.gocci.Base.CustomProgressDialog;
import com.example.kinagafuji.gocci.R;
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
import java.util.ArrayList;

public class ToukouView extends LinearLayout {

    private CustomProgressDialog mSearchtenpoDialog;
    private ListView mTimeline_search_mapListView;
    private Search_tenpoAdapter mSearch_tenpoAdapter;
    private ArrayList<UserData> mSearch_tenpousers = new ArrayList<UserData>();

    private static final String TAG_TELL = "tell";
    private static final String TAG_RESTNAME1 = "restname";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_LOCALITY = "locality";
    private static final String TAG_DISTANCE = "distance";

    public double mLatitude;
    public double mLongitude;

    //public interface ToukouViewListener {
        /**
         * GPSの値をActivityに要求する
         */
    //    public void requestGpsParameter(double latitude, double Longitude);
    //}


    //　コードからの生成用
    public ToukouView(final Context context, final String name, final String pictureImageUrl) {
        super(context);

        //listener.requestGpsParameter(mLatitude, mLongitude);
        setGpsParameter(mLatitude, mLongitude);

        /*
        *検索画面からgetLatitude getLongitude
        *近くの３０件配列もやっておく
         */

        View inflateView = LayoutInflater.from(context).inflate(R.layout.searchlist, this);

        mTimeline_search_mapListView = (ListView) inflateView.findViewById(R.id.searchListView);
        mTimeline_search_mapListView.setDivider(null);
        // スクロールバーを表示しない
        mTimeline_search_mapListView.setVerticalScrollBarEnabled(false);

        // カード部分をselectorにするので、リストのselectorは透明にする
        mTimeline_search_mapListView.setSelector(android.R.color.transparent);

        mSearch_tenpoAdapter = new Search_tenpoAdapter(context, 0, mSearch_tenpousers);

        mTimeline_search_mapListView.setAdapter(mSearch_tenpoAdapter);

        mTimeline_search_mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                UserData country = mSearch_tenpousers.get(pos);

                Intent intent = new Intent(context.getApplicationContext(), IntentVineCamera.class);
                intent.putExtra("restname", country.getRest_name());
                intent.putExtra("name", name);
                intent.putExtra("pictureImageUrl", pictureImageUrl);
                context.startActivity(intent);
            }
        });

    }

    //xmlからの生成用
    public ToukouView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToukouView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public class Search_tenpoAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private SearchTenpoHolder searchTenpoHolder;

        public Search_tenpoAdapter(Context context, int viewResourceId, ArrayList<UserData> search_tenpousers) {
            super(context, viewResourceId, search_tenpousers);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.maplist, null);
                searchTenpoHolder = new SearchTenpoHolder(convertView);
                convertView.setTag(searchTenpoHolder);
            } else {
                searchTenpoHolder = (SearchTenpoHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            searchTenpoHolder.restname.setText(user.getRest_name());
            searchTenpoHolder.category.setText(user.getCategory());
            searchTenpoHolder.distance.setText(user.getDistance());

            return convertView;
        }
    }

    public class SearchTenpoAsyncTask extends AsyncTask<String, String, Integer> {

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
                String search_tenpoData = null;
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    httpResponse.getEntity().writeTo(outputStream);
                    search_tenpoData = outputStream.toString(); // JSONデータ
                    Log.d("data", search_tenpoData);
                } catch (Exception e) {
                    Log.d("JSONSampleActivity", "Error");
                }

                try {

                    JSONArray jsonArray = new JSONArray(search_tenpoData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tell = jsonObject.getString(TAG_TELL);
                        String rest_name = jsonObject.getString(TAG_RESTNAME1);
                        String category = jsonObject.getString(TAG_CATEGORY);
                        Double lat = jsonObject.getDouble(TAG_LAT);
                        Double lon = jsonObject.getDouble(TAG_LON);
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

                        mSearch_tenpousers.add(user);

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
                mSearch_tenpoAdapter.notifyDataSetChanged();
                mTimeline_search_mapListView.invalidateViews();
            } else {
                //通信失敗した際のエラー処理
                Toast.makeText(getContext().getApplicationContext(), "タイムラインの取得に失敗しました。", Toast.LENGTH_SHORT).show();
            }

            mSearchtenpoDialog.dismiss();
        }
    }

    public static class SearchTenpoHolder {
        ImageView search1;
        ImageView search2;
        ImageView search3;
        TextView restname;
        TextView category;
        TextView locality;
        TextView distance;

        public SearchTenpoHolder(View view) {
            this.search1 = (ImageView) view.findViewById(R.id.search1);
            this.search2 = (ImageView) view.findViewById(R.id.search2);
            this.search3 = (ImageView) view.findViewById(R.id.search3);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.category = (TextView) view.findViewById(R.id.category);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.distance = (TextView) view.findViewById(R.id.distance);
        }
    }

    public void setGpsParameter(double latitude, double longitude) {

        Log.d("経度・緯度", latitude + "/" + longitude);
        String mSearch_tenpoUrl = "http://api-gocci.jp/api/public/dist/?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&limit=30";

        new SearchTenpoAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mSearch_tenpoUrl);
        mSearchtenpoDialog = new CustomProgressDialog(getContext());
        mSearchtenpoDialog.setCancelable(false);
        mSearchtenpoDialog.show();

    }
}



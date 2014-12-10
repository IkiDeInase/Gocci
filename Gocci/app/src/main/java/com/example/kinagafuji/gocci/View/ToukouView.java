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

    private ListView mTimeline_search_mapListView;
    private Search_tenpoAdapter mSearch_tenpoAdapter;

    //　コードからの生成用
    public ToukouView(final Context context, final String name, final String pictureImageUrl, final ArrayList<UserData> users) {
        super(context);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.searchlist, this);

        mTimeline_search_mapListView = (ListView) inflateView.findViewById(R.id.searchListView);
        mTimeline_search_mapListView.setDivider(null);
        // スクロールバーを表示しない
        mTimeline_search_mapListView.setVerticalScrollBarEnabled(false);

        // カード部分をselectorにするので、リストのselectorは透明にする
        mTimeline_search_mapListView.setSelector(android.R.color.transparent);

        mSearch_tenpoAdapter = new Search_tenpoAdapter(context, 0, users);

        mTimeline_search_mapListView.setAdapter(mSearch_tenpoAdapter);

        mTimeline_search_mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                UserData country = users.get(pos);

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
}



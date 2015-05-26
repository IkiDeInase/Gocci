package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowerFolloweeCheerListActivity extends AppCompatActivity {

    private String mCategory;
    private String mUrl;

    private ArrayList<UserData> users = new ArrayList<UserData>();

    private ObservableListView listView;
    private SwipeRefreshLayout refresh;

    private FollowerFolloweeAdapter followerFolloweeAdapter;
    private CheerAdapter cheerAdapter;
    private WantAdapter wantAdapter;
    private TenpoCheerAdapter tenpoCheerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_followee_cheer_list);

        Slidr.attach(this);

        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");

        listView = (ObservableListView) findViewById(R.id.list);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        listView.setVerticalScrollBarEnabled(false);

        switch (mCategory) {
            case "follower":
            case "followee":
                followerFolloweeAdapter = new FollowerFolloweeAdapter(this, 0, users);
                break;
            case "cheer":
                cheerAdapter = new CheerAdapter(this, 0, users);
                break;
            case "want":
                wantAdapter = new WantAdapter(this, 0, users);
                break;
            case "tenpo_cheer":
                tenpoCheerAdapter = new TenpoCheerAdapter(this, 0, users);
                break;
        }

        if (mCategory.equals("tenpo_cheer")) {
            mUrl = "http://api-gocci.jp/favorites_list/?restname=" + SavedData.getServerName(this);
        } else {
            mUrl = "http://api-gocci.jp/favorites_list/?user_name=" + SavedData.getServerName(this) + "&get=" + mCategory;
        }
        Log.e("ログ", mUrl);
        getJSON(mUrl, mCategory);

        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);

                getRefreshJSON(mUrl, mCategory);
            }
        });
    }

    private void getJSON(String url, String category) {
        switch (category) {
            case "follower":
                getFollowerJSON(url);
                break;
            case "followee":
                getFolloweeJSON(url);
                break;
            case "cheer":
                getCheerJSON(url);
                break;
            case "want":
                getWantJSON(url);
                break;
            case "tenpo_cheer":
                getTenpo_CheerJSON(url);
                break;
        }
    }

    private void getRefreshJSON(String url, String category) {
        switch (category) {
            case "follower":
                getRefreshFollowerJSON(url);
                break;
            case "followee":
                getRefreshFolloweeJSON(url);
                break;
            case "cheer":
                getRefreshCheerJSON(url);
                break;
            case "want":
                getRefreshWantJSON(url);
            case "tenpo_cheer":
                getRefreshTenpo_CheerJSON(url);
                break;
        }
    }

    private void getFollowerJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String username = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background = jsonObject.getString("background_picture");

                        UserData user = new UserData();
                        user.setUser_name(username);
                        user.setPicture(picture);
                        user.setBackground(background);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listView.setAdapter(followerFolloweeAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

        });

    }

    private void getFolloweeJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String username = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background = jsonObject.getString("background_picture");
                        int status = jsonObject.getInt("status");

                        UserData user = new UserData();
                        user.setUser_name(username);
                        user.setPicture(picture);
                        user.setBackground(background);
                        user.setStatus(status);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listView.setAdapter(followerFolloweeAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

        });

    }

    private void getCheerJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");
                        Double lat = jsonObject.getDouble("lat");
                        Double lon = jsonObject.getDouble("lon");
                        String tell = jsonObject.getString("tell");
                        String category = jsonObject.getString("category");
                        String homepage = jsonObject.getString("homepage");
                        Integer want_flag = jsonObject.getInt("want_flag");
                        Integer total_cheer_num = jsonObject.getInt("total_cheer_num");

                        UserData user = new UserData();
                        user.setRest_name(restname);
                        user.setLocality(locality);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setTell(tell);
                        user.setCategory(category);
                        user.setHomepage(homepage);
                        user.setWant_flag(want_flag);
                        user.setTotal_cheer_num(total_cheer_num);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listView.setAdapter(cheerAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

        });

    }

    private void getWantJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");
                        Double lat = jsonObject.getDouble("lat");
                        Double lon = jsonObject.getDouble("lon");
                        String tell = jsonObject.getString("tell");
                        String category = jsonObject.getString("category");
                        String homepage = jsonObject.getString("homepage");
                        Integer want_flag = jsonObject.getInt("want_flag");
                        Integer total_cheer_num = jsonObject.getInt("total_cheer_num");

                        UserData user = new UserData();
                        user.setRest_name(restname);
                        user.setLocality(locality);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setTell(tell);
                        user.setCategory(category);
                        user.setHomepage(homepage);
                        user.setWant_flag(want_flag);
                        user.setTotal_cheer_num(total_cheer_num);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listView.setAdapter(wantAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

        });

    }

    private void getTenpo_CheerJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String username = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background = jsonObject.getString("background_picture");
                        int status = jsonObject.getInt("status");

                        UserData user = new UserData();
                        user.setUser_name(username);
                        user.setPicture(picture);
                        user.setBackground(background);
                        user.setStatus(status);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listView.setAdapter(tenpoCheerAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

        });

    }

    private void getRefreshFollowerJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String username = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background = jsonObject.getString("background_picture");

                        UserData user = new UserData();
                        user.setUser_name(username);
                        user.setPicture(picture);
                        user.setBackground(background);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                followerFolloweeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshFolloweeJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String username = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background = jsonObject.getString("background_picture");
                        int status = jsonObject.getInt("status");

                        UserData user = new UserData();
                        user.setUser_name(username);
                        user.setPicture(picture);
                        user.setBackground(background);
                        user.setStatus(status);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                followerFolloweeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }
        });

    }

    private void getRefreshCheerJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");
                        Double lat = jsonObject.getDouble("lat");
                        Double lon = jsonObject.getDouble("lon");
                        String tell = jsonObject.getString("tell");
                        String category = jsonObject.getString("category");
                        String homepage = jsonObject.getString("homepage");
                        Integer want_flag = jsonObject.getInt("want_flag");
                        Integer total_cheer_num = jsonObject.getInt("total_cheer_num");

                        UserData user = new UserData();
                        user.setRest_name(restname);
                        user.setLocality(locality);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setTell(tell);
                        user.setCategory(category);
                        user.setHomepage(homepage);
                        user.setWant_flag(want_flag);
                        user.setTotal_cheer_num(total_cheer_num);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                cheerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshWantJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                users.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String restname = jsonObject.getString("restname");
                        String locality = jsonObject.getString("locality");
                        Double lat = jsonObject.getDouble("lat");
                        Double lon = jsonObject.getDouble("lon");
                        String tell = jsonObject.getString("tell");
                        String category = jsonObject.getString("category");
                        String homepage = jsonObject.getString("homepage");
                        Integer want_flag = jsonObject.getInt("want_flag");
                        Integer total_cheer_num = jsonObject.getInt("total_cheer_num");

                        UserData user = new UserData();
                        user.setRest_name(restname);
                        user.setLocality(locality);
                        user.setLat(lat);
                        user.setLon(lon);
                        user.setTell(tell);
                        user.setCategory(category);
                        user.setHomepage(homepage);
                        user.setWant_flag(want_flag);
                        user.setTotal_cheer_num(total_cheer_num);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                wantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

            @Override
            public void onFinish() {
                refresh.setRefreshing(false);
            }

        });

    }

    private void getRefreshTenpo_CheerJSON(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String username = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background = jsonObject.getString("background_picture");
                        int status = jsonObject.getInt("status");

                        UserData user = new UserData();
                        user.setUser_name(username);
                        user.setPicture(picture);
                        user.setBackground(background);
                        user.setStatus(status);

                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listView.setAdapter(tenpoCheerAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ジェイソン失敗", String.valueOf(errorResponse));
            }

        });

    }

    public static class FollowerFolloweeViewHolder {
        ImageView userpicture;
        TextView username;
        ImageView addfollowButton;
        ImageView deletefollowButton;
        RippleView accountRipple;

        public FollowerFolloweeViewHolder(View view) {
            this.userpicture = (ImageView) view.findViewById(R.id.follower_followee_picture);
            this.username = (TextView) view.findViewById(R.id.username);
            this.addfollowButton = (ImageView) view.findViewById(R.id.addfollowButton);
            this.deletefollowButton = (ImageView) view.findViewById(R.id.deletefollowButton);
            this.accountRipple = (RippleView) view.findViewById(R.id.accountButton);
        }
    }

    public static class CheerViewHolder {
        ImageView restpicture;
        TextView restname;
        TextView locality;
        //ImageView deletecheerButton;
        //RippleView cheerRipple;

        public CheerViewHolder(View view) {
            this.restpicture = (ImageView) view.findViewById(R.id.cheer_picture);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.locality = (TextView) view.findViewById(R.id.locality);
            //this.deletecheerButton = (ImageView) view.findViewById(R.id.deleteCheerButton);
            //this.cheerRipple = (RippleView) view.findViewById(R.id.cheerButton);
        }
    }

    public static class WantViewHolder {
        ImageView restpicture;
        TextView restname;
        TextView locality;
        ImageView deletewantButton;
        ImageView addwantButton;
        RippleView wantRipple;

        public WantViewHolder(View view) {
            this.restpicture = (ImageView) view.findViewById(R.id.want_picture);
            this.restname = (TextView) view.findViewById(R.id.restname);
            this.locality = (TextView) view.findViewById(R.id.locality);
            this.deletewantButton = (ImageView) view.findViewById(R.id.deletewantButton);
            this.addwantButton = (ImageView) view.findViewById(R.id.addwantButton);
            this.wantRipple = (RippleView) view.findViewById(R.id.wantButton);
        }
    }

    public static class TenpoCheerViewHolder {
        ImageView userpicture;
        TextView username;
        ImageView addfollowButton;
        ImageView deletefollowButton;
        RippleView accountRipple;

        public TenpoCheerViewHolder(View view) {
            this.userpicture = (ImageView) view.findViewById(R.id.tenpo_cheer_picture);
            this.username = (TextView) view.findViewById(R.id.username);
            this.addfollowButton = (ImageView) view.findViewById(R.id.addfollowButton);
            this.deletefollowButton = (ImageView) view.findViewById(R.id.deletefollowButton);
            this.accountRipple = (RippleView) view.findViewById(R.id.accountButton);
        }
    }

    public class FollowerFolloweeAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public FollowerFolloweeAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FollowerFolloweeViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cell_follower_followee, null);
                viewHolder = new FollowerFolloweeViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (FollowerFolloweeViewHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            viewHolder.username.setText(user.getUser_name());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.userpicture);

            viewHolder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleUserProfActivity.class);
                    intent.putExtra("username", user.getUser_name());
                    intent.putExtra("picture", user.getPicture());
                    intent.putExtra("background", user.getBackground());
                    getContext().startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            viewHolder.userpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleUserProfActivity.class);
                    intent.putExtra("username", user.getUser_name());
                    intent.putExtra("picture", user.getPicture());
                    intent.putExtra("background", user.getBackground());
                    getContext().startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });


            switch (mCategory) {
                case "follower":
                    viewHolder.deletefollowButton.setVisibility(View.VISIBLE);
                    final FollowerFolloweeViewHolder finalViewHolder = viewHolder;
                    viewHolder.accountRipple.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalViewHolder.deletefollowButton.isShown()) {
                                finalViewHolder.deletefollowButton.setVisibility(View.INVISIBLE);
                                finalViewHolder.addfollowButton.setVisibility(View.VISIBLE);
                                postUnFollower(FollowerFolloweeCheerListActivity.this, user.getUser_name());
                            } else {
                                finalViewHolder.deletefollowButton.setVisibility(View.VISIBLE);
                                finalViewHolder.addfollowButton.setVisibility(View.INVISIBLE);
                                postFollower(FollowerFolloweeCheerListActivity.this, user.getUser_name());
                            }
                        }
                    });
                    break;
                case "followee":
                    if (user.getStatus() == 0) {
                        viewHolder.addfollowButton.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.deletefollowButton.setVisibility(View.VISIBLE);
                    }
                    final FollowerFolloweeViewHolder finalViewHolder1 = viewHolder;
                    viewHolder.accountRipple.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalViewHolder1.addfollowButton.isShown()) {
                                finalViewHolder1.addfollowButton.setVisibility(View.INVISIBLE);
                                finalViewHolder1.deletefollowButton.setVisibility(View.VISIBLE);
                                postFollower(FollowerFolloweeCheerListActivity.this, user.getUser_name());
                            } else {
                                finalViewHolder1.addfollowButton.setVisibility(View.VISIBLE);
                                finalViewHolder1.deletefollowButton.setVisibility(View.INVISIBLE);
                                postUnFollower(FollowerFolloweeCheerListActivity.this, user.getUser_name());
                            }
                        }
                    });
                    break;
            }


            return convertView;
        }
    }

    public class CheerAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public CheerAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheerViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cell_cheer, null);
                viewHolder = new CheerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CheerViewHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            viewHolder.restname.setText(user.getRest_name());
            viewHolder.locality.setText(user.getLocality());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);

            viewHolder.restname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleTenpoActivity.class);
                    intent.putExtra("restname", user.getRest_name());
                    intent.putExtra("locality", user.getLocality());
                    intent.putExtra("lat", user.getLat());
                    intent.putExtra("lon", user.getLon());
                    intent.putExtra("phone", user.getTell());
                    intent.putExtra("homepage", user.getHomepage());
                    intent.putExtra("category", user.getCategory());
                    intent.putExtra("want_flag", user.getWant_flag());
                    intent.putExtra("total_cheer_num", user.getTotal_cheer_num());
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            viewHolder.restpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleTenpoActivity.class);
                    intent.putExtra("restname", user.getRest_name());
                    intent.putExtra("locality", user.getLocality());
                    intent.putExtra("lat", user.getLat());
                    intent.putExtra("lon", user.getLon());
                    intent.putExtra("phone", user.getTell());
                    intent.putExtra("homepage", user.getHomepage());
                    intent.putExtra("category", user.getCategory());
                    intent.putExtra("want_flag", user.getWant_flag());
                    intent.putExtra("total_cheer_num", user.getTotal_cheer_num());
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            return convertView;
        }
    }

    public class WantAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public WantAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WantViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cell_want, null);
                viewHolder = new WantViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (WantViewHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            viewHolder.restname.setText(user.getRest_name());
            viewHolder.locality.setText(user.getLocality());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.restpicture);

            viewHolder.restname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleTenpoActivity.class);
                    intent.putExtra("restname", user.getRest_name());
                    intent.putExtra("locality", user.getLocality());
                    intent.putExtra("lat", user.getLat());
                    intent.putExtra("lon", user.getLon());
                    intent.putExtra("phone", user.getTell());
                    intent.putExtra("homepage", user.getHomepage());
                    intent.putExtra("category", user.getCategory());
                    intent.putExtra("want_flag", user.getWant_flag());
                    intent.putExtra("total_cheer_num", user.getTotal_cheer_num());
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            viewHolder.restpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleTenpoActivity.class);
                    intent.putExtra("restname", user.getRest_name());
                    intent.putExtra("locality", user.getLocality());
                    intent.putExtra("lat", user.getLat());
                    intent.putExtra("lon", user.getLon());
                    intent.putExtra("phone", user.getTell());
                    intent.putExtra("homepage", user.getHomepage());
                    intent.putExtra("category", user.getCategory());
                    intent.putExtra("want_flag", user.getWant_flag());
                    intent.putExtra("total_cheer_num", user.getTotal_cheer_num());
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            viewHolder.deletewantButton.setVisibility(View.VISIBLE);
            final WantViewHolder finalViewHolder = viewHolder;
            viewHolder.wantRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder.deletewantButton.isShown()) {
                        finalViewHolder.deletewantButton.setVisibility(View.INVISIBLE);
                        finalViewHolder.addwantButton.setVisibility(View.VISIBLE);
                        postUnWant(FollowerFolloweeCheerListActivity.this, user.getRest_name());
                    } else {
                        finalViewHolder.deletewantButton.setVisibility(View.VISIBLE);
                        finalViewHolder.addwantButton.setVisibility(View.INVISIBLE);
                        postWant(FollowerFolloweeCheerListActivity.this, user.getRest_name());
                    }
                }
            });

            return convertView;
        }
    }

    public class TenpoCheerAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;

        public TenpoCheerAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TenpoCheerViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cell_tenpo_cheer, null);
                viewHolder = new TenpoCheerViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (TenpoCheerViewHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            viewHolder.username.setText(user.getUser_name());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(viewHolder.userpicture);

            viewHolder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleUserProfActivity.class);
                    intent.putExtra("username", user.getUser_name());
                    intent.putExtra("picture", user.getPicture());
                    intent.putExtra("background", user.getBackground());
                    getContext().startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            viewHolder.userpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FollowerFolloweeCheerListActivity.this, FlexibleUserProfActivity.class);
                    intent.putExtra("username", user.getUser_name());
                    intent.putExtra("picture", user.getPicture());
                    intent.putExtra("background", user.getBackground());
                    getContext().startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            });

            if (user.getStatus() == 0) {
                viewHolder.addfollowButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.deletefollowButton.setVisibility(View.VISIBLE);
            }
            final TenpoCheerViewHolder finalViewHolder1 = viewHolder;
            viewHolder.accountRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalViewHolder1.addfollowButton.isShown()) {
                        finalViewHolder1.addfollowButton.setVisibility(View.INVISIBLE);
                        finalViewHolder1.deletefollowButton.setVisibility(View.VISIBLE);
                        postFollower(FollowerFolloweeCheerListActivity.this, user.getUser_name());
                    } else {
                        finalViewHolder1.addfollowButton.setVisibility(View.VISIBLE);
                        finalViewHolder1.deletefollowButton.setVisibility(View.INVISIBLE);
                        postUnFollower(FollowerFolloweeCheerListActivity.this, user.getUser_name());
                    }
                }
            });

            return convertView;
        }
    }

    private void postFollower(final Context context, String username) {
        final AsyncHttpClient client = new AsyncHttpClient();
        Log.e("送る名前", username);
        client.setCookieStore(SavedData.getCookieStore(context));
        final RequestParams favoriteParam = new RequestParams("user_name", username);
        client.post(context, Const.URL_FAVORITE_API, favoriteParam, new JsonHttpResponseHandler() {
            //JSONはどんな形だ？
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    String message = response.getString("message");

                    if (message.equals("ユーザーをお気に入りしました")) {
                        //gocci.addFollower();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        SavedData.addFollower(context);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "処理に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postUnFollower(final Context context, String username) {
        final AsyncHttpClient client = new AsyncHttpClient();
        Log.e("送る名前", username);
        final RequestParams unFavoriteParam = new RequestParams("user_name", username);
        client.setCookieStore(SavedData.getCookieStore(context));
        client.post(context, Const.URL_UNFAVORITE_API, unFavoriteParam, new JsonHttpResponseHandler() {
            //JSONはどんな形だ？
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ジェイソン成功", String.valueOf(response));
                try {
                    String message = response.getString("message");

                    if (message.equals("フォロー解除しました")) {
                        //gocci.downFollower();
                        SavedData.downFollower(context);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(context, "処理に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postWant(final Context context, String restname) {
        RequestParams param = new RequestParams("restname", restname);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(SavedData.getCookieStore(FollowerFolloweeCheerListActivity.this));
        client.post(FollowerFolloweeCheerListActivity.this, Const.URL_RESTRAUNT_FOLLOW, param, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "処理に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");

                    if (!message.equals("行きたいリストに登録しました")) {
                        Toast.makeText(FollowerFolloweeCheerListActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void postUnWant(final Context context, String restname) {
        RequestParams param = new RequestParams("restname", restname);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(SavedData.getCookieStore(FollowerFolloweeCheerListActivity.this));
        client.post(FollowerFolloweeCheerListActivity.this, Const.URL_RESTRAUNT_UNFOLLOW, param, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Toast.makeText(context, "処理に失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String message = response.getString("message");

                    if (!message.equals("行きたいリストから解除しました")) {
                        Toast.makeText(FollowerFolloweeCheerListActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}

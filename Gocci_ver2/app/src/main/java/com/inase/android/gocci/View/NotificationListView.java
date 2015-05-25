package com.inase.android.gocci.View;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/05/12.
 */
public class NotificationListView extends RelativeLayout {

    private ListView mNotificationList;
    private ProgressWheel mNotificationProgress;
    private NotificationAdapter mNotificationAdapter;
    private ArrayList<UserData> mNotificationUsers = new ArrayList<UserData>();

    private String clickedUsername;
    private String clickedUserPicture;
    private String clickedUserbackground;

    private String clickedRestname;
    private String clickedLocality;
    private String clickedPhoneNumber;
    private String clickedHomepage;
    private String clickedCategory;
    private double clickedLat;
    private double clickedLon;

    public NotificationListView(final Context context) {
        super(context);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_notification_list, this);

        mNotificationProgress = (ProgressWheel) inflateView.findViewById(R.id.progress_wheel);
        mNotificationList = (ListView) inflateView.findViewById(R.id.notification_list);
        mNotificationAdapter = new NotificationAdapter(context, 0, mNotificationUsers);

        getNotificationJson(context);
    }

    //xmlからの生成用
    public NotificationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void getNotificationJson(final Context context) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.get(context, Const.URL_NOTICE_LIST, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mNotificationUsers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        int notice_id = jsonObject.getInt("notice_id");
                        String user_name = jsonObject.getString("user_name");
                        String picture = jsonObject.getString("picture");
                        String background_picture = jsonObject.getString("background_picture");
                        String notice = jsonObject.getString("notice");
                        String date_time = jsonObject.getString("noticed");
                        int notice_num = jsonObject.getInt("notice_num");

                        UserData user = new UserData();
                        user.setUser_name(user_name);
                        user.setPicture(picture);
                        user.setBackground(background_picture);
                        user.setNotice(notice);
                        user.setDatetime(date_time);

                        mNotificationUsers.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mNotificationList.setAdapter(mNotificationAdapter);

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getContext(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mNotificationProgress.setVisibility(GONE);
            }
        });
    }

    public static class NotificationHolder {
        ImageView picture;
        TextView notice;
        TextView date_time;

        public NotificationHolder(View view) {
            this.picture = (ImageView) view.findViewById(R.id.circleImage);
            this.notice = (TextView) view.findViewById(R.id.notice);
            this.date_time = (TextView) view.findViewById(R.id.date_time);
        }
    }

    public class NotificationAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private NotificationHolder mNotificationHolder;

        public NotificationAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cell_notification_list, null);
                mNotificationHolder = new NotificationHolder(convertView);
                convertView.setTag(mNotificationHolder);
            } else {
                mNotificationHolder = (NotificationHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            mNotificationHolder.notice.setText(user.getNotice());
            mNotificationHolder.date_time.setText(user.getDatetime());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(mNotificationHolder.picture);

            //コメントのViewをクリックすると、その人のプロフィールに飛ぶ
            mNotificationHolder.notice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedUsername = user.getUser_name();
                    clickedUserPicture = user.getPicture();
                    clickedUserbackground = user.getBackground();

                    //リップルエフェクトを見せるために、すこし遅らせる。
                    Handler handler = new Handler();
                    handler.postDelayed(new commentClickHandler(), 750);
                }
            });

            return convertView;
        }
    }

    class commentClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getContext(), FlexibleUserProfActivity.class);
            intent.putExtra("username", clickedUsername);
            intent.putExtra("picture", clickedUserPicture);
            intent.putExtra("background", clickedUserbackground);
            getContext().startActivity(intent);
        }
    }
}

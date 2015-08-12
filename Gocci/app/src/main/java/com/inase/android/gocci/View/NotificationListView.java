package com.inase.android.gocci.View;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inase.android.gocci.Activity.CommentActivity;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
import com.inase.android.gocci.data.HeaderData;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kinagafuji on 15/05/12.
 */
public class NotificationListView extends RelativeLayout {

    private ListView mNotificationList;
    private ProgressWheel mNotificationProgress;
    private NotificationAdapter mNotificationAdapter;
    private ArrayList<HeaderData> mNotificationUsers = new ArrayList<>();

    public NotificationListView(final Context context) {
        super(context);

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_notification_list, this);

        mNotificationProgress = (ProgressWheel) inflateView.findViewById(R.id.progress);
        mNotificationList = (ListView) inflateView.findViewById(R.id.notification_list);

        mNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HeaderData user = mNotificationUsers.get(position);

                switch (user.getNotice()) {
                    case "like":
                        CommentActivity.startCommentActivityOnContext(user.getNotice_post_id(), getContext());
                        break;
                    case "follow":
                        Intent intent = new Intent(getContext(), FlexibleUserProfActivity.class);
                        intent.putExtra("user_id", user.getNotice_user_id());
                        intent.putExtra("user_name", user.getUsername());
                        getContext().startActivity(intent);
                        break;
                    case "comment":
                        CommentActivity.startCommentActivityOnContext(user.getNotice_post_id(), getContext());

                        break;
                }
            }
        });

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
        Const.asyncHttpClient.setCookieStore(SavedData.getCookieStore(context));
        Const.asyncHttpClient.get(context, Const.getNoticeAPI(), new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                mNotificationProgress.setVisibility(VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mNotificationUsers.clear();

                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);
                        mNotificationUsers.add(HeaderData.createNoticeHeaderData(jsonObject));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mNotificationAdapter = new NotificationAdapter(context, 0, mNotificationUsers);
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
        TextView notice_username;
        TextView notice_sub;
        TextView date_time;

        public NotificationHolder(View view) {
            this.picture = (ImageView) view.findViewById(R.id.circleImage);
            this.notice_username = (TextView) view.findViewById(R.id.notice_username);
            this.notice_sub = (TextView) view.findViewById(R.id.notice_subText);
            this.date_time = (TextView) view.findViewById(R.id.date_time);
        }
    }

    public class NotificationAdapter extends ArrayAdapter<HeaderData> {
        private LayoutInflater layoutInflater;
        private NotificationHolder mNotificationHolder;

        public NotificationAdapter(Context context, int viewResourceId, ArrayList<HeaderData> users) {
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

            final HeaderData user = this.getItem(position);

            mNotificationHolder.notice_username.setText(user.getUsername());
            mNotificationHolder.date_time.setText(user.getNotice_date());

            Picasso.with(getContext())
                    .load(user.getProfile_img())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(mNotificationHolder.picture);

            //コメントのViewをクリックすると、その人のプロフィールに飛ぶ
            mNotificationHolder.notice_username.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), FlexibleUserProfActivity.class);
                    intent.putExtra("user_id", user.getNotice_user_id());
                    intent.putExtra("user_name", user.getUsername());
                    getContext().startActivity(intent);
                }
            });

            mNotificationHolder.picture.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), FlexibleUserProfActivity.class);
                    intent.putExtra("user_id", user.getNotice_user_id());
                    intent.putExtra("user_name", user.getUsername());
                    getContext().startActivity(intent);
                }
            });


            switch (user.getNotice()) {
                case "like":
                    mNotificationHolder.notice_sub.setText("さんからいいねされました");

                    break;
                case "follow":
                    mNotificationHolder.notice_sub.setText("さんからフォローされました");

                    break;
                case "comment":
                    mNotificationHolder.notice_sub.setText("さんからコメントされました");

                    break;
            }

            return convertView;
        }
    }
}

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
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.data.UserData;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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
public class NotificationListView extends LinearLayout {

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

    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE_URL = "picture";
    private static final String TAG_BACKGROUND_PICTURE = "background_picture";

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private RequestParams mLoginParam;

    public NotificationListView(final Context context, RequestParams loginParam) {
        super(context);

        mLoginParam = loginParam;

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_notification_list, this);

        mNotificationProgress = (ProgressWheel) inflateView.findViewById(R.id.progress_wheel);
        mNotificationList = (ListView) inflateView.findViewById(R.id.notification_list);
    }

    //xmlからの生成用
    public NotificationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void getSignupAsync(final Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.post(context, Const.URL_SIGNUP_API, mLoginParam, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("サインアップ成功", "status=" + statusCode);
                getNotificationJson(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mNotificationProgress.setVisibility(GONE);
                Toast.makeText(getContext(), "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNotificationJson(final Context context) {
        httpClient.get(context, Const.URL_SIGNUP_API, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mNotificationUsers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

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

    public static class CommentHolder {
        RippleView commentRipple;
        TextView comment;
        TextView user_name;
        ImageView pisture_url;

        public CommentHolder(View view) {
            this.commentRipple = (RippleView) view.findViewById(R.id.commentRipple);
            this.comment = (TextView) view.findViewById(R.id.usercomment);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.pisture_url = (ImageView) view.findViewById(R.id.commentUserImage);
        }
    }

    public class NotificationAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private CommentHolder mCommentHolder;

        public NotificationAdapter(Context context, int viewResourceId, ArrayList<UserData> users) {
            super(context, viewResourceId, users);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.cell_comment, null);
                mCommentHolder = new CommentHolder(convertView);
                convertView.setTag(mCommentHolder);
            } else {
                mCommentHolder = (CommentHolder) convertView.getTag();
            }

            final UserData user = this.getItem(position);

            mCommentHolder.comment.setText(user.getComment());
            mCommentHolder.user_name.setText(user.getUser_name());

            Picasso.with(getContext())
                    .load(user.getPicture())
                    .placeholder(R.drawable.ic_userpicture)
                    .transform(new RoundedTransformation())
                    .into(mCommentHolder.pisture_url);

            //コメントのViewをクリックすると、その人のプロフィールに飛ぶ
            mCommentHolder.commentRipple.setOnClickListener(new OnClickListener() {
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

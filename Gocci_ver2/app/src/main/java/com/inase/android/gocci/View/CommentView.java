package com.inase.android.gocci.View;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.inase.android.gocci.Activity.FlexibleUserProfActivity;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Base.RoundedTransformation;
import com.inase.android.gocci.R;
import com.inase.android.gocci.common.Const;
import com.inase.android.gocci.common.SavedData;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CommentView extends LinearLayout {

    private ListView mCommentList;
    private EditText mCommenttext;
    private ImageButton mCommentButton;
    private ImageView mEmptyView;
    private ProgressWheel mCommentProgress;
    private CommentAdapter mCommentAdapter;
    private ArrayList<UserData> mCommentusers = new ArrayList<UserData>();

    private String mEncodePost_id;
    private String mCommentJsonUrl;
    private String mCommentString;
    private String mPost_id;

    private String clickedUsername;
    private String clickedUserPicture;
    private String clickedUserbackground;

    private static final String TAG_COMMENT = "comment";
    private static final String TAG_USER_NAME = "user_name";
    private static final String TAG_PICTURE_URL = "picture";
    private static final String TAG_BACKGROUND_PICTURE = "background_picture";

    private AsyncHttpClient httpClient;
    private AsyncHttpClient httpClient2;
    private RequestParams commentPostParam;

    public CommentView(final Context context, String post_id) {
        super(context);

        mPost_id = post_id;

        try {
            mEncodePost_id = URLEncoder.encode(mPost_id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mCommentJsonUrl = "http://api-gocci.jp/comment_json/?post_id=" + mEncodePost_id;

        View inflateView = LayoutInflater.from(context).inflate(R.layout.view_comment, this);

        mCommentProgress = (ProgressWheel) inflateView.findViewById(R.id.commentprogress_wheel);
        mCommentList = (ListView) inflateView.findViewById(R.id.commentList);
        mCommenttext = (EditText) inflateView.findViewById(R.id.commentText);
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mCommentButton = (ImageButton) inflateView.findViewById(R.id.commentButton);
        mEmptyView = (ImageView) inflateView.findViewById(R.id.comment_empty);

        // スクロールバーを表示しない
        mCommentList.setVerticalScrollBarEnabled(false);

        mCommentAdapter = new CommentAdapter(context, 0, mCommentusers);

        getSignupAsync(context);//サインアップとJSON受け取り

        mCommenttext.setOnKeyListener(new View.OnKeyListener() {
            //キーボードの決定ボタンを押した時、キーボードをたたむ
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    inputMethodManager.hideSoftInputFromWindow(mCommenttext.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    mCommentString = String.valueOf(mCommenttext.getText());
                    return true;
                }
                return false;
            }
        });

        mCommentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentString != null) {
                    commentPostParam = new RequestParams();
                    commentPostParam.put("post_id", mPost_id);
                    commentPostParam.put("comment", mCommentString);

                    postSignupAsync(context);

                    mCommenttext.setText(null);
                } else {
                    Toast.makeText(context, "コメントを入力してください", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    //xmlからの生成用
    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void getSignupAsync(final Context context) {
        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(SavedData.getCookieStore(context));
        httpClient.get(context, mCommentJsonUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mCommentusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String comment = jsonObject.getString(TAG_COMMENT);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture_url = jsonObject.getString(TAG_PICTURE_URL);
                        String background = jsonObject.getString(TAG_BACKGROUND_PICTURE);

                        UserData user = new UserData();

                        user.setComment(comment);
                        user.setUser_name(user_name);
                        user.setPicture(picture_url);
                        user.setBackground(background);

                        mCommentusers.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mCommentList.setAdapter(mCommentAdapter);

                if (mCommentusers.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getContext(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mCommentProgress.setVisibility(GONE);
            }
        });
    }

    private void postSignupAsync(final Context context) {
        httpClient2 = new AsyncHttpClient();
        httpClient2.setCookieStore(SavedData.getCookieStore(context));
        httpClient2.post(context, Const.URL_POST_COMMENT_API, commentPostParam, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mCommentProgress.setVisibility(VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                postCommentJson(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mCommentProgress.setVisibility(GONE);
                Toast.makeText(getContext(), "コメント送信に失敗しました", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postCommentJson(final Context context) {
        httpClient2.get(context, mCommentJsonUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                mCommentusers.clear();
                try {
                    for (int i = 0; i < timeline.length(); i++) {
                        JSONObject jsonObject = timeline.getJSONObject(i);

                        String comment = jsonObject.getString(TAG_COMMENT);
                        String user_name = jsonObject.getString(TAG_USER_NAME);
                        String picture_url = jsonObject.getString(TAG_PICTURE_URL);
                        String background = jsonObject.getString(TAG_BACKGROUND_PICTURE);

                        UserData user = new UserData();

                        user.setComment(comment);
                        user.setUser_name(user_name);
                        user.setPicture(picture_url);
                        user.setBackground(background);

                        mCommentusers.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mCommentAdapter.notifyDataSetChanged();

                if (mCommentusers.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                Toast.makeText(getContext(), "読み取りに失敗しました", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                mCommentProgress.setVisibility(GONE);
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

    public class CommentAdapter extends ArrayAdapter<UserData> {
        private LayoutInflater layoutInflater;
        private CommentHolder mCommentHolder;

        public CommentAdapter(Context context, int viewResourceId, ArrayList<UserData> mCommentusers) {
            super(context, viewResourceId, mCommentusers);
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

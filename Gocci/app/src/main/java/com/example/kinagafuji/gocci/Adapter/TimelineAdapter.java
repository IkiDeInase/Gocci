package com.example.kinagafuji.gocci.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.AsyncTask.TimelineGoodAsyncTask;
import com.example.kinagafuji.gocci.Fragment.TimelineFragment;
import com.example.kinagafuji.gocci.R;
import com.example.kinagafuji.gocci.View.CommentView;
import com.example.kinagafuji.gocci.data.LayoutHolder;
import com.example.kinagafuji.gocci.data.RoundedTransformation;
import com.example.kinagafuji.gocci.data.ToukouPopup;
import com.example.kinagafuji.gocci.data.UserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TimelineAdapter extends ArrayAdapter<UserData> {
    private LayoutInflater layoutInflater;
    public int mTagPosition;
    private int mShowPosition;
    private String mNextGoodnum;

    TimelineFragment fragment = new TimelineFragment();

    public TimelineAdapter(Context context, int viewResourceId, ArrayList<UserData> timelineusers) {
        super(context, viewResourceId, timelineusers);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int line = (position / 5) * 5;
        int pos = position - line;
        mTagPosition = position;

        final UserData user = getItem(position);

        switch (pos) {

            case 0:
                convertView = layoutInflater.inflate(R.layout.name_picture_bar, null);
                break;

            case 1:
                convertView = layoutInflater.inflate(R.layout.video_bar, null);
                break;

            case 2:
                convertView = layoutInflater.inflate(R.layout.comment_bar, null);
                break;

            case 3:
                convertView = layoutInflater.inflate(R.layout.restaurant_bar, null);
                break;

            case 4:
                convertView = layoutInflater.inflate(R.layout.likes_comments_bar, null);
                break;

        }

        switch (pos) {

            case 0:
                LayoutHolder.NameHolder nameHolder = new LayoutHolder.NameHolder(convertView);

                nameHolder.user_name.setText(user.getUser_name());

                Picasso.with(getContext())
                        .load(user.getPicture())
                        .resize(50, 50)
                        .placeholder(R.drawable.ic_userpicture)
                        .centerCrop()
                        .transform(new RoundedTransformation())
                        .into(nameHolder.circleImage);
                break;

            case 1:
                final LayoutHolder.VideoHolder videoHolder = new LayoutHolder.VideoHolder(convertView);

                Picasso.with(getContext())
                        .load(user.getThumbnail())
                        .placeholder(R.color.videobackground)
                        .into(videoHolder.mVideoThumbnail);
                videoHolder.mVideoThumbnail.setVisibility(View.VISIBLE);

                if (!fragment.mBusy) {

                    videoHolder.movie.setVideoURI(Uri.parse(user.getMovie()));
                    Log.e("読み込みました", user.getMovie());
                    videoHolder.movie.requestFocus();
                    videoHolder.movie.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            VideoView nextVideo = (VideoView) fragment.mTimelineListView.findViewWithTag(mShowPosition);

                            if (nextVideo != null) {
                                nextVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.stop();
                                    }
                                });
                                Log.e("TAG", "pause : " + mShowPosition);
                            }

                            videoHolder.mVideoThumbnail.setVisibility(View.GONE);
                            videoHolder.movie.start();

                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.start();
                                    mp.setLooping(true);
                                }
                            });

                            Log.e("TAG", "start : " + position);
                            mShowPosition = position;
                        }
                    });

                    videoHolder.movie.setTag(position);
                }
                break;

            case 2:
                fragment.commentHolder = new LayoutHolder.CommentHolder(convertView);
                fragment.commentHolder.likesnumber.setText(String.valueOf(user.getgoodnum()));
                fragment.commentHolder.commentsnumber.setText(String.valueOf(user.getComment_num()));

                mNextGoodnum = String.valueOf(user.getgoodnum() + 1);
                fragment.currentgoodnum = String.valueOf((user.getgoodnum()));

                break;

            case 3:
                LayoutHolder.RestHolder restHolder = new LayoutHolder.RestHolder(convertView);

                restHolder.rest_name.setText(user.getRest_name());
                restHolder.locality.setText(user.getLocality());
                break;

            case 4:
                fragment.likeCommentHolder = new LayoutHolder.LikeCommentHolder(convertView);
                //クリックされた時の処理
                fragment.likeCommentHolder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("いいねをクリック", user.getPost_id() + mNextGoodnum);

                        fragment.likeCommentHolder.likes.setClickable(false);
                        fragment.commentHolder.likesnumber.setText(mNextGoodnum);
                        //画像差し込み
                        fragment.likeCommentHolder.likes.setBackgroundResource(R.drawable.ic_like_orange);

                        new TimelineGoodAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user.getPost_id());
                    }
                });

                fragment.likeCommentHolder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("コメントをクリック", "コメント！" + user.getPost_id());

                        //引数に入れたい値を入れていく
                        View commentView = new CommentView(fragment.getActivity(), fragment.mName, fragment.mPictureImageUrl, user.getPost_id());

                        final PopupWindow window = ToukouPopup.newBasicPopupWindow(fragment.getActivity());
                        window.setContentView(commentView);
                        //int totalHeight = getWindowManager().getDefaultDisplay().getHeight();
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        ToukouPopup.showLikeQuickAction(window, commentView, v, fragment.getActivity().getWindowManager(), 0, 0);
                    }
                });
                break;

        }

        return convertView;

    }
}

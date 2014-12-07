package com.example.kinagafuji.gocci.data;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.kinagafuji.gocci.R;

public class LayoutHolder {

    public static class NameHolder {
        public ImageView circleImage;
        public TextView user_name;
        public TextView time;

        public NameHolder(View view) {
            this.circleImage = (ImageView) view.findViewById(R.id.circleImage);
            this.user_name = (TextView) view.findViewById(R.id.user_name);
            this.time = (TextView) view.findViewById(R.id.time);
        }
    }

    public static class VideoHolder {
        public VideoView movie;
        public ImageView mVideoThumbnail;

        public VideoHolder(View view) {
            this.movie = (VideoView) view.findViewById(R.id.videoView);
            this.mVideoThumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
        }
    }

    public static class CommentHolder {
        public TextView likesnumber;
        public TextView likes;
        public TextView commentsnumber;
        public TextView comments;
        public TextView sharenumber;
        public TextView share;

        public CommentHolder(View view) {
            this.likesnumber = (TextView) view.findViewById(R.id.likesnumber);
            this.likes = (TextView) view.findViewById(R.id.likes);
            this.commentsnumber = (TextView) view.findViewById(R.id.commentsnumber);
            this.comments = (TextView) view.findViewById(R.id.comments);
            this.sharenumber = (TextView) view.findViewById(R.id.sharenumber);
            this.share = (TextView) view.findViewById(R.id.share);
        }
    }

    public static class RestHolder {
        public ImageView restaurantImage;
        public TextView locality;
        public TextView rest_name;

        public RestHolder(View view) {
            this.restaurantImage = (ImageView) view.findViewById(R.id.restaurantImage);
            this.rest_name = (TextView) view.findViewById(R.id.rest_name);
            this.locality = (TextView) view.findViewById(R.id.locality);
        }
    }

    public static class LikeCommentHolder {
        public ImageView likes;
        public ImageView comments;
        public ImageView share;

        public LikeCommentHolder(View view) {
            this.likes = (ImageView) view.findViewById(R.id.likes);
            this.comments = (ImageView) view.findViewById(R.id.comments);
            this.share = (ImageView) view.findViewById(R.id.share);

        }
    }
}

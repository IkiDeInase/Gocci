package com.inase.android.gocci.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.amazonaws.regions.Regions;
import com.andexert.library.RippleView;
import com.google.android.exoplayer.VideoSurfaceView;
import com.inase.android.gocci.Base.SquareImageView;
import com.inase.android.gocci.Base.SquareVideoView;
import com.inase.android.gocci.R;
import com.inase.android.gocci.VideoPlayer.SquareExoVideoView;
import com.loopj.android.http.AsyncHttpClient;

/**
 * 定数定義クラス
 * Created by kmaeda on 2015/01/22.
 */
public class Const {
    public static final int VERSION_NUMBER = 1;
    public static final String URL_PREFIX = "http://test.api.gocci.me/v";

    //AWS SN　ログイン　エンドポイント
    public static final String ENDPOINT_FACEBOOK = "graph.facebook.com";
    public static final String ENDPOINT_TWITTER = "api.twitter.com";
    public static final String ENDPOINT_INASE = "login.inase.gocci";

    public static final String IDENTITY_POOL_ID = "us-east-1:a8cc1fdb-92b1-4586-ba97-9e6994a43195";
    public static final Regions REGION = Regions.US_EAST_1;

    public static final String POST_MOVIE_BUCKET_NAME = "gocci.movies.bucket.jp-test";
    public static final String POST_PHOTO_BUCKET_NAME = "gocci.imgs.provider.jp-test";

    public static String getAuthWelcomeAPI(String identity_id, int flag) {
        return URL_PREFIX + VERSION_NUMBER + "/auth/welcome/?identity_id=" + identity_id +
                "&sns_flag=" + flag;
    }
    public static String getAuthLoginAPI(String identity_id) {
        return URL_PREFIX + VERSION_NUMBER + "/auth/login/?identity_id=" + identity_id;
    }
    public static String getAuthSignupAPI(String username, String os, String model, String register_id) {
        return URL_PREFIX + VERSION_NUMBER + "/auth/signup/?username=" + username + "&os=android_" + os +
                "&model=" + model + "&register_id=" + register_id;
    }
    public static String getAuthConversionAPI(String username, String profile_img, String os, String model, String register_id) {
        return  URL_PREFIX + VERSION_NUMBER + "/auth/conversion/?username=" + username +
                "&profile_img=" + profile_img + "&os=android_" + os +
                "&model=" + model + "&register_id=" + register_id;
    }
    public static String getTimelineAPI() {
        return URL_PREFIX + VERSION_NUMBER + "/get/timeline";
    }
    public static String getTimelineNextApi(int call) {
        return URL_PREFIX + VERSION_NUMBER + "/get/timeline_next/?call=" + call;
    }
    public static String getPopularAPI() {
        return URL_PREFIX + VERSION_NUMBER + "/get/popular";
    }
    public static String getPopularNextApi(int call) {
        return URL_PREFIX + VERSION_NUMBER + "/get/popular_next/?call=" + call;
    }
    public static String getCommentAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/comment/?post_id=" + post_id;
    }
    public static String getRestpageAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/rest/?rest_id=" + rest_id;
    }
    public static String getUserpageAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/user/?target_user_id=" + user_id;
    }
    public static String getNoticeAPI() {
        return URL_PREFIX + VERSION_NUMBER + "/get/notice";
    }
    public static String getNearAPI(double lat, double lon) {
        return URL_PREFIX + VERSION_NUMBER + "/get/near/?lon=" + lon + "&lat=" + lat;
    }
    public static String getFollowAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/follow/?target_user_id=" + user_id;
    }
    public static String getFollowerAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/follower/?target_user_id=" + user_id;
    }
    public static String getWantAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/want/?target_user_id=" + user_id;
    }
    public static String getUserCheerAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/user_cheer/?target_user_id=" + user_id;
    }
    public static String getRestCheerAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/get/rest_cheer/?rest_id=" + rest_id;
    }
    public static String getPostGochiAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/gochi/?post_id=" + post_id;
    }
    public static String getPostDeleteAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/postdel/?post_id=" + post_id;
    }
    public static String getPostViolateAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/postblock/?post_id=" + post_id;
    }
    public static String getPostFollowAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/follow/?target_user_id=" + user_id;
    }
    public static String getPostUnFollowAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/unfollow/?target_user_id=" + user_id;
    }
    public static String getPostWantAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/want/?rest_id=" + rest_id;
    }
    public static String getPostUnWantAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/post/unwant/?rest_id=" + rest_id;
    }
    public static String getPostFeedbackAPI(String feedback) {
        return URL_PREFIX + VERSION_NUMBER + "/post/feedback/?feedback=" + feedback;
    }
    public static String getPostCommentAPI(String post_id, String comment) {
        return URL_PREFIX + VERSION_NUMBER + "/post/comment/?post_id=" + post_id + "&comment=" + comment;
    }
    public static String getPostMovieAPI(int rest_id, String movie, int category_id, int tag_id, String value, String memo, int cheer_flag) {
        return URL_PREFIX + VERSION_NUMBER + "/post/post/?rest_id=" + rest_id +
                "&movie_name=" + movie + "&category_id=" + category_id + "&tag_id=" + tag_id +
                "&value=" + value + "&memo=" + memo + "&cheer_flag=" + cheer_flag;
    }
    public static String getPostRestAddAPI(String restname, double lat, double lon) {
        return URL_PREFIX + VERSION_NUMBER + "/post/restadd/?rest_name=" + restname +
                "&lat=" + lat + "&lon=" + lon;
    }

    public static final int FLAG_CHANGE_NAME = 0;
    public static final int FLAG_CHANGE_PICTURE = 1;
    public static final int FLAG_CHANGE_BOTH = 2;
    public static String getPostUpdateProfileAPI(int flag, String username, String profile_img) {
        String returnUrl = null;
        switch (flag) {
            case FLAG_CHANGE_NAME:
                returnUrl = URL_PREFIX + VERSION_NUMBER + "/post/update_profile/?username=" + username;
                break;
            case FLAG_CHANGE_PICTURE:
                returnUrl = URL_PREFIX + VERSION_NUMBER + "/post/update_profile/?profile_img=" + profile_img;
                break;
            case FLAG_CHANGE_BOTH:
                returnUrl = URL_PREFIX + VERSION_NUMBER + "/post/update_profile/?username=" + username +
                        "&profile_img=" + profile_img;
                break;
        }
        return returnUrl;
    }
    public static final String URL_POST_PROFILE_EDIT_API = URL_PREFIX + VERSION_NUMBER + "post/";

    // 動画ファイルのキャッシュファイルの接頭辞
    public static final String MOVIE_CACHE_PREFIX = "movie_cache_";

    // HTTP Status Not Modified
    public static final int HTTP_STATUS_NOT_MODIFIED = 304;

    // 動画取得リトライ上限回数
    public static final int GET_MOVIE_MAX_RETRY_COUNT = 5;

    public static final int INTENT_TO_TIMELINE = 0;
    public static final int INTENT_TO_MYPAGE = 1;
    public static final int INTENT_TO_USERPAGE = 2;
    public static final int INTENT_TO_RESTPAGE = 3;
    public static final int INTENT_TO_COMMENT = 4;
    public static final int INTENT_TO_CAMERA = 5;
    public static final int INTENT_TO_LICENSE = 6;
    public static final int INTENT_TO_POLICY = 7;
    public static final int INTENT_TO_ADVICE = 8;
    public static final int INTENT_TO_LIST = 9;

    public static final int CATEGORY_FOLLOW = 1;
    public static final int CATEGORY_FOLLOWER = 2;
    public static final int CATEGORY_USER_CHEER = 3;
    public static final int CATEGORY_WANT = 4;
    public static final int CATEGORY_REST_CHEER = 5;

    public static final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static final class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView circleImage;
        public TextView user_name;
        public TextView datetime;
        public TextView comment;
        public RippleView menuRipple;
        public SquareVideoView movie;
        public RoundCornerProgressBar movieProgress;
        public ImageView mVideoThumbnail;
        //public ImageView restaurantImage;
        //public TextView locality;
        public TextView rest_name;
        public TextView category;
        public TextView value;
        public TextView atmosphere;
        public RippleView tenpoRipple;
        public TextView likes;
        public ImageView likes_Image;
        public TextView comments;
        public RippleView likes_ripple;
        public RippleView comments_ripple;
        public RippleView share_ripple;
        public FrameLayout videoFrame;

        public ViewHolder(View view) {
            super(view);
            circleImage = (ImageView) view.findViewById(R.id.circleImage);
            user_name = (TextView) view.findViewById(R.id.user_name);
            datetime = (TextView) view.findViewById(R.id.time_text);
            comment = (TextView) view.findViewById(R.id.comment);
            menuRipple = (RippleView) view.findViewById(R.id.menuRipple);
            movie = (SquareVideoView) view.findViewById(R.id.videoView);
            movieProgress = (RoundCornerProgressBar) view.findViewById(R.id.video_progress);
            mVideoThumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
            //viewHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
            rest_name = (TextView) view.findViewById(R.id.rest_name);
            //viewHolder.locality = (TextView) convertView.findViewById(R.id.locality);
            category = (TextView) view.findViewById(R.id.category);
            value = (TextView) view.findViewById(R.id.value);
            atmosphere = (TextView) view.findViewById(R.id.mood);
            tenpoRipple = (RippleView) view.findViewById(R.id.tenpoRipple);
            likes = (TextView) view.findViewById(R.id.likes_Number);
            likes_Image = (ImageView) view.findViewById(R.id.likes_Image);
            comments = (TextView) view.findViewById(R.id.comments_Number);
            likes_ripple = (RippleView) view.findViewById(R.id.likes_ripple);
            comments_ripple = (RippleView) view.findViewById(R.id.comments_ripple);
            share_ripple = (RippleView) view.findViewById(R.id.share_ripple);
            videoFrame = (FrameLayout) view.findViewById(R.id.videoFrame);
        }
    }

    public static final class ExoViewHolder extends RecyclerView.ViewHolder {

        public ImageView circleImage;
        public TextView user_name;
        public TextView datetime;
        public TextView comment;
        public RippleView menuRipple;
        public ImageView mVideoThumbnail;
        public SquareExoVideoView movie;
        //public ImageView restaurantImage;
        //public TextView locality;
        public TextView rest_name;
        public TextView category;
        public TextView value;
        public TextView atmosphere;
        public RippleView tenpoRipple;
        public TextView likes;
        public ImageView likes_Image;
        public TextView comments;
        public RippleView likes_ripple;
        public RippleView comments_ripple;
        public RippleView share_ripple;
        public FrameLayout videoFrame;

        public ExoViewHolder(View view) {
            super(view);
            circleImage = (ImageView) view.findViewById(R.id.circleImage);
            user_name = (TextView) view.findViewById(R.id.user_name);
            datetime = (TextView) view.findViewById(R.id.time_text);
            comment = (TextView) view.findViewById(R.id.comment);
            menuRipple = (RippleView) view.findViewById(R.id.menuRipple);
            movie = (SquareExoVideoView) view.findViewById(R.id.square_video_exo);
            mVideoThumbnail = (ImageView) view.findViewById(R.id.video_thumbnail);
            //viewHolder.restaurantImage = (ImageView) convertView.findViewById(R.id.restaurantImage);
            rest_name = (TextView) view.findViewById(R.id.rest_name);
            //viewHolder.locality = (TextView) convertView.findViewById(R.id.locality);
            category = (TextView) view.findViewById(R.id.category);
            value = (TextView) view.findViewById(R.id.value);
            atmosphere = (TextView) view.findViewById(R.id.mood);
            tenpoRipple = (RippleView) view.findViewById(R.id.tenpoRipple);
            likes = (TextView) view.findViewById(R.id.likes_Number);
            likes_Image = (ImageView) view.findViewById(R.id.likes_Image);
            comments = (TextView) view.findViewById(R.id.comments_Number);
            likes_ripple = (RippleView) view.findViewById(R.id.likes_ripple);
            comments_ripple = (RippleView) view.findViewById(R.id.comments_ripple);
            share_ripple = (RippleView) view.findViewById(R.id.share_ripple);
            videoFrame = (FrameLayout) view.findViewById(R.id.videoFrame);
        }
    }
}

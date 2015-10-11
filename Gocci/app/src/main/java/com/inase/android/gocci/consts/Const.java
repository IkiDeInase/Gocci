package com.inase.android.gocci.consts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.regions.Regions;
import com.andexert.library.RippleView;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.inase.android.gocci.R;
import com.inase.android.gocci.ui.view.SquareExoVideoView;
import com.inase.android.gocci.ui.view.SquareImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 定数定義クラス
 * Created by kmaeda on 2015/01/22.
 */
public class Const {
    public static final int VERSION_NUMBER = 1;
    public static final String URL_PREFIX = "http://test.api.gocci.me/v";
//    public static final String URL_PREFIX = "https://api.gocci.me/v";

    //AWS SN　ログイン　エンドポイント
    public static final String ENDPOINT_FACEBOOK = "graph.facebook.com";
    public static final String ENDPOINT_TWITTER = "api.twitter.com";
    public static final String ENDPOINT_INASE = "test.login.gocci";
//    public static final String ENDPOINT_INASE = "login.gocci";

    public static final String IDENTITY_POOL_ID = "us-east-1:b563cebf-1de2-4931-9f08-da7b4725ae35";
//    public static final String IDENTITY_POOL_ID = "us-east-1:b0252276-27e1-4069-be84-3383d4b3f897";

    public static final String ANALYTICS_ID = "9cc17c8116ae40738002f10d907bbd16";

    public static final Regions REGION = Regions.US_EAST_1;

    public static final String POST_MOVIE_BUCKET_NAME = "gocci.movies.bucket.jp-test";
    public static final String GET_MOVIE_BUCKET_NAME = "gocci.movies.provider.jp-test";
    public static final String POST_PHOTO_BUCKET_NAME = "gocci.imgs.provider.jp-test";
//    public static final String POST_MOVIE_BUCKET_NAME = "gocci.movies.bucket.jp";
//    public static final String GET_MOVIE_BUCKET_NAME = "gocci.movies.provider.jp";
//    public static final String POST_PHOTO_BUCKET_NAME = "gocci.imgs.provider.jp";

    public static String getAuthWelcomeAPI(String identity_id, int flag) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/welcome/?identity_id=" + identity_id +
                "&sns_flag=" + flag;
    }

    public static String getAuthLoginAPI(String identity_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/login/?identity_id=" + identity_id;
    }

    public static String getAuthSNSLoginAPI(String identity_id, String os, String model, String register_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/sns_login/?identity_id=" + identity_id + "&os=android_" + os +
                "&model=" + model + "&register_id=" + register_id;
    }

    public static String getAuthSignupAPI(String username, String os, String model, String register_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/signup/?username=" + username + "&os=android_" + os +
                "&model=" + model + "&register_id=" + register_id;
    }

    public static String getAuthConversionAPI(String username, String profile_img, String os, String model, String register_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/conversion/?username=" + username +
                "&profile_img=" + profile_img + "&os=android_" + os +
                "&model=" + model + "&register_id=" + register_id;
    }

    public static String getAuthSNSMatchAPI(String providerName, String token, String profile_img) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/sns/?provider=" + providerName +
                "&token=" + token + "&profile_img=" + profile_img;
    }

    public static String getAuthSNSUnLinkAPI(String providerName, String token) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/sns_unlink/?provider=" + providerName +
                "&token=" + token;
    }

    public static String getAuthSNSConversionAPI(String providerName, String token, String profile_img, String os, String model, String register_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/sns_conversion/?profile_img=" + profile_img +
                "&token=" + token + "&provider=" + providerName + "&os=android_" + os +
                "&model=" + model + "&register_id=" + register_id;
    }

    public static String getAuthUsernamePasswordAPI(String username, String password, String os, String model, String register_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/auth/pass_login/?username=" + username +
                "&pass=" + password + "&os=android_" + os + "&model=" + model + "&register_id=" + register_id;
    }

    public static String getCustomTimelineAPI(int position, int sort_id, int category_id, int value_id, double lon, double lat, int call) {
        StringBuilder url = null;
        switch (position) {
            case 0: //近い店
                url = new StringBuilder(URL_PREFIX + VERSION_NUMBER + "/mobile/get/timeline/?order_id=1&lon=" + lon + "&lat=" + lat);
                if (category_id != 0) url.append("&category_id=").append(category_id);
                if (value_id != 0) url.append("&value_id=").append(value_id);
                if (call != 0) url.append("&call=").append(call);
                break;
            case 1: //フォロー
                url = new StringBuilder(URL_PREFIX + VERSION_NUMBER + "/mobile/get/followline/?call=" + call);
                if (sort_id != 0) url.append("&order_id=").append(sort_id);
                if (sort_id == 1) url.append("&lon=").append(lon).append("&lat=").append(lat);
                if (category_id != 0) url.append("&category_id=").append(category_id);
                if (value_id != 0) url.append("&value_id=").append(value_id);
                break;
            case 2: //新着
                url = new StringBuilder(URL_PREFIX + VERSION_NUMBER + "/mobile/get/timeline/?call=" + call);
                if (sort_id != 0) url.append("&order_id=").append(sort_id);
                if (sort_id == 1) url.append("&lon=").append(lon).append("&lat=").append(lat);
                if (category_id != 0) url.append("&category_id=").append(category_id);
                if (value_id != 0) url.append("&value_id=").append(value_id);
                break;
        }
        return new String(url);
    }

    public static String getLatestAPI() {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/timeline";
    }

    public static String getLatestNextApi(int call) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/timeline/?call=" + call;
    }

    public static String getFollowlineApi() {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/followline";
    }

    public static String getFollowlineNextApi(int call) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/followline/?call=" + call;
    }

    public static String getCommentAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/comment/?post_id=" + post_id;
    }

    public static String getRestpageAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/rest/?rest_id=" + rest_id;
    }

    public static String getUserpageAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/user/?target_user_id=" + user_id;
    }

    public static String getNoticeAPI() {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/notice";
    }

    public static String getNearAPI(double lat, double lon) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/near/?lon=" + lon + "&lat=" + lat;
    }

    public static String getFollowAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/follow/?target_user_id=" + user_id;
    }

    public static String getFollowerAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/follower/?target_user_id=" + user_id;
    }

    public static String getWantAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/want/?target_user_id=" + user_id;
    }

    public static String getUserCheerAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/user_cheer/?target_user_id=" + user_id;
    }

    public static String getRestCheerAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/rest_cheer/?rest_id=" + rest_id;
    }

    public static String getPostGochiAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/gochi/?post_id=" + post_id;
    }

    public static String getPostDeleteAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/postdel/?post_id=" + post_id;
    }

    public static String getPostViolateAPI(String post_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/postblock/?post_id=" + post_id;
    }

    public static String getPostFollowAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/follow/?target_user_id=" + user_id;
    }

    public static String getPostUnFollowAPI(int user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/unfollow/?target_user_id=" + user_id;
    }

    public static String getPostWantAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/want/?rest_id=" + rest_id;
    }

    public static String getPostUnWantAPI(int rest_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/unwant/?rest_id=" + rest_id;
    }

    public static String getPostFeedbackAPI(String feedback) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/feedback/?feedback=" + feedback;
    }

    public static String getPostCommentAPI(String post_id, String comment) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/comment/?post_id=" + post_id + "&comment=" + comment;
    }

    public static String getPostCommentWithNoticeAPI(String post_id, String comment, String re_user_id) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/comment/?post_id=" + post_id + "&comment=" + comment + "&re_user_id=" + re_user_id;
    }

    public static String getPostPasswordAPI(String password) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/password/?pass=" + password;
    }

    public static String getPostMovieAPI(int rest_id, String movie, int category_id, int tag_id, String value, String memo, int cheer_flag) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/post/?rest_id=" + rest_id +
                "&movie_name=" + movie + "&category_id=" + category_id + "&tag_id=" + tag_id +
                "&value=" + value + "&memo=" + memo + "&cheer_flag=" + cheer_flag;
    }

    public static String getPostRestAddAPI(String restname, double lat, double lon) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/post/restadd/?rest_name=" + restname +
                "&lat=" + lat + "&lon=" + lon;
    }

    public static String getPostRefreshRegId(String token, String user_id, String os) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/background/update_register_id/?user_id=" + user_id +
                "&os=android_" + os + "&register_id=" + token;
    }

    public static String getPostSearchUser(String username) {
        return URL_PREFIX + VERSION_NUMBER + "/mobile/get/user_search/?username=" + username;
    }

    public static final int FLAG_CHANGE_NAME = 0;
    public static final int FLAG_CHANGE_PICTURE = 1;
    public static final int FLAG_CHANGE_BOTH = 2;

    public static String getPostUpdateProfileAPI(int flag, String username, String profile_img) {
        String returnUrl = null;
        switch (flag) {
            case FLAG_CHANGE_NAME:
                returnUrl = URL_PREFIX + VERSION_NUMBER + "/mobile/post/update_profile/?username=" + username;
                break;
            case FLAG_CHANGE_PICTURE:
                returnUrl = URL_PREFIX + VERSION_NUMBER + "/mobile/post/update_profile/?profile_img=" + profile_img;
                break;
            case FLAG_CHANGE_BOTH:
                returnUrl = URL_PREFIX + VERSION_NUMBER + "/mobile/post/update_profile/?username=" + username +
                        "&profile_img=" + profile_img;
                break;
        }
        return returnUrl;
    }

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
    public static final int INTENT_TO_SETTING = 10;

    public static final int INTENT_TO_GRIDSEARCH = 12;

    public static final int CATEGORY_FOLLOW = 1;
    public static final int CATEGORY_FOLLOWER = 2;
    public static final int CATEGORY_USER_CHEER = 3;
    public static final int CATEGORY_WANT = 4;
    public static final int CATEGORY_REST_CHEER = 5;

    public static final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public static final SyncHttpClient syncHttpClient = new SyncHttpClient();

    public static final class ExoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.circle_image)
        public ImageView mCircleImage;
        @Bind(R.id.user_name)
        public TextView mUserName;
        @Bind(R.id.time_text)
        public TextView mTimeText;
        @Bind(R.id.comment)
        public TextView mComment;
        @Bind(R.id.menu_ripple)
        public RippleView mMenuRipple;
        @Bind(R.id.video_thumbnail)
        public SquareImageView mVideoThumbnail;
        @Bind(R.id.square_video_exo)
        public SquareExoVideoView mSquareVideoExo;
        @Bind(R.id.rest_name)
        public TextView mRestname;
        @Bind(R.id.category)
        public TextView mCategory;
        @Bind(R.id.value)
        public TextView mValue;
        @Bind(R.id.mood)
        public TextView mMood;
        @Bind(R.id.tenpo_ripple)
        public RippleView mTenpoRipple;
        @Bind(R.id.likes_number)
        public TextView mLikesNumber;
        @Bind(R.id.likes_Image)
        public ImageView mLikesImage;
        @Bind(R.id.comments_number)
        public TextView mCommentsNumber;
        @Bind(R.id.likes_ripple)
        public RippleView mLikesRipple;
        @Bind(R.id.comments_ripple)
        public RippleView mCommentsRipple;
        @Bind(R.id.share_ripple)
        public RippleView mShareRipple;
        @Bind(R.id.video_frame)
        public AspectRatioFrameLayout mVideoFrame;

        public ExoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static final class TwoCellViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.video_thumbnail)
        public SquareImageView mSquareImage;
        @Bind(R.id.square_video_exo)
        public SquareExoVideoView mSquareExoVideo;
        @Bind(R.id.video_frame)
        public AspectRatioFrameLayout mAspectFrame;
        @Bind(R.id.restname)
        public TextView mRestname;
        @Bind(R.id.distance)
        public TextView mDistance;

        public TwoCellViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
